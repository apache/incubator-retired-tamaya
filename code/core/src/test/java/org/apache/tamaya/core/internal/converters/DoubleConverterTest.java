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
 * Tests the default converter for doubles.
 */
public class DoubleConverterTest {

    /**
     * Test conversion. The value are provided by
     * {@link org.apache.tamaya.core.internal.converters.ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Double_Decimal() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Double valueRead = config.get("tests.converter.double.decimal", Double.class);
        assertTrue(valueRead!=null);
        assertEquals(valueRead, 1.23456789, 0.0d);
    }

    /**
     * Test conversion. The value are provided by
     * {@link org.apache.tamaya.core.internal.converters.ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Double_DecimalNegative() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Double valueRead = config.get("tests.converter.double.decimalNegative", Double.class);
        assertTrue(valueRead!=null);
        assertEquals(valueRead, -1.23456789, 0.0d);
    }

    /**
     * Test conversion. The value are provided by
     * {@link org.apache.tamaya.core.internal.converters.ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Double_Integer() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Double valueRead = config.get("tests.converter.double.integer", Double.class);
        assertTrue(valueRead!=null);
        assertEquals(valueRead,100d, 0.0d);
    }

    /**
     * Test conversion. The value are provided by
     * {@link org.apache.tamaya.core.internal.converters.ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Double_Hex1() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Double valueRead = config.get("tests.converter.double.hex1", Double.class);
        assertTrue(valueRead!=null);
        assertEquals(valueRead,255d, 0.0d);
    }

    /**
     * Test conversion. The value are provided by
     * {@link org.apache.tamaya.core.internal.converters.ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Double_Hex2() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Double valueRead = config.get("tests.converter.double.hex2", Double.class);
        assertTrue(valueRead!=null);
        assertEquals(valueRead,-255d, 0.0d);
    }

    /**
     * Test conversion. The value are provided by
     * {@link org.apache.tamaya.core.internal.converters.ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Double_Hex3() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Double valueRead = config.get("tests.converter.double.hex3", Double.class);
        assertTrue(valueRead!=null);
    }

    /**
     * Test conversion. The value are provided by
     * {@link org.apache.tamaya.core.internal.converters.ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Double_MinValue() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Double valueRead = config.get("tests.converter.double.min", Double.class);
        assertTrue(valueRead!=null);
        assertEquals(Double.MIN_VALUE, valueRead,0.0d);
    }

    /**
     * Test conversion. The value are provided by
     * {@link org.apache.tamaya.core.internal.converters.ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Double_MaxValue() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Double valueRead = config.get("tests.converter.double.max", Double.class);
        assertTrue(valueRead!=null);
        assertEquals(Double.MAX_VALUE, valueRead,0.0d);
    }

    /**
     * Test conversion. The value are provided by
     * {@link org.apache.tamaya.core.internal.converters.ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Double_NaNValue() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Double valueRead = config.get("tests.converter.double.nan", Double.class);
        assertTrue(valueRead!=null);
        assertEquals(Double.NaN, valueRead,0.0d);
    }

    /**
     * Test conversion. The value are provided by
     * {@link org.apache.tamaya.core.internal.converters.ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Double_PositiveInfinityValue() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Double valueRead = config.get("tests.converter.double.pi", Double.class);
        assertTrue(valueRead!=null);
        assertEquals(Double.POSITIVE_INFINITY, valueRead,0.0d);
    }

    /**
     * Test conversion. The value are provided by
     * {@link org.apache.tamaya.core.internal.converters.ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Double_NegativeInfinityValue() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Double valueRead = config.get("tests.converter.double.ni", Double.class);
        assertTrue(valueRead!=null);
        assertEquals(Double.NEGATIVE_INFINITY, valueRead,0.0d);
    }
    
    
    @Test(expected = ConfigException.class)
    public void testConvert_DoubleInvalid() throws ConfigException {
        Configuration config = ConfigurationProvider.getConfiguration();
        config.get("tests.converter.double.invalid", Double.class);
    }

    @Test
    public void callToConvertAddsMoreSupportedFormatsToTheContext() throws Exception {
        ConversionContext context = new ConversionContext.Builder(TypeLiteral.of(Double.class)).build();
        DoubleConverter converter = new DoubleConverter();
        converter.convert("", context);

        assertTrue(context.getSupportedFormats().contains("<double> (DoubleConverter)"));
        assertTrue(context.getSupportedFormats().contains("MIN_VALUE (DoubleConverter)"));
        assertTrue(context.getSupportedFormats().contains("MAX_VALUE (DoubleConverter)"));
    }

    @Test
    public void testHashCode() {
        DoubleConverter instance = new DoubleConverter();
        assertEquals(DoubleConverter.class.hashCode(), instance.hashCode());
    }

}
