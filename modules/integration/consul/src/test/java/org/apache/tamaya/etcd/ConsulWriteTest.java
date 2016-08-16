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

import com.google.common.net.HostAndPort;
import org.apache.tamaya.consul.ConsulPropertySource;
import org.junit.BeforeClass;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Tests for th consul backend integration for writing to the consul backend.
 */
public class ConsulWriteTest {

    private static HostAndPort accessor;
    static boolean execute = false;
    private static ConsulPropertySource propertySource;

    @BeforeClass
    public static void setup() throws MalformedURLException, URISyntaxException {
        System.setProperty("consul.urls", "http://127.0.0.1:8300");
        accessor = HostAndPort.fromString("127.0.0.1:8500");
        propertySource = new ConsulPropertySource();
    }

    @org.junit.Test
    public void testSetNormal() throws Exception {
        if (!execute) return;
        String taID = UUID.randomUUID().toString();
        propertySource.put(taID, "testSetNormal", taID.toString());
        propertySource.commitTransaction(taID);
    }


    @org.junit.Test
    public void testDelete() throws Exception {
        if(!execute)return;
        String taID = UUID.randomUUID().toString();
        propertySource.put(taID, "testDelete", taID.toString());
        propertySource.commitTransaction(taID);
        assertEquals(propertySource.get("testDelete").getValue(), taID.toString());
        assertNotNull(propertySource.get("_testDelete.createdIndex"));
        propertySource.remove(taID, "testDelete");
        propertySource.commitTransaction(taID);
        assertNull(propertySource.get("testDelete"));
    }

    @org.junit.Test
    public void testGetProperties() throws Exception {
        if(!execute)return;
        Map<String,String> result = propertySource.getProperties();
        assertTrue(result.isEmpty());
    }
}