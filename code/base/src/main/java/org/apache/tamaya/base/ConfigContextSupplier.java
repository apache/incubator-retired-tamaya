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

import org.apache.tamaya.base.convert.ConverterManager;
import org.apache.tamaya.base.filter.Filter;

import javax.config.Config;
import javax.config.spi.ConfigSource;
import javax.config.spi.Converter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Central SPI for programmatically dealing with the setup of the configuration system.
 * This includes adding and enlisting {@link ConfigSource}s,
 * managing {@link Converter}s, ConfigFilters, etc.
 */
@FunctionalInterface
public interface ConfigContextSupplier {

    /**
     * Get a context supplier from the given {@link Config}. If the {@link Config} implements
     * {@link ConfigContextSupplier} it is cast and the result will be returned. If the
     * config does not implement {@link ConfigContextSupplier}, a default context is created,
     * which includes all convereters as defined by {@link ConverterManager#defaultInstance()#getConverters()},
     * an empty filter list and the {@link ConfigSource}s as declared by the given {@link Config}
     * instance.
     * @param config the config instance, not null.
     * @return a context supplier instance, never null.
     */
    static ConfigContextSupplier of(Config config){
        if(config instanceof ConfigContextSupplier){
            return (ConfigContextSupplier)config;
        }
        return () -> new ConfigContext() {
            @Override
            public List<ConfigSource> getConfigSources() {
                List<ConfigSource> configSources = new ArrayList<>();
                for(ConfigSource cs:config.getConfigSources()){
                    configSources.add(cs);
                }
                return configSources;
            }

            @Override
            public List<Filter> getFilters() {
                return Collections.emptyList();
            }

            @Override
            public Map<Type, List<Converter>> getConverters() {
                return ConverterManager.defaultInstance().getConverters();
            }

            @Override
            public String toString() {
                return "ConfigContext#default{\n  delegate:"+config+"\n}";
            }
        };
    }

    /**
     * Make an instance of a configuration accessible for use with Apache Tamaya specific extensions.
     * In most cases it should be sufficient to implement this interfance on your implementation of
     * {@link javax.config.Config}.
     *
     * @return the corresponding configuration context, never null.
     */
    ConfigContext getConfigContext();

}

