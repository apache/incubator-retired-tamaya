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

import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests the default converter for Longs.
 */
public class LongConverterTest {

    /**
     * Test conversion. The value are provided by
     * {@link ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Long_Decimal() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Long valueRead = config.get("tests.converter.long.decimal", Long.class);
        assertTrue(valueRead != null);
        assertEquals(valueRead.intValue(), 101);
    }

    /**
     * Test conversion. The value are provided by
     * {@link ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Long_Octal() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Long valueRead = config.get("tests.converter.long.octal", Long.class);
        assertTrue(valueRead != null);
        assertEquals(valueRead.intValue(), Long.decode("02").intValue());
    }

    /**
     * Test conversion. The value are provided by
     * {@link ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Long_Hex() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Long valueRead = config.get("tests.converter.long.hex.lowerX", Long.class);
        assertTrue(valueRead != null);
        assertEquals(valueRead.intValue(), Long.decode("0x2F").intValue());
        valueRead = config.get("tests.converter.long.hex.upperX", Long.class);
        assertTrue(valueRead != null);
        assertEquals(valueRead.intValue(), Long.decode("0X3F").intValue());
    }

    /**
     * Test conversion. The value are provided by
     * {@link ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_NotPresent() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Long valueRead = config.get("tests.converter.long.foo", Long.class);
        assertFalse(valueRead != null);
    }

    /**
     * Test conversion. The value are provided by
     * {@link org.apache.tamaya.core.internal.converters.ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Long_MinValue() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Long valueRead = config.get("tests.converter.long.min", Long.class);
        assertTrue(valueRead != null);
        assertEquals(Long.MIN_VALUE, valueRead.longValue());
    }

    /**
     * Test conversion. The value are provided by
     * {@link org.apache.tamaya.core.internal.converters.ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Long_MaxValue() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Long valueRead = config.get("tests.converter.long.max", Long.class);
        assertTrue(valueRead != null);
        assertEquals(Long.MAX_VALUE, valueRead.longValue());
    }
}