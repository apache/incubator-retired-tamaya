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
package org.apache.tamaya.spisupport;

import java.util.HashMap;
import java.util.Map;
import org.apache.tamaya.spi.PropertySource;
import org.apache.tamaya.spi.PropertyValue;

/**
 *
 * @author William.Lieurance 2018.02.17
 */
public class MockedPropertySource implements PropertySource {

    private String name = "MockedPropertySource";
    private int ordinal = 10;

    public MockedPropertySource() {
        this("MockedPropertySource", 10);
    }

    public MockedPropertySource(String id, int ordinal) {
        this.name = id;
        this.ordinal = ordinal;
    }

    @Override
    public int getOrdinal() {
        return ordinal;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public PropertyValue get(String key) {
        if (key.contains("Null")) {
            return PropertyValue.of(key, null, "MockedPropertySource");
        } else if (key.contains("missing")){
            return null;
        }
        return PropertyValue.of(key, "valueFromMockedPropertySource", "MockedPropertySource");
    }

    @Override
    public Map<String, PropertyValue> getProperties() {
        Map<String, PropertyValue> returnable = new HashMap<>();
        returnable.put("shouldBeNull", get("shouldBeNull"));
        returnable.put("Filterednull", get("shouldBeFiltered"));
        returnable.put("someKey", get("someKey"));

        return returnable;
    }

    @Override
    public boolean isScannable() {
        return true;
    }

}
