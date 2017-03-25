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

import org.apache.tamaya.TypeLiteral;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Tests for {@link FilterContext}.
 */
public class FilterContextTest {

    @Test(expected = NullPointerException.class)
    public void constructorRequiresNonNullPropertyValueTwoParameterVariant() {
        new FilterContext(null, new TestConfigContext());
    }

    @Test(expected = NullPointerException.class)
    public void constructorRequiresNonNullConfigurationContextTwoParameterVariant() {
        new FilterContext(PropertyValue.of("a", "b", "s"), null);
    }

    @Test(expected = NullPointerException.class)
    public void constructorRequiresNonNullPropertyValueThreeParameterVariant() {
        new FilterContext(null, Collections.EMPTY_MAP, new TestConfigContext());
    }

    @Test(expected = NullPointerException.class)
    public void constructorRequiresNonNullConfigurationContextThreeParameterVariant() {
        new FilterContext(PropertyValue.of("a", "b", "s"), Collections.EMPTY_MAP, null);
    }

    @Test(expected = NullPointerException.class)
    public void constructorRequiresNonNullMapForConfigEntriesThreeParameterVariant() {
        new FilterContext(PropertyValue.of("a", "b", "s"), null, new TestConfigContext());
    }

    @Test
    public void getKey() throws Exception {
        PropertyValue val = PropertyValue.of("getKey", "v", "");
        FilterContext ctx = new FilterContext(val,
                new HashMap<String,PropertyValue>(), new TestConfigContext());
        assertEquals(val, ctx.getProperty());
    }

    @Test
    public void isSinglePropertyScoped() throws Exception {
        PropertyValue val = PropertyValue.of("isSinglePropertyScoped", "v", "");
        FilterContext ctx = new FilterContext(val, new HashMap<String,PropertyValue>(), new TestConfigContext());
        assertEquals(false, ctx.isSinglePropertyScoped());
        ctx = new FilterContext(val, new TestConfigContext());
        assertEquals(true, ctx.isSinglePropertyScoped());
    }

    @Test
    public void getConfigEntries() throws Exception {
        Map<String,PropertyValue> config = new HashMap<>();
        for(int i=0;i<10;i++) {
            config.put("key-"+i, PropertyValue.of("key-"+i, "value-"+i, "test"));
        }
        PropertyValue val = PropertyValue.of("getConfigEntries", "v", "");
        FilterContext ctx = new FilterContext(val, config, new TestConfigContext());
        assertEquals(config, ctx.getConfigEntries());
        assertTrue(config != ctx.getConfigEntries());
    }

    @Test
    public void testToString() throws Exception {
        Map<String,PropertyValue> config = new HashMap<>();
        for(int i=0;i<2;i++) {
            config.put("key-"+i, PropertyValue.of("key-"+i, "value-"+i, "test"));
        }
        PropertyValue val = PropertyValue.of("testToString", "val", "mySource");
        FilterContext ctx = new FilterContext(val, config, new TestConfigContext());
        String toString = ctx.toString();

        assertNotNull(toString);
        assertTrue(toString.contains("FilterContext{value='PropertyValue{key='testToString', value='val', " +
                                     "source='mySource'}', configEntries=["));
        assertTrue(toString.contains("key-0"));
        assertTrue(toString.contains("key-1"));
        assertTrue(toString.endsWith("}"));
    }

    private static class TestConfigContext implements ConfigurationContext{

        @Override
        public void addPropertySources(PropertySource... propertySources) {

        }

        @Override
        public List<PropertySource> getPropertySources() {
            return null;
        }

        @Override
        public PropertySource getPropertySource(String name) {
            return null;
        }

        @Override
        public <T> void addPropertyConverter(TypeLiteral<T> type, PropertyConverter<T> propertyConverter) {

        }

        @Override
        public Map<TypeLiteral<?>, List<PropertyConverter<?>>> getPropertyConverters() {
            return null;
        }

        @Override
        public <T> List<PropertyConverter<T>> getPropertyConverters(TypeLiteral<T> type) {
            return null;
        }

        @Override
        public List<PropertyFilter> getPropertyFilters() {
            return null;
        }

        @Override
        public PropertyValueCombinationPolicy getPropertyValueCombinationPolicy() {
            return null;
        }

        @Override
        public ConfigurationContextBuilder toBuilder() {
            return null;
        }
    }

}