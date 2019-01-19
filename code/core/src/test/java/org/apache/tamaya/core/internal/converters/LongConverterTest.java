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

import static org.assertj.core.api.Assertions.*;

/**
 * Tests the default converter for Longs.
 */
public class LongConverterTest {

    /**
     * Test conversion. The createValue are provided by
     * {@link ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Long_Decimal() throws Exception {
        Configuration config = Configuration.current();
        Long valueRead = config.get("tests.converter.long.decimal", Long.class);
        assertThat(valueRead != null).isTrue();
        assertThat(101).isEqualTo(valueRead.intValue());
    }

    /**
     * Test conversion. The createValue are provided by
     * {@link ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Long_Octal() throws Exception {
        Configuration config = Configuration.current();
        Long valueRead = config.get("tests.converter.long.octal", Long.class);
        assertThat(valueRead != null).isTrue();
        assertThat(Long.decode("02").intValue()).isEqualTo(valueRead.intValue());
    }

    /**
     * Test conversion. The createValue are provided by
     * {@link ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Long_Hex() throws Exception {
        Configuration config = Configuration.current();
        Long valueRead = config.get("tests.converter.long.hex.lowerX", Long.class);
        assertThat(valueRead != null).isTrue();
        assertThat(Long.decode("0x2F").intValue()).isEqualTo(valueRead.intValue());
        valueRead = config.get("tests.converter.long.hex.upperX", Long.class);
        assertThat(valueRead != null).isTrue();
        assertThat(Long.decode("0X3F").intValue()).isEqualTo(valueRead.intValue());
    }

    /**
     * Test conversion. The createValue are provided by
     * {@link ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_NotPresent() throws Exception {
        Configuration config = Configuration.current();
        Long valueRead = config.get("tests.converter.long.foo", Long.class);
        assertThat(valueRead != null).isFalse();
    }

    /**
     * Test conversion. The createValue are provided by
     * {@link org.apache.tamaya.core.internal.converters.ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Long_MinValue() throws Exception {
        Configuration config = Configuration.current();
        Long valueRead = config.get("tests.converter.long.min", Long.class);
        assertThat(valueRead != null).isTrue();
        assertThat(valueRead.longValue()).isEqualTo(Long.MIN_VALUE);
    }

    /**
     * Test conversion. The createValue are provided by
     * {@link org.apache.tamaya.core.internal.converters.ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Long_MaxValue() throws Exception {
        Configuration config = Configuration.current();
        Long valueRead = config.get("tests.converter.long.max", Long.class);
        assertThat(valueRead != null).isTrue();
        assertThat(valueRead.longValue()).isEqualTo(Long.MAX_VALUE);
    }    
    
    @Test(expected = ConfigException.class)
    public void testConvert_LongInvalid() throws ConfigException {
        Configuration config = Configuration.current();
        config.get("tests.converter.long.invalid", Long.class);
    }

    @Test
    public void callToConvertAddsMoreSupportedFormatsToTheContext() throws Exception {
        ConversionContext context = new ConversionContext.Builder(TypeLiteral.of(Long.class)).build();

        LongConverter converter = new LongConverter();
        converter.convert("", context);


        assertThat(context.getSupportedFormats()).contains("<long> (LongConverter)",
                "MIN_VALUE (LongConverter)", "MAX_VALUE (LongConverter)");
    }

    @Test
    public void testHashCode() {
        LongConverter instance = new LongConverter();
        assertThat(instance.hashCode()).isEqualTo(LongConverter.class.hashCode());
    }

    @Test
    public void testEquality() {
        LongConverter converter = new LongConverter();

        assertThat(converter).isEqualTo(new LongConverter());
        assertThat(converter).isNotEqualTo(new NumberConverter());
        assertThat(converter).isNotEqualTo(null);
    }
}
