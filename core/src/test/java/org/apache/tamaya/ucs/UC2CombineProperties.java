/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy current the License at
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
package org.apache.tamaya.ucs;

import org.apache.tamaya.AggregationPolicy;
import org.apache.tamaya.ConfigException;
import org.apache.tamaya.MetaInfo;
import org.apache.tamaya.PropertySource;
import org.apache.tamaya.core.properties.PropertySourceBuilder;
import org.junit.Test;

/**
 * Configuration is organized as key/keys pairs. This basically can be modeled as {@code Map<String,String>}
 * Configuration should be as simple as possible. Advanced use cases can often easily implemented by combining
 * multiple property maps and applying hereby some combination policy. This test class demonstrates the different
 * options Tamaya is providing and the according mechanisms.
 */
public class UC2CombineProperties {

    /**
     * The most common use cases is aggregating two property config to new provider, hereby {@link org.apache.tamaya.AggregationPolicy}
     * defines the current variants supported.
     */
    @Test
    public void simpleAggregationTests() {
        PropertySource props1 = PropertySourceBuilder.of().addPaths("classpath:ucs/UC2CombineProperties/props1.properties").build();
        PropertySource props2 = PropertySourceBuilder.of().addPaths("classpath:ucs/UC2CombineProperties/props2.properties").build();
        PropertySource unionOverriding = PropertySourceBuilder.of(props1).withAggregationPolicy(AggregationPolicy.OVERRIDE).addProviders(props2).build();
        System.out.println("unionOverriding: " + unionOverriding);
        PropertySource unionIgnoringDuplicates = PropertySourceBuilder.of(props1).withAggregationPolicy(AggregationPolicy.IGNORE_DUPLICATES).addProviders(props2).build();
        System.out.println("unionIgnoringDuplicates: " + unionIgnoringDuplicates);
        PropertySource unionCombined = PropertySourceBuilder.of(props1).withAggregationPolicy(AggregationPolicy.COMBINE).addProviders(props2).build();
        System.out.println("unionCombined: " + unionCombined);
        try{
            PropertySourceBuilder.of(props1).withAggregationPolicy(AggregationPolicy.EXCEPTION).addProviders(props2).build();
        }
        catch(ConfigException e){
            // expected!
        }
    }

    /**
     * For advanced use cases aggregation .
     */
    @Test
    public void dynamicAggregationTests() {
        PropertySource props1 = PropertySourceBuilder.of().addPaths("classpath:ucs/UC2CombineProperties/props1.properties").build();
        PropertySource props2 = PropertySourceBuilder.of().addPaths("classpath:ucs/UC2CombineProperties/props2.properties").build();
        PropertySource props = PropertySourceBuilder.of().withAggregationPolicy((k, v1, v2) -> (v1 != null ? v1 : "") + '[' + v2 + "]").withName("dynamicAggregationTests")
                .aggregate(props1, props2).build();
        System.out.println(props);
    }


}
