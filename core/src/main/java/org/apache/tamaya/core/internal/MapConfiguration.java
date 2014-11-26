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

import org.apache.tamaya.Configuration;
import org.apache.tamaya.MetaInfo;
import org.apache.tamaya.PropertyAdapters;
import org.apache.tamaya.PropertyProvider;

import java.beans.PropertyChangeListener;
import java.util.*;

/**
 * Configuration based on a simple Map.
 */
class MapConfiguration implements Configuration, PropertyProvider {

    private Map<String,String> data = new HashMap<>();
    private MetaInfo metaInfo;
    private String version = UUID.randomUUID().toString();

    public MapConfiguration(MetaInfo metaInfo, Map<String,String> data){
        Objects.requireNonNull(metaInfo);
        Objects.requireNonNull(data);
        this.metaInfo = metaInfo;
        this.data.putAll(data);
        this.data = Collections.unmodifiableMap(this.data);
    }

    @Override
    public String getVersion() {
        return version;
    }

//    @Override
//    public <T> T getOrDefault(String key, Class<T> type, T defaultValue){
//        String value = get(key);
//        if(value==null){
//            return defaultValue;
//        }
//        return PropertyAdapters.getAdapter(type).adapt(get(key));
//    }

    @Override
    public <T> Optional<T> get(String key, Class<T> type){
        Optional<String> value = get(key);
        if(value.isPresent()){
            return Optional.ofNullable(PropertyAdapters.getAdapter(type).adapt(value.get()));
        }
        return Optional.empty();
//        throw new ConfigException("No such config value: " + key + " in " +  getMetaInfo());
    }

    @Override
    public Set<String> getAreas(){
        final Set<String> areas = new HashSet<>();
        this.keySet().forEach(s -> {
            int index = s.lastIndexOf('.');
            if(index > 0){
                areas.add(s.substring(0, index));
            }
            else{
                areas.add("<root>");
            }
        });
        return areas;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener l){
        // TODO
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener l){
        // TODO
    }

    @Override
    public boolean containsKey(String key){
        return data.containsKey(key);
    }

    @Override
    public Map<String,String> toMap(){
        return Collections.unmodifiableMap(data);
    }

    @Override
    public MetaInfo getMetaInfo(){
        return this.metaInfo;
    }

    @Override
    public Optional<String> get(String key){
        return Optional.ofNullable(data.get(key));
    }

    @Override
    public Set<String> keySet(){
        return data.keySet();
    }


}
