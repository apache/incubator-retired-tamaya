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
package org.apache.tamaya.core.internal;

import org.apache.tamaya.Configuration;
import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.core.testdata.TestPropertyDefaultSource;
import org.apache.tamaya.spi.ConversionContext;
import org.apache.tamaya.spi.FilterContext;
import org.apache.tamaya.spi.PropertyConverter;
import org.apache.tamaya.spi.PropertyFilter;
import org.apache.tamaya.spi.PropertyValue;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Simple tests for {@link CoreConfiguration} by atsticks on 16.08.16.
 */
public class CoreConfigurationTest {

    @Test
    public void addPropertySources() throws Exception {
        TestPropertyDefaultSource def = new TestPropertyDefaultSource();
        Configuration cfg = new CoreConfigurationBuilder().build();
        assertThat(cfg.getContext().getPropertySources()).doesNotContain(def);
        cfg = new CoreConfigurationBuilder()
                .addPropertySources(def).build();
        assertThat(cfg.getContext().getPropertySources()).contains(def);
    }

    @Test
    public void testToString() throws Exception {
        String toString = Configuration.current().getContext().toString();
        assertThat(toString).contains("Property Filters").contains("Property Converters");
    }

    @Test
    public void getPropertySources() throws Exception {
        Configuration cfg = new CoreConfigurationBuilder().build();
        assertThat(cfg.getContext().getPropertySources()).isNotNull().hasSize(0);
        cfg = new CoreConfigurationBuilder().addDefaultPropertySources().build();
        assertThat(cfg.getContext().getPropertySources()).isNotNull().hasSize(7);
    }

    @Test
    public void getPropertySource() throws Exception {
        TestPropertyDefaultSource ps = new TestPropertyDefaultSource();
        Configuration cfg = new CoreConfigurationBuilder()
                .addPropertySources(ps).build();
        assertThat(cfg.getContext().getPropertySources()).isNotNull().hasSize(1);
        assertThat(cfg.getContext().getPropertySource(ps.getName())).isNotNull();
        assertThat(cfg.getContext().getPropertySource(ps.getName()).getName()).isEqualTo(ps.getName());
        assertThat(cfg.getContext().getPropertySource("huhu")).isNull();

    }

    @Test
    public void testHashCode() throws Exception {
        TestPropertyDefaultSource ps = new TestPropertyDefaultSource();
        Configuration cfg1 = new CoreConfigurationBuilder()
                .addPropertySources(ps).build();
        Configuration cfg2 = new CoreConfigurationBuilder()
                .addPropertySources(ps).build();
        assertThat(cfg2.hashCode()).isEqualTo(cfg1.hashCode());
        cfg2 = new CoreConfigurationBuilder()
                .build();
        assertThat(cfg1.hashCode()).isNotEqualTo(cfg2.hashCode());

    }

    @Test
    public void addPropertyConverter() throws Exception {
        Configuration cfg = new CoreConfigurationBuilder().build();
        PropertyConverter testConverter = new PropertyConverter() {
            @Override
            public Object convert(String value, ConversionContext ctx) {
                return "";
            }
        };
        assertThat(cfg.getContext().getPropertyConverters(TypeLiteral.of(String.class))).doesNotContain(testConverter);
        cfg = new CoreConfigurationBuilder().addPropertyConverters(TypeLiteral.of(String.class), testConverter).build();
        assertThat(cfg.getContext().getPropertyConverters(TypeLiteral.of(String.class))).contains(testConverter);
    }

    @Test
    public void getPropertyConverters() throws Exception {
        PropertyConverter testConverter = new PropertyConverter() {
            @Override
            public Object convert(String value, ConversionContext ctx) {
                return "";
            }
        };
        Configuration cfg = new CoreConfigurationBuilder().addPropertyConverters(TypeLiteral.of(String.class), testConverter).build();
        assertThat(cfg.getContext().getPropertyConverters()).isNotNull().containsKey(TypeLiteral.of(String.class));
        assertThat(cfg.getContext().getPropertyConverters().get(TypeLiteral.of(String.class))).contains(testConverter);
        testConverter = new PropertyConverter() {
            @Override
            public Object convert(String value, ConversionContext ctx) {
                return Integer.valueOf(5);
            }
        };
        cfg = new CoreConfigurationBuilder().addPropertyConverters(TypeLiteral.of(Integer.class), testConverter).build();
        assertThat(cfg.getContext().getPropertyConverters()).containsKey(TypeLiteral.of(Integer.class));
        assertThat(cfg.getContext().getPropertyConverters().get(TypeLiteral.of(Integer.class))).contains(testConverter);
    }

    @Test
    public void getPropertyConverters1() throws Exception {
        Configuration cfg = new CoreConfigurationBuilder().build();
        PropertyConverter testConverter = new PropertyConverter() {
            @Override
            public Object convert(String value, ConversionContext ctx) {
                return "";
            }
        };
        assertThat(cfg.getContext().getPropertyConverters(TypeLiteral.of(String.class))).isNotNull().hasSize(0);

        cfg = new CoreConfigurationBuilder().addPropertyConverters(TypeLiteral.of(String.class), testConverter).build();
        assertThat(cfg.getContext().getPropertyConverters(TypeLiteral.of(String.class))).isNotNull().hasSize(1).contains(testConverter);

    }

    @Test
    public void getPropertyFilters() throws Exception {
        Configuration cfg = new CoreConfigurationBuilder().build();
        PropertyFilter testFilter = new PropertyFilter() {

            @Override
            public PropertyValue filterProperty(PropertyValue value, FilterContext ctx) {
                return value;
            }
        };
        assertThat(cfg.getContext().getPropertyFilters()).isNotNull().doesNotContain(testFilter);
        cfg = cfg.toBuilder().addPropertyFilters(testFilter).build();
        assertThat(cfg.getContext().getPropertyFilters()).contains(testFilter);
    }

    @Test
    public void toBuilder() throws Exception {
        assertThat(new CoreConfigurationBuilder().build().toBuilder()).isNotNull();
    }

    @Test
    public void testRoundTrip() throws Exception {
        Configuration cfg = new CoreConfigurationBuilder().build();
        assertThat(cfg).isEqualTo(cfg.toBuilder().build());
    }

}
