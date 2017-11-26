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

import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;
import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.core.testdata.TestPropertyDefaultSource;
import org.apache.tamaya.spi.*;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Simple tests for {@link CoreConfiguration} by atsticks on 16.08.16.
 */
public class CoreConfigurationTest {

    @Test
    public void addPropertySources() throws Exception {
        Configuration cfg = new CoreConfigurationBuilder().build();
        TestPropertyDefaultSource def = new TestPropertyDefaultSource();
        assertFalse(cfg.getContext().getPropertySources().contains(def));
        cfg.getContext().addPropertySources(def);
        assertTrue(cfg.getContext().getPropertySources().contains(def));
    }

    @Test
    public void testToString() throws Exception {
        String toString = ConfigurationProvider.getConfiguration().getContext().toString();
    }

    @Test
    public void getPropertySources() throws Exception {
        Configuration cfg = new CoreConfigurationBuilder().build();
        assertNotNull(cfg.getContext().getPropertySources());
        assertEquals(cfg.getContext().getPropertySources().size(), 0);
        cfg = new CoreConfigurationBuilder().addDefaultPropertySources().build();
        assertNotNull(cfg.getContext().getPropertySources());
        assertEquals(7, cfg.getContext().getPropertySources().size());
    }

    @Test
    public void getPropertySource() throws Exception {
        TestPropertyDefaultSource ps = new TestPropertyDefaultSource();
        Configuration cfg = new CoreConfigurationBuilder()
                .addPropertySources(ps).build();
        assertNotNull(cfg.getContext().getPropertySources());
        assertEquals(cfg.getContext().getPropertySources().size(), 1);
        assertNotNull((cfg.getContext()).getPropertySource(ps.getName()));
        assertEquals(ps.getName(), cfg.getContext().getPropertySource(ps.getName()).getName());
        assertNull(cfg.getContext().getPropertySource("huhu"));

    }

    @Test
    public void testHashCode() throws Exception {
        TestPropertyDefaultSource ps = new TestPropertyDefaultSource();
        Configuration cfg1 = new CoreConfigurationBuilder()
                .addPropertySources(ps).build();
        Configuration cfg2 = new CoreConfigurationBuilder()
                .addPropertySources(ps).build();
        assertEquals(cfg1.hashCode(), cfg2.hashCode());
        cfg2 = new CoreConfigurationBuilder()
                .build();
        assertNotEquals(cfg1.hashCode(), cfg2.hashCode());

    }

    @Test
    public void addPropertyConverter() throws Exception {
        Configuration cfg = new CoreConfigurationBuilder().build();
        PropertyConverter testConverter = new PropertyConverter() {
            @Override
            public Object convert(String value, ConversionContext context) {
                return "";
            }
        };
        assertFalse(cfg.getContext().getPropertyConverters(TypeLiteral.of(String.class)).contains(testConverter));
        cfg.getContext().addPropertyConverter(TypeLiteral.of(String.class), testConverter);
        assertTrue(cfg.getContext().getPropertyConverters(TypeLiteral.of(String.class)).contains(testConverter));
    }

    @Test
    public void getPropertyConverters() throws Exception {
        Configuration cfg = new CoreConfigurationBuilder().build();
        PropertyConverter testConverter = new PropertyConverter() {
            @Override
            public Object convert(String value, ConversionContext context) {
                return "";
            }
        };
        cfg.getContext().addPropertyConverter(TypeLiteral.of(String.class), testConverter);
        assertNotNull(cfg.getContext().getPropertyConverters());
        assertTrue(cfg.getContext().getPropertyConverters().containsKey(TypeLiteral.of(String.class)));
        assertTrue(cfg.getContext().getPropertyConverters().get(TypeLiteral.of(String.class)).contains(testConverter));
        testConverter = new PropertyConverter() {
            @Override
            public Object convert(String value, ConversionContext context) {
                return Integer.valueOf(5);
            }
        };
        cfg.getContext().addPropertyConverter(TypeLiteral.of(Integer.class), testConverter);
        assertTrue(cfg.getContext().getPropertyConverters().containsKey(TypeLiteral.of(Integer.class)));
        assertTrue(cfg.getContext().getPropertyConverters().get(TypeLiteral.of(Integer.class)).contains(testConverter));
    }

    @Test
    public void getPropertyConverters1() throws Exception {
        Configuration cfg = new CoreConfigurationBuilder().build();
        PropertyConverter testConverter = new PropertyConverter() {
            @Override
            public Object convert(String value, ConversionContext context) {
                return "";
            }
        };
        assertNotNull(cfg.getContext().getPropertyConverters(TypeLiteral.of(String.class)));
        assertEquals(cfg.getContext().getPropertyConverters(TypeLiteral.of(String.class)).size(),0);
        cfg.getContext().addPropertyConverter(TypeLiteral.of(String.class), testConverter);
        assertNotNull(cfg.getContext().getPropertyConverters(TypeLiteral.of(String.class)));
        assertEquals(cfg.getContext().getPropertyConverters(TypeLiteral.of(String.class)).size(),1);
        assertTrue(cfg.getContext().getPropertyConverters(TypeLiteral.of(String.class)).contains(testConverter));

    }

    @Test
    public void getPropertyFilters() throws Exception {
        Configuration cfg = new CoreConfigurationBuilder().build();
        PropertyFilter testFilter = new PropertyFilter() {

            @Override
            public PropertyValue filterProperty(PropertyValue value, FilterContext context) {
                return value;
            }
        };
        assertNotNull(cfg.getContext().getPropertyFilters());
        assertFalse(cfg.getContext().getPropertyFilters().contains(testFilter));
        cfg = cfg.toBuilder().addPropertyFilters(testFilter).build();
        assertTrue(cfg.getContext().getPropertyFilters().contains(testFilter));
    }

    @Test
    public void getPropertyValueCombinationPolicy() throws Exception {
        Configuration cfg = new CoreConfigurationBuilder().build();
        assertNotNull(cfg.getContext().getPropertyValueCombinationPolicy());
        assertEquals(cfg.getContext().getPropertyValueCombinationPolicy(),
                PropertyValueCombinationPolicy.DEFAULT_OVERRIDING_POLICY);
    }

    @Test
    public void toBuilder() throws Exception {
        assertNotNull(new CoreConfigurationBuilder().build().toBuilder());
    }

    @Test
    public void testRoundTrip() throws Exception {
        Configuration cfg = new CoreConfigurationBuilder().build();
        assertEquals(cfg.toBuilder().build(), cfg);
    }

}