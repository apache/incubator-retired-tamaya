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
package org.apache.tamaya.spisupport;

import org.apache.tamaya.ConfigOperator;
import org.apache.tamaya.Configuration;
import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.spi.*;
import org.junit.Test;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import static org.assertj.core.api.Assertions.*;
import org.assertj.core.internal.cglib.core.Predicate;

public class DefaultConfigurationTest {

    /**
     * Tests for current(String)
     */
    @Test(expected = NullPointerException.class)
    public void getDoesNotAcceptNull() {
        DefaultConfiguration c = new DefaultConfiguration(new MockedConfigurationContext());

        c.get((String)null);
    }

    /**
     * Tests for current(String)
     */
    @Test(expected = NullPointerException.class)
    public void getDoesNotAcceptNull2() {
        DefaultConfiguration c = new DefaultConfiguration(new MockedConfigurationContext());

        c.get((Iterable<String>) null);
    }

    /**
     * Tests for current(String, Class)
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test(expected = NullPointerException.class)
    public void getDoesNotAcceptNullForClassTargetType() {
        DefaultConfiguration c = new DefaultConfiguration(new MockedConfigurationContext());

        c.get("a", (Class) null);
    }

    /**
     * Tests for current(String, TypeLiteral)
     */
    @Test(expected = NullPointerException.class)
    public void getDoesNotAcceptNullForTypeLiteralTargetType() {
        DefaultConfiguration c = new DefaultConfiguration(new MockedConfigurationContext());

        c.get("a", (TypeLiteral<?>) null);
    }

    @Test
    public void getReturnsNullOrNotAsAppropriate() {
        DefaultConfiguration c = new DefaultConfiguration(new MockedConfigurationContext());
        assertThat(c.get("valueOfValid")).isNotNull();
        assertThat(c.get("valueOfNull")).isNull();
        assertThat(c.get("Filternull")).isNull(); //current does apply filtering
    }

    /**
     * Tests for getOrDefault(String, Class, String)
     */
    @Test(expected = NullPointerException.class)
    public void getOrDefaultDoesNotAcceptNullAsKeyForThreeParameterVariant() {
        DefaultConfiguration c = new DefaultConfiguration(new MockedConfigurationContext());

        c.getOrDefault((String)null, String.class, "ok");
    }

    /**
     * Tests for getOrDefault(String, Class, String)
     */
    @Test(expected = NullPointerException.class)
    public void getOrDefaultDoesNotAcceptNullAsKeyForThreeParameterVariant2() {
        DefaultConfiguration c = new DefaultConfiguration(new MockedConfigurationContext());

        c.getOrDefault((Iterable<String>)null, String.class, "ok");
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test(expected = NullPointerException.class)
    public void getOrDefaultDoesNotAcceptNullAsTargetTypeForThreeParameterVariant() {
        DefaultConfiguration c = new DefaultConfiguration(new MockedConfigurationContext());

        c.getOrDefault("a", (Class) null, "b");
    }

    /**
     * Tests for getOrDefault(String, TypeLiteral, String)
     */
    @Test(expected = NullPointerException.class)
    public void getOrDefaultDoesNotAcceptNullAsKeyForThreeParameterVariantSecondIsTypeLiteral() {
        DefaultConfiguration c = new DefaultConfiguration(new MockedConfigurationContext());

        c.getOrDefault((String)null, TypeLiteral.of(String.class), "ok");
    }

    /**
     * Tests for getOrDefault(String, TypeLiteral, String)
     */
    @Test(expected = NullPointerException.class)
    public void getOrDefaultDoesNotAcceptNullAsKeyForThreeParameterVariantSecondIsTypeLiteral2() {
        DefaultConfiguration c = new DefaultConfiguration(new MockedConfigurationContext());

        c.getOrDefault((Iterable<String>)null, TypeLiteral.of(String.class), "ok");
    }

    @Test
    public void getOrDefaultDoesAcceptNullAsDefaultValueForThreeParameterVariant() {
        DefaultConfiguration c = new DefaultConfiguration(new MockedConfigurationContext());
        assertThat(c.getOrDefault("a", String.class, null)).isNotNull();
        assertThat((String) c.getOrDefault("a", TypeLiteral.of(String.class), null)).isNotNull();
    }

    @Test(expected = NullPointerException.class)
    public void getOrDefaultDoesNotAcceptNullAsTargetTypeForThreeParameterVariantSecondIsTypeLiteral() {
        DefaultConfiguration c = new DefaultConfiguration(new MockedConfigurationContext());

        c.getOrDefault("a", (TypeLiteral<String>) null, "b");
    }

    /**
     * Tests for getOrDefault(String, String)
     */
    @Test(expected = NullPointerException.class)
    public void getOrDefaultDoesNotAcceptNullAsKeyForTwoParameterVariantDefaultValueIsSecond() {
        DefaultConfiguration c = new DefaultConfiguration(new MockedConfigurationContext());

        c.getOrDefault((String)null, "ok");
    }

    /**
     * Tests for getOrDefault(String, String)
     */
    @Test(expected = NullPointerException.class)
    public void getOrDefaultDoesNotAcceptNullAsKeyForTwoParameterVariantDefaultValueIsSecond2() {
        DefaultConfiguration c = new DefaultConfiguration(new MockedConfigurationContext());

        c.getOrDefault((Iterable<String>)null, "ok");
    }

    @Test
    public void getOrDefaultDoesAcceptNullAsDefaultValueForTwoParameterVariantDefaultValueIsSecond() {
        DefaultConfiguration c = new DefaultConfiguration(new MockedConfigurationContext());
        assertThat(c.getOrDefault("a", null)).isNotNull();
    }

    @Test
    public void getOrDefaultReturnDefaultIfValueWouldHaveBeenNull() {
        DefaultConfiguration c = new DefaultConfiguration(new MockedConfigurationContext());
        assertThat("ok").isEqualTo(c.getOrDefault("valueOfNull", "ok"));
        assertThat("ok").isEqualTo(c.getOrDefault("valueOfNull", String.class, "ok"));
        assertThat("ok").isEqualTo(c.getOrDefault("valueOfNull", TypeLiteral.of(String.class), "ok"));
    }

    /**
     * Tests for evaluateRawValue(String)
     */
    @Test
    public void evaluateRawValueReturnsNullOrNotAsAppropriate() {
        DefaultConfiguration c = new DefaultConfiguration(new MockedConfigurationContext());
        assertThat(c.evaluteRawValue("valueOfValid")).isNotNull();
        assertThat(c.evaluteRawValue("valueOfNull")).isNull();
        assertThat(c.evaluteRawValue("Filternull")).isNotNull(); //evaluateRawValue does not apply filtering
    }

    /**
     * Tests for getProperties()
     */
    @Test
    public void getPropertiesReturnsNullOrNotAsAppropriate() {
        DefaultConfiguration c = new DefaultConfiguration(new MockedConfigurationContext());
        Map<String, String> result = c.getProperties();
        assertThat(result.get("someKey")).isEqualTo("valueFromMockedPropertySource");
        assertThat(result.get("notInThePropertiesMock")).isNull();
        assertThat(result.get("valueOfNull")).isNull();
        assertThat(result.get("Filternull")).isNull();
    }

    /**
     * Tests for convertValue(String key, String createValue, TypeLiteral<T> type)
     */
    @Test
    public void testConvertValue() {
        DefaultConfiguration c = new DefaultConfiguration(new MockedConfigurationContext());
        assertThat(100 == (Integer) c.convertValue("aHundred",
                Collections.singletonList(PropertyValue.of("aHundred", "100", null)),
                TypeLiteral.of(Integer.class))).isTrue();
    }

    @Test(expected = NullPointerException.class)
    public void with_Null() {
        DefaultConfiguration c = new DefaultConfiguration(new MockedConfigurationContext());

        c.with(null);
    }

    @Test(expected = NullPointerException.class)
    public void map_Null() {
        DefaultConfiguration c = new DefaultConfiguration(new MockedConfigurationContext());

        c.map(null);
    }

    @Test(expected = NullPointerException.class)
    public void query_Null() {
        DefaultConfiguration c = new DefaultConfiguration(new MockedConfigurationContext());

        c.query(null);
    }

    @Test(expected = NullPointerException.class)
    public void adapt_Null() {
        DefaultConfiguration c = new DefaultConfiguration(new MockedConfigurationContext());

        c.adapt(null);
    }

    @Test
    public void with() {
        DefaultConfiguration c = new DefaultConfiguration(new MockedConfigurationContext());
        assertThat(c).isEqualTo(c.with(config -> config));
    }

    @Test
    public void map() {
        DefaultConfiguration c = new DefaultConfiguration(new MockedConfigurationContext());
        assertThat(c).isEqualTo(c.map(config -> config));
    }

    @Test
    public void query() {
        DefaultConfiguration c = new DefaultConfiguration(new MockedConfigurationContext());
        assertThat("testQ").isEqualTo(c.query(config -> "testQ"));
    }

    @Test
    public void adapt() {
        DefaultConfiguration c = new DefaultConfiguration(new MockedConfigurationContext());
        assertThat("testQ").isEqualTo(c.adapt(config -> "testQ"));
    }
    
    @Test
    public void testEqualsAndHashAndToStringValues() {
        ConfigurationContext sharedContext = new MockedConfigurationContext();
        DefaultConfiguration config1 = new DefaultConfiguration(sharedContext);
        DefaultConfiguration config2 = new DefaultConfiguration(sharedContext);
        DefaultConfiguration config3 = new DefaultConfiguration(new MockedConfigurationContext());

        assertThat(config1).isEqualTo(config1);
        assertThat(config1).isNotEqualTo(null);
        assertThat(sharedContext).isNotEqualTo(config1);
        assertThat(config1).isNotEqualTo(sharedContext);
        assertThat("aString").isNotEqualTo(config1);
        assertThat(config2).isEqualTo(config1);
        assertThat(config1).isNotEqualTo(config3);
        assertThat(config2.hashCode()).isEqualTo(config1.hashCode());
        assertThat(config1.hashCode()).isNotEqualTo(config3.hashCode());
        assertThat(config1.toString().contains("Configuration{")).isTrue();
    }

}
