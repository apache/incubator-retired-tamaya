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
package org.apache.tamaya.functions;

import org.apache.tamaya.spi.PropertySource;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Configuration that filters part of the entries defined by a filter predicate.
 */
class MetaEnrichedPropertySource implements PropertySource {

    private final PropertySource basePropertySource;
    private final Map<String, String> metaInfo;

    MetaEnrichedPropertySource(PropertySource basePropertySource, Map<String, String> metaInfo) {
        this.basePropertySource = Objects.requireNonNull(basePropertySource);
        this.metaInfo = Objects.requireNonNull(metaInfo);
    }

    // [meta:origin]a.b.c
    @Override
    public String get(String key) {
        if(key.startsWith("[meta:")){
            key = key.substring(6);
            int index = key.indexOf(']');
            String metaKey = key.substring(0,index);
            String entryKey = key.substring(index+1);
            String value =  basePropertySource.get(entryKey);
            if(value!=null) {
                return metaInfo.get(metaKey);
            }
        }
        return basePropertySource.get(key);
    }

    @Override
    public String getName() {
        return basePropertySource.getName();
    }

    @Override
    public Map<String, String> getProperties() {
        Map<String, String> baseProperties = basePropertySource.getProperties();
        Map<String, String> allProperties = new HashMap<>(baseProperties);
        for(Map.Entry<String,String> en: baseProperties.entrySet()) {
            for (Map.Entry<String, String> miEn : metaInfo.entrySet()) {
                allProperties.put("[meta:" + miEn.getKey() + ']' + en.getKey(), miEn.getValue());
            }
        }
        return allProperties;
    }

    @Override
    public String toString() {
        return "MetaEnrichedPropertySource{" +
                "basePropertySource=" + basePropertySource +
                ", metaInfo=" + metaInfo +
                '}';
    }

}
