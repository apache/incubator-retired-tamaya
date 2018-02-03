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
package org.apache.tamaya.core;

import org.apache.tamaya.base.DefaultConfigBuilder;
import org.apache.tamaya.spi.*;
import org.osgi.service.component.annotations.Component;

import javax.config.Config;
import javax.config.spi.ConfigBuilder;
import javax.config.spi.ConfigProviderResolver;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of the Configuration API. This class uses the current {@link ConfigContext} to evaluate the
 * chain of {@link javax.config.spi.ConfigSource} and {@link Filter}
 * instance to evaluate the current Configuration.
 */
@Component(service = ConfigProviderResolver.class)
public class TamayaConfigProviderResolver extends ConfigProviderResolver {

    private static final Logger LOG = Logger.getLogger(TamayaConfigProviderResolver.class.getName());

    private Map<ClassLoader, Config> configs = new ConcurrentHashMap<>();

    @Override
    public Config getConfig() {
        return getConfig(Thread.currentThread().getContextClassLoader());
    }

    @Override
    public Config getConfig(ClassLoader loader) {
        Config config = this.configs.get(loader);
        if(config==null){
            ConfigFactory factory = ServiceContextManager.getServiceContext().getService(ConfigFactory.class);
            if(factory!=null){
                try {
                    config = factory.createConfig(loader);
                }catch(Exception e){
                    LOG.log(Level.SEVERE, "Config factory threw exception: " + factory, e);
                }
            }
            if(config==null) {
                LOG.finest(() -> "Creating default config for classloader: " + loader);
                config = new DefaultConfigBuilder()
                        .addDiscoveredFilters()
                        .addDiscoveredConverters()
                        .addDefaultSources()
                        .addDiscoveredSources()
                        .build();
            }
            registerConfig(config, loader);
        }
        return config;
    }

    @Override
    public ConfigBuilder getBuilder() {
        return new DefaultConfigBuilder();
    }

    @Override
    public void registerConfig(Config config, ClassLoader classLoader) {
        if(classLoader==null){
            classLoader = ServiceContext.defaultClassLoader();
        }
        if(configs.containsKey(classLoader)){
            LOG.warning("Replacing existing config for classloader: " + classLoader);
        }
        LOG.info( "Registering config for classloader: " + classLoader + ": " + config);
        this.configs.put(classLoader, config);
    }

    @Override
    public void releaseConfig(Config config) {
        for(Map.Entry<ClassLoader, Config> en: this.configs.entrySet()){
            if(en.getValue().equals(config)){
                LOG.info( "Releasing config for classloader: " + en.getKey());
                this.configs.remove(en.getKey());
                return;
            }
        }
    }

    /**
     * Registering an implementation allows to define the way the default configurations are created if accessed the
     * first time.
     */
    @FunctionalInterface
    public interface ConfigFactory{

        /**
         * Create a new configuration to be used for the given classloader.
         * @param classLoader the classloader, not null.
         * @return the new config to be used, or null.
         */
        Config createConfig(ClassLoader classLoader);
    }

}
