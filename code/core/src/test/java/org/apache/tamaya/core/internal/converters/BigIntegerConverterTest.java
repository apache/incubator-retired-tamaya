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

import java.math.BigInteger;
import org.apache.tamaya.ConfigException;
import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;
import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.spi.ConversionContext;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import static org.mockito.Mockito.mock;

/**
 *
 * @author William.Lieurance 2018-02-01
 */
public class BigIntegerConverterTest {

    /**
     * Test conversion. The value are provided by
     * {@link org.apache.tamaya.core.internal.converters.ConverterTestsPropertySource}.
     *
     * @throws Exception
     */
    @Test
    public void testConvert_BigInteger_Decimal() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        BigInteger valueRead = config.get("tests.converter.bd.decimal", BigInteger.class);
        assertThat(valueRead).isNotNull();
        assertEquals(BigInteger.valueOf(101), valueRead);
    }

    /**
     * Test conversion. The value are provided by
     * {@link org.apache.tamaya.core.internal.converters.ConverterTestsPropertySource}.
     *
     * @throws Exception
     */
    @Test
    public void testConvert_BigInteger_Hex() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        BigInteger valueRead = config.get("tests.converter.bd.hex.lowerX", BigInteger.class);
        assertThat(valueRead).isNotNull();
        assertEquals(new BigInteger("47"), valueRead);
        valueRead = config.get("tests.converter.bd.hex.upperX", BigInteger.class);
        assertThat(valueRead).isNotNull();
        assertEquals(new BigInteger("63"), valueRead);
        valueRead = config.get("tests.converter.bd.hex.negLowerX", BigInteger.class);
        assertThat(valueRead).isNotNull();
        assertEquals(new BigInteger("-47"), valueRead);
        valueRead = config.get("tests.converter.bd.hex.negUpperX", BigInteger.class);
        assertThat(valueRead).isNotNull();
        assertEquals(new BigInteger("-63"), valueRead);

    }

    /**
     * Test conversion. The value are provided by
     * {@link org.apache.tamaya.core.internal.converters.ConverterTestsPropertySource}.
     *
     * @throws Exception
     */
    @Ignore
    @Test
    public void testConvert_BigInteger_BigHex() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        BigInteger valueRead = config.get("tests.converter.bd.hex.subTenX", BigInteger.class);
        assertThat(valueRead).isNotNull();
        assertEquals(new BigInteger("16777215"), valueRead);
        valueRead = config.get("tests.converter.bd.hex.negSubTenX", BigInteger.class);
        assertThat(valueRead).isNotNull();
        assertEquals(new BigInteger("-263"), valueRead);
    }

    @Test(expected = ConfigException.class)
    public void badPositiveHex() {
        Configuration config = ConfigurationProvider.getConfiguration();
        config.get("tests.converter.bd.hex.badX", BigInteger.class);
    }

    @Test(expected = ConfigException.class)
    public void badNegativeHex() {
        Configuration config = ConfigurationProvider.getConfiguration();
        config.get("tests.converter.bd.hex.negBadX", BigInteger.class);
    }

    /**
     * Test conversion. The value are provided by
     * {@link org.apache.tamaya.core.internal.converters.ConverterTestsPropertySource}.
     *
     * @throws Exception
     */
    @Test
    public void testConvert_NotPresent() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        BigInteger valueRead = config.get("tests.converter.bd.foo", BigInteger.class);
        assertNull(valueRead);
    }

    /**
     * Test conversion. The value are provided by
     * {@link ConverterTestsPropertySource}.
     *
     * @throws Exception
     */
    @Test
    public void testConvert_BigInteger_BigValue() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        BigInteger valueRead = config.get("tests.converter.bd.big", BigInteger.class);
        assertThat(valueRead).isNotNull();
        assertEquals(new BigInteger("101666666666666662333337263723628763821638923628193612983618293628763"),
                valueRead);
    }

    @Test
    public void converterHandlesNullValueCorrectly() throws Exception {
        ConversionContext context = mock(ConversionContext.class);

        BigIntegerConverter converter = new BigIntegerConverter();
        BigInteger value = converter.convert("", context);

        assertThat(value).isNull();
    }

    @Test
    public void callToConvertAddsMoreSupportedFormatsToTheContext() throws Exception {
        ConversionContext context = new ConversionContext.Builder(TypeLiteral.of(BigInteger.class)).build();
        BigIntegerConverter converter = new BigIntegerConverter();
        BigInteger value = converter.convert("", context);

        assertThat(value).isNull();
        assertTrue(context.getSupportedFormats().contains("<bigint> -> new BigInteger(bigint) (BigIntegerConverter)"));
    }

    @Test
    public void testHashCode() {
        BigIntegerConverter instance = new BigIntegerConverter();
        assertEquals(BigIntegerConverter.class.hashCode(), instance.hashCode());
    }

}
