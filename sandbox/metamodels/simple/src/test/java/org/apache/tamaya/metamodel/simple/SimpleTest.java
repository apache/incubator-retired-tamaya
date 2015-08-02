package org.apache.tamaya.metamodel.simple;

import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Anatole on 26.07.2015.
 */
public class SimpleTest {

    @Test
    public void testClasspathConfig() {
        Configuration config = ConfigurationProvider.getConfiguration();
        assertEquals(config.get("test1"), "1");
        assertEquals(config.get("test2"), "2");
        // overridden by file config
        assertEquals(config.get("test3"), "3-overridden");
        assertEquals(config.get("test4"), "4");
        // added by file config
        assertEquals(config.get("test5"), "value5");
    }

}
