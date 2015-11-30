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
import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;
import org.apache.tamaya.inject.ConfigAutoInject;
import org.apache.tamaya.inject.NoConfig;
import org.apache.tamaya.inject.api.Config;
import org.apache.tamaya.inject.api.ConfigDefaultSections;
import org.apache.tamaya.inject.spi.ConfiguredField;
import org.apache.tamaya.inject.spi.ConfiguredMethod;
import org.apache.tamaya.inject.spi.ConfiguredType;

/**
 * Structure that contains and manages configuration related things for a configured type registered.
 * Created by Anatole on 03.10.2014.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class ConfiguredTypeImpl implements ConfiguredType{
    /** The log used. */
    private static final Logger LOG = Logger.getLogger(ConfiguredTypeImpl.class.getName());
    /**
     * A list with all annotated instance variables.
     */
    private List<ConfiguredField> configuredFields = new ArrayList<>();
    /**
     * A list with all annotated methods (templates).
     */
    private List<ConfiguredMethod> configuredSetterMethods = new ArrayList<>();
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

    public ConfiguredTypeImpl(Class type) {
        this.type = Objects.requireNonNull(type);
        ConfigDefaultSections confType = (ConfigDefaultSections)
                type.getAnnotation(ConfigDefaultSections.class);
        ConfigAutoInject autoInject = (ConfigAutoInject)type.getAnnotation(ConfigAutoInject.class);
        if(confType!=null){
            initFields(type, autoInject!=null);
            initMethods(type, autoInject!=null);
        }else{
            initFields(type, false);
            initMethods(type, false);
        }
    }

    private void initFields(Class type, boolean autoConfigure) {
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
                if(isConfiguredField(f) || autoConfigure) {
                    ConfiguredField configuredField = new ConfiguredFieldImpl(f);
                    configuredFields.add(configuredField);
                    LOG.finer("Registered field " + f.getClass().getName() + "#" +
                            f.toGenericString());
                }
            } catch (Exception e) {
                throw new ConfigException("Failed to initialized configured field: " +
                        f.getDeclaringClass().getName() + '.' + f.getName(), e);
            }
        }
    }

    private void initMethods(Class type, boolean autoConfigure) {
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
            if(isConfiguredMethod(m) || autoConfigure) {
                Config propAnnot = m.getAnnotation(Config.class);
                if (addPropertySetter(m, propAnnot)) {
                    LOG.finer("Added configured setter: " + m.getClass().getName() + "#" +
                            m.toGenericString());
                }
            }
        }
    }

    private boolean addPropertySetter(Method m, Config prop) {
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


    /**
     * Method called to configure an instance.
     *
     * @param instance       The instance to be configured.
     */
    public void configure(Object instance) {
        configure(instance, ConfigurationProvider.getConfiguration());
    }

    @Override
    public void configure(Object instance, Configuration config) {
        for (ConfiguredField field : configuredFields) {
            field.configure(instance, config);
        }
        for (ConfiguredMethod method : configuredSetterMethods) {
            method.configure(instance, config);
//            // TODO, if method should be recalled on changes, corresponding callbacks could be registered here
        }
    }


    public static boolean isConfigured(Class type) {
        if (type.getAnnotation(ConfigDefaultSections.class) != null) {
            return true;
        }
        // if no class level annotation is there we might have field level annotations only
        for (Field field : type.getDeclaredFields()) {
            if (isConfiguredField(field)) {
                return true;
            }
        }
        // if no class level annotation is there we might have method level annotations only
        for (Method method : type.getDeclaredMethods()) {
            if(isConfiguredMethod(method)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isConfiguredField(Field field) {
        return field.isAnnotationPresent(Config.class);
    }

    public static boolean isConfiguredMethod(Method method) {
        return method.isAnnotationPresent(Config.class);
    }

    @Override
    public Class getType() {
        return this.type;
    }

    @Override
    public String getName() {
        return this.type.getName();
    }

    /**
     * Get the registered configured fields.
     * @return the registered configured fields, never null.
     */
    @Override
    public Collection<ConfiguredField> getConfiguredFields(){
        return configuredFields;
    }

    /**
     * Get the registered annotated setter methods.
     * @return the registered annotated setter methods, never null.
     */
    @Override
    public Collection<ConfiguredMethod> getConfiguredMethods(){
        return configuredSetterMethods;
    }

    @Override
    public String toString() {
        return "ConfigDefaultSections{"+ this.getType().getName() + '}';
    }
}
