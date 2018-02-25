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
package org.apache.tamaya;

import javax.annotation.Priority;

import org.apache.tamaya.spi.ConfigurationContext;
import org.apache.tamaya.spi.ConfigurationBuilder;
import org.apache.tamaya.spi.ConfigurationContextBuilder;
import org.apache.tamaya.spi.ConfigurationProviderSpi;
import org.mockito.Mockito;

/**
 * Test Configuration class, that is used to testdata the default methods
 * provided by the API.
 */
@Priority(-1)
public class TestConfigurationProvider implements ConfigurationProviderSpi {

    private Configuration config = new TestConfiguration();
    private ConfigurationContext context = Mockito.mock(ConfigurationContext.class);

    @Override
    public Configuration getConfiguration() {
        return config;
    }

    @Override
    public Configuration createConfiguration(ConfigurationContext context) {
        return config;
    }

    @Override
    public ConfigurationContext getConfigurationContext() {
        return context;
    }
    
    public ConfigurationContext getConfigurationContextFromInterface(){
        return ConfigurationProviderSpi.super.getConfigurationContext();
    }

    @Override
    public void setConfigurationContext(ConfigurationContext context) {
        this.context = context;
    }


    @Override
    public ConfigurationBuilder getConfigurationBuilder() {
        return Mockito.mock(ConfigurationBuilder.class, Mockito.RETURNS_DEEP_STUBS);
    }

    @Override
    public ConfigurationContextBuilder getConfigurationContextBuilder() {
        return Mockito.mock(ConfigurationContextBuilder.class);
    }

    @Override
    public void setConfiguration(Configuration config) {
        this.config = config;
    }
}
