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
package org.apache.tamaya.core.testdata;

import org.apache.tamaya.spisupport.propertysource.BasePropertySource;
import org.apache.tamaya.spi.PropertyValue;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Test provider reading properties from classpath:cfg/defaults/**.properties.
 */
public class TestPropertyDefaultSource extends BasePropertySource{

    private Map<String,PropertyValue> properties = new HashMap<>();

    public TestPropertyDefaultSource() {
        super(100);
        properties.put("name",PropertyValue.of("name", "Anatole", "test"));
        properties.put("name2",PropertyValue.of("name2", "Sabine", "test"));
        properties = Collections.unmodifiableMap(properties);
    }

    @Override
    public String getName() {
        return "default-testdata-properties";
    }

    @Override
    public Map<String, PropertyValue> getProperties() {
        return properties;
    }

    @Override
    public boolean isScannable() {
        return true;
    }

}
