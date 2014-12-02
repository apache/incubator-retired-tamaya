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
package org.apache.tamaya.core.config;

import org.apache.tamaya.AggregationPolicy;
import org.apache.tamaya.PropertyProviders;
import org.apache.tamaya.core.spi.ConfigurationProviderSpi;

import org.apache.tamaya.Configuration;

/**
 * Provides a {@link org.apache.tamaya.Configuration} named 'environment.properties'
 * containing the current environment properties.
 *
 * Created by Anatole on 29.09.2014.
 */
public class EnvPropertiesConfigProvider implements ConfigurationProviderSpi{

    private Configuration envConfig;

    public EnvPropertiesConfigProvider(){
        envConfig = ConfigurationBuilder.of("environment.properties").addConfigMaps(AggregationPolicy.OVERRIDE(),
                PropertyProviders.fromEnvironmentProperties()).build();
    }

    @Override
    public String getConfigName(){
        return "environment.properties";
    }

    @Override
    public Configuration getConfiguration(){
        return envConfig;
    }
}
