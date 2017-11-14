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
package org.apache.tamaya.core;

import org.apache.tamaya.ConfigurationProvider;
import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.spi.*;
import org.apache.tamaya.core.internal.CoreConfigurationContextBuilder;
import org.junit.Test;

import java.util.Arrays;
import java.util.Comparator;

import static org.junit.Assert.*;

/**
 * Tests for {@link CoreConfigurationContextBuilder} by atsticks on 06.09.16.
 */
public class ConfigurationContextBuilderTest {

    private TestPropertySource testPropertySource = new TestPropertySource(){};

    @Test
    public void setContext() throws Exception {
        ConfigurationContext context = ConfigurationProvider.getConfiguration().getContext();
        ConfigurationContextBuilder b = ConfigurationProvider.getConfigurationContextBuilder()
                .setContext(context);
        assertEquals(context, b.build());
    }

    @Test
    public void addPropertySources_Array() throws Exception {
        PropertySource testPS2 = new TestPropertySource("addPropertySources_Array", 1);
        ConfigurationContextBuilder b = new CoreConfigurationContextBuilder()
                .addPropertySources(testPropertySource, testPS2);
        ConfigurationContext ctx = b.build();
        assertEquals(2, ctx.getPropertySources().size());
        assertTrue(ctx.getPropertySources().contains(testPropertySource));
        assertTrue(ctx.getPropertySources().contains(testPS2));
        // Ensure no sorting happens during add, so switch ordinals!
        testPS2 = new TestPropertySource("addPropertySources_Array", 1);
        b = ConfigurationProvider.getConfigurationContextBuilder()
                .addPropertySources(testPS2, testPropertySource);
        ctx = b.build();
        assertEquals(2, ctx.getPropertySources().size());
        assertTrue(ctx.getPropertySources().contains(testPropertySource));
        assertTrue(ctx.getPropertySources().contains(testPS2));
        assertEquals(ctx.getPropertySources().get(1).getName(), "TestPropertySource");
        assertEquals(ctx.getPropertySources().get(0).getName(), "addPropertySources_Array");
    }

    @Test
    public void addPropertySources_Collection() throws Exception {
        PropertySource testPS2 = new TestPropertySource("addPropertySources_Collection", 1);
        ConfigurationContextBuilder b = new CoreConfigurationContextBuilder()
                .addPropertySources(Arrays.asList(new PropertySource[]{testPropertySource, testPS2}));
        ConfigurationContext ctx = b.build();
        assertEquals(2, ctx.getPropertySources().size());
        assertTrue(ctx.getPropertySources().contains(testPropertySource));
        assertTrue(ctx.getPropertySources().contains(testPS2));
        assertEquals(ctx.getPropertySources().get(0).getName(), "TestPropertySource");
        assertEquals(ctx.getPropertySources().get(1).getName(), "addPropertySources_Collection");
        // Ensure no sorting happens during add, so switch ordinals!
        testPS2 = new TestPropertySource("addPropertySources_Collection", 1);
        b = ConfigurationProvider.getConfigurationContextBuilder()
                .addPropertySources(Arrays.asList(new PropertySource[]{testPS2, testPropertySource}));
        ctx = b.build();
        assertEquals(2, ctx.getPropertySources().size());
        assertTrue(ctx.getPropertySources().contains(testPropertySource));
        assertTrue(ctx.getPropertySources().contains(testPS2));
        assertEquals(ctx.getPropertySources().get(1).getName(), "TestPropertySource");
        assertEquals(ctx.getPropertySources().get(0).getName(), "addPropertySources_Collection");
    }

    @Test
    public void removePropertySources_Array() throws Exception {
        PropertySource testPS2 = new TestPropertySource("removePropertySources_Array", 1);
        ConfigurationContextBuilder b = ConfigurationProvider.getConfigurationContextBuilder()
                .addPropertySources(testPropertySource, testPS2);
        ConfigurationContext ctx = b.build();
        assertEquals(2, ctx.getPropertySources().size());
        assertTrue(ctx.getPropertySources().contains(testPropertySource));
        assertTrue(ctx.getPropertySources().contains(testPS2));
        b = ConfigurationProvider.getConfigurationContextBuilder()
                .addPropertySources(testPropertySource, testPS2);
        b.removePropertySources(testPropertySource);
        ctx = b.build();
        assertFalse(ctx.getPropertySources().contains(testPropertySource));
        assertTrue(ctx.getPropertySources().contains(testPS2));
        assertEquals(1, ctx.getPropertySources().size());
    }

    @Test
    public void removePropertySources_Collection() throws Exception {
        PropertySource testPS2 = new TestPropertySource("removePropertySources_Array", 1);
        ConfigurationContextBuilder b = ConfigurationProvider.getConfigurationContextBuilder()
                .addPropertySources(testPropertySource, testPS2);
        ConfigurationContext ctx = b.build();
        assertEquals(2, ctx.getPropertySources().size());
        assertTrue(ctx.getPropertySources().contains(testPropertySource));
        assertTrue(ctx.getPropertySources().contains(testPS2));
        b = ConfigurationProvider.getConfigurationContextBuilder()
                .addPropertySources(testPropertySource, testPS2);
        b.removePropertySources(testPropertySource);
        ctx = b.build();
        assertEquals(1, ctx.getPropertySources().size());
        assertFalse(ctx.getPropertySources().contains(testPropertySource));
        assertTrue(ctx.getPropertySources().contains(testPS2));
    }

    @Test
    public void addPropertyFilters_Array() throws Exception {
        PropertyFilter filter1 = (value, context) -> value;
        PropertyFilter filter2 = (value, context) -> value;
        ConfigurationContextBuilder b = ConfigurationProvider.getConfigurationContextBuilder();
        b.addPropertyFilters(filter1, filter2);
        ConfigurationContext ctx = b.build();
        assertTrue(ctx.getPropertyFilters().contains(filter1));
        assertTrue(ctx.getPropertyFilters().contains(filter2));
        assertEquals(2, ctx.getPropertyFilters().size());
        b = ConfigurationProvider.getConfigurationContextBuilder();
        b.addPropertyFilters(filter1, filter2);
        b.addPropertyFilters(filter1, filter2);
        assertEquals(2, ctx.getPropertyFilters().size());
    }

    @Test
    public void addPropertyFilters_Collection() throws Exception {
        PropertyFilter filter1 = (value, context) -> value;
        PropertyFilter filter2 = (value, context) -> value;
        ConfigurationContextBuilder b = ConfigurationProvider.getConfigurationContextBuilder();
        b.addPropertyFilters(Arrays.asList(new PropertyFilter[]{filter1, filter2}));
        ConfigurationContext ctx = b.build();
        assertTrue(ctx.getPropertyFilters().contains(filter1));
        assertTrue(ctx.getPropertyFilters().contains(filter2));
        assertEquals(2, ctx.getPropertyFilters().size());
        b = ConfigurationProvider.getConfigurationContextBuilder();
        b.addPropertyFilters(filter1, filter2);
        b.addPropertyFilters(filter1, filter2);
        assertEquals(2, ctx.getPropertyFilters().size());
    }

    @Test
    public void removePropertyFilters_Array() throws Exception {
        PropertyFilter filter1 = (value, context) -> value;
        PropertyFilter filter2 = (value, context) -> value;
        ConfigurationContextBuilder b = ConfigurationProvider.getConfigurationContextBuilder()
                .addPropertyFilters(filter1, filter2);
        ConfigurationContext ctx = b.build();
        assertTrue(ctx.getPropertyFilters().contains(filter1));
        assertTrue(ctx.getPropertyFilters().contains(filter2));
        assertEquals(2, ctx.getPropertyFilters().size());
        b = ConfigurationProvider.getConfigurationContextBuilder()
                .addPropertyFilters(filter1, filter2);
        b.removePropertyFilters(filter1);
        ctx = b.build();
        assertEquals(1, ctx.getPropertyFilters().size());
        assertFalse(ctx.getPropertyFilters().contains(filter1));
        assertTrue(ctx.getPropertyFilters().contains(filter2));
    }

    @Test
    public void removePropertyFilters_Collection() throws Exception {
        PropertyFilter filter1 = (value, context) -> value;
        PropertyFilter filter2 = (value, context) -> value;
        ConfigurationContextBuilder b = ConfigurationProvider.getConfigurationContextBuilder()
                .addPropertyFilters(Arrays.asList(new PropertyFilter[]{filter1, filter2}));
        ConfigurationContext ctx = b.build();
        assertTrue(ctx.getPropertyFilters().contains(filter1));
        assertTrue(ctx.getPropertyFilters().contains(filter2));
        assertEquals(2, ctx.getPropertyFilters().size());
        b = ConfigurationProvider.getConfigurationContextBuilder()
                .addPropertyFilters(Arrays.asList(new PropertyFilter[]{filter1, filter2}));
        b.removePropertyFilters(filter1);
        ctx = b.build();
        assertEquals(1, ctx.getPropertyFilters().size());
        assertFalse(ctx.getPropertyFilters().contains(filter1));
        assertTrue(ctx.getPropertyFilters().contains(filter2));
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void addPropertyConverters_Array() throws Exception {
		PropertyConverter converter = (value, context) -> value.toLowerCase();
        ConfigurationContextBuilder b = ConfigurationProvider.getConfigurationContextBuilder()
                .addPropertyConverters(TypeLiteral.of(String.class), converter);
        ConfigurationContext ctx = b.build();
        assertTrue(ctx.getPropertyConverters(TypeLiteral.of(String.class)).contains(converter));
        assertEquals(1, ctx.getPropertyConverters().size());
        b = ConfigurationProvider.getConfigurationContextBuilder()
                .addPropertyConverters(TypeLiteral.of(String.class), converter);
        b.addPropertyConverters(TypeLiteral.of(String.class), converter);
        assertEquals(1, ctx.getPropertyConverters().size());
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void addPropertyConverters_Collection() throws Exception {
		PropertyConverter converter = (value, context) -> value.toLowerCase();
        ConfigurationContextBuilder b = ConfigurationProvider.getConfigurationContextBuilder()
                .addPropertyConverters(TypeLiteral.of(String.class),
                        Arrays.<PropertyConverter<Object>>asList(new PropertyConverter[]{converter}));
        ConfigurationContext ctx = b.build();
        assertTrue(ctx.getPropertyConverters(TypeLiteral.of(String.class)).contains(converter));
        assertEquals(ctx.getPropertyConverters().size(), 1);
        b = ConfigurationProvider.getConfigurationContextBuilder()
                .addPropertyConverters(TypeLiteral.of(String.class),
                        Arrays.<PropertyConverter<Object>>asList(new PropertyConverter[]{converter}));
        b.addPropertyConverters(TypeLiteral.of(String.class), converter);
        assertEquals(ctx.getPropertyConverters().size(), 1);
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void removePropertyConverters_Array() throws Exception {
        PropertyConverter converter = (value, context) -> value.toLowerCase();
        ConfigurationContextBuilder b = ConfigurationProvider.getConfigurationContextBuilder()
                .addPropertyConverters(TypeLiteral.of(String.class), converter);
        ConfigurationContext ctx = b.build();
        assertTrue(ctx.getPropertyConverters(TypeLiteral.of(String.class)).contains(converter));
        assertEquals(1, ctx.getPropertyConverters(TypeLiteral.of(String.class)).size());
        b = ConfigurationProvider.getConfigurationContextBuilder()
                .addPropertyConverters(TypeLiteral.of(String.class), converter);
        b.removePropertyConverters(TypeLiteral.of(String.class), converter);
        ctx = b.build();
        assertFalse(ctx.getPropertyConverters(TypeLiteral.of(String.class)).contains(converter));
        assertTrue(ctx.getPropertyConverters(TypeLiteral.of(String.class)).isEmpty());
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
    public void removePropertyConverters_Collection() throws Exception {
        PropertyConverter converter = (value, context) -> value.toLowerCase();
        ConfigurationContextBuilder b = ConfigurationProvider.getConfigurationContextBuilder()
                .addPropertyConverters(TypeLiteral.of(String.class), Arrays.<PropertyConverter<Object>>asList(new PropertyConverter[]{converter}));
        ConfigurationContext ctx = b.build();
        assertTrue(ctx.getPropertyConverters(TypeLiteral.of(String.class)).contains(converter));
        assertEquals(1, ctx.getPropertyConverters(TypeLiteral.of(String.class)).size());
        b = ConfigurationProvider.getConfigurationContextBuilder()
                .addPropertyConverters(TypeLiteral.of(String.class), Arrays.<PropertyConverter<Object>>asList(new PropertyConverter[]{converter}));
        b.removePropertyConverters(TypeLiteral.of(String.class), Arrays.<PropertyConverter<Object>>asList(new PropertyConverter[]{converter}));
        ctx = b.build();
        assertFalse(ctx.getPropertyConverters(TypeLiteral.of(String.class)).contains(converter));
        assertTrue(ctx.getPropertyConverters(TypeLiteral.of(String.class)).isEmpty());
    }

    @Test
    public void setPropertyValueCombinationPolicy() throws Exception {
        PropertyValueCombinationPolicy combPol = (currentValue, key, propertySource) -> currentValue;
        ConfigurationContextBuilder b = ConfigurationProvider.getConfigurationContextBuilder()
                .setPropertyValueCombinationPolicy(combPol);
        ConfigurationContext ctx = b.build();
        assertEquals(ctx.getPropertyValueCombinationPolicy(), combPol);
    }

    @Test
    public void increasePriority(){
        ConfigurationContextBuilder b = ConfigurationProvider.getConfigurationContextBuilder();
        TestPropertySource[] propertySources = new TestPropertySource[10];
        for(int i=0;i<propertySources.length;i++){
            propertySources[i] = new TestPropertySource("ps"+i,i);
        }
        b.addPropertySources(propertySources);
        b.increasePriority(propertySources[propertySources.length-1]);
        for(int i=0;i<propertySources.length;i++){
            assertEquals(propertySources[i], b.getPropertySources().get(i));
        }
        b.increasePriority(propertySources[propertySources.length-2]);
        for(int i=0;i<propertySources.length-2;i++){
            assertEquals(propertySources[i], b.getPropertySources().get(i));
        }
        assertEquals(propertySources[propertySources.length-1], b.getPropertySources().get(propertySources.length-2));
        assertEquals(propertySources[propertySources.length-2], b.getPropertySources().get(propertySources.length-1));
    }

    @Test
    public void decreasePriority(){
        ConfigurationContextBuilder b = ConfigurationProvider.getConfigurationContextBuilder();
        TestPropertySource[] propertySources = new TestPropertySource[10];
        for(int i=0;i<propertySources.length;i++){
            propertySources[i] = new TestPropertySource("ps"+i,i);
        }
        b.addPropertySources(propertySources);
        b.decreasePriority(propertySources[0]);
        for(int i=0;i<propertySources.length;i++){
            assertEquals(propertySources[i], b.getPropertySources().get(i));
        }
        b.decreasePriority(propertySources[1]);
        for(int i=2;i<propertySources.length;i++){
            assertEquals(propertySources[i], b.getPropertySources().get(i));
        }
        assertEquals(propertySources[0], b.getPropertySources().get(1));
        assertEquals(propertySources[1], b.getPropertySources().get(0));
    }

    @Test
    public void lowestPriority(){
        // setup
        ConfigurationContextBuilder b = ConfigurationProvider.getConfigurationContextBuilder();
        TestPropertySource[] propertySources = new TestPropertySource[10];
        for(int i=0;i<propertySources.length;i++){
            propertySources[i] = new TestPropertySource("ps"+i,i);
        }
        b.addPropertySources(propertySources);
        // test
        b.lowestPriority(propertySources[0]);
        for(int i=0;i<propertySources.length;i++){
            assertEquals(propertySources[i], b.getPropertySources().get(i));
        }
        b.lowestPriority(propertySources[1]);
        for(int i=2;i<propertySources.length;i++){
            assertEquals(propertySources[i], b.getPropertySources().get(i));
        }
        assertEquals(propertySources[0], b.getPropertySources().get(1));
        assertEquals(propertySources[1], b.getPropertySources().get(0));
        b.lowestPriority(propertySources[5]);
        assertEquals(propertySources[5], b.getPropertySources().get(0));
    }

    @Test
    public void highestPriority(){
        // setup
        ConfigurationContextBuilder b = ConfigurationProvider.getConfigurationContextBuilder();
        TestPropertySource[] propertySources = new TestPropertySource[10];
        for(int i=0;i<propertySources.length;i++){
            propertySources[i] = new TestPropertySource("ps"+i,i);
        }
        b.addPropertySources(propertySources);
        // test
        b.highestPriority(propertySources[propertySources.length-1]);
        for(int i=0;i<propertySources.length;i++){
            assertEquals(propertySources[i], b.getPropertySources().get(i));
        }
        b.highestPriority(propertySources[propertySources.length-2]);
        for(int i=0;i<propertySources.length-2;i++){
            assertEquals(propertySources[i], b.getPropertySources().get(i));
        }
        assertEquals(propertySources[propertySources.length-2], b.getPropertySources().get(propertySources.length-1));
        assertEquals(propertySources[propertySources.length-1], b.getPropertySources().get(propertySources.length-2));
        b.highestPriority(propertySources[5]);
        assertEquals(propertySources[5], b.getPropertySources().get(propertySources.length-1));
    }

    @Test
    public void sortPropertySources(){
        // setup
        ConfigurationContextBuilder b = ConfigurationProvider.getConfigurationContextBuilder();
        TestPropertySource[] propertySources = new TestPropertySource[10];
        for(int i=0;i<propertySources.length;i++){
            propertySources[i] = new TestPropertySource("ps"+i,i);
        }
        b.addPropertySources(propertySources);
        Comparator<PropertySource> psComp = (o1, o2) -> o1.toString().compareTo(o2.toString());
        // test
        b.sortPropertySources(psComp);
        Arrays.sort(propertySources, psComp);
        for(int i=0;i<propertySources.length;i++){
            assertEquals(propertySources[i], b.getPropertySources().get(i));
        }
    }

    @Test
    public void sortPropertyFilter(){
        // setup
        ConfigurationContextBuilder b = ConfigurationProvider.getConfigurationContextBuilder();
        PropertyFilter[] propertyFilters = new PropertyFilter[10];
        for(int i=0;i<propertyFilters.length;i++){
            propertyFilters[i] = (value, context) -> value.toBuilder().setValue(toString() + " - ").build();
        }
        b.addPropertyFilters(propertyFilters);
        Comparator<PropertyFilter> pfComp = (o1, o2) -> o1.toString().compareTo(o2.toString());
        // test
        b.sortPropertyFilter(pfComp);
        Arrays.sort(propertyFilters, pfComp);
        for(int i=0;i<propertyFilters.length;i++){
            assertEquals(propertyFilters[i], b.getPropertyFilters().get(i));
        }
    }

    @Test
    public void build() throws Exception {
        ConfigurationContextBuilder b = ConfigurationProvider.getConfigurationContextBuilder();
        ConfigurationContext ctx = b.build();
        assertNotNull(ctx);
        assertTrue(ctx.getPropertySources().isEmpty());
        assertTrue(ctx.getPropertyFilters().isEmpty());
    }

    @Test
    public void testRemoveAllFilters() throws Exception {
        ConfigurationContextBuilder b = ConfigurationProvider.getConfigurationContextBuilder();
        b.addPropertyFilters((value, context) -> value.toBuilder().setValue(toString() + " - ").build());
        assertFalse(b.getPropertyFilters().isEmpty());
        b.removePropertyFilters(b.getPropertyFilters());
        assertTrue(b.getPropertyFilters().isEmpty());
    }

    @Test
    public void testRemoveAllSources() throws Exception {
        ConfigurationContextBuilder b = ConfigurationProvider.getConfigurationContextBuilder();
        b.addPropertySources(new TestPropertySource());
        assertFalse(b.getPropertySources().isEmpty());
        b.removePropertySources(b.getPropertySources());
        assertTrue(b.getPropertyFilters().isEmpty());
    }
}