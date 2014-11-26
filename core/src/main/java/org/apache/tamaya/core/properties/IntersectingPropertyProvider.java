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
package org.apache.tamaya.core.properties;

import org.apache.tamaya.MetaInfo;
import org.apache.tamaya.MetaInfoBuilder;
import org.apache.tamaya.PropertyProvider;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Anatole on 22.10.2014.
 */
class IntersectingPropertyProvider extends AbstractPropertyProvider {

    private Collection<PropertyProvider> providers;
    private PropertyProvider union;

    public IntersectingPropertyProvider(AggregationPolicy policy, PropertyProvider... providers) {
        super(MetaInfoBuilder.of().setType("intersection").set("providers", Arrays.toString(providers)).build());
        this.providers = Arrays.asList(Objects.requireNonNull(providers));
        union = PropertyProviders.union(policy, providers);
    }

    public IntersectingPropertyProvider(MetaInfo metaInfo, AggregationPolicy policy, PropertyProvider... providers) {
        super(metaInfo);
        this.providers = Arrays.asList(Objects.requireNonNull(providers));
        union = PropertyProviders.union(policy, providers);
    }


    @Override
    public Optional<String> get(String key) {
        if (containsKey(key))
            return union.get(key);
        return Optional.empty();
    }

    private boolean filter(Map.Entry<String, String> entry) {
        return containsKey(entry.getKey());
    }

    @Override
    public boolean containsKey(String key) {
        for (PropertyProvider prov : this.providers) {
            if (!prov.containsKey(key)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Map<String, String> toMap() {
        return union.toMap().entrySet().stream().filter(en -> containsKey(en.getKey())).collect(
                Collectors.toConcurrentMap(en -> en.getKey(), en -> en.getValue()));
    }

}
