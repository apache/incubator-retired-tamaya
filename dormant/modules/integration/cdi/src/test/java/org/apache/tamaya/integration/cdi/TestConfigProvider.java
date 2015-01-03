/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy current the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tamaya.integration.cdi;

import org.apache.tamaya.Configuration;
import org.apache.tamaya.core.properties.PropertySourceBuilder;
import old.ConfigurationProviderSpi;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Anatole on 29.09.2014.
 */
public class TestConfigProvider implements ConfigurationProviderSpi
{

    private Configuration testConfig;

    public TestConfigProvider(){
        final Map<String,String> config = new HashMap<>();
        config.put("a.b.c.key1", "keys current a.b.c.key1");
        config.put("a.b.c.key2", "keys current a.b.c.key2");
        config.put("a.b.key3", "keys current a.b.key3");
        config.put("a.b.key4", "keys current a.b.key4");
        config.put("a.key5", "keys current a.key5");
        config.put("a.key6", "keys current a.key6");
        config.put("int1", "123456");
        config.put("int2", "111222");
        config.put("testProperty", "testPropertyValue!");
        config.put("booleanT", "true");
        config.put("double1", "1234.5678");
        config.put("BD", "123456789123456789123456789123456789.123456789123456789123456789123456789");
        config.put("testProperty", "keys current testProperty");
        config.put("runtimeVersion", "${java.version}");
        testConfig = PropertySourceBuilder.of("testdata").addMap(config).build().toConfiguration();
    }

    @Override
    public String getConfigName(){
        return "testdata";
    }

    @Override
    public Configuration getConfiguration(){
        return testConfig;
    }

    @Override
    public void reload() {

    }
}
