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
package org.apache.tamaya.core.propertysource;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for PropertySource for reading main arguments as configuration.
 */
public class CLIPropertySourceTest {

    @Test
    public void setCLIProps() throws Exception {
        System.clearProperty("main.args");
        CLIPropertySource ps = new CLIPropertySource();
        assertTrue(ps.getProperties().isEmpty());
        CLIPropertySource.initMainArgs("-a", "b");
        assertFalse(ps.getProperties().isEmpty());
        assertEquals(ps.getProperties().get("a").getValue(), "b");
        CLIPropertySource.initMainArgs("--c");
        assertFalse(ps.getProperties().isEmpty());
        assertEquals(ps.getProperties().get("c").getValue(), "c");
        CLIPropertySource.initMainArgs("sss");
        assertFalse(ps.getProperties().isEmpty());
        assertEquals("sss", ps.getProperties().get("sss").getValue());
        CLIPropertySource.initMainArgs("-a", "b", "--c", "sss", "--val=vvv");
        assertFalse(ps.getProperties().isEmpty());
        assertEquals("b", ps.getProperties().get("a").getValue());
        assertEquals("c", ps.getProperties().get("c").getValue());
        assertEquals("sss", ps.getProperties().get("sss").getValue());
    // getProperties() throws Exception {
        System.setProperty("main.args", "-a b\t--c sss  ");
        ps = new CLIPropertySource();
        assertFalse(ps.getProperties().isEmpty());
        System.clearProperty("main.args");
        assertEquals("b", ps.getProperties().get("a").getValue());
        assertEquals("c", ps.getProperties().get("c").getValue());
        assertEquals("sss", ps.getProperties().get("sss").getValue());
    }
}