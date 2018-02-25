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
package org.apache.tamaya.spisupport.propertysource;

import java.io.File;
import org.apache.tamaya.ConfigException;
import org.apache.tamaya.spi.PropertyValue;
import org.junit.Test;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.hasEntry;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class SimplePropertySourceTest {

    @Test
    public void successfulCreationWithPropertiesFromXMLPropertiesFile() {
        URL resource = getClass().getResource("/valid-properties.xml");

        SimplePropertySource source = new SimplePropertySource(resource);

        assertThat(source, notNullValue());
        assertThat(source.getProperties(), aMapWithSize(2)); // double the size for .source values.
        assertThat(source.getProperties(), hasEntry("a", PropertyValue.of("a", "b", resource.toString())));
        assertThat(source.getProperties(), hasEntry("b", PropertyValue.of("b", "1", resource.toString())));

    }

    @Test
    public void failsToCreateFromNonXMLPropertiesXMLFile() {
        URL resource = getClass().getResource("/non-xml-properties.xml");
        ConfigException catchedException = null;

        try {
            new SimplePropertySource(resource);
        } catch (ConfigException ce) {
            catchedException = ce;
        }

        assertThat(catchedException.getMessage(), allOf(startsWith("Error loading properties from"),
                endsWith("non-xml-properties.xml")));
    }

    @Test
    public void failsToCreateFromInvalidPropertiesXMLFile() {
        URL resource = getClass().getResource("/invalid-properties.xml");
        ConfigException catchedException = null;

        try {
            new SimplePropertySource(resource);
        } catch (ConfigException ce) {
            catchedException = ce;
        }

        assertThat(catchedException.getMessage(), allOf(startsWith("Error loading properties from"),
                endsWith("invalid-properties.xml")));
    }

    @Test
    public void successfulCreationWithPropertiesFromSimplePropertiesFile() {
        URL resource = getClass().getResource("/testfile.properties");

        SimplePropertySource source = new SimplePropertySource(resource);

        assertThat(source, notNullValue());
        assertThat(source.getProperties(), aMapWithSize(5)); // double the size for .source values.
    }

    @Test
    public void testWithMap() {
        Map<String, String> propertyFirst = new HashMap<>();
        propertyFirst.put("firstKey", "firstValue");

        SimplePropertySource source = new SimplePropertySource("testWithMap", propertyFirst, 166);
        assertEquals("testWithMap", source.getName());
        assertEquals(166, source.getDefaultOrdinal());
        assertTrue(source.getProperties().containsKey("firstKey"));

    }

    @Test
    public void builder() throws Exception {
        assertNotNull(SimplePropertySource.newBuilder());
        assertNotEquals(SimplePropertySource.newBuilder(), SimplePropertySource.newBuilder());
    }

    @Test
    public void getOrdinal() throws Exception {
        SimplePropertySource ps1 = SimplePropertySource.newBuilder()
                .withUuidName()
                .withOrdinal(55)
                .withDefaultOrdinal(166)
                .build();

        assertEquals(55, ps1.getOrdinal());
        assertEquals(166, ps1.getDefaultOrdinal());
    }

    @Test
    public void getName() throws Exception {
        //SimplePropertySource ps1 = SimplePropertySource.newBuilder()
        //        .withName("test1")
        //        .build();
        //assertEquals("test1", ps1.getName());
        SimplePropertySource ps1 = SimplePropertySource.newBuilder()
                .withUuidName().build();
        assertNotNull(UUID.fromString(ps1.getName()));
    }

    @Test
    public void get() throws Exception {
        SimplePropertySource ps1 = SimplePropertySource.newBuilder()
                .withUuidName()
                .withProperty("a", "b").build();
        assertEquals("b", ps1.get("a").getValue());
    }

    @Test
    public void getProperties() throws Exception {
        SimplePropertySource ps1 = SimplePropertySource.newBuilder()
                .withUuidName()
                .withProperty("a", "b")
                .build();
        assertNotNull(ps1.getProperties());
        assertEquals(1, ps1.getProperties().size());
        assertEquals("b", ps1.getProperties().get("a").getValue());
    }

    @Test
    public void testScannable() {
        SimplePropertySource sps = SimplePropertySource.newBuilder().withUuidName().build();
        assertTrue(sps.isScannable());
    }

    @Test
    public void testBuilderWithMaps() {
        URL resource = getClass().getResource("/valid-properties.xml");
        File resourceAsFile = new File(resource.getPath());

        Map<String, String> propertyFirst = new HashMap<>();
        propertyFirst.put("firstKey", "firstValue");

        SimplePropertySource sps = SimplePropertySource.newBuilder()
                .withUuidName()
                .withProperties(propertyFirst)
                .withProperties(resource)
                .build();

        assertEquals("firstValue", sps.get("firstKey").getValue());
        assertThat(sps.getProperties(), hasEntry("a", PropertyValue.of("a", "b", resource.toString())));
        assertThat(sps.getProperties(), hasEntry("b", PropertyValue.of("b", "1", resource.toString())));

        sps = SimplePropertySource.newBuilder()
                .withUuidName()
                .withProperties(propertyFirst)
                .withProperties(resourceAsFile)
                .build();

        assertEquals("firstValue", sps.get("firstKey").getValue());
        assertThat(sps.getProperties(), hasEntry("a", PropertyValue.of("a", "b", resource.toString())));
        assertThat(sps.getProperties(), hasEntry("b", PropertyValue.of("b", "1", resource.toString())));
    }
}
