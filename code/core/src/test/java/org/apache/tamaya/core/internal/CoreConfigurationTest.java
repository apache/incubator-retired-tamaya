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
import org.apache.tamaya.spi.*;
import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Simple tests for {@link CoreConfiguration} by atsticks on 16.08.16.
 */
public class CoreConfigurationTest {

    @Test
    public void addPropertySources() throws Exception {
        Configuration cfg = new CoreConfigurationBuilder().build();
        TestPropertyDefaultSource def = new TestPropertyDefaultSource();
        assertThat(cfg.getContext().getPropertySources().contains(def)).isFalse();
        cfg.getContext().addPropertySources(def);
        assertThat(cfg.getContext().getPropertySources().contains(def)).isTrue();
    }

    @Test
    public void testToString() throws Exception {
        String toString = Configuration.current().getContext().toString();
    }

    @Test
    public void getPropertySources() throws Exception {
        Configuration cfg = new CoreConfigurationBuilder().build();
        assertThat(cfg.getContext().getPropertySources()).isNotNull();
        assertThat(0).isEqualTo(cfg.getContext().getPropertySources().size());
        cfg = new CoreConfigurationBuilder().addDefaultPropertySources().build();
        assertThat(cfg.getContext().getPropertySources()).isNotNull();
        assertThat(cfg.getContext().getPropertySources()).hasSize(7);
    }

    @Test
    public void getPropertySource() throws Exception {
        TestPropertyDefaultSource ps = new TestPropertyDefaultSource();
        Configuration cfg = new CoreConfigurationBuilder()
                .addPropertySources(ps).build();
        assertThat(cfg.getContext().getPropertySources()).isNotNull();
        assertThat(1).isEqualTo(cfg.getContext().getPropertySources().size());
        assertThat((cfg.getContext()).getPropertySource(ps.getName())).isNotNull();
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
            public Object convert(String value) {
                return "";
            }
        };
        assertThat(cfg.getContext().getPropertyConverters(TypeLiteral.of(String.class)).contains(testConverter)).isFalse();
        cfg.getContext().addPropertyConverter(TypeLiteral.of(String.class), testConverter);
        assertThat(cfg.getContext().getPropertyConverters(TypeLiteral.of(String.class)).contains(testConverter)).isTrue();
    }

    @Test
    public void getPropertyConverters() throws Exception {
        Configuration cfg = new CoreConfigurationBuilder().build();
        PropertyConverter testConverter = new PropertyConverter() {
            @Override
            public Object convert(String value) {
                return "";
            }
        };
        cfg.getContext().addPropertyConverter(TypeLiteral.of(String.class), testConverter);
        assertThat(cfg.getContext().getPropertyConverters()).isNotNull();
        assertThat(cfg.getContext().getPropertyConverters().containsKey(TypeLiteral.of(String.class))).isTrue();
        assertThat(cfg.getContext().getPropertyConverters().get(TypeLiteral.of(String.class)).contains(testConverter)).isTrue();
        testConverter = new PropertyConverter() {
            @Override
            public Object convert(String value) {
                return Integer.valueOf(5);
            }
        };
        cfg.getContext().addPropertyConverter(TypeLiteral.of(Integer.class), testConverter);
        assertThat(cfg.getContext().getPropertyConverters().containsKey(TypeLiteral.of(Integer.class))).isTrue();
        assertThat(cfg.getContext().getPropertyConverters().get(TypeLiteral.of(Integer.class)).contains(testConverter)).isTrue();
    }

    @Test
    public void getPropertyConverters1() throws Exception {
        Configuration cfg = new CoreConfigurationBuilder().build();
        PropertyConverter testConverter = new PropertyConverter() {
            @Override
            public Object convert(String value) {
                return "";
            }
        };
        assertThat(cfg.getContext().getPropertyConverters(TypeLiteral.of(String.class))).isNotNull();
        assertThat(0).isEqualTo(cfg.getContext().getPropertyConverters(TypeLiteral.of(String.class)).size());
        cfg.getContext().addPropertyConverter(TypeLiteral.of(String.class), testConverter);
        assertThat(cfg.getContext().getPropertyConverters(TypeLiteral.of(String.class))).isNotNull();
        assertThat(1).isEqualTo(cfg.getContext().getPropertyConverters(TypeLiteral.of(String.class)).size());
        assertThat(cfg.getContext().getPropertyConverters(TypeLiteral.of(String.class)).contains(testConverter)).isTrue();

    }

    @Test
    public void getPropertyFilters() throws Exception {
        Configuration cfg = new CoreConfigurationBuilder().build();
        PropertyFilter testFilter = new PropertyFilter() {

            @Override
            public PropertyValue filterProperty(PropertyValue value) {
                return value;
            }
        };
        assertThat(cfg.getContext().getPropertyFilters()).isNotNull();
        assertThat(cfg.getContext().getPropertyFilters().contains(testFilter)).isFalse();
        cfg = cfg.toBuilder().addPropertyFilters(testFilter).build();
        assertThat(cfg.getContext().getPropertyFilters().contains(testFilter)).isTrue();
    }

    @Test
    public void getPropertyValueCombinationPolicy() throws Exception {
        Configuration cfg = new CoreConfigurationBuilder().build();
        assertThat(cfg.getContext().getPropertyValueCombinationPolicy()).isNotNull();
        assertThat(cfg.getContext().getPropertyValueCombinationPolicy())
                .isEqualTo(PropertyValueCombinationPolicy.DEFAULT_OVERRIDING_POLICY);
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
