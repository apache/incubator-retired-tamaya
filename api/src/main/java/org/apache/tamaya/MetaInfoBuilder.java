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
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Builder class to create new instances current {@lin MetaInfo}.
 */
public final class MetaInfoBuilder{

    Map<String,String> map = new ConcurrentHashMap<>();

    private MetaInfoBuilder(MetaInfo metaInfo){
        if(metaInfo!=null){
            this.map.putAll(metaInfo.toMap());
        }
    }

    private MetaInfoBuilder(String name){
        this.map.put(MetaInfo.NAME, Objects.requireNonNull(name));
    }

    public static MetaInfoBuilder of(MetaInfo metaInfo){
        return new MetaInfoBuilder(metaInfo);
    }

    public static MetaInfoBuilder of(String name){
        return new MetaInfoBuilder(name);
    }

    public static MetaInfoBuilder of(){
        return new MetaInfoBuilder("<noname>");
    }

    public MetaInfoBuilder setName(String name){
        Objects.requireNonNull(name);
        map.put(MetaInfo.NAME, name);
        return this;
    }

    public MetaInfoBuilder setType(String type){
        Objects.requireNonNull(type);
        map.put(MetaInfo.TYPE, type);
        return this;
    }

    public MetaInfoBuilder setInfo(String info){
        Objects.requireNonNull(info);
        map.put(MetaInfo.INFO, info);
        return this;
    }

    public MetaInfoBuilder setSources(String... sources){
        Objects.requireNonNull(sources);
        map.put(MetaInfo.SOURCE, Arrays.toString(sources));
        return this;
    }

    public MetaInfoBuilder setMetaInfo(MetaInfo metaInfo){
        if(metaInfo!=null){
            Objects.requireNonNull(metaInfo);
            map.putAll(metaInfo.toMap());
        }
        return this;
    }

    public MetaInfoBuilder setSourceExpressions(String... sourceExpressions){
        Objects.requireNonNull(sourceExpressions);
        map.put(MetaInfo.SOURCE_EXPRESSION, Arrays.toString(sourceExpressions));
        return this;
    }

    public MetaInfoBuilder setTimestamp(long timestamp){
        map.put(MetaInfo.TIMESTAMP, String.valueOf(timestamp));
        return this;
    }

    public MetaInfoBuilder setContext(String context){
        Objects.requireNonNull(context);
        map.put(MetaInfo.CONTEXT, context);
        return this;
    }

    public MetaInfoBuilder set(String key, String value){
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        map.put(key, value);
        return this;
    }

    public String get(String key){
        Objects.requireNonNull(key);
        return map.get(key);
    }

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
