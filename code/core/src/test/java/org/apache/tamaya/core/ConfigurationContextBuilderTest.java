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

import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;
import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.spi.*;
import org.apache.tamaya.spisupport.DefaultConfigurationContextBuilder;
import org.junit.Test;

import java.util.Arrays;
import java.util.Comparator;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests for {@link ConfigurationContextBuilder} by atsticks on 06.09.16.
 */
@Deprecated
public class ConfigurationContextBuilderTest {

    private TestPropertySource testPropertySource = new TestPropertySource(){};

    @Test
    public void setContext() throws Exception {
        ConfigurationContext context = Configuration.current().getContext();
        ConfigurationContextBuilder b = ConfigurationProvider.getConfigurationContextBuilder()
                .setContext(context);
        assertThat(b.build()).isEqualTo(context);
    }

    @Test
    public void addPropertySources_Array() throws Exception {
        PropertySource testPS2 = new TestPropertySource("addPropertySources_Array", 1);
        ConfigurationContextBuilder b = new DefaultConfigurationContextBuilder()
                .addPropertySources(testPropertySource, testPS2);
        ConfigurationContext ctx = b.build();
        assertThat(ctx.getPropertySources()).hasSize(2);
        assertThat(ctx.getPropertySources().contains(testPropertySource)).isTrue();
        assertThat(ctx.getPropertySources().contains(testPS2)).isTrue();
        // Ensure no sorting happens during add, so switch ordinals!
        testPS2 = new TestPropertySource("addPropertySources_Array", 1);
        b = ConfigurationProvider.getConfigurationContextBuilder()
                .addPropertySources(testPS2, testPropertySource);
        ctx = b.build();
        assertThat(ctx.getPropertySources()).hasSize(2);
        assertThat(ctx.getPropertySources().contains(testPropertySource)).isTrue();
        assertThat(ctx.getPropertySources().contains(testPS2)).isTrue();
        assertThat("TestPropertySource").isEqualTo(ctx.getPropertySources().get(1).getName());
        assertThat("addPropertySources_Array").isEqualTo(ctx.getPropertySources().get(0).getName());
    }

    @Test
    public void addPropertySources_Collection() throws Exception {
        PropertySource testPS2 = new TestPropertySource("addPropertySources_Collection", 1);
        ConfigurationContextBuilder b = new DefaultConfigurationContextBuilder()
                .addPropertySources(Arrays.asList(new PropertySource[]{testPropertySource, testPS2}));
        ConfigurationContext ctx = b.build();
        assertThat(ctx.getPropertySources()).hasSize(2);
        assertThat(ctx.getPropertySources().contains(testPropertySource)).isTrue();
        assertThat(ctx.getPropertySources().contains(testPS2)).isTrue();
        assertThat("TestPropertySource").isEqualTo(ctx.getPropertySources().get(0).getName());
        assertThat("addPropertySources_Collection").isEqualTo(ctx.getPropertySources().get(1).getName());
        // Ensure no sorting happens during add, so switch ordinals!
        testPS2 = new TestPropertySource("addPropertySources_Collection", 1);
        b = ConfigurationProvider.getConfigurationContextBuilder()
                .addPropertySources(Arrays.asList(new PropertySource[]{testPS2, testPropertySource}));
        ctx = b.build();
        assertThat(ctx.getPropertySources()).hasSize(2);
        assertThat(ctx.getPropertySources().contains(testPropertySource)).isTrue();
        assertThat(ctx.getPropertySources().contains(testPS2)).isTrue();
        assertThat("TestPropertySource").isEqualTo(ctx.getPropertySources().get(1).getName());
        assertThat("addPropertySources_Collection").isEqualTo(ctx.getPropertySources().get(0).getName());
    }

    @Test
    public void removePropertySources_Array() throws Exception {
        PropertySource testPS2 = new TestPropertySource("removePropertySources_Array", 1);
        ConfigurationContextBuilder b = ConfigurationProvider.getConfigurationContextBuilder()
                .addPropertySources(testPropertySource, testPS2);
        ConfigurationContext ctx = b.build();
        assertThat(ctx.getPropertySources()).hasSize(2);
        assertThat(ctx.getPropertySources().contains(testPropertySource)).isTrue();
        assertThat(ctx.getPropertySources().contains(testPS2)).isTrue();
        b = ConfigurationProvider.getConfigurationContextBuilder()
                .addPropertySources(testPropertySource, testPS2);
        b.removePropertySources(testPropertySource);
        ctx = b.build();
        assertThat(ctx.getPropertySources().contains(testPropertySource)).isFalse();
        assertThat(ctx.getPropertySources().contains(testPS2)).isTrue();
        assertThat(ctx.getPropertySources()).hasSize(1);
    }

    @Test
    public void removePropertySources_Collection() throws Exception {
        PropertySource testPS2 = new TestPropertySource("removePropertySources_Array", 1);
        ConfigurationContextBuilder b = ConfigurationProvider.getConfigurationContextBuilder()
                .addPropertySources(testPropertySource, testPS2);
        ConfigurationContext ctx = b.build();
        assertThat(ctx.getPropertySources()).hasSize(2);
        assertThat(ctx.getPropertySources().contains(testPropertySource)).isTrue();
        assertThat(ctx.getPropertySources().contains(testPS2)).isTrue();
        b = ConfigurationProvider.getConfigurationContextBuilder()
                .addPropertySources(testPropertySource, testPS2);
        b.removePropertySources(testPropertySource);
        ctx = b.build();
        assertThat(ctx.getPropertySources()).hasSize(1);
        assertThat(ctx.getPropertySources().contains(testPropertySource)).isFalse();
        assertThat(ctx.getPropertySources().contains(testPS2)).isTrue();
    }

    @Test
    public void addPropertyFilters_Array() throws Exception {
        PropertyFilter filter1 = (value, ctx) -> value;
        PropertyFilter filter2 = (value, ctx) -> value;
        ConfigurationContextBuilder b = ConfigurationProvider.getConfigurationContextBuilder();
        b.addPropertyFilters(filter1, filter2);
        ConfigurationContext ctx = b.build();
        assertThat(ctx.getPropertyFilters().contains(filter1)).isTrue();
        assertThat(ctx.getPropertyFilters().contains(filter2)).isTrue();
        assertThat(ctx.getPropertyFilters()).hasSize(2);
        b = ConfigurationProvider.getConfigurationContextBuilder();
        b.addPropertyFilters(filter1, filter2);
        b.addPropertyFilters(filter1, filter2);
        assertThat(ctx.getPropertyFilters()).hasSize(2);
    }

    @Test
    public void addPropertyFilters_Collection() throws Exception {
        PropertyFilter filter1 = (value, ctx) -> value;
        PropertyFilter filter2 = (value, ctx) -> value;
        ConfigurationContextBuilder b = ConfigurationProvider.getConfigurationContextBuilder();
        b.addPropertyFilters(Arrays.asList(new PropertyFilter[]{filter1, filter2}));
        ConfigurationContext ctx = b.build();
        assertThat(ctx.getPropertyFilters().contains(filter1)).isTrue();
        assertThat(ctx.getPropertyFilters().contains(filter2)).isTrue();
        assertThat(ctx.getPropertyFilters()).hasSize(2);
        b = ConfigurationProvider.getConfigurationContextBuilder();
        b.addPropertyFilters(filter1, filter2);
        b.addPropertyFilters(filter1, filter2);
        assertThat(ctx.getPropertyFilters()).hasSize(2);
    }

    @Test
    public void removePropertyFilters_Array() throws Exception {
        PropertyFilter filter1 = (value, ctx) -> value;
        PropertyFilter filter2 = (value, ctx) -> value;
        ConfigurationContextBuilder b = ConfigurationProvider.getConfigurationContextBuilder()
                .addPropertyFilters(filter1, filter2);
        ConfigurationContext ctx = b.build();
        assertThat(ctx.getPropertyFilters().contains(filter1)).isTrue();
        assertThat(ctx.getPropertyFilters().contains(filter2)).isTrue();
        assertThat(ctx.getPropertyFilters()).hasSize(2);
        b = ConfigurationProvider.getConfigurationContextBuilder()
                .addPropertyFilters(filter1, filter2);
        b.removePropertyFilters(filter1);
        ctx = b.build();
        assertThat(ctx.getPropertyFilters()).hasSize(1);
        assertThat(ctx.getPropertyFilters().contains(filter1)).isFalse();
        assertThat(ctx.getPropertyFilters().contains(filter2)).isTrue();
    }

    @Test
    public void removePropertyFilters_Collection() throws Exception {
        PropertyFilter filter1 = (value, ctx) -> value;
        PropertyFilter filter2 = (value, ctx) -> value;
        ConfigurationContextBuilder b = ConfigurationProvider.getConfigurationContextBuilder()
                .addPropertyFilters(Arrays.asList(new PropertyFilter[]{filter1, filter2}));
        ConfigurationContext ctx = b.build();
        assertThat(ctx.getPropertyFilters().contains(filter1)).isTrue();
        assertThat(ctx.getPropertyFilters().contains(filter2)).isTrue();
        assertThat(ctx.getPropertyFilters()).hasSize(2);
        b = ConfigurationProvider.getConfigurationContextBuilder()
                .addPropertyFilters(Arrays.asList(new PropertyFilter[]{filter1, filter2}));
        b.removePropertyFilters(filter1);
        ctx = b.build();
        assertThat(ctx.getPropertyFilters()).hasSize(1);
        assertThat(ctx.getPropertyFilters().contains(filter1)).isFalse();
        assertThat(ctx.getPropertyFilters().contains(filter2)).isTrue();
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void addPropertyConverters_Array() throws Exception {
		PropertyConverter converter = (value, ctx) -> value.toLowerCase();
        ConfigurationContextBuilder b = ConfigurationProvider.getConfigurationContextBuilder()
                .addPropertyConverters(TypeLiteral.of(String.class), converter);
        ConfigurationContext ctx = b.build();
        assertThat(ctx.getPropertyConverters(TypeLiteral.of(String.class)).contains(converter)).isTrue();
        assertThat(ctx.getPropertyConverters()).hasSize(1);
        b = ConfigurationProvider.getConfigurationContextBuilder()
                .addPropertyConverters(TypeLiteral.of(String.class), converter);
        b.addPropertyConverters(TypeLiteral.of(String.class), converter);
        assertThat(ctx.getPropertyConverters()).hasSize(1);
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void addPropertyConverters_Collection() throws Exception {
		PropertyConverter converter = (value, ctx) -> value.toLowerCase();
        ConfigurationContextBuilder b = ConfigurationProvider.getConfigurationContextBuilder()
                .addPropertyConverters(TypeLiteral.of(String.class),
                        Arrays.<PropertyConverter<Object>>asList(new PropertyConverter[]{converter}));
        ConfigurationContext ctx = b.build();
        assertThat(ctx.getPropertyConverters(TypeLiteral.of(String.class)).contains(converter)).isTrue();
        assertThat(1).isEqualTo(ctx.getPropertyConverters().size());
        b = ConfigurationProvider.getConfigurationContextBuilder()
                .addPropertyConverters(TypeLiteral.of(String.class),
                        Arrays.<PropertyConverter<Object>>asList(new PropertyConverter[]{converter}));
        b.addPropertyConverters(TypeLiteral.of(String.class), converter);
        assertThat(1).isEqualTo(ctx.getPropertyConverters().size());
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void removePropertyConverters_Array() throws Exception {
        PropertyConverter converter = (value, ctx) -> value.toLowerCase();
        ConfigurationContextBuilder b = ConfigurationProvider.getConfigurationContextBuilder()
                .addPropertyConverters(TypeLiteral.of(String.class), converter);
        ConfigurationContext ctx = b.build();
        assertThat(ctx.getPropertyConverters(TypeLiteral.of(String.class)).contains(converter)).isTrue();
        assertThat(ctx.getPropertyConverters(TypeLiteral.of(String.class))).hasSize(1);
        b = ConfigurationProvider.getConfigurationContextBuilder()
                .addPropertyConverters(TypeLiteral.of(String.class), converter);
        b.removePropertyConverters(TypeLiteral.of(String.class), converter);
        ctx = b.build();
        assertThat(ctx.getPropertyConverters(TypeLiteral.of(String.class)).contains(converter)).isFalse();
        assertThat(ctx.getPropertyConverters(TypeLiteral.of(String.class)).isEmpty()).isTrue();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
    public void removePropertyConverters_Collection() throws Exception {
        PropertyConverter converter = (value, ctx) -> value.toLowerCase();
        ConfigurationContextBuilder b = ConfigurationProvider.getConfigurationContextBuilder()
                .addPropertyConverters(TypeLiteral.of(String.class), Arrays.<PropertyConverter<Object>>asList(new PropertyConverter[]{converter}));
        ConfigurationContext ctx = b.build();
        assertThat(ctx.getPropertyConverters(TypeLiteral.of(String.class)).contains(converter)).isTrue();
        assertThat(ctx.getPropertyConverters(TypeLiteral.of(String.class))).hasSize(1);
        b = ConfigurationProvider.getConfigurationContextBuilder()
                .addPropertyConverters(TypeLiteral.of(String.class), Arrays.<PropertyConverter<Object>>asList(new PropertyConverter[]{converter}));
        b.removePropertyConverters(TypeLiteral.of(String.class), Arrays.<PropertyConverter<Object>>asList(new PropertyConverter[]{converter}));
        ctx = b.build();
        assertThat(ctx.getPropertyConverters(TypeLiteral.of(String.class)).contains(converter)).isFalse();
        assertThat(ctx.getPropertyConverters(TypeLiteral.of(String.class)).isEmpty()).isTrue();
    }

    @Test
    public void setPropertyValueCombinationPolicy() throws Exception {
        PropertyValueCombinationPolicy combPol = (currentValue, key, propertySource) -> currentValue;
        ConfigurationContextBuilder b = ConfigurationProvider.getConfigurationContextBuilder()
                .setPropertyValueCombinationPolicy(combPol);
        ConfigurationContext ctx = b.build();
        assertThat(combPol).isEqualTo(ctx.getPropertyValueCombinationPolicy());
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
            assertThat(b.getPropertySources().get(i)).isEqualTo(propertySources[i]);
        }
        b.increasePriority(propertySources[propertySources.length-2]);
        for(int i=0;i<propertySources.length-2;i++){
            assertThat(b.getPropertySources().get(i)).isEqualTo(propertySources[i]);
        }
        assertThat(b.getPropertySources().get(propertySources.length-2)).isEqualTo(propertySources[propertySources.length-1]);
        assertThat(b.getPropertySources().get(propertySources.length-1)).isEqualTo(propertySources[propertySources.length-2]);
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
            assertThat(b.getPropertySources().get(i)).isEqualTo(propertySources[i]);
        }
        b.decreasePriority(propertySources[1]);
        for(int i=2;i<propertySources.length;i++){
            assertThat(b.getPropertySources().get(i)).isEqualTo(propertySources[i]);
        }
        assertThat(b.getPropertySources().get(1)).isEqualTo(propertySources[0]);
        assertThat(b.getPropertySources().get(0)).isEqualTo(propertySources[1]);
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
            assertThat(b.getPropertySources().get(i)).isEqualTo(propertySources[i]);
        }
        b.lowestPriority(propertySources[1]);
        for(int i=2;i<propertySources.length;i++){
            assertThat(b.getPropertySources().get(i)).isEqualTo(propertySources[i]);
        }
        assertThat(b.getPropertySources().get(1)).isEqualTo(propertySources[0]);
        assertThat(b.getPropertySources().get(0)).isEqualTo(propertySources[1]);
        b.lowestPriority(propertySources[5]);
        assertThat(b.getPropertySources().get(0)).isEqualTo(propertySources[5]);
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
            assertThat(b.getPropertySources().get(i)).isEqualTo(propertySources[i]);
        }
        b.highestPriority(propertySources[propertySources.length-2]);
        for(int i=0;i<propertySources.length-2;i++){
            assertThat(b.getPropertySources().get(i)).isEqualTo(propertySources[i]);
        }
        assertThat(b.getPropertySources().get(propertySources.length-1)).isEqualTo(propertySources[propertySources.length-2]);
        assertThat(b.getPropertySources().get(propertySources.length-2)).isEqualTo(propertySources[propertySources.length-1]);
        b.highestPriority(propertySources[5]);
        assertThat(b.getPropertySources().get(propertySources.length-1)).isEqualTo(propertySources[5]);
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
            assertThat(b.getPropertySources().get(i)).isEqualTo(propertySources[i]);
        }
    }

    @Test
    public void sortPropertyFilter(){
        // setup
        ConfigurationContextBuilder b = ConfigurationProvider.getConfigurationContextBuilder();
        PropertyFilter[] propertyFilters = new PropertyFilter[10];
        for(int i=0;i<propertyFilters.length;i++){
            propertyFilters[i] = (value, ctx) -> value.setValue(toString() + " - ");
        }
        b.addPropertyFilters(propertyFilters);
        Comparator<PropertyFilter> pfComp = (o1, o2) -> o1.toString().compareTo(o2.toString());
        // test
        b.sortPropertyFilter(pfComp);
        Arrays.sort(propertyFilters, pfComp);
        for(int i=0;i<propertyFilters.length;i++){
            assertThat(b.getPropertyFilters().get(i)).isEqualTo(propertyFilters[i]);
        }
    }

    @Test
    public void build() throws Exception {
        ConfigurationContextBuilder b = ConfigurationProvider.getConfigurationContextBuilder();
        ConfigurationContext ctx = b.build();
        assertThat(ctx).isNotNull();
        assertThat(ctx.getPropertySources().isEmpty()).isTrue();
        assertThat(ctx.getPropertyFilters().isEmpty()).isTrue();
    }

    @Test
    public void testRemoveAllFilters() throws Exception {
        ConfigurationContextBuilder b = ConfigurationProvider.getConfigurationContextBuilder();
        b.addPropertyFilters((value, ctx) -> value.setValue(toString() + " - "));
        assertThat(b.getPropertyFilters().isEmpty()).isFalse();
        b.removePropertyFilters(b.getPropertyFilters());
        assertThat(b.getPropertyFilters().isEmpty()).isTrue();
    }

    @Test
    public void testRemoveAllSources() throws Exception {
        ConfigurationContextBuilder b = ConfigurationProvider.getConfigurationContextBuilder();
        b.addPropertySources(new TestPropertySource());
        assertThat(b.getPropertySources().isEmpty()).isFalse();
        b.removePropertySources(b.getPropertySources());
        assertThat(b.getPropertyFilters().isEmpty()).isTrue();
    }
}