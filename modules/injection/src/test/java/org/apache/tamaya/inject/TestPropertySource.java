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
package org.apache.tamaya.inject;

import org.apache.tamaya.spi.PropertySource;
import org.apache.tamaya.spi.PropertyValue;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Anatole on 12.01.2015.
 */
public class TestPropertySource implements PropertySource {

    private Map<String,String> properties = new HashMap<>();

    public TestPropertySource(){
        properties.put("env.stage", "ET");
        properties.put("simple_value", "aSimpleValue");
        properties.put("host.name", "tamaya01.incubator.apache.org");
        properties.put("anotherValue", "HALLO!");
        properties.put("NonAnnotatedConfigBean.classFieldKey", "Class-Field-Value");
        properties.put("NonAnnotatedConfigBean.fieldKey", "Field-Value");
        properties.put("annottext.NonAnnotatedConfigBean.fullKey", "Fullkey-Value");
    }

    @Override
    public int getOrdinal() {
        return 0;
    }

    @Override
    public String getName() {
        return getClass().getName();
    }

    @Override
    public PropertyValue get(String key) {
        return PropertyValue.of(key,properties.get(key),getName());
    }

    @Override
    public Map<String, String> getProperties() {
        return properties;
    }

    @Override
    public boolean isScannable() {
        return true;
    }
}
