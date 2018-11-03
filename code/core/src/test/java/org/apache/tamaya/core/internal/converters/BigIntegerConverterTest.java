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
import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.spi.ConversionContext;
import org.junit.Test;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;

/**
 *
 * @author William.Lieurance 2018-02-01
 */
public class BigIntegerConverterTest {

    /**
     * Test conversion. The createValue are provided by
     * {@link org.apache.tamaya.core.internal.converters.ConverterTestsPropertySource}.
     *
     * @throws Exception
     */
    @Test
    public void testConvert_BigInteger_Decimal() throws Exception {
        Configuration config = Configuration.current();
        BigInteger valueRead = config.get("tests.converter.bd.decimal", BigInteger.class);
        assertThat(valueRead).isNotNull();
        assertThat(valueRead).isEqualTo(BigInteger.valueOf(101));
    }

    /**
     * Test conversion. The createValue are provided by
     * {@link org.apache.tamaya.core.internal.converters.ConverterTestsPropertySource}.
     *
     * @throws Exception
     */
    @Test
    public void testConvert_BigInteger_Hex() throws Exception {
        Configuration config = Configuration.current();
        BigInteger valueRead = config.get("tests.converter.bd.hex.lowerX", BigInteger.class);
        assertThat(valueRead).isNotNull();
        assertThat(valueRead).isEqualTo(new BigInteger("47"));
        valueRead = config.get("tests.converter.bd.hex.upperX", BigInteger.class);
        assertThat(valueRead).isNotNull();
        assertThat(valueRead).isEqualTo(new BigInteger("63"));
        valueRead = config.get("tests.converter.bd.hex.negLowerX", BigInteger.class);
        assertThat(valueRead).isNotNull();
        assertThat(valueRead).isEqualTo(new BigInteger("-47"));
        valueRead = config.get("tests.converter.bd.hex.negUpperX", BigInteger.class);
        assertThat(valueRead).isNotNull();
        assertThat(valueRead).isEqualTo(new BigInteger("-63"));

    }

    /**
     * Test conversion. The createValue are provided by
     * {@link org.apache.tamaya.core.internal.converters.ConverterTestsPropertySource}.
     *
     * @throws Exception
     */
    @Test
    public void testConvert_BigInteger_BigHex() throws Exception {
        Configuration config = Configuration.current();
        BigInteger valueRead = config.get("tests.converter.bd.hex.subTenX", BigInteger.class);
        assertThat(valueRead).isNotNull();
        assertThat(valueRead).isEqualTo(new BigInteger("16777215"));
        valueRead = config.get("tests.converter.bd.hex.negSubTenX", BigInteger.class);
        assertThat(valueRead).isNotNull();
        assertThat(valueRead).isEqualTo(new BigInteger("-263"));
    }

    @Test(expected = ConfigException.class)
    public void badPositiveHex() {
        Configuration config = Configuration.current();
        config.get("tests.converter.bd.hex.badX", BigInteger.class);
    }

    @Test(expected = ConfigException.class)
    public void badNegativeHex() {
        Configuration config = Configuration.current();
        config.get("tests.converter.bd.hex.negBadX", BigInteger.class);
    }

    /**
     * Test conversion. The createValue are provided by
     * {@link org.apache.tamaya.core.internal.converters.ConverterTestsPropertySource}.
     *
     * @throws Exception
     */
    @Test
    public void testConvert_NotPresent() throws Exception {
        Configuration config = Configuration.current();
        BigInteger valueRead = config.get("tests.converter.bd.foo", BigInteger.class);
        assertThat(valueRead).isNull();
    }

    /**
     * Test conversion. The createValue are provided by
     * {@link ConverterTestsPropertySource}.
     *
     * @throws Exception
     */
    @Test
    public void testConvert_BigInteger_BigValue() throws Exception {
        Configuration config = Configuration.current();
        BigInteger valueRead = config.get("tests.converter.bd.big", BigInteger.class);
        assertThat(valueRead).isNotNull();
        assertThat(new BigInteger("101666666666666662333337263723628763821638923628193612983618293628763"))
                .isEqualTo(valueRead);
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
        assertThat(context.getSupportedFormats().contains("<bigint> -> new BigInteger(bigint) (BigIntegerConverter)")).isTrue();
    }

    @Test
    public void testHashCode() {
        BigIntegerConverter instance = new BigIntegerConverter();
        assertThat(instance.hashCode()).isEqualTo(BigIntegerConverter.class.hashCode());
    }

    @Test
    public void testEquality() {
        BigIntegerConverter converter = new BigIntegerConverter();

        assertThat(converter).isEqualTo(new BigIntegerConverter());
        assertThat(converter).isNotEqualTo(new BooleanConverter());
        assertThat(converter).isNotEqualTo(null);
    }

}
