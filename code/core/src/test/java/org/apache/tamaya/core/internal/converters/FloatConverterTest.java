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
import static org.junit.Assert.assertEquals;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Tests the default converter for bytes.
 */
public class FloatConverterTest {

    /**
     * Test conversion. The value are provided by
     * {@link ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Float_Decimal() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Float valueRead = config.get("tests.converter.float.decimal", Float.class);
        assertTrue(valueRead!=null);
        assertEquals(valueRead, 1.23456789f, 0.0f);
    }

    /**
     * Test conversion. The value are provided by
     * {@link ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Float_DecimalNegative() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Float valueRead = config.get("tests.converter.float.decimalNegative", Float.class);
        assertTrue(valueRead!=null);
        assertEquals(valueRead, -1.23456789f, 0.0f);
    }

    /**
     * Test conversion. The value are provided by
     * {@link ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Float_Integer() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Float valueRead = config.get("tests.converter.float.integer", Float.class);
        assertTrue(valueRead!=null);
        assertEquals(valueRead,100f, 0.0f);
    }

    /**
     * Test conversion. The value are provided by
     * {@link ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Float_Hex1() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Float valueRead = config.get("tests.converter.float.hex1", Float.class);
        assertTrue(valueRead!=null);
        assertEquals(valueRead,255f, 0.0f);
    }

    /**
     * Test conversion. The value are provided by
     * {@link ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Float_Hex2() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Float valueRead = config.get("tests.converter.float.hex2", Float.class);
        assertTrue(valueRead!=null);
        assertEquals(valueRead,-255f, 0.0f);
    }

    /**
     * Test conversion. The value are provided by
     * {@link ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Float_Hex3() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Float valueRead = config.get("tests.converter.float.hex3", Float.class);
        assertTrue(valueRead!=null);
        assertEquals(valueRead,255f, 0.0f);
    }

    /**
     * Test conversion. The value are provided by
     * {@link ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Float_MinValue() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Float valueRead = config.get("tests.converter.float.min", Float.class);
        assertTrue(valueRead!=null);
        assertEquals(Float.MIN_VALUE, valueRead,0.0f);
    }

    /**
     * Test conversion. The value are provided by
     * {@link ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Float_MaxValue() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Float valueRead = config.get("tests.converter.float.max", Float.class);
        assertTrue(valueRead!=null);
        assertEquals(Float.MAX_VALUE, valueRead,0.0f);
    }

    /**
     * Test conversion. The value are provided by
     * {@link ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Float_NaNValue() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Float valueRead = config.get("tests.converter.float.nan", Float.class);
        assertTrue(valueRead!=null);
        assertEquals(Float.NaN, valueRead,0.0f);
    }

    /**
     * Test conversion. The value are provided by
     * {@link ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Float_PositiveInfinityValue() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Float valueRead = config.get("tests.converter.float.pi", Float.class);
        assertTrue(valueRead!=null);
        assertEquals(Float.POSITIVE_INFINITY, valueRead,0.0f);
    }

    /**
     * Test conversion. The value are provided by
     * {@link ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Float_NegativeInfinityValue() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Float valueRead = config.get("tests.converter.float.ni", Float.class);
        assertTrue(valueRead!=null);
        assertEquals(Float.NEGATIVE_INFINITY, valueRead,0.0f);
    }
      
    @Test(expected = ConfigException.class)
    public void testConvert_FloatInvalid() throws ConfigException {
        Configuration config = ConfigurationProvider.getConfiguration();
        config.get("tests.converter.float.invalid", Float.class);
    }

    @Test
    public void callToConvertAddsMoreSupportedFormatsToTheContext() throws Exception {
        ConversionContext context = new ConversionContext.Builder(TypeLiteral.of(Float.class)).build();
        FloatConverter converter = new FloatConverter();
        converter.convert("", context);

        assertTrue(context.getSupportedFormats().contains("<float> (FloatConverter)"));
        assertTrue(context.getSupportedFormats().contains("MIN_VALUE (FloatConverter)"));
        assertTrue(context.getSupportedFormats().contains("MAX_VALUE (FloatConverter)"));
    }

    @Test
    public void testHashCode() {
        FloatConverter instance = new FloatConverter();
        assertEquals(FloatConverter.class.hashCode(), instance.hashCode());
    }


}
