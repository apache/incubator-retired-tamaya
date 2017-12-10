///*
// * Licensed to the Apache Software Foundation (ASF) under one
// *  or more contributor license agreements.  See the NOTICE file
// *  distributed with this work for additional information
// *  regarding copyright ownership.  The ASF licenses this file
// *  to you under the Apache License, Version 2.0 (the
// *  "License"); you may not use this file except in compliance
// *  with the License.  You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// *  Unless required by applicable law or agreed to in writing,
// *  software distributed under the License is distributed on an
// *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// *  KIND, either express or implied.  See the License for the
// *  specific language governing permissions and limitations
// *  under the License.
// */
//package org.apache.tamaya.core;
//
//import org.apache.tamaya.TypeLiteral;
//import org.apache.tamaya.spi.ConfigContext;
//import org.apache.tamaya.spisupport.DefaultConfigBuilder;
//import org.junit.Test;
//
//import javax.config.Config;
//import javax.config.spi.ConfigBuilder;
//import javax.config.spi.ConfigProviderResolver;
//import javax.config.spi.ConfigSource;
//import javax.config.spi.Converter;
//
//import static org.junit.Assert.*;
//
///**
// * Tests for {@link ConfigBuilder} by atsticks on 06.09.16.
// */
//public class ConfigBuilderTest {
//
//    private TestConfigSource testConfigSource = new TestConfigSource(){};
//
//
//    @Test
//    public void addConfigSources_Array() throws Exception {
//        ConfigSource testPS2 = new TestConfigSource("addConfigSources_Array", 1);
//        ConfigBuilder b = new DefaultConfigBuilder()
//                .withSources(testConfigSource, testPS2);
//        Config cfg = b.build();
//        assertEquals(2, ConfigContext.of(cfg).getSources().size());
//        assertTrue(ConfigContext.of(cfg).getSources().contains(testConfigSource));
//        assertTrue(ConfigContext.of(cfg).getSources().contains(testPS2));
//        // Ensure no sorting happens during add, so switch ordinals!
//        testPS2 = new TestConfigSource("addConfigSources_Array", 1);
//        b = ConfigProviderResolver.instance().getBuilder()
//                .withSources(testPS2, testConfigSource);
//        cfg = b.build();
//        assertEquals(2, ConfigContext.of(cfg).getSources().size());
//        assertTrue(ConfigContext.of(cfg).getSources().contains(testConfigSource));
//        assertTrue(ConfigContext.of(cfg).getSources().contains(testPS2));
//        assertEquals(ConfigContext.of(cfg).getSources().get(1).getName(), "TestConfigSource");
//        assertEquals(ConfigContext.of(cfg).getSources().get(0).getName(), "addConfigSources_Array");
//    }
//
//    @Test
//    @SuppressWarnings({ "rawtypes", "unchecked" })
//    public void addPropertyConverters_Array() throws Exception {
//		Converter<String> converter = new Converter<String>(){
//            @Override
//            public String convert(String value) {
//                return value;
//            }
//        }; //(value) -> value.toLowerCase();
//        ConfigBuilder b = ConfigProviderResolver.instance().getBuilder()
//                .withConverters(converter);
//        Config cfg = b.build();
//        ConfigContext ctx = ConfigContext.of(cfg);
//        assertTrue(ctx.getConverters(TypeLiteral.of(String.class)).contains(converter));
//        assertEquals(1, ctx.getConverters().size());
//        b = ConfigProviderResolver.instance().getBuilder()
//                .withConverters(converter);
//        b.withConverters(converter);
//        assertEquals(1, ctx.getConverters().size());
//    }
//
//    @Test
//    public void build() throws Exception {
//        ConfigBuilder b = ConfigProviderResolver.instance().getBuilder();
//        Config cfg = b.build();
//        ConfigContext ctx = ConfigContext.of(cfg);
//        assertNotNull(ctx);
//        assertTrue(ctx.getSources().isEmpty());
//        assertTrue(ctx.getPropertyFilters().isEmpty());
//    }
//
//}