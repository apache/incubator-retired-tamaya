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
package org.apache.tamaya.core.internal;

import org.apache.tamaya.*;
import org.apache.tamaya.core.properties.AbstractPropertyProvider;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Provider implementation that combines multiple other providers by intersecting
 * the key/values common to all providers, conflicting keys are resolved using an
 * {@link org.apache.tamaya.AggregationPolicy}.
 */
class IntersectingPropertyProvider extends AbstractPropertyProvider {

    private List<PropertyProvider> providers;
    private PropertyProvider aggregatedDelegate;

    public IntersectingPropertyProvider(AggregationPolicy policy, List<PropertyProvider> providers) {
        super(MetaInfoBuilder.of().setType("intersection").build());
        this.providers = new ArrayList<>(providers);
        aggregatedDelegate = PropertyProviders.aggregate(policy, this.providers);
    }

    public IntersectingPropertyProvider(MetaInfo metaInfo, AggregationPolicy policy, PropertyProvider... providers) {
        super(metaInfo);
        this.providers = Arrays.asList(Objects.requireNonNull(providers));
        aggregatedDelegate = PropertyProviders.aggregate(policy, providers);
    }


    @Override
    public Optional<String> get(String key) {
        if (containsKey(key))
            return aggregatedDelegate.get(key);
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
        return aggregatedDelegate.toMap().entrySet().stream().filter(en -> containsKey(en.getKey())).collect(
                Collectors.toConcurrentMap(en -> en.getKey(), en -> en.getValue()));
    }

}
