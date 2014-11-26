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
package org.apache.tamaya.core.config;

import org.apache.tamaya.core.spi.ConfigurationFormat;

import org.apache.tamaya.Environment;
import java.net.URI;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public final class MetaInfoEditor{

    public static final String METAINFO = "_metainfo";
    public static final String TIMESTAMP = "timestamp";
    public static final String CONTEXT = "context";
    public static final String NAME = "name";
    public static final String SOURCE = "source";
    public static final String FORMAT = "format";
    public static final String ENVIRONMENT = "environment";
    public static final String SOURCE_EXPRESSION = "source-expression";

    private Map<String,String> map = new ConcurrentHashMap<>();

    private MetaInfoEditor(String metaInfo){
        // TODO parse
    }

    public static MetaInfoEditor of(String metaInfo){
        return new MetaInfoEditor(metaInfo);
    }

    public static MetaInfoEditor of(){
        return new MetaInfoEditor(null);
    }

    public MetaInfoEditor withName(String name){
        Objects.requireNonNull(name);
        map.put(NAME, name);
        return this;
    }

    public MetaInfoEditor withSource(String source){
        Objects.requireNonNull(source);
        map.put(SOURCE, source);
        return this;
    }

    public static Map<String, String> setMetaInfo(Map<String, String> targetMap, String key, String metaInfo){
        Objects.requireNonNull(metaInfo);
        Objects.requireNonNull(key);
        Objects.requireNonNull(targetMap);
        targetMap.put(key + '.' + METAINFO, metaInfo);
        return targetMap;
    }

    public static Map<String, String> setMetaInfo(Map<String, String> targetMap, String metaInfo){
        if(metaInfo!=null){
            Objects.requireNonNull(metaInfo);
            Objects.requireNonNull(targetMap);
            targetMap.put(METAINFO, metaInfo);
        }
        return targetMap;
    }

    public MetaInfoEditor withSource(URI source){
        Objects.requireNonNull(source);
        map.put(SOURCE, source.toString());
        return this;
    }

    public MetaInfoEditor withSourceExpression(String sourceExpression){
        Objects.requireNonNull(sourceExpression);
        map.put(SOURCE_EXPRESSION, sourceExpression);
        return this;
    }

    public MetaInfoEditor withTimestamp(long timestamp){
        map.put(TIMESTAMP, String.valueOf(timestamp));
        return this;
    }

    public MetaInfoEditor withContext(String context){
        Objects.requireNonNull(context);
        map.put(CONTEXT, context);
        return this;
    }

    public MetaInfoEditor withFormat(ConfigurationFormat format){
        Objects.requireNonNull(format);
        map.put(FORMAT, format.getClass().getName());
        return this;
    }

    public MetaInfoEditor withEnvironment(Environment configurationContext){
        Objects.requireNonNull(configurationContext);
        map.put(ENVIRONMENT, configurationContext.toString());
        return this;
    }

    public String build(){
        StringBuilder r = new StringBuilder("METADATA[");
        for(Map.Entry<String,String> en:map.entrySet()){
            r.append(escape(en.getKey())).append('=').append(escape(en.getValue())).append(", ");
        }
        if(!map.isEmpty()){
            r.setLength(r.length()-2);
        }
        r.append(']');
        return r.toString();
    }

    private String escape(String val){
        return val.replaceAll("=", "\\\\=").replaceAll("[", "\\\\[").replaceAll("]", "\\\\]").replaceAll(",", "\\\\,");
    }

}
