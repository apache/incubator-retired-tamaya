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
package org.apache.tamaya.core.internal.inject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

import org.apache.tamaya.ConfigChangeSet;
import org.apache.tamaya.ConfigException;
import org.apache.tamaya.Configuration;
import org.apache.tamaya.PropertySource;
import org.apache.tamaya.annotation.*;
import org.apache.tamaya.core.internal.Utils;

/**
 * Structure that contains and manages configuration related things for a configured type registered.
 * Created by Anatole on 03.10.2014.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class ConfiguredType {
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
                continue;
            }
            try {
                ConfiguredField configuredField = new ConfiguredField(f);
                configuredFields.add(configuredField);
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
                continue;
            }
            ObservesConfigChange mAnnot = m.getAnnotation(ObservesConfigChange.class);
            Collection<ConfiguredProperty> propertiesAnnots = Utils.getAnnotations(m, ConfiguredProperty.class, ConfiguredProperties.class);
            if (type.isInterface()) {
                // it is a template
                if (mAnnot != null) {
                    if (m.isDefault()) {
                        addObserverMethod(m);
                    }
                } else {
                    if (m.isDefault()) {
                        addPropertySetter(m, propertiesAnnots);
                    }
                }
            } else {
                if (mAnnot != null) {
                    addObserverMethod(m);
                } else {
                    addPropertySetter(m, propertiesAnnots);
                }
            }
        }
    }

    private boolean addPropertySetter(Method m, Collection<ConfiguredProperty> propertiesAnnots) {
        if (!propertiesAnnots.isEmpty()) {
            if (m.getParameterTypes().length == 0) {
                // getter method
                Class<?> returnType = m.getReturnType();
                if (!void.class.equals(returnType)) {
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



    private void addObserverMethod(Method m) {
        if (m.getParameterTypes().length != 1) {
            return;
        }
        if (!m.getParameterTypes()[0].equals(ConfigChangeSet.class)) {
            return;
        }
        if (!void.class.equals(m.getReturnType())) {
            return;
        }
        try {
            this.callbackMethods.add(new ConfigChangeCallbackMethod(m));
        } catch (Exception e) {
            throw new ConfigException("Failed to initialized configured callback method: " +
                    m.getDeclaringClass().getName() + '.' + m.getName(), e);
        }
    }


    /**
     * Method called to configure an instance.
     *
     * @param instance       The instance to be configured.
     * @param configurations Configuration instances that replace configuration served by services. This allows
     *                       more easily testing and adaption.
     */
    public void configure(Object instance, Configuration... configurations) {
        for (ConfiguredField field : configuredFields) {
            field.applyInitialValue(instance, configurations);
        }
        for (ConfiguredSetterMethod method : configuredSetterMethods) {
            method.applyInitialValue(instance, configurations);
            // TODO, if method should be recalled on changes, corresponding callbacks could be registered here
            WeakConfigListenerManager.of().registerConsumer(instance, method.createConsumer(instance, configurations));
        }
        // Register callbacks for this intance (weakly)
        for (ConfigChangeCallbackMethod callback : callbackMethods) {
            WeakConfigListenerManager.of().registerConsumer(instance, callback.createConsumer(instance, configurations));
        }
    }


    private String getName(Object source) {
        if (source instanceof PropertySource) {
            PropertySource ps = (PropertySource) source;
            return ps.getName();
        }
        return "N/A";
    }


    public boolean isConfiguredBy(Configuration configuration) {
        // TODO implement this
        return true;
    }

    public static boolean isConfigured(Class type) {
        if (type.getAnnotation(DefaultAreas.class) != null) {
            return true;
        }
        // if no class level annotation is there we might have field level annotations only
        for (Field field : type.getDeclaredFields()) {
            if (field.isAnnotationPresent(ConfiguredProperties.class)) {
                return true;
            }
        }
        // if no class level annotation is there we might have method level annotations only
        for (Method method : type.getDeclaredMethods()) {
            if (method.isAnnotationPresent(ConfiguredProperties.class)) {
                return true;
            }
        }
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
}
