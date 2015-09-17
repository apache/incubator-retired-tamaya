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
 *
 */
package org.apache.tamaya.integration.cdi.cfg;

import org.apache.tamaya.spi.PropertySource;

import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Anatole on 17.09.2015.
 */
@Singleton
public class TestPropertySource implements PropertySource{

    final Map<String,String> config = new HashMap<>();

    public TestPropertySource(){
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
        config.put("{meta}source.type:"+getClass().getName(), "PropertySource");
    }

    @Override
    public int getOrdinal() {
        return 10;
    }

    @Override
    public String getName() {
        return getClass().getName();
    }

    @Override
    public String get(String key) {
        return config.get(key);
    }

    @Override
    public Map<String, String> getProperties() {
        return config;
    }

    @Override
    public boolean isScannable() {
        return true;
    }
}
