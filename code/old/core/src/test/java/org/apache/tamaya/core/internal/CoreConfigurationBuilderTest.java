/*
 * Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.apache.tamaya.core.internal;

import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;
import org.apache.tamaya.spi.TypeLiteral;
import org.apache.tamaya.spi.*;
import org.junit.Test;

import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Tests for {@link CoreConfigurationBuilder} by atsticks on 06.09.16.
 */
public class CoreConfigurationBuilderTest {

    private TestPropertySource testPropertySource = new TestPropertySource(){};

    @Test
    public void setContext() throws Exception {
        ConfigurationContext context = ConfigurationProvider.getConfiguration().getContext();
        ConfigurationBuilder b = new CoreConfigurationBuilder()
                .setContext(context);
        assertEquals(context, b.build().getContext());
    }

    @Test
    public void setConfiguration() throws Exception {
        Configuration cfg = ConfigurationProvider.getConfiguration();
        ConfigurationBuilder b = new CoreConfigurationBuilder()
                .setConfiguration(cfg);
        assertEquals(cfg, b.build());
    }

    @Test
    public void addPropertySources_Array() throws Exception {
        PropertySource testPS2 = new TestPropertySource("addPropertySources_Array_2");
        ConfigurationBuilder b = new CoreConfigurationBuilder()
                .addPropertySources(testPropertySource, testPS2);
        Configuration cfg = b.build();
        ConfigurationContext ctx = cfg.getContext();
        assertEquals(2, ctx.getPropertySources().size());
        assertTrue(ctx.getPropertySources().contains(testPropertySource));
        assertTrue(ctx.getPropertySources().contains(testPS2));
    }

    @Test
    public void removePropertySources_Array() throws Exception {
        PropertySource testPS2 = new TestPropertySource("addPropertySources_Array_2");
        ConfigurationBuilder b = new CoreConfigurationBuilder()
                .addPropertySources(testPropertySource, testPS2);
        Configuration cfg = b.build();
        ConfigurationContext ctx = cfg.getContext();
        assertEquals(2, ctx.getPropertySources().size());
        assertTrue(ctx.getPropertySources().contains(testPropertySource));
        assertTrue(ctx.getPropertySources().contains(testPS2));
        b = new CoreConfigurationBuilder()
                .addPropertySources(testPropertySource, testPS2);
        b.removePropertySources(testPropertySource);
        cfg = b.build();
        ctx = cfg.getContext();
        assertEquals(1, ctx.getPropertySources().size());
        assertFalse(ctx.getPropertySources().contains(testPropertySource));
        assertTrue(ctx.getPropertySources().contains(testPS2));
    }

    @Test
    public void addPropertyFilters_Array() throws Exception {
        PropertyFilter filter1 = (value, context) -> value;
        PropertyFilter filter2 = (value, context) -> value;
        CoreConfigurationBuilder b = new CoreConfigurationBuilder();
        b.addPropertyFilters(filter1, filter2);
        Configuration cfg = b.build();
        ConfigurationContext ctx = cfg.getContext();
        assertTrue(ctx.getPropertyFilters().contains(filter1));
        assertTrue(ctx.getPropertyFilters().contains(filter2));
        assertEquals(2, ctx.getPropertyFilters().size());
        b = new CoreConfigurationBuilder();
        b.addPropertyFilters(filter1, filter2);
        b.addPropertyFilters(filter1, filter2);
        assertEquals(2, ctx.getPropertyFilters().size());
    }

    @Test
    public void removePropertyFilters_Array() throws Exception {
        PropertyFilter filter1 = (value, context) -> value;
        PropertyFilter filter2 = (value, context) -> value;
        ConfigurationBuilder b = new CoreConfigurationBuilder()
                .addPropertyFilters(filter1, filter2);
        Configuration cfg = b.build();
        ConfigurationContext ctx = cfg.getContext();
        assertTrue(ctx.getPropertyFilters().contains(filter1));
        assertTrue(ctx.getPropertyFilters().contains(filter2));
        assertEquals(2, ctx.getPropertyFilters().size());
        b = new CoreConfigurationBuilder()
                .addPropertyFilters(filter1, filter2);
        b.removePropertyFilters(filter1);
        cfg = b.build();
        ctx = cfg.getContext();
        assertEquals(1, ctx.getPropertyFilters().size());
        assertFalse(ctx.getPropertyFilters().contains(filter1));
        assertTrue(ctx.getPropertyFilters().contains(filter2));
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void addPropertyConverter() throws Exception {
		PropertyConverter converter = (value, context) -> value.toLowerCase();
		ConfigurationBuilder b = new CoreConfigurationBuilder()
                .addPropertyConverters(TypeLiteral.of(String.class), converter);
        Configuration cfg = b.build();
        ConfigurationContext ctx = cfg.getContext();
        assertTrue(ctx.getPropertyConverters(TypeLiteral.of(String.class)).contains(converter));
        assertEquals(1, ctx.getPropertyConverters().size());
        b = new CoreConfigurationBuilder()
                .addPropertyConverters(TypeLiteral.of(String.class), converter);
        b.addPropertyConverters(TypeLiteral.of(String.class), converter);
        assertEquals(1, ctx.getPropertyConverters().size());
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void removePropertyConverters_Array() throws Exception {
        PropertyConverter converter = (value, context) -> value.toLowerCase();
        ConfigurationBuilder b = new CoreConfigurationBuilder()
                .addPropertyConverters(TypeLiteral.of(String.class), converter);
        Configuration cfg = b.build();
        ConfigurationContext ctx = cfg.getContext();
        assertTrue(ctx.getPropertyConverters(TypeLiteral.of(String.class)).contains(converter));
        assertEquals(1, ctx.getPropertyConverters(TypeLiteral.of(String.class)).size());
        b = new CoreConfigurationBuilder()
                .addPropertyConverters(TypeLiteral.of(String.class), converter);
        b.removePropertyConverters(TypeLiteral.of(String.class), converter);
        cfg = b.build();
        ctx = cfg.getContext();
        assertFalse(ctx.getPropertyConverters(TypeLiteral.of(String.class)).contains(converter));
        assertTrue(ctx.getPropertyConverters(TypeLiteral.of(String.class)).isEmpty());
    }

    @Test
    public void setPropertyValueCombinationPolicy() throws Exception {
        PropertyValueCombinationPolicy combPol = (currentValue, key, propertySource) -> currentValue;
        ConfigurationBuilder b = new CoreConfigurationBuilder()
                .setPropertyValueCombinationPolicy(combPol);
        Configuration cfg = b.build();
        ConfigurationContext ctx = cfg.getContext();
        assertEquals(ctx.getPropertyValueCombinationPolicy(), combPol);
    }

    @Test
    public void build() throws Exception {
        assertNotNull(new CoreConfigurationBuilder().build());
    }

    @Test
    public void bla() throws Exception {
        ConfigurationBuilder builder = ConfigurationProvider.getConfigurationBuilder();
        builder.addDefaultPropertyConverters();
    }

    private static class TestPropertySource implements PropertySource{

        private String id;

        public TestPropertySource(){
            this(null);
        }

        public TestPropertySource(String id){
            this.id = id;
        }

        @Override
        public int getOrdinal() {
            return 200;
        }

        @Override
        public String getName() {
            return id!=null?id:"TestPropertySource";
        }

        @Override
        public PropertyValue get(String key) {
            return PropertyValue.of(key, key + "Value", getName());
        }

        @Override
        public Map<String, PropertyValue> getProperties() {
            return Collections.emptyMap();
        }

        @Override
        public boolean isScannable() {
            return false;
        }
    }

}