package org.apache.tamaya.metamodel.simple;

import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Anatole on 26.07.2015.
 */
public class SimpleTest {

    @Test
    public void testClasspathConfig() {
        Configuration config = ConfigurationProvider.getConfiguration();
        assertEquals(config.get("test1"), "1");
        assertEquals(config.get("test2"), "2");
        assertEquals(config.get("test3"), "3");
        assertEquals(config.get("test4"), "4");
    }

}
