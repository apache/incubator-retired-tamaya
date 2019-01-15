/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tamaya.spisupport.propertysource;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import org.apache.tamaya.spi.PropertySource;
import org.apache.tamaya.spi.PropertyValue;
import org.junit.Test;

import java.util.Map;
import java.util.Properties;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SystemPropertySourceTest {

    private final SystemPropertySource testPropertySource = new SystemPropertySource();

    @Test
    public void testConstrcutorWithPrefix() throws Exception {
        SystemPropertySource testPropertySource = new SystemPropertySource("PRE::");
        assertNotNull(testPropertySource.getProperties());
        for(Map.Entry en:System.getProperties().entrySet()){
            assertEquals(System.getProperty(en.getKey().toString()),
                    testPropertySource.get("PRE::"+en.getKey()).getValue());
        }
    }

    @Test
    public void testConstructionPropertiesAndDisabledBehavior() throws IOException {
        SystemPropertySource localSystemPropertySource;
        StringWriter stringBufferWriter = new StringWriter();
        System.getProperties().store(stringBufferWriter, null);
        String before = stringBufferWriter.toString();

        try {
            assertThat(testPropertySource.toStringValues().contains("disabled=true")).isFalse();

            System.setProperty("tamaya.sysprops.prefix", "fakeprefix");
            System.setProperty("tamaya.sysprops.disable", "true");
            localSystemPropertySource = new SystemPropertySource();
            //assertThat(localSystemPropertySource.getPrefix()).isEqualTo("fakeprefix");
            assertThat(localSystemPropertySource.toStringValues().contains("disabled=true")).isTrue();
            assertThat(localSystemPropertySource.get(System.getenv().entrySet().iterator().next().getKey())).isNull();
            assertThat(localSystemPropertySource.getName().contains("(disabled)")).isTrue();
            assertThat(localSystemPropertySource.getProperties().isEmpty()).isTrue();
            assertThat(localSystemPropertySource.toString().contains("disabled=true")).isTrue();

            System.getProperties().clear();
            System.getProperties().load(new StringReader(before));
            System.setProperty("tamaya.defaults.disable", "true");
            localSystemPropertySource = new SystemPropertySource();
            assertThat(localSystemPropertySource.toStringValues().contains("disabled=true")).isTrue();

            System.getProperties().clear();
            System.getProperties().load(new StringReader(before));
            System.setProperty("tamaya.sysprops.disable", "");
            localSystemPropertySource = new SystemPropertySource();
            assertThat(localSystemPropertySource.toStringValues().contains("disabled=true")).isFalse();

            System.getProperties().clear();
            System.getProperties().load(new StringReader(before));
            System.setProperty("tamaya.defaults.disable", "");
            localSystemPropertySource = new SystemPropertySource();
            assertThat(localSystemPropertySource.toStringValues().contains("disabled=true")).isFalse();

        } finally {
            System.getProperties().clear();
            System.getProperties().load(new StringReader(before));
        }
    }

    @Test
    public void testGetOrdinal() throws Exception {

        // test the default ordinal
        assertThat(testPropertySource.getOrdinal()).isEqualTo(SystemPropertySource.DEFAULT_ORDINAL);

        // setCurrent the ordinal to 1001
        System.setProperty(PropertySource.TAMAYA_ORDINAL, "1001");
        assertThat(new SystemPropertySource().getOrdinal()).isEqualTo(1001);
        // currently its not possible to change ordinal at runtime

        // reset it to not destroy other tests!!
        System.clearProperty(PropertySource.TAMAYA_ORDINAL);

        SystemPropertySource constructorSetOrdinal22 = new SystemPropertySource(22);
        assertThat(constructorSetOrdinal22.getOrdinal()).isEqualTo(22);

        SystemPropertySource constructorSetOrdinal16 = new SystemPropertySource("sixteenprefix", 16);
        assertThat(constructorSetOrdinal16.getOrdinal()).isEqualTo(16);
    }

    @Test
    public void testIsScannable() throws Exception {
        assertThat(testPropertySource.isScannable()).isTrue();
    }

    @Test
    public void testGetName() throws Exception {
        assertThat(testPropertySource.getName()).isEqualTo("system-properties");
    }

    @Test
    public void testGet() throws Exception {
        String propertyKeyToCheck = System.getProperties().stringPropertyNames().iterator().next();

        PropertyValue property = testPropertySource.get(propertyKeyToCheck);
        assertThat(property).isNotNull();
        assertThat(property.getValue()).isEqualTo(System.getProperty(propertyKeyToCheck));
    }

    @Test
    public void testGetProperties() throws Exception {
        checkWithSystemProperties(testPropertySource.getProperties());

        // modify system properties
        System.setProperty("test", "myTestVal");

        checkWithSystemProperties(testPropertySource.getProperties());

        // cleanup
        System.clearProperty("test");
    }

    private void checkWithSystemProperties(Map<String, PropertyValue> toCheck) {
        Properties systemEntries = System.getProperties();
        int num = 0;
        for (PropertyValue propertySourceEntry : toCheck.values()) {
            if (propertySourceEntry.getKey().startsWith("_")) {
                continue; // getMeta entry
            }
            num++;
            assertThat(systemEntries.getProperty(propertySourceEntry.getKey())).isEqualTo(propertySourceEntry.getValue());
        }
          assertThat(systemEntries).hasSize(num);
    }
}
