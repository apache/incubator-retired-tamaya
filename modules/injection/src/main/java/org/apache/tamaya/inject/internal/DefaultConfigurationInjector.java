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

import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;
import org.apache.tamaya.inject.ConfigurationInjector;

import javax.annotation.Priority;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.apache.tamaya.inject.api.ConfiguredItemSupplier;
import org.apache.tamaya.inject.api.NoConfig;
import org.apache.tamaya.inject.api.Config;
import org.apache.tamaya.inject.api.ConfigDefaultSections;
import org.apache.tamaya.inject.spi.ConfiguredType;

/**
 * Simple injector singleton that also registers instances configured using weak references.
 */
@Priority(0)
public final class DefaultConfigurationInjector implements ConfigurationInjector {

    private final Map<Class<?>, ConfiguredType> configuredTypes = new ConcurrentHashMap<>();

    private static final Logger LOG = Logger.getLogger(DefaultConfigurationInjector.class.getName());

    /**
     * Extract the configuration annotation config and registers it per class, for later reuse.
     *
     * @param type the type to be configured.
     * @return the configured type registered.
     */
    public ConfiguredType registerType(Class<?> type) {
        ConfiguredType confType = configuredTypes.get(type);
        if (confType == null && isConfigured(type)) {
            confType = new ConfiguredTypeImpl(type);
            configuredTypes.put(type, confType);
            InjectionHelper.sendConfigurationEvent(confType);
        }
        return confType;
//        return configuredTypes.computeIfAbsent(type, ConfigDefaultSections::new);
    }

    /**
     * CHecks if type is eligible for configuration injection.
     * @param type the target type, not null.
     * @return true, if the type, a method or field has Tamaya config annotation on it.
     */
    private boolean isConfigured(Class<?> type) {
        if(type.getClass().isAnnotationPresent(ConfigDefaultSections.class)){
            return true;
        }
        for (Field f : type.getDeclaredFields()) {
            if (f.isAnnotationPresent(NoConfig.class) || f.isAnnotationPresent(Config.class)) {
                return true;
            }
        }
        for (Method m : type.getDeclaredMethods()) {
            if (m.isAnnotationPresent(NoConfig.class) || m.isAnnotationPresent(Config.class)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Configured the current instance and reigsterd necessary listener to forward config change events as
     * defined by the current annotations in place.
     *
     * @param instance the instance to be configured
     */
    @Override
    public <T> T configure(T instance) {
        return configure(instance, ConfigurationProvider.getConfiguration());
    }

    /**
     * Configured the current instance and reigsterd necessary listener to forward config change events as
     * defined by the current annotations in place.
     *
     * @param instance the instance to be configured
     * @param config the target configuration, not null.
     */
    @Override
    public <T> T configure(T instance, Configuration config) {
        Class<?> type = Objects.requireNonNull(instance).getClass();
        ConfiguredType configuredType = registerType(type);
        if(configuredType!=null){
            configuredType.configure(instance, config);
        }else{
            LOG.info("Instance passed is not annotated for configuration: " + instance);
        }
        return instance;
    }

    /**
     * Create a template implementting the annotated methods based on current configuration data.
     *
     * @param templateType the type of the template to be created.
     */
    @Override
    public <T> T createTemplate(Class<T> templateType) {
        return createTemplate(templateType, ConfigurationProvider.getConfiguration());
    }

    /**
     * Create a template implementting the annotated methods based on current configuration data.
     *
     * @param templateType the type of the template to be created.
     * @param config the target configuration, not null.
     */
    @Override
    public <T> T createTemplate(Class<T> templateType, Configuration config) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if(cl==null){
            cl = this.getClass().getClassLoader();
        }
        return templateType.cast(Proxy.newProxyInstance(cl, new Class[]{ConfiguredItemSupplier.class, Objects.requireNonNull(templateType)},
                new ConfigTemplateInvocationHandler(templateType)));
    }

    @Override
    public <T> ConfiguredItemSupplier<T> getConfiguredSupplier(final ConfiguredItemSupplier<T> supplier) {
        return getConfiguredSupplier(supplier, ConfigurationProvider.getConfiguration());
    }

    @Override
    public <T> ConfiguredItemSupplier<T> getConfiguredSupplier(final ConfiguredItemSupplier<T> supplier, final Configuration config) {
        return new ConfiguredItemSupplier<T>() {
            public T get() {
                return configure(supplier.get(), config);
            }
        };
    }
}
