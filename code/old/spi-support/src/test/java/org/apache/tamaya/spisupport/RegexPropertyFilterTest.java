/*
 * Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.apache.tamaya.spisupport;

import org.apache.tamaya.spi.ConfigurationContext;
import org.apache.tamaya.spi.FilterContext;
import org.apache.tamaya.spi.PropertyValue;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Tests for {@link RegexPropertyFilter}. Created by anatole on 11.02.16.
 */
public class RegexPropertyFilterTest {

    private static PropertyValue prop1 = PropertyValue.of("test1", "test1", "test");
    private static PropertyValue prop2 = PropertyValue.of("test2", "test2", "test");
    private static PropertyValue prop3 = PropertyValue.of("test1.test3", "test.test3", "test");
    private static ConfigurationContext configContext = EmptyConfigurationContext.instance();

    @org.junit.Test
    public void testFilterProperty() throws Exception {
        RegexPropertyFilter filter = new RegexPropertyFilter();
        filter.setIncludes("test1.*");
        Map<String,PropertyValue> map = new HashMap<>();
        map.put(prop1.getKey(), prop1);
        map.put(prop2.getKey(), prop2);
        map.put(prop3.getKey(), prop3);
        assertEquals(filter.filterProperty(prop1, new FilterContext(prop1, configContext)), prop1);
        assertNull(filter.filterProperty(prop2, new FilterContext(prop2, configContext)));
        assertEquals(filter.filterProperty(
                prop3,
                new FilterContext(prop3, map, configContext)), prop3);
        assertEquals(filter.filterProperty(
                prop3,
                new FilterContext(prop3, map, configContext)), prop3);
        filter = new RegexPropertyFilter();
        filter.setIncludes("test1.*");
        assertNotNull(filter.filterProperty(prop1, new FilterContext(prop1, map, configContext)));
        assertNull(filter.filterProperty(prop2, new FilterContext(prop2, map, configContext)));
        assertNotNull(filter.filterProperty(prop3, new FilterContext(prop3, map, configContext)));
    }

    @org.junit.Test
    public void testToString() throws Exception {
        RegexPropertyFilter filter = new RegexPropertyFilter();
        filter.setIncludes("test\\..*");
        assertTrue(filter.toString().contains("test\\..*"));
        assertTrue(filter.toString().contains("RegexPropertyFilter"));
    }
}