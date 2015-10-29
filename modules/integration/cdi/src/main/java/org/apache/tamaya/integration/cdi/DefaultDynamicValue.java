/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tamaya.integration.cdi;

import org.apache.commons.lang3.Conversion;
import org.apache.tamaya.ConfigException;
import org.apache.tamaya.Configuration;
import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.inject.api.BaseDynamicValue;
import org.apache.tamaya.inject.api.DynamicValue;
import org.apache.tamaya.inject.api.InjectionUtils;
import org.apache.tamaya.inject.api.LoadPolicy;
import org.apache.tamaya.inject.api.UpdatePolicy;
import org.apache.tamaya.inject.api.WithPropertyConverter;
import org.apache.tamaya.spi.ConversionContext;
import org.apache.tamaya.spi.PropertyConverter;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * A accessor for a single configured value. This can be used to support values that may change during runtime,
 * reconfigured or final. Hereby external code (could be Tamaya configuration listners or client code), can set a
 * new value. Depending on the {@link UpdatePolicy} the new value is immedeately active or it requires an active commit
 * by client code. Similarly an instance also can ignore all later changes to the value.
 * <h3>Implementation Details</h3>
 * This class is
 * <ul>
 * <li>Serializable, when also the item stored is serializable</li>
 * <li>Thread safe</li>
 * </ul>
 *
 * @param <T> The type of the value.
 */
final class DefaultDynamicValue<T> extends BaseDynamicValue<T> {

    private static final long serialVersionUID = -2071172847144537443L;

    /**
     * The property name of the entry.
     */
    private String propertyName;
    /**
     * The keys to be resolved.
     */
    private String[] keys;
    /**
     * Back reference to the base configuration instance. This reference is used reevalaute the given property and
     * compare the result with the previous value after a configuration change was triggered.
     */
    private Configuration configuration;
    /**
     * The target type of the property used to lookup a matching {@link PropertyConverter}.
     * If null, {@code propertyConverter} is set and used instead.
     */
    private TypeLiteral<T> targetType;
    /**
     * The property converter to be applied, may be null. In the ladder case targetType is not null.
     */
    private PropertyConverter<T> propertyConverter;
    /**
     * Policy that defines how new values are applied, be default it is applied initially once, but never updated
     * anymore.
     */
    private UpdatePolicy updatePolicy;
    /**
     * Load policy.
     */
    private final LoadPolicy loadPolicy;

    /**
     * The current value, never null.
     */
    private transient T value;
    /**
     * The new value, or null.
     */
    private transient Object[] newValue;
    /**
     * List of listeners that listen for changes.
     */
    private transient WeakList<PropertyChangeListener> listeners;

    /**
     * Constructor.
     *
     * @param propertyName      the name of the fields' property/method.
     * @param keys              the keys of the property, not null.
     * @param configuration     the configuration, not null.
     * @param targetType        the target type, not null.
     * @param propertyConverter the optional converter to be used.
     */
    private DefaultDynamicValue(String propertyName, Configuration configuration, TypeLiteral<T> targetType,
                                PropertyConverter<T> propertyConverter, List<String> keys, LoadPolicy loadPolicy,
                                UpdatePolicy updatePolicy) {
        this.propertyName = Objects.requireNonNull(propertyName);
        this.keys = keys.toArray(new String[keys.size()]);
        this.configuration = Objects.requireNonNull(configuration);
        this.propertyConverter = propertyConverter;
        this.targetType = targetType;
        this.loadPolicy = Objects.requireNonNull(loadPolicy);
        this.updatePolicy = Objects.requireNonNull(updatePolicy);
        if(loadPolicy == LoadPolicy.INITIAL){
            this.value = evaluateValue();
        }
    }

    public static DynamicValue of(Field annotatedField, Configuration configuration) {
        return of(annotatedField, configuration, LoadPolicy.ALWAYS, UpdatePolicy.IMMEDEATE);
    }

    public static DynamicValue of(Field annotatedField, Configuration configuration, LoadPolicy loadPolicy) {
        return of(annotatedField, configuration, loadPolicy, UpdatePolicy.IMMEDEATE);
    }

    public static DynamicValue of(Field annotatedField, Configuration configuration, UpdatePolicy updatePolicy) {
        return of(annotatedField, configuration, LoadPolicy.ALWAYS, updatePolicy);
    }

    public static DynamicValue of(Field annotatedField, Configuration configuration, LoadPolicy loadPolicy, UpdatePolicy updatePolicy) {
        // Check for adapter/filter
        Type targetType = annotatedField.getGenericType();
        if (targetType == null) {
            throw new ConfigException("Failed to evaluate target type for " + annotatedField.getAnnotatedType().getType().getTypeName()
                    + '.' + annotatedField.getName());
        }
        if (targetType instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) targetType;
            Type[] types = pt.getActualTypeArguments();
            if (types.length != 1) {
                throw new ConfigException("Failed to evaluate target type for " + annotatedField.getAnnotatedType().getType().getTypeName()
                        + '.' + annotatedField.getName());
            }
            targetType = (Type) types[0];
        }
        PropertyConverter<?> propertyConverter = null;
        WithPropertyConverter annot = annotatedField.getAnnotation(WithPropertyConverter.class);
        if (annot != null) {
            try {
                propertyConverter = annot.value().newInstance();
            } catch (Exception e) {
                throw new ConfigException("Failed to instantiate annotated PropertyConverter on " +
                        annotatedField.getAnnotatedType().getType().getTypeName()
                        + '.' + annotatedField.getName(), e);
            }
        }
        List<String> keys = InjectionUtils.getKeys(annotatedField);
        return new DefaultDynamicValue(annotatedField.getName(), configuration,
                TypeLiteral.of(targetType), propertyConverter, keys, loadPolicy, updatePolicy);
    }

    public static DynamicValue of(Method method, Configuration configuration) {
        return of(method, configuration, LoadPolicy.ALWAYS, UpdatePolicy.IMMEDEATE);
    }

    public static DynamicValue of(Method method, Configuration configuration, UpdatePolicy updatePolicy) {
        return of(method, configuration, LoadPolicy.ALWAYS, updatePolicy);
    }

    public static DynamicValue of(Method method, Configuration configuration, LoadPolicy loadPolicy) {
        return of(method, configuration, loadPolicy, UpdatePolicy.IMMEDEATE);
    }

    public static DynamicValue of(Method method, Configuration configuration, LoadPolicy loadPolicy, UpdatePolicy updatePolicy) {
        // Check for adapter/filter
        Type targetType = method.getGenericReturnType();
        if (targetType == null) {
            throw new ConfigException("Failed to evaluate target type for " + method.getDeclaringClass()
                    .getTypeName() + '.' + method.getName());
        }
        if (targetType instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) targetType;
            Type[] types = pt.getActualTypeArguments();
            if (types.length != 1) {
                throw new ConfigException("Failed to evaluate target type for " + method.getDeclaringClass()
                        .getTypeName() + '.' + method.getName());
            }
            targetType = (Class) types[0];
        }
        PropertyConverter<Object> propertyConverter = null;
        WithPropertyConverter annot = method.getAnnotation(WithPropertyConverter.class);
        if (annot != null) {
            try {
                propertyConverter = (PropertyConverter<Object>) annot.value().newInstance();
            } catch (Exception e) {
                throw new ConfigException("Failed to instantiate annotated PropertyConverter on " +
                        method.getDeclaringClass().getTypeName()
                        + '.' + method.getName(), e);
            }
        }
        return new DefaultDynamicValue<Object>(method.getName(),
                configuration, TypeLiteral.of(targetType), propertyConverter, InjectionUtils.getKeys(method),
                loadPolicy, updatePolicy);
    }


    /**
     * Commits a new value that has not been committed yet, make it the new value of the instance. On change any
     * registered listeners will be triggered.
     */
    public void commit() {
        T oldValue = value;
        value = newValue==null?null:(T)newValue[0];
        newValue = null;
        informListeners(oldValue, value);
    }

    private void informListeners(T value, T newValue) {
        synchronized (this) {
            PropertyChangeEvent evt = new PropertyChangeEvent(this, propertyName, value,
                    newValue);
            if (listeners != null) {
                for (PropertyChangeListener consumer : listeners.get()) {
                    consumer.propertyChange(evt);
                }
            }
        }
    }

    /**
     * Discards a new value that was published. No listeners will be informed.
     */
    public void discard() {
        newValue = null;
    }


    /**
     * Access the {@link UpdatePolicy} used for updating this value.
     *
     * @return the update policy, never null.
     */
    public UpdatePolicy getUpdatePolicy() {
        return updatePolicy;
    }

    /**
     * Sets a new {@link UpdatePolicy}.
     *
     * @param updatePolicy the new policy, not null.
     */
    public void setUpdatePolicy(UpdatePolicy updatePolicy) {
        this.updatePolicy = Objects.requireNonNull(updatePolicy);
    }

    /**
     * Add a listener to be called as weak reference, when this value has been changed.
     *
     * @param l the listener, not null
     */
    public void addListener(PropertyChangeListener l) {
        if (listeners == null) {
            listeners = new WeakList<>();
        }
        listeners.add(l);
    }

    /**
     * Removes a listener to be called, when this value has been changed.
     *
     * @param l the listner to be removed, not null
     */
    public void removeListener(PropertyChangeListener l) {
        if (listeners != null) {
            listeners.remove(l);
        }
    }

    /**
     * If a value is present in this {@code DynamicValue}, returns the value,
     * otherwise throws {@code ConfigException}.
     *
     * @return the non-null value held by this {@code Optional}
     * @throws ConfigException if there is no value present
     * @see DefaultDynamicValue#isPresent()
     */
    public T get() {
        T newLocalValue = null;
        if(loadPolicy!=LoadPolicy.INITIAL) {
            newLocalValue = evaluateValue();
            if (this.value == null) {
                this.value = newLocalValue;
            }
            if(!Objects.equals(this.value, newLocalValue)){
                switch (updatePolicy){
                    case IMMEDEATE:
                        commit();
                        break;
                    case EXPLCIT:
                        this.newValue = new Object[]{newLocalValue};
                        break;
                    case LOG_ONLY:
                        informListeners(this.value, newLocalValue);
                        this.newValue = null;
                        break;
                    case NEVER:
                        this.newValue = null;
                        break;
                    default:
                        this.newValue = null;
                        break;
                }
            }
        }
        return value;
    }

    /**
     * Method to check for and apply a new value. Depending on the {@link  UpdatePolicy}
     * the value is immediately or deferred visible (or it may even be ignored completely).
     *
     * @return true, if a new value has been detected. The value may not be visible depending on the current
     * {@link UpdatePolicy} in place.
     */
    public boolean updateValue() {
        if(this.value==null && this.newValue==null){
            this.value = evaluateValue();
            return false;
        }
        T newValue = evaluateValue();
        if (Objects.equals(newValue, this.value)) {
            return false;
        }
        switch (this.updatePolicy) {
            case LOG_ONLY:
                Logger.getLogger(getClass().getName()).info("Discard change on " + this + ", newValue=" + newValue);
                informListeners(value, newValue);
                this.newValue = null;
                break;
            case NEVER:
                this.newValue = null;
                break;
            case EXPLCIT:
            case IMMEDEATE:
            default:
                this.newValue = new Object[]{newValue};
                commit();
                break;
        }
        return true;
    }

    /**
     * Evaluates the current value dynamically from the underlying configuration.
     *
     * @return the current actual value, or null.
     */
    public T evaluateValue() {
        T value = null;

        for (String key : keys) {
            if (propertyConverter == null) {
                value = configuration.get(key, targetType);
            } else {
                String source = configuration.get(key);
                ConversionContext ctx = new ConversionContext.Builder(configuration, key, targetType).build();
                value = propertyConverter.convert(source, ctx);
            }

            if (value != null) {
                break;
            }
        }

        return value;
    }

    /**
     * Access a new value that has not yet been committed.
     *
     * @return the uncommitted new value, or null.
     */
    public T getNewValue() {
        T nv = newValue==null?null:(T)newValue[0];
        if (nv != null) {
            return nv;
        }
        return null;
    }


    /**
     * Serialization implementation that strips away the non serializable Optional part.
     *
     * @param oos the output stream
     * @throws IOException if serialization fails.
     */
    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.writeObject(getUpdatePolicy());
        oos.writeObject(get());
    }

    /**
     * Reads an instance from the input stream.
     *
     * @param ois the object input stream
     * @throws IOException            if deserialization fails.
     * @throws ClassNotFoundException
     */
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        this.updatePolicy = (UpdatePolicy) ois.readObject();
        if (isPresent()) {
            this.value = (T) ois.readObject();
        }
        newValue = null;
    }


    /**
     * Simple helper that allows keeping the listeners registered as weak references, hereby avoiding any
     * memory leaks.
     *
     * @param <I> the type
     */
    private class WeakList<I> {
        final List<WeakReference<I>> refs = new LinkedList<>();

        /**
         * Adds a new instance.
         *
         * @param t the new instance, not null.
         */
        void add(I t) {
            refs.add(new WeakReference<>(t));
        }

        /**
         * Removes a instance.
         *
         * @param t the instance to be removed.
         */
        void remove(I t) {
            synchronized (refs) {
                for (Iterator<WeakReference<I>> iterator = refs.iterator(); iterator.hasNext(); ) {
                    WeakReference<I> ref = iterator.next();
                    I instance = ref.get();
                    if (instance == null || instance == t) {
                        iterator.remove();
                        break;
                    }
                }
            }
        }


        /**
         * Access a list (copy) of the current instances that were not discarded by the GC.
         *
         * @return the list of accessible items.
         */
        public List<I> get() {
            synchronized (refs) {
                List<I> res = new ArrayList<>();
                for (Iterator<WeakReference<I>> iterator = refs.iterator(); iterator.hasNext(); ) {
                    WeakReference<I> ref = iterator.next();
                    I instance = ref.get();
                    if (instance == null) {
                        iterator.remove();
                    } else {
                        res.add(instance);
                    }
                }
                return res;
            }
        }
    }


}
