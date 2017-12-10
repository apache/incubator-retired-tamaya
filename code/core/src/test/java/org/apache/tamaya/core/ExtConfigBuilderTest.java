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
//import org.apache.tamaya.spi.*;
//import org.apache.tamaya.spi.Filter;
//import org.junit.Test;
//
//import javax.config.Config;
//import javax.config.ConfigProvider;
//import javax.config.spi.ConfigBuilder;
//import javax.config.spi.ConfigProviderResolver;
//import javax.config.spi.ConfigSource;
//import javax.config.spi.Converter;
//import java.util.Arrays;
//import java.util.Comparator;
//
//import static org.junit.Assert.*;
//
///**
// * Tests for {@link ConfigBuilder} by atsticks on 06.09.16.
// */
//public class ExtConfigBuilderTest {
//
//    private TestConfigSource testConfigSource = new TestConfigSource(){};
//
//    @Test
//    public void fromConfig() throws Exception {
//        Config cfg = ConfigProvider.getConfig();
//        ConfigBuilder b = ExtConfigBuilder.create(cfg);
//        assertEquals(cfg, b.build());
//    }
//
//    @Test
//    public void fromConfigBuilder() throws Exception {
//        ConfigBuilder b = ExtConfigBuilder.from(ConfigProviderResolver.instance().getBuilder());
//        assertNotNull(b);
//    }
//
//    @Test
//    public void addConfigSources_Array() throws Exception {
//        ConfigSource testPS2 = new TestConfigSource("addConfigSources_Array", 1);
//        ExtConfigBuilder b = ExtConfigBuilder.create()
//                .withSources(testConfigSource, testPS2);
//        Config cfg = b.build();
//        assertEquals(2, ConfigContext.of(cfg).getSources().size());
//        assertTrue(ConfigContext.of(cfg).getSources().contains(testConfigSource));
//        assertTrue(ConfigContext.of(cfg).getSources().contains(testPS2));
//        // Ensure no sorting happens during add, so switch ordinals!
//        testPS2 = new TestConfigSource("addConfigSources_Array", 1);
//        b = ExtConfigBuilder.create()
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
//    public void addConfigSources_Collection() throws Exception {
//        ConfigSource testPS2 = new TestConfigSource("addConfigSources_Collection", 1);
//        ExtConfigBuilder b = ExtConfigBuilder.create()
//                .withSources(Arrays.asList(new ConfigSource[]{testConfigSource, testPS2}));
//        Config cfg = b.build();
//        assertEquals(2, ConfigContext.of(cfg).getSources().size());
//        assertTrue(ConfigContext.of(cfg).getSources().contains(testConfigSource));
//        assertTrue(ConfigContext.of(cfg).getSources().contains(testPS2));
//        assertEquals(ConfigContext.of(cfg).getSources().get(0).getName(), "TestConfigSource");
//        assertEquals(ConfigContext.of(cfg).getSources().get(1).getName(), "addConfigSources_Collection");
//        // Ensure no sorting happens during add, so switch ordinals!
//        testPS2 = new TestConfigSource("addConfigSources_Collection", 1);
//        b = ExtConfigBuilder.create()
//                .withSources(testPS2, testConfigSource);
//        cfg = b.build();
//        assertEquals(2, ConfigContext.of(cfg).getSources().size());
//        assertTrue(ConfigContext.of(cfg).getSources().contains(testConfigSource));
//        assertTrue(ConfigContext.of(cfg).getSources().contains(testPS2));
//        assertEquals(ConfigContext.of(cfg).getSources().get(1).getName(), "TestConfigSource");
//        assertEquals(ConfigContext.of(cfg).getSources().get(0).getName(), "addConfigSources_Collection");
//    }
//
//    @Test
//    public void removeConfigSources_Array() throws Exception {
//        ConfigSource testPS2 = new TestConfigSource("removeConfigSources_Array", 1);
//        ExtConfigBuilder b = ExtConfigBuilder.create()
//                .withSources(testConfigSource, testPS2);
//        Config cfg = b.build();
//        assertEquals(2, ConfigContext.of(cfg).getSources().size());
//        assertTrue(ConfigContext.of(cfg).getSources().contains(testConfigSource));
//        assertTrue(ConfigContext.of(cfg).getSources().contains(testPS2));
//        b = ExtConfigBuilder.from(ConfigProviderResolver.instance().getBuilder()
//                .withSources(testConfigSource, testPS2));
//        b.removeSources(testConfigSource);
//        cfg = b.build();
//        assertFalse(ConfigContext.of(cfg).getSources().contains(testConfigSource));
//        assertTrue(ConfigContext.of(cfg).getSources().contains(testPS2));
//        assertEquals(1, ConfigContext.of(cfg).getSources().size());
//    }
//
//    @Test
//    public void removeConfigSources_Collection() throws Exception {
//        ConfigSource testPS2 = new TestConfigSource("removeConfigSources_Array", 1);
//        ExtConfigBuilder b = ExtConfigBuilder.create()
//                .withSources(testConfigSource, testPS2);
//        Config cfg = b.build();
//        assertEquals(2, ConfigContext.of(cfg).getSources().size());
//        assertTrue(ConfigContext.of(cfg).getSources().contains(testConfigSource));
//        assertTrue(ConfigContext.of(cfg).getSources().contains(testPS2));
//        b = ExtConfigBuilder.from(ConfigProviderResolver.instance().getBuilder())
//                .withSources(testConfigSource, testPS2);
//        b.removeSources(testConfigSource);
//        cfg = b.build();
//        assertEquals(1, ConfigContext.of(cfg).getSources().size());
//        assertFalse(ConfigContext.of(cfg).getSources().contains(testConfigSource));
//        assertTrue(ConfigContext.of(cfg).getSources().contains(testPS2));
//    }
//
//    @Test
//    public void addPropertyFilters_Array() throws Exception {
//        Filter filter1 = (value) -> value;
//        Filter filter2 = (value) -> value;
//        ExtConfigBuilder b = ExtConfigBuilder.create();
//        b.withFilters(filter1, filter2);
//        Config cfg = b.build();
//        ConfigContext ctx = ConfigContext.of(cfg);
//        assertTrue(ctx.getPropertyFilters().contains(filter1));
//        assertTrue(ctx.getPropertyFilters().contains(filter2));
//        assertEquals(2, ctx.getPropertyFilters().size());
//        b = ExtConfigBuilder.create();
//        b.withFilters(filter1, filter2);
//        b.withFilters(filter1, filter2);
//        assertEquals(2, ctx.getPropertyFilters().size());
//    }
//
//    @Test
//    public void addPropertyFilters_Collection() throws Exception {
//        Filter filter1 = (value) -> value;
//        Filter filter2 = (value) -> value;
//        ExtConfigBuilder b = ExtConfigBuilder.create();
//        b.withFilters(Arrays.asList(new Filter[]{filter1, filter2}));
//        Config cfg = b.build();
//        ConfigContext ctx = ConfigContext.of(cfg);
//        assertTrue(ctx.getPropertyFilters().contains(filter1));
//        assertTrue(ctx.getPropertyFilters().contains(filter2));
//        assertEquals(2, ctx.getPropertyFilters().size());
//        b = ExtConfigBuilder.create();
//        b.withFilters(filter1, filter2);
//        b.withFilters(filter1, filter2);
//        assertEquals(2, ctx.getPropertyFilters().size());
//    }
//
//    @Test
//    public void removePropertyFilters_Array() throws Exception {
//        Filter filter1 = (value) -> value;
//        Filter filter2 = (value) -> value;
//        ExtConfigBuilder b = ExtConfigBuilder.create()
//                .withFilters(filter1, filter2);
//        Config cfg = b.build();
//        ConfigContext ctx = ConfigContext.of(cfg);
//        assertTrue(ctx.getPropertyFilters().contains(filter1));
//        assertTrue(ctx.getPropertyFilters().contains(filter2));
//        assertEquals(2, ctx.getPropertyFilters().size());
//        b = ExtConfigBuilder.create()
//                .withFilters(filter1, filter2);
//        b.removeFilters(filter1);
//        cfg = b.build();
//        ctx = ConfigContext.of(cfg);
//        assertEquals(1, ctx.getPropertyFilters().size());
//        assertFalse(ctx.getPropertyFilters().contains(filter1));
//        assertTrue(ctx.getPropertyFilters().contains(filter2));
//    }
//
//    @Test
//    public void removePropertyFilters_Collection() throws Exception {
//        Filter filter1 = (value) -> value;
//        Filter filter2 = (value) -> value;
//        ExtConfigBuilder b = ExtConfigBuilder.create()
//                .withFilters(Arrays.asList(new Filter[]{filter1, filter2}));
//        Config cfg = b.build();
//        ConfigContext ctx = ConfigContext.of(cfg);
//        assertTrue(ctx.getPropertyFilters().contains(filter1));
//        assertTrue(ctx.getPropertyFilters().contains(filter2));
//        assertEquals(2, ctx.getPropertyFilters().size());
//        b = ExtConfigBuilder.create()
//                .withFilters(Arrays.asList(new Filter[]{filter1, filter2}));
//        b.removeFilters(filter1);
//        cfg = b.build();
//        ctx = ConfigContext.of(cfg);
//        assertEquals(1, ctx.getPropertyFilters().size());
//        assertFalse(ctx.getPropertyFilters().contains(filter1));
//        assertTrue(ctx.getPropertyFilters().contains(filter2));
//    }
//
//    @Test
//    @SuppressWarnings({ "rawtypes", "unchecked" })
//    public void addPropertyConverters_Array() throws Exception {
//		Converter converter = (value) -> value.toLowerCase();
//        ExtConfigBuilder b = ExtConfigBuilder.create()
//                .withConverters(TypeLiteral.of(String.class), converter);
//        Config cfg = b.build();
//        ConfigContext ctx = ConfigContext.of(cfg);
//        assertTrue(ctx.getConverters(TypeLiteral.of(String.class)).contains(converter));
//        assertEquals(1, ctx.getConverters().size());
//        b = ExtConfigBuilder.create()
//                .withConverters(TypeLiteral.of(String.class), converter);
//        b.withConverters(TypeLiteral.of(String.class), converter);
//        assertEquals(1, ctx.getConverters().size());
//    }
//
//    @Test
//    @SuppressWarnings({ "rawtypes", "unchecked" })
//    public void addPropertyConverters_Collection() throws Exception {
//		Converter converter = (value) -> value.toLowerCase();
//        ExtConfigBuilder b = ExtConfigBuilder.create()
//                .withConverters(TypeLiteral.of(String.class),
//                        Arrays.<Converter<Object>>asList(new Converter[]{converter}));
//        Config cfg = b.build();
//        ConfigContext ctx = ConfigContext.of(cfg);
//        assertTrue(ctx.getConverters(TypeLiteral.of(String.class)).contains(converter));
//        assertEquals(ctx.getConverters().size(), 1);
//        b = ExtConfigBuilder.create()
//                .withConverters(TypeLiteral.of(String.class),
//                        Arrays.<Converter<Object>>asList(new Converter[]{converter}));
//        b.withConverters(TypeLiteral.of(String.class), converter);
//        assertEquals(ctx.getConverters().size(), 1);
//    }
//
//    @Test
//    @SuppressWarnings({ "rawtypes", "unchecked" })
//    public void removePropertyConverters_Array() throws Exception {
//        Converter converter = (value) -> value.toLowerCase();
//        ExtConfigBuilder b = b = ExtConfigBuilder.create()
//                .withConverters(TypeLiteral.of(String.class), converter);
//        Config cfg = b.build();
//        ConfigContext ctx = ConfigContext.of(cfg);
//        assertTrue(ctx.getConverters(TypeLiteral.of(String.class)).contains(converter));
//        assertEquals(1, ctx.getConverters(TypeLiteral.of(String.class)).size());
//        b = ExtConfigBuilder.create()
//                .withConverters(TypeLiteral.of(String.class), converter);
//        b.removeConverters(TypeLiteral.of(String.class), converter);
//        cfg = b.build();
//        ctx = ConfigContext.of(cfg);
//        assertFalse(ctx.getConverters(TypeLiteral.of(String.class)).contains(converter));
//        assertTrue(ctx.getConverters(TypeLiteral.of(String.class)).isEmpty());
//    }
//
//
//    @SuppressWarnings({ "rawtypes", "unchecked" })
//	@Test
//    public void removePropertyConverters_Collection() throws Exception {
//        Converter converter = (value) -> value.toLowerCase();
//        ExtConfigBuilder b = ExtConfigBuilder.create()
//                .withConverters(TypeLiteral.of(String.class), Arrays.<Converter<Object>>asList(new Converter[]{converter}));
//        Config cfg = b.build();
//        ConfigContext ctx = ConfigContext.of(cfg);
//        assertTrue(ctx.getConverters(TypeLiteral.of(String.class)).contains(converter));
//        assertEquals(1, ctx.getConverters(TypeLiteral.of(String.class)).size());
//        b = b = ExtConfigBuilder.create()
//                .withConverters(TypeLiteral.of(String.class), Arrays.<Converter<Object>>asList(new Converter[]{converter}));
//        b.removeConverters(TypeLiteral.of(String.class), Arrays.<Converter<Object>>asList(new Converter[]{converter}));
//        cfg = b.build();
//        ctx = ConfigContext.of(cfg);
//        assertFalse(ctx.getConverters(TypeLiteral.of(String.class)).contains(converter));
//        assertTrue(ctx.getConverters(TypeLiteral.of(String.class)).isEmpty());
//    }
//
//    @Test
//    public void setPropertyValueCombinationPolicy() throws Exception {
//        ConfigValueCombinationPolicy combPol = (currentValue, key, ConfigSource) -> currentValue;
//        ExtConfigBuilder b = b = ExtConfigBuilder.create()
//                .withPropertyValueCombinationPolicy(combPol);
//        Config cfg = b.build();
//        ConfigContext ctx = ConfigContext.of(cfg);
//        assertEquals(ctx.getConfigValueCombinationPolicy(), combPol);
//    }
//
//    @Test
//    public void increasePriority(){
//        ExtConfigBuilder b = ExtConfigBuilder.create();
//        TestConfigSource[] ConfigSources = new TestConfigSource[10];
//        for(int i=0;i<ConfigSources.length;i++){
//            ConfigSources[i] = new TestConfigSource("ps"+i,i);
//        }
//        b.withSources(ConfigSources);
//        b.increasePriority(ConfigSources[ConfigSources.length-1]);
//        for(int i=0;i<ConfigSources.length;i++){
//            assertEquals(ConfigSources[i], b.getSources().get(i));
//        }
//        b.increasePriority(ConfigSources[ConfigSources.length-2]);
//        for(int i=0;i<ConfigSources.length-2;i++){
//            assertEquals(ConfigSources[i], b.getSources().get(i));
//        }
//        assertEquals(ConfigSources[ConfigSources.length-1], b.getSources().get(ConfigSources.length-2));
//        assertEquals(ConfigSources[ConfigSources.length-2], b.getSources().get(ConfigSources.length-1));
//    }
//
//    @Test
//    public void decreasePriority(){
//        ExtConfigBuilder b = b = ExtConfigBuilder.create();
//        TestConfigSource[] ConfigSources = new TestConfigSource[10];
//        for(int i=0;i<ConfigSources.length;i++){
//            ConfigSources[i] = new TestConfigSource("ps"+i,i);
//        }
//        b.withSources(ConfigSources);
//        b.decreasePriority(ConfigSources[0]);
//        for(int i=0;i<ConfigSources.length;i++){
//            assertEquals(ConfigSources[i], b.getSources().get(i));
//        }
//        b.decreasePriority(ConfigSources[1]);
//        for(int i=2;i<ConfigSources.length;i++){
//            assertEquals(ConfigSources[i], b.getSources().get(i));
//        }
//        assertEquals(ConfigSources[0], b.getSources().get(1));
//        assertEquals(ConfigSources[1], b.getSources().get(0));
//    }
//
//    @Test
//    public void lowestPriority(){
//        // setup
//        ExtConfigBuilder b = ExtConfigBuilder.create();
//        TestConfigSource[] ConfigSources = new TestConfigSource[10];
//        for(int i=0;i<ConfigSources.length;i++){
//            ConfigSources[i] = new TestConfigSource("ps"+i,i);
//        }
//        b.withSources(ConfigSources);
//        // test
//        b.lowestPriority(ConfigSources[0]);
//        for(int i=0;i<ConfigSources.length;i++){
//            assertEquals(ConfigSources[i], b.getSources().get(i));
//        }
//        b.lowestPriority(ConfigSources[1]);
//        for(int i=2;i<ConfigSources.length;i++){
//            assertEquals(ConfigSources[i], b.getSources().get(i));
//        }
//        assertEquals(ConfigSources[0], b.getSources().get(1));
//        assertEquals(ConfigSources[1], b.getSources().get(0));
//        b.lowestPriority(ConfigSources[5]);
//        assertEquals(ConfigSources[5], b.getSources().get(0));
//    }
//
//    @Test
//    public void highestPriority(){
//        // setup
//        ExtConfigBuilder b = ExtConfigBuilder.create();
//        TestConfigSource[] ConfigSources = new TestConfigSource[10];
//        for(int i=0;i<ConfigSources.length;i++){
//            ConfigSources[i] = new TestConfigSource("ps"+i,i);
//        }
//        b.withSources(ConfigSources);
//        // test
//        b.highestPriority(ConfigSources[ConfigSources.length-1]);
//        for(int i=0;i<ConfigSources.length;i++){
//            assertEquals(ConfigSources[i], b.getSources().get(i));
//        }
//        b.highestPriority(ConfigSources[ConfigSources.length-2]);
//        for(int i=0;i<ConfigSources.length-2;i++){
//            assertEquals(ConfigSources[i], b.getSources().get(i));
//        }
//        assertEquals(ConfigSources[ConfigSources.length-2], b.getSources().get(ConfigSources.length-1));
//        assertEquals(ConfigSources[ConfigSources.length-1], b.getSources().get(ConfigSources.length-2));
//        b.highestPriority(ConfigSources[5]);
//        assertEquals(ConfigSources[5], b.getSources().get(ConfigSources.length-1));
//    }
//
//    @Test
//    public void sortConfigSources(){
//        // setup
//        ExtConfigBuilder b = ExtConfigBuilder.create();
//        TestConfigSource[] ConfigSources = new TestConfigSource[10];
//        for(int i=0;i<ConfigSources.length;i++){
//            ConfigSources[i] = new TestConfigSource("ps"+i,i);
//        }
//        b.withSources(ConfigSources);
//        Comparator<ConfigSource> psComp = (o1, o2) -> o1.toString().compareTo(o2.toString());
//        // test
//        b.sortSources(psComp);
//        Arrays.sort(ConfigSources, psComp);
//        for(int i=0;i<ConfigSources.length;i++){
//            assertEquals(ConfigSources[i], b.getSources().get(i));
//        }
//    }
//
//    @Test
//    public void sortPropertyFilter(){
//        // setup
//        ExtConfigBuilder b = ExtConfigBuilder.create();
//        Filter[] filters = new Filter[10];
//        for(int i = 0; i< filters.length; i++){
//            filters[i] = (value) -> value.toBuilder().setValue(toString() + " - ").build();
//        }
//        b.withFilters(filters);
//        Comparator<Filter> pfComp = (o1, o2) -> o1.toString().compareTo(o2.toString());
//        // test
//        b.sortFilter(pfComp);
//        Arrays.sort(filters, pfComp);
//        for(int i = 0; i< filters.length; i++){
//            assertEquals(filters[i], b.getFilters().get(i));
//        }
//    }
//
//    @Test
//    public void build() throws Exception {
//        ExtConfigBuilder b = ExtConfigBuilder.create();
//        Config cfg = b.build();
//        ConfigContext ctx = ConfigContext.of(cfg);
//        assertNotNull(ctx);
//        assertTrue(ctx.getSources().isEmpty());
//        assertTrue(ctx.getPropertyFilters().isEmpty());
//    }
//
//    @Test
//    public void testRemoveAllFilters() throws Exception {
//        ExtConfigBuilder b = ExtConfigBuilder.create();
//        b.withFilters((value) -> value.toBuilder().setValue(toString() + " - ").build());
//        assertFalse(b.getFilters().isEmpty());
//        b.removeFilters(b.getFilters());
//        assertTrue(b.getFilters().isEmpty());
//    }
//
//    @Test
//    public void testRemoveAllSources() throws Exception {
//        ExtConfigBuilder b = ExtConfigBuilder.create();
//        b.withSources(new TestConfigSource());
//        assertFalse(b.getSources().isEmpty());
//        b.removeSources(b.getSources());
//        assertTrue(b.getSources().isEmpty());
//    }
//}