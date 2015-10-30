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

import org.apache.tamaya.ConfigOperator;
import org.apache.tamaya.ConfigQuery;
import org.apache.tamaya.Configuration;
import org.apache.tamaya.TypeLiteral;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;


/**
 * Configuration that filters part of the entries defined by a filter predicate.
 */
class MappedConfiguration implements Configuration {

    private static final Logger LOG = Logger.getLogger(MappedConfiguration.class.getName());
    private final Configuration baseConfiguration;
    private final KeyMapper keyMapper;
    private final String mapType;

    MappedConfiguration(Configuration baseConfiguration, KeyMapper keyMapper, String mapType) {
        this.baseConfiguration = Objects.requireNonNull(baseConfiguration);
        this.keyMapper = Objects.requireNonNull(keyMapper);
        this.mapType = mapType!=null?mapType:this.keyMapper.toString();
    }

    @Override
    public String get(String key) {
        return get(key, String.class);
    }

    @Override
    public String getOrDefault(String key, String defaultValue) {
        String val = get(key);
        if(val==null){
            return defaultValue;
        }
        return val;
    }

    @Override
    public <T> T getOrDefault(String key, Class<T> type, T defaultValue) {
        T val = get(key, type);
        if(val==null){
            return defaultValue;
        }
        return val;
    }

    @Override
    public <T> T get(String key, Class<T> type) {
        return (T)get(key, TypeLiteral.of(type));
    }

    @Override
    public <T> T get(String key, TypeLiteral<T> type) {
        String targetKey = keyMapper.mapKey(key);
        if (targetKey != null) {
            return baseConfiguration.get(targetKey, type);
        }
        LOG.finest("Configuration property hidden by KeyMapper, key="+key+", mapper="+keyMapper+", config="+this);
        return null;
    }

    @Override
    public <T> T getOrDefault(String key, TypeLiteral<T> type, T defaultValue) {
        T val = get(key, type);
        if(val==null){
            return defaultValue;
        }
        return val;
    }

    @Override
    public Map<String, String> getProperties() {
        Map<String, String> baseProps = baseConfiguration.getProperties();
        Map<String, String> props = new HashMap<>(baseProps.size());
        for(Map.Entry<String,String> en:baseProps.entrySet()){
            String targetKey = keyMapper.mapKey(en.getKey());
            if (targetKey != null) {
                props.put(targetKey, en.getValue());
            }
        }
        return props;
    }

    @Override
    public Configuration with(ConfigOperator operator) {
        return operator.operate(this);
    }

    @Override
    public <T> T query(ConfigQuery<T> query) {
        return query.query(this);
    }

    @Override
    public String toString() {
        return "FilteredConfiguration{" +
                "baseConfiguration=" + baseConfiguration +
                ", mapping=" + mapType +
                '}';
    }

}
