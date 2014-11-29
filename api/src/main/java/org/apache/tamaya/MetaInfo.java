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
    /** The key used for storing the data owner. */
    private static final String OWNER_KEY = "_owner";
    /** The meta information data. */
    private final Map<String, String> metaInfo = new HashMap<>();

    /**
     * Constructor, used from the builder.
     * @param builder the builder, not null.
     */
    MetaInfo(MetaInfoBuilder builder){
        Objects.requireNonNull(builder);
        this.metaInfo.putAll(builder.map);
    }

    /**
     * Returns a new instance of this class, adding only a general information property.
     * @param info the info property, not null.
     * @return a new instance of this class, never null.
     */
    public static MetaInfo of(String info){
        return MetaInfoBuilder.of(info).build();
    }

    /**
     * Access a meta data property.
     * @param key the property key, not null.
     * @return the corresponding property value, or null.
     */
    public String get(String key){
        return this.metaInfo.get(key);

    }

    /**
     * Get the information about the data owner.
     * @return the data owner info, or null.
     */
    public String getOwnerInfo(){
        return this.metaInfo.get(OWNER_KEY);
    }

    /**
     * @see java.util.Map#keySet()
     * @return the key set.
     */
    public Set<String> keySet(){
        return this.metaInfo.keySet();
    }

    /**
     * Access the meta info as map.
     * @return the corresponding map, never null.
     */
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

    /**
     * Helper method to escape "=\\[]".
     * @param val the input value, not null.
     * @return the escaped String, not null.
     */
    static String escape(String val){
        return val.replaceAll("=", "\\\\=").replaceAll("\\[", "\\\\[").replaceAll("]", "\\\\]").replaceAll("\\,", "\\\\,");
    }

}
