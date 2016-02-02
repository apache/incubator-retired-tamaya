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
package org.apache.tamaya.spi;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by atsticks on 02.02.16.
 */
public class PropertyValueBuilderTest {

    @Test
    public void testKey() throws Exception {
        PropertyValueBuilder b = new PropertyValueBuilder("k", "v", "testKey");
        PropertyValue val = b.build();
        assertEquals(val.getKey(),"k");
    }

    @Test
    public void testValue() throws Exception {
        PropertyValueBuilder b = new PropertyValueBuilder("k", "v", "testValue");
        PropertyValue val = b.build();
        assertEquals(val.getValue(),"v");
        assertEquals(val.getConfigEntries().get("k"),"v");
    }

    @Test(expected=NullPointerException.class)
    public void testKeyNullValue() throws Exception {
        new PropertyValueBuilder("k", null, "testKeyNullValue");
    }

    @Test
    public void testSetContextData() throws Exception {
        PropertyValueBuilder b = new PropertyValueBuilder("k", "v", "testSetContextData");
        Map<String,String> context = new HashMap<>();
        context.put("source", "testSetContextData");
        context.put("ts", String.valueOf(System.currentTimeMillis()));
        context.put("y", "yValue");
        b.setContextData(new HashMap<String, String>());
        b.setContextData(context);
        context.remove("y");
        b.setContextData(context);
        PropertyValue contextData = b.build();
        assertEquals(contextData.getConfigEntries().size(), context.size()+1);
        assertEquals(contextData.get("_k.source"), "testSetContextData");
        assertNotNull(contextData.get("_k.ts"));
        assertNull(contextData.get("_k.y"));
    }

    @Test
    public void testAddContextData() throws Exception {
        PropertyValueBuilder b = new PropertyValueBuilder("k", "v", "testAddContextData");
        b.addContextData("ts", System.currentTimeMillis());
        b.addContextData("y", "yValue");
        b.addContextData("y", "y2");
        PropertyValue contextData = b.build();
        assertEquals(contextData.getConfigEntries().size(), 4);
        assertEquals(contextData.get("_k.source"), "testAddContextData");
        assertNotNull(contextData.get("_k.ts"));
        assertEquals(contextData.get("_k.y"), "y2");
    }

}