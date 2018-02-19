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
package org.apache.tamaya.base;

import org.apache.tamaya.base.filter.Filter;
import org.junit.Test;

import javax.config.spi.ConfigBuilder;
import javax.config.spi.ConfigProviderResolver;
import javax.config.spi.ConfigSource;
import javax.config.spi.Converter;
import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Tests for {@link  DefaultConfigBuilder} by atsticks on 06.09.16.
 */
public class DefaultConfigBuilderTest {

    private TestPropertySource testPropertySource = new TestPropertySource(){};


    @Test
    public void addPropertySources_Array() throws Exception {
        ConfigSource testPS2 = new TestPropertySource("addPropertySources_Array_2");
        TamayaConfigBuilder b = new DefaultConfigBuilder()
                .withSources(testPropertySource, testPS2);
        ConfigContext ctx = b.getConfigContext();
        assertEquals(2, ctx.getConfigSources().size());
        assertTrue(ctx.getConfigSources().contains(testPropertySource));
        assertTrue(ctx.getConfigSources().contains(testPS2));
    }

    @Test
    public void removePropertySources_Array() throws Exception {
        ConfigSource testPS2 = new TestPropertySource("addPropertySources_Array_2");
        TamayaConfigBuilder b = new DefaultConfigBuilder()
                .withSources(testPropertySource, testPS2);
        ConfigContext ctx = b.getConfigContext();
        assertEquals(2, ctx.getConfigSources().size());
        assertTrue(ctx.getConfigSources().contains(testPropertySource));
        assertTrue(ctx.getConfigSources().contains(testPS2));
        b = new DefaultConfigBuilder()
                .withSources(testPropertySource, testPS2);
        b.removeSources(testPropertySource);
        ctx = b.getConfigContext();
        assertEquals(1, ctx.getConfigSources().size());
        assertFalse(ctx.getConfigSources().contains(testPropertySource));
        assertTrue(ctx.getConfigSources().contains(testPS2));
    }

    @Test
    public void addPropertyFilters_Array() throws Exception {
        Filter filter1 = (key,value) -> value;
        Filter filter2 = (key,value) -> value;
        DefaultConfigBuilder b = new DefaultConfigBuilder();
        b.withFilters(filter1, filter2);
        ConfigContext ctx = b.getConfigContext();
        assertTrue(ctx.getFilters().contains(filter1));
        assertTrue(ctx.getFilters().contains(filter2));
        assertEquals(2, ctx.getFilters().size());
        b = new DefaultConfigBuilder();
        b.withFilters(filter1, filter2);
        b.withFilters(filter1, filter2);
        assertEquals(2, ctx.getFilters().size());
    }

    @Test
    public void removePropertyFilters_Array() throws Exception {
        Filter filter1 = (key,value) -> value;
        Filter filter2 = (key,value) -> value;
        TamayaConfigBuilder b = new DefaultConfigBuilder()
                .withFilters(filter1, filter2);
        ConfigContext ctx = b.getConfigContext();
        assertTrue(ctx.getFilters().contains(filter1));
        assertTrue(ctx.getFilters().contains(filter2));
        assertEquals(2, ctx.getFilters().size());
        b = new DefaultConfigBuilder()
                .withFilters(filter1, filter2);
        b.removeFilters(filter1);
        ctx = b.getConfigContext();
        assertEquals(1, ctx.getFilters().size());
        assertFalse(ctx.getFilters().contains(filter1));
        assertTrue(ctx.getFilters().contains(filter2));
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void addPropertyConverter() throws Exception {
		Converter converter = (value) -> value.toLowerCase();
		TamayaConfigBuilder b = new DefaultConfigBuilder()
                .withConverters(String.class, converter);
        ConfigContext ctx = b.getConfigContext();
        assertTrue(ctx.getConverters(String.class).contains(converter));
        assertEquals(1, ctx.getConverters().size());
        b = new DefaultConfigBuilder()
                .withConverters(String.class, converter);
        b.withConverters(String.class, converter);
        assertEquals(1, ctx.getConverters().size());
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void removePropertyConverters_Array() throws Exception {
        Converter converter = (value) -> value.toLowerCase();
        TamayaConfigBuilder b = new DefaultConfigBuilder()
                .withConverters(String.class, converter);
        ConfigContext ctx = b.getConfigContext();
        assertTrue(ctx.getConverters(String.class).contains(converter));
        assertEquals(1, ctx.getConverters().get(String.class).size());
        b = new DefaultConfigBuilder()
                .withConverters(String.class, converter);
        b.removeConverters(String.class, converter);
        ctx = b.getConfigContext();
        assertFalse(ctx.getConverters(String.class).contains(converter));
        assertTrue(ctx.getConverters(String.class).isEmpty());
    }

    @Test
    public void setPropertyValueCombinationPolicy() throws Exception {
        ConfigValueCombinationPolicy combPol = (currentValue, key, propertySource) -> currentValue;
        TamayaConfigBuilder b = new DefaultConfigBuilder()
                .withPropertyValueCombinationPolicy(combPol);
        ConfigContext ctx = b.getConfigContext();
        assertEquals(ctx.getConfigValueCombinationPolicy(), combPol);
    }

    @Test
    public void build() throws Exception {
        assertNotNull(new DefaultConfigBuilder().build());
    }

    @Test
    public void addDiscoveredConverters() throws Exception {
        ConfigBuilder builder = ConfigProviderResolver.instance().getBuilder();
        builder.addDiscoveredConverters();
    }

    private static class TestPropertySource implements ConfigSource{

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
        public String getValue(String key) {
            return key + "Value";
        }

        @Override
        public Map<String, String> getProperties() {
            return Collections.emptyMap();
        }

    }

}