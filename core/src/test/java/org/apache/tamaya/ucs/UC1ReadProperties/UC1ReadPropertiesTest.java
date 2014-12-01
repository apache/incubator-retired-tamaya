/**
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
package org.apache.tamaya.ucs.UC1ReadProperties;

import org.apache.tamaya.*;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

/**
 * Configuration is organized as key/value pairs. This basically can be modeled as {@code Map<String,String>}
 * Configuration should be as simple as possible. A {@code Map<String,String>} instance has methods that may not
 * be used in many use cases and/or are not easy to implement. Currently the following functionality
 * must be supported:
 * <ul>
 * <li>access a value by key (+get+)</li>
 * <li>check if a value is present (+containsKey+)</li>
 * <li>get a set of all defined keys (+keySet+)</li>
 * <li>a property provider must be convertible to a +Map+, by calling +toMap()+</li>
 * <li>a property provider must get access to its meta information.</li>
 * </ul>
 * Additionally there are other requirement important for ease of use:
 * <ul>
 * <li>The API must never return null.</li>
 * <li>The API should support undefined values.</li>
 * <li>The API must support passing default values, to be returned if a value is undefined.</li>
 * <li>The API must allow to throw exceptions, when a value is undefined.
 * Customized exceptions hereby should be supported.</li>
 * <li>Properties can be stored in the classpath, on a file.</li>
 * <li>Properties can be stored as properties, xml-properties or as ini-files.</li>
 * <li>Properties can also be provided as properties, or as a Map<String,String></li>
 * </ul>
 */
public class UC1ReadPropertiesTest {

    @Test
    public void example() {
        Configuration config = PropertyProviders.fromPaths("classpath:ucs/UC1ReadProperties/UC1ReadPropertiesTest.properties").toConfiguration();
        String name = config.get("name").orElse("Anatole");
        BigDecimal bigNum = config.get("num.BD", BigDecimal.class).orElseThrow(() -> new IllegalStateException("Sorry"));
        double anotherNum = config.getDouble("num.Double").getAsDouble();
        long longNum = config.getLong("num.Long").orElse(288900L);

        // or more simpler use area function
        Configuration areaConfig2 = config.with(ConfigFunctions.selectArea("num"));
        System.out.println(areaConfig2);

        // iterator over an area, using streams only
        Map<String, String> areaMap = config.toMap().entrySet().stream()
                .filter((e) -> e.getKey().startsWith("num."))
                .collect(Collectors.toMap((e) -> e.getKey().substring("num.".length()), (e) -> e.getValue()));
        Configuration areaConfig = PropertyProviders.fromMap(areaMap).toConfiguration();
        System.out.println(areaConfig);
    }

    @Test
    public void getConfigurationTest() {
        PropertyProvider provider = PropertyProviders.fromPaths("classpath:barFoo.properties");
        Configuration config = provider.toConfiguration();
        assertNotNull(config);
        assertTrue(config.isEmpty());
        assertTrue(config.keySet().isEmpty());
        assertFalse(config.isMutable());
    }

    @Test
    public void readBadPropertiesTest() {
        PropertyProvider provider = PropertyProviders.fromPaths("classpath:barFoo.properties");
        assertNotNull(provider);
        assertTrue(provider.isEmpty());
        assertTrue(provider.keySet().isEmpty());
        assertFalse(provider.isMutable());
    }

    @Test
    public void readPropertiesTest() {
        PropertyProvider provider = PropertyProviders.fromPaths("classpath:ucs/UC1ReadProperties/UC1ReadPropertiesTest.properties");
        assertNotNull(provider);
        assertEquals(provider.get("a").get(), "aValue");
        assertEquals(provider.get("b").get(), "bValue");
        assertEquals(provider.get("c").get(), "cValue");
        assertEquals(provider.get("a.b.c").get(), "abcValue");
        assertEquals(provider.get("a.b.a").get(), "abaValue");
        assertEquals(provider.get("a.b.b").get(), "abbValue");
        assertEquals(provider.get("a.b").get(), "abValue");
        assertEquals(provider.get("a.a.a").get(), "aaaValue");
        assertEquals(provider.get("b.b.b").get(), "bbbValue");
        assertEquals(provider.get("c.c.c").get(), "cccValue");
        assertEquals(provider.get("numInt").get(), "9999");
        assertEquals(provider.get("num.Int").get(), "123");
        assertEquals(provider.get("num.Byte").get(), "100");
        assertEquals(provider.get("boolean").get(), "true");
        assertEquals(provider.get("num.BD").get(), "2376523725372653.287362836283628362863");
        assertEquals(provider.get("num.Double").get(), "21334.43254");
        assertTrue(!provider.get("blabla").isPresent());
        assertTrue(provider.get("num.BD").isPresent());
    }

    @Test
    public void readXmlPropertiesTest() {
        PropertyProvider provider = PropertyProviders.fromPaths("classpath:ucs/UC1ReadProperties/UC1ReadPropertiesTest.xml");
        assertNotNull(provider);
        assertEquals(provider.get("a-xml").get(), "aFromXml");
        assertEquals(provider.get("b-xml").get(), "bFromXml");
        assertEquals(provider.get("a.b.c-xml").get(), "abcFromXml");
    }

    @Test
    public void readIniPropertiesTest() {
        PropertyProvider provider = PropertyProviders.fromPaths("classpath:ucs/UC1ReadProperties/UC1ReadPropertiesTest.ini");
        assertNotNull(provider);
        assertEquals(provider.get("a.b.c").get(), "abcValue-fromIni");
        assertEquals(provider.get("a.b.b").get(), "abbValue-fromIni");
        assertEquals(provider.get("a.b.a").get(), "abaValue-fromIni");
        assertEquals(provider.get("mixed.a.b").get(), "abValue");
        assertFalse(provider.get("mixed.foo").isPresent());
        assertTrue(provider.get("num.BD").isPresent());
    }

    @Test
    public void readAllPropertiesTest() {
        PropertyProvider provider = PropertyProviders.fromPaths(AggregationPolicy.IGNORE, "classpath:ucs/UC1ReadProperties/UC1ReadPropertiesTest.*");
        assertNotNull(provider);
        // fromMap ini file
        assertEquals(provider.get("a.b.c").get(), "abcValue-fromIni");
        assertEquals(provider.get("a.b.b").get(), "abbValue-fromIni");
        assertEquals(provider.get("a.b.a").get(), "abaValue-fromIni");
        // fromMap properties
        assertTrue(provider.containsKey("num.BD"));
        // fromMap xml properties
        assertEquals(provider.get("a-xml").get(), "aFromXml");
        assertEquals(provider.get("b-xml").get(), "bFromXml");
    }

    @Test
    public void checkForAValue() {
        PropertyProvider provider = PropertyProviders.fromPaths("classpath:ucs/UC1ReadProperties/UC1ReadPropertiesTest.properties");
        assertFalse(provider.containsKey("blabla"));
        assertTrue(provider.containsKey("num.BD"));
        assertFalse(provider.get("blabla").isPresent());
        assertTrue(provider.get("num.BD").isPresent());
    }

    @Test
    public void checkKeys() {
        PropertyProvider provider = PropertyProviders.fromPaths("classpath:ucs/UC1ReadProperties/UC1ReadPropertiesTest.properties");
        assertEquals(provider.keySet().size(), 16);
        assertTrue(provider.keySet().contains("boolean"));
        assertFalse(provider.keySet().contains("blabla"));
    }

    @Test
    public void checkToMap() {
        PropertyProvider provider = PropertyProviders.fromPaths("classpath:ucs/UC1ReadProperties/UC1ReadPropertiesTest.properties");
        Map<String, String> map = provider.toMap();
        assertNotNull(map);
        assertEquals(map.size(), 16);
        assertEquals(provider.keySet(), map.keySet());
        assertTrue(map.keySet().contains("boolean"));
        assertFalse(map.keySet().contains("blabla"));
    }

    @Test
    public void checkMetaInfo() {
        PropertyProvider provider = PropertyProviders.fromPaths("classpath:ucs/UC1ReadProperties/UC1ReadPropertiesTest.properties");
        MetaInfo meta = provider.getMetaInfo();
        assertNotNull(meta);
    }

    @Test
    public void checkNeverNull() {
        PropertyProvider provider = PropertyProviders.fromPaths("classpath:ucs/UC1ReadProperties/UC1ReadPropertiesTest.properties");
        assertNotNull(provider.get("blabla"));
        assertNotNull(provider.get("a.b.c"));
    }

    @Test
    public void checkUndefined() {
        PropertyProvider provider = PropertyProviders.fromPaths("classpath:ucs/UC1ReadProperties/UC1ReadPropertiesTest.properties");
        assertFalse(provider.get("blabla").isPresent());
        assertTrue(provider.get("a.b.c").isPresent());
    }

    @Test
    public void checkPassDefaultValues() {
        PropertyProvider provider = PropertyProviders.fromPaths("classpath:ucs/UC1ReadProperties/UC1ReadPropertiesTest.properties");
        assertEquals("myDefaultValue", provider.get("blabla").orElse("myDefaultValue"));
    }

    @Test(expected = IllegalStateException.class)
    public void checkThrowCustomException() {
        PropertyProvider provider = PropertyProviders.fromPaths("classpath:ucs/UC1ReadProperties/UC1ReadPropertiesTest.properties");
        provider.get("blabla").orElseThrow(() -> new IllegalStateException("checkThrowCustomException"));
    }
}
