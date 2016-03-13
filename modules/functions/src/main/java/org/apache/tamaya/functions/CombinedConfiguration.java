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
import org.apache.tamaya.spi.ConfigurationContext;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Combines a set of child configurations to a new one, by overriding the first entries with result from
 * later instances.
 */
class CombinedConfiguration implements Configuration{
    /** The name of the new configuration. */
    private final String name;
    /** The configuration's in evaluation order. Instances with higher indices override results with lower ones. */
    private final Configuration[] configurations;

    /**
     * Creates a combined configuration instance.
     * @param configName the name of the new config.
     * @param configs the configurations hereby instances with higher indices override results with lower ones.
     */
    public CombinedConfiguration(String configName, Configuration... configs){
        this.name = configName;
        this.configurations =configs.clone();
    }

    @Override
    public String get(String key) {
        String curValue = null;
        for(Configuration config: configurations){
            String value = config.get(key);
            if(value!=null){
                curValue = value;
            }
        }
        return curValue;
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
        T curValue = null;
        for(Configuration config: configurations){
            T value = config.get(key, type);
            if(value!=null){
                curValue = value;
            }
        }
        return curValue;
    }

    @Override
    public <T> T get(String key, TypeLiteral<T> type) {
        T curValue = null;
        for(Configuration config: configurations){
            T value = config.get(key, type);
            if(value!=null){
                curValue = value;
            }
        }
        return curValue;
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
        Map<String, String> result = new HashMap<>();
        for(Configuration ps:configurations){
            result.putAll(ps.getProperties());
        }
        return result;
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
    public ConfigurationContext getContext() {
        // TODO thjink on combining the participating contexts...
        return configurations[0].getContext();
    }

    @Override
    public String toString() {
        return "CombinedConfiguration{" +
                "name='" + name + '\'' +
                ", configurations=" + Arrays.toString(configurations) +
                '}';
    }
}
