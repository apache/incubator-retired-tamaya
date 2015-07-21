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
package org.apache.tamaya.events;

import org.apache.tamaya.ConfigException;
import org.apache.tamaya.ConfigOperator;
import org.apache.tamaya.ConfigQuery;
import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;
import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.spi.PropertyConverter;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * /**
 * Configuration implementation that stores all current values of a given (possibly dynamic, contextual and non remote
 * capable instance) and is fully serializable. Note that hereby only the scannable key/value pairs are considered.
 */
public final class FrozenConfiguration implements Configuration, Serializable {
    private static final long serialVersionUID = -6373137316556444171L;
    /**
     * The properties frozen.
     */
    private Map<String, String> properties = new HashMap<>();

    /**
     * Constructor.
     *
     * @param config The base configuration.
     */
    private FrozenConfiguration(Configuration config) {
        this.properties.putAll(config.getProperties());
        this.properties.put("[meta]frozenAt", String.valueOf(System.currentTimeMillis()));
        this.properties = Collections.unmodifiableMap(this.properties);
    }

    /**
     * Creates a new FrozenConfiguration instance based on a Configuration given.
     *
     * @param config the configuration to be frozen, not null.
     * @return the frozen Configuration.
     */
    public static FrozenConfiguration of(Configuration config) {
        if (config instanceof FrozenConfiguration) {
            return (FrozenConfiguration) config;
        }
        return new FrozenConfiguration(config);
    }

    @Override
    public String get(String key) {
        return this.properties.get(key);
    }

    @Override
    public <T> T get(String key, Class<T> type) {
        return (T) get(key, TypeLiteral.of(type));
    }

    /**
     * Accesses the current String value for the given key and tries to convert it
     * using the {@link org.apache.tamaya.spi.PropertyConverter} instances provided by the current
     * {@link org.apache.tamaya.spi.ConfigurationContext}.
     *
     * @param key  the property's absolute, or relative path, e.g. @code
     *             a/b/c/d.myProperty}.
     * @param type The target type required, not null.
     * @param <T>  the value type
     * @return the converted value, never null.
     */
    @Override
    public <T> T get(String key, TypeLiteral<T> type) {
        String value = get(key);
        if (value != null) {
            List<PropertyConverter<T>> converters = ConfigurationProvider.getConfigurationContext()
                    .getPropertyConverters(type);
            for (PropertyConverter<T> converter : converters) {
                try {
                    T t = converter.convert(value);
                    if (t != null) {
                        return t;
                    }
                } catch (Exception e) {
                    Logger.getLogger(getClass().getName())
                            .log(Level.FINEST, "PropertyConverter: " + converter + " failed to convert value: " + value,
                                    e);
                }
            }
            throw new ConfigException("Unparseable config value for type: " + type.getRawType().getName() + ": " + key);
        }

        return null;
    }

    @Override
    public Map<String, String> getProperties() {
        return properties;
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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FrozenConfiguration that = (FrozenConfiguration) o;
        return properties.equals(that.properties);
    }

    @Override
    public int hashCode() {
        return properties.hashCode();
    }

    @Override
    public String toString() {
        return "FrozenConfiguration{" +
                "properties=" + properties +
                '}';
    }
}
