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
package org.apache.tamaya.core.internal;

import org.apache.tamaya.ConfigException;
import org.apache.tamaya.Configuration;
import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.spi.ConfigurationContext;
import org.apache.tamaya.spi.PropertyConverter;
import org.apache.tamaya.spi.PropertyFilter;
import org.apache.tamaya.spi.PropertySource;
import org.apache.tamaya.spi.PropertyValueCombinationPolicy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Implementation of the Configuration API. This class uses the current {@link ConfigurationContext} to evaluate the
 * chain of {@link org.apache.tamaya.spi.PropertySource} and {@link org.apache.tamaya.spi.PropertyFilter}
 * instance to evaluate the current Configuration.
 */
public class DefaultConfiguration implements Configuration {
    /**
     * The logger.
     */
    private static final Logger LOG = Logger.getLogger(DefaultConfiguration.class.getName());
    /**
     * The maximal number of filter cycles performed before aborting.
     */
    private static final int MAX_FILTER_LOOPS = 10;

    /**
     * The current {@link org.apache.tamaya.spi.ConfigurationContext} of the current instance.
     */
    private final ConfigurationContext configurationContext;

    /**
     * Constructor.
     * @param configurationContext The configuration Context to be used.
     */
    public DefaultConfiguration(ConfigurationContext configurationContext){
        this.configurationContext = Objects.requireNonNull(configurationContext);
    }

    /**
     * This method evaluates the given configuration key. Hereby if goes down the chain or PropertySource instances
     * provided by the current {@link org.apache.tamaya.spi.ConfigurationContext}. The first non-null-value returned
     * is taken as an intermediate value. Finally the value is filtered through the
     * {@link org.apache.tamaya.spi.PropertyFilter} instances installed, before it is returned as the final result of
     * this method.
     *
     * @param key the property's key, not null.
     * @return the optional configuration value, never null.
     */
    @Override
    public String get(String key) {
        List<PropertySource> propertySources = configurationContext.getPropertySources();
        String unfilteredValue = null;
        PropertyValueCombinationPolicy combinationPolicy = this.configurationContext
                .getPropertyValueCombinationPolicy();
        for (PropertySource propertySource : propertySources) {
                unfilteredValue = combinationPolicy.collect(unfilteredValue, key, propertySource);
        }
        return applyFilter(key, unfilteredValue);
    }

    /**
     * Apply filters to a single property value.
     *
     * @param key             the key, used for logging, not null.
     * @param unfilteredValue the unfiltered property value.
     * @return the filtered value, or null.
     */
    private String applyFilter(String key, String unfilteredValue) {
        // Apply filters to values, prevent values filtered to null!
        for (int i = 0; i < MAX_FILTER_LOOPS; i++) {
            boolean changed = false;
            // Apply filters to values, prevent values filtered to null!
            for (PropertyFilter filter : configurationContext.getPropertyFilters()) {
                String newValue = filter.filterProperty(key, unfilteredValue);
                if (newValue != null && !newValue.equals(unfilteredValue)) {
                    changed = true;
                    if (LOG.isLoggable(Level.FINEST)) {
                        LOG.finest("Filter - " + key + ": " + unfilteredValue + " -> " + newValue + " by " + filter);
                    }
                } else if (unfilteredValue != null && !unfilteredValue.equals(newValue)) {
                    changed = true;
                    if (LOG.isLoggable(Level.FINEST)) {
                        LOG.finest("Filter - " + key + ": " + unfilteredValue + " -> " + newValue + " by " + filter);
                    }
                }
                unfilteredValue = newValue;
            }
            if (!changed) {
                LOG.finest(() -> "Finishing filter loop, no changes detected.");
                break;
            } else {
                if (i == (MAX_FILTER_LOOPS - 1)) {
                    if (LOG.isLoggable(Level.WARNING)) {
                        LOG.warning("Maximal filter loop count reached, aborting filter evaluation after cycles: " + i);
                    }
                } else {
                    LOG.finest(() -> "Repeating filter loop, changes detected.");
                }
            }
        }
        return unfilteredValue;
    }

    /**
     * Get the current properties, composed by the loaded {@link org.apache.tamaya.spi.PropertySource} and filtered
     * by registered {@link org.apache.tamaya.spi.PropertyFilter}.
     *
     * @return the final properties.
     */
    @Override
    public Map<String, String> getProperties() {
        List<PropertySource> propertySources = new ArrayList<>(configurationContext.getPropertySources());
        Map<String, String> result = new HashMap<>();
        for (PropertySource propertySource : propertySources) {
            try {
                int origSize = result.size();
                Map<String, String> otherMap = propertySource.getProperties();
                LOG.log(Level.FINEST, null, () -> "Overriding with properties from " + propertySource.getName());
                result.putAll(otherMap);
                LOG.log(Level.FINEST, null, () -> "Handled properties from " + propertySource.getName() + "(new: " +
                        (result.size() - origSize) + ", overrides: " + origSize + ", total: " + result.size());
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "Error adding properties from PropertySource: " + propertySource + ", ignoring PropertySource.", e);
            }
        }
        return applyFilters(result);
    }

    /**
     * Filter a full configuration property map.
     *
     * @param inputMap the unfiltered map
     * @return the filtered map.
     */
    private Map<String, String> applyFilters(Map<String, String> inputMap) {
        // Apply filters to values, prevent values filtered to null!
        for (int i = 0; i < MAX_FILTER_LOOPS; i++) {
            AtomicInteger changes = new AtomicInteger();
            for (PropertyFilter filter : configurationContext.getPropertyFilters()) {
                inputMap.replaceAll((k, v) -> {
                    String newValue = filter.filterProperty(k, v);
                    if (newValue != null && !newValue.equals(v)) {
                        changes.incrementAndGet();
                        LOG.finest(() -> "Filter - " + k + ": " + v + " -> " + newValue + " by " + filter);
                    } else if (v != null && !v.equals(newValue)) {
                        changes.incrementAndGet();
                        LOG.finest(() -> "Filter - " + k + ": " + v + " -> " + newValue + " by " + filter);
                    }
                    return newValue;
                });
            }
            if (changes.get() == 0) {
                LOG.finest(() -> "Finishing filter loop, no changes detected.");
                break;
            } else {
                if (i == (MAX_FILTER_LOOPS - 1)) {
                    if (LOG.isLoggable(Level.WARNING)) {
                        LOG.warning("Maximal filter loop count reached, aborting filter evaluation after cycles: " + i);
                    }
                } else {
                    LOG.finest(() -> "Repeating filter loop, changes detected: " + changes.get());
                }
                changes.set(0);
            }
        }
        // Remove null values
        return inputMap.entrySet().parallelStream().filter((e) -> e.getValue() != null).collect(
                Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Accesses the current String value for the given key (see {@link #getOptional(String)}) and tries to convert it
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
        Optional<String> value = getOptional(key);
        if (value.isPresent()) {
            List<PropertyConverter<T>> converters = configurationContext.getPropertyConverters(type);
            for (PropertyConverter<T> converter : converters) {
                try {
                    T t = converter.convert(value.get());
                    if (t != null) {
                        return t;
                    }
                } catch (Exception e) {
                    LOG.log(Level.FINEST, e, () -> "PropertyConverter: " + converter +
                            " failed to convert value: " + value.get());
                }
            }

            throw new ConfigException("Unable to convert config value for key " +
                                      key + " in type " + type.getType());
        }
        return null;
    }

}
