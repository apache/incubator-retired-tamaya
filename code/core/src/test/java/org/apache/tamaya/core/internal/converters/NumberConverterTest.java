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

import static org.assertj.core.api.Assertions.*;

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
        assertThat(valueRead).isNotNull();
        assertThat(101L).isEqualTo(valueRead);
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
        assertThat(valueRead).isNotNull();
        assertThat(Long.valueOf("47")).isEqualTo(valueRead);
        valueRead = config.get("tests.converter.bd.hex.upperX", Number.class);
        assertThat(valueRead).isNotNull();
        assertThat(Long.valueOf("63")).isEqualTo(valueRead);
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
        assertThat(valueRead).isNull();
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
        assertThat(valueRead).isNotNull();
        assertThat(new BigDecimal("101666666666666662333337263723628763821638923628193612983618293628763"))
                .isEqualTo(valueRead);
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
        assertThat(valueRead).isNotNull();
        assertThat(new BigDecimal("1016666666666666623333372637236287638216389293628763.1016666666666666623333372" +
                "63723628763821638923628193612983618293628763"))
                .isEqualTo(valueRead);
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
        assertThat(valueRead).isNotNull();
        assertThat(valueRead.doubleValue()).isCloseTo(Double.POSITIVE_INFINITY, within(0.0d));
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
        assertThat(valueRead).isNotNull();
        assertThat(valueRead.doubleValue()).isCloseTo(Double.NEGATIVE_INFINITY, within(0.0d));
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
        assertThat(valueRead).isNotNull();
        assertThat(valueRead.doubleValue()).isCloseTo(Double.NaN, within(0.0d));
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

        assertThat(context.getSupportedFormats().contains("<double>, <long> (NumberConverter)")).isTrue();
        assertThat(context.getSupportedFormats().contains("POSITIVE_INFINITY (NumberConverter)")).isTrue();
        assertThat(context.getSupportedFormats().contains("NEGATIVE_INFINITY (NumberConverter)")).isTrue();
        assertThat(context.getSupportedFormats().contains("NAN (NumberConverter)")).isTrue();
    }

    @Test
    public void testHashCode() {
        NumberConverter instance = new NumberConverter();
        assertThat(instance.hashCode()).isEqualTo(NumberConverter.class.hashCode());
    }
}