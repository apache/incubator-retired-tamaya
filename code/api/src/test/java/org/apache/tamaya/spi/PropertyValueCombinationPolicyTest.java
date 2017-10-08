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
package org.apache.tamaya.spi;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class PropertyValueCombinationPolicyTest {

    @Test
    public void defaulPolicyOverridesCurrentValueByTheOneOfTheGivenProperySource() throws Exception {
        PropertyValueCombinationPolicy policy = PropertyValueCombinationPolicy.DEFAULT_OVERRIDING_POLICY;

        PropertyValue current = PropertyValue.of("a", "AAA", "Test");
        PropertyValue result = policy.collect(current, "a", new DummyPropertySource());

        assertThat(result.getKey()).isEqualTo("a");
        assertThat(result.getValue()).isEqualTo("Ami");
    }

    @Test
    public void defaulPolicyOverridesKeepsTheCurrentValueIfGivenProperySourceDoesNotHaveIt() throws Exception {
        PropertyValueCombinationPolicy policy = PropertyValueCombinationPolicy.DEFAULT_OVERRIDING_POLICY;

        PropertyValue current = PropertyValue.of("a", "AAA", "Test");
        PropertyValue result = policy.collect(current, "a", PropertySource.EMPTY);

        assertThat(result.getKey()).isEqualTo("a");
        assertThat(result.getValue()).isEqualTo("AAA");
        assertThat(result).isEqualTo(current);
    }


    static class DummyPropertySource implements PropertySource {
        @Override
        public int getOrdinal() {
            return 10;
        }

        @Override
        public String getName() {
            return "NAME";
        }

        @Override
        public PropertyValue get(String key) {
            return getProperties().get(key);
        }

        @Override
        public Map<String, PropertyValue> getProperties() {
            PropertyValue a = PropertyValue.of("a", "Ami", "Test");
            PropertyValue b = PropertyValue.of("b", "Big", "Test");

            HashMap<String, PropertyValue> properties = new HashMap<>();

            properties.put(a.getKey(), a);
            properties.put(b.getKey(), b);

            return properties;
        }

        @Override
        public boolean isScannable() {
            return true;
        }
    }

}