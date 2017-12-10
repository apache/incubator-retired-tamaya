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
//import javax.config.ConfigProvider;
//import javax.config.spi.ConfigSource;
//import javax.config.spi.Converter;
//import java.util.Arrays;
//import java.util.Comparator;
//
//import static org.junit.Assert.*;
//
///**
// * Tests for {@link ConfigContextBuilder} by atsticks on 06.09.16.
// */
//public class ConfigContextBuilderTest {
//
//    private TestConfigSource TestConfigSource = new TestConfigSource(){};
//
//    @Test
//    public void setContext() throws Exception {
//        ConfigContext context = ConfigContext.of(ConfigProvider.getConfig());
//        ConfigContextBuilder b = new ConfigContextBuilder()
//                .withContext(context);
//        assertEquals(context, b.build());
//    }
//
//    @Test
//    public void withSources_Array() throws Exception {
//        ConfigSource testPS2 = new TestConfigSource("withSources_Array", 1);
//        ConfigContextBuilder b = new ConfigContextBuilder()
//                .withSources(TestConfigSource, testPS2);
//        ConfigContext ctx = b.build();
//        assertEquals(2, ctx.getSources().size());
//        assertTrue(ctx.getSources().contains(TestConfigSource));
//        assertTrue(ctx.getSources().contains(testPS2));
//        // Ensure no sorting happens during add, so switch ordinals!
//        testPS2 = new TestConfigSource("withSources_Array", 1);
//        b = new ConfigContextBuilder()
//                .withSources(testPS2, TestConfigSource);
//        ctx = b.build();
//        assertEquals(2, ctx.getSources().size());
//        assertTrue(ctx.getSources().contains(TestConfigSource));
//        assertTrue(ctx.getSources().contains(testPS2));
//        assertEquals(ctx.getSources().get(1).getName(), "TestConfigSource");
//        assertEquals(ctx.getSources().get(0).getName(), "withSources_Array");
//    }
//
//    @Test
//    public void withSources_Collection() throws Exception {
//        ConfigSource testPS2 = new TestConfigSource("withSources_Collection", 1);
//        ConfigContextBuilder b = new ConfigContextBuilder()
//                .withSources(Arrays.asList(new ConfigSource[]{TestConfigSource, testPS2}));
//        ConfigContext ctx = b.build();
//        assertEquals(2, ctx.getSources().size());
//        assertTrue(ctx.getSources().contains(TestConfigSource));
//        assertTrue(ctx.getSources().contains(testPS2));
//        assertEquals(ctx.getSources().get(0).getName(), "TestConfigSource");
//        assertEquals(ctx.getSources().get(1).getName(), "withSources_Collection");
//        // Ensure no sorting happens during add, so switch ordinals!
//        testPS2 = new TestConfigSource("withSources_Collection", 1);
//        b = new ConfigContextBuilder()
//                .withSources(Arrays.asList(new ConfigSource[]{testPS2, TestConfigSource}));
//        ctx = b.build();
//        assertEquals(2, ctx.getSources().size());
//        assertTrue(ctx.getSources().contains(TestConfigSource));
//        assertTrue(ctx.getSources().contains(testPS2));
//        assertEquals(ctx.getSources().get(1).getName(), "TestConfigSource");
//        assertEquals(ctx.getSources().get(0).getName(), "withSources_Collection");
//    }
//
//    @Test
//    public void removeConfigSources_Array() throws Exception {
//        ConfigSource testPS2 = new TestConfigSource("removeConfigSources_Array", 1);
//        ConfigContextBuilder b = new ConfigContextBuilder()
//                .withSources(TestConfigSource, testPS2);
//        ConfigContext ctx = b.build();
//        assertEquals(2, ctx.getSources().size());
//        assertTrue(ctx.getSources().contains(TestConfigSource));
//        assertTrue(ctx.getSources().contains(testPS2));
//        b = new ConfigContextBuilder()
//                .withSources(TestConfigSource, testPS2);
//        b.removeSources(TestConfigSource);
//        ctx = b.build();
//        assertFalse(ctx.getSources().contains(TestConfigSource));
//        assertTrue(ctx.getSources().contains(testPS2));
//        assertEquals(1, ctx.getSources().size());
//    }
//
//    @Test
//    public void removeConfigSources_Collection() throws Exception {
//        ConfigSource testPS2 = new TestConfigSource("removeConfigSources_Array", 1);
//        ConfigContextBuilder b = new ConfigContextBuilder()
//                .withSources(TestConfigSource, testPS2);
//        ConfigContext ctx = b.build();
//        assertEquals(2, ctx.getSources().size());
//        assertTrue(ctx.getSources().contains(TestConfigSource));
//        assertTrue(ctx.getSources().contains(testPS2));
//        b = new ConfigContextBuilder()
//                .withSources(TestConfigSource, testPS2);
//        b.removeSources(TestConfigSource);
//        ctx = b.build();
//        assertEquals(1, ctx.getSources().size());
//        assertFalse(ctx.getSources().contains(TestConfigSource));
//        assertTrue(ctx.getSources().contains(testPS2));
//    }
//
//    @Test
//    public void addPropertyFilters_Array() throws Exception {
//        Filter filter1 = (value) -> value;
//        Filter filter2 = (value) -> value;
//        ConfigContextBuilder b = new ConfigContextBuilder();
//        b.withFilters(filter1, filter2);
//        ConfigContext ctx = b.build();
//        assertTrue(ctx.getPropertyFilters().contains(filter1));
//        assertTrue(ctx.getPropertyFilters().contains(filter2));
//        assertEquals(2, ctx.getPropertyFilters().size());
//        b = new ConfigContextBuilder();
//        b.withFilters(filter1, filter2);
//        b.withFilters(filter1, filter2);
//        assertEquals(2, ctx.getPropertyFilters().size());
//    }
//
//    @Test
//    public void addPropertyFilters_Collection() throws Exception {
//        Filter filter1 = (value) -> value;
//        Filter filter2 = (value) -> value;
//        ConfigContextBuilder b = new ConfigContextBuilder();
//        b.withFilters(Arrays.asList(new Filter[]{filter1, filter2}));
//        ConfigContext ctx = b.build();
//        assertTrue(ctx.getPropertyFilters().contains(filter1));
//        assertTrue(ctx.getPropertyFilters().contains(filter2));
//        assertEquals(2, ctx.getPropertyFilters().size());
//        b = new ConfigContextBuilder();
//        b.withFilters(filter1, filter2);
//        b.withFilters(filter1, filter2);
//        assertEquals(2, ctx.getPropertyFilters().size());
//    }
//
//    @Test
//    public void removePropertyFilters_Array() throws Exception {
//        Filter filter1 = (value) -> value;
//        Filter filter2 = (value) -> value;
//        ConfigContextBuilder b = new ConfigContextBuilder()
//                .withFilters(filter1, filter2);
//        ConfigContext ctx = b.build();
//        assertTrue(ctx.getPropertyFilters().contains(filter1));
//        assertTrue(ctx.getPropertyFilters().contains(filter2));
//        assertEquals(2, ctx.getPropertyFilters().size());
//        b = new ConfigContextBuilder()
//                .withFilters(filter1, filter2);
//        b.removeFilters(filter1);
//        ctx = b.build();
//        assertEquals(1, ctx.getPropertyFilters().size());
//        assertFalse(ctx.getPropertyFilters().contains(filter1));
//        assertTrue(ctx.getPropertyFilters().contains(filter2));
//    }
//
//    @Test
//    public void removePropertyFilters_Collection() throws Exception {
//        Filter filter1 = (value) -> value;
//        Filter filter2 = (value) -> value;
//        ConfigContextBuilder b = new ConfigContextBuilder()
//                .withFilters(Arrays.asList(new Filter[]{filter1, filter2}));
//        ConfigContext ctx = b.build();
//        assertTrue(ctx.getPropertyFilters().contains(filter1));
//        assertTrue(ctx.getPropertyFilters().contains(filter2));
//        assertEquals(2, ctx.getPropertyFilters().size());
//        b = new ConfigContextBuilder()
//                .withFilters(Arrays.asList(new Filter[]{filter1, filter2}));
//        b.removeFilters(filter1);
//        ctx = b.build();
//        assertEquals(1, ctx.getPropertyFilters().size());
//        assertFalse(ctx.getPropertyFilters().contains(filter1));
//        assertTrue(ctx.getPropertyFilters().contains(filter2));
//    }
//
//    @Test
//    @SuppressWarnings({ "rawtypes", "unchecked" })
//    public void addConverters_Array() throws Exception {
//		Converter converter = (value) -> value.toLowerCase();
//        ConfigContextBuilder b = new ConfigContextBuilder()
//                .withConverters(TypeLiteral.of(String.class), converter);
//        ConfigContext ctx = b.build();
//        assertTrue(ctx.getConverters(TypeLiteral.of(String.class)).contains(converter));
//        assertEquals(1, ctx.getConverters().size());
//        b = new ConfigContextBuilder()
//                .withConverters(TypeLiteral.of(String.class), converter);
//        b.withConverters(TypeLiteral.of(String.class), converter);
//        assertEquals(1, ctx.getConverters().size());
//    }
//
//    @Test
//    @SuppressWarnings({ "rawtypes", "unchecked" })
//    public void addConverters_Collection() throws Exception {
//		Converter converter = (value) -> value.toLowerCase();
//        ConfigContextBuilder b = new ConfigContextBuilder()
//                .withConverters(TypeLiteral.of(String.class),
//                        Arrays.<Converter<Object>>asList(new Converter[]{converter}));
//        ConfigContext ctx = b.build();
//        assertTrue(ctx.getConverters(TypeLiteral.of(String.class)).contains(converter));
//        assertEquals(ctx.getConverters().size(), 1);
//        b = new ConfigContextBuilder()
//                .withConverters(TypeLiteral.of(String.class),
//                        Arrays.<Converter<Object>>asList(new Converter[]{converter}));
//        b.withConverters(TypeLiteral.of(String.class), converter);
//        assertEquals(ctx.getConverters().size(), 1);
//    }
//
//    @Test
//    @SuppressWarnings({ "rawtypes", "unchecked" })
//    public void removeConverters_Array() throws Exception {
//        Converter converter = (value) -> value.toLowerCase();
//        ConfigContextBuilder b = new ConfigContextBuilder()
//                .withConverters(TypeLiteral.of(String.class), converter);
//        ConfigContext ctx = b.build();
//        assertTrue(ctx.getConverters(TypeLiteral.of(String.class)).contains(converter));
//        assertEquals(1, ctx.getConverters(TypeLiteral.of(String.class)).size());
//        b = new ConfigContextBuilder()
//                .withConverters(TypeLiteral.of(String.class), converter);
//        b.removeConverters(TypeLiteral.of(String.class), converter);
//        ctx = b.build();
//        assertFalse(ctx.getConverters(TypeLiteral.of(String.class)).contains(converter));
//        assertTrue(ctx.getConverters(TypeLiteral.of(String.class)).isEmpty());
//    }
//
//    @SuppressWarnings({ "rawtypes", "unchecked" })
//	@Test
//    public void removeConverters_Collection() throws Exception {
//        Converter converter = (value) -> value.toLowerCase();
//        ConfigContextBuilder b = new ConfigContextBuilder()
//                .withConverters(TypeLiteral.of(String.class), Arrays.<Converter<Object>>asList(new Converter[]{converter}));
//        ConfigContext ctx = b.build();
//        assertTrue(ctx.getConverters(TypeLiteral.of(String.class)).contains(converter));
//        assertEquals(1, ctx.getConverters(TypeLiteral.of(String.class)).size());
//        b = new ConfigContextBuilder()
//                .withConverters(TypeLiteral.of(String.class), Arrays.<Converter<Object>>asList(new Converter[]{converter}));
//        b.removeConverters(TypeLiteral.of(String.class), Arrays.<Converter<Object>>asList(new Converter[]{converter}));
//        ctx = b.build();
//        assertFalse(ctx.getConverters(TypeLiteral.of(String.class)).contains(converter));
//        assertTrue(ctx.getConverters(TypeLiteral.of(String.class)).isEmpty());
//    }
//
//    @Test
//    public void setPropertyValueCombinationPolicy() throws Exception {
//        ConfigValueCombinationPolicy combPol = (currentValue, key, ConfigSource) -> currentValue;
//        ConfigContextBuilder b = new ConfigContextBuilder()
//                .withPropertyValueCombinationPolicy(combPol);
//        ConfigContext ctx = b.build();
//        assertEquals(ctx.getConfigValueCombinationPolicy(), combPol);
//    }
//
//    @Test
//    public void increasePriority(){
//        ConfigContextBuilder b = new ConfigContextBuilder();
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
//        ConfigContextBuilder b = new ConfigContextBuilder();
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
//        ConfigContextBuilder b = new ConfigContextBuilder();
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
//        ConfigContextBuilder b = new ConfigContextBuilder();
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
//        ConfigContextBuilder b = new ConfigContextBuilder();
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
//        ConfigContextBuilder b = new ConfigContextBuilder();
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
//        ConfigContextBuilder b = new ConfigContextBuilder();
//        ConfigContext ctx = b.build();
//        assertNotNull(ctx);
//        assertTrue(ctx.getSources().isEmpty());
//        assertTrue(ctx.getPropertyFilters().isEmpty());
//    }
//
//    @Test
//    public void testRemoveAllFilters() throws Exception {
//        ConfigContextBuilder b = new ConfigContextBuilder();
//        b.withFilters((value) -> value.toBuilder().setValue(toString() + " - ").build());
//        assertFalse(b.getFilters().isEmpty());
//        b.removeFilters(b.getFilters());
//        assertTrue(b.getFilters().isEmpty());
//    }
//
//    @Test
//    public void testRemoveAllSources() throws Exception {
//        ConfigContextBuilder b = new ConfigContextBuilder();
//        b.withSources(new TestConfigSource());
//        assertFalse(b.getSources().isEmpty());
//        b.removeSources(b.getSources());
//        assertTrue(b.getFilters().isEmpty());
//    }
//}