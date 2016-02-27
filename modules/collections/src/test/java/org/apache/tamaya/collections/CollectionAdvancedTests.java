package org.apache.tamaya.collections;

import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;
import org.apache.tamaya.TypeLiteral;
import org.junit.Test;

import java.util.Currency;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

/**
 * Created by atsticks on 16.02.16.
 */
public class CollectionAdvancedTests {

    /**
     * Tests if a custom separator works, Config is
     * <pre>
     *  sep-list=a,b,c|d,e,f|g,h,i
     *  _sep-list.collection-type=List
     *  _sep-list.collection-separator=|
     * </pre>
     */
    @Test
    public void testCustomSeparator(){
        Configuration config = ConfigurationProvider.getConfiguration();
        List<String> items = config.get("sep-list", new TypeLiteral<List<String>>(){});
        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals(3, items.size());
        assertEquals("a,b,c", items.get(0));
        assertEquals("d,e,f", items.get(1));
        assertEquals("g,h,i", items.get(2));
    }

    /**
     * Test typed content.
     * <pre>
     *  currency-list=CHF,USD,YEN
     *  _currency-list.collection-type=List
     * </pre>
     */
    @Test
    public void testTypedContent(){
        Configuration config = ConfigurationProvider.getConfiguration();
        List<Currency> items = config.get("currency-list", new TypeLiteral<List<Currency>>(){});
        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals(3, items.size());
        assertEquals("CHF", items.get(0).getCurrencyCode());
        assertEquals("USD", items.get(1).getCurrencyCode());
        assertEquals("USS", items.get(2).getCurrencyCode());
    }

    /**
     * Tests if a custom parser works, Config is
     * <pre>
     *  parser-list=a,b,c
     *  _parser-list.collection-type=List
     *  _parser-list.item-converter=org.apache.tamaya.collections.MyUpperCaseConverter
     * </pre>
     */
    @Test
    public void testCustomParser(){
        Configuration config = ConfigurationProvider.getConfiguration();
        List<String> items = config.get("parser-list", new TypeLiteral<List<String>>(){});
        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals(3, items.size());
        assertEquals("(A)", items.get(0));
        assertEquals("(B)", items.get(1));
        assertEquals("(C)", items.get(2));
    }

    /**
     * Redefined map format parsing, Config is as follows:
     * <pre>
     *  redefined-map=0==none | 1==single | 2==any
     *  _redefined-map.map-entry-separator===
     *  _redefined-map.item-separator=|
     * </pre>
     */
    @Test
    public void testCustomMapParser(){
        Configuration config = ConfigurationProvider.getConfiguration();
        Map<String,String> items = config.get("redefined-map", Map.class);
        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals(3, items.size());
        assertEquals("none", items.get("0"));
        assertEquals("single", items.get("1"));
        assertEquals("any", items.get("2"));
    }
}
