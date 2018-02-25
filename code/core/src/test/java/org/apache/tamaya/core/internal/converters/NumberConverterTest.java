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

import java.math.BigDecimal;
import org.apache.tamaya.ConfigException;
import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.spi.ConversionContext;

import static org.junit.Assert.*;

/**
 * Tests the default converter for Number.
 */
public class NumberConverterTest {

    /**
     * Test conversion. The value are provided by
     * {@link ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Decimal() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Number valueRead = config.get("tests.converter.bd.decimal", Number.class);
        assertNotNull(valueRead);
        assertEquals(valueRead, 101L);
    }


    /**
     * Test conversion. The value are provided by
     * {@link ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Hex() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Number valueRead = config.get("tests.converter.bd.hex.lowerX", Number.class);
        assertNotNull(valueRead);
        assertEquals(valueRead, Long.valueOf("47"));
        valueRead = config.get("tests.converter.bd.hex.upperX", Number.class);
        assertNotNull(valueRead);
        assertEquals(valueRead, Long.valueOf("63"));
    }

    /**
     * Test conversion. The value are provided by
     * {@link ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_NotPresent() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Number valueRead = config.get("tests.converter.bd.foo", Number.class);
        assertNull(valueRead);
    }

    /**
     * Test conversion. The value are provided by
     * {@link org.apache.tamaya.core.internal.converters.ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_BigValue() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Number valueRead = config.get("tests.converter.bd.big", Number.class);
        assertNotNull(valueRead);
        assertEquals(new BigDecimal("101666666666666662333337263723628763821638923628193612983618293628763"),
                valueRead);
    }

    /**
     * Test conversion. The value are provided by
     * {@link org.apache.tamaya.core.internal.converters.ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_BigFloatValue() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Number valueRead = config.get("tests.converter.bd.bigFloat", Number.class);
        assertNotNull(valueRead);
        assertEquals(new BigDecimal("1016666666666666623333372637236287638216389293628763.1016666666666666623333372" +
                "63723628763821638923628193612983618293628763"), valueRead);
    }
    
    /**
     * Test conversion. The value are provided by
     * {@link org.apache.tamaya.core.internal.converters.ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_PositiveInfinityValue() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Number valueRead = config.get("tests.converter.double.pi", Number.class);
        assertNotNull(valueRead);
        assertEquals(Double.POSITIVE_INFINITY, valueRead.doubleValue(),0.0d);
    }

    /**
     * Test conversion. The value are provided by
     * {@link org.apache.tamaya.core.internal.converters.ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_NegativeInfinityValue() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Number valueRead = config.get("tests.converter.double.ni", Number.class);
        assertNotNull(valueRead);
        assertEquals(Double.NEGATIVE_INFINITY, valueRead.doubleValue(),0.0d);
    }
   
    /**
     * Test conversion. The value are provided by
     * {@link org.apache.tamaya.core.internal.converters.ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_NaNValue() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Number valueRead = config.get("tests.converter.double.nan", Number.class);
        assertNotNull(valueRead);
        assertEquals(Double.NaN, valueRead.doubleValue(),0.0d);
    }
        
    @Test(expected = ConfigException.class)
    public void testConvert_NumberInvalid() throws ConfigException {
        Configuration config = ConfigurationProvider.getConfiguration();
        config.get("tests.converter.bd.invalid", Number.class);
    }

    @Test
    public void callToConvertAddsMoreSupportedFormatsToTheContext() throws Exception {
        ConversionContext context = new ConversionContext.Builder(TypeLiteral.of(Number.class)).build();
        NumberConverter converter = new NumberConverter();
        converter.convert("", context);

        assertTrue(context.getSupportedFormats().contains("<double>, <long> (NumberConverter)"));
        assertTrue(context.getSupportedFormats().contains("POSITIVE_INFINITY (NumberConverter)"));
        assertTrue(context.getSupportedFormats().contains("NEGATIVE_INFINITY (NumberConverter)"));
        assertTrue(context.getSupportedFormats().contains("NAN (NumberConverter)"));
    }

    @Test
    public void testHashCode() {
        NumberConverter instance = new NumberConverter();
        assertEquals(NumberConverter.class.hashCode(), instance.hashCode());
    }
}