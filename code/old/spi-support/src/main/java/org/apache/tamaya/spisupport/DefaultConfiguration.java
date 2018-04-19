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
package org.apache.tamaya.spisupport;

import org.apache.tamaya.ConfigException;
import org.apache.tamaya.ConfigOperator;
import org.apache.tamaya.ConfigQuery;
import org.apache.tamaya.Configuration;
import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.spi.ConfigurationContext;
import org.apache.tamaya.spi.ConversionContext;
import org.apache.tamaya.spi.PropertyConverter;
import org.apache.tamaya.spi.PropertySource;
import org.apache.tamaya.spi.PropertyValue;
import org.apache.tamaya.spi.PropertyValueCombinationPolicy;
import org.apache.tamaya.spi.ServiceContextManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of the Configuration API. This class uses the current {@link ConfigurationContext} to evaluate the
 * chain of {@link PropertySource} and {@link org.apache.tamaya.spi.PropertyFilter}
 * instances to evaluate the current Configuration.
 */
public class DefaultConfiguration implements Configuration {
    /**
     * The logger.
     */
    private static final Logger LOG = Logger.getLogger(DefaultConfiguration.class.getName());

    /**
     * The current {@link ConfigurationContext} of the current instance.
     */
    private final ConfigurationContext configurationContext;

    /**
     * EvaluationStrategy
     */
    private ConfigValueEvaluator configEvaluator = loadConfigValueEvaluator();

    private ConfigValueEvaluator loadConfigValueEvaluator() {
        ConfigValueEvaluator eval = null;
        try{
            eval = ServiceContextManager.getServiceContext()
                    .getService(ConfigValueEvaluator.class);
        }catch(Exception e){
            LOG.log(Level.WARNING, "Failed to load ConfigValueEvaluator from ServiceContext, using default.", e);
        }
        if(eval==null){
            eval = new DefaultConfigValueEvaluator();
        }
        return eval;
    }


    /**
     * Constructor.
     * @param configurationContext The configuration Context to be used.
     */
    public DefaultConfiguration(ConfigurationContext configurationContext){
        this.configurationContext = Objects.requireNonNull(configurationContext);
    }

    /**
     * Get a given value, filtered with the context's filters as needed.
     * @param key the property's key, not null.
     * @return the filtered value, or null.
     */
    @Override
    public String get(String key) {
        Objects.requireNonNull(key, "Key must not be null.");

        PropertyValue value = configEvaluator.evaluteRawValue(key, configurationContext);
        if(value==null || value.getValue()==null){
            return null;
        }
        value = PropertyFiltering.applyFilter(value, configurationContext);
        if(value!=null){
            return value.getValue();
        }
        return null;
    }

    /**
     * Evaluates the raw value using the context's {@link PropertyValueCombinationPolicy}.
     * @param key the key, not null.
     * @return the value, before filtering is applied.
     */
    protected PropertyValue evaluteRawValue(String key) {
        List<PropertySource> propertySources = configurationContext.getPropertySources();
        PropertyValue filteredValue = null;
        PropertyValueCombinationPolicy combinationPolicy = this.configurationContext
                .getPropertyValueCombinationPolicy();
        for (PropertySource propertySource : propertySources) {
            filteredValue = combinationPolicy.collect(filteredValue, key, propertySource);
        }
        return filteredValue;
    }


    @Override
    public String getOrDefault(String key, String defaultValue) {
        Objects.requireNonNull(key, "Key must not be null.");

        String val = get(key);
        if(val==null){
            return defaultValue;
        }
        return val;
    }

    @Override
    public <T> T getOrDefault(String key, Class<T> type, T defaultValue) {
        Objects.requireNonNull(key, "Key must not be null.");
        Objects.requireNonNull(type, "Target type must not be null");

        T val = get(key, type);
        if(val==null){
            return defaultValue;
        }
        return val;
    }

    /**
     * Get the current properties, composed by the loaded {@link PropertySource} and filtered
     * by registered {@link org.apache.tamaya.spi.PropertyFilter}.
     *
     * @return the final properties.
     */
    @Override
    public Map<String, String> getProperties() {
        Map<String, PropertyValue> filtered = PropertyFiltering.applyFilters(
                configEvaluator.evaluateRawValues(configurationContext),
                configurationContext);
        Map<String,String> result = new HashMap<>();
        for(PropertyValue val:filtered.values()){
            if(val.getValue()!=null) {
                result.put(val.getKey(), val.getValue());
                // TODO: Discuss metadata handling...
                result.putAll(val.getMetaEntries());
            }
        }
        return result;
    }


    /**
     * Accesses the current String value for the given key and tries to convert it
     * using the {@link PropertyConverter} instances provided by the current
     * {@link ConfigurationContext}.
     *
     * @param key  the property's absolute, or relative path, e.g. {@code
     *             a/b/c/d.myProperty}, never {@code null}.
     * @param type The target type required, not {@code null}.
     * @param <T>  the value type
     * @return the converted value, never {@code null}.
     */
    @Override
    public <T> T get(String key, Class<T> type) {
        return get(key, (TypeLiteral<T>)TypeLiteral.of(type));
    }

    /**
     * Accesses the current String value for the given key and tries to convert it
     * using the {@link PropertyConverter} instances provided by the current
     * {@link ConfigurationContext}.
     *
     * @param key  the property's absolute, or relative path, e.g. {@code
     *             a/b/c/d.myProperty}.
     * @param type The target type required, not {@code null}.
     * @param <T>  the value type
     * @return the converted value, never {@code null}.
     */
    @Override
    public <T> T get(String key, TypeLiteral<T> type) {
        Objects.requireNonNull(key, "Key must not be null.");
        Objects.requireNonNull(type, "Target type must not be null");

        return convertValue(key, get(key), type);
    }

    @SuppressWarnings("unchecked")
	protected <T> T convertValue(String key, String value, TypeLiteral<T> type) {
        if (value != null) {
            List<PropertyConverter<T>> converters = configurationContext.getPropertyConverters(type);
            ConversionContext context = new ConversionContext.Builder(this, this.configurationContext, key, type)
                    .build();
            for (PropertyConverter<T> converter : converters) {
                try {
                    T t = converter.convert(value, context);
                    if (t != null) {
                        return t;
                    }
                } catch (Exception e) {
                    LOG.log(Level.FINEST, "PropertyConverter: " + converter + " failed to convert value: " + value, e);
                }
            }
            // if the target type is a String, we can return the value, no conversion required.
            if(type.equals(TypeLiteral.of(String.class))){
                return (T)value;
            }
            // unsupported type, throw an exception
            throw new ConfigException("Unparseable config value for type: " + type.getRawType().getName() + ": " + key +
                    ", supported formats: " + context.getSupportedFormats());
        }
        return null;
    }

    @Override
    public <T> T getOrDefault(String key, TypeLiteral<T> type, T defaultValue) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(type);

        T val = get(key, type);
        if(val==null){
            return defaultValue;
        }
        return val;
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
        return this.configurationContext;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DefaultConfiguration that = (DefaultConfiguration) o;

        if (!configurationContext.equals(that.configurationContext)) return false;
        return configEvaluator.getClass().equals(that.configEvaluator.getClass());
    }

    @Override
    public int hashCode() {
        int result = configurationContext.hashCode();
        result = 31 * result + configEvaluator.getClass().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Configuration{\n " +
                configurationContext +
                '}';
    }
}
