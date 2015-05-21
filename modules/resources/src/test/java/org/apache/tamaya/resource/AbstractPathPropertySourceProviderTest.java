package org.apache.tamaya.resource;

import org.apache.tamaya.spi.PropertySource;
import org.junit.Test;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class AbstractPathPropertySourceProviderTest {

    private AbstractPathPropertySourceProvider myProvider = new AbstractPathPropertySourceProvider("*.properties") {
        @Override
        protected Collection<PropertySource> getPropertySources(URL url) {
            List<PropertySource> result = new ArrayList<>();
            result.add(new PropertySource() {
                @Override
                public String getName() {
                    return "<empty>";
                }

                @Override
                public Map<String, String> getProperties() {
                    return Collections.emptyMap();
                }
            });
            return result;
        }
    };

    @Test
    public void testGetPropertySources() throws Exception {
        assertNotNull(myProvider.getPropertySources());
    }

    @Test
    public void testCreatePropertiesPropertySource() throws Exception {
        PropertySource ps = AbstractPathPropertySourceProvider.createPropertiesPropertySource(
                ClassLoader.getSystemClassLoader().getResource("test.properties")
        );
        assertNotNull(ps);
        assertTrue(ps.getProperties().isEmpty());
    }
}