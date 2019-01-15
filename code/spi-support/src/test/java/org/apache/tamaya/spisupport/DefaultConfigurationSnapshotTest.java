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
package org.apache.tamaya.spisupport;

import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationSnapshot;
import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.spi.ConfigurationContext;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class DefaultConfigurationSnapshotTest {

//    confkey1=javaconf-value1
//    confkey2=javaconf-value2
//    confkey3=javaconf-value3

    @Test
    public void testCreationForKeys() {
        Configuration config = Configuration.current();
        DefaultConfigurationSnapshot snapshot = new DefaultConfigurationSnapshot(config,
                Arrays.asList("confkey1", "confkey2", "confkey3"));
    }

    @Test
    public void getFrozenAtReturnsTheCorrectTimestamp() throws InterruptedException {
        Configuration source = mock(Configuration.class);
        Mockito.when(source.getContext()).thenReturn(ConfigurationContext.EMPTY);
        Mockito.when(source.getSnapshot(Mockito.anyCollection())).thenReturn(ConfigurationSnapshot.EMPTY);
        Mockito.when(source.getSnapshot()).thenReturn(ConfigurationSnapshot.EMPTY);
        Mockito.when(source.getProperties()).thenReturn(Collections.emptyMap());

        long poiStart = System.nanoTime();
        Thread.sleep(10L);
        DefaultConfigurationSnapshot fc = new DefaultConfigurationSnapshot(source);
        Thread.sleep(10L);

        long poiEnd = System.nanoTime();

        assertTrue(fc.getTimestamp()>poiStart);
        assertTrue(fc.getTimestamp()<poiEnd);
    }


    @Test
    public void idMustBeNotNull() {
        Configuration source = mock(Configuration.class);

        Mockito.when(source.getContext()).thenReturn(ConfigurationContext.EMPTY);
        Mockito.when(source.getProperties()).thenReturn(Collections.emptyMap());

        DefaultConfigurationSnapshot fc = new DefaultConfigurationSnapshot(source);

        assertNotNull(fc);
    }

    /*
     * All tests for equals() and hashCode() go here...
     */
    @Test
    public void twoFrozenAreDifferentIfTheyHaveADifferentIdAndFrozenAtTimestamp() {
        Map<String, String> properties = new HashMap<>();
        properties.put("key", "createValue");

        Configuration configuration = mock(Configuration.class);
        Mockito.when(configuration.getContext()).thenReturn(ConfigurationContext.EMPTY);
        doReturn(properties).when(configuration).getProperties();

        DefaultConfigurationSnapshot fcA = new DefaultConfigurationSnapshot(configuration);
        DefaultConfigurationSnapshot fcB = new DefaultConfigurationSnapshot(configuration);

        assertNotEquals(fcA, fcB);
    }

    /*
     * END OF ALL TESTS for equals() and hashCode()
     */

    @Test
    public void testGetContext() {
        Configuration config = Configuration.current();
        DefaultConfigurationSnapshot snapshot = new DefaultConfigurationSnapshot(config,
                Arrays.asList("confkey1", "confkey2", "confkey3"));
        assertEquals(config.getContext().getPropertySources().size(),snapshot.getContext().getPropertySources().size());
        assertEquals(config.getContext().getPropertyConverters().size(),snapshot.getContext().getPropertyConverters().size());
        assertEquals(config.getContext().getPropertyFilters().size(),snapshot.getContext().getPropertyFilters().size());
    }

    @Test
    public void testGet() {
        Configuration config = Configuration.current();
        DefaultConfigurationSnapshot snapshot = new DefaultConfigurationSnapshot(config,
                Arrays.asList("confkey1", "confkey2", "confkey3"));
        assertEquals("javaconf-value1", snapshot.get("confkey1"));
        assertEquals("javaconf-value2", snapshot.get("confkey2"));
        assertEquals("javaconf-value3", snapshot.get("confkey3"));
        assertNull(snapshot.getOrDefault("confkey4", null));
        assertEquals("javaconf-value1", snapshot.get("confkey1", String.class));
        assertEquals("javaconf-value2", snapshot.get("confkey2", String.class));
        assertEquals("javaconf-value3", snapshot.get("confkey3", String.class));
        assertNull(snapshot.getOrDefault("confkey4", String.class, null));
        assertEquals("javaconf-value1", snapshot.get("confkey1", TypeLiteral.of(String.class)));
        assertEquals("javaconf-value2", snapshot.get("confkey2", TypeLiteral.of(String.class)));
        assertEquals("javaconf-value3", snapshot.get("confkey3", TypeLiteral.of(String.class)));
        assertNull(snapshot.getOrDefault("confkey4", TypeLiteral.of(String.class), null));
    }

    @Test
    public void testGet_with_Subset() {
        Configuration config = Configuration.current();
        ConfigurationSnapshot snapshot = new DefaultConfigurationSnapshot(config,
                Arrays.asList("confkey1", "confkey2", "confkey3"));
        snapshot = snapshot.getSnapshot(Arrays.asList("confkey1"));
        assertEquals("javaconf-value1", snapshot.get("confkey1"));
        assertNull(snapshot.getOrDefault("confkey2", null));
        assertEquals("javaconf-value1", snapshot.get("confkey1", String.class));
        assertNull(snapshot.getOrDefault("confkey2", String.class, null));
        assertEquals("javaconf-value1", snapshot.get("confkey1", TypeLiteral.of(String.class)));
        assertNull(snapshot.getOrDefault("confkey2", TypeLiteral.of(String.class), null));
    }

    @Test
    public void testGet_with_MultiKey() {
        Configuration config = Configuration.current();
        DefaultConfigurationSnapshot snapshot = new DefaultConfigurationSnapshot(config,
                Arrays.asList("confkey1", "confkey2", "confkey3"));
        assertEquals("javaconf-value1", snapshot.get(Arrays.asList("confkey1", "foo")));
        assertEquals("javaconf-value2", snapshot.get(Arrays.asList("foo", "confkey2")));
        assertEquals("javaconf-value1", snapshot.get(Arrays.asList("confkey1", "foo"), String.class));
        assertEquals("javaconf-value2", snapshot.get(Arrays.asList("foo", "confkey2"), String.class));
    }

    @Test
    public void testGetOrDefault() {
        Configuration config = Configuration.current();
        DefaultConfigurationSnapshot snapshot = new DefaultConfigurationSnapshot(config,
                Arrays.asList("confkey1", "confkey2", "confkey3"));
        assertEquals("javaconf-value1", snapshot.getOrDefault("confkey1", "foo"));
        assertEquals("javaconf-value2", snapshot.getOrDefault("confkey2", "foo"));
        assertEquals("javaconf-value2", snapshot.getOrDefault("confkey2", String.class,"foo"));
        assertEquals("javaconf-value3", snapshot.getOrDefault("confkey3", "foo"));
        assertEquals("foo", snapshot.getOrDefault("confkey4", "foo"));
        assertEquals("foo", snapshot.getOrDefault("confkey4", String.class,"foo"));
    }

    @Test
    public void testGetProperties() {
        Configuration config = Configuration.current();
        DefaultConfigurationSnapshot snapshot = new DefaultConfigurationSnapshot(config,
                Arrays.asList("confkey1", "confkey2", "confkey3", "missing"));
        Map<String, String> properties = snapshot.getProperties();
        assertNotNull(properties);
        assertEquals(3, properties.size());
        assertEquals("javaconf-value1", properties.get("confkey1"));
        assertEquals("javaconf-value2", properties.get("confkey2"));
        assertEquals("javaconf-value3", properties.get("confkey3"));
        assertEquals(null, properties.get("confkey4"));
    }

    @Test
    public void testGetId() {
        Configuration config = Configuration.current();
        ConfigurationSnapshot snapshot1 = new DefaultConfigurationSnapshot(config,
                Arrays.asList("confkey1", "confkey2", "confkey3", "foo"));
        ConfigurationSnapshot snapshot2 = new DefaultConfigurationSnapshot(config,
                Arrays.asList("confkey1", "confkey2", "confkey3", "foo"));
        assertNotEquals(snapshot1, snapshot2);
        assertNotEquals(((DefaultConfigurationSnapshot) snapshot1).getId(),
                ((DefaultConfigurationSnapshot) snapshot2).getId());
    }

    @Test
    public void testGetOrDefault_withSubset() {
        Configuration config = Configuration.current();
        ConfigurationSnapshot snapshot = new DefaultConfigurationSnapshot(config,
                Arrays.asList("confkey1", "confkey2", "confkey3"));
        snapshot = snapshot.getSnapshot(Arrays.asList("confkey1"));
        assertEquals("javaconf-value1", snapshot.getOrDefault("confkey1", "foo"));
        assertEquals("foo", snapshot.getOrDefault("confkey2", "foo"));
        assertEquals("javaconf-value1", snapshot.getOrDefault("confkey1", String.class,"foo"));
        assertEquals("foo", snapshot.getOrDefault("confkey2", String.class, "foo"));
    }

    @Test
    public void testGetOrDefault_with_MultiKey() {
        Configuration config = Configuration.current();
        DefaultConfigurationSnapshot snapshot = new DefaultConfigurationSnapshot(config,
                Arrays.asList("confkey1", "confkey2", "confkey3"));
        assertEquals("javaconf-value1", snapshot.getOrDefault(Arrays.asList("confkey1", "foo"), "foo"));
        assertEquals("foo", snapshot.getOrDefault(Arrays.asList("confkeyNone", "foo"), "foo"));
        assertEquals("javaconf-value1", snapshot.getOrDefault(Arrays.asList("confkey1", "foo"), String.class, "foo"));
        assertEquals("foo", snapshot.getOrDefault(Arrays.asList("confkeyNone", "foo"), String.class, "foo"));
    }

    @Test
    public void testGetKeys() {
        Configuration config = Configuration.current();
        DefaultConfigurationSnapshot snapshot = new DefaultConfigurationSnapshot(config,
                Arrays.asList("confkey1", "confkey2", "confkey3"));
        assertTrue(snapshot.getKeys().contains("confkey1"));
        assertTrue(snapshot.getKeys().contains("confkey2"));
        assertTrue(snapshot.getKeys().contains("confkey3"));
        assertFalse(snapshot.getKeys().contains("confkey4"));
        assertFalse(snapshot.getKeys().contains("foo"));
    }

    @Test
    public void testGetKeys_Subkeys() {
        Configuration config = Configuration.current();
        ConfigurationSnapshot snapshot = new DefaultConfigurationSnapshot(config,
                Arrays.asList("confkey1", "confkey2", "confkey3"));
        assertTrue(snapshot.getKeys().contains("confkey1"));
        assertTrue(snapshot.getKeys().contains("confkey2"));
        assertTrue(snapshot.getKeys().contains("confkey3"));
        assertFalse(snapshot.getKeys().contains("confkey4"));
        assertFalse(snapshot.getKeys().contains("foo"));
        snapshot = snapshot.getSnapshot(Arrays.asList("confkey1", "confkey2"));
        assertTrue(snapshot.getKeys().contains("confkey1"));
        assertTrue(snapshot.getKeys().contains("confkey2"));
        assertFalse(snapshot.getKeys().contains("confkey3"));
        assertFalse(snapshot.getKeys().contains("confkey4"));
        assertFalse(snapshot.getKeys().contains("foo"));
        snapshot = snapshot.getSnapshot(Arrays.asList("confkey1", "foo"));
        assertTrue(snapshot.getKeys().contains("confkey1"));
        assertFalse(snapshot.getKeys().contains("confkey2"));
        assertFalse(snapshot.getKeys().contains("confkey3"));
        assertFalse(snapshot.getKeys().contains("confkey4"));
        assertTrue(snapshot.getKeys().contains("foo"));
    }

}