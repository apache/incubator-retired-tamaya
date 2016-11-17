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
 * Tests for {@link FilterContext}.
 */
public class FilterContextTest {
    @Test
    public void getKey() throws Exception {
        FilterContext ctx = new FilterContext("getKey",
                new HashMap<String,String>(), true);
        assertEquals("getKey", ctx.getKey());
    }

    @Test
    public void isSinglePropertyScoped() throws Exception {
        FilterContext ctx = new FilterContext("isSinglePropertyScoped",
                new HashMap<String,String>(), true);
        assertEquals(true, ctx.isSinglePropertyScoped());
        ctx = new FilterContext("isSinglePropertyScoped",
                new HashMap<String,String>(), false);
        assertEquals(false, ctx.isSinglePropertyScoped());
    }

    @Test
    public void getConfigEntries() throws Exception {
        Map<String,String> config = new HashMap<>();
        for(int i=0;i<10;i++) {
            config.put("key-"+i, "value-"+i);
        }
        FilterContext ctx = new FilterContext("getConfigEntries",
                config, true);
        assertEquals(config, ctx.getConfigEntries());
        assertTrue(config != ctx.getConfigEntries());
    }

    @Test
    public void testToString() throws Exception {
        Map<String,String> config = new HashMap<>();
        for(int i=0;i<2;i++) {
            config.put("key-"+i, "value-"+i);
        }
        FilterContext ctx = new FilterContext("testToString",
                config, true);
        String toString = ctx.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("FilterContext{key='testToString', configEntries={"));
        assertTrue(toString.contains("key-0=value-0"));
        assertTrue(toString.contains("key-1=value-1"));
        assertTrue(toString.endsWith("}"));
    }

}