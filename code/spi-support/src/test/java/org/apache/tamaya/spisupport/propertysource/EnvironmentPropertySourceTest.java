/*
 * Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.apache.tamaya.spisupport.propertysource;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

import org.apache.tamaya.spi.PropertyValue;
import org.junit.Test;

/**
 * Tests for {@link EnvironmentPropertySource}.
 */
public class EnvironmentPropertySourceTest {

    private final EnvironmentPropertySource envPropertySource = new EnvironmentPropertySource();

    @Test
    public void testConstructionPropertiesAndDisabledBehavior() throws IOException {
        EnvironmentPropertySource localEnvironmentPropertySource;
        StringWriter stringBufferWriter = new StringWriter();
        System.getProperties().store(stringBufferWriter, null);
        String before = stringBufferWriter.toString();

        try {
            assertThat(envPropertySource.isDisabled()).isFalse();

            System.setProperty("tamaya.envprops.prefix", "fakeprefix");
            System.setProperty("tamaya.envprops.disable", "true");
            localEnvironmentPropertySource = new EnvironmentPropertySource();
            assertThat(localEnvironmentPropertySource.getPrefix()).isEqualTo("fakeprefix");
            assertThat(localEnvironmentPropertySource.isDisabled()).isTrue();
            assertThat(localEnvironmentPropertySource.get(System.getenv().entrySet().iterator().next().getKey()))
                    .isNull();
            assertThat(localEnvironmentPropertySource.getName()).contains("(disabled)");
            assertThat(localEnvironmentPropertySource.getProperties()).isEmpty();
            assertThat(localEnvironmentPropertySource.toString()).contains("disabled=true");

            System.getProperties().clear();
            System.getProperties().load(new StringReader(before));
            System.setProperty("tamaya.defaults.disable", "true");
            localEnvironmentPropertySource = new EnvironmentPropertySource();
            assertThat(localEnvironmentPropertySource.isDisabled()).isTrue();

            System.getProperties().clear();
            System.getProperties().load(new StringReader(before));
            System.setProperty("tamaya.envprops.disable", "");
            localEnvironmentPropertySource = new EnvironmentPropertySource();
            assertThat(localEnvironmentPropertySource.isDisabled()).isFalse();

            System.getProperties().clear();
            System.getProperties().load(new StringReader(before));
            System.setProperty("tamaya.defaults.disable", "");
            localEnvironmentPropertySource = new EnvironmentPropertySource();
            assertThat(localEnvironmentPropertySource.isDisabled()).isFalse();

        } finally {
            System.getProperties().clear();
            System.getProperties().load(new StringReader(before));
        }
    }

    @Test
    public void testGetOrdinal() throws Exception {
        assertThat(envPropertySource.getOrdinal()).isEqualTo(EnvironmentPropertySource.DEFAULT_ORDINAL);
        EnvironmentPropertySource constructorSetOrdinal22 = new EnvironmentPropertySource(22);
        assertThat(constructorSetOrdinal22.getOrdinal()).isEqualTo(22);

        EnvironmentPropertySource constructorSetOrdinal16 = new EnvironmentPropertySource("sixteenprefix", 16);
        assertThat(constructorSetOrdinal16.getOrdinal()).isEqualTo(16);

    }

    @Test
    public void testGetName() throws Exception {
        assertThat(envPropertySource.getName()).isEqualTo("environment-properties");
    }

  @Test
  public void testSingleCharacterNull() {
    assertThat(envPropertySource.get("a")).isNull();
  }

    @Test
    public void testGet() throws Exception {
        for (Map.Entry<String, String> envEntry : System.getenv().entrySet()) {
            assertThat(envEntry.getValue()).isEqualTo(envPropertySource.get(envEntry.getKey()).getValue());
        }
    }

    @Test
    public void testPrefixedGet() throws Exception {
        EnvironmentPropertySource localEnvironmentPropertySource = new EnvironmentPropertySource("fancyprefix");
        System.out.println(localEnvironmentPropertySource);
        assertThat(localEnvironmentPropertySource.getPrefix()).isEqualTo("fancyprefix");
        localEnvironmentPropertySource.setPropertiesProvider(new MockedSystemPropertiesProvider());
        assertThat(localEnvironmentPropertySource.get("somekey").getValue()).isEqualTo("somekey.createValue");
    }

    @Test
    public void testGetProperties() throws Exception {
        Map<String, PropertyValue> props = envPropertySource.getProperties();
        for (Map.Entry<String, PropertyValue> en : props.entrySet()) {
            if (!en.getKey().startsWith("_")) {
                assertThat(en.getValue().getValue()).isEqualTo(System.getenv(en.getKey()));
            }
        }
    }

    @Test
    public void testPrefixedGetProperties() throws Exception {
        EnvironmentPropertySource localEnvironmentPropertySource = new EnvironmentPropertySource("someprefix");
        Map<String, PropertyValue> props = localEnvironmentPropertySource.getProperties();
        for (Map.Entry<String, PropertyValue> en : props.entrySet()) {
            assertThat(en.getKey().startsWith("someprefix")).isTrue();
            String thisKey = en.getKey().replaceFirst("someprefix", "");
            if (!thisKey.startsWith("_")) {
                assertThat(en.getValue().getValue()).isEqualTo(System.getenv(thisKey));
            }
        }
    }

    @Test
    public void testIsScannable() throws Exception {
        assertThat(envPropertySource.isScannable()).isTrue();
    }

    private class MockedSystemPropertiesProvider extends EnvironmentPropertySource.SystemPropertiesProvider {
        @Override
        String getenv(String key) {
            return key + ".createValue";
        }
    }
}
