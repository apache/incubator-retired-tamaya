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
import java.util.logging.Logger;

/**
 * Default metadata provider implementation, which searches for all kind of entries
 * formatted as {@code [(META)key].metaKey=metaValue}. All matching key/values are added to the
 * meta data mapProperties for the given key as {@code metaKey=metaValue} meta entries.
 */
public class DefaultMetaDataProvider implements MetadataProvider {

    private static final Logger LOG = Logger.getLogger(DefaultMetaDataProvider.class.getName());
    private static final String META_PREFIX = "[(META)";
    private ConfigurationContext context;
    private Map<String, Map<String, String>> additionalProperties = new ConcurrentHashMap<>();
    private AtomicLong lastHash = new AtomicLong();
    private Map<String, Map<String, String>> propertyCache = new HashMap<>();

    @Override
    public MetadataProvider init(ConfigurationContext context) {
        this.context = Objects.requireNonNull(context);
        return this;
    }

    @Override
    public Map<String, String> getMetaData(String property) {
        long configHash = Objects.hash(context.getPropertySources().toArray());
        if(configHash!=lastHash.get()){
            lastHash.set(configHash);
            propertyCache = loadMetaProperties();
        }
        Map<String, String> meta = propertyCache.get(property);
        if(meta==null){
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(meta);
    }

    private Map<String, Map<String, String> > loadMetaProperties() {
        Map<String, Map<String, String> > result = new HashMap<>();
        for(PropertySource ps:context.getPropertySources()){
            ps.getProperties().values().forEach(v -> {
                Map<String, String> meta = result.computeIfAbsent(v.getKey(), k -> new HashMap<>());
                meta.putAll(v.getMeta());
                if(v.getQualifiedKey().toUpperCase(Locale.ENGLISH).startsWith(META_PREFIX)){
                    loadExplicitMetadata(v);
                }
            });
        }
        // Override with manual properties
        for(Map.Entry<String,Map<String, String>> en: additionalProperties.entrySet()) {
            Map<String, String> meta = result.get(en.getKey());
            if(meta==null){
                result.put(en.getKey(), new HashMap<>(en.getValue()));
            }else {
                meta.putAll(en.getValue());
            }
        }
        return Collections.unmodifiableMap(result);
    }

    /**
     * Iterates all values and it's children and adds all meta-entries found in the configuration
     * (entries starting with {@code [(META)}).
     */
    private void loadExplicitMetadata(PropertyValue value) {
        String key = value.getQualifiedKey();
        if(value.getValue()!=null){
            String[] keyValue = getMetaKeys(key);
            if(keyValue==null){
                LOG.warning("Encountered invalid META-ENTRY: " + key);
            }else {
                Map<String, String> meta = additionalProperties.computeIfAbsent(keyValue[0], k -> new HashMap<>());
                meta.put(keyValue[1], value.getValue());
            }
        }
    }

    private String[] getMetaKeys(String fullKey) {
        String strippedKey = fullKey.substring(META_PREFIX.length());
        int index = strippedKey.lastIndexOf(']');
        if(index<0){
            // invalid meta key
            return null;
        }
        String[] result = new String[2];
        result[0] = strippedKey.substring(0,index);
        result[1] = strippedKey.substring(index+1);
        if(result[1].startsWith(".")){
            result[1] = result[1].substring(1);
        }
        return result;
    }

    @Override
    public MetadataProvider setMeta(String property, String key, String value) {
        Objects.requireNonNull(property, "property null");
        Objects.requireNonNull(key, "key null");
        Objects.requireNonNull(value, "value null");
        additionalProperties.computeIfAbsent(property, p -> new HashMap<>())
            .put(key, value);
        return this;
    }

    @Override
    public MetadataProvider setMeta(String property, Map<String, String> metaData) {
        Objects.requireNonNull(property, "property null");
        Objects.requireNonNull(metaData, "metaData null");
        additionalProperties.computeIfAbsent(property, p -> new HashMap<>())
                .putAll(metaData);
        return this;
    }

    @Override
    public MetadataProvider reset(String property) {
        Objects.requireNonNull(property, "property null");
        additionalProperties.remove(property);
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
