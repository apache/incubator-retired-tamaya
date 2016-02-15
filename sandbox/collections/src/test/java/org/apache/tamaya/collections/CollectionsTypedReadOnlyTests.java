package org.apache.tamaya.collections;

import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;
import org.apache.tamaya.TypeLiteral;
import org.junit.Test;

import java.util.*;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Basic tests for Tamaya collection support. Relevant configs for this tests:
 * <pre>base.items=1,2,3,4,5,6,7,8,9,0
 * base.map=1::a, 2::b, 3::c, [4:: ]
 * </pre>
 */
public class CollectionsTypedReadOnlyTests {

    @Test
    public void testArrayListList_String(){
        Configuration config = ConfigurationProvider.getConfiguration();
        List<String> items = config.get("typed.arraylist", new TypeLiteral<List<String>>(){});
        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals(10, items.size());
        assertTrue(items instanceof ArrayList);
        items = (List<String>) config.get("typed.arraylist", List.class);
        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals(10, items.size());
        assertTrue(items instanceof ArrayList);
    }

    @Test
    public void testLinkedListList_String(){
        Configuration config = ConfigurationProvider.getConfiguration();
        List<String> items = config.get("typed.linkedlist", new TypeLiteral<List<String>>(){});
        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals(10, items.size());
        assertTrue(items instanceof LinkedList);
        items = (List<String>) config.get("typed.linkedlist", List.class);
        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals(10, items.size());
        assertTrue(items instanceof LinkedList);
    }


    @Test
    public void testHashSet_String(){
        Configuration config = ConfigurationProvider.getConfiguration();
        Set<String> items = config.get("typed.hashset", new TypeLiteral<Set<String>>(){});
        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals(10, items.size());
        assertTrue(items instanceof HashSet);
        items = (Set<String>) config.get("typed.hashset", Set.class);
        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals(10, items.size());
        assertTrue(items instanceof HashSet);
    }

    @Test
    public void testTreeSet_String(){
        Configuration config = ConfigurationProvider.getConfiguration();
        Set<String> items = config.get("typed.treeset", new TypeLiteral<Set<String>>(){});
        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals(10, items.size());
        assertTrue(items instanceof TreeSet);
        items = (Set<String>) config.get("typed.treeset", Set.class);
        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals(10, items.size());
        assertTrue(items instanceof TreeSet);
    }

    @Test
    public void testHashMap_String(){
        Configuration config = ConfigurationProvider.getConfiguration();
        Map<String,String> items = config.get("typed.hashmap", new TypeLiteral<Map<String,String>>(){});
        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals(4, items.size());
        assertEquals("a", items.get("1"));
        assertEquals("b", items.get("2"));
        assertEquals("c", items.get("3"));
        assertEquals(" ", items.get("4"));
        assertTrue(items instanceof HashMap);
        items = (Map<String,String>) config.get("typed.hashmap", Map.class);
        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals(4, items.size());
        assertEquals("a", items.get("1"));
        assertEquals("b", items.get("2"));
        assertEquals("c", items.get("3"));
        assertEquals(" ", items.get("4"));
        assertTrue(items instanceof HashMap);
    }

    @Test
    public void testTreeMap_String(){
        Configuration config = ConfigurationProvider.getConfiguration();
        Map<String,String> items = config.get("typed.treemap", new TypeLiteral<Map<String,String>>(){});
        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals(4, items.size());
        assertEquals("a", items.get("1"));
        assertEquals("b", items.get("2"));
        assertEquals("c", items.get("3"));
        assertEquals(" ", items.get("4"));
        assertTrue(items instanceof TreeMap);
        items = (Map<String,String>) config.get("typed.treemap", Map.class);
        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals(4, items.size());
        assertEquals("a", items.get("1"));
        assertEquals("b", items.get("2"));
        assertEquals("c", items.get("3"));
        assertEquals(" ", items.get("4"));
        assertTrue(items instanceof TreeMap);
    }


    @Test
    public void testCollection_TreeMap(){
        Configuration config = ConfigurationProvider.getConfiguration();
        Collection<String> items = config.get("typed.treemap", new TypeLiteral<Collection<String>>(){});
        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals(10, items.size());
        assertTrue(items instanceof TreeMap);
        items = (Collection<String>) config.get("typed.treemap", Collection.class);
        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals(10, items.size());
        assertTrue(items instanceof TreeMap);
    }

    @Test
    public void testCollection_HashMap(){
        Configuration config = ConfigurationProvider.getConfiguration();
        Collection<String> items = config.get("typed.hashmap", new TypeLiteral<Collection<String>>(){});
        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals(10, items.size());
        assertTrue(items instanceof HashMap);
        items = (Collection<String>) config.get("typed.hashmap", Collection.class);
        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals(10, items.size());
        assertTrue(items instanceof HashMap);
    }

    @Test
    public void testCollection_HashSet(){
        Configuration config = ConfigurationProvider.getConfiguration();
        Collection<String> items = config.get("typed.hashset", new TypeLiteral<Collection<String>>(){});
        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals(10, items.size());
        assertTrue(items instanceof HashSet);
        items = (Collection<String>) config.get("typed.hashset", Collection.class);
        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals(10, items.size());
        assertTrue(items instanceof HashSet);
    }

    @Test
    public void testCollection_TreeSet(){
        Configuration config = ConfigurationProvider.getConfiguration();
        Collection<String> items = config.get("typed.treeset", new TypeLiteral<Collection<String>>(){});
        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals(10, items.size());
        assertTrue(items instanceof TreeSet);
        items = (Collection<String>) config.get("typed.treeset", Collection.class);
        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals(10, items.size());
        assertTrue(items instanceof TreeSet);
    }

    @Test
    public void testCollection_ArrayList(){
        Configuration config = ConfigurationProvider.getConfiguration();
        Collection<String> items = config.get("typed.arraylist", new TypeLiteral<Collection<String>>(){});
        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals(10, items.size());
        assertTrue(items instanceof ArrayList);
        items = (Collection<String>) config.get("typed.arraylist", Collection.class);
        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals(10, items.size());
        assertTrue(items instanceof ArrayList);
    }

    @Test
    public void testCollection_LinkedList(){
        Configuration config = ConfigurationProvider.getConfiguration();
        Collection<String> items = config.get("typed.linkedlist", new TypeLiteral<Collection<String>>(){});
        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals(10, items.size());
        assertTrue(items instanceof LinkedList);
        items = (Collection<String>) config.get("typed.linkedlist", Collection.class);
        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals(10, items.size());
        assertTrue(items instanceof LinkedList);
    }
}
