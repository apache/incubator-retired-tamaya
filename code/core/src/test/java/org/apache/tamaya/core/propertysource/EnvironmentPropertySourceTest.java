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
package org.apache.tamaya.core.propertysource;

import org.apache.tamaya.spi.PropertyValue;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Boolean.TRUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link org.apache.tamaya.core.propertysource.EnvironmentPropertySource}.
 */
public class EnvironmentPropertySourceTest {
    private EnvironmentPropertySource envPropertySource;

    @Before
    public void setOUT() {
        envPropertySource = new EnvironmentPropertySource();
    }

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
            assertEquals(envPropertySource.get(envEntry.getKey()).getValue(), envEntry.getValue());
        }
    }

    @Test
    public void testGetProperties() throws Exception {
        Map<String, PropertyValue> props = envPropertySource.getProperties();
        for(Map.Entry<String,PropertyValue> en: props.entrySet()){
            if(!en.getKey().startsWith("_")){
                assertEquals(System.getenv(en.getKey()), en.getValue().getValue());
            }
        }
    }

    @Test
    public void testIsScannable() throws Exception {
        assertTrue(envPropertySource.isScannable());
    }

    @Test
    public void ifPrefixHasBeenConfiguredLookedUpEnvVarNameIsPrefixAndKeyName() {
        EnvironmentPropertySource.SystemPropertiesProvider provider =
            mock(EnvironmentPropertySource.SystemPropertiesProvider.class);

        when(provider.getEnvPropsPrefix()).thenReturn("zzz");
        when(provider.getenv("zzz.VARIABLE")).thenReturn("value");

        envPropertySource.setPropertiesProvider(provider);

        assertThat(envPropertySource.get("VARIABLE").getValue()).isEqualTo("value");
    }

    @Test
    public void ifPrefixHasNotBeenConfiguredLookedUpEnvVarNameIsKeyName() {
        EnvironmentPropertySource.SystemPropertiesProvider provider =
            mock(EnvironmentPropertySource.SystemPropertiesProvider.class);

        when(provider.getEnvPropsPrefix()).thenReturn(null);
        when(provider.getenv("VARIABLE")).thenReturn("value");

        envPropertySource.setPropertiesProvider(provider);

        assertThat(envPropertySource.get("VARIABLE").getValue()).isEqualTo("value");
    }

    @Test
    public void ifPrefixHasBeenSetAllEnvVarsWithPrefixWillBeReturnedByGetProperties() {
        EnvironmentPropertySource.SystemPropertiesProvider provider =
            mock(EnvironmentPropertySource.SystemPropertiesProvider.class);

        when(provider.getEnvPropsPrefix()).thenReturn("zzz");
        when(provider.getenv()).thenReturn(new HashMap<String, String>() {{
            put("zzz.A", "aaa");
            put("zzz.B", "bbb");
            put("C", "ccc");
            put("D", "ddd");
        }});

        envPropertySource.setPropertiesProvider(provider);

        assertThat(envPropertySource.getProperties()).hasSize(2);

        Map<String, PropertyValue> properties = envPropertySource.getProperties();

        assertThat(properties.keySet()).containsOnly("A", "B");
        assertThat(properties.get("A").getValue()).isEqualTo("aaa");
        assertThat(properties.get("B").getValue()).isEqualTo("bbb");
    }

    @Test
    public void canBeDisableBySystemPropertyTamayaDefaultsDisable() {
        EnvironmentPropertySource.SystemPropertiesProvider provider =
            mock(EnvironmentPropertySource.SystemPropertiesProvider.class);

        when(provider.getDefaultsDisable()).thenReturn(TRUE.toString());
        when(provider.getenv("VARIABLE")).thenReturn("value");

        envPropertySource.setPropertiesProvider(provider);

        assertThat(envPropertySource.get("VARIABLE")).isNull();
    }

    @Test
    public void canBeDisableBySystemPropertyTamayaEnvpropsDisable() {
        EnvironmentPropertySource.SystemPropertiesProvider provider =
            mock(EnvironmentPropertySource.SystemPropertiesProvider.class);

        when(provider.getEnvPropsDisable()).thenReturn(TRUE.toString());
        when(provider.getenv("VARIABLE")).thenReturn("value");

        envPropertySource.setPropertiesProvider(provider);

        assertThat(envPropertySource.get("VARIABLE")).isNull();
    }

    @Test
    public void isDisabledIfEvenIsDefaultsDisableIsFalse() throws Exception {
        EnvironmentPropertySource.SystemPropertiesProvider provider =
            mock(EnvironmentPropertySource.SystemPropertiesProvider.class);

        when(provider.getDefaultsDisable()).thenReturn("false");
        when(provider.getEnvPropsDisable()).thenReturn("true");

        envPropertySource.setPropertiesProvider(provider);

        assertThat(envPropertySource.isDisabled()).isTrue();
    }

    @Test
    public void isDisabledIfEvenIsEnvPropsDisableIsFalse() throws Exception {
        EnvironmentPropertySource.SystemPropertiesProvider provider =
            mock(EnvironmentPropertySource.SystemPropertiesProvider.class);

        when(provider.getDefaultsDisable()).thenReturn("true");
        when(provider.getEnvPropsDisable()).thenReturn("false");

        envPropertySource.setPropertiesProvider(provider);

        assertThat(envPropertySource.isDisabled()).isTrue();
    }



}