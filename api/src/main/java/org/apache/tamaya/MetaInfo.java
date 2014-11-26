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

import java.util.*;

/**
 * Simple class to represent configuration meta information. Metainformation can be related to the holw
 * configuration or some if its entries.
 */
public final class MetaInfo{

    private final Map<String, String> metaInfo = new HashMap<>();

    MetaInfo(MetaInfoBuilder builder){
        Objects.requireNonNull(builder);
        this.metaInfo.putAll(builder.map);
    }

    public static MetaInfo of(String info){
        return MetaInfoBuilder.of(info).build();
    }

    public String get(String key){
        return this.metaInfo.get(key);

    }
    public Set<String> keySet(){
        return this.metaInfo.keySet();
    }

    public Map<? extends String,? extends String> toMap(){
        return Collections.unmodifiableMap(this.metaInfo);
    }

    @Override
    public String toString(){
        StringBuilder b = new StringBuilder("MetaInfo[");
        for(Map.Entry<String,String> en:metaInfo.entrySet()){
            b.append(escape(en.getKey())).append('=').append(escape(en.getValue())).append(", ");
        }
        if(!metaInfo.isEmpty()){
            b.setLength(b.length()-2);
        }
        b.append(']');
        return b.toString();
    }

    static String escape(String val){
        return val.replaceAll("=", "\\\\=").replaceAll("\\[", "\\\\[").replaceAll("]", "\\\\]").replaceAll("\\,", "\\\\,");
    }

}
