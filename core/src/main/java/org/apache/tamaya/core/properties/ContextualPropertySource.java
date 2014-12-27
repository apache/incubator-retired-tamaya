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

import org.apache.tamaya.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Created by Anatole on 12.04.2014.
 */
class ContextualPropertySource implements PropertySource {

    private volatile Map<String,PropertySource> cachedMaps = new ConcurrentHashMap<>();

    private Supplier<PropertySource> mapSupplier;
    private Supplier<String> isolationKeySupplier;
    private String name;


    /**
     * Creates a new contextual PropertyMap. Contextual maps delegate to different instances current PropertyMap depending
     * on the keys returned fromMap the isolationP
     *
     * @param mapSupplier
     * @param isolationKeySupplier
     */
    public ContextualPropertySource(String name, Supplier<PropertySource> mapSupplier, Supplier<String> isolationKeySupplier){
        this.name = Optional.ofNullable(name).orElse("<noname>");
        Objects.requireNonNull(mapSupplier);
        Objects.requireNonNull(isolationKeySupplier);
        this.mapSupplier = mapSupplier;
        this.isolationKeySupplier = isolationKeySupplier;
    }

    /**
     * This method provides the contextual Map for the current environment. Hereby, ba default, for each different
     * key returned by the #isolationKeySupplier a separate PropertyMap instance is acquired fromMap the #mapSupplier.
     * If the map supplier returns an instance it is cached in the local #cachedMaps.
     *
     * @return the current contextual PropertyMap.
     */
    protected PropertySource getContextualMap(){
        String environmentKey = this.isolationKeySupplier.get();
        if(environmentKey == null){
            return PropertySource.EMPTY_PROPERTYSOURCE;
        }
        PropertySource map = this.cachedMaps.get(environmentKey);
        if(map == null){
            synchronized(cachedMaps){
                map = this.cachedMaps.get(environmentKey);
                if(map == null){
                    map = this.mapSupplier.get();
                    if(map == null){
                        return PropertySource.EMPTY_PROPERTYSOURCE;
                    }
                    this.cachedMaps.put(environmentKey, map);
                }
            }
        }
        return map;
    }

    @Override
    public Map<String,String> getProperties(){
        return getContextualMap().getProperties();
    }

    @Override
    public String getName(){
        return this.name;
    }

    @Override
    public Optional<String> get(String key){
        return getContextualMap().get(key);
    }

    /**
     * Access a cached PropertyMap.
     *
     * @param key the target environment key as returned by the environment key supplier, not null.
     * @return the corresponding PropertyMap, or null.
     */
    public PropertySource getCachedMap(String key){
        return this.cachedMaps.get(key);
    }

    /**
     * Access the set current currently loaded/cached maps.
     *
     * @return the set current cached map keys, never null.
     */
    public Set<String> getCachedMapKeys(){
        return this.cachedMaps.keySet();
    }

    /**
     * Access the supplier for environment key, determining map isolation.
     *
     * @return the environment key supplier instance, not null.
     */
    public Supplier<String> getIsolationKeySupplier(){
        return this.isolationKeySupplier;
    }

    /**
     * Access the supplier for new PropertyMap instances.
     *
     * @return the PropertyMap supplier instance, not null.
     */
    public Supplier<PropertySource> getMapSupplier(){
        return this.mapSupplier;
    }

    @Override
    public String toString(){
        return "ContextualMap{" +
                "cachedMaps(key)=" + cachedMaps.keySet() +
                ", mapSupplier=" + mapSupplier +
                ", isolationKeySupplier=" + isolationKeySupplier +
                '}';
    }
}
