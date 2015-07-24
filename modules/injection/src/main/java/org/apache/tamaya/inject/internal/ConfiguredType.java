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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.logging.Logger;

import org.apache.tamaya.ConfigException;
import org.apache.tamaya.inject.ConfiguredProperty;
import org.apache.tamaya.inject.NoConfig;
import org.apache.tamaya.event.ObservesConfigChange;
import org.apache.tamaya.event.PropertyChangeSet;

/**
 * Structure that contains and manages configuration related things for a configured type registered.
 * Created by Anatole on 03.10.2014.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class ConfiguredType {
    /** The log used. */
    private static final Logger LOG = Logger.getLogger(ConfiguredType.class.getName());
    /**
     * A list with all annotated instance variables.
     */
    private List<ConfiguredField> configuredFields = new ArrayList<>();
    /**
     * A list with all annotated methods (templates).
     */
    private List<ConfiguredSetterMethod> configuredSetterMethods = new ArrayList<>();
    /**
     * A list with all callback methods listening to config changes.
     */
    private List<ConfigChangeCallbackMethod> callbackMethods = new ArrayList<>();
    /**
     * The basic type.
     */
    private Class type;

    /**
     * Creates an instance of this class hereby evaluating the config annotations given for later effective
     * injection (configuration) of instances.
     *
     * @param type the instance type.
     */

    public ConfiguredType(Class type) {
        this.type = Objects.requireNonNull(type);
        initFields(type);
        initMethods(type);
    }

    private void initFields(Class type) {
        for (Field f : type.getDeclaredFields()) {
            if (f.isAnnotationPresent(NoConfig.class)) {
                LOG.finest("Ignored @NoConfig annotated field " + f.getClass().getName() + "#" +
                        f.toGenericString());
                continue;
            }
            if (Modifier.isFinal(f.getModifiers())) {
                LOG.finest("Ignored final field " + f.getClass().getName() + "#" +
                        f.toGenericString());
                continue;
            }
            if (f.isSynthetic()) {
                LOG.finest("Ignored synthetic field " + f.getClass().getName() + "#" +
                        f.toGenericString());
                continue;
            }
            try {
                ConfiguredField configuredField = new ConfiguredField(f);
                configuredFields.add(configuredField);
                LOG.finer("Registered field " + f.getClass().getName() + "#" +
                        f.toGenericString());
            } catch (Exception e) {
                throw new ConfigException("Failed to initialized configured field: " +
                        f.getDeclaringClass().getName() + '.' + f.getName(), e);
            }
        }
    }

    private void initMethods(Class type) {
        // TODO revisit this logic here...
        for (Method m : type.getDeclaredMethods()) {
            if (m.isAnnotationPresent(NoConfig.class)) {
                LOG.finest("Ignored @NoConfig annotated method " + m.getClass().getName() + "#" +
                        m.toGenericString());
                continue;
            }
            if (m.isSynthetic()) {
                LOG.finest("Ignored synthetic method " + m.getClass().getName() + "#" +
                        m.toGenericString());
                continue;
            }
            ObservesConfigChange observesAnnot = m.getAnnotation(ObservesConfigChange.class);
            ConfiguredProperty propAnnot = m.getAnnotation(ConfiguredProperty.class);
            if (type.isInterface()) {
                // it is a template
                if (observesAnnot != null && m.isDefault()) {
                    if(addObserverMethod(m)){
                        LOG.finer("Added configured observer for template: " + m.getClass().getName() + "#" +
                                m.toGenericString());
                    }
                }
            } else {
                if (observesAnnot != null) {
                    if(addObserverMethod(m)){
                        LOG.finer("Added configured observer: " + m.getClass().getName() + "#" +
                                m.toGenericString());
                    }
                } else {
                    if (addPropertySetter(m, propAnnot)) {
                        LOG.finer("Added configured setter: " + m.getClass().getName() + "#" +
                                m.toGenericString());
                    }
                }
            }
        }
    }

    private boolean addPropertySetter(Method m, ConfiguredProperty prop) {
        if (prop!=null) {
            if (m.getParameterTypes().length == 1) {
                // getter method
                Class<?> returnType = m.getReturnType();
                if (void.class.equals(returnType)) {
                    try {
                        configuredSetterMethods.add(new ConfiguredSetterMethod(m));
                        return true;
                    } catch (Exception e) {
                        throw new ConfigException("Failed to initialized configured setter method: " +
                                m.getDeclaringClass().getName() + '.' + m.getName(), e);
                    }
                }
            }
        }
        return false;
    }



    private boolean addObserverMethod(Method m) {
        if (m.getParameterTypes().length != 1) {
            return false;
        }
        if (!m.getParameterTypes()[0].equals(PropertyChangeSet.class)) {
            return false;
        }
        if (!void.class.equals(m.getReturnType())) {
            return false;
        }
        try {
            this.callbackMethods.add(new ConfigChangeCallbackMethod(m));
            return true;
        } catch (Exception e) {
            throw new ConfigException("Failed to initialized configured callback method: " +
                    m.getDeclaringClass().getName() + '.' + m.getName(), e);
        }
    }


    /**
     * Method called to configure an instance.
     *
     * @param instance       The instance to be configured.
     */
    public void configure(Object instance) {
        for (ConfiguredField field : configuredFields) {
            field.applyValue(instance);
        }
        for (ConfiguredSetterMethod method : configuredSetterMethods) {
            method.applyValue(instance, true);
//            // TODO, if method should be recalled on changes, corresponding callbacks could be registered here
//            WeakConfigListenerManager.of().registerConsumer(instance, method.createConsumer(instance));
        }
        // Register callbacks for this instance (weakly)
        for (ConfigChangeCallbackMethod callback : callbackMethods) {
            WeakConfigListenerManager.of().registerConsumer(instance, callback.createConsumer(instance));
        }
    }


    public static boolean isConfigured(Class type) {
        if (type.getAnnotation(org.apache.tamaya.inject.ConfiguredType.class) != null) {
            return true;
        }
        // if no class level annotation is there we might have field level annotations only
        for (Field field : type.getDeclaredFields()) {
            if (field.isAnnotationPresent(ConfiguredProperty.class)) {
                return true;
            }
        }
        // if no class level annotation is there we might have method level annotations only
        for (Method method : type.getDeclaredMethods()) {
            if (method.isAnnotationPresent(ConfiguredProperty.class)) {
                return true;
            }
        }
        return false;
    }

    public Class getType() {
        return this.type;
    }

    @Override
    public String toString() {
        return "ConfiguredType{"+ this.getType().getName() + '}';
    }
}
