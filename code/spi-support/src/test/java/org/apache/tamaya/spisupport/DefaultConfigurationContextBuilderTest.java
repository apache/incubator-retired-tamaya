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

import org.apache.tamaya.spi.*;

import static org.assertj.core.api.Assertions.*;

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
        assertThat(b.build()).isEqualTo(context);
        boolean caughtAlreadyBuilt = false;
        try {
            b.setContext(context);
        } catch (IllegalStateException e) {
            caughtAlreadyBuilt = true;
        }
        assertThat(caughtAlreadyBuilt).isTrue();
        b = new DefaultConfigurationContextBuilder(context);
        assertThat(b.build()).isEqualTo(context);
    }

    @Test
    public void addPropertySources_Array() throws Exception {
        PropertySource testPS2 = new MockedPropertySource("addPropertySources_Array", 1);
        ConfigurationContextBuilder b = new DefaultConfigurationContextBuilder()
                .addPropertySources(testPropertySource, testPS2);
        ConfigurationContext ctx = b.build();
        assertThat(ctx.getPropertySources()).hasSize(2);
        assertThat(ctx.getPropertySources().contains(testPropertySource)).isTrue();
        assertThat(((DefaultConfigurationContextBuilder)b).getPropertySource(testPropertySource.getName())).isNotNull();
        assertThat(ctx.getPropertySources().contains(testPS2)).isTrue();
        assertThat(((DefaultConfigurationContextBuilder)b).getPropertySource(testPS2.getName())).isNotNull();
        // Ensure no sorting happens during add, so switch ordinals!
        testPS2 = new MockedPropertySource("addPropertySources_Array", 1);
        b = new DefaultConfigurationContextBuilder();
        ctx = b.addPropertySources(testPS2, testPropertySource).build();
        assertThat(ctx.getPropertySources()).hasSize(2);
        assertThat(ctx.getPropertySources().contains(testPropertySource)).isTrue();
        assertThat(((DefaultConfigurationContextBuilder)b).getPropertySource(testPropertySource.getName())).isNotNull();
        assertThat(ctx.getPropertySources().contains(testPS2)).isTrue();
        assertThat(((DefaultConfigurationContextBuilder)b).getPropertySource(testPS2.getName())).isNotNull();
        assertThat("MockedPropertySource").isEqualTo(ctx.getPropertySources().get(1).getName());
        assertThat("addPropertySources_Array").isEqualTo(ctx.getPropertySources().get(0).getName());
    }

    @Test
    public void addPropertySources_Collection() throws Exception {
        PropertySource testPS2 = new MockedPropertySource("addPropertySources_Collection", 1);
        ConfigurationContextBuilder b = new DefaultConfigurationContextBuilder()
                .addPropertySources(Arrays.asList(new PropertySource[]{testPropertySource, testPS2}));
        ConfigurationContext ctx = b.build();
        assertThat(ctx.getPropertySources()).hasSize(2);
        assertThat(ctx.getPropertySources().contains(testPropertySource)).isTrue();
        assertThat(((DefaultConfigurationContextBuilder)b).getPropertySource(testPropertySource.getName())).isNotNull();
        assertThat(ctx.getPropertySources().contains(testPS2)).isTrue();
        assertThat(((DefaultConfigurationContextBuilder)b).getPropertySource(testPS2.getName())).isNotNull();
        assertThat("MockedPropertySource").isEqualTo(ctx.getPropertySources().get(0).getName());
        assertThat("addPropertySources_Collection").isEqualTo(ctx.getPropertySources().get(1).getName());
        // Ensure no sorting happens during add, so switch ordinals!
        testPS2 = new MockedPropertySource("addPropertySources_Collection", 1);
        ctx = new DefaultConfigurationContextBuilder()
                .addPropertySources(Arrays.asList(new PropertySource[]{testPS2, testPropertySource})).build();
        assertThat(ctx.getPropertySources()).hasSize(2);
        assertThat(ctx.getPropertySources().contains(testPropertySource)).isTrue();
        assertThat(((DefaultConfigurationContextBuilder)b).getPropertySource(testPropertySource.getName())).isNotNull();
        assertThat(ctx.getPropertySources().contains(testPS2)).isTrue();
        assertThat(((DefaultConfigurationContextBuilder)b).getPropertySource(testPS2.getName())).isNotNull();
        assertThat("MockedPropertySource").isEqualTo(ctx.getPropertySources().get(1).getName());
        assertThat("addPropertySources_Collection").isEqualTo(ctx.getPropertySources().get(0).getName());
    }

    @Test
    public void removePropertySources_Array() throws Exception {
        PropertySource testPS2 = new MockedPropertySource("removePropertySources_Array", 1);
        ConfigurationContextBuilder b = new DefaultConfigurationContextBuilder()
                .addPropertySources(testPropertySource, testPS2);
        ConfigurationContext ctx = b.build();
        assertThat(ctx.getPropertySources()).hasSize(2);
        assertThat(ctx.getPropertySources().contains(testPropertySource)).isTrue();
        assertThat(((DefaultConfigurationContextBuilder)b).getPropertySource(testPropertySource.getName())).isNotNull();
        assertThat(ctx.getPropertySources().contains(testPS2)).isTrue();
        assertThat(((DefaultConfigurationContextBuilder)b).getPropertySource(testPS2.getName())).isNotNull();
        b = new DefaultConfigurationContextBuilder()
                .addPropertySources(testPropertySource, testPS2);
        ctx = b.removePropertySources(testPropertySource).build();
        assertThat(ctx.getPropertySources().contains(testPropertySource)).isFalse();
        //Throws an exception
        //assertThat(((DefaultConfigurationContextBuilder)b).getPropertySource(testPropertySource.getName())).isNull();
        assertThat(ctx.getPropertySources().contains(testPS2)).isTrue();
        assertThat(((DefaultConfigurationContextBuilder)b).getPropertySource(testPS2.getName())).isNotNull();
        assertThat(ctx.getPropertySources()).hasSize(1);
    }

    @Test
    public void removePropertySources_Collection() throws Exception {
        PropertySource testPS2 = new MockedPropertySource("removePropertySources_Array", 1);
        ConfigurationContextBuilder b = new DefaultConfigurationContextBuilder()
                .addPropertySources(testPropertySource, testPS2);
        ConfigurationContext ctx = b.build();
        assertThat(ctx.getPropertySources()).hasSize(2);
        assertThat(ctx.getPropertySources().contains(testPropertySource)).isTrue();
        assertThat(((DefaultConfigurationContextBuilder)b).getPropertySource(testPropertySource.getName())).isNotNull();
        assertThat(ctx.getPropertySources().contains(testPS2)).isTrue();
        assertThat(ctx.getPropertySource(testPS2.getName())).isNotNull();
        b = new DefaultConfigurationContextBuilder()
                .addPropertySources(testPropertySource, testPS2);
        ctx = b.removePropertySources(testPropertySource).build();
        assertThat(ctx.getPropertySources()).hasSize(1);
        assertThat(ctx.getPropertySources().contains(testPropertySource)).isFalse();
        assertThat(ctx.getPropertySources().contains(testPS2)).isTrue();
        assertThat(ctx.getPropertySource(testPS2.getName())).isNotNull();
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void missingPropertySource() throws Exception {
        PropertySource testPS2 = new MockedPropertySource("removePropertySources_Array", 1);
        ConfigurationContextBuilder b = new DefaultConfigurationContextBuilder()
                .addPropertySources(testPropertySource, testPS2);
        assertThat(((DefaultConfigurationContextBuilder)b).getPropertySource("missing")).isNull();
    }

    @Test
    public void addPropertyFilters_Array() throws Exception {
        PropertyFilter filter1 = (value, ctx) -> value;
        PropertyFilter filter2 = (value, ctx) -> value;
        ConfigurationContextBuilder b = new DefaultConfigurationContextBuilder();
        b.addPropertyFilters(filter1, filter2);
        ConfigurationContext ctx = b.build();
        boolean caughtAlreadyBuilt = false;
        try {
            b.addPropertyFilters(filter1, filter2);
        } catch (IllegalStateException e) {
            caughtAlreadyBuilt = true;
        }
        assertThat(caughtAlreadyBuilt).isTrue();
        assertThat(ctx.getPropertyFilters().contains(filter1)).isTrue();
        assertThat(ctx.getPropertyFilters().contains(filter2)).isTrue();
        assertThat(ctx.getPropertyFilters()).hasSize(2);
        b = new DefaultConfigurationContextBuilder();
        b.addPropertyFilters(filter1, filter2);
        b.addPropertyFilters(filter1, filter2);
        assertThat(ctx.getPropertyFilters()).hasSize(2);
    }

    @Test
    public void addPropertyFilters_Collection() throws Exception {
        PropertyFilter filter1 = (value, ctx) -> value;
        PropertyFilter filter2 = (value, ctx) -> value;
        ConfigurationContextBuilder b = new DefaultConfigurationContextBuilder();
        b.addPropertyFilters(Arrays.asList(new PropertyFilter[]{filter1, filter2}));
        ConfigurationContext ctx = b.build();
        boolean caughtAlreadyBuilt = false;
        try {
            b.addPropertyFilters(Arrays.asList(new PropertyFilter[]{filter1, filter2}));
        } catch (IllegalStateException e) {
            caughtAlreadyBuilt = true;
        }
        assertThat(caughtAlreadyBuilt).isTrue();
        assertThat(ctx.getPropertyFilters().contains(filter1)).isTrue();
        assertThat(ctx.getPropertyFilters().contains(filter2)).isTrue();
        assertThat(ctx.getPropertyFilters()).hasSize(2);
        b = new DefaultConfigurationContextBuilder();
        b.addPropertyFilters(filter1, filter2);
        b.addPropertyFilters(filter1, filter2);
        assertThat(ctx.getPropertyFilters()).hasSize(2);
    }

    @Test
    public void removePropertyFilters_Array() throws Exception {
        PropertyFilter filter1 = (value, ctx) -> value;
        PropertyFilter filter2 = (value, ctx) -> value;
        ConfigurationContextBuilder b = new DefaultConfigurationContextBuilder()
                .addPropertyFilters(filter1, filter2);
        ConfigurationContext ctx = b.build();
        assertThat(ctx.getPropertyFilters().contains(filter1)).isTrue();
        assertThat(ctx.getPropertyFilters().contains(filter2)).isTrue();
        assertThat(ctx.getPropertyFilters()).hasSize(2);
        b = new DefaultConfigurationContextBuilder()
                .addPropertyFilters(filter1, filter2);
        ctx = b.removePropertyFilters(filter1).build();
        assertThat(ctx.getPropertyFilters()).hasSize(1);
        assertThat(ctx.getPropertyFilters().contains(filter1)).isFalse();
        assertThat(ctx.getPropertyFilters().contains(filter2)).isTrue();
        boolean caughtAlreadyBuilt = false;
        try {
            b.removePropertyFilters(filter1);
        } catch (IllegalStateException e) {
            caughtAlreadyBuilt = true;
        }
        assertThat(caughtAlreadyBuilt).isTrue();
    }

    @Test
    public void removePropertyFilters_Collection() throws Exception {
        PropertyFilter filter1 = (value, ctx) -> value;
        PropertyFilter filter2 = (value, ctx) -> value;
        ConfigurationContextBuilder b = new DefaultConfigurationContextBuilder()
                .addPropertyFilters(Arrays.asList(new PropertyFilter[]{filter1, filter2}));
        ConfigurationContext ctx = b.build();
        assertThat(ctx.getPropertyFilters().contains(filter1)).isTrue();
        assertThat(ctx.getPropertyFilters().contains(filter2)).isTrue();
        assertThat(ctx.getPropertyFilters()).hasSize(2);
        b = new DefaultConfigurationContextBuilder()
                .addPropertyFilters(Arrays.asList(new PropertyFilter[]{filter1, filter2}));
        ctx = b.removePropertyFilters(Arrays.asList(filter1)).build();
        assertThat(ctx.getPropertyFilters()).hasSize(1);
        assertThat(ctx.getPropertyFilters().contains(filter1)).isFalse();
        assertThat(ctx.getPropertyFilters().contains(filter2)).isTrue();
        boolean caughtAlreadyBuilt = false;
        try {
            b.removePropertyFilters(filter1);
        } catch (IllegalStateException e) {
            caughtAlreadyBuilt = true;
        }
        assertThat(caughtAlreadyBuilt).isTrue();
    }

    @Test
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void addPropertyConverters_Array() throws Exception {
        PropertyConverter converter = (value, ctx) -> value.toLowerCase();
        ConfigurationContextBuilder b = new DefaultConfigurationContextBuilder()
                .addPropertyConverters(TypeLiteral.of(String.class), converter);
        ConfigurationContext ctx = b.build();
        boolean caughtAlreadyBuilt = false;
        try {
            b.addPropertyConverters(TypeLiteral.of(String.class), converter);
        } catch (IllegalStateException e) {
            caughtAlreadyBuilt = true;
        }
        assertThat(caughtAlreadyBuilt).isTrue();
        assertThat(ctx.getPropertyConverters(TypeLiteral.of(String.class)).contains(converter)).isTrue();
        assertThat(ctx.getPropertyConverters()).hasSize(1);
        b = new DefaultConfigurationContextBuilder()
                .addPropertyConverters(TypeLiteral.of(String.class), converter);
        b.addPropertyConverters(TypeLiteral.of(String.class), converter);
        assertThat(ctx.getPropertyConverters()).hasSize(1);
    }

    @Test
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void addPropertyConverters_Collection() throws Exception {
        PropertyConverter converter = (value, ctx) -> value.toLowerCase();
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
        assertThat(caughtAlreadyBuilt).isTrue();
        assertThat(ctx.getPropertyConverters(TypeLiteral.of(String.class)).contains(converter)).isTrue();
        assertThat(1).isEqualTo(ctx.getPropertyConverters().size());
        b = new DefaultConfigurationContextBuilder()
                .addPropertyConverters(TypeLiteral.of(String.class),
                        Arrays.<PropertyConverter<Object>>asList(new PropertyConverter[]{converter}));
        b.addPropertyConverters(TypeLiteral.of(String.class), converter);
        assertThat(1).isEqualTo(ctx.getPropertyConverters().size());
    }
    
    @Test
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void removePropertyConverters_Type() throws Exception {
        PropertyConverter converter = (value, ctx) -> value.toLowerCase();
        ConfigurationContextBuilder b = new DefaultConfigurationContextBuilder()
                .addPropertyConverters(TypeLiteral.of(String.class), converter);
        ConfigurationContext ctx = b.build();
        assertThat(ctx.getPropertyConverters(TypeLiteral.of(String.class)).contains(converter)).isTrue();
        assertThat(ctx.getPropertyConverters(TypeLiteral.of(String.class))).hasSize(1);
        b = new DefaultConfigurationContextBuilder()
                .addPropertyConverters(TypeLiteral.of(String.class), converter);
        ctx = b.removePropertyConverters(TypeLiteral.of(String.class)).build();
        assertThat(ctx.getPropertyConverters(TypeLiteral.of(String.class)).contains(converter)).isFalse();
        assertThat(ctx.getPropertyConverters(TypeLiteral.of(String.class)).isEmpty()).isTrue();
    }

    @Test
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void removePropertyConverters_Array() throws Exception {
        PropertyConverter converter = (value, ctx) -> value.toLowerCase();
        ConfigurationContextBuilder b = new DefaultConfigurationContextBuilder()
                .addPropertyConverters(TypeLiteral.of(String.class), converter);
        ConfigurationContext ctx = b.build();
        assertThat(ctx.getPropertyConverters(TypeLiteral.of(String.class)).contains(converter)).isTrue();
        assertThat(ctx.getPropertyConverters(TypeLiteral.of(String.class))).hasSize(1);
        b = new DefaultConfigurationContextBuilder()
                .addPropertyConverters(TypeLiteral.of(String.class), converter);
        ctx = b.removePropertyConverters(TypeLiteral.of(String.class), converter).build();
        assertThat(ctx.getPropertyConverters(TypeLiteral.of(String.class)).contains(converter)).isFalse();
        assertThat(ctx.getPropertyConverters(TypeLiteral.of(String.class)).isEmpty()).isTrue();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void removePropertyConverters_Collection() throws Exception {
        PropertyConverter converter = (value, ctx) -> value.toLowerCase();
        ConfigurationContextBuilder b = new DefaultConfigurationContextBuilder()
                .addPropertyConverters(TypeLiteral.of(String.class), Arrays.<PropertyConverter<Object>>asList(new PropertyConverter[]{converter}));
        ConfigurationContext ctx = b.build();
        assertThat(ctx.getPropertyConverters(TypeLiteral.of(String.class)).contains(converter)).isTrue();
        assertThat(ctx.getPropertyConverters(TypeLiteral.of(String.class))).hasSize(1);
        b = new DefaultConfigurationContextBuilder()
                .addPropertyConverters(TypeLiteral.of(String.class), Arrays.<PropertyConverter<Object>>asList(new PropertyConverter[]{converter}));
        ctx = b.removePropertyConverters(TypeLiteral.of(String.class), Arrays.<PropertyConverter<Object>>asList(new PropertyConverter[]{converter})).build();
        assertThat(ctx.getPropertyConverters(TypeLiteral.of(String.class)).contains(converter)).isFalse();
        assertThat(ctx.getPropertyConverters(TypeLiteral.of(String.class)).isEmpty()).isTrue();
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
        assertThat(caughtAlreadyBuilt).isTrue();
        assertThat(combPol).isEqualTo(ctx.getPropertyValueCombinationPolicy());
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
            assertThat(b.getPropertySources().get(i)).isEqualTo(propertySources[i]);
        }
        b.increasePriority(propertySources[propertySources.length - 2]).build();
        for (int i = 0; i < propertySources.length - 2; i++) {
            assertThat(b.getPropertySources().get(i)).isEqualTo(propertySources[i]);
        }
        assertThat(b.getPropertySources().get(propertySources.length - 2)).isEqualTo(propertySources[propertySources.length - 1]);
        assertThat(b.getPropertySources().get(propertySources.length - 1)).isEqualTo(propertySources[propertySources.length - 2]);
        boolean caughtAlreadyBuilt = false;
        try {
            b.increasePriority(propertySources[propertySources.length - 2]);
        } catch (IllegalStateException e) {
            caughtAlreadyBuilt = true;
        }
        assertThat(caughtAlreadyBuilt).isTrue();
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
            assertThat(b.getPropertySources().get(i)).isEqualTo(propertySources[i]);
        }
        b.decreasePriority(propertySources[1]).build();
        for (int i = 2; i < propertySources.length; i++) {
            assertThat(b.getPropertySources().get(i)).isEqualTo(propertySources[i]);
        }
        assertThat(b.getPropertySources().get(1)).isEqualTo(propertySources[0]);
        assertThat(b.getPropertySources().get(0)).isEqualTo(propertySources[1]);
        boolean caughtAlreadyBuilt = false;
        try {
            b.decreasePriority(propertySources[1]);
        } catch (IllegalStateException e) {
            caughtAlreadyBuilt = true;
        }
        assertThat(caughtAlreadyBuilt).isTrue();
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
            assertThat(b.getPropertySources().get(i)).isEqualTo(propertySources[i]);
        }
        b.lowestPriority(propertySources[1]);
        for (int i = 2; i < propertySources.length; i++) {
            assertThat(b.getPropertySources().get(i)).isEqualTo(propertySources[i]);
        }
        assertThat(b.getPropertySources().get(1)).isEqualTo(propertySources[0]);
        assertThat(b.getPropertySources().get(0)).isEqualTo(propertySources[1]);
        b.lowestPriority(propertySources[5]).build();
        assertThat(b.getPropertySources().get(0)).isEqualTo(propertySources[5]);
        boolean caughtAlreadyBuilt = false;
        try {
            b.lowestPriority(propertySources[5]);
        } catch (IllegalStateException e) {
            caughtAlreadyBuilt = true;
        }
        assertThat(caughtAlreadyBuilt).isTrue();
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
            assertThat(b.getPropertySources().get(i)).isEqualTo(propertySources[i]);
        }
        b.highestPriority(propertySources[propertySources.length - 2]);
        for (int i = 0; i < propertySources.length - 2; i++) {
            assertThat(b.getPropertySources().get(i)).isEqualTo(propertySources[i]);
        }
        assertThat(b.getPropertySources().get(propertySources.length - 1)).isEqualTo(propertySources[propertySources.length - 2]);
        assertThat(b.getPropertySources().get(propertySources.length - 2)).isEqualTo(propertySources[propertySources.length - 1]);
        b.highestPriority(propertySources[5]).build();
        assertThat(b.getPropertySources().get(propertySources.length - 1)).isEqualTo(propertySources[5]);
        boolean caughtAlreadyBuilt = false;
        try {
            b.highestPriority(propertySources[5]);
        } catch (IllegalStateException e) {
            caughtAlreadyBuilt = true;
        }
        assertThat(caughtAlreadyBuilt).isTrue();
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
        assertThat(b.sortPropertySources(psComp).getClass()).isEqualTo(DefaultConfigurationContextBuilder.class);
        Arrays.sort(propertySources, psComp);
        for (int i = 0; i < propertySources.length; i++) {
            assertThat(b.getPropertySources().get(i)).isEqualTo(propertySources[i]);
        }
    }

    @Test
    public void sortPropertyFilter() {
        // setup
        ConfigurationContextBuilder b = new DefaultConfigurationContextBuilder();
        PropertyFilter[] propertyFilters = new PropertyFilter[10];
        for (int i = 0; i < propertyFilters.length; i++) {
            propertyFilters[i] = (value, ctx) -> value.setValue(toString() + " - ");
        }

        b.addPropertyFilters(propertyFilters);
        Comparator<PropertyFilter> pfComp = (o1, o2) -> o1.toString().compareTo(o2.toString());
        //test
        assertThat(b.sortPropertyFilter(pfComp).getClass()).isEqualTo(DefaultConfigurationContextBuilder.class);
        Arrays.sort(propertyFilters, pfComp);
        for (int i = 0; i < propertyFilters.length; i++) {
            assertThat(b.getPropertyFilters().get(i)).isEqualTo(propertyFilters[i]);
        }
    }

    @Test
    public void build() throws Exception {
        ConfigurationContextBuilder b = new DefaultConfigurationContextBuilder();
        ConfigurationContext ctx = b.build();
        assertThat(ctx).isNotNull();
        assertThat(ctx.getPropertySources().isEmpty()).isTrue();
        assertThat(ctx.getPropertyFilters().isEmpty()).isTrue();
        boolean caughtAlreadyBuilt = false;
        try {
            b.build();
        } catch (IllegalStateException e) {
            caughtAlreadyBuilt = true;
        }
        assertThat(caughtAlreadyBuilt).isTrue();
    }

    @Test
    public void testRemoveAllFilters() throws Exception {
        ConfigurationContextBuilder b = new DefaultConfigurationContextBuilder();
        b.addPropertyFilters((value, ctx) -> value.setValue(toString() + " - "));
        assertThat(b.getPropertyFilters().isEmpty()).isFalse();
        b.removePropertyFilters(b.getPropertyFilters());
        assertThat(b.getPropertyFilters().isEmpty()).isTrue();
    }

    @Test
    public void testRemoveAllSources() throws Exception {
        ConfigurationContextBuilder b = new DefaultConfigurationContextBuilder();
        b.addPropertySources(new MockedPropertySource());
        assertThat(b.getPropertySources().isEmpty()).isFalse();
        b.removePropertySources(b.getPropertySources());
        assertThat(b.getPropertyFilters().isEmpty()).isTrue();
    }

    @Test
    public void testResetContext() throws Exception {
        PropertyConverter converter = (value, ctx) -> value.toLowerCase();
        DefaultConfigurationContextBuilder b = new DefaultConfigurationContextBuilder();
        ConfigurationContext empty = b.build();

        b = new DefaultConfigurationContextBuilder();
        b.addPropertyFilters((value, ctx) -> value.setValue(toString() + " - "));
        b.addPropertySources(new MockedPropertySource());
        b.addPropertyConverters(TypeLiteral.of(String.class), converter);
        ConfigurationContext full = b.build();

        boolean caughtAlreadyBuilt = false;
        try {
            b.resetWithConfigurationContext(empty);
        } catch (IllegalStateException e) {
            caughtAlreadyBuilt = true;
        }
        assertThat(caughtAlreadyBuilt).isTrue();

        b = new DefaultConfigurationContextBuilder();
        b.addPropertyFilters((value, ctx) -> value.setValue(toString() + " - "));
        b.addPropertySources(new MockedPropertySource());
        b.addPropertyConverters(TypeLiteral.of(String.class), converter);
        b.resetWithConfigurationContext(empty);
        assertThat(b.getPropertyConverter().isEmpty()).isTrue();
        assertThat(b.getPropertySources().isEmpty()).isTrue();
        assertThat(b.getPropertyFilters().isEmpty()).isTrue();
        b.resetWithConfigurationContext(full).build();
        assertThat(b.getPropertyConverter().isEmpty()).isFalse();
        assertThat(b.getPropertySources().isEmpty()).isFalse();
        assertThat(b.getPropertyFilters().isEmpty()).isFalse();

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
        assertThat(caughtAlreadyBuilt).isTrue();

        ConfigurationContext ctx = new DefaultConfigurationContextBuilder().loadDefaults().build();
        assertThat(ctx.getPropertyConverters().isEmpty()).isFalse();
        assertThat(ctx.getPropertyFilters().isEmpty()).isFalse();
        assertThat(ctx.getPropertySources().isEmpty()).isFalse();
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
        assertThat(caughtAlreadyBuilt).isTrue();

        ConfigurationContext ctx = new DefaultConfigurationContextBuilder().addDefaultPropertyConverters().build();
        assertThat(ctx.getPropertyConverters().isEmpty()).isFalse();
        assertThat(ctx.getPropertyConverters(TypeLiteral.of(Integer.class))).isNotNull();
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
        assertThat(caughtAlreadyBuilt).isTrue();

        ConfigurationContext ctx = new DefaultConfigurationContextBuilder().addDefaultPropertyFilters().build();
        assertThat(ctx.getPropertyFilters().isEmpty()).isFalse();
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
        assertThat(caughtAlreadyBuilt).isTrue();

        ConfigurationContext ctx = new DefaultConfigurationContextBuilder().addDefaultPropertySources().build();
        assertThat(ctx.getPropertySources().isEmpty()).isFalse();
        assertThat(ctx.getPropertySource("environment-properties")).isNotNull();
    }
            
        @Test
    public void testAddCorePropertyReources() throws Exception {
        DefaultConfigurationContextBuilder b = new DefaultConfigurationContextBuilder();
        List<PropertySource> ps = new ArrayList<>();
        b.addCorePropertyResources(ps);
        assertThat(ps.isEmpty()).isFalse();
        
    }
}
