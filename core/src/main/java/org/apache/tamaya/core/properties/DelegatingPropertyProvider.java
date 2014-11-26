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

import java.util.*;

/**
 * Implementation for a {@link org.apache.tamaya.PropertyProvider} that is an aggregate of
 * multiple child instances. Controlled by an {@link org.apache.tamaya.core.properties.AggregationPolicy} the
 * following aggregations are supported:
 * <ul>
 * <li><b>IGNORE: </b>Ignore all overrides.</li>
 * <li><b>: </b></li>
 * <li><b>: </b></li>
 * <li><b>: </b></li>
 * </ul>
 */
class DelegatingPropertyProvider implements PropertyProvider{

    private static final long serialVersionUID = -1419376385695224799L;
    private PropertyProvider mainMap;
    private Map<String,String> parentMap;
    private MetaInfo metaInfo;

    /**
     * Creates a mew instance, with aggregation polilcy
     * {@code AggregationPolicy.OVERRIDE}.
     *
     * @param mainMap   The main ConfigMap.
     * @param parentMap The delegated parent ConfigMap.
     */
    public DelegatingPropertyProvider(PropertyProvider mainMap, Map<String,String> parentMap){
        this.metaInfo =
                MetaInfoBuilder.of().setType("delegate").set("provider", mainMap.toString()).set("delegate", parentMap.toString())
                        .build();
        Objects.requireNonNull(parentMap);
        this.parentMap = parentMap;
        Objects.requireNonNull(parentMap);
        this.parentMap = parentMap;
    }

    @Override
    public void load(){
        mainMap.load();
    }

    @Override
    public boolean containsKey(String key){
        return keySet().contains(key);
    }

    @Override
    public Map<String,String> toMap(){
        return null;
    }

    @Override
    public MetaInfo getMetaInfo(){
        return this.metaInfo;
    }

    @Override
    public Optional<String> get(String key){
        Optional<String> val = mainMap.get(key);
        if(!val.isPresent()){
            return Optional.ofNullable(parentMap.get(key));
        }
        return val;
    }

    @Override
    public Set<String> keySet(){
        Set<String> keys = new HashSet<>(mainMap.keySet());
        keys.addAll(parentMap.keySet());
        return keys;
    }

    @Override
    public String toString(){
        return super.toString() + "(mainMap=" + mainMap + ", delegate=" + parentMap + ")";
    }
}
