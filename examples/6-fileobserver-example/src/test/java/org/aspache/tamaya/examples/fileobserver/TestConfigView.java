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
package org.aspache.tamaya.examples.fileobserver;

import org.apache.tamaya.ConfigException;
import org.apache.tamaya.ConfigOperator;
import org.apache.tamaya.ConfigQuery;
import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;
import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.spi.ConversionContext;
import org.apache.tamaya.spi.PropertyConverter;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by Anatole on 24.03.2015.
 */
public class TestConfigView implements ConfigOperator{

    private static final TestConfigView INSTANCE = new TestConfigView();

    private TestConfigView(){}

    public static ConfigOperator of(){
        return INSTANCE;
    }

    @Override
    public Configuration operate(Configuration config) {
        return new Configuration() {
            @Override
            public Map<String, String> getProperties() {
                return config.getProperties().entrySet().stream().filter(e -> e.getKey().startsWith("test")).collect(
                        Collectors.toMap(en -> en.getKey(), en -> en.getValue()));
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
            public String get(String key) {
                return getProperties().get(key);
            }

            @Override
            public String getOrDefault(String key, String defaultValue) {
                String val = get(key);
                if(val==null){
                    return defaultValue;
                }
                return val;
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
            public <T> T get(String key, Class<T> type) {
                return get(key, TypeLiteral.of(type));
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
                    ConversionContext ctx = new ConversionContext.Builder(ConfigurationProvider.getConfiguration(),
                            key, type).build();
                    for (PropertyConverter<T> converter : converters) {
                        try {
                            T t = converter.convert(value, ctx);
                            if (t != null) {
                                return t;
                            }
                        } catch (Exception e) {
                            Logger.getLogger(getClass().getName())
                                    .log(Level.FINEST, "PropertyConverter: " + converter + " failed to convert value: "
                                            + value, e);
                        }
                    }
                    throw new ConfigException("Unparseable config value for type: " + type.getRawType().getName() + ": " + key);
                }
                return null;
            }

            @Override
            public <T> T getOrDefault(String key, TypeLiteral<T> type, T defaultValue) {
                T val = get(key, type);
                if(val==null){
                    return defaultValue;
                }
                return val;
            }
        };
    }
}
