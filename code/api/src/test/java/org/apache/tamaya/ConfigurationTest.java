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

import org.junit.Test;
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
        assertThat(Boolean.TRUE).isEqualTo(ConfigurationProvider.getConfiguration().get("booleanTrue", Boolean.class));
        assertThat(Boolean.FALSE).isEqualTo(ConfigurationProvider.getConfiguration().get("booleanFalse", Boolean.class));
        assertThat((int) Byte.MAX_VALUE).isEqualTo((int) ConfigurationProvider.getConfiguration().get("byte", Byte.class));
        assertThat(Integer.MAX_VALUE).isEqualTo((int) ConfigurationProvider.getConfiguration().get("int", Integer.class));
        assertThat(Long.MAX_VALUE).isEqualTo((long) ConfigurationProvider.getConfiguration().get("long", Long.class));
        assertThat(Float.MAX_VALUE).isCloseTo((float) ConfigurationProvider.getConfiguration().get("float", Float.class), within(0.001f));
        assertThat(Double.MAX_VALUE).isEqualTo(ConfigurationProvider.getConfiguration().get("double", Double.class));
        assertThat("aStringValue").isEqualTo(ConfigurationProvider.getConfiguration().get("String"));
    }

    @Test
    public void testGetBoolean() throws Exception {
        assertThat(ConfigurationProvider.getConfiguration().get("booleanTrue", Boolean.class)).isTrue();
        assertThat(ConfigurationProvider.getConfiguration().get("booleanFalse", Boolean.class)).isFalse();
        assertThat(ConfigurationProvider.getConfiguration().get("foorBar", Boolean.class)).isFalse();
    }

    @Test
    public void testGetInteger() throws Exception {
        assertThat(Integer.MAX_VALUE).isEqualTo((int) ConfigurationProvider.getConfiguration().get("int", Integer.class));
    }

    @Test
    public void testGetLong() throws Exception {
        assertThat(Long.MAX_VALUE).isEqualTo((long) ConfigurationProvider.getConfiguration().get("long", Long.class));
    }

    @Test
    public void testGetDouble() throws Exception {
        assertThat(Double.MAX_VALUE).isEqualTo(ConfigurationProvider.getConfiguration().get("double", Double.class));
    }

    @Test
    public void testGetOrDefault() throws Exception {
        assertThat("StringIfThereWasNotAValueThere").isEqualTo(ConfigurationProvider.getConfiguration().getOrDefault("nonexistant", "StringIfThereWasNotAValueThere"));
        assertThat("StringIfThereWasNotAValueThere").isEqualTo(ConfigurationProvider.getConfiguration().getOrDefault("nonexistant", String.class, "StringIfThereWasNotAValueThere"));
    }

    @Test
    public void testToBuilder() throws Exception {
        assertThat(ConfigurationProvider.getConfiguration().toBuilder()).isNotNull();
    }

    @Test
    public void testWith() throws Exception {
        ConfigOperator noop = (Configuration config) -> config;
        assertThat(ConfigurationProvider.getConfiguration().with(noop)).isNotNull();
    }

    @Test
    public void testQuery() throws Exception {
        ConfigQuery<String> stringQuery = (ConfigQuery) (Configuration config) -> config.get("String");
        assertThat(ConfigurationProvider.getConfiguration().query(stringQuery)).isEqualTo("aStringValue");
    }
}
