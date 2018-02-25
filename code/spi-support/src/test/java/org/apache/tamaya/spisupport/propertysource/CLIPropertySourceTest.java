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
package org.apache.tamaya.spisupport.propertysource;

import java.io.StringReader;
import java.io.StringWriter;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for PropertySource for reading main arguments as configuration.
 */
public class CLIPropertySourceTest {

    @Test
    public void setCLIProps() throws Exception {
        StringWriter stringBufferWriter = new StringWriter();
        System.getProperties().store(stringBufferWriter, null);
        String before = stringBufferWriter.toString();

        try {
            System.clearProperty("main.args");
            
            CLIPropertySource ps = new CLIPropertySource();
            assertTrue(ps.getProperties().isEmpty());
            
            ps = new CLIPropertySource(26);
            assertTrue(ps.getProperties().isEmpty());
            assertEquals(26, ps.getOrdinal());
            
            ps = new CLIPropertySource("-a", "b");
            assertFalse(ps.getProperties().isEmpty());
            assertEquals(ps.getProperties().get("a").getValue(), "b");
            assertTrue(ps.toStringValues().contains("args=[-a, b]"));
            
            ps = new CLIPropertySource(16, "-c", "d");
            assertFalse(ps.getProperties().isEmpty());
            assertEquals(ps.getProperties().get("c").getValue(), "d");
            assertEquals(16, ps.getOrdinal());
            
            CLIPropertySource.initMainArgs("-e", "f");
            assertFalse(ps.getProperties().isEmpty());
            assertEquals(ps.getProperties().get("e").getValue(), "f");
            
            CLIPropertySource.initMainArgs("--g");
            assertFalse(ps.getProperties().isEmpty());
            assertEquals(ps.getProperties().get("g").getValue(), "g");
            
            CLIPropertySource.initMainArgs("sss");
            assertFalse(ps.getProperties().isEmpty());
            assertEquals(ps.getProperties().get("sss").getValue(), "sss");
            
            CLIPropertySource.initMainArgs("-a", "b", "--c", "sss", "--val=vvv");
            assertFalse(ps.getProperties().isEmpty());
            assertEquals(ps.getProperties().get("a").getValue(), "b");
            assertEquals(ps.getProperties().get("c").getValue(), "c");
            assertEquals(ps.getProperties().get("sss").getValue(), "sss");
            
            System.setProperty("main.args", "-a b\t--c sss  ");
            ps = new CLIPropertySource();
            assertFalse(ps.getProperties().isEmpty());
            System.clearProperty("main.args");
            assertEquals(ps.getProperties().get("a").getValue(), "b");
            assertEquals(ps.getProperties().get("c").getValue(), "c");
            assertEquals(ps.getProperties().get("sss").getValue(), "sss");
            
        } finally {
            System.getProperties().clear();
            System.getProperties().load(new StringReader(before));
        }
    }
}
