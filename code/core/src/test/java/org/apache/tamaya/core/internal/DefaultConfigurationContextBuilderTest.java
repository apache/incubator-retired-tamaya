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

import org.apache.tamaya.ConfigurationProvider;
import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.spi.*;
import org.junit.Ignore;
import org.junit.Test;

import javax.annotation.Priority;
import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Tests for {@link DefaultConfigurationContextBuilder} by atsticks on 06.09.16.
 */
public class DefaultConfigurationContextBuilderTest {

    private TestPropertySource testPropertySource = new TestPropertySource(){};

    @Test
    public void setContext() throws Exception {
        ConfigurationContext context = ConfigurationProvider.getConfiguration().getContext();
        ConfigurationContextBuilder b = new DefaultConfigurationContextBuilder()
                .setContext(context);
        assertEquals(context, b.build());
    }

    @Test
    public void addPropertySources_Array() throws Exception {
        PropertySource testPS2 = new TestPropertySource("addPropertySources_Array_2");
        ConfigurationContextBuilder b = new DefaultConfigurationContextBuilder()
                .addPropertySources(testPropertySource, testPS2);
        ConfigurationContext ctx = b.build();
        assertTrue(ctx.getPropertySources().size()==2);
        assertTrue(ctx.getPropertySources().contains(testPropertySource));
        assertTrue(ctx.getPropertySources().contains(testPS2));
    }

    @Test
    public void removePropertySources_Array() throws Exception {
        PropertySource testPS2 = new TestPropertySource("addPropertySources_Array_2");
        ConfigurationContextBuilder b = new DefaultConfigurationContextBuilder()
                .addPropertySources(testPropertySource, testPS2);
        ConfigurationContext ctx = b.build();
        assertTrue(ctx.getPropertySources().size()==2);
        assertTrue(ctx.getPropertySources().contains(testPropertySource));
        assertTrue(ctx.getPropertySources().contains(testPS2));
        b = new DefaultConfigurationContextBuilder()
                .addPropertySources(testPropertySource, testPS2);
        b.removePropertySources(testPropertySource);
        ctx = b.build();
        assertTrue(ctx.getPropertySources().size()==1);
        assertFalse(ctx.getPropertySources().contains(testPropertySource));
        assertTrue(ctx.getPropertySources().contains(testPS2));
    }

    @Test
    public void addPropertyFilters_Array() throws Exception {
        PropertyFilter filter1 = new PropertyFilter(){
            @Override
            public PropertyValue filterProperty(PropertyValue value, FilterContext context) {
                return value;
            }
        };
        PropertyFilter filter2 = new PropertyFilter(){
            @Override
            public PropertyValue filterProperty(PropertyValue value, FilterContext context) {
                return value;
            }
        };
        DefaultConfigurationContextBuilder b = new DefaultConfigurationContextBuilder();
        b.addPropertyFilters(filter1, filter2);
        ConfigurationContext ctx = b.build();
        assertTrue(ctx.getPropertyFilters().contains(filter1));
        assertTrue(ctx.getPropertyFilters().contains(filter2));
        assertTrue(ctx.getPropertyFilters().size()==2);
        b = new DefaultConfigurationContextBuilder();
        b.addPropertyFilters(filter1, filter2);
        b.addPropertyFilters(filter1, filter2);
        assertTrue(ctx.getPropertyFilters().size()==2);
    }

    @Test
    public void removePropertyFilters_Array() throws Exception {
        PropertyFilter filter1 = new PropertyFilter(){
            @Override
            public PropertyValue filterProperty(PropertyValue value, FilterContext context) {
                return value;
            }
        };
        PropertyFilter filter2 = new PropertyFilter(){
            @Override
            public PropertyValue filterProperty(PropertyValue value, FilterContext context) {
                return value;
            }
        };
        ConfigurationContextBuilder b = new DefaultConfigurationContextBuilder()
                .addPropertyFilters(filter1, filter2);
        ConfigurationContext ctx = b.build();
        assertTrue(ctx.getPropertyFilters().contains(filter1));
        assertTrue(ctx.getPropertyFilters().contains(filter2));
        assertTrue(ctx.getPropertyFilters().size()==2);
        b = new DefaultConfigurationContextBuilder()
                .addPropertyFilters(filter1, filter2);
        b.removePropertyFilters(filter1);
        ctx = b.build();
        assertTrue(ctx.getPropertyFilters().size()==1);
        assertFalse(ctx.getPropertyFilters().contains(filter1));
        assertTrue(ctx.getPropertyFilters().contains(filter2));
    }

    @Test
    public void addPropertyConverter() throws Exception {
        PropertyConverter converter = new PropertyConverter(){

            @Override
            public Object convert(String value, ConversionContext context) {
                return value.toLowerCase();
            }
        };
        ConfigurationContextBuilder b = new DefaultConfigurationContextBuilder()
                .addPropertyConverters(TypeLiteral.of(String.class), converter);
        ConfigurationContext ctx = b.build();
        assertTrue(ctx.getPropertyConverters(TypeLiteral.of(String.class)).contains(converter));
        assertEquals(ctx.getPropertyConverters().size(), 1);
        b = new DefaultConfigurationContextBuilder()
                .addPropertyConverters(TypeLiteral.of(String.class), converter);
        b.addPropertyConverters(TypeLiteral.of(String.class), converter);
        assertEquals(ctx.getPropertyConverters().size(), 1);
    }

    @Test
    public void removePropertyConverters_Array() throws Exception {
        PropertyConverter converter = new PropertyConverter(){

            @Override
            public Object convert(String value, ConversionContext context) {
                return value.toLowerCase();
            }
        };
        ConfigurationContextBuilder b = new DefaultConfigurationContextBuilder()
                .addPropertyConverters(TypeLiteral.of(String.class), converter);
        ConfigurationContext ctx = b.build();
        assertTrue(ctx.getPropertyConverters(TypeLiteral.of(String.class)).contains(converter));
        assertEquals(ctx.getPropertyConverters(TypeLiteral.of(String.class)).size(), 1);
        b = new DefaultConfigurationContextBuilder()
                .addPropertyConverters(TypeLiteral.of(String.class), converter);
        b.removePropertyConverters(TypeLiteral.of(String.class), converter);
        ctx = b.build();
        assertFalse(ctx.getPropertyConverters(TypeLiteral.of(String.class)).contains(converter));
        assertEquals(ctx.getPropertyConverters(TypeLiteral.of(String.class)).size(), 0);
    }

    @Test
    public void setPropertyValueCombinationPolicy() throws Exception {
        PropertyValueCombinationPolicy combPol = new PropertyValueCombinationPolicy(){

            @Override
            public PropertyValue collect(PropertyValue currentValue, String key, PropertySource propertySource) {
                return currentValue;
            }

        };
        ConfigurationContextBuilder b = new DefaultConfigurationContextBuilder()
                .setPropertyValueCombinationPolicy(combPol);
        ConfigurationContext ctx = b.build();
        assertEquals(ctx.getPropertyValueCombinationPolicy(), combPol);
    }

    @Test
    public void build() throws Exception {
        ConfigurationContextBuilder b = new DefaultConfigurationContextBuilder();
        assertNotNull(b.build());
    }

    @Test
    public void bla() throws Exception {
        ConfigurationContextBuilder builder = ConfigurationProvider.getConfigurationContextBuilder();
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