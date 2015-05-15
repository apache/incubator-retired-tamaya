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
import org.apache.tamaya.spi.ConfigurationContextBuilder;
import org.apache.tamaya.spi.ConfigurationProviderSpi;
import org.apache.tamaya.spi.ServiceContext;
import org.apache.tamaya.spi.ServiceContextManager;

import java.util.Optional;

/**
 * Implementation of the Configuration API. This class uses the current {@link org.apache.tamaya.spi.ConfigurationContext} to evaluate the
 * chain of {@link org.apache.tamaya.spi.PropertySource} and {@link org.apache.tamaya.spi.PropertyFilter}
 * instance to evaluate the current Configuration.
 */
public class DefaultConfigurationProvider implements ConfigurationProviderSpi {

    private ConfigurationContext context = new DefaultConfigurationContext();
    private Configuration config = new DefaultConfiguration(context);

    @Override
    public Configuration getConfiguration() {
        return config;
    }

    @Override
    public ConfigurationContext getConfigurationContext() {
        return context;
    }

    @Override
    public ConfigurationContextBuilder getConfigurationContextBuilder() {
        ServiceContext serviceContext = ServiceContextManager.getServiceContext();
        Optional<ConfigurationContextBuilder> service = serviceContext.getService(ConfigurationContextBuilder.class);

        return service.get();
    }

    @Override
    public void setConfigurationContext(ConfigurationContext context){
        Configuration oldConfig = this.config;
        Configuration newConfig = new DefaultConfiguration(context);
        this.config = newConfig;
        this.context = context;
    }
}
