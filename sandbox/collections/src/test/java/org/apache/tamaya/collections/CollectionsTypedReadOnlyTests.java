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
package org.apache.tamaya.collections;

import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;
import org.apache.tamaya.TypeLiteral;
import org.junit.Test;

import java.util.*;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.*;

/**
 * Basic tests for Tamaya collection support. Relevant configs for this tests:
 * <pre>base.items=1,2,3,4,5,6,7,8,9,0
 * base.map=1::a, 2::b, 3::c, [4:: ]
 * </pre>
 */
public class CollectionsTypedReadOnlyTests {

    @Test(expected=UnsupportedOperationException.class)
    public void testArrayListList_1(){
        Configuration config = ConfigurationProvider.getConfiguration();
        List<String> items = config.get("typed.arraylist", new TypeLiteral<List<String>>(){});
        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals(10, items.size());
        items.add("test");
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testArrayListList_2(){
        Configuration config = ConfigurationProvider.getConfiguration();
        List<String> items = (List<String>) config.get("typed.arraylist", List.class);
        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals(10, items.size());
        items.add("test");
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testLinkedListList_1(){
        Configuration config = ConfigurationProvider.getConfiguration();
        List<String> items = config.get("typed.linkedlist", new TypeLiteral<List<String>>(){});
        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals(10, items.size());
        items.add("test");
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testLinkedListList_2(){
        Configuration config = ConfigurationProvider.getConfiguration();
        List<String> items = (List<String>) config.get("typed.linkedlist", List.class);
        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals(10, items.size());
        items.add("test");
    }


    @Test(expected=UnsupportedOperationException.class)
    public void testHashSet_1(){
        Configuration config = ConfigurationProvider.getConfiguration();
        Set<String> items = config.get("typed.hashset", new TypeLiteral<Set<String>>(){});
        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals(10, items.size());
        items.add("test");
    }
    @Test(expected=UnsupportedOperationException.class)
    public void testHashSet_2(){
        Configuration config = ConfigurationProvider.getConfiguration();
        Set<String> items = (Set<String>) config.get("typed.hashset", Set.class);
        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals(10, items.size());
        items.add("test");
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testTreeSet_1(){
        Configuration config = ConfigurationProvider.getConfiguration();
        Set<String> items = config.get("typed.treeset", new TypeLiteral<Set<String>>(){});
        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals(10, items.size());
        items.add("test");
    }
    @Test(expected=UnsupportedOperationException.class)
    public void testTreeSet_2(){
        Configuration config = ConfigurationProvider.getConfiguration();
        Set<String> items = items = (Set<String>) config.get("typed.treeset", Set.class);
        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals(10, items.size());
        items.add("test");
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testHashMap_1(){
        Configuration config = ConfigurationProvider.getConfiguration();
        Map<String,String> items = config.get("typed.hashmap", new TypeLiteral<Map<String,String>>(){});
        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals(4, items.size());
        assertEquals("a", items.get("1"));
        assertEquals("b", items.get("2"));
        assertEquals("c", items.get("3"));
        assertEquals(" ", items.get("4"));
        items.put("g","hjhhj");
    }
    @Test(expected=UnsupportedOperationException.class)
    public void testHashMap_2(){
        Configuration config = ConfigurationProvider.getConfiguration();
        Map<String,String> items = (Map<String,String>) config.get("typed.hashmap", Map.class);
        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals(4, items.size());
        assertEquals("a", items.get("1"));
        assertEquals("b", items.get("2"));
        assertEquals("c", items.get("3"));
        assertEquals(" ", items.get("4"));
        items.put("g","hjhhj");
    }


    @Test(expected=UnsupportedOperationException.class)
    public void testTreeMap_1(){
        Configuration config = ConfigurationProvider.getConfiguration();
        Map<String,String> items = config.get("typed.treemap", new TypeLiteral<Map<String,String>>(){});
        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals(4, items.size());
        assertEquals("a", items.get("1"));
        assertEquals("b", items.get("2"));
        assertEquals("c", items.get("3"));
        assertEquals(" ", items.get("4"));
        items.put("g","hjhhj");
    }
    @Test(expected=UnsupportedOperationException.class)
    public void testTreeMap_2(){
        Configuration config = ConfigurationProvider.getConfiguration();
        Map<String,String> items = (Map<String,String>) config.get("typed.treemap", Map.class);
        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals(4, items.size());
        assertEquals("a", items.get("1"));
        assertEquals("b", items.get("2"));
        assertEquals("c", items.get("3"));
        assertEquals(" ", items.get("4"));
        items.put("g","hjhhj");
    }

}
