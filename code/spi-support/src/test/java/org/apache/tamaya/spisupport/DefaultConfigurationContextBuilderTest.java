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
package org.apache.tamaya.spisupport;

import org.apache.tamaya.spi.ConfigurationContext;
import org.apache.tamaya.spi.ConfigurationContextBuilder;
import org.apache.tamaya.spi.PropertyConverter;
import org.apache.tamaya.spi.PropertyFilter;
import org.apache.tamaya.spi.PropertySource;
import org.apache.tamaya.spi.PropertyValueCombinationPolicy;
import static org.junit.Assert.*;

import org.apache.tamaya.ConfigurationProvider;
import org.apache.tamaya.TypeLiteral;
import org.junit.Test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author William.Lieurance 2018.02.17
 *
 * This class is an almost exact copy of
 * com.tamaya.core.internal.CoreConfigurationBuilderTest, which itself uses
 * DefaultConfigurationContextBuilder under the covers.
 *
 */
public class DefaultConfigurationContextBuilderTest {

    private MockedPropertySource testPropertySource = new MockedPropertySource() {
    };

    @Test
    public void setContext() throws Exception {
        ConfigurationContext context = ConfigurationProvider.getConfiguration().getContext();
        ConfigurationContextBuilder b = new DefaultConfigurationContextBuilder()
                .setContext(context);
        assertEquals(context, b.build());
        boolean caughtAlreadyBuilt = false;
        try {
            b.setContext(context);
        } catch (IllegalStateException e) {
            caughtAlreadyBuilt = true;
        }
        assertTrue(caughtAlreadyBuilt);
        b = new DefaultConfigurationContextBuilder(context);
        assertEquals(context, b.build());
    }

    @Test
    public void addPropertySources_Array() throws Exception {
        PropertySource testPS2 = new MockedPropertySource("addPropertySources_Array", 1);
        ConfigurationContextBuilder b = new DefaultConfigurationContextBuilder()
                .addPropertySources(testPropertySource, testPS2);
        ConfigurationContext ctx = b.build();
        assertEquals(2, ctx.getPropertySources().size());
        assertTrue(ctx.getPropertySources().contains(testPropertySource));
        assertNotNull(((DefaultConfigurationContextBuilder)b).getPropertySource(testPropertySource.getName()));
        assertTrue(ctx.getPropertySources().contains(testPS2));
        assertNotNull(((DefaultConfigurationContextBuilder)b).getPropertySource(testPS2.getName()));
        // Ensure no sorting happens during add, so switch ordinals!
        testPS2 = new MockedPropertySource("addPropertySources_Array", 1);
        b = new DefaultConfigurationContextBuilder();
        ctx = b.addPropertySources(testPS2, testPropertySource).build();
        assertEquals(2, ctx.getPropertySources().size());
        assertTrue(ctx.getPropertySources().contains(testPropertySource));
        assertNotNull(((DefaultConfigurationContextBuilder)b).getPropertySource(testPropertySource.getName()));
        assertTrue(ctx.getPropertySources().contains(testPS2));
        assertNotNull(((DefaultConfigurationContextBuilder)b).getPropertySource(testPS2.getName()));
        assertEquals(ctx.getPropertySources().get(1).getName(), "MockedPropertySource");
        assertEquals(ctx.getPropertySources().get(0).getName(), "addPropertySources_Array");
    }

    @Test
    public void addPropertySources_Collection() throws Exception {
        PropertySource testPS2 = new MockedPropertySource("addPropertySources_Collection", 1);
        ConfigurationContextBuilder b = new DefaultConfigurationContextBuilder()
                .addPropertySources(Arrays.asList(new PropertySource[]{testPropertySource, testPS2}));
        ConfigurationContext ctx = b.build();
        assertEquals(2, ctx.getPropertySources().size());
        assertTrue(ctx.getPropertySources().contains(testPropertySource));
        assertNotNull(((DefaultConfigurationContextBuilder)b).getPropertySource(testPropertySource.getName()));
        assertTrue(ctx.getPropertySources().contains(testPS2));
        assertNotNull(((DefaultConfigurationContextBuilder)b).getPropertySource(testPS2.getName()));
        assertEquals(ctx.getPropertySources().get(0).getName(), "MockedPropertySource");
        assertEquals(ctx.getPropertySources().get(1).getName(), "addPropertySources_Collection");
        // Ensure no sorting happens during add, so switch ordinals!
        testPS2 = new MockedPropertySource("addPropertySources_Collection", 1);
        ctx = new DefaultConfigurationContextBuilder()
                .addPropertySources(Arrays.asList(new PropertySource[]{testPS2, testPropertySource})).build();
        assertEquals(2, ctx.getPropertySources().size());
        assertTrue(ctx.getPropertySources().contains(testPropertySource));
        assertNotNull(((DefaultConfigurationContextBuilder)b).getPropertySource(testPropertySource.getName()));
        assertTrue(ctx.getPropertySources().contains(testPS2));
        assertNotNull(((DefaultConfigurationContextBuilder)b).getPropertySource(testPS2.getName()));
        assertEquals(ctx.getPropertySources().get(1).getName(), "MockedPropertySource");
        assertEquals(ctx.getPropertySources().get(0).getName(), "addPropertySources_Collection");
    }

    @Test
    public void removePropertySources_Array() throws Exception {
        PropertySource testPS2 = new MockedPropertySource("removePropertySources_Array", 1);
        ConfigurationContextBuilder b = new DefaultConfigurationContextBuilder()
                .addPropertySources(testPropertySource, testPS2);
        ConfigurationContext ctx = b.build();
        assertEquals(2, ctx.getPropertySources().size());
        assertTrue(ctx.getPropertySources().contains(testPropertySource));
        assertNotNull(((DefaultConfigurationContextBuilder)b).getPropertySource(testPropertySource.getName()));
        assertTrue(ctx.getPropertySources().contains(testPS2));
        assertNotNull(((DefaultConfigurationContextBuilder)b).getPropertySource(testPS2.getName()));
        b = new DefaultConfigurationContextBuilder()
                .addPropertySources(testPropertySource, testPS2);
        ctx = b.removePropertySources(testPropertySource).build();
        assertFalse(ctx.getPropertySources().contains(testPropertySource));
        //Throws an exception
        //assertNull(((DefaultConfigurationContextBuilder)b).getPropertySource(testPropertySource.getName()));
        assertTrue(ctx.getPropertySources().contains(testPS2));
        assertNotNull(((DefaultConfigurationContextBuilder)b).getPropertySource(testPS2.getName()));
        assertEquals(1, ctx.getPropertySources().size());
    }

    @Test
    public void removePropertySources_Collection() throws Exception {
        PropertySource testPS2 = new MockedPropertySource("removePropertySources_Array", 1);
        ConfigurationContextBuilder b = new DefaultConfigurationContextBuilder()
                .addPropertySources(testPropertySource, testPS2);
        ConfigurationContext ctx = b.build();
        assertEquals(2, ctx.getPropertySources().size());
        assertTrue(ctx.getPropertySources().contains(testPropertySource));
        assertNotNull(((DefaultConfigurationContextBuilder)b).getPropertySource(testPropertySource.getName()));
        assertTrue(ctx.getPropertySources().contains(testPS2));
        assertNotNull(ctx.getPropertySource(testPS2.getName()));
        b = new DefaultConfigurationContextBuilder()
                .addPropertySources(testPropertySource, testPS2);
        ctx = b.removePropertySources(testPropertySource).build();
        assertEquals(1, ctx.getPropertySources().size());
        assertFalse(ctx.getPropertySources().contains(testPropertySource));
        assertTrue(ctx.getPropertySources().contains(testPS2));
        assertNotNull(ctx.getPropertySource(testPS2.getName()));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void missingPropertySource() throws Exception {
        PropertySource testPS2 = new MockedPropertySource("removePropertySources_Array", 1);
        ConfigurationContextBuilder b = new DefaultConfigurationContextBuilder()
                .addPropertySources(testPropertySource, testPS2);
        assertNull(((DefaultConfigurationContextBuilder)b).getPropertySource("missing"));
    }

    @Test
    public void addPropertyFilters_Array() throws Exception {
        PropertyFilter filter1 = (value, context) -> value;
        PropertyFilter filter2 = (value, context) -> value;
        ConfigurationContextBuilder b = new DefaultConfigurationContextBuilder();
        b.addPropertyFilters(filter1, filter2);
        ConfigurationContext ctx = b.build();
        boolean caughtAlreadyBuilt = false;
        try {
            b.addPropertyFilters(filter1, filter2);
        } catch (IllegalStateException e) {
            caughtAlreadyBuilt = true;
        }
        assertTrue(caughtAlreadyBuilt);
        assertTrue(ctx.getPropertyFilters().contains(filter1));
        assertTrue(ctx.getPropertyFilters().contains(filter2));
        assertEquals(2, ctx.getPropertyFilters().size());
        b = new DefaultConfigurationContextBuilder();
        b.addPropertyFilters(filter1, filter2);
        b.addPropertyFilters(filter1, filter2);
        assertEquals(2, ctx.getPropertyFilters().size());
    }

    @Test
    public void addPropertyFilters_Collection() throws Exception {
        PropertyFilter filter1 = (value, context) -> value;
        PropertyFilter filter2 = (value, context) -> value;
        ConfigurationContextBuilder b = new DefaultConfigurationContextBuilder();
        b.addPropertyFilters(Arrays.asList(new PropertyFilter[]{filter1, filter2}));
        ConfigurationContext ctx = b.build();
        boolean caughtAlreadyBuilt = false;
        try {
            b.addPropertyFilters(Arrays.asList(new PropertyFilter[]{filter1, filter2}));
        } catch (IllegalStateException e) {
            caughtAlreadyBuilt = true;
        }
        assertTrue(caughtAlreadyBuilt);
        assertTrue(ctx.getPropertyFilters().contains(filter1));
        assertTrue(ctx.getPropertyFilters().contains(filter2));
        assertEquals(2, ctx.getPropertyFilters().size());
        b = new DefaultConfigurationContextBuilder();
        b.addPropertyFilters(filter1, filter2);
        b.addPropertyFilters(filter1, filter2);
        assertEquals(2, ctx.getPropertyFilters().size());
    }

    @Test
    public void removePropertyFilters_Array() throws Exception {
        PropertyFilter filter1 = (value, context) -> value;
        PropertyFilter filter2 = (value, context) -> value;
        ConfigurationContextBuilder b = new DefaultConfigurationContextBuilder()
                .addPropertyFilters(filter1, filter2);
        ConfigurationContext ctx = b.build();
        assertTrue(ctx.getPropertyFilters().contains(filter1));
        assertTrue(ctx.getPropertyFilters().contains(filter2));
        assertEquals(2, ctx.getPropertyFilters().size());
        b = new DefaultConfigurationContextBuilder()
                .addPropertyFilters(filter1, filter2);
        ctx = b.removePropertyFilters(filter1).build();
        assertEquals(1, ctx.getPropertyFilters().size());
        assertFalse(ctx.getPropertyFilters().contains(filter1));
        assertTrue(ctx.getPropertyFilters().contains(filter2));
        boolean caughtAlreadyBuilt = false;
        try {
            b.removePropertyFilters(filter1);
        } catch (IllegalStateException e) {
            caughtAlreadyBuilt = true;
        }
        assertTrue(caughtAlreadyBuilt);
    }

    @Test
    public void removePropertyFilters_Collection() throws Exception {
        PropertyFilter filter1 = (value, context) -> value;
        PropertyFilter filter2 = (value, context) -> value;
        ConfigurationContextBuilder b = new DefaultConfigurationContextBuilder()
                .addPropertyFilters(Arrays.asList(new PropertyFilter[]{filter1, filter2}));
        ConfigurationContext ctx = b.build();
        assertTrue(ctx.getPropertyFilters().contains(filter1));
        assertTrue(ctx.getPropertyFilters().contains(filter2));
        assertEquals(2, ctx.getPropertyFilters().size());
        b = new DefaultConfigurationContextBuilder()
                .addPropertyFilters(Arrays.asList(new PropertyFilter[]{filter1, filter2}));
        ctx = b.removePropertyFilters(Arrays.asList(filter1)).build();
        assertEquals(1, ctx.getPropertyFilters().size());
        assertFalse(ctx.getPropertyFilters().contains(filter1));
        assertTrue(ctx.getPropertyFilters().contains(filter2));
        boolean caughtAlreadyBuilt = false;
        try {
            b.removePropertyFilters(filter1);
        } catch (IllegalStateException e) {
            caughtAlreadyBuilt = true;
        }
        assertTrue(caughtAlreadyBuilt);
    }

    @Test
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void addPropertyConverters_Array() throws Exception {
        PropertyConverter converter = (value, context) -> value.toLowerCase();
        ConfigurationContextBuilder b = new DefaultConfigurationContextBuilder()
                .addPropertyConverters(TypeLiteral.of(String.class), converter);
        ConfigurationContext ctx = b.build();
        boolean caughtAlreadyBuilt = false;
        try {
            b.addPropertyConverters(TypeLiteral.of(String.class), converter);
        } catch (IllegalStateException e) {
            caughtAlreadyBuilt = true;
        }
        assertTrue(caughtAlreadyBuilt);
        assertTrue(ctx.getPropertyConverters(TypeLiteral.of(String.class)).contains(converter));
        assertEquals(1, ctx.getPropertyConverters().size());
        b = new DefaultConfigurationContextBuilder()
                .addPropertyConverters(TypeLiteral.of(String.class), converter);
        b.addPropertyConverters(TypeLiteral.of(String.class), converter);
        assertEquals(1, ctx.getPropertyConverters().size());
    }

    @Test
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void addPropertyConverters_Collection() throws Exception {
        PropertyConverter converter = (value, context) -> value.toLowerCase();
        ConfigurationContextBuilder b = new DefaultConfigurationContextBuilder()
                .addPropertyConverters(TypeLiteral.of(String.class),
                        Arrays.<PropertyConverter<Object>>asList(new PropertyConverter[]{converter}));
        ConfigurationContext ctx = b.build();
        boolean caughtAlreadyBuilt = false;
        try {
            b.addPropertyConverters(TypeLiteral.of(String.class),
                    Arrays.<PropertyConverter<Object>>asList(new PropertyConverter[]{converter}));
        } catch (IllegalStateException e) {
            caughtAlreadyBuilt = true;
        }
        assertTrue(caughtAlreadyBuilt);
        assertTrue(ctx.getPropertyConverters(TypeLiteral.of(String.class)).contains(converter));
        assertEquals(ctx.getPropertyConverters().size(), 1);
        b = new DefaultConfigurationContextBuilder()
                .addPropertyConverters(TypeLiteral.of(String.class),
                        Arrays.<PropertyConverter<Object>>asList(new PropertyConverter[]{converter}));
        b.addPropertyConverters(TypeLiteral.of(String.class), converter);
        assertEquals(ctx.getPropertyConverters().size(), 1);
    }
    
    @Test
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void removePropertyConverters_Type() throws Exception {
        PropertyConverter converter = (value, context) -> value.toLowerCase();
        ConfigurationContextBuilder b = new DefaultConfigurationContextBuilder()
                .addPropertyConverters(TypeLiteral.of(String.class), converter);
        ConfigurationContext ctx = b.build();
        assertTrue(ctx.getPropertyConverters(TypeLiteral.of(String.class)).contains(converter));
        assertEquals(1, ctx.getPropertyConverters(TypeLiteral.of(String.class)).size());
        b = new DefaultConfigurationContextBuilder()
                .addPropertyConverters(TypeLiteral.of(String.class), converter);
        ctx = b.removePropertyConverters(TypeLiteral.of(String.class)).build();
        assertFalse(ctx.getPropertyConverters(TypeLiteral.of(String.class)).contains(converter));
        assertTrue(ctx.getPropertyConverters(TypeLiteral.of(String.class)).isEmpty());
    }

    @Test
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void removePropertyConverters_Array() throws Exception {
        PropertyConverter converter = (value, context) -> value.toLowerCase();
        ConfigurationContextBuilder b = new DefaultConfigurationContextBuilder()
                .addPropertyConverters(TypeLiteral.of(String.class), converter);
        ConfigurationContext ctx = b.build();
        assertTrue(ctx.getPropertyConverters(TypeLiteral.of(String.class)).contains(converter));
        assertEquals(1, ctx.getPropertyConverters(TypeLiteral.of(String.class)).size());
        b = new DefaultConfigurationContextBuilder()
                .addPropertyConverters(TypeLiteral.of(String.class), converter);
        ctx = b.removePropertyConverters(TypeLiteral.of(String.class), converter).build();
        assertFalse(ctx.getPropertyConverters(TypeLiteral.of(String.class)).contains(converter));
        assertTrue(ctx.getPropertyConverters(TypeLiteral.of(String.class)).isEmpty());
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void removePropertyConverters_Collection() throws Exception {
        PropertyConverter converter = (value, context) -> value.toLowerCase();
        ConfigurationContextBuilder b = new DefaultConfigurationContextBuilder()
                .addPropertyConverters(TypeLiteral.of(String.class), Arrays.<PropertyConverter<Object>>asList(new PropertyConverter[]{converter}));
        ConfigurationContext ctx = b.build();
        assertTrue(ctx.getPropertyConverters(TypeLiteral.of(String.class)).contains(converter));
        assertEquals(1, ctx.getPropertyConverters(TypeLiteral.of(String.class)).size());
        b = new DefaultConfigurationContextBuilder()
                .addPropertyConverters(TypeLiteral.of(String.class), Arrays.<PropertyConverter<Object>>asList(new PropertyConverter[]{converter}));
        ctx = b.removePropertyConverters(TypeLiteral.of(String.class), Arrays.<PropertyConverter<Object>>asList(new PropertyConverter[]{converter})).build();
        assertFalse(ctx.getPropertyConverters(TypeLiteral.of(String.class)).contains(converter));
        assertTrue(ctx.getPropertyConverters(TypeLiteral.of(String.class)).isEmpty());
    }

    @Test
    public void setPropertyValueCombinationPolicy() throws Exception {
        PropertyValueCombinationPolicy combPol = (currentValue, key, propertySource) -> currentValue;
        ConfigurationContextBuilder b = new DefaultConfigurationContextBuilder()
                .setPropertyValueCombinationPolicy(combPol);
        ConfigurationContext ctx = b.build();
        boolean caughtAlreadyBuilt = false;
        try {
            b.setPropertyValueCombinationPolicy(combPol);
        } catch (IllegalStateException e) {
            caughtAlreadyBuilt = true;
        }
        assertTrue(caughtAlreadyBuilt);
        assertEquals(ctx.getPropertyValueCombinationPolicy(), combPol);
    }

    @Test
    public void increasePriority() {
        ConfigurationContextBuilder b = new DefaultConfigurationContextBuilder();
        MockedPropertySource[] propertySources = new MockedPropertySource[10];
        for (int i = 0; i < propertySources.length; i++) {
            propertySources[i] = new MockedPropertySource("ps" + i, i);
        }
        b.addPropertySources(propertySources);
        b.increasePriority(propertySources[propertySources.length - 1]);
        for (int i = 0; i < propertySources.length; i++) {
            assertEquals(propertySources[i], b.getPropertySources().get(i));
        }
        b.increasePriority(propertySources[propertySources.length - 2]).build();
        for (int i = 0; i < propertySources.length - 2; i++) {
            assertEquals(propertySources[i], b.getPropertySources().get(i));
        }
        assertEquals(propertySources[propertySources.length - 1], b.getPropertySources().get(propertySources.length - 2));
        assertEquals(propertySources[propertySources.length - 2], b.getPropertySources().get(propertySources.length - 1));
        boolean caughtAlreadyBuilt = false;
        try {
            b.increasePriority(propertySources[propertySources.length - 2]);
        } catch (IllegalStateException e) {
            caughtAlreadyBuilt = true;
        }
        assertTrue(caughtAlreadyBuilt);
    }

    @Test
    public void decreasePriority() {
        ConfigurationContextBuilder b = new DefaultConfigurationContextBuilder();
        MockedPropertySource[] propertySources = new MockedPropertySource[10];
        for (int i = 0; i < propertySources.length; i++) {
            propertySources[i] = new MockedPropertySource("ps" + i, i);
        }
        b.addPropertySources(propertySources);
        b.decreasePriority(propertySources[0]);
        for (int i = 0; i < propertySources.length; i++) {
            assertEquals(propertySources[i], b.getPropertySources().get(i));
        }
        b.decreasePriority(propertySources[1]).build();
        for (int i = 2; i < propertySources.length; i++) {
            assertEquals(propertySources[i], b.getPropertySources().get(i));
        }
        assertEquals(propertySources[0], b.getPropertySources().get(1));
        assertEquals(propertySources[1], b.getPropertySources().get(0));
        boolean caughtAlreadyBuilt = false;
        try {
            b.decreasePriority(propertySources[1]);
        } catch (IllegalStateException e) {
            caughtAlreadyBuilt = true;
        }
        assertTrue(caughtAlreadyBuilt);
    }

    @Test
    public void lowestPriority() {
        // setup
        ConfigurationContextBuilder b = new DefaultConfigurationContextBuilder();
        MockedPropertySource[] propertySources = new MockedPropertySource[10];
        for (int i = 0; i < propertySources.length; i++) {
            propertySources[i] = new MockedPropertySource("ps" + i, i);
        }
        b.addPropertySources(propertySources);
        // test
        b.lowestPriority(propertySources[0]);
        for (int i = 0; i < propertySources.length; i++) {
            assertEquals(propertySources[i], b.getPropertySources().get(i));
        }
        b.lowestPriority(propertySources[1]);
        for (int i = 2; i < propertySources.length; i++) {
            assertEquals(propertySources[i], b.getPropertySources().get(i));
        }
        assertEquals(propertySources[0], b.getPropertySources().get(1));
        assertEquals(propertySources[1], b.getPropertySources().get(0));
        b.lowestPriority(propertySources[5]).build();
        assertEquals(propertySources[5], b.getPropertySources().get(0));
        boolean caughtAlreadyBuilt = false;
        try {
            b.lowestPriority(propertySources[5]);
        } catch (IllegalStateException e) {
            caughtAlreadyBuilt = true;
        }
        assertTrue(caughtAlreadyBuilt);
    }

    @Test
    public void highestPriority() {
        // setup
        ConfigurationContextBuilder b = new DefaultConfigurationContextBuilder();
        MockedPropertySource[] propertySources = new MockedPropertySource[10];
        for (int i = 0; i < propertySources.length; i++) {
            propertySources[i] = new MockedPropertySource("ps" + i, i);
        }
        b.addPropertySources(propertySources);
        // test
        b.highestPriority(propertySources[propertySources.length - 1]);
        for (int i = 0; i < propertySources.length; i++) {
            assertEquals(propertySources[i], b.getPropertySources().get(i));
        }
        b.highestPriority(propertySources[propertySources.length - 2]);
        for (int i = 0; i < propertySources.length - 2; i++) {
            assertEquals(propertySources[i], b.getPropertySources().get(i));
        }
        assertEquals(propertySources[propertySources.length - 2], b.getPropertySources().get(propertySources.length - 1));
        assertEquals(propertySources[propertySources.length - 1], b.getPropertySources().get(propertySources.length - 2));
        b.highestPriority(propertySources[5]).build();
        assertEquals(propertySources[5], b.getPropertySources().get(propertySources.length - 1));
        boolean caughtAlreadyBuilt = false;
        try {
            b.highestPriority(propertySources[5]);
        } catch (IllegalStateException e) {
            caughtAlreadyBuilt = true;
        }
        assertTrue(caughtAlreadyBuilt);
    }

    @Test
    public void sortPropertySources() {
        // setup
        ConfigurationContextBuilder b = new DefaultConfigurationContextBuilder();
        MockedPropertySource[] propertySources = new MockedPropertySource[10];
        for (int i = 0; i < propertySources.length; i++) {
            propertySources[i] = new MockedPropertySource("ps" + i, i);
        }
        b.addPropertySources(propertySources);
        Comparator<PropertySource> psComp = (o1, o2) -> o1.toString().compareTo(o2.toString());
        // test 
        assertEquals(DefaultConfigurationContextBuilder.class, b.sortPropertySources(psComp).getClass());
        Arrays.sort(propertySources, psComp);
        for (int i = 0; i < propertySources.length; i++) {
            assertEquals(propertySources[i], b.getPropertySources().get(i));
        }
    }

    @Test
    public void sortPropertyFilter() {
        // setup
        ConfigurationContextBuilder b = new DefaultConfigurationContextBuilder();
        PropertyFilter[] propertyFilters = new PropertyFilter[10];
        for (int i = 0; i < propertyFilters.length; i++) {
            propertyFilters[i] = (value, context) -> value.toBuilder().setValue(toString() + " - ").build();
        }

        b.addPropertyFilters(propertyFilters);
        Comparator<PropertyFilter> pfComp = (o1, o2) -> o1.toString().compareTo(o2.toString());
        //test
        assertEquals(DefaultConfigurationContextBuilder.class, b.sortPropertyFilter(pfComp).getClass());
        Arrays.sort(propertyFilters, pfComp);
        for (int i = 0; i < propertyFilters.length; i++) {
            assertEquals(propertyFilters[i], b.getPropertyFilters().get(i));
        }
    }

    @Test
    public void build() throws Exception {
        ConfigurationContextBuilder b = new DefaultConfigurationContextBuilder();
        ConfigurationContext ctx = b.build();
        assertNotNull(ctx);
        assertTrue(ctx.getPropertySources().isEmpty());
        assertTrue(ctx.getPropertyFilters().isEmpty());
        boolean caughtAlreadyBuilt = false;
        try {
            b.build();
        } catch (IllegalStateException e) {
            caughtAlreadyBuilt = true;
        }
        assertTrue(caughtAlreadyBuilt);
    }

    @Test
    public void testRemoveAllFilters() throws Exception {
        ConfigurationContextBuilder b = new DefaultConfigurationContextBuilder();
        b.addPropertyFilters((value, context) -> value.toBuilder().setValue(toString() + " - ").build());
        assertFalse(b.getPropertyFilters().isEmpty());
        b.removePropertyFilters(b.getPropertyFilters());
        assertTrue(b.getPropertyFilters().isEmpty());
    }

    @Test
    public void testRemoveAllSources() throws Exception {
        ConfigurationContextBuilder b = new DefaultConfigurationContextBuilder();
        b.addPropertySources(new MockedPropertySource());
        assertFalse(b.getPropertySources().isEmpty());
        b.removePropertySources(b.getPropertySources());
        assertTrue(b.getPropertyFilters().isEmpty());
    }

    @Test
    public void testResetContext() throws Exception {
        PropertyConverter converter = (value, context) -> value.toLowerCase();
        DefaultConfigurationContextBuilder b = new DefaultConfigurationContextBuilder();
        ConfigurationContext empty = b.build();

        b = new DefaultConfigurationContextBuilder();
        b.addPropertyFilters((value, context) -> value.toBuilder().setValue(toString() + " - ").build());
        b.addPropertySources(new MockedPropertySource());
        b.addPropertyConverters(TypeLiteral.of(String.class), converter);
        ConfigurationContext full = b.build();

        boolean caughtAlreadyBuilt = false;
        try {
            b.resetWithConfigurationContext(empty);
        } catch (IllegalStateException e) {
            caughtAlreadyBuilt = true;
        }
        assertTrue(caughtAlreadyBuilt);

        b = new DefaultConfigurationContextBuilder();
        b.addPropertyFilters((value, context) -> value.toBuilder().setValue(toString() + " - ").build());
        b.addPropertySources(new MockedPropertySource());
        b.addPropertyConverters(TypeLiteral.of(String.class), converter);
        b.resetWithConfigurationContext(empty);
        assertTrue(b.getPropertyConverter().isEmpty());
        assertTrue(b.getPropertySources().isEmpty());
        assertTrue(b.getPropertyFilters().isEmpty());
        b.resetWithConfigurationContext(full).build();
        assertFalse(b.getPropertyConverter().isEmpty());
        assertFalse(b.getPropertySources().isEmpty());
        assertFalse(b.getPropertyFilters().isEmpty());

    }

    @Test
    public void testLoadDefaults() throws Exception {
        DefaultConfigurationContextBuilder b = new DefaultConfigurationContextBuilder();
        b.build();
        boolean caughtAlreadyBuilt = false;
        try {
            b.loadDefaults();
        } catch (IllegalStateException e) {
            caughtAlreadyBuilt = true;
        }
        assertTrue(caughtAlreadyBuilt);

        ConfigurationContext ctx = new DefaultConfigurationContextBuilder().loadDefaults().build();
        assertFalse(ctx.getPropertyConverters().isEmpty());
        assertFalse(ctx.getPropertyFilters().isEmpty());
        assertFalse(ctx.getPropertySources().isEmpty());
    }
    
    
    @Test
    public void testAddDefaultPropertyConverters() throws Exception {
        DefaultConfigurationContextBuilder b = new DefaultConfigurationContextBuilder();
        b.build();
        boolean caughtAlreadyBuilt = false;
        try {
            b.addDefaultPropertyConverters();
        } catch (IllegalStateException e) {
            caughtAlreadyBuilt = true;
        }
        assertTrue(caughtAlreadyBuilt);

        ConfigurationContext ctx = new DefaultConfigurationContextBuilder().addDefaultPropertyConverters().build();
        assertFalse(ctx.getPropertyConverters().isEmpty());
        assertNotNull(ctx.getPropertyConverters(TypeLiteral.of(Integer.class)));
    }
    
        @Test
    public void testAddDefaultPropertyFilters() throws Exception {
        DefaultConfigurationContextBuilder b = new DefaultConfigurationContextBuilder();
        b.build();
        boolean caughtAlreadyBuilt = false;
        try {
            b.addDefaultPropertyFilters();
        } catch (IllegalStateException e) {
            caughtAlreadyBuilt = true;
        }
        assertTrue(caughtAlreadyBuilt);

        ConfigurationContext ctx = new DefaultConfigurationContextBuilder().addDefaultPropertyFilters().build();
        assertFalse(ctx.getPropertyFilters().isEmpty());
    }
        
        @Test
    public void testAddDefaultPropertySources() throws Exception {
        DefaultConfigurationContextBuilder b = new DefaultConfigurationContextBuilder();
        b.build();
        boolean caughtAlreadyBuilt = false;
        try {
            b.addDefaultPropertySources();
        } catch (IllegalStateException e) {
            caughtAlreadyBuilt = true;
        }
        assertTrue(caughtAlreadyBuilt);

        ConfigurationContext ctx = new DefaultConfigurationContextBuilder().addDefaultPropertySources().build();
        assertFalse(ctx.getPropertySources().isEmpty());
        assertNotNull(ctx.getPropertySource("environment-properties"));
    }
            
        @Test
    public void testAddCorePropertyReources() throws Exception {
        DefaultConfigurationContextBuilder b = new DefaultConfigurationContextBuilder();
        List<PropertySource> ps = new ArrayList<>();
        b.addCorePropertyResources(ps);
        assertFalse(ps.isEmpty());
        
    }
}
