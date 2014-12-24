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

import java.io.Serializable;
import java.time.Instant;
import java.util.*;

import org.apache.tamaya.ConfigChangeSet;
import org.apache.tamaya.MetaInfo;
import org.apache.tamaya.MetaInfoBuilder;
import org.apache.tamaya.PropertySource;

/**
 * This class models a freezed instance current an {@link org.apache.tamaya.PropertySource}.
 * Created by Anatole on 28.03.14.
 */
final class FreezedPropertySource implements PropertySource, Serializable{

    private static final long serialVersionUID = 3365413090311267088L;
//    private Map<String,Map<String,String>> fieldMMetaInfo = new HashMap<>();
    private String name;
    private Map<String,String> properties = new HashMap<>();

    private FreezedPropertySource(String name, PropertySource propertyMap) {
        Map<String, String> map = propertyMap.getProperties();
        this.properties.putAll(map);
        this.properties = Collections.unmodifiableMap(this.properties);
        this.name = Optional.ofNullable(name).orElse(propertyMap.getName()+"(freezed)");
    }

    public static PropertySource of(String name, PropertySource propertyProvider){
        if(propertyProvider instanceof FreezedPropertySource){
            return propertyProvider;
        }
        return new FreezedPropertySource(name, propertyProvider);
    }

    @Override
    public ConfigChangeSet load(){
        return ConfigChangeSet.emptyChangeSet(this);
    }

    public int size(){
        return properties.size();
    }

    public boolean isEmpty(){
        return properties.isEmpty();
    }

    @Override
    public Map<String,String> getProperties(){
        return Collections.unmodifiableMap(this.properties);
    }

    @Override
    public String getName(){
        return this.name;
    }

    @Override
    public Optional<String> get(String key){
        return Optional.ofNullable(properties.get(key));
    }


}
