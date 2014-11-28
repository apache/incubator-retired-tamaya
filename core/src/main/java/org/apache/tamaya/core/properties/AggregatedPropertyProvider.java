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

import org.apache.tamaya.ConfigChangeSet;
import org.apache.tamaya.MetaInfoBuilder;
import org.apache.tamaya.PropertyProvider;
import java.util.*;

/**
 * Implementation for a {@link org.apache.tamaya.PropertyProvider} that is an aggregate of
 * multiple child instances. Controlled by an {@link AggregationPolicy} the
 * following aggregations are supported:
 * <ul>
 * <li><b>IGNORE: </b>Ignore all overrides.</li>
 * <li><b>: </b></li>
 * <li><b>: </b></li>
 * <li><b>: </b></li>
 * </ul>
 */
class AggregatedPropertyProvider extends AbstractPropertyProvider{

    private static final long serialVersionUID = -1419376385695224799L;
	private AggregationPolicy policy = AggregationPolicy.IGNORE;
	private List<PropertyProvider> units = new ArrayList<PropertyProvider>();
    private PropertyProvider mutableProvider;

    /**
     * Creates a new instance.
     * @param mutableProvider the provider instance that would be used for delegating
     *                        change requests.
     * @param policy
     *            The aggregation policy to be used.
     * @param propertyMaps
     *            The property sets to be included.
     */
	public AggregatedPropertyProvider(PropertyProvider mutableProvider, AggregationPolicy policy, PropertyProvider... propertyMaps) {
        super(MetaInfoBuilder.of().setType("aggregated").set("policy", policy.toString()).build());
        Objects.requireNonNull(policy);
        this.policy = policy;
		units.addAll(Arrays.asList(propertyMaps));
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
	 * Return the names of the {@link org.apache.tamaya.PropertyProvider} instances to be
	 * aggregated in this instance, in the order of precedence (the first are
	 * the weakest).
	 * 
	 * @return the ordered list of aggregated scope identifiers, never
	 *         {@code null}.
	 */
	public List<PropertyProvider> getConfigurationUnits() {
		return Collections.unmodifiableList(units);
	}

    /**
     * Apply a config change to this item. Hereby the change must be related to the same instance.
     * @param change the config change
     * @throws org.apache.tamaya.ConfigException if an unrelated change was passed.
     * @throws UnsupportedOperationException when the configuration is not writable.
     */
    @Override
    public void apply(ConfigChangeSet change){
        if(mutableProvider!=null)
            mutableProvider.apply(change);
        else
            super.apply(change);
    }

    @Override
    public Map<String,String> toMap() {
		Map<String, String> value = new HashMap<>();
        for (PropertyProvider unit : units) {
            for (Map.Entry<String, String> entry : unit.toMap()
                    .entrySet()) {
                switch (policy) {
                    case IGNORE:
                        if (!value.containsKey(entry.getKey())) {
                            value.put(entry.getKey(), entry.getValue());
                        }
                        break;
                    case EXCEPTION:
                        if (value.containsKey(entry.getKey())) {
                            throw new IllegalStateException("Duplicate key: "
                                                                    + entry.getKey()
                                                                    + " in " + this);
                        }
                        else {
                            value.put(entry.getKey(), entry.getValue());
                        }
                        break;
                    case OVERRIDE:
                        value.put(entry.getKey(), entry.getValue());
                        break;
                    default:
                        break;
                }
            }
        }
        return value;
	}

    @Override
	public ConfigChangeSet load() {
		for (PropertyProvider unit : units) {
			unit.load();
		}
        return super.load();
	}

}
