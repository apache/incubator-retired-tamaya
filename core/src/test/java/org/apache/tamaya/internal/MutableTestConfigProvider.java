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
package org.apache.tamaya.internal;

import org.apache.tamaya.Configuration;
import org.apache.tamaya.MetaInfoBuilder;
import org.apache.tamaya.PropertyProvider;
import org.apache.tamaya.PropertyProviders;
import org.apache.tamaya.core.config.Configurations;
import org.apache.tamaya.core.spi.ConfigurationProviderSpi;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Anatole on 29.09.2014.
 */
public class MutableTestConfigProvider implements ConfigurationProviderSpi{

    private static final String CONFIG_NAME = "mutableTestConfig";
    private Configuration testConfig;
    private final Map<String, String> dataMap = new ConcurrentHashMap<>();

    public MutableTestConfigProvider(){
        dataMap.put("dad", "Anatole");
        dataMap.put("mom", "Sabine");
        dataMap.put("sons.1", "Robin");
        dataMap.put("sons.2", "Luke");
        dataMap.put("sons.3", "Benjamin");
        PropertyProvider provider = PropertyProviders.fromMap(MetaInfoBuilder.of().setName(CONFIG_NAME).build(),
                dataMap);
        testConfig = Configurations.getConfiguration(provider);
    }

    @Override
    public String getConfigName(){
        return CONFIG_NAME;
    }

    @Override
    public Configuration getConfiguration(){
        return testConfig;
    }
}
