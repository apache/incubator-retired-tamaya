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
import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.spi.ConversionContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import org.junit.Test;

/**
 * Tests the default converter for bytes.
 */
public class FloatConverterTest {

    /**
     * Test conversion. The createValue are provided by
     * {@link ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Float_Decimal() throws Exception {
        Configuration config = Configuration.current();
        Float valueRead = config.get("tests.converter.float.decimal", Float.class);
        assertThat(valueRead!=null).isTrue();
        assertThat(1.23456789f).isCloseTo(valueRead, within(0.0f));
    }

    /**
     * Test conversion. The createValue are provided by
     * {@link ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Float_DecimalNegative() throws Exception {
        Configuration config = Configuration.current();
        Float valueRead = config.get("tests.converter.float.decimalNegative", Float.class);
        assertThat(valueRead!=null).isTrue();
        assertThat(-1.23456789f).isCloseTo(valueRead, within(0.0f));
    }

    /**
     * Test conversion. The createValue are provided by
     * {@link ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Float_Integer() throws Exception {
        Configuration config = Configuration.current();
        Float valueRead = config.get("tests.converter.float.integer", Float.class);
        assertThat(valueRead!=null).isTrue();
        assertThat(100f).isCloseTo(valueRead, within(0.0f));
    }

    /**
     * Test conversion. The createValue are provided by
     * {@link ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Float_Hex1() throws Exception {
        Configuration config = Configuration.current();
        Float valueRead = config.get("tests.converter.float.hex1", Float.class);
        assertThat(valueRead!=null).isTrue();
        assertThat(255f).isCloseTo(valueRead, within(0.0f));
    }

    /**
     * Test conversion. The createValue are provided by
     * {@link ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Float_Hex2() throws Exception {
        Configuration config = Configuration.current();
        Float valueRead = config.get("tests.converter.float.hex2", Float.class);
        assertThat(valueRead!=null).isTrue();
        assertThat(-255f).isCloseTo(valueRead, within(0.0f));
    }

    /**
     * Test conversion. The createValue are provided by
     * {@link ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Float_Hex3() throws Exception {
        Configuration config = Configuration.current();
        Float valueRead = config.get("tests.converter.float.hex3", Float.class);
        assertThat(valueRead!=null).isTrue();
        assertThat(255f).isCloseTo(valueRead, within(0.0f));
    }

    /**
     * Test conversion. The createValue are provided by
     * {@link ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Float_MinValue() throws Exception {
        Configuration config = Configuration.current();
        Float valueRead = config.get("tests.converter.float.min", Float.class);
        assertThat(valueRead!=null).isTrue();
        assertThat(valueRead).isCloseTo(Float.MIN_VALUE, within(0.0f));
    }

    /**
     * Test conversion. The createValue are provided by
     * {@link ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Float_MaxValue() throws Exception {
        Configuration config = Configuration.current();
        Float valueRead = config.get("tests.converter.float.max", Float.class);
        assertThat(valueRead!=null).isTrue();
        assertThat(valueRead).isCloseTo(Float.MAX_VALUE, within(0.0f));
    }

    /**
     * Test conversion. The createValue are provided by
     * {@link ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Float_NaNValue() throws Exception {
        Configuration config = Configuration.current();
        Float valueRead = config.get("tests.converter.float.nan", Float.class);
        assertThat(valueRead!=null).isTrue();
        assertThat(valueRead).isCloseTo(Float.NaN, within(0.0f));
    }

    /**
     * Test conversion. The createValue are provided by
     * {@link ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Float_PositiveInfinityValue() throws Exception {
        Configuration config = Configuration.current();
        Float valueRead = config.get("tests.converter.float.pi", Float.class);
        assertThat(valueRead!=null).isTrue();
        assertThat(valueRead).isCloseTo(Float.POSITIVE_INFINITY, within(0.0f));
    }

    /**
     * Test conversion. The createValue are provided by
     * {@link ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Float_NegativeInfinityValue() throws Exception {
        Configuration config = Configuration.current();
        Float valueRead = config.get("tests.converter.float.ni", Float.class);
        assertThat(valueRead!=null).isTrue();
        assertThat(valueRead).isCloseTo(Float.NEGATIVE_INFINITY, within(0.0f));
    }
      
    @Test(expected = ConfigException.class)
    public void testConvert_FloatInvalid() throws ConfigException {
        Configuration config = Configuration.current();
        config.get("tests.converter.float.invalid", Float.class);
    }

    @Test
    public void callToConvertAddsMoreSupportedFormatsToTheContext() throws Exception {
        ConversionContext context = new ConversionContext.Builder(TypeLiteral.of(Float.class)).build();

        FloatConverter converter = new FloatConverter();
        converter.convert("", context);

        assertThat(context.getSupportedFormats().contains("<float> (FloatConverter)")).isTrue();
        assertThat(context.getSupportedFormats().contains("MIN_VALUE (FloatConverter)")).isTrue();
        assertThat(context.getSupportedFormats().contains("MAX_VALUE (FloatConverter)")).isTrue();
    }

    @Test
    public void testHashCode() {
        FloatConverter instance = new FloatConverter();
        assertThat(instance.hashCode()).isEqualTo(FloatConverter.class.hashCode());
    }

    @Test
    public void testEquality() {
        FloatConverter converter = new FloatConverter();

        assertThat(converter).isEqualTo(new FloatConverter());
        assertThat(converter).isNotEqualTo(new InstantConverter());
        assertThat(converter).isNotEqualTo(null);
    }
}
