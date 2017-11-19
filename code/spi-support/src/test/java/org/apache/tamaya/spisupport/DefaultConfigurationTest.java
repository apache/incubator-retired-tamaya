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

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class DefaultConfigurationTest {

    /**
     * Tests for get(String)
     */
    @Test(expected = NullPointerException.class)
    public void getDoesNotAcceptNull() {
        DefaultConfiguration c =  new DefaultConfiguration(new DummyConfigurationContext());

        c.get(null);
    }

    /**
     * Tests for get(String, Class)
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Test(expected = NullPointerException.class)
    public void getDoesNotAcceptNullForClassTargetType() {
        DefaultConfiguration c = new DefaultConfiguration(new DummyConfigurationContext());

        c.get("a", (Class) null);
    }

    /**
     * Tests for get(String, TypeLiteral)
     */
    @Test(expected = NullPointerException.class)
    public void getDoesNotAcceptNullForTypeLiteralTargetType() {
        DefaultConfiguration c =  new DefaultConfiguration(new DummyConfigurationContext());

        c.get("a", (TypeLiteral<?>)null);
    }

    /**
     * Tests for getOrDefault(String, Class, String)
     */
    @Test(expected = NullPointerException.class)
    public void getOrDefaultDoesNotAcceptNullAsKeyForThreeParameterVariant() {
        DefaultConfiguration c = new DefaultConfiguration(new DummyConfigurationContext());

        c.getOrDefault(null, String.class, "ok");
    }

    @Test
    public void getOrDefaultDoesAcceptNullAsDefaultValueForThreeParameterVariant() {
        DefaultConfiguration c = new DefaultConfiguration(new DummyConfigurationContext());

        assertNull(c.getOrDefault("a", String.class, null));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	@Test(expected = NullPointerException.class)
    public void getOrDefaultDoesNotAcceptNullAsTargetTypeForThreeParameterVariant() {
        DefaultConfiguration c = new DefaultConfiguration(new DummyConfigurationContext());

        c.getOrDefault("a", (Class)null, "b");
    }

    /**
     * Tests for getOrDefault(String, TypeLiteral, String)
     */
    @Test(expected = NullPointerException.class)
    public void getOrDefaultDoesNotAcceptNullAsKeyForThreeParameterVariantSecondIsTypeLiteral() {
        DefaultConfiguration c = new DefaultConfiguration(new DummyConfigurationContext());

        c.getOrDefault(null, TypeLiteral.of(String.class), "ok");
    }

    @Test
    public void getOrDefaultDoesAcceptNullAsDefaultValueForThreeParameterVariantSecondIsTypeLiteral() {
        DefaultConfiguration c = new DefaultConfiguration(new DummyConfigurationContext());

        assertNull(c.getOrDefault("a", TypeLiteral.of(String.class), null));
    }

    @Test(expected = NullPointerException.class)
    public void getOrDefaultDoesNotAcceptNullAsTargetTypeForThreeParameterVariantSecondIsTypeLiteral() {
        DefaultConfiguration c = new DefaultConfiguration(new DummyConfigurationContext());

        c.getOrDefault("a", (TypeLiteral<String>) null, "b");
    }

    /**
     * Tests for getOrDefault(String, String)
     */
    @Test(expected = NullPointerException.class)
    public void getOrDefaultDoesNotAcceptNullAsKeyForTwoParameterVariantDefaultValueIsSecond() {
        DefaultConfiguration c = new DefaultConfiguration(new DummyConfigurationContext());

        c.getOrDefault(null, "ok");
    }

    @Test(expected = NullPointerException.class)
    public void getOrDefaultDoesNotAcceptNullAsDefaultValueForTwoParameterVariantDefaultValueIsSecond() {
        DefaultConfiguration c = new DefaultConfiguration(new DummyConfigurationContext());

        c.getOrDefault("a", null);
    }

    @Test(expected = NullPointerException.class)
    public void with_Null() {
        DefaultConfiguration c = new DefaultConfiguration(new DummyConfigurationContext());

        c.with(null);
    }

    @Test(expected = NullPointerException.class)
    public void query_Null() {
        DefaultConfiguration c = new DefaultConfiguration(new DummyConfigurationContext());

        c.query(null);
    }

    @Test
    public void with() {
        DefaultConfiguration c = new DefaultConfiguration(new DummyConfigurationContext());
        assertEquals(c.with(config -> config), c);
    }

    @Test
    public void query() {
        DefaultConfiguration c = new DefaultConfiguration(new DummyConfigurationContext());
        assertEquals(c.query(config -> "testQ"), "testQ");
    }

    public static class DummyConfigurationContext implements ConfigurationContext {
        @Override
        public void addPropertySources(PropertySource... propertySources) {
            throw new RuntimeException("Method should be never called in this test");
        }

        @Override
        public List<PropertySource> getPropertySources() {
            return Collections.emptyList();
        }

        @Override
        public PropertySource getPropertySource(String name) {
            throw new RuntimeException("Method should be never called in this test");
        }

        @Override
        public <T> void addPropertyConverter(TypeLiteral<T> type, PropertyConverter<T> propertyConverter) {
            throw new RuntimeException("Method should be never called in this test");
        }

        @Override
        public Map<TypeLiteral<?>, List<PropertyConverter<?>>> getPropertyConverters() {
            return Collections.emptyMap();
        }

        @Override
        public <T> List<PropertyConverter<T>> getPropertyConverters(TypeLiteral<T> type) {
            return Collections.emptyList();
        }

        @Override
        public List<PropertyFilter> getPropertyFilters() {
            return Collections.emptyList();
        }

        @Override
        public PropertyValueCombinationPolicy getPropertyValueCombinationPolicy() {
            throw new RuntimeException("Method should be never called in this test");
        }

        @Override
        public ConfigurationContextBuilder toBuilder() {
            throw new RuntimeException("Method should be never called in this test");
        }
    }
}