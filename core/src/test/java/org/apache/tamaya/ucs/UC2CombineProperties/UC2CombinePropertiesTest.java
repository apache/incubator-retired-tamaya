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
package org.apache.tamaya.ucs.UC2CombineProperties;

import org.apache.tamaya.*;
import org.junit.Test;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;

/**
 * Configuration is organized as key/value pairs. This basically can be modeled as {@code Map<String,String>}
 * Configuration should be as simple as possible. Advanced use cases can often easily implemented by combining
 * multiple property maps and applying hereby some combination policy. This test class demonstrates the different
 * options Tamaya is providing and the according mechanisms.
 */
public class UC2CombinePropertiesTest {

    /**
     * The most common use cases is aggregating two property providers to new provider, hereby {@link org.apache.tamaya.AggregationPolicy}
     * defines the current variants supported.
     */
    @Test
    public void simpleAggregationTests() {
        PropertyProvider props1 = PropertyProviders.fromPaths("classpath:ucs/UC2CombineProperties/props1.properties");
        PropertyProvider props2 = PropertyProviders.fromPaths("classpath:ucs/UC2CombineProperties/props2.properties");
        PropertyProvider unionOverriding = PropertyProviders.aggregate(AggregationPolicy.OVERRIDE(), props1, props2);
        System.out.println("unionOverriding: " + unionOverriding);
        PropertyProvider unionIgnoringDuplicates = PropertyProviders.aggregate(AggregationPolicy.IGNORE_DUPLICATES(), props1, props2);
        System.out.println("unionIgnoringDuplicates: " + unionIgnoringDuplicates);
        PropertyProvider unionCombined = PropertyProviders.aggregate(AggregationPolicy.COMBINE(), props1, props2);
        System.out.println("unionCombined: " + unionCombined);
        try{
            PropertyProviders.aggregate(AggregationPolicy.EXCEPTION(), props1, props2);
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
        PropertyProvider props1 = PropertyProviders.fromPaths("classpath:ucs/UC2CombineProperties/props1.properties");
        PropertyProvider props2 = PropertyProviders.fromPaths("classpath:ucs/UC2CombineProperties/props2.properties");
        PropertyProvider props = PropertyProviders.aggregate((k, v1, v2) -> (v1 != null ? v1 : "") + '[' + v2 + "]", MetaInfo.of("dynamicAggregationTests"),
                props1, props2);
        System.out.println(props);
    }


}
