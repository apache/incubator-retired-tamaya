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
import org.apache.tamaya.ConfigurationSnapshot;
import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.spi.ConfigurationContext;
import org.apache.tamaya.spi.ConversionContext;
import org.apache.tamaya.spi.PropertyConverter;
import org.apache.tamaya.spi.PropertyValue;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of the Configuration API. This class uses the current {@link ConfigurationContext} to evaluate the
 * chain of {@link org.apache.tamaya.spi.PropertySource} and {@link org.apache.tamaya.spi.PropertyFilter}
 * instances to evaluate the current Configuration.
 */
public class DefaultConfiguration implements Configuration, Serializable {
    /**
     * The logger.
     */
    private static final Logger LOG = Logger.getLogger(DefaultConfiguration.class.getName());

    /**
     * The current {@link ConfigurationContext} of the current instance.
     */
    private ConfigurationContext configurationContext;

    /**
     * EvaluationStrategy
     */
    private transient ConfigValueEvaluator configEvaluator;


    private ConfigValueEvaluator loadConfigValueEvaluator() {
        ConfigValueEvaluator eval = null;
        try{
            eval = configurationContext.getServiceContext()
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
        this.configEvaluator = loadConfigValueEvaluator();
    }

    /**
     * Get a given createValue, filtered with the context's filters as needed.
     * @param key the property's key, not null.
     * @return the filtered createValue, or null.
     */
    @Override
    public String get(String key) {
        Objects.requireNonNull(key, "Key must not be null.");

        PropertyValue value = configEvaluator.evaluateRawValue(key, configurationContext);
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
     * Get a given createValue, filtered with the context's filters as needed.
     * @param key the property's key, not null.
     * @return the filtered createValue, or null.
     */
    public List<PropertyValue> getValues(String key) {
        Objects.requireNonNull(key, "Key must not be null.");

        List<PropertyValue> value = configEvaluator.evaluateAllValues(key, configurationContext);
        if(value==null || value.isEmpty()){
            return Collections.emptyList();
        }
        value = PropertyFiltering.applyFilters(value, configurationContext);
        if(value!=null){
            return value;
        }
        return null;
    }


    /**
     * Evaluates the raw value.
     * @param key the key, not null.
     * @return the createValue, before filtering is applied.
     * @deprecated Use {@link ConfigValueEvaluator#evaluateRawValue(String, ConfigurationContext)}.
     */
    @Deprecated
    protected PropertyValue evaluteRawValue(String key) {
        return configEvaluator.evaluateRawValue(key, configurationContext);
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
     * Get the current properties, composed by the loaded {@link org.apache.tamaya.spi.PropertySource} and filtered
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
            }
        }
        return result;
    }


    /**
     * Accesses the current String createValue for the given key and tries to convert it
     * using the {@link PropertyConverter} instances provided by the current
     * {@link ConfigurationContext}.
     *
     * @param key  the property's absolute, or relative path, e.g. {@code
     *             a/b/c/d.myProperty}, never {@code null}.
     * @param type The target type required, not {@code null}.
     * @param <T>  the createValue type
     * @return the converted createValue, never {@code null}.
     */
    @Override
    public <T> T get(String key, Class<T> type) {
        return get(key, (TypeLiteral<T>)TypeLiteral.of(type));
    }

    /**
     * Accesses the current String createValue for the given key and tries to convert it
     * using the {@link PropertyConverter} instances provided by the current
     * {@link ConfigurationContext}.
     *
     * @param key  the property's absolute, or relative path, e.g. {@code
     *             a/b/c/d.myProperty}.
     * @param type The target type required, not {@code null}.
     * @param <T>  the createValue type
     * @return the converted createValue, never {@code null}.
     */
    @Override
    public <T> T get(String key, TypeLiteral<T> type) {
        Objects.requireNonNull(key, "Key must not be null.");
        Objects.requireNonNull(type, "Target type must not be null");

        return convertValue(key, getValues(key), type);
    }

    @SuppressWarnings("unchecked")
    protected <T> T convertValue(String key, List<PropertyValue> values, TypeLiteral<T> type) {
        if (values != null && !values.isEmpty()) {
            List<PropertyConverter<T>> converters = configurationContext.getPropertyConverters(type);
            ConversionContext context = new ConversionContext.Builder(this, key, type)
                    .setValues(values)
                    .build();
            String value = values.get(0).getValue();
            for (PropertyConverter<T> converter : converters) {
                try {
                    T t = converter.convert(value, context);
                    if (t != null) {
                        return t;
                    }
                } catch (Exception e) {
                    LOG.log(Level.FINEST, "PropertyConverter: " + converter + " failed to convert createValue: " + value, e);
                }
            }
            // if the target type is a String, we can return the createValue, no conversion required.
            if (type.equals(TypeLiteral.of(String.class))) {
                return (T) value;
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
    public ConfigurationSnapshot getSnapshot(Iterable<String> keys) {
        return new DefaultConfigurationSnapshot(this, keys);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DefaultConfiguration that = (DefaultConfiguration) o;

        if (!configurationContext.equals(that.configurationContext)) {
            return false;
        }
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

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        configurationContext = (ConfigurationContext)ois.readObject();
        configEvaluator = loadConfigValueEvaluator();
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        if(configurationContext instanceof Serializable){
            oos.writeObject(configurationContext);
        }else{
            oos.writeObject(new DefaultConfigurationContext(
                    this.configurationContext.getServiceContext(),
                    this.configurationContext.getPropertyFilters(),
                    this.configurationContext.getPropertySources(),
                    this.configurationContext.getPropertyConverters(),
                    this.configurationContext.getServiceContext().getService(MetadataProvider.class,
                            () -> new DefaultMetaDataProvider())));
        }
    }
}
