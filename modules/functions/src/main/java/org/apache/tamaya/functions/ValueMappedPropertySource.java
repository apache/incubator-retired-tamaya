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
package org.apache.tamaya.functions;

import org.apache.tamaya.spi.PropertySource;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


/**
 * Property source which filters any key/values dynamically.
 */
class ValueMappedPropertySource implements PropertySource{

    private final String name;
    private final PropertyMapper valueFilter;
    private final PropertySource source;

    public ValueMappedPropertySource(String name, PropertyMapper valueFilter, PropertySource current) {
        this.name =  name!=null?name:"<valueFiltered> -> name="+current.getName()+", valueFilter="+valueFilter.toString();
        this.valueFilter = valueFilter;
        this.source = Objects.requireNonNull(current);
    }

    @Override
    public int getOrdinal() {
        return source.getOrdinal();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String get(String key) {
        String value = this.source.get(key);
        if(value!=null) {
            return valueFilter.mapProperty(key, value);
        }
        return null;
    }

    @Override
    public Map<String, String> getProperties() {
        Map<String, String> map = new HashMap<>();
        for(Map.Entry<String,String> entry:source.getProperties().entrySet()) {
            map.put(entry.getKey(), valueFilter.mapProperty(entry.getKey(), entry.getValue()));
        }
        return map;
    }

    @Override
    public boolean isScannable() {
        return source.isScannable();
    }

    @Override
    public String toString() {
        return "ValueMappedPropertySource{" +
                "source=" + source.getName() +
                ", name='" + name + '\'' +
                ", valueFilter=" + valueFilter +
                '}';
    }
}
