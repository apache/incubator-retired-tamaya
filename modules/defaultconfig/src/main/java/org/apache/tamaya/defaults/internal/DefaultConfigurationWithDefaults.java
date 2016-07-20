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
package org.apache.tamaya.defaults.internal;

import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;
import org.apache.tamaya.defaults.ConfigurationWithDefaults;
import org.apache.tamaya.types.TypeLiteral;
import org.apache.tamaya.types.TypedConfiguration;
import org.apache.tamaya.types.internal.adapters.DefaultTypedConfiguration;

import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Implementation of the {@link TypedConfiguration} API.
 */
public class DefaultConfigurationWithDefaults implements ConfigurationWithDefaults {
    /**
     * The logger.
     */
    private static final Logger LOG = Logger.getLogger(DefaultTypedConfiguration.class.getName());

    private TypedConfiguration configuration;

    /**
     * Constructor.
     * @param configuration The configuration to be decorated.
     */
    public DefaultConfigurationWithDefaults(Configuration configuration){
        this.configuration = Objects.requireNonNull(configuration).adapt(TypedConfiguration.class);
    }

    @Override
    public String get(String key) {
        return configuration.get(key);
    }

    @Override
    public String getOrDefault(String key, String defaultValue) {
        String value = configuration.get(key);
        if(value==null){
            return defaultValue;
        }
        return value;
    }

    @Override
    public <T> T getOrDefault(String key, Class<T> type, T defaultValue) {
        T val = get(key, type);
        if(val==null){
            return defaultValue;
        }
        return val;
    }

    @Override
    public Map<String, String> getProperties() {
        return configuration.getProperties();
    }

    @Override
    public <T> T adapt(Class<T> type) {
        if(ConfigurationWithDefaults.class.equals(type)){
            return (T)this;
        }
        return configuration.adapt(type);
    }

    @Override
    public <T> T getOrDefault(String key, TypeLiteral<T> type, T defaultValue) {
        T val = get(key, type);
        if(val==null){
            return defaultValue;
        }
        return val;
    }

    @Override
    public <T> T get(String key, Class<T> type) {
        return configuration.get(key, type);
    }

    @Override
    public <T> T get(String key, TypeLiteral<T> type) {
        return configuration.get(key, type);
    }
}
