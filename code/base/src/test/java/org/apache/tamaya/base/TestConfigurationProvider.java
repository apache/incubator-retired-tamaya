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

import org.apache.tamaya.spi.Filter;
import org.apache.tamaya.spi.ServiceContext;
import org.apache.tamaya.base.DefaultConfigBuilder;

import javax.config.Config;
import javax.config.spi.ConfigBuilder;
import javax.config.spi.ConfigProviderResolver;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of the Configuration API. This class uses the current {@link Config} to evaluate the
 * chain of {@link javax.config.spi.ConfigSource} and {@link Filter}
 * instance to evaluate the current Configuration.
 */
public class TestConfigurationProvider extends ConfigProviderResolver {

    private static final Map<ClassLoader,Config> configurations = new ConcurrentHashMap<>();

    @Override
    public Config getConfig() {
        return getConfig(ServiceContext.defaultClassLoader());
    }

    @Override
    public Config getConfig(ClassLoader classLoader){
        Config config = configurations.get(classLoader);
        if(config==null){
            synchronized (configurations) {
                config = configurations.get(classLoader);
                if(config==null) {
                    config = new DefaultConfigBuilder()
                            .addDiscoveredConverters()
                            .addDiscoveredFilters()
                            .addDefaultSources()
                            .addDiscoveredSources()
                            .build();
                    configurations.put(classLoader, config);
                }
            }
        }
        return config;
    }

    @Override
    public ConfigBuilder getBuilder() {
        return new DefaultConfigBuilder();
    }

    @Override
    public void registerConfig(Config config, ClassLoader classLoader) {
        Objects.requireNonNull(config);
        this.configurations.put(classLoader, Objects.requireNonNull(config));
    }

    @Override
    public void releaseConfig(Config config) {
        configurations.forEach((k,v) -> {if(v.equals(config)){configurations.remove(k);}});
    }

}
