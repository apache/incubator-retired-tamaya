package org.apache.tamaya.collections;

import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;
import org.apache.tamaya.TypeLiteral;
import org.junit.Test;

import java.util.*;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

/**
 * Basic tests for Tamaya collection support. Relevant configs for this tests:
 * <pre>base.items=1,2,3,4,5,6,7,8,9,0
 * base.map=1::a, 2::b, 3::c, [4:: ]
 * </pre>
 */
public class CollectionsTypedTests {

    @Test
    public void testList_String(){
        Configuration config = ConfigurationProvider.getConfiguration();
        List<String> items = config.get("base.items", new TypeLiteral<List<String>>(){});
        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals(10, items.size());
        items = (List<String>) config.get("base.items", List.class);
        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals(10, items.size());
    }

    @Test
    public void testArrayList_String(){
        Configuration config = ConfigurationProvider.getConfiguration();
        ArrayList<String> items = config.get("base.items", new TypeLiteral<ArrayList<String>>(){});
        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals(10, items.size());
        items = (ArrayList<String>) config.get("base.items", ArrayList.class);
        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals(10, items.size());
    }

    @Test
    public void testLinkedList_String(){
        Configuration config = ConfigurationProvider.getConfiguration();
        LinkedList<String> items = config.get("base.items", new TypeLiteral<LinkedList<String>>(){});
        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals(10, items.size());
        items = (LinkedList<String>) config.get("base.items", LinkedList.class);
        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals(10, items.size());
    }

    @Test
    public void testSet_String(){
        Configuration config = ConfigurationProvider.getConfiguration();
        Set<String> items = config.get("base.items", new TypeLiteral<Set<String>>(){});
        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals(10, items.size());
        items = (Set<String>) config.get("base.items", Set.class);
        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals(10, items.size());
    }

    @Test
    public void testSortedSet_String(){
        Configuration config = ConfigurationProvider.getConfiguration();
        Set<String> items = config.get("base.items", new TypeLiteral<SortedSet<String>>(){});
        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals(10, items.size());
        items = (SortedSet<String>) config.get("base.items", SortedSet.class);
        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals(10, items.size());
    }

    @Test
    public void testHashSet_String(){
        Configuration config = ConfigurationProvider.getConfiguration();
        Set<String> items = config.get("base.items", new TypeLiteral<HashSet<String>>(){});
        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals(10, items.size());
        items = (HashSet<String>) config.get("base.items", HashSet.class);
        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals(10, items.size());
    }

    @Test
    public void testTreeSet_String(){
        Configuration config = ConfigurationProvider.getConfiguration();
        TreeSet<String> items = config.get("base.items", new TypeLiteral<TreeSet<String>>(){});
        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals(10, items.size());
        items = (TreeSet<String>) config.get("base.items", TreeSet.class);
        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals(10, items.size());
    }

    @Test
    public void testMap_String(){
        Configuration config = ConfigurationProvider.getConfiguration();
        Map<String,String> items = config.get("base.map", new TypeLiteral<Map<String,String>>(){});
        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals(4, items.size());
        assertEquals("a", items.get("1"));
        assertEquals("b", items.get("2"));
        assertEquals("c", items.get("3"));
        assertEquals(" ", items.get("4"));
        items = (Map<String,String>) config.get("base.map", Map.class);
        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals(4, items.size());
        assertEquals("a", items.get("1"));
        assertEquals("b", items.get("2"));
        assertEquals("c", items.get("3"));
        assertEquals(" ", items.get("4"));
    }

    @Test
    public void testHashMap_String(){
        Configuration config = ConfigurationProvider.getConfiguration();
        Map<String,String> items = config.get("base.map", new TypeLiteral<HashMap<String,String>>(){});
        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals(4, items.size());
        assertEquals("a", items.get("1"));
        assertEquals("b", items.get("2"));
        assertEquals("c", items.get("3"));
        assertEquals(" ", items.get("4"));
        items = (HashMap<String,String>) config.get("base.map", HashMap.class);
        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals(4, items.size());
        assertEquals("a", items.get("1"));
        assertEquals("b", items.get("2"));
        assertEquals("c", items.get("3"));
        assertEquals(" ", items.get("4"));
    }

    @Test
    public void testSortedMap_String(){
        Configuration config = ConfigurationProvider.getConfiguration();
        Map<String,String> items = config.get("base.map", new TypeLiteral<SortedMap<String,String>>(){});
        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals(4, items.size());
        assertEquals("a", items.get("1"));
        assertEquals("b", items.get("2"));
        assertEquals("c", items.get("3"));
        assertEquals(" ", items.get("4"));
        items = (Map<String,String>) config.get("base.map", SortedMap.class);
        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals(4, items.size());
        assertEquals("a", items.get("1"));
        assertEquals("b", items.get("2"));
        assertEquals("c", items.get("3"));
        assertEquals(" ", items.get("4"));
    }

    @Test
    public void testTreeMap_String(){
        Configuration config = ConfigurationProvider.getConfiguration();
        TreeMap<String,String> items = config.get("base.map", new TypeLiteral<TreeMap<String,String>>(){});
        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals(4, items.size());
        assertEquals("a", items.get("1"));
        assertEquals("b", items.get("2"));
        assertEquals("c", items.get("3"));
        assertEquals(" ", items.get("4"));
        items =  config.get("base.map", TreeMap.class);
        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals(4, items.size());
        assertEquals("a", items.get("1"));
        assertEquals("b", items.get("2"));
        assertEquals("c", items.get("3"));
        assertEquals(" ", items.get("4"));
    }

    @Test
    public void testCollection_String(){
        Configuration config = ConfigurationProvider.getConfiguration();
        Collection<String> items = config.get("base.items", new TypeLiteral<Collection<String>>(){});
        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals(10, items.size());
        items = (Collection<String>) config.get("base.items", Collection.class);
        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals(10, items.size());
    }
}
