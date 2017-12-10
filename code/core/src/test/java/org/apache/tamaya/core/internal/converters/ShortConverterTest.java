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
package org.apache.tamaya.core.internal.converters;

import org.junit.Test;

import javax.config.Config;
import javax.config.ConfigProvider;

import static org.junit.Assert.*;

/**
 * Tests the default converter for Shorts.
 */
public class ShortConverterTest {

    /**
     * Test conversion. The values are provided by
     * {@link ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Short_Decimal() throws Exception {
        Config config = ConfigProvider.getConfig();
        Short valueRead = config.getValue("tests.converter.short.decimal", Short.class);
        assertTrue(valueRead != null);
        assertEquals(valueRead.intValue(), 101);
    }

    /**
     * Test conversion. The values are provided by
     * {@link ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Short_Octal() throws Exception {
        Config config = ConfigProvider.getConfig();
        Short valueRead = config.getValue("tests.converter.short.octal", Short.class);
        assertTrue(valueRead != null);
        assertEquals(valueRead.intValue(), Short.decode("02").intValue());
    }

    /**
     * Test conversion. The values are provided by
     * {@link ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Short_Hex() throws Exception {
        Config config = ConfigProvider.getConfig();
        Short valueRead = config.getValue("tests.converter.short.hex.lowerX", Short.class);
        assertTrue(valueRead != null);
        assertEquals(valueRead.intValue(), Short.decode("0x2F").intValue());
        valueRead = config.getValue("tests.converter.short.hex.upperX", Short.class);
        assertTrue(valueRead != null);
        assertEquals(valueRead.intValue(), Short.decode("0X3F").intValue());
    }

    /**
     * Test conversion. The value are provided by
     * {@link ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_NotPresent() throws Exception {
        Config config = ConfigProvider.getConfig();
        Short valueRead = config.getValue("tests.converter.short.foo", Short.class);
        assertFalse(valueRead != null);
    }

    /**
     * Test conversion. The values are provided by
     * {@link org.apache.tamaya.core.internal.converters.ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Short_MinValue() throws Exception {
        Config config = ConfigProvider.getConfig();
        Short valueRead = config.getValue("tests.converter.short.min", Short.class);
        assertTrue(valueRead != null);
        assertEquals(Short.MIN_VALUE, valueRead.intValue());
    }

    /**
     * Test conversion. The values are provided by
     * {@link org.apache.tamaya.core.internal.converters.ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Short_MaxValue() throws Exception {
        Config config = ConfigProvider.getConfig();
        Short valueRead = config.getValue("tests.converter.short.max", Short.class);
        assertTrue(valueRead != null);
        assertEquals(Short.MAX_VALUE, valueRead.intValue());
    }
}