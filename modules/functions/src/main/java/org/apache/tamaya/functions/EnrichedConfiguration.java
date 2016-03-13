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
package org.apache.tamaya.functions;

import org.apache.tamaya.ConfigOperator;
import org.apache.tamaya.ConfigQuery;
import org.apache.tamaya.Configuration;
import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.spi.ConfigurationContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Configuration, that has values added or overridden.
 */
class EnrichedConfiguration implements Configuration {

    private final Configuration baseConfiguration;

    private final Map<String, Object> addedProperties = new HashMap<>();

    private final boolean overriding;

    /**
     * Constructor.
     *
     * @param configuration the base config, not null.
     * @param properties the properties to be added, not null.
     * @param overriding true, if existing keys should be overriden, or config should be extended only.
     */
    EnrichedConfiguration(Configuration configuration, Map<String, Object> properties, boolean overriding) {
        this.baseConfiguration = Objects.requireNonNull(configuration);
        this.addedProperties.putAll(addedProperties);
        this.overriding = overriding;
    }

    @Override
    public String get(String key) {
        if (overriding) {
            Object val = addedProperties.get(key);
            if (val != null) {
                return val.toString();
            }
            return baseConfiguration.get(key);
        }
        String val = baseConfiguration.get(key);
        if (val != null) {
            return val;
        }
        Object val2 = addedProperties.get(key);
        if (val2 != null) {
            return val2.toString();
        }
        return null;
    }

    @Override
    public String getOrDefault(String key, String defaultValue) {
        String val = get(key);
        if (val == null) {
            return defaultValue;
        }
        return val;
    }

    @Override
    public <T> T getOrDefault(String key, Class<T> type, T defaultValue) {
        T val = get(key, type);
        if (val == null) {
            return defaultValue;
        }
        return val;
    }

    @Override
    public <T> T get(String key, Class<T> type) {
        return (T) get(key, TypeLiteral.of(type));
    }

    @Override
    public <T> T get(String key, TypeLiteral<T> type) {
        if (overriding) {
            Object val = addedProperties.get(key);
            if (val != null && type.getRawType().isAssignableFrom(val.getClass())) {
                return (T) val;
            }
            return baseConfiguration.get(key, type);
        }
        T val = baseConfiguration.get(key, type);
        if (val != null) {
            return val;
        }
        Object val2 = addedProperties.get(key);
        if (val2 != null && type.getRawType().isAssignableFrom(val2.getClass())) {
            return (T) val2;
        }
        return null;
    }

    @Override
    public <T> T getOrDefault(String key, TypeLiteral<T> type, T defaultValue) {
        T val = get(key, type);
        if (val == null) {
            return defaultValue;
        }
        return val;
    }

    @Override
    public Map<String, String> getProperties() {
        Map<String, String> allProps = new HashMap<>();
        if (overriding) {
            allProps.putAll(baseConfiguration.getProperties());
            for (Map.Entry<String, Object> en : addedProperties.entrySet()) {
                allProps.put(en.getKey(), en.getValue().toString());
            }
        } else {
            for (Map.Entry<String, Object> en : addedProperties.entrySet()) {
                allProps.put(en.getKey(), en.getValue().toString());
            }
            allProps.putAll(baseConfiguration.getProperties());
        }
        return allProps;
    }

    @Override
    public Configuration with(ConfigOperator operator) {
        return operator.operate(this);
    }

    @Override
    public <T> T query(ConfigQuery<T> query) {
        return query.query(this);
    }

    @Override
    public ConfigurationContext getContext() {
        return baseConfiguration.getContext();
    }

}
