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

import java.util.*;

/**
 * Implementation for a {@link org.apache.tamaya.PropertySource} that is an aggregate current
 * multiple child instances, where all existing key/values in a replacementMap will
 * replace values in a main map, if present there.
 */
class ReplacingPropertySource implements PropertySource {

    private PropertySource mainMap;
    private Map<String,String> replacingMap;
    private String name;

    /**
     * Creates a mew instance, with aggregation polilcy
     * {@code AggregationPolicy.OVERRIDE}.
     *
     * @param mainMap      The main ConfigMap.
     * @param replacingMap The replacing ConfigMap.
     */
    public ReplacingPropertySource(String name, PropertySource mainMap, Map<String, String> replacingMap){
        this.replacingMap = Objects.requireNonNull(replacingMap);
        this.mainMap = Objects.requireNonNull(mainMap);
        this.name = Objects.requireNonNull(name);
    }

    @Override
    public Map<String,String> getProperties(){
        Map<String,String> result = new HashMap<>(replacingMap);
        mainMap.getProperties().entrySet().stream().filter(en -> !replacingMap.containsKey(en.getKey())).forEach(en ->
                result.put(en.getKey(), en.getValue()));
        return result;
    }

    @Override
    public String getName(){
        return this.name;
    }

    @Override
    public Optional<String> get(String key){
        String val = replacingMap.get(key);
        if(val == null){
            return mainMap.get(key);
        }
        return Optional.ofNullable(val);
    }

    @Override
    public String toString(){
        return super.toString() + "(mainMap=" + mainMap + ", replacingMap=" + replacingMap + ")";
    }
}
