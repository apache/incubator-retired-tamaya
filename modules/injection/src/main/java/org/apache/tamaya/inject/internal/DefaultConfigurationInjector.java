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

import org.apache.tamaya.ConfigurationProvider;
import org.apache.tamaya.inject.ConfigurationInjector;

import javax.annotation.Priority;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.tamaya.inject.ConfiguredItemSupplier;

/**
 * Simple injector singleton that also registers instances configured using weak references.
 */
@Priority(0)
public final class DefaultConfigurationInjector implements ConfigurationInjector {

    private Map<Class, ConfiguredType> configuredTypes = new ConcurrentHashMap<>();

    /**
     * Extract the configuration annotation config and registers it per class, for later reuse.
     *
     * @param type the type to be configured.
     * @return the configured type registered.
     */
    public ConfiguredType registerType(Class<?> type) {
        ConfiguredType confType = configuredTypes.get(type);
        if (confType == null) {
            confType = new ConfiguredType(type);
            ModelPopulator.register(confType);
            configuredTypes.put(type, confType);
        }
        return confType;
//        return configuredTypes.computeIfAbsent(type, ConfiguredType::new);
    }

    /**
     * Configured the current instance and reigsterd necessary listener to forward config change events as
     * defined by the current annotations in place.
     *
     * @param instance the instance to be configured
     */
    @Override
    public <T> T configure(T instance) {
        Class type = Objects.requireNonNull(instance).getClass();
        ConfiguredType configuredType = registerType(type);
        Objects.requireNonNull(configuredType).configure(instance);
        return instance;
    }

    /**
     * Create a template implementting the annotated methods based on current configuration data.
     *
     * @param templateType the type of the template to be created.
     */
    @Override
    public <T> T createTemplate(Class<T> templateType) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if(cl==null){
            cl = this.getClass().getClassLoader();
        }
        return (T) Proxy.newProxyInstance(cl, new Class[]{ConfiguredItemSupplier.class, Objects.requireNonNull(templateType)},
                new ConfigTemplateInvocationHandler(templateType, ConfigurationProvider.getConfiguration()));
    }


    @Override
    public <T> ConfiguredItemSupplier<T> getConfiguredSupplier(final ConfiguredItemSupplier<T> supplier) {
        return new ConfiguredItemSupplier<T>() {
            public T get() {
                return supplier.get();
            }
        };
    }
}
