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
package org.apache.tamaya;

import org.apache.tamaya.spi.ConfigurationBuilder;
import org.apache.tamaya.spi.ConfigurationContext;
import org.apache.tamaya.spi.ServiceContextManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

/**
 * Test class that tests the default methods implemented on
 * {@link org.apache.tamaya.Configuration}. The provided
 * {@link org.apache.tamaya.TestConfiguration} is implemented with maximal use
 * of the default methods.
 */
public class ConfigurationTest {

    @Before
    @After
    public void setup(){
        ServiceContextManager.getServiceContext().reset();
    }

    @Test
    public void test_setCurrent() throws Exception {
        Configuration saved = Configuration.current();
        Configuration.setCurrent(Configuration.EMPTY, ServiceContextManager.getDefaultClassLoader());
        assertThat(Configuration.EMPTY).isEqualTo(Configuration.current());
        Configuration.setCurrent(saved, ServiceContextManager.getDefaultClassLoader());
        assertThat(saved).isEqualTo(Configuration.current());
    }

    @Test
    public void test_toString() throws Exception {
        assertThat(Configuration.EMPTY.toString()).isEqualTo("Configuration<EMPTY>");
    }

    @Test
    public void test_getProperties() throws Exception {
        assertThat(Configuration.EMPTY.getProperties()).isEmpty();
    }

    @Test
    public void test_get_key() throws Exception {
        assertThat(Configuration.EMPTY.get("foo")).isNull();
    }

    @Test
    public void test_get_key_type() throws Exception {
        assertThat(Configuration.EMPTY.get("foo", Boolean.class)).isNull();
    }

    @Test
    public void test_get_key_typeliteral() throws Exception {
        assertThat((Predicate<Boolean>)Configuration.EMPTY.get("foo", TypeLiteral.of(Boolean.class))).isNull();
    }

    @Test
    public void test_getOptional_key() throws Exception {
        assertThat(Configuration.EMPTY.getOptional("foo")).isNotNull().isNotPresent();
    }

    @Test
    public void test_get_key_class() throws Exception {
        assertThat(Configuration.EMPTY.get("foo", Boolean.class)).isNull();
    }

    @Test
    public void test_get_keys_class() throws Exception {
        assertThat(Configuration.EMPTY.get(Arrays.asList("foo", "bar"), Boolean.class)).isNull();
    }

    @Test
    public void test_get_keys_typeliteral() throws Exception {
        assertThat((Predicate<Boolean>)Configuration.EMPTY.get(Arrays.asList("foo", "bar"), TypeLiteral.of(Boolean.class)))
                .isNull();
    }

    @Test
    public void test_get_keys_typeliteral_default() throws Exception {
        assertThat(Configuration.EMPTY.getOrDefault(Arrays.asList("foo", "bar"), TypeLiteral.of(Boolean.class), Boolean.TRUE))
                .isEqualTo(Boolean.TRUE);
    }

    @Test
    public void test_getOptional_key_class() throws Exception {
        assertThat(Configuration.EMPTY.getOptional("foo", Boolean.class)).isNotNull().isNotPresent();
    }

    @Test
    public void test_getOptional_key_typeliteral() throws Exception {
        assertThat(Configuration.EMPTY.getOptional("foo", TypeLiteral.of(Boolean.class))).isNotNull().isNotPresent();
    }

    @Test
    public void test_getOptional_keys() throws Exception {
        assertThat(Configuration.EMPTY.getOptional(Arrays.asList("foo", "bar"))).isNotNull().isNotPresent();
    }

    @Test
    public void test_getOptional_keys_class() throws Exception {
        assertThat(Configuration.EMPTY.getOptional(Arrays.asList("foo", "bar"), Boolean.class)).isNotNull().isNotPresent();
    }

    @Test
    public void test_getOptional_keys_typeliteral() throws Exception {
        assertThat(Configuration.EMPTY.getOptional(Arrays.asList("foo", "bar"), TypeLiteral.of(Boolean.class))).isNotNull().isNotPresent();
    }

    @Test
    public void test_getOrDefault_key_default() throws Exception {
        assertThat(Configuration.EMPTY.getOrDefault("foo", "bar")).isEqualTo("bar");
    }

    @Test
    public void test_getOrDefault_key_type_default() throws Exception {
        assertThat(Configuration.EMPTY.getOrDefault("foo", Boolean.class, Boolean.TRUE)).isEqualTo(Boolean.TRUE);
    }

    @Test
    public void test_current() throws Exception {
        assertThat(Configuration.current()).isNotNull();
    }

    @Test
    public void test_current_classloader() throws Exception {
        assertThat(Configuration.current(ClassLoader.getSystemClassLoader())).isNotNull();
    }

    @Test
    public void test_release() throws Exception {
        Configuration c1 = Configuration.current();
        Configuration c2 = Configuration.current();
        Configuration.releaseConfiguration(c2.getContext().getServiceContext().getClassLoader());
        Configuration c3 = Configuration.current();
        assertThat(c1).isSameAs(c2);
        assertThat(c2).isNotSameAs(c3);
    }

    @Test
    public void testget() throws Exception {
        assertThat(Boolean.TRUE).isEqualTo(Configuration.current().get("booleanTrue", Boolean.class));
        assertThat(Boolean.FALSE).isEqualTo(Configuration.current().get("booleanFalse", Boolean.class));
        assertThat((int) Byte.MAX_VALUE).isEqualTo((int) Configuration.current().get("byte", Byte.class));
        assertThat(Integer.MAX_VALUE).isEqualTo((int) Configuration.current().get("int", Integer.class));
        assertThat(Long.MAX_VALUE).isEqualTo((long) Configuration.current().get("long", Long.class));
        assertThat(Float.MAX_VALUE).isCloseTo((float) Configuration.current().get("float", Float.class), within(0.001f));
        assertThat(Double.MAX_VALUE).isEqualTo(Configuration.current().get("double", Double.class));
        assertThat("aStringValue").isEqualTo(Configuration.current().get("String"));
    }

    @Test
    public void testget_Iterable() throws Exception {
        assertThat(Configuration.current().get(Arrays.asList("String","foo"))).isEqualTo("aStringValue");
        assertThat(Configuration.current().get(Arrays.asList("foo", "bar", "String"))).isEqualTo("aStringValue");
    }

    @Test
    public void testget_Iterable_default() throws Exception {
        assertThat("foo").isEqualTo(Configuration.current().getOrDefault(Arrays.asList("adasd","safsada"), "foo"));
        assertThat("aStringValue").isEqualTo(Configuration.current().getOrDefault(Arrays.asList("foo1", "bar1", "String"), "foo"));
    }

    @Test
    public void testget_Iterable_default_typed() throws Exception {
        assertThat(25).isEqualTo(Configuration.current().getOrDefault(Arrays.asList("adasd","safsada"),Integer.class,25));
        assertThat(BigDecimal.ZERO).isEqualTo(Configuration.current().getOrDefault(Arrays.asList("foo1", "bar1", "2.0"), BigDecimal.class, BigDecimal.ZERO));
    }

    @Test
    public void testget_Iterable_with_Type() throws Exception {
        assertThat(Boolean.TRUE).isEqualTo(Configuration.current().get(Arrays.asList("booleanTrue","booleanFalse"), Boolean.class));
        assertThat(Boolean.TRUE).isEqualTo(Configuration.current().get(Arrays.asList("foo1", "bar1", "booleanTrue"), Boolean.class));
    }

    @Test
    public void testGetBoolean() throws Exception {
        assertThat(Configuration.current().get("booleanTrue", Boolean.class)).isTrue();
        assertThat(Configuration.current().get("booleanFalse", Boolean.class)).isFalse();
    }

    @Test
    public void testGetInteger() throws Exception {
        assertThat(Integer.MAX_VALUE).isEqualTo((int) Configuration.current().get("int", Integer.class));
    }

    @Test
    public void testGetLong() throws Exception {
        assertThat(Long.MAX_VALUE).isEqualTo((long) Configuration.current().get("long", Long.class));
    }

    @Test
    public void testGetDouble() throws Exception {
        assertThat(Double.MAX_VALUE).isEqualTo(Configuration.current().get("double", Double.class));
    }

    @Test
    public void testGetOrDefault() throws Exception {
        assertThat("StringIfThereWasNotAValueThere").isEqualTo(Configuration.current().getOrDefault("nonexistant", "StringIfThereWasNotAValueThere"));
        assertThat("StringIfThereWasNotAValueThere").isEqualTo(Configuration.current().getOrDefault("nonexistant", String.class, "StringIfThereWasNotAValueThere"));
    }

    @Test
    public void testGetSnapshot() throws Exception {
        assertThat(Configuration.EMPTY.getSnapshot()).isNotNull();
    }

    @Test
    public void testGetSnapshot_keys() throws Exception {
        assertThat(Configuration.EMPTY.getSnapshot("foo", "bar")).isNotNull();
    }

    @Test
    public void testGetSnapshot_iterable() throws Exception {
        assertThat(Configuration.EMPTY.getSnapshot(Arrays.asList("foo", "bar"))).isNotNull();
    }

    @Test
    public void testToBuilder() throws Exception {
        assertThat(Configuration.current().toBuilder()).isNotNull();
    }

    @Test
    @Deprecated
    public void testWith() throws Exception {
        ConfigOperator noop = (Configuration config) -> config;
        assertThat(Configuration.current().with(noop)).isNotNull();
    }

    @Test
    @Deprecated
    public void testQuery() throws Exception {
        ConfigQuery<String> stringQuery = (ConfigQuery) (Configuration config) -> config.get("String");
        assertThat(Configuration.current().query(stringQuery)).isEqualTo("aStringValue");
    }

    @Test
    public void testMap() throws Exception {
        UnaryOperator<Configuration> noop = (Configuration config) -> config;
        assertThat(Configuration.current().map(noop)).isNotNull();
        assertThat(Configuration.current().map(noop)== Configuration.current());
    }

    @Test
    public void testAdapt() throws Exception {
        Function<Configuration, String> stringQuery = (Configuration config) -> config.get("String");
        assertThat(Configuration.current().adapt(stringQuery)).isEqualTo("aStringValue");
    }


    /**
     * Test of getConfiguration method, of class ConfigurationProvider.
     */
    @Test
    public void testGetSetConfiguration() {
        Configuration currentConfig = Configuration.current();
        assertThat(currentConfig instanceof Configuration).isTrue();
        Configuration newConfig = Mockito.mock(Configuration.class);
        try{
            Configuration.setCurrent(newConfig);
            assertThat(Configuration.current()).isEqualTo(newConfig);
        }finally{
            Configuration.setCurrent(currentConfig);
        }
        assertThat(Configuration.current()).isEqualTo(currentConfig);
    }

    /**
     * Test of createConfigurationBuilder method, of class ConfigurationProvider.
     */
    @Test
    public void testGetConfigurationBuilder() {
        ConfigurationBuilder result = Configuration.createConfigurationBuilder();
        assertThat(result instanceof ConfigurationBuilder).isTrue();
    }

    @Test
    public void testEmpty() {
        Configuration c = Configuration.EMPTY;
        assertThat(c).isNotNull();
        assertThat(c.get("foo")).isNull();
        assertThat(c.get("foo", Boolean.class)).isNull();
        assertThat(c.get(Arrays.asList("foo", "bar"))).isNull();
        assertThat(c.getOrDefault("foo", "")).isEqualTo("");
        assertThat(c.getOrDefault("foo", Integer.class, 234)).isEqualTo(234);
        assertThat(c.getOrDefault(Arrays.asList("foo", "bar"), "default")).isEqualTo("default");
        assertThat(c.getOrDefault(Arrays.asList("foo", "bar"), Integer.class, 234)).isEqualTo(234);
        assertThat(c.get(Arrays.asList("foo", "bar"), Integer.class)).isNull();
        assertThat(c.getContext()).isEqualTo(ConfigurationContext.EMPTY);
        assertThat(c.getOptional("kjhkjh")).isNotNull();
        assertThat(c.getOptional("kjhkjh")).isNotPresent();
        assertThat(c.getOptional("kjhkjh", Integer.class)).isNotNull();
        assertThat(c.getOptional("kjhkjh", Integer.class)).isNotPresent();
        assertThat(c.get("foo")).isNull();
        assertThat(c.get("foo", Boolean.class)).isNull();
    }
}
