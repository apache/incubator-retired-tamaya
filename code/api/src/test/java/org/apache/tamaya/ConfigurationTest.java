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
import org.junit.Test;
import org.mockito.Mockito;

import java.util.function.Function;
import java.util.function.UnaryOperator;

import static org.assertj.core.api.Assertions.*;

/**
 * Test class that tests the default methods implemented on
 * {@link org.apache.tamaya.Configuration}. The provided
 * {@link org.apache.tamaya.TestConfiguration} is implemented with maximal use
 * of the default methods.
 */
public class ConfigurationTest {

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
    public void testGetBoolean() throws Exception {
        assertThat(Configuration.current().get("booleanTrue", Boolean.class)).isTrue();
        assertThat(Configuration.current().get("booleanFalse", Boolean.class)).isFalse();
        assertThat(Configuration.current().get("foorBar", Boolean.class)).isFalse();
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
}
