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
package org.apache.tamaya.core.internal;

import org.apache.tamaya.ConfigChangeSet;
import org.apache.tamaya.MetaInfo;
import org.apache.tamaya.MetaInfoBuilder;
import org.apache.tamaya.PropertyProvider;

import java.io.Serializable;
import java.time.Instant;
import java.util.*;

/**
 * This class models a freezed instance of an {@link org.apache.tamaya.PropertyProvider}.
 * Created by Anatole on 28.03.14.
 */
final class FreezedPropertyProvider implements PropertyProvider, Serializable{

    private static final long serialVersionUID = 3365413090311267088L;
    private Map<String,Map<String,String>> fieldMMetaInfo = new HashMap<>();
    private MetaInfo metaInfo;
    private Map<String,String> properties = new HashMap<>();

    private FreezedPropertyProvider(MetaInfo metaInfo, PropertyProvider propertyMap) {
        Map<String, String> map = propertyMap.toMap();
        this.properties.putAll(map);
        this.properties = Collections.unmodifiableMap(this.properties);
        if (metaInfo == null) {
            this.metaInfo =
                    MetaInfoBuilder.of(propertyMap.getMetaInfo()).set("freezedAt", Instant.now().toString()).build();
        } else {
            this.metaInfo = metaInfo;
        }
    }

    public static PropertyProvider of(MetaInfo metaInfo, PropertyProvider propertyProvider){
        if(propertyProvider instanceof FreezedPropertyProvider){
            return propertyProvider;
        }
        return new FreezedPropertyProvider(metaInfo, propertyProvider);
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
    public boolean containsKey(String key){
        return properties.containsKey(key);
    }

    @Override
    public Map<String,String> toMap(){
        return Collections.unmodifiableMap(this.properties);
    }

    @Override
    public MetaInfo getMetaInfo(){
        return this.metaInfo;
    }

    @Override
    public Optional<String> get(String key){
        return Optional.ofNullable(properties.get(key));
    }

    @Override
    public Set<String> keySet(){
        return properties.keySet();
    }

}
