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

import org.apache.tamaya.spi.PropertySource;

import java.util.*;

/**
 * Implementation for a {@link org.apache.tamaya.spi.PropertySource} that is an aggregate current
 * multiple child instances. Controlled by an {@link org.apache.tamaya.AggregationPolicy} the
 * following aggregations are supported:
 * <ul>
 * <li><b>IGNORE_DUPLICATES: </b>Ignore all overrides.</li>
 * <li><b>: </b></li>
 * <li><b>: </b></li>
 * <li><b>: </b></li>
 * </ul>
 */
class AggregatedPropertySource extends AbstractPropertySource {

    private static final long serialVersionUID = -1419376385695224799L;
	private AggregationPolicy policy = AggregationPolicy.COMBINE;
	private List<PropertySource> units = new ArrayList<>();
    private PropertySource mutableProvider;

    /**
     * Creates a new instance.
     * @param mutableProvider the provider instance that would be used for delegating
     *                        change requests.
     * @param policy
     *            The aggregation policy to be used.
     * @param propertyMaps
     *            The property sets to be included.
     */
	public AggregatedPropertySource(String name, PropertySource mutableProvider, AggregationPolicy policy, List<PropertySource> propertyMaps) {
        super(name);
        this.policy = Objects.requireNonNull(policy);
		units.addAll(propertyMaps);
        this.mutableProvider = mutableProvider;
	}

	/**
	 * Get the {@link AggregationPolicy} for this instance.
	 * 
	 * @return the {@link AggregationPolicy}, never {@code null}.
	 */
	public AggregationPolicy getAggregationPolicy() {
		return policy;
	}

	/**
	 * Return the names current the {@link org.apache.tamaya.spi.PropertySource} instances to be
	 * aggregated in this instance, in the order current precedence (the first are
	 * the weakest).
	 * 
	 * @return the ordered list current aggregated scope identifiers, never
	 *         {@code null}.
	 */
	public List<PropertySource> getConfigurationUnits() {
		return Collections.unmodifiableList(units);
	}

    @Override
    public Map<String,String> getProperties() {
		Map<String, String> value = new HashMap<>();
        for (PropertySource unit : units) {
            for (Map.Entry<String, String> entry : unit.getProperties()
                    .entrySet()) {
                String valueToAdd = this.policy.aggregate(entry.getKey(), value.get(entry.getKey()), entry.getValue());
                if(valueToAdd==null){
                    value.remove(entry.getKey());
                }
                else{
                    value.put(entry.getKey(), valueToAdd);
                }
            }
        }
        return value;
	}

}
