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
package org.apache.tamaya;

import org.apache.tamaya.spi.ConfigurationContext;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.*;

public class ConfigurationSnapshotTest {

    public void testEMPTY(){
        assertNotNull(ConfigurationSnapshot.EMPTY);
    }

    @Test
    public void testEMPTY_timestamp(){
        assertTrue(ConfigurationSnapshot.EMPTY.getTimestamp()==0);
    }

    @Test
    public void testEMPTY_get(){
        assertNull(ConfigurationSnapshot.EMPTY.get("foo"));
    }

    @Test
    public void testEMPTY_getOptional(){
        assertNotNull(ConfigurationSnapshot.EMPTY.getOptional("foo"));
        assertFalse(ConfigurationSnapshot.EMPTY.getOptional("foo").isPresent());
    }

    @Test
    public void testEMPTY_getOrDefault_noValue(){
        assertNull(ConfigurationSnapshot.EMPTY.getOrDefault("foo", null));
    }

    @Test
    public void testEMPTY_getOrDefault_withValue(){
        assertEquals("foo", ConfigurationSnapshot.EMPTY.getOrDefault("foo", "foo"));
    }

    @Test
    public void testEMPTY_getOptional_Iterable(){
        assertNotNull(ConfigurationSnapshot.EMPTY.getOptional(Collections.singleton("foo")));
        assertFalse(ConfigurationSnapshot.EMPTY.getOptional(Collections.singleton("foo")).isPresent());
    }

    @Test
    public void testEMPTY_getOptional_Class_Iterable(){
        assertNotNull(ConfigurationSnapshot.EMPTY.getOptional(Collections.singleton("foo"), String.class));
        assertFalse(ConfigurationSnapshot.EMPTY.getOptional(Collections.singleton("foo"), String.class).isPresent());
    }

    @Test
    public void testEMPTY_getOptional_Typeliteral_Iterable(){
        assertNotNull(ConfigurationSnapshot.EMPTY.getOptional(Collections.singleton("foo"), TypeLiteral.of(String.class)));
        assertFalse(ConfigurationSnapshot.EMPTY.getOptional(Collections.singleton("foo"), TypeLiteral.of(String.class)).isPresent());
    }

    @Test
    public void testEMPTY_get_Iterable(){
        assertNull(ConfigurationSnapshot.EMPTY.get(Collections.singleton("foo")));
    }

    @Test
    public void testEMPTY_get_Iterable_Class(){
        assertNull(ConfigurationSnapshot.EMPTY.get(Collections.singleton("foo"), String.class));
    }

    @Test
    public void testEMPTY_getOrDefault_Class_withValue(){
        assertEquals("foo", ConfigurationSnapshot.EMPTY.getOrDefault("foo", String.class, "foo"));
    }

    @Test
    public void testEMPTY_getOrDefault_TypeLiteral_withValue(){
        assertEquals("foo", ConfigurationSnapshot.EMPTY.getOrDefault("foo", TypeLiteral.of(String.class), "foo"));
    }

    @Test
    public void testEMPTY_get_Iterable_TypeLiteral(){
        assertNull(ConfigurationSnapshot.EMPTY.get(Collections.singleton("foo"), TypeLiteral.of(String.class)));
    }

    @Test
    public void testEMPTY_get_Classl(){
        assertNull(ConfigurationSnapshot.EMPTY.get("foo", TypeLiteral.of(String.class)));
    }

    @Test
    public void testEMPTY_get_TypeLiteral(){
        assertNull(ConfigurationSnapshot.EMPTY.get("foo", TypeLiteral.of(String.class)));
    }

    @Test
    public void testEMPTY_getKeys(){
        assertNotNull(ConfigurationSnapshot.EMPTY.getKeys());
        assertTrue(ConfigurationSnapshot.EMPTY.getKeys().isEmpty());
    }

    @Test
    public void testEMPTY_getContext(){
        assertEquals(ConfigurationContext.EMPTY, ConfigurationSnapshot.EMPTY.getContext());
    }

    @Test
    public void testEMPTY_getPropertiest(){
        assertNotNull(ConfigurationSnapshot.EMPTY.getProperties());
        assertTrue(ConfigurationSnapshot.EMPTY.getProperties().isEmpty());
    }

    @Test
    public void testEMPTY_toBuildert(){
        assertNotNull(ConfigurationSnapshot.EMPTY.toBuilder());
    }

    @Test
    public void testEMPTY_toStringt(){
        assertNotNull(ConfigurationSnapshot.EMPTY.toString());
    }

    @Test
    public void testEMPTY_getSnapshot(){
        assertEquals(ConfigurationSnapshot.EMPTY, ConfigurationSnapshot.EMPTY.getSnapshot());
    }

    @Test
    public void testEMPTY_getSnapshot_Keys(){
        assertEquals(ConfigurationSnapshot.EMPTY, ConfigurationSnapshot.EMPTY.getSnapshot("foo"));
    }

    @Test
    public void testEMPTY_getSnapshot_Iterable(){
        assertEquals(ConfigurationSnapshot.EMPTY, ConfigurationSnapshot.EMPTY.getSnapshot(Collections.singletonList("foo")));
    }

}