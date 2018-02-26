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
import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests the default converter for Shorts.
 */
public class ShortConverterTest {

    /**
     * Test conversion. The values are provided by
     * {@link ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Short_Decimal() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Short valueRead = config.get("tests.converter.short.decimal", Short.class);
        assertThat(valueRead != null).isTrue();
        assertThat(101).isEqualTo(valueRead.intValue());
    }

    /**
     * Test conversion. The values are provided by
     * {@link ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Short_Octal() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Short valueRead = config.get("tests.converter.short.octal", Short.class);
        assertThat(valueRead != null).isTrue();
        assertThat(Short.decode("02").intValue()).isEqualTo(valueRead.intValue());
    }

    /**
     * Test conversion. The values are provided by
     * {@link ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Short_Hex() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Short valueRead = config.get("tests.converter.short.hex.lowerX", Short.class);
        assertThat(valueRead != null).isTrue();
        assertThat(Short.decode("0x2F").intValue()).isEqualTo(valueRead.intValue());
        valueRead = config.get("tests.converter.short.hex.upperX", Short.class);
        assertThat(valueRead != null).isTrue();
        assertThat(Short.decode("0X3F").intValue()).isEqualTo(valueRead.intValue());
    }

    /**
     * Test conversion. The value are provided by
     * {@link ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_NotPresent() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Short valueRead = config.get("tests.converter.short.foo", Short.class);
        assertThat(valueRead != null).isFalse();
    }

    /**
     * Test conversion. The values are provided by
     * {@link org.apache.tamaya.core.internal.converters.ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Short_MinValue() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Short valueRead = config.get("tests.converter.short.min", Short.class);
        assertThat(valueRead != null).isTrue();
        assertThat(valueRead.intValue()).isEqualTo(Short.MIN_VALUE);
    }

    /**
     * Test conversion. The values are provided by
     * {@link org.apache.tamaya.core.internal.converters.ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Short_MaxValue() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Short valueRead = config.get("tests.converter.short.max", Short.class);
        assertThat(valueRead != null).isTrue();
        assertThat(valueRead.intValue()).isEqualTo(Short.MAX_VALUE);
    }
    
        
    @Test(expected = ConfigException.class)
    public void testConvert_ShortInvalid() throws ConfigException {
        Configuration config = ConfigurationProvider.getConfiguration();
        config.get("tests.converter.short.invalid", Short.class);
    }

    @Test
    public void callToConvertAddsMoreSupportedFormatsToTheContext() throws Exception {
        ConversionContext context = new ConversionContext.Builder(TypeLiteral.of(Short.class)).build();
        ShortConverter converter = new ShortConverter();
        converter.convert("", context);

        assertThat(context.getSupportedFormats().contains("short (ShortConverter)")).isTrue();
        assertThat(context.getSupportedFormats().contains("MIN_VALUE (ShortConverter)")).isTrue();
        assertThat(context.getSupportedFormats().contains("MAX_VALUE (ShortConverter)")).isTrue();
    }

    @Test
    public void testHashCode() {
        ShortConverter instance = new ShortConverter();
        assertThat(instance.hashCode()).isEqualTo(ShortConverter.class.hashCode());
    }
}