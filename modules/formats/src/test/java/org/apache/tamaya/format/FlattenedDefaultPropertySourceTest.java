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
package org.apache.tamaya.format;

import org.apache.tamaya.format.formats.PropertiesFormat;
import org.apache.tamaya.spi.PropertySource;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Tests for {@link org.apache.tamaya.format.FlattenedDefaultPropertySource}.
 */
public class FlattenedDefaultPropertySourceTest {

    @Test
    public void testGetName() throws Exception {
        FlattenedDefaultPropertySource ps = new FlattenedDefaultPropertySource(createConfigurationData("test1"));
        assertEquals("test1", ps.getName());
    }

    private ConfigurationData createConfigurationData(String sourceName) {
        return ConfigurationDataBuilder.of(sourceName, new PropertiesFormat())
                .addProperty("a", "aValue").addProperty("section1", "sectionKey1", "sectionValue11")
                .addSections("section1", "section12")
                .addProperty("section2", "sectionKey1", "sectionValue21").build();
    }

    private ConfigurationData createConfigurationData(String sourceName, int ordinal) {
        return ConfigurationDataBuilder.of(sourceName, new PropertiesFormat())
                .addProperty("a", "aValue").addProperty("section1", "sectionKey1", "sectionValue11")
                .addSections("section1", "section12").addProperty(PropertySource.TAMAYA_ORDINAL, String.valueOf(ordinal))
                .addProperty("section2", "sectionKey1", "sectionValue21").build();
    }

    private ConfigurationData createConfigurationDataNoDefault(String sourceName) {
        return ConfigurationDataBuilder.of(sourceName, new PropertiesFormat())
                .addProperty("section1", "sectionKey1", "sectionValue11")
                .addSections("section1", "section12")
                .addProperty("section2", "sectionKey1", "sectionValue21").build();
    }

    @Test
    public void testGetOrdinal() throws Exception {
        FlattenedDefaultPropertySource ps = new FlattenedDefaultPropertySource(createConfigurationData("test1", 11));
        assertEquals(11, ps.getOrdinal());
    }

    @Test
    public void testGet() throws Exception {
        FlattenedDefaultPropertySource ps = new FlattenedDefaultPropertySource(createConfigurationData("test2"));
        assertEquals("aValue", ps.get("a"));
        assertNull(ps.get("section1.sectionKey1"));
        assertNull(ps.get("section2.sectionKey1"));
        ps = new FlattenedDefaultPropertySource(createConfigurationDataNoDefault("test2"));
        assertEquals("sectionValue11", ps.get("section1.sectionKey1"));
        assertEquals("sectionValue21", ps.get("section2.sectionKey1"));
        assertNull(ps.get("a"));
        assertNull(ps.get("section1"));
    }

    @Test
    public void testGetProperties() throws Exception {
        FlattenedDefaultPropertySource ps = new FlattenedDefaultPropertySource(createConfigurationData("test3"));
        assertNotNull(ps.getProperties());
        assertEquals("aValue", ps.getProperties().get("a"));
        assertNull(ps.getProperties().get("section1.sectionKey1"));
        assertNull(ps.getProperties().get("section2.sectionKey1"));
        ps = new FlattenedDefaultPropertySource(createConfigurationDataNoDefault("test3"));
        assertNotNull(ps.getProperties());
        assertEquals("sectionValue11", ps.getProperties().get("section1.sectionKey1"));
        assertEquals("sectionValue21", ps.getProperties().get("section2.sectionKey1"));
        assertNull(ps.get("section1"));
    }
}