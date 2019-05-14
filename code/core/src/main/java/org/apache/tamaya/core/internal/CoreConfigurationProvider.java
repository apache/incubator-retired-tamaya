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

import org.apache.tamaya.Configuration;
import org.apache.tamaya.spi.ConfigurationContext;
import org.apache.tamaya.spi.ConfigurationBuilder;
import org.apache.tamaya.spi.ConfigurationProviderSpi;
import org.osgi.service.component.annotations.Component;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Implementation of the Configuration API. This class uses the current {@link org.apache.tamaya.spi.ConfigurationContext} to evaluate the
 * chain of {@link org.apache.tamaya.spi.PropertySource} and {@link org.apache.tamaya.spi.PropertyFilter}
 * instance to evaluate the current Configuration.
 */
@Component(service = ConfigurationProviderSpi.class)
public class CoreConfigurationProvider implements ConfigurationProviderSpi {

    private static final Logger LOG = Logger.getLogger(CoreConfigurationProvider.class.getName());

    private final Map<ClassLoader, Configuration> configurations = new ConcurrentHashMap<>();

    public CoreConfigurationProvider(){
        Configuration defaultConfig = new CoreConfigurationBuilder()
                .setClassLoader(getClass().getClassLoader())
            .addDefaultPropertyConverters()
            .addDefaultPropertyFilters()
            .addDefaultPropertySources()
            .build();
        configurations.put(getClass().getClassLoader(), defaultConfig);
        String bannerConfig = defaultConfig.getOrDefault("tamaya.banner", "OFF");
        BannerManager bm = new BannerManager(bannerConfig);
        bm.outputBanner();
    }



    @Override
    public Configuration getConfiguration(ClassLoader classLoader) {
        return configurations.computeIfAbsent(classLoader, cl -> new CoreConfigurationBuilder()
                    .setClassLoader(classLoader)
                    .addDefaultPropertyConverters()
                    .addDefaultPropertyFilters()
                    .addDefaultPropertySources()
                    .build()
        );
    }

    @Override
    public Configuration createConfiguration(ConfigurationContext context) {
        return new CoreConfiguration(context);
    }

    @Override
    public ConfigurationBuilder getConfigurationBuilder() {
        return new CoreConfigurationBuilder();
    }

    @Override
    public void setConfiguration(Configuration config, ClassLoader classLoader) {
        Objects.requireNonNull(config.getContext());
        Configuration old = this.configurations.put(classLoader, Objects.requireNonNull(config));
        if(old != null){
            LOG.warning(String.format("Replaced config %s with %s for classloader %s", old, config, classLoader));
        }
    }

    @Override
    public Configuration releaseConfiguration(ClassLoader classloader) {
        LOG.info("Releasing config for classloader: " + classloader);
        return this.configurations.remove(classloader);
    }

    @Override
    public boolean isConfigurationSettable(ClassLoader classLoader) {
        return true;
    }

}
