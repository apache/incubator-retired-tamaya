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

import static org.junit.Assert.*;

/**
 * Test class that tests the default methods implemented on {@link org.apache.tamaya.Configuration}. The provided
 * {@link org.apache.tamaya.TestConfiguration} is implemeted with maximal use of the default methods.
 */
public class ConfigurationTest {

    @org.junit.Test
    public void testGetAdapted() throws Exception {
        assertEquals(Boolean.TRUE, Configuration.current().getAdapted("booleanTrue", (s) -> Boolean.valueOf(s)).get());
        assertEquals(Boolean.FALSE, Configuration.current().getAdapted("booleanFalse", (s) -> Boolean.valueOf(s)).get());
        assertEquals((int)Byte.MAX_VALUE, (int)Configuration.current().getAdapted("byte", (s) -> Byte.valueOf(s)).get());
        assertEquals((int)Integer.MAX_VALUE, (int)Configuration.current().getAdapted("int", (s) -> Integer.valueOf(s)).get());
        assertEquals((long)Long.MAX_VALUE, (long)Configuration.current().getAdapted("long", (s) -> Long.valueOf(s)).get());
        assertEquals((double)Float.MAX_VALUE, (double)Configuration.current().getAdapted("float", (s) -> Float.valueOf(s)).get(), 0.0d);
        assertEquals((double)Double.MAX_VALUE, (double)Configuration.current().getAdapted("double", (s) -> Double.valueOf(s)).get(), 0.0d);
    }

    @org.junit.Test
    public void testGetBoolean() throws Exception {
        assertTrue(Configuration.current().getBoolean("booleanTrue"));
        assertFalse(Configuration.current().getBoolean("booleanFalse"));
        assertFalse(Configuration.current().getBoolean("foorBar"));
    }

    @org.junit.Test
    public void testGetInteger() throws Exception {
        assertEquals(Integer.MAX_VALUE,Configuration.current().getInteger("int").getAsInt());
    }

    @org.junit.Test
    public void testGetLong() throws Exception {
        assertEquals(Long.MAX_VALUE,Configuration.current().getLong("long").getAsLong());
    }

    @org.junit.Test
    public void testGetDouble() throws Exception {
        assertEquals(Double.MAX_VALUE,Configuration.current().getDouble("double").getAsDouble(), 0.0d);
    }

    @org.junit.Test
    public void testWith() throws Exception {
        assertEquals(Configuration.current(), Configuration.current().with(c-> c));
    }

    @org.junit.Test
    public void testQuery() throws Exception {
        assertEquals("myFooResult", Configuration.current().query(c -> "myFooResult"));
    }
}