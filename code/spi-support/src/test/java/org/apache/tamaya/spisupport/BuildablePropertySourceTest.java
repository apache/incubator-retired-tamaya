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
package org.apache.tamaya.spisupport;

import org.apache.tamaya.spisupport.propertysource.BuildablePropertySource;
import org.junit.Test;

import static org.junit.Assert.*;

public class BuildablePropertySourceTest {
    @Test
    public void getOrdinal() throws Exception {
        BuildablePropertySource ps1 = BuildablePropertySource.builder()
                .withOrdinal(55).build();
        assertEquals(55, ps1.getOrdinal());
    }

    @Test
    public void getName() throws Exception {
        BuildablePropertySource ps1 = BuildablePropertySource.builder()
                .withName("test1").build();
        assertEquals("test1", ps1.getName());
        ps1 = BuildablePropertySource.builder().build();
        assertNotNull(ps1.getName());
    }

    @Test
    public void get() throws Exception {
        BuildablePropertySource ps1 = BuildablePropertySource.builder()
                .withSimpleProperty("a", "b").build();
        assertEquals("b", ps1.get("a").getValue());
    }

    @Test
    public void getProperties() throws Exception {
        BuildablePropertySource ps1 = BuildablePropertySource.builder()
                .withSimpleProperty("a", "b").build();
        assertNotNull(ps1.getProperties());
        assertEquals(1, ps1.getProperties().size());
        assertEquals("b", ps1.getProperties().get("a").getValue());
    }

    @Test
    public void equals() throws Exception {
        BuildablePropertySource ps1 = BuildablePropertySource.builder()
                .withName("test1").build();
        BuildablePropertySource ps2 = BuildablePropertySource.builder()
                .withName("test1").build();
        assertEquals(ps1, ps2);
        ps2 = BuildablePropertySource.builder()
                .withName("test2").build();
        assertNotEquals(ps1, ps2);
    }

    @Test
    public void testHashCode() throws Exception {
        BuildablePropertySource ps1 = BuildablePropertySource.builder()
                .withName("test1").build();
        BuildablePropertySource ps2 = BuildablePropertySource.builder()
                .withName("test1").build();
        assertEquals(ps1.hashCode(), ps2.hashCode());
        ps2 = BuildablePropertySource.builder()
                .withName("test2").build();
        assertNotEquals(ps1.hashCode(), ps2.hashCode());
    }

    @Test
    public void builder() throws Exception {
        assertNotNull(BuildablePropertySource.builder());
        assertNotEquals(BuildablePropertySource.builder(), BuildablePropertySource.builder());
    }

}