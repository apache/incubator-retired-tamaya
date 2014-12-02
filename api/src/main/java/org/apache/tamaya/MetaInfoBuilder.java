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

    public static final String METAINFO = "_metainfo";
    public static final String TIMESTAMP = "timestamp";
    public static final String CONTEXT = "context";
    public static final String NAME = "name";
    public static final String INFO = "info";
    public static final String TYPE = "type";
    public static final String SOURCE = "source";
    public static final String ENVIRONMENT = "environment";
    public static final String SOURCE_EXPRESSION = "source-expression";

    Map<String,String> map = new ConcurrentHashMap<>();

    private MetaInfoBuilder(MetaInfo metaInfo){
        if(metaInfo!=null){
            this.map.putAll(metaInfo.toMap());
        }
    }

    public static MetaInfoBuilder of(MetaInfo metaInfo){
        return new MetaInfoBuilder(metaInfo);
    }

    public static MetaInfoBuilder of(String name){
        return new MetaInfoBuilder(null).setName(name);
    }

    public static MetaInfoBuilder of(){
        return new MetaInfoBuilder(null).setName("<noname>");
    }

    public MetaInfoBuilder withName(String name){
        Objects.requireNonNull(name);
        map.put(NAME, name);
        return this;
    }

    public MetaInfoBuilder setName(String name){
        Objects.requireNonNull(name);
        map.put(NAME, name);
        return this;
    }

    public MetaInfoBuilder setType(String type){
        Objects.requireNonNull(type);
        map.put(TYPE, type);
        return this;
    }

    public MetaInfoBuilder setInfo(String info){
        Objects.requireNonNull(info);
        map.put(INFO, info);
        return this;
    }

    public MetaInfoBuilder setSources(String... sources){
        Objects.requireNonNull(sources);
        map.put(SOURCE, Arrays.toString(sources));
        return this;
    }

    public MetaInfoBuilder setMetaInfo(String key, String metaInfo){
        Objects.requireNonNull(metaInfo);
        Objects.requireNonNull(key);
        map.put(key + '.' + METAINFO, metaInfo);
        return this;
    }

    public MetaInfoBuilder setMetaInfo(String metaInfo){
        if(metaInfo!=null){
            Objects.requireNonNull(metaInfo);
            map.put(METAINFO, metaInfo);
        }
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
        map.put(SOURCE_EXPRESSION, Arrays.toString(sourceExpressions));
        return this;
    }

    public MetaInfoBuilder setTimestamp(long timestamp){
        map.put(TIMESTAMP, String.valueOf(timestamp));
        return this;
    }

    public MetaInfoBuilder setContext(String context){
        Objects.requireNonNull(context);
        map.put(CONTEXT, context);
        return this;
    }

    public MetaInfoBuilder setEnvironment(Environment configurationContext){
        Objects.requireNonNull(configurationContext);
        map.put(ENVIRONMENT, configurationContext.toString());
        return this;
    }

    public MetaInfoBuilder set(String key, String value){
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        map.put(key, value);
        return this;
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
