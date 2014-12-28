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
package org.apache.tamaya.core.properties;

import org.apache.tamaya.PropertySource;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

/**
 * Property source which filters any key/values dynamically.
 */
class ValueFilteredPropertySource implements PropertySource{

    private String name;
    private BiFunction<String, String, String> valueFilter;
    private PropertySource source;

    public ValueFilteredPropertySource(String name, BiFunction<String, String, String> valueFilter, PropertySource current) {
        this.name = Optional.ofNullable(name).orElse("<valueFiltered> -> name="+current.getName()+", valueFilter="+valueFilter.toString());
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Optional<String> get(String key) {
        String value = this.source.get(key).orElse(null);
        value = valueFilter.apply(key, value);
        return Optional.ofNullable(value);
    }

    @Override
    public Map<String, String> getProperties() {
        Map<String, String> map = new HashMap<>(source.getProperties());
        map.replaceAll(valueFilter);
        return map;
    }

    @Override
    public String toString() {
        return "ValueFilteredPropertySource{" +
                "source=" + source.getName() +
                ", name='" + name + '\'' +
                ", valueFilter=" + valueFilter +
                '}';
    }
}
