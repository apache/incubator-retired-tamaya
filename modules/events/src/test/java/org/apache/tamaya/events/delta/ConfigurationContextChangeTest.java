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
import org.apache.tamaya.core.propertysource.SystemPropertySource;
import org.apache.tamaya.spi.PropertySource;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test class for {@link org.apache.tamaya.events.delta.ConfigurationContextChange}.
 */
public class ConfigurationContextChangeTest {

    @Test
    public void testEmptyChangeSet() throws Exception {
        ConfigurationContextChange change = ConfigurationContextChange.emptyChangeSet();
        assertNotNull(change);
        assertTrue(change.isEmpty());
    }

    @Test
    public void testGetVersion() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        ConfigurationContextChange change = ConfigurationContextChangeBuilder.of().build();
        assertNotNull(change.getVersion());
        change = ConfigurationContextChangeBuilder.of().setVersion("version2").build();
        assertNotNull(change.getVersion());
        assertEquals("version2", change.getVersion());
    }

    @Test
    public void testGetTimestamp() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        ConfigurationContextChange change = ConfigurationContextChangeBuilder.of().build();
        assertTrue((System.currentTimeMillis() - change.getTimestamp()) <= 10L);
        change = ConfigurationContextChangeBuilder.of().setTimestamp(10L).build();
        assertEquals(10L, change.getTimestamp());
    }

    @Test
    public void testGetPropertySourceChanges() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        ConfigurationContextChange change = ConfigurationContextChangeBuilder.of().build();
        assertTrue(change.getPropertySourceChanges(). isEmpty());
        change = ConfigurationContextChangeBuilder.of().build();
        assertTrue(change.getPropertySourceChanges(). isEmpty());
    }

    @Test
    public void testGetPropertySourceUpdates() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        ConfigurationContextChange change = ConfigurationContextChangeBuilder.of().build();
        assertTrue(change.getPropertySourceChanges(). isEmpty());
        change = ConfigurationContextChangeBuilder.of().build();
        assertTrue(change.getPropertySourceUpdates(). isEmpty());
    }

    @Test
    public void testGetRemovedPropertySources() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        ConfigurationContextChange change = ConfigurationContextChangeBuilder.of().build();
        assertTrue(change.getPropertySourceChanges(). isEmpty());
        change = ConfigurationContextChangeBuilder.of().build();
        assertTrue(change.getRemovedPropertySources(). isEmpty());
    }

    @Test
    public void testGetAddedPropertySources() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        ConfigurationContextChange change = ConfigurationContextChangeBuilder.of().build();
        assertTrue(change.getPropertySourceChanges(). isEmpty());
        change = ConfigurationContextChangeBuilder.of().build();
        assertTrue(change.getAddedPropertySources().isEmpty());
    }

    @Test
    public void testGetUpdatedPropertySources() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        ConfigurationContextChange change = ConfigurationContextChangeBuilder.of().build();
        assertTrue(change.getPropertySourceChanges(). isEmpty());
        change = ConfigurationContextChangeBuilder.of().build();
        assertTrue(change.getUpdatedPropertySources().isEmpty());
    }

    @Test
    public void testIsAffected() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        PropertySource ps = new SystemPropertySource();
        ConfigurationContextChange change = ConfigurationContextChangeBuilder.of().changedPropertySource(
                PropertySourceChangeBuilder.of(ps, ChangeType.UPDATED).build()
        ).build();
        String toString = change.toString();
        assertTrue(change.isAffected(ps));
    }

    @Test
    public void testIsEmpty() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        ConfigurationContextChange change = ConfigurationContextChangeBuilder.of().build();
        assertTrue(change.isEmpty());
        change = ConfigurationContextChangeBuilder.of().newPropertySource(new SystemPropertySource()).build();
        assertFalse(change.isEmpty());
    }

    @Test
    public void testToString() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        ConfigurationContextChange change = ConfigurationContextChangeBuilder.of().newPropertySource(new SystemPropertySource()).build();
        String toString = change.toString();
        assertNotNull(toString);
        assertTrue(toString.contains(new SystemPropertySource().getName()));
    }
}