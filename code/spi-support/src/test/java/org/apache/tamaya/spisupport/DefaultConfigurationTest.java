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

import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.spi.*;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class DefaultConfigurationTest {

    /**
     * Tests for get(String)
     */
    @Test(expected = NullPointerException.class)
    public void getDoesNotAcceptNull() {
        DefaultConfiguration c = new DefaultConfiguration(new MockedConfigurationContext());

        c.get(null);
    }

    /**
     * Tests for get(String, Class)
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test(expected = NullPointerException.class)
    public void getDoesNotAcceptNullForClassTargetType() {
        DefaultConfiguration c = new DefaultConfiguration(new MockedConfigurationContext());

        c.get("a", (Class) null);
    }

    /**
     * Tests for get(String, TypeLiteral)
     */
    @Test(expected = NullPointerException.class)
    public void getDoesNotAcceptNullForTypeLiteralTargetType() {
        DefaultConfiguration c = new DefaultConfiguration(new MockedConfigurationContext());

        c.get("a", (TypeLiteral<?>) null);
    }

    @Test
    public void getReturnsNullOrNotAsAppropriate() {
        DefaultConfiguration c = new DefaultConfiguration(new MockedConfigurationContext());
        assertNotNull(c.get("valueOfValid"));
        assertNull(c.get("valueOfNull"));
        assertNull(c.get("Filternull")); //get does apply filtering
    }

    /**
     * Tests for getOrDefault(String, Class, String)
     */
    @Test(expected = NullPointerException.class)
    public void getOrDefaultDoesNotAcceptNullAsKeyForThreeParameterVariant() {
        DefaultConfiguration c = new DefaultConfiguration(new MockedConfigurationContext());

        c.getOrDefault(null, String.class, "ok");
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

        c.getOrDefault(null, TypeLiteral.of(String.class), "ok");
    }

    @Test
    public void getOrDefaultDoesAcceptNullAsDefaultValueForThreeParameterVariant() {
        DefaultConfiguration c = new DefaultConfiguration(new MockedConfigurationContext());

        assertNotNull(c.getOrDefault("a", String.class, null));
        assertNotNull(c.getOrDefault("a", TypeLiteral.of(String.class), null));
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

        c.getOrDefault(null, "ok");
    }

    @Test
    public void getOrDefaultDoesAcceptNullAsDefaultValueForTwoParameterVariantDefaultValueIsSecond() {
        DefaultConfiguration c = new DefaultConfiguration(new MockedConfigurationContext());
        assertNotNull(c.getOrDefault("a", null));
    }

    @Test
    public void getOrDefaultReturnDefaultIfValueWouldHaveBeenNull() {
        DefaultConfiguration c = new DefaultConfiguration(new MockedConfigurationContext());
        assertEquals("ok", c.getOrDefault("valueOfNull", "ok"));
        assertEquals("ok", c.getOrDefault("valueOfNull", String.class, "ok"));
        assertEquals("ok", c.getOrDefault("valueOfNull", TypeLiteral.of(String.class), "ok"));
    }

    /**
     * Tests for evaluateRawValue(String)
     */
    @Test
    public void evaluateRawValueReturnsNullOrNotAsAppropriate() {
        DefaultConfiguration c = new DefaultConfiguration(new MockedConfigurationContext());
        assertNotNull(c.evaluteRawValue("valueOfValid"));
        assertNull(c.evaluteRawValue("valueOfNull"));
        assertNotNull(c.evaluteRawValue("Filternull")); //evaluateRawValue does not apply filtering
    }

    /**
     * Tests for getProperties()
     */
    @Test
    public void getPropertiesReturnsNullOrNotAsAppropriate() {
        DefaultConfiguration c = new DefaultConfiguration(new MockedConfigurationContext());
        Map<String, String> result = c.getProperties();
        assertEquals("valueFromMockedPropertySource", result.get("someKey"));
        assertNull(result.get("notInThePropertiesMock"));
        assertNull(result.get("valueOfNull"));
        assertNull(result.get("Filternull"));
    }

    /**
     * Tests for convertValue(String key, String value, TypeLiteral<T> type)
     */
    @Test
    public void testConvertValue() {
        DefaultConfiguration c = new DefaultConfiguration(new MockedConfigurationContext());
        assertTrue(100 == (Integer) c.convertValue("aHundred", "100", TypeLiteral.of(Integer.class)));
    }

    @Test(expected = NullPointerException.class)
    public void with_Null() {
        DefaultConfiguration c = new DefaultConfiguration(new MockedConfigurationContext());

        c.with(null);
    }

    @Test(expected = NullPointerException.class)
    public void query_Null() {
        DefaultConfiguration c = new DefaultConfiguration(new MockedConfigurationContext());

        c.query(null);
    }

    @Test
    public void with() {
        DefaultConfiguration c = new DefaultConfiguration(new MockedConfigurationContext());
        assertEquals(c.with(config -> config), c);
    }

    @Test
    public void query() {
        DefaultConfiguration c = new DefaultConfiguration(new MockedConfigurationContext());
        assertEquals(c.query(config -> "testQ"), "testQ");
    }
    
    @Test
    public void testEqualsAndHashAndToStringValues() {
        ConfigurationContext sharedContext = new MockedConfigurationContext();
        DefaultConfiguration config1 = new DefaultConfiguration(sharedContext);
        DefaultConfiguration config2 = new DefaultConfiguration(sharedContext);
        DefaultConfiguration config3 = new DefaultConfiguration(new MockedConfigurationContext());

        assertEquals(config1, config1);
        assertNotEquals(null, config1);
        assertNotEquals(sharedContext, config1);
        assertNotEquals(config1, sharedContext);
        assertNotEquals("aString", config1);
        assertEquals(config1, config2);
        assertNotEquals(config1, config3);
        assertEquals(config1.hashCode(), config2.hashCode());
        assertNotEquals(config1.hashCode(), config3.hashCode());
        assertTrue(config1.toString().contains("Configuration{"));
    }

}
