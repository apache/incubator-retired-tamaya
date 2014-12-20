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

import org.apache.tamaya.Configuration;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple injector singleton that also registers instances configured using weak references.
 */
@SuppressWarnings("rawtypes")
public final class ConfigurationInjector {

    private static final ConfigurationInjector INSTANCE = new ConfigurationInjector();

	private Map<Class, ConfiguredType> configuredTypes = new ConcurrentHashMap<>();

    /**
     * Extract the configuration annotation config and registers it per class, for later reuse.
     * @param type the type to be configured.
     * @return the configured type registered.
     */
    public static ConfiguredType registerType(Class<?> type){
        if (!ConfiguredType.isConfigured(type)) {
            return null;
        }
        return INSTANCE.configuredTypes.computeIfAbsent(type, ConfiguredType::new);
    }

    /**
     * Configured the current instance and reigsterd necessary listener to forward config change events as
     * defined by the current annotations in place.
     * @param instance the instance to be configured
     * @param configurations Configuration instances that replace configuration served by services. This allows
     *                       more easily testing and adaption.
     */
    public static void configure(Object instance, Configuration... configurations){
        Class type = Objects.requireNonNull(instance).getClass();
        if (!ConfiguredType.isConfigured(type)) {
            throw new IllegalArgumentException("Not a configured type: " + type.getName());
        }
        ConfiguredType configuredType = registerType(type);
        Objects.requireNonNull(configuredType).configure(instance, configurations);
    }



}
