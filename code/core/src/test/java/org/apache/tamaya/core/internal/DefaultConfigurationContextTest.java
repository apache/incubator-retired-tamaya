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
package org.apache.tamaya.core.internal;

import org.apache.tamaya.ConfigurationProvider;
import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.core.testdata.TestPropertyDefaultSource;
import org.apache.tamaya.spi.*;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Simple tests for {@link DefaultConfigurationContext} by atsticks on 16.08.16.
 */
public class DefaultConfigurationContextTest {

    @Test
    public void addPropertySources() throws Exception {
        ConfigurationContext ctx = new DefaultConfigurationContextBuilder().build();
        TestPropertyDefaultSource def = new TestPropertyDefaultSource();
        assertFalse(ctx.getPropertySources().contains(def));
        ctx.addPropertySources(def);
        assertTrue(ctx.getPropertySources().contains(def));
    }

    @Test
    public void testToString() throws Exception {
        String toString = ConfigurationProvider.getConfiguration().getContext().toString();
        System.out.println(toString);
    }

    @Test
    public void getPropertySources() throws Exception {
        ConfigurationContext ctx = new DefaultConfigurationContextBuilder().build();
        assertNotNull(ctx.getPropertySources());
        assertEquals(ctx.getPropertySources().size(), 0);
        ctx = new DefaultConfigurationContextBuilder().addDefaultPropertySources().build();
        assertNotNull(ctx.getPropertySources());
        assertEquals(7, ctx.getPropertySources().size());
    }

    @Test
    public void getPropertySource() throws Exception {
        TestPropertyDefaultSource ps = new TestPropertyDefaultSource();
        ConfigurationContext ctx = new DefaultConfigurationContextBuilder()
                .addPropertySources(ps).build();
        assertNotNull(ctx.getPropertySources());
        assertEquals(ctx.getPropertySources().size(), 1);
        assertNotNull(((DefaultConfigurationContext)ctx).getPropertySource(ps.getName()));
        assertEquals(ps.getName(), ((DefaultConfigurationContext)ctx).getPropertySource(ps.getName()).getName());
        assertNull(((DefaultConfigurationContext)ctx).getPropertySource("huhu"));

    }

    @Test
    public void testHashCode() throws Exception {
        TestPropertyDefaultSource ps = new TestPropertyDefaultSource();
        ConfigurationContext ctx1 = new DefaultConfigurationContextBuilder()
                .addPropertySources(ps).build();
        ConfigurationContext ctx2 = new DefaultConfigurationContextBuilder()
                .addPropertySources(ps).build();
        assertEquals(ctx1.hashCode(), ctx2.hashCode());
        ctx2 = new DefaultConfigurationContextBuilder()
                .build();
        assertNotEquals(ctx1.hashCode(), ctx2.hashCode());

    }

    @Test
    public void addPropertyConverter() throws Exception {
        ConfigurationContext ctx = new DefaultConfigurationContextBuilder().build();
        PropertyConverter testConverter = new PropertyConverter() {
            @Override
            public Object convert(String value, ConversionContext context) {
                return "";
            }
        };
        assertFalse(ctx.getPropertyConverters(TypeLiteral.of(String.class)).contains(testConverter));
        ctx.addPropertyConverter(TypeLiteral.of(String.class), testConverter);
        assertTrue(ctx.getPropertyConverters(TypeLiteral.of(String.class)).contains(testConverter));
    }

    @Test
    public void getPropertyConverters() throws Exception {
        ConfigurationContext ctx = new DefaultConfigurationContextBuilder().build();
        PropertyConverter testConverter = new PropertyConverter() {
            @Override
            public Object convert(String value, ConversionContext context) {
                return "";
            }
        };
        ctx.addPropertyConverter(TypeLiteral.of(String.class), testConverter);
        assertNotNull(ctx.getPropertyConverters());
        assertTrue(ctx.getPropertyConverters().containsKey(TypeLiteral.of(String.class)));
        assertTrue(ctx.getPropertyConverters().get(TypeLiteral.of(String.class)).contains(testConverter));
        testConverter = new PropertyConverter() {
            @Override
            public Object convert(String value, ConversionContext context) {
                return Integer.valueOf(5);
            }
        };
        ctx.addPropertyConverter(TypeLiteral.of(Integer.class), testConverter);
        assertTrue(ctx.getPropertyConverters().containsKey(TypeLiteral.of(Integer.class)));
        assertTrue(ctx.getPropertyConverters().get(TypeLiteral.of(Integer.class)).contains(testConverter));
    }

    @Test
    public void getPropertyConverters1() throws Exception {
        ConfigurationContext ctx = new DefaultConfigurationContextBuilder().build();
        PropertyConverter testConverter = new PropertyConverter() {
            @Override
            public Object convert(String value, ConversionContext context) {
                return "";
            }
        };
        assertNotNull(ctx.getPropertyConverters(TypeLiteral.of(String.class)));
        assertEquals(ctx.getPropertyConverters(TypeLiteral.of(String.class)).size(),0);
        ctx.addPropertyConverter(TypeLiteral.of(String.class), testConverter);
        assertNotNull(ctx.getPropertyConverters(TypeLiteral.of(String.class)));
        assertEquals(ctx.getPropertyConverters(TypeLiteral.of(String.class)).size(),1);
        assertTrue(ctx.getPropertyConverters(TypeLiteral.of(String.class)).contains(testConverter));

    }

    @Test
    public void getPropertyFilters() throws Exception {
        ConfigurationContext ctx = new DefaultConfigurationContextBuilder().build();
        PropertyFilter testFilter = new PropertyFilter() {

            @Override
            public PropertyValue filterProperty(PropertyValue value, FilterContext context) {
                return value;
            }
        };
        assertNotNull(ctx.getPropertyFilters());
        assertFalse(ctx.getPropertyFilters().contains(testFilter));
        ctx = ctx.toBuilder().addPropertyFilters(testFilter).build();
        assertTrue(ctx.getPropertyFilters().contains(testFilter));
    }

    @Test
    public void getPropertyValueCombinationPolicy() throws Exception {
        ConfigurationContext ctx = new DefaultConfigurationContextBuilder().build();
        assertNotNull(ctx.getPropertyValueCombinationPolicy());
        assertEquals(ctx.getPropertyValueCombinationPolicy(),
                PropertyValueCombinationPolicy.DEFAULT_OVERRIDING_COLLECTOR);
    }

    @Test
    public void toBuilder() throws Exception {
        assertNotNull(new DefaultConfigurationContextBuilder().build().toBuilder());
    }

    @Test
    public void testRoundTrip() throws Exception {
        ConfigurationContext ctx = new DefaultConfigurationContextBuilder().build();
        assertEquals(ctx.toBuilder().build(), ctx);
    }

}