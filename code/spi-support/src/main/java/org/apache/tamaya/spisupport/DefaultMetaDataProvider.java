/*
 * Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.apache.tamaya.spisupport;

import org.apache.tamaya.spi.ConfigurationContext;
import org.apache.tamaya.spi.PropertySource;
import org.apache.tamaya.spi.PropertyValue;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Default metadata provider implementation, which searches for all kind of entries
 * starting with {@code [META]}. All matching key/values are added to the
 * meta data map, hereby reoving the key prefix.
 */
public class DefaultMetaDataProvider implements MetadataProvider {

    private static final String META_PREFIX = "[META]";
    private ConfigurationContext context;
    private Map<String, String> additionalProperties = new ConcurrentHashMap<>();
    private AtomicLong lastHash = new AtomicLong();
    private Map<String, String> propertyCache = new HashMap<>();

    @Override
    public MetadataProvider init(ConfigurationContext context) {
        this.context = Objects.requireNonNull(context);
        return this;
    }

    @Override
    public Map<String, String> getMetaData() {
        long configHash = Objects.hash(context.getPropertySources().toArray());
        if(configHash!=lastHash.get()){
            lastHash.set(configHash);
            propertyCache = loadMetaProperties();
        }
        return propertyCache;
    }

    private Map<String, String> loadMetaProperties() {
        Map<String, String> result = new HashMap<>();
        for(PropertySource ps:context.getPropertySources()){
            ps.getProperties().values().forEach(v -> {
                loadMetaData(v, result);
            });
        }
        result.putAll(additionalProperties);
        return Collections.unmodifiableMap(result);
    }

    /**
     * Iterates all values and it's children and adds all meta-entries found.
     * @param value the starting value.
     * @param result the result map to add/override values found.
     */
    private void loadMetaData(PropertyValue value, Map<String, String> result) {
        String key = value.getQualifiedKey();
        if(key.toUpperCase(Locale.ENGLISH).startsWith(META_PREFIX)){
            if(value.getValue()!=null){
                result.put(key.substring(META_PREFIX.length()), value.getValue());
            }
            value.iterator().forEachRemaining(v -> loadMetaData(v, result));
        }
    }

    @Override
    public MetadataProvider setMeta(String key, String value) {
        additionalProperties.put(key, value);
        return this;
    }

    @Override
    public MetadataProvider setMeta(Map<String, String> metaData) {
        additionalProperties.putAll(metaData);
        return this;
    }

    @Override
    public MetadataProvider reset() {
        additionalProperties.clear();
        return this;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", this.getClass().getSimpleName() + "[", "]")
                .add("additionalProperties = " + additionalProperties)
                .add("context = " + context)
                .toString();
    }
}
