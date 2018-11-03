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

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.util.Collection;
import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;
import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.spi.*;
import org.junit.Test;

import java.util.Collections;
import java.util.Currency;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests for {@link CoreConfigurationBuilder} by atsticks on 06.09.16.
 */
public class CoreConfigurationBuilderTest {

    private TestPropertySource testPropertySource = new TestPropertySource(){};

    @Test
    public void setContext() throws Exception {
        ConfigurationContext context = Configuration.current().getContext();
        ConfigurationBuilder b = new CoreConfigurationBuilder()
                .setContext(context);
        assertThat(b.build().getContext()).isEqualTo(context);
    }

    @Test
    public void setConfiguration() throws Exception {
        Configuration cfg = Configuration.current();
        ConfigurationBuilder b = new CoreConfigurationBuilder()
                .setConfiguration(cfg);
        assertThat(b.build()).isEqualTo(cfg);
    }

    
    @Test
    public void addPropertySources_Array() throws Exception {
        PropertySource testPS2 = new TestPropertySource("addPropertySources_Array_2");
        ConfigurationBuilder b = new CoreConfigurationBuilder()
                .addPropertySources(testPropertySource, testPS2);
        Configuration cfg = b.build();
        ConfigurationContext ctx = cfg.getContext();
        assertThat(ctx.getPropertySources()).hasSize(2);
        assertThat(ctx.getPropertySources().contains(testPropertySource)).isTrue();
        assertThat(ctx.getPropertySources().contains(testPS2)).isTrue();
    }

    @Test
    public void removePropertySources_Array() throws Exception {
        PropertySource testPS2 = new TestPropertySource("addPropertySources_Array_2");
        ConfigurationBuilder b = new CoreConfigurationBuilder()
                .addPropertySources(testPropertySource, testPS2);
        Configuration cfg = b.build();
        ConfigurationContext ctx = cfg.getContext();
        assertThat(ctx.getPropertySources()).hasSize(2);
        assertThat(ctx.getPropertySources().contains(testPropertySource)).isTrue();
        assertThat(ctx.getPropertySources().contains(testPS2)).isTrue();
        b = new CoreConfigurationBuilder()
                .addPropertySources(testPropertySource, testPS2);
        b.removePropertySources(testPropertySource);
        cfg = b.build();
        ctx = cfg.getContext();
        assertThat(ctx.getPropertySources()).hasSize(1);
        assertThat(ctx.getPropertySources().contains(testPropertySource)).isFalse();
        assertThat(ctx.getPropertySources().contains(testPS2)).isTrue();
    }

    @Test
    public void addPropertyFilters_Array() throws Exception {
        PropertyFilter filter1 = (value, ctx) -> value;
        PropertyFilter filter2 = (value, ctx) -> value;
        CoreConfigurationBuilder b = new CoreConfigurationBuilder();
        b.addPropertyFilters(filter1, filter2);
        Configuration cfg = b.build();
        ConfigurationContext ctx = cfg.getContext();
        assertThat(ctx.getPropertyFilters().contains(filter1)).isTrue();
        assertThat(ctx.getPropertyFilters().contains(filter2)).isTrue();
        assertThat(ctx.getPropertyFilters()).hasSize(2);
        b = new CoreConfigurationBuilder();
        b.addPropertyFilters(filter1, filter2);
        b.addPropertyFilters(filter1, filter2);
        assertThat(ctx.getPropertyFilters()).hasSize(2);
    }

    @Test
    public void removePropertyFilters_Array() throws Exception {
        PropertyFilter filter1 = (value, ctx) -> value;
        PropertyFilter filter2 = (value, ctx) -> value;
        ConfigurationBuilder b = new CoreConfigurationBuilder()
                .addPropertyFilters(filter1, filter2);
        Configuration cfg = b.build();
        ConfigurationContext ctx = cfg.getContext();
        assertThat(ctx.getPropertyFilters().contains(filter1)).isTrue();
        assertThat(ctx.getPropertyFilters().contains(filter2)).isTrue();
        assertThat(ctx.getPropertyFilters()).hasSize(2);
        b = new CoreConfigurationBuilder()
                .addPropertyFilters(filter1, filter2);
        b.removePropertyFilters(filter1);
        cfg = b.build();
        ctx = cfg.getContext();
        assertThat(ctx.getPropertyFilters()).hasSize(1);
        assertThat(ctx.getPropertyFilters().contains(filter1)).isFalse();
        assertThat(ctx.getPropertyFilters().contains(filter2)).isTrue();
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void addPropertyConverter() throws Exception {
		PropertyConverter converter = (value, ctx) -> value.toLowerCase();
		ConfigurationBuilder b = new CoreConfigurationBuilder()
                .addPropertyConverters(TypeLiteral.of(String.class), converter);
        Configuration cfg = b.build();
        ConfigurationContext ctx = cfg.getContext();
        assertThat(ctx.getPropertyConverters(TypeLiteral.of(String.class)).contains(converter)).isTrue();
        assertThat(ctx.getPropertyConverters()).hasSize(1);
        b = new CoreConfigurationBuilder()
                .addPropertyConverters(TypeLiteral.of(String.class), converter);
        b.addPropertyConverters(TypeLiteral.of(String.class), converter);
        assertThat(ctx.getPropertyConverters()).hasSize(1);
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void removePropertyConverters_Array() throws Exception {
        PropertyConverter converter = (value, ctx) -> value.toLowerCase();
        ConfigurationBuilder b = new CoreConfigurationBuilder()
                .addPropertyConverters(TypeLiteral.of(String.class), converter);
        Configuration cfg = b.build();
        ConfigurationContext ctx = cfg.getContext();
        assertThat(ctx.getPropertyConverters(TypeLiteral.of(String.class)).contains(converter)).isTrue();
        assertThat(ctx.getPropertyConverters(TypeLiteral.of(String.class))).hasSize(1);
        b = new CoreConfigurationBuilder()
                .addPropertyConverters(TypeLiteral.of(String.class), converter);
        b.removePropertyConverters(TypeLiteral.of(String.class), converter);
        cfg = b.build();
        ctx = cfg.getContext();
        assertThat(ctx.getPropertyConverters(TypeLiteral.of(String.class)).contains(converter)).isFalse();
        assertThat(ctx.getPropertyConverters(TypeLiteral.of(String.class)).isEmpty()).isTrue();
    }

    @Test
    public void setPropertyValueCombinationPolicy() throws Exception {
        PropertyValueCombinationPolicy combPol = (currentValue, key, propertySource) -> currentValue;
        ConfigurationBuilder b = new CoreConfigurationBuilder()
                .setPropertyValueCombinationPolicy(combPol);
        Configuration cfg = b.build();
        ConfigurationContext ctx = cfg.getContext();
        assertThat(combPol).isEqualTo(ctx.getPropertyValueCombinationPolicy());
    }

    @Test
    public void build() throws Exception {
        assertThat(new CoreConfigurationBuilder().build()).isNotNull();
    }

    @Test
    public void addDefaultPropertyConverters() throws Exception {
        ConfigurationBuilder builder = ConfigurationProvider.getConfigurationBuilder();
        builder.addDefaultPropertyConverters();
    }
    
    @Test
    public void addCorePropertyConverters() throws Exception {
        CoreConfigurationBuilder b = new CoreConfigurationBuilder();
        b.addCorePropertyConverters();
        Map<TypeLiteral<?>, Collection<PropertyConverter<?>>> converters = b.getPropertyConverter();
        assertThat(converters.containsKey(TypeLiteral.<BigDecimal>of(BigDecimal.class))).isTrue();
        assertThat(converters.containsKey(TypeLiteral.<BigInteger>of(BigInteger.class))).isTrue();
        assertThat(converters.containsKey(TypeLiteral.<Boolean>of(Boolean.class))).isTrue();
        assertThat(converters.containsKey(TypeLiteral.<Byte>of(Byte.class))).isTrue();
        assertThat(converters.containsKey(TypeLiteral.<Character>of(Character.class))).isTrue();
        assertThat(converters.containsKey(TypeLiteral.<Class<?>>of(Class.class))).isTrue();
        assertThat(converters.containsKey(TypeLiteral.<Currency>of(Currency.class))).isTrue();
        assertThat(converters.containsKey(TypeLiteral.<Double>of(Double.class))).isTrue();
        assertThat(converters.containsKey(TypeLiteral.<File>of(File.class))).isTrue();
        assertThat(converters.containsKey(TypeLiteral.<Float>of(Float.class))).isTrue();
        assertThat(converters.containsKey(TypeLiteral.<Integer>of(Integer.class))).isTrue();
        assertThat(converters.containsKey(TypeLiteral.<Long>of(Long.class))).isTrue();
        assertThat(converters.containsKey(TypeLiteral.<Number>of(Number.class))).isTrue();
        assertThat(converters.containsKey(TypeLiteral.<Path>of(Path.class))).isTrue();
        assertThat(converters.containsKey(TypeLiteral.<Short>of(Short.class))).isTrue();
        assertThat(converters.containsKey(TypeLiteral.<URI>of(URI.class))).isTrue();
        assertThat(converters.containsKey(TypeLiteral.<URL>of(URL.class))).isTrue();
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