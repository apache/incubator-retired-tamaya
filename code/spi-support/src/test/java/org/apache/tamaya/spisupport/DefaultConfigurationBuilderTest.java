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

import java.util.*;

import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;
import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.spi.ConfigurationBuilder;
import org.apache.tamaya.spi.ConfigurationContext;
import org.apache.tamaya.spi.PropertyConverter;
import org.apache.tamaya.spi.PropertyFilter;
import org.apache.tamaya.spi.PropertySource;
import org.apache.tamaya.spi.PropertyValue;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

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
        assertThat(b.build().getContext()).isEqualTo(context);
    }

    @Test
    public void setConfiguration() throws Exception {
        Configuration cfg = Configuration.current();
        ConfigurationBuilder b = new DefaultConfigurationBuilder()
                .setConfiguration(cfg);
        assertThat(b.build()).isEqualTo(cfg);
    }

    @Test
    public void addRemovePropertySources_Array() throws Exception {
        PropertySource testPS2 = new TestPropertySource("addRemovePropertySources_Array");
        ConfigurationBuilder b = new DefaultConfigurationBuilder()
                .addPropertySources(testPropertySource, testPS2);
        Configuration cfg = b.build();
        ConfigurationContext ctx = cfg.getContext();
        assertThat(ctx.getPropertySources()).hasSize(2).contains(testPropertySource, testPS2);

        b = new DefaultConfigurationBuilder()
                .addPropertySources(testPropertySource, testPS2);
        cfg = b.removePropertySources(testPropertySource).build();
        ctx = cfg.getContext();
        assertThat(ctx.getPropertySources()).hasSize(1).contains(testPS2).doesNotContain(testPropertySource);
    }

    @Test
    public void addRemovePropertySources_Collection() throws Exception {
        PropertySource testPS2 = new TestPropertySource("addRemovePropertySources_Collection");
        ConfigurationBuilder b = new DefaultConfigurationBuilder()
                .addPropertySources(Arrays.asList(testPropertySource, testPS2));
        Configuration cfg = b.build();
        ConfigurationContext ctx = cfg.getContext();
        assertThat(ctx.getPropertySources()).hasSize(2).contains(testPropertySource, testPS2);

        b = new DefaultConfigurationBuilder()
                .addPropertySources(testPropertySource, testPS2);
        cfg = b.removePropertySources(Arrays.asList(testPropertySource)).build();
        ctx = cfg.getContext();
        assertThat(ctx.getPropertySources()).hasSize(1).contains(testPS2).doesNotContain(testPropertySource);
    }

    @Test
    public void addRemovePropertyFilters_Array() throws Exception {
        PropertyFilter filter1 = (value, ctx) -> value;
        PropertyFilter filter2 = (value, ctx) -> value;
        DefaultConfigurationBuilder b = new DefaultConfigurationBuilder();
        Configuration cfg = b.addPropertyFilters(filter1, filter2).build();
        ConfigurationContext ctx = cfg.getContext();
        assertThat(ctx.getPropertyFilters()).hasSize(2).contains(filter1, filter2);

        b = new DefaultConfigurationBuilder();
        b.addPropertyFilters(filter1, filter2);
        cfg = b.addPropertyFilters(filter1, filter2).build();
        ctx = cfg.getContext();
        assertThat(ctx.getPropertyFilters()).hasSize(2);

        b = new DefaultConfigurationBuilder();
        b.addPropertyFilters(filter1, filter2);
        cfg = b.removePropertyFilters(filter1).build();
        ctx = cfg.getContext();
        assertThat(ctx.getPropertyFilters()).hasSize(1).contains(filter2).doesNotContain(filter1);

    }

    @Test
    public void addRemovePropertyFilters_Collection() throws Exception {
        PropertyFilter filter1 = (value, ctx) -> value;
        PropertyFilter filter2 = (value, ctx) -> value;
        DefaultConfigurationBuilder b = new DefaultConfigurationBuilder();
        Configuration cfg = b.addPropertyFilters(Arrays.asList(filter1, filter2)).build();
        ConfigurationContext ctx = cfg.getContext();
        assertThat(ctx.getPropertyFilters()).hasSize(2).contains(filter1, filter2);

        b = new DefaultConfigurationBuilder();
        b.addPropertyFilters(Arrays.asList(filter1, filter2, filter1));
        cfg = b.addPropertyFilters(Arrays.asList(filter1, filter2)).build();
        ctx = cfg.getContext();
        assertThat(ctx.getPropertyFilters()).hasSize(2);

        b = new DefaultConfigurationBuilder();
        b.addPropertyFilters(Arrays.asList(filter1, filter2));
        cfg = b.removePropertyFilters(Arrays.asList(filter1)).build();
        ctx = cfg.getContext();
        assertThat(ctx.getPropertyFilters()).hasSize(1).contains(filter2).doesNotContain(filter1);

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
        DefaultConfigurationBuilder b = new DefaultConfigurationBuilder();
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
        DefaultConfigurationBuilder b = new DefaultConfigurationBuilder();
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
        DefaultConfigurationBuilder b = new DefaultConfigurationBuilder();
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
        DefaultConfigurationBuilder b = new DefaultConfigurationBuilder();
        MockedPropertySource[] propertySources = new MockedPropertySource[10];
        for (int i = 0; i < propertySources.length; i++) {
            propertySources[i] = new MockedPropertySource("ps" + i, i);
        }
        b.addPropertySources(propertySources);
        Comparator<PropertySource> psComp = (o1, o2) -> o1.toString().compareTo(o2.toString());
        // test 
        assertThat(b.sortPropertySources(psComp).getClass()).isEqualTo(DefaultConfigurationBuilder.class);
        Arrays.sort(propertySources, psComp);
        for (int i = 0; i < propertySources.length; i++) {
            assertThat(b.getPropertySources().get(i)).isEqualTo(propertySources[i]);
        }
    }

    @Test
    public void sortPropertyFilter() {
        // setup
        DefaultConfigurationBuilder b = new DefaultConfigurationBuilder();
        PropertyFilter[] propertyFilters = new PropertyFilter[10];
        for (int i = 0; i < propertyFilters.length; i++) {
            propertyFilters[i] = (value, ctx) -> value.setValue(toString() + " - ");
        }

        b.addPropertyFilters(propertyFilters);
        Comparator<PropertyFilter> pfComp = (o1, o2) -> o1.toString().compareTo(o2.toString());
        //test
        assertThat(b.sortPropertyFilter(pfComp).getClass()).isEqualTo(DefaultConfigurationBuilder.class);
        Arrays.sort(propertyFilters, pfComp);
        for (int i = 0; i < propertyFilters.length; i++) {
            assertThat(b.getPropertyFilters().get(i)).isEqualTo(propertyFilters[i]);
        }
    }

    @Test
    public void addRemovePropertyConverter_Array() throws Exception {
        PropertyConverter converter1 = (value, ctx) -> value.toLowerCase();
        PropertyConverter converter2 = (value, ctx) -> value.toUpperCase();
        ConfigurationBuilder b = new DefaultConfigurationBuilder()
                .addPropertyConverters(TypeLiteral.of(String.class), converter1, converter2);
        Configuration cfg = b.build();
        ConfigurationContext ctx = cfg.getContext();
        Map<TypeLiteral<?>, List<PropertyConverter<?>>> buildConverters = b.getPropertyConverter();
        assertThat(ctx.getPropertyConverters(TypeLiteral.of(String.class))).hasSize(2).contains(converter1, converter2);
        assertThat(ctx.getPropertyConverters()).hasSize(1);
        assertThat(buildConverters.get(TypeLiteral.of(String.class)).containsAll(
                        ctx.getPropertyConverters().get(TypeLiteral.of(String.class)))).isTrue();

        b = new DefaultConfigurationBuilder()
                .addPropertyConverters(TypeLiteral.of(String.class), converter1);
        cfg = b.addPropertyConverters(TypeLiteral.of(String.class), converter1).build();
        ctx = cfg.getContext();
        assertThat(ctx.getPropertyConverters(TypeLiteral.of(String.class))).hasSize(1);

        b = new DefaultConfigurationBuilder().addPropertyConverters(TypeLiteral.of(String.class), converter1, converter2);
        cfg = b.removePropertyConverters(TypeLiteral.of(String.class), converter1).build();
        ctx = cfg.getContext();
        assertThat(ctx.getPropertyConverters(TypeLiteral.of(String.class))).contains(converter2).doesNotContain(converter1);

        b = new DefaultConfigurationBuilder().addPropertyConverters(TypeLiteral.of(String.class), converter1, converter2);
        cfg = b.removePropertyConverters(TypeLiteral.of(String.class)).build();
        ctx = cfg.getContext();
        assertThat(ctx.getPropertyConverters(TypeLiteral.of(String.class))).isEmpty();
    }

    @Test
    public void addRemovePropertyConverter_Collection() throws Exception {
        PropertyConverter converter1 = (value, ctx) -> value.toLowerCase();
        PropertyConverter converter2 = (value, ctx) -> value.toUpperCase();
        ConfigurationBuilder b = new DefaultConfigurationBuilder()
                .addPropertyConverters(TypeLiteral.of(String.class), Arrays.asList(converter1, converter2));
        Configuration cfg = b.build();
        ConfigurationContext ctx = cfg.getContext();
        Map<TypeLiteral<?>, List<PropertyConverter<?>>> buildConverters = b.getPropertyConverter();
        assertThat(ctx.getPropertyConverters(TypeLiteral.of(String.class))).hasSize(2).contains(converter1, converter2);
        assertThat(ctx.getPropertyConverters()).hasSize(1);
        assertThat(buildConverters.get(TypeLiteral.of(String.class)).containsAll(
                        ctx.getPropertyConverters().get(TypeLiteral.of(String.class)))).isTrue();

        b = new DefaultConfigurationBuilder()
                .addPropertyConverters(TypeLiteral.of(String.class), Arrays.asList(converter1));
        cfg = b.addPropertyConverters(TypeLiteral.of(String.class), Arrays.asList(converter1)).build();
        ctx = cfg.getContext();
        assertThat(ctx.getPropertyConverters(TypeLiteral.of(String.class))).hasSize(1);

        b = new DefaultConfigurationBuilder().addPropertyConverters(TypeLiteral.of(String.class), Arrays.asList(converter1, converter2));
        cfg = b.removePropertyConverters(TypeLiteral.of(String.class), Arrays.asList(converter1)).build();
        ctx = cfg.getContext();
        assertThat(ctx.getPropertyConverters(TypeLiteral.of(String.class))).doesNotContain(converter1).contains(converter2);

        b = new DefaultConfigurationBuilder().addPropertyConverters(TypeLiteral.of(String.class), Arrays.asList(converter1, converter2));
        cfg = b.removePropertyConverters(TypeLiteral.of(String.class)).build();
        ctx = cfg.getContext();
        assertThat(ctx.getPropertyConverters(TypeLiteral.of(String.class))).isEmpty();
    }

    @Test
    public void build() throws Exception {
        assertThat(new DefaultConfigurationBuilder().build()).isNotNull();
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
            return PropertyValue.createValue(key, key + "Value").setMeta("source", getName());
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
