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
package org.apache.tamaya.mutableconfig.internal;

import org.apache.tamaya.ConfigurationProvider;
import org.apache.tamaya.mutableconfig.MutableConfigurationQuery;
import org.apache.tamaya.mutableconfig.MutableConfiguration;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.*;

/**
 * Tests for {@link PropertiesFileConfigBackendSpi}.
 */
public class PropertiesFileConfigBackendTest {
    /**
     * Test read write properties with rollback.
     *
     * @throws IOException the io exception
     */
    @Test
    public void testReadWriteProperties_WithCancel() throws IOException {
        File f = File.createTempFile("testReadWriteProperties_WithCancel",".properties");
        f.delete();
        MutableConfiguration req = ConfigurationProvider.getConfiguration().query(
                MutableConfigurationQuery.of(f.toURI()));
        req.put("key1", "value1");
        Map<String,String> cm = new HashMap<>();
        cm.put("key2", "value2");
        cm.put("key3", "value3");
        req.rollback();
        assertFalse(f.exists());
    }

    /**
     * Test read write properties with commit.
     *
     * @throws IOException the io exception
     */
    @Test
    public void testReadWriteProperties_WithCommit() throws IOException {
        File f = File.createTempFile("testReadWriteProperties_WithCommit",".properties");
        f.delete();
        MutableConfiguration req = ConfigurationProvider.getConfiguration().query(
                MutableConfigurationQuery.of(f.toURI()));
        req.put("key1", "value1");
        Map<String,String> cm = new HashMap<>();
        cm.put("key2", "value2");
        cm.put("key3", "value3");
        req.putAll(cm);
        req.commit();
        assertTrue(f.exists());
        MutableConfiguration req2 = ConfigurationProvider.getConfiguration().query(
                MutableConfigurationQuery.of(f.toURI()));
        assertTrue(req != req2);
        req2.remove("foo");
        req2.remove("key3");
        req2.put("key1", "value1.2");
        req2.put("key4", "value4");
        req2.commit();
        Properties props = new Properties();
        props.load(f.toURL().openStream());
        assertEquals(3, props.size());
        assertEquals("value1.2", props.getProperty("key1"));
        assertEquals("value2", props.getProperty("key2"));
        assertEquals("value4", props.getProperty("key4"));
    }

    /**
     * Test read write xml properties with commit.
     *
     * @throws IOException the io exception
     */
    @Test
    public void testReadWriteXmlProperties_WithCommit() throws IOException {
        File f = File.createTempFile("testReadWriteProperties_WithCommit",".xml");
        f.delete();
        MutableConfiguration req = ConfigurationProvider.getConfiguration().query(
                MutableConfigurationQuery.of(f.toURI()));
        req.put("key1", "value1");
        Map<String,String> cm = new HashMap<>();
        cm.put("key2", "value2");
        cm.put("key3", "value3");
        req.putAll(cm);
        req.commit();
        assertTrue(f.exists());
        MutableConfiguration req2 = ConfigurationProvider.getConfiguration().query(
                MutableConfigurationQuery.of(f.toURI()));
        assertTrue(req != req2);
        req2.remove("foo");
        req2.remove("key3");
        req2.put("key1", "value1.2");
        req2.put("key4", "value4");
        req2.commit();
        Properties props = new Properties();
        props.loadFromXML(f.toURL().openStream());
        assertEquals(3, props.size());
        assertEquals("value1.2", props.getProperty("key1"));
        assertEquals("value2", props.getProperty("key2"));
        assertEquals("value4", props.getProperty("key4"));
    }

}