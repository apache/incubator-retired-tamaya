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

import java.beans.PropertyChangeEvent;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

import org.apache.tamaya.ConfigException;
import org.apache.tamaya.Configuration;
import org.apache.tamaya.PropertySource;
import org.apache.tamaya.annotation.ConfiguredProperties;
import org.apache.tamaya.annotation.ConfiguredProperty;
import org.apache.tamaya.annotation.DefaultAreas;
import org.apache.tamaya.annotation.ObservesConfigChange;
import org.apache.tamaya.core.internal.Utils;

/**
 * Structure that contains and manages configuration related things for a configured type registered.
 * Created by Anatole on 03.10.2014.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class ConfiguredType {
    /** A list with all annotated instance variables. */
    private List<ConfiguredField> configuredFields = new ArrayList<>();
    /** A list with all annotated methods (templates). */
    private List<ConfiguredMethod> configuredMethods = new ArrayList<>();
    /** A list with all callback methods listening to config changes. */
    private List<ConfigChangeCallbackMethod> callbackMethods = new ArrayList<>();
    /** The basic type. */
	private Class type;

    /**
     * Creates an instance of this class hereby evaluating the config annotations given for later effective
     * injection (configuration) of instances.
     * @param type the instance type.
     */

	public ConfiguredType(Class type) {
        this.type = Objects.requireNonNull(type);
        initFields(type);
        initMethods(type);
    }

    private void initMethods(Class type) {
        for (Method m : type.getDeclaredMethods()) {
            ObservesConfigChange mAnnot = m.getAnnotation(ObservesConfigChange.class);
            if (mAnnot != null) {
                if (m.getParameterTypes().length != 1) {
                    continue;
                }
                if (!m.getParameterTypes()[0].equals(PropertyChangeEvent.class)) {
                    continue;
                }
                if (!void.class.equals(m.getReturnType())) {
                    continue;
                }
                try {
                    this.callbackMethods.add(new ConfigChangeCallbackMethod(m));
                } catch (Exception e) {
                    throw new ConfigException("Failed to initialized configured callback method: " +
                            m.getDeclaringClass().getName() + '.' + m.getName(), e);
                }
            } else {
                Collection<ConfiguredProperty> propertiesAnnots = Utils.getAnnotations(m, ConfiguredProperty.class, ConfiguredProperties.class);
                if (!propertiesAnnots.isEmpty()) {
                    try {
                        ConfiguredMethod configuredMethod = new ConfiguredMethod(m);
                        configuredMethods.add(configuredMethod);
                    } catch (Exception e) {
                        throw new ConfigException("Failed to initialized configured method: " +
                                m.getDeclaringClass().getName() + '.' + m.getName(), e);
                    }
                }
            }
        }
    }

    private void initFields(Class type) {
        for (Field f : type.getDeclaredFields()) {
            ConfiguredProperties propertiesAnnot = f.getAnnotation(ConfiguredProperties.class);
            if (propertiesAnnot != null) {
                try {
                    ConfiguredField configuredField = new ConfiguredField(f);
                    configuredFields.add(configuredField);
                } catch (Exception e) {
                    throw new ConfigException("Failed to initialized configured field: " +
                            f.getDeclaringClass().getName() + '.' + f.getName(), e);
                }
            } else {
                ConfiguredProperty propertyAnnot = f.getAnnotation(ConfiguredProperty.class);
                if (propertyAnnot != null) {
                    try {
                        ConfiguredField configuredField = new ConfiguredField(f);
                        configuredFields.add(configuredField);
                    } catch (Exception e) {
                        throw new ConfigException("Failed to initialized configured field: " +
                                f.getDeclaringClass().getName() + '.' + f.getName(), e);
                    }
                }
            }
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
            // TODO, if reinjection on changes should be done, corresponding callbacks could be registered here
        }
        for (ConfiguredMethod method : configuredMethods) {
            method.applyInitialValue(instance, configurations);
            // TODO, if method should be recalled on changes, corresponding callbacks could be registered here
        }
        // Register callbacks for this intance (weakly)
        for(ConfigChangeCallbackMethod callback: callbackMethods){
            WeakConfigListenerManager.of().registerConsumer(instance, callback.createConsumer(instance, configurations));
        }
    }


    private String getName(Object source){
        if(source instanceof PropertySource){
            PropertySource ps = (PropertySource)source;
            return ps.getMetaInfo().getName();
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
