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
package org.apache.tamaya.spisupport;

import java.util.Arrays;
import java.util.Collection;
import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;
import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.spi.ConfigurationBuilder;
import org.apache.tamaya.spi.*;
import org.junit.Test;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Tests for {@link  DefaultConfigurationBuilder} by atsticks on 06.09.16.
 */
public class DefaultConfigurationBuilderTest {

    private TestPropertySource testPropertySource = new TestPropertySource() {
    };

    @Test
    public void setContext() throws Exception {
        ConfigurationContext context = ConfigurationProvider.getConfiguration().getContext();
        ConfigurationBuilder b = new DefaultConfigurationBuilder()
                .setContext(context);
        assertEquals(context, b.build().getContext());
    }

    @Test
    public void setConfiguration() throws Exception {
        Configuration cfg = ConfigurationProvider.getConfiguration();
        ConfigurationBuilder b = new DefaultConfigurationBuilder()
                .setConfiguration(cfg);
        assertEquals(cfg, b.build());
    }

    @Test
    public void addRemovePropertySources_Array() throws Exception {
        PropertySource testPS2 = new TestPropertySource("addRemovePropertySources_Array");
        ConfigurationBuilder b = new DefaultConfigurationBuilder()
                .addPropertySources(testPropertySource, testPS2);
        Configuration cfg = b.build();
        ConfigurationContext ctx = cfg.getContext();
        assertEquals(2, ctx.getPropertySources().size());
        assertTrue(ctx.getPropertySources().contains(testPropertySource));
        assertTrue(ctx.getPropertySources().contains(testPS2));

        b = new DefaultConfigurationBuilder()
                .addPropertySources(testPropertySource, testPS2);
        cfg = b.removePropertySources(testPropertySource).build();
        ctx = cfg.getContext();
        assertEquals(1, ctx.getPropertySources().size());
        assertFalse(ctx.getPropertySources().contains(testPropertySource));
        assertTrue(ctx.getPropertySources().contains(testPS2));
    }

    @Test
    public void addRemovePropertySources_Collection() throws Exception {
        PropertySource testPS2 = new TestPropertySource("addRemovePropertySources_Collection");
        ConfigurationBuilder b = new DefaultConfigurationBuilder()
                .addPropertySources(Arrays.asList(testPropertySource, testPS2));
        Configuration cfg = b.build();
        ConfigurationContext ctx = cfg.getContext();
        assertEquals(2, ctx.getPropertySources().size());
        assertTrue(ctx.getPropertySources().contains(testPropertySource));
        assertTrue(ctx.getPropertySources().contains(testPS2));

        b = new DefaultConfigurationBuilder()
                .addPropertySources(testPropertySource, testPS2);
        cfg = b.removePropertySources(Arrays.asList(testPropertySource)).build();
        ctx = cfg.getContext();
        assertEquals(1, ctx.getPropertySources().size());
        assertFalse(ctx.getPropertySources().contains(testPropertySource));
        assertTrue(ctx.getPropertySources().contains(testPS2));
    }

    @Test
    public void addRemovePropertyFilters_Array() throws Exception {
        PropertyFilter filter1 = (value, context) -> value;
        PropertyFilter filter2 = (value, context) -> value;
        DefaultConfigurationBuilder b = new DefaultConfigurationBuilder();
        Configuration cfg = b.addPropertyFilters(filter1, filter2).build();
        ConfigurationContext ctx = cfg.getContext();
        assertTrue(ctx.getPropertyFilters().contains(filter1));
        assertTrue(ctx.getPropertyFilters().contains(filter2));
        assertEquals(2, ctx.getPropertyFilters().size());

        b = new DefaultConfigurationBuilder();
        b.addPropertyFilters(filter1, filter2);
        cfg = b.addPropertyFilters(filter1, filter2).build();
        ctx = cfg.getContext();
        assertEquals(2, ctx.getPropertyFilters().size());

        b = new DefaultConfigurationBuilder();
        b.addPropertyFilters(filter1, filter2);
        cfg = b.removePropertyFilters(filter1).build();
        ctx = cfg.getContext();
        assertEquals(1, ctx.getPropertyFilters().size());
        assertFalse(ctx.getPropertyFilters().contains(filter1));
        assertTrue(ctx.getPropertyFilters().contains(filter2));

    }

    @Test
    public void addRemovePropertyFilters_Collection() throws Exception {
        PropertyFilter filter1 = (value, context) -> value;
        PropertyFilter filter2 = (value, context) -> value;
        DefaultConfigurationBuilder b = new DefaultConfigurationBuilder();
        Configuration cfg = b.addPropertyFilters(Arrays.asList(filter1, filter2)).build();
        ConfigurationContext ctx = cfg.getContext();
        assertTrue(ctx.getPropertyFilters().contains(filter1));
        assertTrue(ctx.getPropertyFilters().contains(filter2));
        assertEquals(2, ctx.getPropertyFilters().size());

        b = new DefaultConfigurationBuilder();
        b.addPropertyFilters(Arrays.asList(filter1, filter2, filter1));
        cfg = b.addPropertyFilters(Arrays.asList(filter1, filter2)).build();
        ctx = cfg.getContext();
        assertEquals(2, ctx.getPropertyFilters().size());

        b = new DefaultConfigurationBuilder();
        b.addPropertyFilters(Arrays.asList(filter1, filter2));
        cfg = b.removePropertyFilters(Arrays.asList(filter1)).build();
        ctx = cfg.getContext();
        assertEquals(1, ctx.getPropertyFilters().size());
        assertFalse(ctx.getPropertyFilters().contains(filter1));
        assertTrue(ctx.getPropertyFilters().contains(filter2));

    }

    @Test
    public void increasePriority() {
        DefaultConfigurationBuilder b = new DefaultConfigurationBuilder();
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
        DefaultConfigurationBuilder b = new DefaultConfigurationBuilder();
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
        DefaultConfigurationBuilder b = new DefaultConfigurationBuilder();
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
        DefaultConfigurationBuilder b = new DefaultConfigurationBuilder();
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
        DefaultConfigurationBuilder b = new DefaultConfigurationBuilder();
        MockedPropertySource[] propertySources = new MockedPropertySource[10];
        for (int i = 0; i < propertySources.length; i++) {
            propertySources[i] = new MockedPropertySource("ps" + i, i);
        }
        b.addPropertySources(propertySources);
        Comparator<PropertySource> psComp = (o1, o2) -> o1.toString().compareTo(o2.toString());
        // test 
        assertEquals(DefaultConfigurationBuilder.class, b.sortPropertySources(psComp).getClass());
        Arrays.sort(propertySources, psComp);
        for (int i = 0; i < propertySources.length; i++) {
            assertEquals(propertySources[i], b.getPropertySources().get(i));
        }
    }

    @Test
    public void sortPropertyFilter() {
        // setup
        DefaultConfigurationBuilder b = new DefaultConfigurationBuilder();
        PropertyFilter[] propertyFilters = new PropertyFilter[10];
        for (int i = 0; i < propertyFilters.length; i++) {
            propertyFilters[i] = (value, context) -> value.toBuilder().setValue(toString() + " - ").build();
        }

        b.addPropertyFilters(propertyFilters);
        Comparator<PropertyFilter> pfComp = (o1, o2) -> o1.toString().compareTo(o2.toString());
        //test
        assertEquals(DefaultConfigurationBuilder.class, b.sortPropertyFilter(pfComp).getClass());
        Arrays.sort(propertyFilters, pfComp);
        for (int i = 0; i < propertyFilters.length; i++) {
            assertEquals(propertyFilters[i], b.getPropertyFilters().get(i));
        }
    }

    @Test
    public void addRemovePropertyConverter_Array() throws Exception {
        PropertyConverter converter1 = (value, context) -> value.toLowerCase();
        PropertyConverter converter2 = (value, context) -> value.toUpperCase();
        ConfigurationBuilder b = new DefaultConfigurationBuilder()
                .addPropertyConverters(TypeLiteral.of(String.class), converter1, converter2);
        Configuration cfg = b.build();
        ConfigurationContext ctx = cfg.getContext();
        Map<TypeLiteral<?>, Collection<PropertyConverter<?>>> buildConverters = b.getPropertyConverter();
        assertTrue(ctx.getPropertyConverters(TypeLiteral.of(String.class)).contains(converter1));
        assertTrue(ctx.getPropertyConverters(TypeLiteral.of(String.class)).contains(converter2));
        assertEquals(1, ctx.getPropertyConverters().size());
        assertEquals(2, ctx.getPropertyConverters(TypeLiteral.of(String.class)).size());
        assertTrue(buildConverters.get(TypeLiteral.of(String.class)).containsAll(
                        ctx.getPropertyConverters().get(TypeLiteral.of(String.class))));

        b = new DefaultConfigurationBuilder()
                .addPropertyConverters(TypeLiteral.of(String.class), converter1);
        cfg = b.addPropertyConverters(TypeLiteral.of(String.class), converter1).build();
        ctx = cfg.getContext();
        assertEquals(1, ctx.getPropertyConverters(TypeLiteral.of(String.class)).size());

        b = new DefaultConfigurationBuilder().addPropertyConverters(TypeLiteral.of(String.class), converter1, converter2);
        cfg = b.removePropertyConverters(TypeLiteral.of(String.class), converter1).build();
        ctx = cfg.getContext();
        assertFalse(ctx.getPropertyConverters(TypeLiteral.of(String.class)).contains(converter1));
        assertTrue(ctx.getPropertyConverters(TypeLiteral.of(String.class)).contains(converter2));

        b = new DefaultConfigurationBuilder().addPropertyConverters(TypeLiteral.of(String.class), converter1, converter2);
        cfg = b.removePropertyConverters(TypeLiteral.of(String.class)).build();
        ctx = cfg.getContext();
        assertTrue(ctx.getPropertyConverters(TypeLiteral.of(String.class)).isEmpty());
    }

    @Test
    public void addRemovePropertyConverter_Collection() throws Exception {
        PropertyConverter converter1 = (value, context) -> value.toLowerCase();
        PropertyConverter converter2 = (value, context) -> value.toUpperCase();
        ConfigurationBuilder b = new DefaultConfigurationBuilder()
                .addPropertyConverters(TypeLiteral.of(String.class), Arrays.asList(converter1, converter2));
        Configuration cfg = b.build();
        ConfigurationContext ctx = cfg.getContext();
        Map<TypeLiteral<?>, Collection<PropertyConverter<?>>> buildConverters = b.getPropertyConverter();
        assertTrue(ctx.getPropertyConverters(TypeLiteral.of(String.class)).contains(converter1));
        assertTrue(ctx.getPropertyConverters(TypeLiteral.of(String.class)).contains(converter2));
        assertEquals(1, ctx.getPropertyConverters().size());
        assertEquals(2, ctx.getPropertyConverters(TypeLiteral.of(String.class)).size());
        assertTrue(buildConverters.get(TypeLiteral.of(String.class)).containsAll(
                        ctx.getPropertyConverters().get(TypeLiteral.of(String.class))));

        b = new DefaultConfigurationBuilder()
                .addPropertyConverters(TypeLiteral.of(String.class), Arrays.asList(converter1));
        cfg = b.addPropertyConverters(TypeLiteral.of(String.class), Arrays.asList(converter1)).build();
        ctx = cfg.getContext();
        assertEquals(1, ctx.getPropertyConverters(TypeLiteral.of(String.class)).size());

        b = new DefaultConfigurationBuilder().addPropertyConverters(TypeLiteral.of(String.class), Arrays.asList(converter1, converter2));
        cfg = b.removePropertyConverters(TypeLiteral.of(String.class), Arrays.asList(converter1)).build();
        ctx = cfg.getContext();
        assertFalse(ctx.getPropertyConverters(TypeLiteral.of(String.class)).contains(converter1));
        assertTrue(ctx.getPropertyConverters(TypeLiteral.of(String.class)).contains(converter2));

        b = new DefaultConfigurationBuilder().addPropertyConverters(TypeLiteral.of(String.class), Arrays.asList(converter1, converter2));
        cfg = b.removePropertyConverters(TypeLiteral.of(String.class)).build();
        ctx = cfg.getContext();
        assertTrue(ctx.getPropertyConverters(TypeLiteral.of(String.class)).isEmpty());
    }

    @Test
    public void setPropertyValueCombinationPolicy() throws Exception {
        PropertyValueCombinationPolicy combPol = (currentValue, key, propertySource) -> currentValue;
        ConfigurationBuilder b = new DefaultConfigurationBuilder()
                .setPropertyValueCombinationPolicy(combPol);
        Configuration cfg = b.build();
        ConfigurationContext ctx = cfg.getContext();
        assertEquals(ctx.getPropertyValueCombinationPolicy(), combPol);
    }

    @Test
    public void build() throws Exception {
        assertNotNull(new DefaultConfigurationBuilder().build());
    }

    @Test
    public void bla() throws Exception {
        ConfigurationBuilder builder = ConfigurationProvider.getConfigurationBuilder();
        builder.addDefaultPropertyConverters();
    }

    private static class TestPropertySource implements PropertySource {

        private String id;

        public TestPropertySource() {
            this(null);
        }

        public TestPropertySource(String id) {
            this.id = id;
        }

        @Override
        public int getOrdinal() {
            return 200;
        }

        @Override
        public String getName() {
            return id != null ? id : "TestPropertySource";
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
