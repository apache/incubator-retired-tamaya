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

import org.apache.tamaya.spi.PropertySource;

/**
 * Provider implementation that combines multiple other config by intersecting
 * the key/values common to one source.
 */
class IntersectingPropertySource extends AggregatedPropertySource {


    /**
     * Creates a mew instance, with aggregation polilcy
     * {@code AggregationPolicy.OVERRIDE}.
     *
     * @param ordinal           the ordinal
     * @param name              The name to be used, not null.
     * @param baseSource        the base property source, not null
     * @param aggregatedSource  the aggregatesd property source, not null
     */
    public IntersectingPropertySource(int ordinal, String name,
                                    PropertySource baseSource, PropertySource aggregatedSource) {
        super(ordinal, name, (k,v1,v2) -> {
            if(v1!=null && v2!=null && v1.equals(v2)){
                return v1;
            }
            return null;
        }, baseSource, aggregatedSource);
    }


}
