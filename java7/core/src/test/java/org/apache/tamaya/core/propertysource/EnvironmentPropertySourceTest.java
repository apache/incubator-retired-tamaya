package org.apache.tamaya.core.propertysource;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EnvironmentPropertySourceTest {

    private EnvironmentPropertySource envPropertySource = new EnvironmentPropertySource();

    @Test
    public void testGetOrdinal() throws Exception {
        assertEquals(EnvironmentPropertySource.DEFAULT_ORDINAL, envPropertySource.getOrdinal());
    }

    @Test
    public void testGetName() throws Exception {
        assertEquals("environment-properties", envPropertySource.getName());
    }

    @Test
    public void testGet() throws Exception {
        for (Map.Entry<String, String> envEntry : System.getenv().entrySet()) {
            assertEquals(envPropertySource.get(envEntry.getKey()), envEntry.getValue());
        }
    }

    @Test
    public void testGetProperties() throws Exception {
        Map<String, String> props = envPropertySource.getProperties();
        assertEquals(System.getenv(), props);
    }

    @Test
    public void testIsScannable() throws Exception {
        assertTrue(envPropertySource.isScannable());
    }
}