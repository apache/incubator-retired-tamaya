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

import org.apache.tamaya.ConfigException;
import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;
import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.spi.ConversionContext;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;
/**
 * Tests the default converter for bytes.
 */
public class ByteConverterTest {

    /**
     * Test conversion. The value are provided by
     * {@link org.apache.tamaya.core.internal.converters.ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Byte() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Byte valueRead = config.get("tests.converter.byte.decimal", Byte.class);
        assertNotNull(valueRead);
        assertEquals(valueRead.byteValue(), 101);
        valueRead = config.get("tests.converter.byte.octal", Byte.class);
        assertNotNull(valueRead);
        assertEquals(valueRead.byteValue(), Byte.decode("02").byteValue());
        valueRead = config.get("tests.converter.byte.hex.lowerX", Byte.class);
        assertNotNull(valueRead);
        assertEquals(valueRead.byteValue(), Byte.decode("0x2F").byteValue());
        valueRead = config.get("tests.converter.byte.hex.upperX", Byte.class);
        assertNotNull(valueRead);
        assertEquals(valueRead.byteValue(), Byte.decode("0X3F").byteValue());
        valueRead = config.get("tests.converter.byte.foo", Byte.class);
        assertNull(valueRead);
    }

    /**
     * Test conversion. The value are provided by
     * {@link ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Byte_MinValue() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Byte valueRead = config.get("tests.converter.byte.min", Byte.class);
        assertThat(valueRead).isNotNull();
        assertEquals(Byte.MIN_VALUE, valueRead.byteValue());
    }

    /**
     * Test conversion. The value are provided by
     * {@link ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Byte_MaxValue() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Byte valueRead = config.get("tests.converter.byte.max", Byte.class);
        assertThat(valueRead).isNotNull();
        assertEquals(Byte.MAX_VALUE, valueRead.byteValue());
    }
    /**
     * Test conversion. The value are provided by
     * {@link ConverterTestsPropertySource}.
     *
     * @throws ConfigException
     */
    @Test(expected = ConfigException.class)
    public void testConvert_ByteInvalid() throws ConfigException {
        Configuration config = ConfigurationProvider.getConfiguration();
        Byte valueRead = config.get("tests.converter.byte.invalid", Byte.class);
        assertNull(valueRead);
    }

    @Test
    public void callToConvertAddsMoreSupportedFormatsToTheContext() throws Exception {
        ConversionContext context = new ConversionContext.Builder(TypeLiteral.of(Byte.class)).build();
        ByteConverter converter = new ByteConverter();
        converter.convert("", context);
        
        assertTrue(context.getSupportedFormats().contains("<byte> (ByteConverter)"));
        assertTrue(context.getSupportedFormats().contains("MIN_VALUE (ByteConverter)"));
        assertTrue(context.getSupportedFormats().contains("MAX_VALUE (ByteConverter)"));
    }
    
    @Test
    public void testHashCode() {
        ByteConverter instance = new ByteConverter();
        assertEquals(ByteConverter.class.hashCode(), instance.hashCode());
    }
}
