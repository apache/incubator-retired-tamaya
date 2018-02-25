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
import static org.junit.Assert.*;

/**
 * Test class that tests the default methods implemented on
 * {@link org.apache.tamaya.Configuration}. The provided
 * {@link org.apache.tamaya.TestConfiguration} is implemented with maximal use of
 * the default methods.
 */
public class ConfigurationTest {

    @Test
    public void testget() throws Exception {
        assertEquals(Boolean.TRUE, ConfigurationProvider.getConfiguration().get("booleanTrue", Boolean.class));
        assertEquals(Boolean.FALSE, ConfigurationProvider.getConfiguration().get("booleanFalse", Boolean.class));
        assertEquals((int) Byte.MAX_VALUE, (int) ConfigurationProvider.getConfiguration().get("byte", Byte.class));
        assertEquals(Integer.MAX_VALUE, (int) ConfigurationProvider.getConfiguration().get("int", Integer.class));
        assertEquals(Long.MAX_VALUE, (long) ConfigurationProvider.getConfiguration().get("long", Long.class));
        assertEquals(Float.MAX_VALUE, (double) ConfigurationProvider.getConfiguration().get("float", Float.class), 0.0d);
        assertEquals(Double.MAX_VALUE, ConfigurationProvider.getConfiguration().get("double", Double.class), 0.0d);
        assertEquals("aStringValue", ConfigurationProvider.getConfiguration().get("String"));
    }

    @Test
    public void testGetBoolean() throws Exception {
        assertTrue(ConfigurationProvider.getConfiguration().get("booleanTrue", Boolean.class));
        assertFalse(ConfigurationProvider.getConfiguration().get("booleanFalse", Boolean.class));
        assertFalse(ConfigurationProvider.getConfiguration().get("foorBar", Boolean.class));
    }

    @Test
    public void testGetInteger() throws Exception {
        assertEquals(Integer.MAX_VALUE, (int) ConfigurationProvider.getConfiguration().get("int", Integer.class));
    }

    @Test
    public void testGetLong() throws Exception {
        assertEquals(Long.MAX_VALUE, (long) ConfigurationProvider.getConfiguration().get("long", Long.class));
    }

    @Test
    public void testGetDouble() throws Exception {
        assertEquals(Double.MAX_VALUE, ConfigurationProvider.getConfiguration().get("double", Double.class), 0.0d);
    }

    @Test
    public void testGetOrDefault() throws Exception {
        assertEquals("StringIfThereWasNotAValueThere", ConfigurationProvider.getConfiguration().getOrDefault("nonexistant", "StringIfThereWasNotAValueThere"));
        assertEquals("StringIfThereWasNotAValueThere", ConfigurationProvider.getConfiguration().getOrDefault("nonexistant", String.class, "StringIfThereWasNotAValueThere"));
    }

    @Test
    public void testToBuilder() throws Exception {
        assertNotNull(ConfigurationProvider.getConfiguration().toBuilder());
    }
    
    @Test
    public void testWith() throws Exception {
        ConfigOperator noop = (Configuration config) -> config;
        assertNotNull(ConfigurationProvider.getConfiguration().with(noop));
    }
    
    @Test
    public void testQuery() throws Exception {
        ConfigQuery<String> stringQuery = (ConfigQuery) (Configuration config) -> config.get("String");
        assertEquals("aStringValue", ConfigurationProvider.getConfiguration().query(stringQuery));
    }
}
