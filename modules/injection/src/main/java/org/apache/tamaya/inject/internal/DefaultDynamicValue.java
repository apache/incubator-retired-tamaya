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
package org.apache.tamaya.inject.internal;

import org.apache.tamaya.ConfigException;
import org.apache.tamaya.Configuration;
import org.apache.tamaya.inject.Supplier;
import org.apache.tamaya.spi.PropertyConverter;
import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.inject.DynamicValue;
import org.apache.tamaya.inject.WithPropertyConverter;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
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
public final class DefaultDynamicValue<T> implements DynamicValue<T>, Serializable {

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
     * The target type of the property used to lookup a matching {@link org.apache.tamaya.spi.PropertyConverter}.
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
    private UpdatePolicy updatePolicy = UpdatePolicy.NEVER;
    /**
     * The current value, never null.
     */
    private transient T value;
    /**
     * The new value, or null.
     */
    private transient T newValue;
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
                        PropertyConverter<T> propertyConverter, List<String> keys) {
        this.propertyName = Objects.requireNonNull(propertyName);
        this.keys = keys.toArray(new String[keys.size()]);
        this.configuration = Objects.requireNonNull(configuration);
        this.propertyConverter = propertyConverter;
        this.targetType = targetType;
        this.value = evaluateValue();
    }

    public static DynamicValue of(Field annotatedField, Configuration configuration){
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
            targetType = (Class) types[0];
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
                TypeLiteral.of(targetType), propertyConverter, keys);
    }

    public static DynamicValue of(Method method, Configuration configuration){
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
                configuration, TypeLiteral.of(targetType), propertyConverter, InjectionUtils.getKeys(method));
    }

    /**
     * Performs a commit, if necessary, and returns the current value.
     *
     * @return the non-null value held by this {@code DynamicValue}
     * @throws org.apache.tamaya.ConfigException if there is no value present
     * @see DefaultDynamicValue#isPresent()
     */
    public T commitAndGet() {
        commit();
        return get();
    }

    /**
     * Commits a new value that has not been committed yet, make it the new value of the instance. On change any
     * registered listeners will be triggered.
     */
    public void commit() {
        synchronized (this) {
            PropertyChangeEvent evt = new PropertyChangeEvent(this, propertyName, value,
                    newValue);
            value = newValue;
            newValue = null;
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
     * @throws org.apache.tamaya.ConfigException if there is no value present
     * @see DefaultDynamicValue#isPresent()
     */
    public T get() {
        return value;
    }

    /**
     * Method to check for and apply a new value. Depending on the {@link  UpdatePolicy}
     * the value is immediately or deferred visible (or it may even be ignored completely).
     *
     * @return true, if a new value has been detected. The value may not be visible depending on the current
     * {@link DefaultDynamicValue.UpdatePolicy} in place.
     */
    public boolean updateValue() {
        T newValue = evaluateValue();
        if (Objects.equals(newValue, this.value)) {
            return false;
        }
        switch (this.updatePolicy) {
            case IMMEDIATE:
                this.newValue = newValue;
                commit();
                break;
            case LOG_AND_DISCARD:
                Logger.getLogger(getClass().getName()).info("Discard change on " + this + ", newValue=" + newValue);
                this.newValue = null;
                break;
            case NEVER:
                this.newValue = null;
                break;
            case EXPLCIT:
            default:
                this.newValue = newValue;
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
                value = propertyConverter.convert(source);
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
        T nv = newValue;
        if (nv != null) {
            return nv;
        }
        return null;
    }

    /**
     * Return {@code true} if there is a value present, otherwise {@code false}.
     *
     * @return {@code true} if there is a value present, otherwise {@code false}
     */
    public boolean isPresent() {
        return value != null;
    }


    /**
     * Return the value if present, otherwise return {@code other}.
     *
     * @param other the value to be returned if there is no value present, may
     *              be null
     * @return the value, if present, otherwise {@code other}
     */
    public T orElse(T other) {
        if (value == null) {
            return other;
        }
        return value;
    }

    /**
     * Return the value if present, otherwise invoke {@code other} and return
     * the result of that invocation.
     *
     * @param other a {@code Supplier} whose result is returned if no value
     *              is present
     * @return the value if present otherwise the result of {@code other.get()}
     * @throws NullPointerException if value is not present and {@code other} is
     *                              null
     */
    public T orElseGet(Supplier<? extends T> other) {
        if (value == null) {
            return other.get();
        }
        return value;
    }

    /**
     * Return the contained value, if present, otherwise throw an exception
     * to be created by the provided supplier.
     * <p>
     * NOTE A method reference to the exception constructor with an empty
     * argument list can be used as the supplier. For example,
     * {@code IllegalStateException::new}
     *
     * @param <X>               Type of the exception to be thrown
     * @param exceptionSupplier The supplier which will return the exception to
     *                          be thrown
     * @return the present value
     * @throws X                    if there is no value present
     * @throws NullPointerException if no value is present and
     *                              {@code exceptionSupplier} is null
     */
    public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        if (value == null) {
            throw exceptionSupplier.get();
        }
        return value;
    }

    /**
     * Serialization implementation that strips away the non serializable Optional part.
     *
     * @param oos the output stream
     * @throws java.io.IOException if serialization fails.
     */
    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.writeObject(updatePolicy);
        if (isPresent()) {
            oos.writeObject(this.value);
        } else {
            oos.writeObject(null);
        }
    }

    /**
     * Reads an instance from the input stream.
     *
     * @param ois the object input stream
     * @throws java.io.IOException    if deserialization fails.
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
