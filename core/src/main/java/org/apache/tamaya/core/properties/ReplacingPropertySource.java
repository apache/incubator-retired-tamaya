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

import org.apache.tamaya.ConfigChangeSet;
import org.apache.tamaya.MetaInfo;
import org.apache.tamaya.MetaInfoBuilder;
import org.apache.tamaya.PropertySource;

import java.util.*;

/**
 * Implementation for a {@link org.apache.tamaya.PropertySource} that is an aggregate current
 * multiple child instances, where all existing key/values in a replacementMap will
 * replace values in a main map, if present there.
 */
class ReplacingPropertySource implements PropertySource {

    private static final long serialVersionUID = -1419376385695224799L;
    private PropertySource mainMap;
    private Map<String,String> replacingMap;
    private MetaInfo metaInfo;

    /**
     * Creates a mew instance, with aggregation polilcy
     * {@code AggregationPolicy.OVERRIDE}.
     *
     * @param mainMap      The main ConfigMap.
     * @param replacingMap The replacing ConfigMap.
     */
    public ReplacingPropertySource(MetaInfo metaInfo, PropertySource mainMap, Map<String, String> replacingMap){
        this.replacingMap = Objects.requireNonNull(replacingMap);
        this.mainMap = Objects.requireNonNull(mainMap);
        if(metaInfo==null) {
            this.metaInfo = MetaInfoBuilder.of().setType("replacing").set("mainProvider", mainMap.toString())
                    .set("replacing", replacingMap.toString()).build();
        }
        else{
            this.metaInfo = MetaInfoBuilder.of(metaInfo).setType("replacing").set("mainProvider", mainMap.toString())
                    .set("replacing", replacingMap.toString()).build();
        }
    }

    @Override
    public ConfigChangeSet load(){
        return mainMap.load();
    }

    @Override
    public boolean containsKey(String key){
        return mainMap.containsKey(key);
    }

    @Override
    public Map<String,String> toMap(){
        Map<String,String> result = new HashMap<>(replacingMap);
        mainMap.toMap().entrySet().stream().filter(en -> !replacingMap.containsKey(en.getKey())).forEach(en -> {
            result.put(en.getKey(), en.getValue());
        });
        return result;
    }

    @Override
    public MetaInfo getMetaInfo(){
        return this.metaInfo;
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
    public Set<String> keySet(){
        return mainMap.keySet();
    }

    /**
     * Apply a config change to this item. Hereby the change must be related to the same instance.
     * @param change the config change
     * @throws org.apache.tamaya.ConfigException if an unrelated change was passed.
     * @throws UnsupportedOperationException when the configuration is not writable.
     */
    @Override
    public void apply(ConfigChangeSet change){
        this.mainMap.apply(change);
    }

    @Override
    public String toString(){
        return super.toString() + "(mainMap=" + mainMap + ", replacingMap=" + replacingMap + ")";
    }
}
