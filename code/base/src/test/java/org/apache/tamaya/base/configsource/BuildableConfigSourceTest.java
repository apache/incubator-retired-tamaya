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
package org.apache.tamaya.base.configsource;

import org.apache.tamaya.base.configsource.BuildableConfigSource;
import org.junit.Test;

import static org.junit.Assert.*;

public class BuildableConfigSourceTest {
    @Test
    public void getOrdinal() throws Exception {
        BuildableConfigSource ps1 = BuildableConfigSource.builder()
                .withOrdinal(55).build();
        assertEquals(55, ps1.getOrdinal());
    }

    @Test
    public void getName() throws Exception {
        BuildableConfigSource ps1 = BuildableConfigSource.builder()
                .withName("test1").build();
        assertEquals("test1", ps1.getName());
        ps1 = BuildableConfigSource.builder().build();
        assertNotNull(ps1.getName());
    }

    @Test
    public void get() throws Exception {
        BuildableConfigSource ps1 = BuildableConfigSource.builder()
                .withProperty("a", "b").build();
        assertEquals("b", ps1.getValue("a"));
    }

    @Test
    public void getProperties() throws Exception {
        BuildableConfigSource ps1 = BuildableConfigSource.builder()
                .withProperty("a", "b").build();
        assertNotNull(ps1.getProperties());
        assertEquals(1, ps1.getProperties().size());
        assertEquals("b", ps1.getProperties().get("a"));
    }

    @Test
    public void equals() throws Exception {
        BuildableConfigSource ps1 = BuildableConfigSource.builder()
                .withName("test1").build();
        BuildableConfigSource ps2 = BuildableConfigSource.builder()
                .withName("test1").build();
        assertEquals(ps1, ps2);
        ps2 = BuildableConfigSource.builder()
                .withName("test2").build();
        assertNotEquals(ps1, ps2);
    }

    @Test
    public void testHashCode() throws Exception {
        BuildableConfigSource ps1 = BuildableConfigSource.builder()
                .withName("test1").build();
        BuildableConfigSource ps2 = BuildableConfigSource.builder()
                .withName("test1").build();
        assertEquals(ps1.hashCode(), ps2.hashCode());
        ps2 = BuildableConfigSource.builder()
                .withName("test2").build();
        assertNotEquals(ps1.hashCode(), ps2.hashCode());
    }

    @Test
    public void builder() throws Exception {
        assertNotNull(BuildableConfigSource.builder());
        assertNotEquals(BuildableConfigSource.builder(), BuildableConfigSource.builder());
    }

}