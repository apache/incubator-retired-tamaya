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
package org.apache.tamaya.types.internal.adapters;

import org.apache.tamaya.ConfigException;
import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;
import org.apache.tamaya.types.TypeLiteral;
import org.apache.tamaya.types.TypedConfiguration;
import org.apache.tamaya.types.internal.PropertyConverterManager;
import org.apache.tamaya.types.spi.ConversionContext;
import org.apache.tamaya.types.spi.PropertyConverter;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of the {@link TypedConfiguration} API.
 */
public class DefaultTypedConfiguration implements TypedConfiguration {
    /**
     * The logger.
     */
    private static final Logger LOG = Logger.getLogger(DefaultTypedConfiguration.class.getName());

    private Configuration configuration;

    private PropertyConverterManager propertyConverterManager = new PropertyConverterManager();

    /**
     * Constructor.
     * @param configuration The configuration to be decorated.
     */
    public DefaultTypedConfiguration(Configuration configuration){
        this.configuration = Objects.requireNonNull(configuration);
    }

    private Configuration getConfig(){
        return this.configuration!=null?this.configuration:ConfigurationProvider.getConfiguration();
    }

    @Override
    public String get(String key) {
        return getConfig().get(key);
    }

    @Override
    public Map<String, String> getProperties() {
        return getConfig().getProperties();
    }

    /**
     * Accesses the current String value for the given key and tries to convert it
     * using the {@link org.apache.tamaya.types.spi.PropertyConverter} instances provided by
     * the current context.
     *
     * @param key  the property's absolute, or relative path, e.g. @code
     *             a/b/c/d.myProperty}.
     * @param type The target type required, not null.
     * @param <T>  the value type
     * @return the converted value, never null.
     */
    @Override
    public <T> T get(String key, Class<T> type) {
        return get(key, (TypeLiteral<T>)TypeLiteral.of(type));
    }

    /**
     * Accesses the current String value for the given key and tries to convert it
     * using the {@link org.apache.tamaya.types.spi.PropertyConverter} instances provided by the current
     * context.
     *
     * @param key  the property's absolute, or relative path, e.g. @code
     *             a/b/c/d.myProperty}.
     * @param type The target type required, not null.
     * @param <T>  the value type
     * @return the converted value, never null.
     */
    @Override
    public <T> T get(String key, TypeLiteral<T> type) {
        return convertValue(key, get(key), type);
    }

    @Override
    public <T> void addPropertyConverter(TypeLiteral<T> typeToConvert, PropertyConverter<T> propertyConverter) {
        this.propertyConverterManager.register(typeToConvert, propertyConverter);
    }

    @Override
    public Map<TypeLiteral<?>, List<PropertyConverter<?>>> getPropertyConverters() {
        return this.propertyConverterManager.getPropertyConverters();
    }

    @Override
    public <T> List<PropertyConverter<T>> getPropertyConverters(TypeLiteral<T> type) {
        return this.propertyConverterManager.getPropertyConverters(type);
    }

    @Override
    public void removePropertyConverters(TypeLiteral<?> typeToConvert, PropertyConverter<?>... converters) {
        removePropertyConverters(typeToConvert, Arrays.asList(converters));
    }

    @Override
    public void removePropertyConverters(TypeLiteral<?> typeToConvert, Collection<PropertyConverter<?>> converters) {
        List<? extends PropertyConverter<?>> existing = this.propertyConverterManager.getPropertyConverters(typeToConvert);
        if(existing!=null) {
            existing.removeAll(converters);
        }
    }


    protected <T> T convertValue(String key, String value, TypeLiteral<T> type) {
        if (value != null) {
            List<PropertyConverter<T>> converters = propertyConverterManager.getPropertyConverters(type);
            ConversionContext context = new ConversionContext.Builder(this, key, type).build();
            for (PropertyConverter<T> converter : converters) {
                try {
                    T t = converter.convert(value, context);
                    if (t != null) {
                        return t;
                    }
                } catch (Exception e) {
                    LOG.log(Level.INFO, "PropertyConverter: " + converter + " failed to convert value: " + value, e);
                }
            }
            throw new ConfigException("Unparseable config value for type: " + type.getRawType().getName() + ": " + key +
                    ", supported formats: " + context.getSupportedFormats());
        }
        return null;
    }

}
