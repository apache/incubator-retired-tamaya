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
package org.apache.tamaya.etcd;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * Created by atsticks on 07.01.16.
 */
public class EtcdPropertySourceTest {

    private final EtcdPropertySource propertySource = new EtcdPropertySource();

    @BeforeClass
    public static void setup(){
        System.setProperty("etcd.server.urls", "http://8.8.8.8:4001,http://192.168.99.105:4001");
    }

    @Test
    public void testGetOrdinal() throws Exception {
        assertEquals(propertySource.getOrdinal(), 100);
    }

    @Test
    public void testGetDefaultOrdinal() throws Exception {
        assertEquals(propertySource.getDefaultOrdinal(), 100);
    }

    @Test
    public void testGetName() throws Exception {
        assertEquals("etcd", propertySource.getName());
    }

    @Test
    public void testGet() throws Exception {
        Map<String,String> props = propertySource.getProperties();
        for(Map.Entry<String,String> en:props.entrySet()){
            assertNotNull("Key not found: " + en.getKey(), propertySource.get(en.getKey()));
        }
    }

    @Test
    public void testGetProperties() throws Exception {
        Map<String,String> props = propertySource.getProperties();
        assertNotNull(props);
    }

    @Test
    public void testIsScannable() throws Exception {
        assertTrue(propertySource.isScannable());
    }
}