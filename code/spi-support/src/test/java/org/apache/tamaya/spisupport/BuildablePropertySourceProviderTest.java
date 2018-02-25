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

import java.util.Arrays;
import java.util.Iterator;
import org.apache.tamaya.spisupport.propertysource.BuildablePropertySource;
import org.apache.tamaya.spisupport.propertysource.BuildablePropertySourceProvider;
import org.junit.Test;

import static org.junit.Assert.*;

public class BuildablePropertySourceProviderTest {

    @Test
    public void getPropertySources() throws Exception {
        BuildablePropertySource ps1 = BuildablePropertySource.builder()
                .withName("test1").build();
        BuildablePropertySource ps2 = BuildablePropertySource.builder()
                .withName("test2").build();
        BuildablePropertySourceProvider prov = BuildablePropertySourceProvider.builder()
                .withPropertySourcs(ps1)
                .withPropertySourcs(Arrays.asList(ps2))
                .build();
        assertNotNull(prov);
        Iterator testable = prov.getPropertySources().iterator();
        assertEquals(testable.next(), ps1);
        assertEquals(testable.next(), ps2);
    }

    @Test
    public void equals() throws Exception {
        BuildablePropertySource ps = BuildablePropertySource.builder()
                .withName("test1").build();
        BuildablePropertySourceProvider prov1 = BuildablePropertySourceProvider.builder()
                .withPropertySourcs(ps).build();
        BuildablePropertySourceProvider prov2 = BuildablePropertySourceProvider.builder()
                .withPropertySourcs(ps).build();
        BuildablePropertySource ps2 = BuildablePropertySource.builder()
                .withName("test2").build();
        BuildablePropertySourceProvider prov3 = BuildablePropertySourceProvider.builder()
                .withPropertySourcs(ps2).build();
        
        assertEquals(prov1, prov1);
        assertEquals(prov1, prov2);
        assertNotEquals(prov1, prov3);
        assertNotEquals(prov1, null);
        assertNotEquals(prov1, "aString");
    }

    @Test
    public void testHashCode() throws Exception {
        BuildablePropertySource ps = BuildablePropertySource.builder()
                .withName("test1").build();
        BuildablePropertySourceProvider prov1 = BuildablePropertySourceProvider.builder()
                .withPropertySourcs(ps).build();
        BuildablePropertySourceProvider prov2 = BuildablePropertySourceProvider.builder()
                .withPropertySourcs(ps).build();
        assertEquals(prov1.hashCode(), prov2.hashCode());
        BuildablePropertySource ps2 = BuildablePropertySource.builder()
                .withName("test12").build();
        prov2 = BuildablePropertySourceProvider.builder()
                .withPropertySourcs(ps2).build();
        assertNotEquals(prov1.hashCode(), prov2.hashCode());
    }


    @Test
    public void builder() throws Exception {
        assertNotNull(BuildablePropertySource.builder());
        assertNotEquals(BuildablePropertySource.builder(), BuildablePropertySource.builder());
    }

}