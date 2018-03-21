/*
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
package org.apache.tamaya.base;

import org.junit.Test;

import javax.config.ConfigProvider;
import javax.config.ConfigValue;
import javax.config.spi.Converter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Tests for {@link DefaultConfigValue}.
 */
public class DefaultConfigValueTest {

    private DefaultConfigValue<String> value = new DefaultConfigValue<>(
            (DefaultConfig)ConfigProvider.getConfig(), (DefaultConfig)ConfigProvider.getConfig(),
            "java.version", String.class);
    private DefaultConfigValue<String> notExisting = new DefaultConfigValue<>(
            (DefaultConfig)ConfigProvider.getConfig(), (DefaultConfig)ConfigProvider.getConfig(),
            "notExisting", String.class);

    @Test
    public void as() throws Exception {
        ConfigValue<Integer> val = notExisting
                .withStringDefault("123")
                .as(Integer.class);
        assertNotNull(val);
        assertEquals(Integer.valueOf("123"), val.getValue());
        ConfigValue<BigDecimal> val2 = val.as(BigDecimal.class);
        assertNotNull(val2);
        assertEquals(new BigDecimal("123"), val2.getValue());
    }

    @Test
    public void asList() throws Exception {
        ConfigValue<List<String>> val = value.asList();
        assertNotNull(val);
        assertNotNull(val.getValue());
        assertFalse(val.getValue().isEmpty());
        assertEquals(val.getValue().size(), 1);
        assertEquals(val.getValue().get(0), System.getProperty("java.version"));
    }

    @Test
    public void asSet() throws Exception {
        ConfigValue<Set<String>> val = value.asSet();
        assertNotNull(val);
        assertNotNull(val.getValue());
        assertFalse(val.getValue().isEmpty());
        assertEquals(val.getValue().size(), 1);
        assertEquals(val.getValue().iterator().next(), System.getProperty("java.version"));
    }

    @Test
    public void getConverter() throws Exception {
        Integer val = notExisting
                .withStringDefault("123")
                .as(Integer.class)
                .getValue();
        assertNotNull(val);
    }

    @Test
    public void useConverter() throws Exception {
        ConfigValue<Integer> val = value.useConverter(new Converter<Integer>() {
            @Override
            public Integer convert(String s) {
               return new Integer(1234);
            }
        });
        assertNotNull(val);
        assertEquals(new Integer(1234), val.getValue());
    }

    @Test
    public void withDefault() throws Exception {
        ConfigValue<Double> val = notExisting.as(Double.class)
        .withDefault(Double.valueOf("123.45"));
        assertNotNull(val);
        assertEquals(Double.valueOf("123.45"), val.getDefaultValue());
        assertEquals(Double.valueOf("123.45"), val.getValue());
    }

    @Test
    public void withStringDefault() throws Exception {
        ConfigValue<String> val = notExisting.withStringDefault("12345");
        assertNotNull(val);
        assertEquals("12345", val.getValue());
        assertEquals(null, val.getDefaultValue());
    }

    @Test
    public void cacheFor() throws Exception {
        ConfigValue<String> val = value.cacheFor(1, TimeUnit.SECONDS);
        assertNotNull(val);
    }

    @Test
    public void evaluateVariables() throws Exception {
        ConfigValue<String> val = value.evaluateVariables(true);
        assertNotNull(val);

        val = value.evaluateVariables(false);
        assertNotNull(val);
    }

    @Test
    public void withLookupChain() throws Exception {
        ConfigValue<String> val = value.withLookupChain("");
        assertNotNull(val);
        val = value.withLookupChain("a");
        assertNotNull(val);
        val = value.withLookupChain("a", "b");
        assertNotNull(val);
    }

    @Test
    public void onChange() throws Exception {
        String[] keyChanged = new String[1];
        String[] oldVal = new String[1];
        String[] newVal = new String[1];
        ConfigValue<String> val = notExisting.onChange(new ConfigValue.ConfigChanged() {
            @Override
            public <T> void onValueChange(String k, T o, T n) {
                keyChanged[0] = k;
                oldVal[0] = (String)o;
                newVal[0] = (String)n;
            }
        });
        assertNotNull(val);
        val = val.withDefault("123");
        val.getOptionalValue();
        assertEquals(keyChanged[0], notExisting.getKey());
        assertEquals(oldVal[0], null);
        assertEquals(newVal[0], "123");
        val = val.withDefault("1234");
        val.getOptionalValue();
        assertEquals(keyChanged[0], notExisting.getKey());
        assertEquals(oldVal[0], "123");
        assertEquals(newVal[0], "1234");
    }

    @Test
    public void getValue() throws Exception {
        assertEquals(System.getProperty("java.version"), value.getValue());
    }

    @Test
    public void getOptionalValue() throws Exception {
        Optional<String> optValue = value.getOptionalValue();
        assertNotNull(optValue);
        assertTrue(optValue.isPresent());
        assertEquals(System.getProperty("java.version"), optValue.get());
        assertFalse(notExisting.getOptionalValue().isPresent());
    }

    @Test
    public void getKey() throws Exception {
        assertEquals("java.version", value.getKey());
    }

    @Test
    public void getResolvedKey() throws Exception {
        assertEquals("java.version", value.getResolvedKey());
    }

    @Test
    public void getDefaultValue() throws Exception {
        assertNull(value.getDefaultValue());
    }

    @Test
    public void evaluateKeys() throws Exception {
        List<String> keys = value.evaluateKeys();
        assertNotNull(keys);
        assertEquals(keys.size(), 1);
        assertTrue(keys.contains("java.version"));
    }

    @Test
    public void testToString() throws Exception {
        assertNotNull(value.toString());
    }

}