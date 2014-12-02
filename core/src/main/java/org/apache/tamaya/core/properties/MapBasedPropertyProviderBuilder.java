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

import org.apache.tamaya.MetaInfo;
import org.apache.tamaya.MetaInfoBuilder;
import org.apache.tamaya.PropertyProvider;
import org.apache.tamaya.PropertyProviders;

import java.util.*;

/**
 * Builder class for creating a new instance current
 * {@link org.apache.tamaya.core.internal.MapBasedPropertyProvider}-
 */
public final class MapBasedPropertyProviderBuilder {
    protected MetaInfoBuilder metaInfoBuilder = MetaInfoBuilder.of();
    protected Map<String,String> entries = new HashMap<>();
    protected long configReadDT = System.currentTimeMillis();

    private MapBasedPropertyProviderBuilder(){
    }

    private MapBasedPropertyProviderBuilder(String name){
        Objects.requireNonNull(name);
        this.metaInfoBuilder.setName(name);
    }

    public static final MapBasedPropertyProviderBuilder of(){
        return new MapBasedPropertyProviderBuilder();
    }

    public static final MapBasedPropertyProviderBuilder of(String name){
        return new MapBasedPropertyProviderBuilder(name);
    }

    public MapBasedPropertyProviderBuilder setMetaInfo(String metaInfo){
        Objects.requireNonNull(metaInfo);
        this.metaInfoBuilder.setMetaInfo(metaInfo);
        return this;
    }

    public MapBasedPropertyProviderBuilder setMetaInfo(MetaInfo metaInfo){
        Objects.requireNonNull(metaInfo, "metaInfo required.");
        this.metaInfoBuilder.setMetaInfo(metaInfo);
        return this;
    }

    public MapBasedPropertyProviderBuilder addEntry(String key, String value){
        return addEntry(key, value, null);
    }

    public MapBasedPropertyProviderBuilder addEntry(String key, String value, String metaInfo){
        Objects.requireNonNull(key, "key required.");
        Objects.requireNonNull(value, "value required.");
        this.entries.put(key, value);
        if(metaInfo != null){
            this.metaInfoBuilder.setMetaInfo(key, metaInfo);
        }
        return this;
    }

    public MapBasedPropertyProviderBuilder addEntries(Map<String,String> data, String metaInfo){
        for(Map.Entry<String,String> en : data.entrySet()){
            this.entries.put(en.getKey(), en.getValue());
            if(metaInfo != null){
                this.metaInfoBuilder.setMetaInfo(en.getKey(), metaInfo);
            }
        }
        return this;
    }

    public MapBasedPropertyProviderBuilder addEntries(Map<String,String> data){
        Objects.requireNonNull(data, "data required.");
        addEntries(data, null);
        return this;
    }

    public MapBasedPropertyProviderBuilder addEntries(Properties data){
        return addEntries(data, null);
    }

    public MapBasedPropertyProviderBuilder addEntries(Properties data, String metaInfo){
        Objects.requireNonNull(data, "data required.");
        for(Map.Entry<Object,Object> en : data.entrySet()){
            this.entries.put(en.getKey().toString(), en.getValue().toString());
            if(metaInfo != null){
                this.metaInfoBuilder.setMetaInfo(en.getKey().toString(), metaInfo);
            }
        }
        return this;
    }

    public PropertyProvider build(){
        MetaInfo metaInfo = metaInfoBuilder.setTimestamp(configReadDT).build();
        return PropertyProviders.fromMap(metaInfo, entries);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString(){
        return "DefaultConfigurationUnit.Builder [configReadDT=" + configReadDT + ", entries=" + entries + "]";
    }

}
