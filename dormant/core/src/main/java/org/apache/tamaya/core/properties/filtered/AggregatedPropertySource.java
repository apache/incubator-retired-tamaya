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
package org.apache.tamaya.core.properties.filtered;

import org.apache.tamaya.core.properties.AggregationPolicy;
import org.apache.tamaya.spi.PropertySource;

import java.util.*;

/**
 * Implementation for a {@link org.apache.tamaya.spi.PropertySource} that is an aggregate current
 * multiple child instances.
 */
public class AggregatedPropertySource implements PropertySource {

    private PropertySource baseSource;
    private PropertySource aggregatedSource;
    private String name;
    private int ordinal;
    private AggregationPolicy aggregationPolicy;

    /**
     * Creates a mew instance, with aggregation polilcy
     * {@code AggregationPolicy.OVERRIDE}.
     *
     * @param ordinal           the ordinal
     * @param name              The name to be used, not null.
     * @param aggregationPolicy the {@link org.apache.tamaya.core.properties.AggregationPolicy} to be applied
     * @param baseSource        the base property source, not null
     * @param aggregatedSource  the aggregatesd property source, not null
     */
    public AggregatedPropertySource(int ordinal, String name, AggregationPolicy aggregationPolicy,
                                    PropertySource baseSource, PropertySource aggregatedSource) {
        this.aggregationPolicy = Objects.requireNonNull(aggregationPolicy);
        this.name = Objects.requireNonNull(name);
        this.ordinal = ordinal;
        this.baseSource = Objects.requireNonNull(baseSource);
        this.aggregatedSource = Objects.requireNonNull(aggregatedSource);
    }

    @Override
    public int getOrdinal() {
        return this.ordinal;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Map<String, String> getProperties() {
        Map<String, String> result = new HashMap<>();
        Set<String> keySet = new HashSet<>(this.baseSource.getProperties().keySet());
        keySet.addAll(this.aggregatedSource.getProperties().keySet());
        for (String key : keySet) {
            String value = this.aggregationPolicy.aggregate(
                    key,
                    baseSource.get(key).orElse(null),
                    aggregatedSource.get(key).orElse(null));
            if (value != null) {
                result.put(key, value);
            }
        }
        return result;
    }

    @Override
    public Optional<String> get(String key) {
        String value1 = this.baseSource.get(key).orElse(null);
        String value2 = this.aggregatedSource.get(key).orElse(null);
        return Optional.ofNullable(aggregationPolicy.aggregate(key, value1, value2));
    }

    @Override
    public String toString() {
        return "Aggregate(baseSource=" + baseSource +
                ", aggregatedSource=" + aggregatedSource.getName() +
                ", aggregationPolicy=" + aggregationPolicy + ")";
    }
}
