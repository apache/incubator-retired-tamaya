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
import org.apache.tamaya.base.configsource.BuildableConfigSourceProvider;
import org.junit.Test;

import static org.junit.Assert.*;

public class BuildableConfigSourceProviderTest {

    @Test
    public void getPropertySources() throws Exception {
        BuildableConfigSource ps = BuildableConfigSource.builder()
                .withName("test1").build();
        BuildableConfigSourceProvider prov = BuildableConfigSourceProvider.builder()
                .withPropertySourcs(ps).build();
        assertNotNull(prov);
        assertEquals(prov.getConfigSources(null).iterator().next(), ps);
    }

    @Test
    public void equals() throws Exception {
        BuildableConfigSource ps = BuildableConfigSource.builder()
                .withName("test1").build();
        BuildableConfigSourceProvider prov1 = BuildableConfigSourceProvider.builder()
                .withPropertySourcs(ps).build();
        BuildableConfigSourceProvider prov2 = BuildableConfigSourceProvider.builder()
                .withPropertySourcs(ps).build();
        assertEquals(prov1, prov2);
        BuildableConfigSource ps2 = BuildableConfigSource.builder()
                .withName("test12").build();
        prov2 = BuildableConfigSourceProvider.builder()
                .withPropertySourcs(ps2).build();
        assertNotEquals(prov1, prov2);
    }

    @Test
    public void testHashCode() throws Exception {
        BuildableConfigSource ps = BuildableConfigSource.builder()
                .withName("test1").build();
        BuildableConfigSourceProvider prov1 = BuildableConfigSourceProvider.builder()
                .withPropertySourcs(ps).build();
        BuildableConfigSourceProvider prov2 = BuildableConfigSourceProvider.builder()
                .withPropertySourcs(ps).build();
        assertEquals(prov1.hashCode(), prov2.hashCode());
        BuildableConfigSource ps2 = BuildableConfigSource.builder()
                .withName("test12").build();
        prov2 = BuildableConfigSourceProvider.builder()
                .withPropertySourcs(ps2).build();
        assertNotEquals(prov1.hashCode(), prov2.hashCode());
    }


    @Test
    public void builder() throws Exception {
        assertNotNull(BuildableConfigSource.builder());
        assertNotEquals(BuildableConfigSource.builder(), BuildableConfigSource.builder());
    }

}