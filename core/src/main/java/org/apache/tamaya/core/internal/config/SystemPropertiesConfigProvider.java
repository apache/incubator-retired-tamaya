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
package org.apache.tamaya.core.internal.config;

import org.apache.tamaya.core.properties.PropertySourceBuilder;
import org.apache.tamaya.core.spi.ConfigurationProviderSpi;

import org.apache.tamaya.Configuration;

/**
 * Provides a {@link org.apache.tamaya.Configuration} named 'system.properties'
 * containing the current system properties.
 *
 * Created by Anatole on 29.09.2014.
 */
public class SystemPropertiesConfigProvider implements ConfigurationProviderSpi{

    private Configuration systemConfig;

    public SystemPropertiesConfigProvider(){
        systemConfig = PropertySourceBuilder.of("system.properties").addSystemProperties().build().toConfiguration();
    }

    @Override
    public String getConfigName(){
        return "system.properties";
    }

    @Override
    public Configuration getConfiguration(){
        return systemConfig;
    }

    @Override
    public void reload() {
        // nothing todo here
    }
}
