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

import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;
import org.apache.tamaya.events.ConfigurationChange;
import org.apache.tamaya.events.ConfigurationChangeBuilder;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

/**
 * Test class for {@link ConfigurationChange}.
 */
public class ConfigurationChangeTest {

    @Test
    public void testEmptyChangeSet() throws Exception {
        ConfigurationChange change = ConfigurationChange.emptyChangeSet(ConfigurationProvider.getConfiguration());
        assertNotNull(change);
        assertTrue(change.isEmpty());
    }

    @Test
    public void testGetConfiguration() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        ConfigurationChange change = ConfigurationChangeBuilder.of(config).build();
        assertNotNull(change);
        assertTrue(change.isEmpty());
        for (Map.Entry<String, String> en : config.getProperties().entrySet()) {
            if (!"[meta]frozenAt".equals(en.getKey())) {
                assertEquals("Error for " + en.getKey(), en.getValue(), change.getResource().get(en.getKey()));
            }
        }
    }

    @Test
    public void testGetVersion() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        ConfigurationChange change = ConfigurationChangeBuilder.of(config).build();
        assertNotNull(change.getVersion());
        change = ConfigurationChangeBuilder.of(config).setVersion("version2").build();
        assertNotNull(change.getVersion());
        assertEquals("version2", change.getVersion());
    }

    @Test
    public void testGetTimestamp() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        ConfigurationChange change = ConfigurationChangeBuilder.of(config).build();
        assertTrue((System.currentTimeMillis() - change.getTimestamp()) <= 10L);
        change = ConfigurationChangeBuilder.of(config).setTimestamp(10L).build();
        assertEquals(10L, change.getTimestamp());
    }

    @Test
    public void testGetEvents() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        ConfigurationChange change = ConfigurationChangeBuilder.of(config).removeKey("key1", "key2").build();
        assertTrue(change.getChanges().size() == 2);
        change = ConfigurationChangeBuilder.of(config).addChange("key1Added", "value1Added").build();
        assertTrue(change.getChanges().size() == 1);
    }

    @Test
    public void testGetRemovedSize() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        ConfigurationChange change = ConfigurationChangeBuilder.of(config).removeKey("java.version", "key2").build();
        assertTrue(change.getRemovedSize() == 2);
        assertTrue(change.getAddedSize() == 0);
    }

    @Test
    public void testGetAddedSize() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        ConfigurationChange change = ConfigurationChangeBuilder.of(config).addChange("key1", "key2").build();
        assertTrue(change.getAddedSize() == 1);
        assertTrue(change.getRemovedSize() == 0);
    }

    @Test
    public void testGetUpdatedSize() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        ConfigurationChange change = ConfigurationChangeBuilder.of(config).addChange("java.version", "1.8").build();
        assertTrue(change.getUpdatedSize() == 1);
    }

    @Test
    public void testIsRemoved() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        ConfigurationChange change = ConfigurationChangeBuilder.of(config).removeKey("java.version").build();
        assertTrue(change.isRemoved("java.version"));
    }

    @Test
    public void testIsAdded() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        ConfigurationChange change = ConfigurationChangeBuilder.of(config).addChange("key1", "key2").build();
        assertTrue(change.isAdded("key1"));
    }

    @Test
    public void testIsUpdated() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        ConfigurationChange change = ConfigurationChangeBuilder.of(config).addChange("java.version", "1.8").build();
        assertTrue(change.isUpdated("java.version"));
    }

    @Test
    public void testContainsKey() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        ConfigurationChange change = ConfigurationChangeBuilder.of(config).addChange("key1", "key2").build();
        assertTrue(change.isKeyAffected("key1"));
        assertFalse(change.isKeyAffected("key2"));
        change = ConfigurationChangeBuilder.of(config).removeKey("java.version").build();
        assertFalse(change.isKeyAffected("java.version"));
        assertFalse(change.isKeyAffected("key2"));
    }

    @Test
    public void testIsEmpty() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        ConfigurationChange change = ConfigurationChangeBuilder.of(config).build();
        assertTrue(change.isEmpty());
    }

    @Test
    public void testToString() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        ConfigurationChange change = ConfigurationChangeBuilder.of(config).addChange("key1", "key2").build();
        change = ConfigurationChangeBuilder.of(config).removeKey("java.version").build();
        assertTrue(change.toString().contains("timestamp"));
        assertTrue(change.toString().contains("version"));
        assertTrue(change.toString().contains("configuration"));
        assertFalse(change.toString().contains("key1"));
        assertFalse(change.toString().contains("key2"));
    }
}