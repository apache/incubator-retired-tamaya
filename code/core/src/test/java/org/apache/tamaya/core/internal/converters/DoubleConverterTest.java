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
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

/**
 * Tests the default converter for doubles.
 */
public class DoubleConverterTest {

    /**
     * Test conversion. The createValue are provided by
     * {@link org.apache.tamaya.core.internal.converters.ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Double_Decimal() throws Exception {
        Configuration config = Configuration.current();
        Double valueRead = config.get("tests.converter.double.decimal", Double.class);
        assertThat(valueRead!=null).isTrue();
        assertThat(1.23456789).isCloseTo(valueRead, within(0.0d));
    }

    /**
     * Test conversion. The createValue are provided by
     * {@link org.apache.tamaya.core.internal.converters.ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Double_DecimalNegative() throws Exception {
        Configuration config = Configuration.current();
        Double valueRead = config.get("tests.converter.double.decimalNegative", Double.class);
        assertThat(valueRead!=null).isTrue();
        assertThat(-1.23456789).isCloseTo(valueRead, within(0.0d));
    }

    /**
     * Test conversion. The createValue are provided by
     * {@link org.apache.tamaya.core.internal.converters.ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Double_Integer() throws Exception {
        Configuration config = Configuration.current();
        Double valueRead = config.get("tests.converter.double.integer", Double.class);
        assertThat(valueRead!=null).isTrue();
        assertThat(100d).isCloseTo(valueRead, within(0.0d));
    }

    /**
     * Test conversion. The createValue are provided by
     * {@link org.apache.tamaya.core.internal.converters.ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Double_Hex1() throws Exception {
        Configuration config = Configuration.current();
        Double valueRead = config.get("tests.converter.double.hex1", Double.class);
        assertThat(valueRead!=null).isTrue();
        assertThat(255d).isCloseTo(valueRead, within(0.0d));
    }

    /**
     * Test conversion. The createValue are provided by
     * {@link org.apache.tamaya.core.internal.converters.ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Double_Hex2() throws Exception {
        Configuration config = Configuration.current();
        Double valueRead = config.get("tests.converter.double.hex2", Double.class);
        assertThat(valueRead!=null).isTrue();
        assertThat(-255d).isCloseTo(valueRead, within(0.0d));
    }

    /**
     * Test conversion. The createValue are provided by
     * {@link org.apache.tamaya.core.internal.converters.ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Double_Hex3() throws Exception {
        Configuration config = Configuration.current();
        Double valueRead = config.get("tests.converter.double.hex3", Double.class);
        assertThat(valueRead!=null).isTrue();
    }

    /**
     * Test conversion. The createValue are provided by
     * {@link org.apache.tamaya.core.internal.converters.ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Double_MinValue() throws Exception {
        Configuration config = Configuration.current();
        Double valueRead = config.get("tests.converter.double.min", Double.class);
        assertThat(valueRead!=null).isTrue();
        assertThat(valueRead).isCloseTo(Double.MIN_VALUE, within(0.0d));
    }

    /**
     * Test conversion. The createValue are provided by
     * {@link org.apache.tamaya.core.internal.converters.ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Double_MaxValue() throws Exception {
        Configuration config = Configuration.current();
        Double valueRead = config.get("tests.converter.double.max", Double.class);
        assertThat(valueRead!=null).isTrue();
        assertThat(valueRead).isCloseTo(Double.MAX_VALUE, within(0.0d));
    }

    /**
     * Test conversion. The createValue are provided by
     * {@link org.apache.tamaya.core.internal.converters.ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Double_NaNValue() throws Exception {
        Configuration config = Configuration.current();
        Double valueRead = config.get("tests.converter.double.nan", Double.class);
        assertThat(valueRead!=null).isTrue();
        assertThat(valueRead).isCloseTo(Double.NaN, within(0.0d));
    }

    /**
     * Test conversion. The createValue are provided by
     * {@link org.apache.tamaya.core.internal.converters.ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Double_PositiveInfinityValue() throws Exception {
        Configuration config = Configuration.current();
        Double valueRead = config.get("tests.converter.double.pi", Double.class);
        assertThat(valueRead!=null).isTrue();
        assertThat(valueRead).isCloseTo(Double.POSITIVE_INFINITY, within(0.0d));
    }

    /**
     * Test conversion. The createValue are provided by
     * {@link org.apache.tamaya.core.internal.converters.ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Double_NegativeInfinityValue() throws Exception {
        Configuration config = Configuration.current();
        Double valueRead = config.get("tests.converter.double.ni", Double.class);
        assertThat(valueRead!=null).isTrue();
        assertThat(valueRead).isCloseTo(Double.NEGATIVE_INFINITY, within(0.0d));
    }
    
    
    @Test(expected = ConfigException.class)
    public void testConvert_DoubleInvalid() throws ConfigException {
        Configuration config = Configuration.current();
        config.get("tests.converter.double.invalid", Double.class);
    }

    @Test
    public void callToConvertAddsMoreSupportedFormatsToTheContext() throws Exception {
        ConversionContext context = new ConversionContext.Builder(TypeLiteral.of(Double.class)).build();

        DoubleConverter converter = new DoubleConverter();
        converter.convert("", context);

        assertThat(context.getSupportedFormats()).contains("<double> (DoubleConverter)",
                    "MIN_VALUE (DoubleConverter)", "MAX_VALUE (DoubleConverter)");
    }

    @Test
    public void testHashCode() {
        DoubleConverter instance = new DoubleConverter();
        assertThat(instance.hashCode()).isEqualTo(DoubleConverter.class.hashCode());
    }

    @Test
    public void testEquality() {
        DoubleConverter converter = new DoubleConverter();

        assertThat(converter).isEqualTo(new DoubleConverter());
        assertThat(converter).isNotEqualTo(new DurationConverter());
        assertThat(converter).isNotEqualTo(null);
    }
}
