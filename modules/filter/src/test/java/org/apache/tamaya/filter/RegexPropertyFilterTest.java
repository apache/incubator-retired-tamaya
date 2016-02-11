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
package org.apache.tamaya.filter;

import org.apache.tamaya.spi.FilterContext;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Tests for {@link RegexPropertyFilter}. Created by anatole on 11.02.16.
 */
public class RegexPropertyFilterTest {

    @org.junit.Test
    public void testFilterProperty() throws Exception {
        RegexPropertyFilter filter = new RegexPropertyFilter("test\\..*");
        Map<String,String> map = new HashMap<>();
        map.put("test1", "test1");
        map.put("test2", "test2");
        map.put("test.test3", "test.test3");
        assertEquals(filter.filterProperty("test1", new FilterContext("test1", map, true)), "test1");
        assertEquals(filter.filterProperty("test2", new FilterContext("test2", map, true)), "test2");
        assertNull(filter.filterProperty("test.test3", new FilterContext("test.test3", map, true)));
        filter = new RegexPropertyFilter(".*");
        assertNull(filter.filterProperty("test1", new FilterContext("test1", map, true)));
        assertNull(filter.filterProperty("test2", new FilterContext("test2", map, true)));
        assertNull(filter.filterProperty("test.test3", new FilterContext("test.test3", map, true)));
    }

    @org.junit.Test
    public void testToString() throws Exception {
        RegexPropertyFilter filter = new RegexPropertyFilter("test\\..*");
        assertTrue(filter.toString().contains("test\\..*"));
        assertTrue(filter.toString().contains("RegexPropertyFilter"));
    }
}