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
import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.core.internal.CoreConfigurationBuilder;
import org.apache.tamaya.spi.*;
import org.junit.Test;

import java.util.Arrays;
import java.util.Comparator;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests for {@link ConfigurationBuilder} by atsticks on 06.09.16.
 */
public class ConfigurationBuilderTest {

    private TestPropertySource testPropertySource = new TestPropertySource(){};

    @Test
    public void setContext() throws Exception {
        Configuration cfg = Configuration.current();
        ConfigurationBuilder b = Configuration.createConfigurationBuilder()
                .setConfiguration(cfg);
        assertThat(b.build()).isEqualTo(cfg);
    }

    @Test
    public void addPropertySources_Array() throws Exception {
        PropertySource testPS2 = new TestPropertySource("addPropertySources_Array", 1);
        ConfigurationBuilder b = new CoreConfigurationBuilder()
                .addPropertySources(testPropertySource, testPS2);
        Configuration cfg = b.build();
        assertThat(cfg.getContext().getPropertySources()).hasSize(2).contains(testPropertySource, testPS2);
        // Ensure no sorting happens during add, so switch ordinals!
        testPS2 = new TestPropertySource("addPropertySources_Array", 1);
        b = Configuration.createConfigurationBuilder()
                .addPropertySources(testPS2, testPropertySource);
        cfg = b.build();
        assertThat(cfg.getContext().getPropertySources()).hasSize(2).contains(testPropertySource, testPS2);
        assertThat("TestPropertySource").isEqualTo(cfg.getContext().getPropertySources().get(1).getName());
        assertThat("addPropertySources_Array").isEqualTo(cfg.getContext().getPropertySources().get(0).getName());
    }

    @Test
    public void addPropertySources_Collection() throws Exception {
        PropertySource testPS2 = new TestPropertySource("addPropertySources_Collection", 1);
        ConfigurationBuilder b = new CoreConfigurationBuilder()
                .addPropertySources(Arrays.asList(new PropertySource[]{testPropertySource, testPS2}));
        Configuration cfg = b.build();
        assertThat(cfg.getContext().getPropertySources()).hasSize(2).contains(testPropertySource, testPS2);
        assertThat("TestPropertySource").isEqualTo(cfg.getContext().getPropertySources().get(0).getName());
        assertThat("addPropertySources_Collection").isEqualTo(cfg.getContext().getPropertySources().get(1).getName());
        // Ensure no sorting happens during add, so switch ordinals!
        testPS2 = new TestPropertySource("addPropertySources_Collection", 1);
        b = Configuration.createConfigurationBuilder()
                .addPropertySources(Arrays.asList(new PropertySource[]{testPS2, testPropertySource}));
        cfg = b.build();
        assertThat(cfg.getContext().getPropertySources()).hasSize(2).contains(testPropertySource, testPS2);
        assertThat("TestPropertySource").isEqualTo(cfg.getContext().getPropertySources().get(1).getName());
        assertThat("addPropertySources_Collection").isEqualTo(cfg.getContext().getPropertySources().get(0).getName());
    }

    @Test
    public void removePropertySources_Array() throws Exception {
        PropertySource testPS2 = new TestPropertySource("removePropertySources_Array", 1);
        ConfigurationBuilder b = Configuration.createConfigurationBuilder()
                .addPropertySources(testPropertySource, testPS2);
        Configuration cfg = b.build();
        assertThat(cfg.getContext().getPropertySources()).hasSize(2).contains(testPropertySource, testPS2);
        b = Configuration.createConfigurationBuilder()
                .addPropertySources(testPropertySource, testPS2);
        b.removePropertySources(testPropertySource);
        cfg = b.build();
        assertThat(cfg.getContext().getPropertySources()).hasSize(1).contains(testPS2).doesNotContain(testPropertySource);
    }

    @Test
    public void removePropertySources_Collection() throws Exception {
        PropertySource testPS2 = new TestPropertySource("removePropertySources_Array", 1);
        ConfigurationBuilder b = Configuration.createConfigurationBuilder()
                .addPropertySources(testPropertySource, testPS2);
        Configuration cfg = b.build();
        assertThat(cfg.getContext().getPropertySources()).hasSize(2).contains(testPropertySource, testPS2);
        b = Configuration.createConfigurationBuilder()
                .addPropertySources(testPropertySource, testPS2);
        b.removePropertySources(testPropertySource);
        cfg = b.build();
        assertThat(cfg.getContext().getPropertySources()).hasSize(1).doesNotContain(testPropertySource).contains(testPS2);
    }

    @Test
    public void addPropertyFilters_Array() throws Exception {
        PropertyFilter filter1 = (value, ctx) -> value;
        PropertyFilter filter2 = (value, ctx) -> value;
        ConfigurationBuilder b = Configuration.createConfigurationBuilder();
        b.addPropertyFilters(filter1, filter2);
        Configuration cfg = b.build();
        ConfigurationContext ctx = cfg.getContext();
        assertThat(ctx.getPropertyFilters()).hasSize(2).contains(filter1, filter2);
        b = Configuration.createConfigurationBuilder();
        b.addPropertyFilters(filter1, filter2);
        b.addPropertyFilters(filter1, filter2);
        assertThat(ctx.getPropertyFilters()).hasSize(2);
    }

    @Test
    public void addPropertyFilters_Collection() throws Exception {
        PropertyFilter filter1 = (value, ctx) -> value;
        PropertyFilter filter2 = (value, ctx) -> value;
        ConfigurationBuilder b = Configuration.createConfigurationBuilder();
        b.addPropertyFilters(Arrays.asList(new PropertyFilter[]{filter1, filter2}));
        Configuration cfg = b.build();
        ConfigurationContext ctx = cfg.getContext();
        assertThat(ctx.getPropertyFilters()).hasSize(2).contains(filter1, filter2);
        b = Configuration.createConfigurationBuilder();
        b.addPropertyFilters(filter1, filter2);
        b.addPropertyFilters(filter1, filter2);
        assertThat(ctx.getPropertyFilters()).hasSize(2);
    }

    @Test
    public void removePropertyFilters_Array() throws Exception {
        PropertyFilter filter1 = (value, ctx) -> value;
        PropertyFilter filter2 = (value, ctx) -> value;
        ConfigurationBuilder b = Configuration.createConfigurationBuilder()
                .addPropertyFilters(filter1, filter2);
        Configuration cfg = b.build();
        ConfigurationContext ctx = cfg.getContext();
        assertThat(ctx.getPropertyFilters()).hasSize(2).contains(filter1, filter2);
        b = Configuration.createConfigurationBuilder()
                .addPropertyFilters(filter1, filter2);
        b.removePropertyFilters(filter1);
        cfg = b.build();
        ctx = cfg.getContext();
        assertThat(ctx.getPropertyFilters()).hasSize(1).doesNotContain(filter1).contains(filter2);
    }

    @Test
    public void removePropertyFilters_Collection() throws Exception {
        PropertyFilter filter1 = (value, ctx) -> value;
        PropertyFilter filter2 = (value, ctx) -> value;
        ConfigurationBuilder b = Configuration.createConfigurationBuilder()
                .addPropertyFilters(Arrays.asList(new PropertyFilter[]{filter1, filter2}));
        Configuration cfg = b.build();
        ConfigurationContext ctx = cfg.getContext();
        assertThat(ctx.getPropertyFilters()).hasSize(2).contains(filter1, filter2);
        b = Configuration.createConfigurationBuilder()
                .addPropertyFilters(Arrays.asList(new PropertyFilter[]{filter1, filter2}));
        b.removePropertyFilters(filter1);
        cfg = b.build();
        ctx = cfg.getContext();
        assertThat(ctx.getPropertyFilters()).hasSize(1).doesNotContain(filter1).contains(filter2);
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void addPropertyConverters_Array() throws Exception {
		PropertyConverter converter = (value, ctx) -> value.toLowerCase();
        ConfigurationBuilder b = Configuration.createConfigurationBuilder()
                .addPropertyConverters(TypeLiteral.of(String.class), converter);
        Configuration cfg = b.build();
        ConfigurationContext ctx = cfg.getContext();
        assertThat(ctx.getPropertyConverters(TypeLiteral.of(String.class))).contains(converter);
        assertThat(ctx.getPropertyConverters()).hasSize(1);
        b = Configuration.createConfigurationBuilder()
                .addPropertyConverters(TypeLiteral.of(String.class), converter);
        b.addPropertyConverters(TypeLiteral.of(String.class), converter);
        assertThat(ctx.getPropertyConverters()).hasSize(1);
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void addPropertyConverters_Collection() throws Exception {
		PropertyConverter converter = (value, ctx) -> value.toLowerCase();
        ConfigurationBuilder b = Configuration.createConfigurationBuilder()
                .addPropertyConverters(TypeLiteral.of(String.class),
                        Arrays.<PropertyConverter<Object>>asList(new PropertyConverter[]{converter}));
        Configuration cfg = b.build();
        ConfigurationContext ctx = cfg.getContext();
        assertThat(ctx.getPropertyConverters(TypeLiteral.of(String.class))).contains(converter);
        assertThat(ctx.getPropertyConverters()).hasSize(1);
        b = Configuration.createConfigurationBuilder()
                .addPropertyConverters(TypeLiteral.of(String.class),
                        Arrays.<PropertyConverter<Object>>asList(new PropertyConverter[]{converter}));
        b.addPropertyConverters(TypeLiteral.of(String.class), converter);
        assertThat(ctx.getPropertyConverters()).hasSize(1);
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void removePropertyConverters_Array() throws Exception {
        PropertyConverter converter = (value, ctx) -> value.toLowerCase();
        ConfigurationBuilder b = Configuration.createConfigurationBuilder()
                .addPropertyConverters(TypeLiteral.of(String.class), converter);
        Configuration cfg = b.build();
        ConfigurationContext ctx = cfg.getContext();
        assertThat(ctx.getPropertyConverters(TypeLiteral.of(String.class))).hasSize(1).contains(converter);
        b = Configuration.createConfigurationBuilder()
                .addPropertyConverters(TypeLiteral.of(String.class), converter);
        b.removePropertyConverters(TypeLiteral.of(String.class), converter);
        cfg = b.build();
        ctx = cfg.getContext();
        assertThat(ctx.getPropertyConverters(TypeLiteral.of(String.class))).isEmpty();
    }


    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
    public void removePropertyConverters_Collection() throws Exception {
        PropertyConverter converter = (value, ctx) -> value.toLowerCase();
        ConfigurationBuilder b = Configuration.createConfigurationBuilder()
                .addPropertyConverters(TypeLiteral.of(String.class), Arrays.<PropertyConverter<Object>>asList(new PropertyConverter[]{converter}));
        Configuration cfg = b.build();
        ConfigurationContext ctx = cfg.getContext();
        assertThat(ctx.getPropertyConverters(TypeLiteral.of(String.class))).hasSize(1).contains(converter);
        b = Configuration.createConfigurationBuilder()
                .addPropertyConverters(TypeLiteral.of(String.class), Arrays.<PropertyConverter<Object>>asList(new PropertyConverter[]{converter}));
        b.removePropertyConverters(TypeLiteral.of(String.class), Arrays.<PropertyConverter<Object>>asList(new PropertyConverter[]{converter}));
        cfg = b.build();
        ctx = cfg.getContext();
        assertThat(ctx.getPropertyConverters(TypeLiteral.of(String.class))).isEmpty();
    }

    @Test
    public void increasePriority(){
        ConfigurationBuilder b = Configuration.createConfigurationBuilder();
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
        ConfigurationBuilder b = Configuration.createConfigurationBuilder();
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
        ConfigurationBuilder b = Configuration.createConfigurationBuilder();
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
        ConfigurationBuilder b = Configuration.createConfigurationBuilder();
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
        ConfigurationBuilder b = Configuration.createConfigurationBuilder();
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
        ConfigurationBuilder b = Configuration.createConfigurationBuilder();
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
        ConfigurationBuilder b = Configuration.createConfigurationBuilder();
        Configuration cfg = b.build();
        ConfigurationContext ctx = cfg.getContext();
        assertThat(ctx).isNotNull();
        assertThat(ctx.getPropertySources()).isEmpty();
        assertThat(ctx.getPropertyFilters()).isEmpty();
    }

    @Test
    public void testRemoveAllFilters() throws Exception {
        ConfigurationBuilder b = Configuration.createConfigurationBuilder();
        b.addPropertyFilters((value, ctx) -> value.setValue(toString() + " - "));
        assertThat(b.getPropertyFilters()).isNotEmpty();
        b.removePropertyFilters(b.getPropertyFilters());
        assertThat(b.getPropertyFilters()).isEmpty();
    }

    @Test
    public void testRemoveAllSources() throws Exception {
        ConfigurationBuilder b = Configuration.createConfigurationBuilder();
        b.addPropertySources(new TestPropertySource());
        assertThat(b.getPropertySources()).isNotEmpty();
        b.removePropertySources(b.getPropertySources());
        assertThat(b.getPropertyFilters()).isEmpty();
    }
}
