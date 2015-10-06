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
package org.apache.tamaya.events.delta;

import org.apache.tamaya.core.propertysource.EnvironmentPropertySource;
import org.apache.tamaya.core.propertysource.SimplePropertySource;
import org.apache.tamaya.core.propertysource.SystemPropertySource;
import org.apache.tamaya.spi.PropertySource;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Tests for {@link org.apache.tamaya.events.delta.PropertySourceChange} and its builder.
 */
public class PropertySourceChangeTest {

    private static final PropertySource myPS = new SystemPropertySource();

    @Test
    public void testGetChangeType() throws Exception {
        PropertySourceChange change = PropertySourceChangeBuilder.of(myPS, ChangeType.DELETED).build();
        assertEquals(change.getChangeType(), ChangeType.DELETED);
        change = PropertySourceChangeBuilder.of(myPS, ChangeType.UPDATED).build();
        assertEquals(change.getChangeType(), ChangeType.UPDATED);
    }

    @Test
    public void testGetPropertySource() throws Exception {
        PropertySourceChange change = PropertySourceChangeBuilder.of(myPS, ChangeType.DELETED).build();
        assertEquals(change.getResource().getName(), myPS.getName());
    }

    @Test
    public void testGetVersion() throws Exception {
        PropertySourceChange change = PropertySourceChangeBuilder.of(myPS, ChangeType.DELETED)
                .setVersion("myVersion1").build();
        assertEquals(change.getVersion(), "myVersion1");
    }

    @Test
    public void testGetTimestamp() throws Exception {
        PropertySourceChange change = PropertySourceChangeBuilder.of(myPS, ChangeType.DELETED)
                .setTimestamp(111L).build();
        assertEquals(change.getTimestamp(), 111L);
    }

    @Test
    public void testGetEvents() throws Exception {
        PropertySourceChange change = PropertySourceChangeBuilder.of(myPS, ChangeType.DELETED)
                .addChanges(
                        new EnvironmentPropertySource()
                ).build();
        assertTrue(change.getChanges().size()>0);
    }

    @Test
    public void testGetRemovedSize() throws Exception {
        PropertySourceChange change = PropertySourceChangeBuilder.of(myPS, ChangeType.UPDATED)
                .addChanges(
                        new EnvironmentPropertySource()
                ).build();
        assertTrue(change.getRemovedSize()>0);
    }

    @Test
    public void testGetAddedSize() throws Exception {
        PropertySourceChange change = PropertySourceChangeBuilder.of(myPS, ChangeType.DELETED)
                .addChanges(
                        new EnvironmentPropertySource()
                ).build();
        assertTrue(change.getAddedSize()>0);
    }

    @Test
    public void testGetUpdatedSize() throws Exception {
        PropertySourceChange change = PropertySourceChangeBuilder.of(myPS, ChangeType.DELETED)
                .addChanges(
                        new EnvironmentPropertySource()
                ).build();
        assertTrue(change.getUpdatedSize()==0);
    }

    @Test
    public void testIsRemoved() throws Exception {
        Map<String, String> testData = new HashMap<>();
        testData.put("key1", "value1");
        testData.put("key2", "value2");
        PropertySource ps1 = new SimplePropertySource("test", testData);
        testData = new HashMap<>();
        testData.put("key1", "value2");
        testData.put("key3", "value3");
        PropertySource ps2 = new SimplePropertySource("test", testData);
        PropertySourceChange change = PropertySourceChangeBuilder.of(ps1, ChangeType.UPDATED)
                .addChanges(
                        ps2
                ).build();
        assertFalse(change.isRemoved("key1"));
        assertTrue(change.isRemoved("key2"));
        assertFalse(change.isRemoved("key3"));
    }

    @Test
    public void testIsAdded() throws Exception {
        Map<String, String> testData = new HashMap<>();
        testData.put("key1", "value1");
        testData.put("key2", "value2");
        PropertySource ps1 = new SimplePropertySource("test", testData);
        testData = new HashMap<>();
        testData.put("key1", "value2");
        testData.put("key3", "value3");
        PropertySource ps2 = new SimplePropertySource("test", testData);
        PropertySourceChange change = PropertySourceChangeBuilder.of(ps1, ChangeType.UPDATED)
                .addChanges(
                        ps2
                ).build();
        assertTrue(change.isAdded("key3"));
        assertFalse(change.isAdded("key2"));
        assertFalse(change.isAdded("key1"));
    }

    @Test
    public void testIsUpdated() throws Exception {
        Map<String, String> testData = new HashMap<>();
        testData.put("key1", "value1");
        testData.put("key2", "value2");
        PropertySource ps1 = new SimplePropertySource("test", testData);
        testData = new HashMap<>();
        testData.put("key1", "value2");
        testData.put("key3", "value3");
        PropertySource ps2 = new SimplePropertySource("test", testData);
        PropertySourceChange change = PropertySourceChangeBuilder.of(ps1, ChangeType.UPDATED)
                .addChanges(
                        ps2
                ).build();
        assertTrue(change.isUpdated("key1"));
        assertFalse(change.isUpdated("key2"));
        assertFalse(change.isUpdated("key3"));
    }

    @Test
    public void testContainsKey() throws Exception {
        PropertySourceChange change = PropertySourceChangeBuilder.of(new EnvironmentPropertySource(), ChangeType.DELETED)
                .addChanges(
                        myPS
                ).build();
        assertTrue(change.isKeyAffected("java.version"));
    }

    @Test
    public void testIsEmpty() throws Exception {
        PropertySourceChange change = PropertySourceChangeBuilder.of(new EnvironmentPropertySource(), ChangeType.DELETED)
                .build();
        assertTrue(change.isEmpty());
        change = PropertySourceChangeBuilder.of(new EnvironmentPropertySource(), ChangeType.DELETED)
                .addChanges(
                        myPS
                ).build();
        assertFalse(change.isEmpty());
    }

    @Test
    public void testOfAdded() throws Exception {
        PropertySourceChange change = PropertySourceChange.ofAdded(myPS);
        assertNotNull(change);
        assertEquals(change.getChangeType(), ChangeType.NEW);
    }

    @Test
    public void testOfDeleted() throws Exception {
        PropertySourceChange change = PropertySourceChange.ofDeleted(myPS);
        assertNotNull(change);
        assertEquals(change.getChangeType(), ChangeType.DELETED);
    }

    @Test
    public void testToString() throws Exception {
        PropertySourceChange change = PropertySourceChange.ofAdded(myPS);
        String toString = change.toString();
        assertNotNull(toString);
        assertTrue(toString.contains(myPS.getName()));
        change = PropertySourceChange.ofDeleted(myPS);
        toString = change.toString();
        assertNotNull(toString);
        assertTrue(toString.contains(myPS.getName()));
    }
}