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
import java.util.Optional;

/**
 * Configuration that filters part of the entries defined by a filter predicate.
 */
class FilteredConfiguration implements Configuration {

    private final Configuration baseConfiguration;
    private final BiPredicate<String, String> filter;
    private String filterType;

    FilteredConfiguration(Configuration baseConfiguration, BiPredicate<String, String> filter, String filterType) {
        this.baseConfiguration = Objects.requireNonNull(baseConfiguration);
        this.filter = Objects.requireNonNull(filter);
        this.filterType = Optional.ofNullable(filterType).orElse(this.filter.toString());
    }

    @Override
    public String get(String key) {
        return get(key, String.class);
    }

    @Override
    public <T> T get(String key, Class<T> type) {
        return (T)get(key, TypeLiteral.of(type));
    }

    @Override
    public <T> T get(String key, TypeLiteral<T> type) {
        String value = baseConfiguration.get(key);
        if (filter.test(key, value)) {
            return baseConfiguration.get(key, type);
        }
        return null;
    }

    @Override
    public Map<String, String> getProperties() {
        Map<String, String> result = new HashMap<>();
        for(Map.Entry<String,String> en:baseConfiguration.getProperties().entrySet()){
            if(filter.test(en.getKey(), en.getValue())){
                result.put(en.getKey(), en.getValue());
            }
        }
        return result;
    }

    @Override
    public Configuration with(ConfigOperator operator) {
        return null;
    }

    @Override
    public <T> T query(ConfigQuery<T> query) {
        return query.query(this);
    }

    @Override
    public String toString() {
        return "FilteredConfiguration{" +
                "baseConfiguration=" + baseConfiguration +
                ", filter=" + filter +
                '}';
    }

}
