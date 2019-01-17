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
 * Tests the default converter for Integers.
 */
public class IntegerConverterTest {

    /**
     * Test conversion. The createValue are provided by
     * {@link org.apache.tamaya.core.internal.converters.ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Integer_Decimal() throws Exception {
        Configuration config = Configuration.current();
        Integer valueRead = config.get("tests.converter.integer.decimal", Integer.class);
        assertThat(valueRead != null).isTrue();
        assertThat(101).isEqualTo(valueRead.intValue());
    }

    /**
     * Test conversion. The createValue are provided by
     * {@link org.apache.tamaya.core.internal.converters.ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Integer_Octal() throws Exception {
        Configuration config = Configuration.current();
        Integer valueRead = config.get("tests.converter.integer.octal", Integer.class);
        assertThat(valueRead != null).isTrue();
        assertThat(Integer.decode("02").intValue()).isEqualTo(valueRead.intValue());
    }

    /**
     * Test conversion. The createValue are provided by
     * {@link org.apache.tamaya.core.internal.converters.ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Integer_Hex() throws Exception {
        Configuration config = Configuration.current();
        Integer valueRead = config.get("tests.converter.integer.hex.lowerX", Integer.class);
        assertThat(valueRead != null).isTrue();
        assertThat(Integer.decode("0x2F").intValue()).isEqualTo(valueRead.intValue());
        valueRead = config.get("tests.converter.integer.hex.upperX", Integer.class);
        assertThat(valueRead != null).isTrue();
        assertThat(Integer.decode("0X3F").intValue()).isEqualTo(valueRead.intValue());
    }

    /**
     * Test conversion. The createValue are provided by
     * {@link org.apache.tamaya.core.internal.converters.ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_NotPresent() throws Exception {
        Configuration config = Configuration.current();
        Integer valueRead = config.get("tests.converter.integer.foo", Integer.class);
        assertThat(valueRead != null).isFalse();
    }

    /**
     * Test conversion. The createValue are provided by
     * {@link ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Integer_MinValue() throws Exception {
        Configuration config = Configuration.current();
        Integer valueRead = config.get("tests.converter.integer.min", Integer.class);
        assertThat(valueRead != null).isTrue();
        assertThat(valueRead.intValue()).isEqualTo(Integer.MIN_VALUE);
    }

    /**
     * Test conversion. The createValue are provided by
     * {@link ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Integer_MaxValue() throws Exception {
        Configuration config = Configuration.current();
        Integer valueRead = config.get("tests.converter.integer.max", Integer.class);
        assertThat(valueRead != null).isTrue();
        assertThat(valueRead.intValue()).isEqualTo(Integer.MAX_VALUE);
    }
        
    @Test(expected = ConfigException.class)
    public void testConvert_IntegerInvalid() throws ConfigException {
        Configuration config = Configuration.current();
        config.get("tests.converter.integer.invalid", Integer.class);
    }

    @Test
    public void callToConvertAddsMoreSupportedFormatsToTheContext() throws Exception {
        ConversionContext context = new ConversionContext.Builder(TypeLiteral.of(Integer.class)).build();

        IntegerConverter converter = new IntegerConverter();
        converter.convert("", context);

        assertThat(context.getSupportedFormats()).contains("<int> (IntegerConverter)",
                "MIN_VALUE (IntegerConverter)", "MAX_VALUE (IntegerConverter)");
    }

    @Test
    public void testHashCode() {
        IntegerConverter instance = new IntegerConverter();
        assertThat(instance.hashCode()).isEqualTo(IntegerConverter.class.hashCode());
    }
}
