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
package org.apache.tamaya;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Builder class to create new instances current {@link MetaInfo}. This class is not thread safe.
 */
public final class MetaInfoBuilder{
    /** Current meta info data map. */
    Map<String,String> map = new HashMap<>();

    /**
     * Creates a new builder using an existing meta info as default.
     * @param metaInfo the meta info, or null.
     */
    private MetaInfoBuilder(MetaInfo metaInfo){
        if(metaInfo!=null){
            this.map.putAll(metaInfo.toMap());
        }
    }

    /**
     * Creates a new builder, for a meta info with the given name.
     * @param name the name, not null.
     */
    private MetaInfoBuilder(String name){
        this.map.put(MetaInfo.NAME, Objects.requireNonNull(name));
    }

    /**
     * Creates a new builder, for a meta info with the given meta info.
     * @param metaInfo the meta info, or null.
     * @return the new builder instance.
     */
    public static MetaInfoBuilder of(MetaInfo metaInfo){
        return new MetaInfoBuilder(metaInfo);
    }

    /**
     * Creates a new builder, for a meta info with the given name.
     * @param name the name, not null.
     * @return the new builder instance.
     */
    public static MetaInfoBuilder of(String name){
        return new MetaInfoBuilder(name);
    }

    /**
     * Creates a new builder, for a meta info with name '<noname>'..
     * @return the new builder instance.
     */
    public static MetaInfoBuilder of(){
        return new MetaInfoBuilder("<noname>");
    }

    /**
     * Sets the name.
     * @param name the name, not null.
     * @return the builder for chaining.
     */
    public MetaInfoBuilder setName(String name){
        Objects.requireNonNull(name);
        map.put(MetaInfo.NAME, name);
        return this;
    }

    /**
     * Sets the type.
     * @param type the type.
     * @return  the builder for chaining.
     */
    public MetaInfoBuilder setType(String type){
        Objects.requireNonNull(type);
        map.put(MetaInfo.TYPE, type);
        return this;
    }

    /**
     * Sets the info property.
     * @param info the info.
     * @return the builder for chaining.
     */
    public MetaInfoBuilder setInfo(String info){
        Objects.requireNonNull(info);
        map.put(MetaInfo.INFO, info);
        return this;
    }

    /**
     * Sets the sources from which a config/property source is read.
     * @param sources the sources, not null.
     * @return the builder for chaining.
     */
    public MetaInfoBuilder setSources(String... sources){
        Objects.requireNonNull(sources);
        map.put(MetaInfo.SOURCE, Arrays.toString(sources));
        return this;
    }

    /**
     * Applies all properties from the given meta info instance.
     * @param metaInfo the other meta info, not null.
     * @return the builder for chaining.
     */
    public MetaInfoBuilder setMetaInfo(MetaInfo metaInfo){
        if(metaInfo!=null){
            Objects.requireNonNull(metaInfo);
            map.putAll(metaInfo.toMap());
        }
        return this;
    }

    /**
     * Sets the source expressions used, to evaluate the sources read.
     * @param sourceExpressions the expressions, not null.
     * @return the builder for chaining.
     */
    public MetaInfoBuilder setSourceExpressions(String... sourceExpressions){
        Objects.requireNonNull(sourceExpressions);
        map.put(MetaInfo.SOURCE_EXPRESSION, Arrays.toString(sourceExpressions));
        return this;
    }

    /**
     * Sets the timestamp of the metainfo.
     * @param timestamp the timestamp.
     * @return the builder for chaining.
     */
    public MetaInfoBuilder setTimestamp(long timestamp){
        map.put(MetaInfo.TIMESTAMP, String.valueOf(timestamp));
        return this;
    }

    /**
     * Sets any key/value for the meta info.
     * @param key the target key, not null.
     * @param value the value, not null.
     * @return the builder for chaining.
     */
    public MetaInfoBuilder set(String key, String value){
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        map.put(key, value);
        return this;
    }

    /**
     * Access a key present in the current meta info to be built.
     * @param key the key, not null.
     * @return the key's value, or null.
     */
    public String get(String key){
        Objects.requireNonNull(key);
        return map.get(key);
    }

    /**
     * Builds a new MetaInfo instance, based on the current data.
     * @return
     */
    public MetaInfo build(){
        return new MetaInfo(this);
    }

    @Override
    public String toString(){
        StringBuilder b = new StringBuilder("MetaInfoBuilder[");
        for(Map.Entry<String,String> en:map.entrySet()){
            b.append(MetaInfo.escape(en.getKey())).append('=').append(MetaInfo.escape(en.getValue())).append(", ");
        }
        if(!map.isEmpty()){
            b.setLength(b.length()-2);
        }
        b.append(']');
        return b.toString();
    }

}
