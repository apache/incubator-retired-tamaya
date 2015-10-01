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
 * PropertySource, that has values added or overridden.
 */
class EnrichedPropertySource implements PropertySource{

    private PropertySource basePropertySource;

    private Map<String,String> addedProperties;

    private boolean overriding;

    /**
     * Constructor.
     * @param propertySource
     * @param properties
     * @param overriding
     */
    EnrichedPropertySource(PropertySource propertySource, Map<String,String> properties, boolean overriding){
        this.basePropertySource = Objects.requireNonNull(propertySource);
        this.addedProperties = Objects.requireNonNull(properties);
        this.overriding = overriding;
    }


    @Override
    public int getOrdinal() {
        return basePropertySource.getOrdinal();
    }

    @Override
    public String getName() {
        return basePropertySource.getName();
    }

    @Override
    public String get(String key) {
        if(overriding){
            String val = addedProperties.get(key);
            if(val!=null){
                return val;
            }
            return basePropertySource.get(key);
        }
        String val = basePropertySource.get(key);
        if(val!=null){
            return val;
        }
        return addedProperties.get(key);

    }

    @Override
    public Map<String, String> getProperties() {
        Map<String, String> allProps;
        if(overriding) {
            allProps = new HashMap<>(basePropertySource.getProperties());
            allProps.putAll(addedProperties);
        }
        else{
            allProps = new HashMap<>(addedProperties);
            allProps.putAll(basePropertySource.getProperties());
        }
        return allProps;
    }

    @Override
    public boolean isScannable() {
        return basePropertySource.isScannable();
    }
}
