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
package org.apache.tamaya.base;


import org.apache.tamaya.base.configsource.ConfigSourceManager;
import org.apache.tamaya.base.convert.ConverterManager;
import org.apache.tamaya.base.filter.Filter;
import org.apache.tamaya.base.filter.FilterManager;

import javax.config.Config;
import javax.config.ConfigValue;
import javax.config.spi.ConfigSource;
import javax.config.spi.Converter;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 * Implementation of the Configuration API.
 */
public class DefaultConfig implements Config, ConfigContextSupplier {
    /**
     * The logger.
     */
    private static final Logger LOG = Logger.getLogger(DefaultConfig.class.getName());

    /**
     * The current {@link ConverterManager} of the current instance.
     */
    private final ConverterManager converterManager = new ConverterManager();

    /**
     * The current {@link ConverterManager} of the current instance.
     */
    private final FilterManager filterManager = new FilterManager();

    /**
     * The current {@link ConfigSourceManager} of the current instance.
     */
    private final ConfigSourceManager configSourceManager = new ConfigSourceManager();


    /**
     * Constructor.
     * @param configContext The configuration Context to be used.
     */
    public DefaultConfig(ConfigContext configContext){
        this.configSourceManager.addSources(configContext.getConfigSources());
        this.configSourceManager.setConfigValueCombinationPolicy(configContext.getConfigValueCombinationPolicy());
        configContext.getConverters().forEach((t,c) -> this.converterManager.addConverter(t, Collection.class.cast(c)));
        this.filterManager.addFilter(configContext.getFilters());
    }

    /**
     * Accesses the current String value for the given key and tries to convert it
     * using the {@link javax.config.spi.Converter} instances provided by the current
     * {@link ConfigContext}.
     *
     * @param key  the property's absolute, or relative path, e.g. @code
     *             a/b/c/d.myProperty}, never {@code null}.
     * @param targetType The target type required, not {@code null}.
     * @param <T>  the value type
     * @return the converted value, never {@code null}.
     */
    @Override
    public <T> T getValue(String key, Class<T> targetType) {
        Objects.requireNonNull(key, "Key must not be null.");
        Objects.requireNonNull(targetType, "Target type must not be null.");

        String value = configSourceManager.evaluteRawValue(key);
        if(value==null){
            return null;
        }
        value = filterManager.filterValue(key, value, this);
        if(value!=null){
            return (T)converterManager.convertValue(value, targetType);
        }
        return null;
    }


    @Override
    public <T> Optional<T> getOptionalValue(String key, Class<T> type) {
        Objects.requireNonNull(key, "Key must not be null.");
        Objects.requireNonNull(type, "Type must not be null.");
        T val = getValue(key, type);
        return Optional.ofNullable(val);
    }

    @Override
    public ConfigValue<String> access(String key) {
        return new DefaultConfigValue(this, key, String.class);
    }

    /**
     * Get the current properties, composed by the loaded {@link ConfigSource} and filtered
     * by registered {@link Filter}.
     *
     * @return the final properties.
     */
    @Override
    public Set<String> getPropertyNames() {
        Map<String, String> filtered = configSourceManager.evaluateRawValues();
        return filtered.keySet();
    }

    @Override
    public Iterable<ConfigSource> getConfigSources() {
        return configSourceManager.getSources();
    }

    @Override
    public void registerConfigChangedListener(Consumer<Set<String>> consumer) {
        throw new UnsupportedOperationException("Not yet implemented.");
    }


    public <T> T getOrDefault(String key, Class<T> type, T defaultValue) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(type);

        Optional<T> val = getOptionalValue(key, type);
        return val.orElse(defaultValue);
    }

    @Override
    public ConfigContext getConfigContext() {
        return new ConfigContext() {
            @Override
            public List<ConfigSource> getConfigSources() {
                return configSourceManager.getSources();
            }

            @Override
            public List<Filter> getFilters() {
                return filterManager.getFilters();
            }

            @Override
            public Map<Type, List<Converter>> getConverters() {
                return converterManager.getConverters();
            }
        };
    }


    @Override
    public String toString() {
        String b = "Configuration{\n" +
                this.configSourceManager + '\n' +
                this.filterManager + '\n' +
                this.converterManager + '\n' +
                '}';
        return b;
    }

}
