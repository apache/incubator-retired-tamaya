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

import java.util.Currency;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

/**
 * Tests the default converter for currencies.
 */
public class CurrencyConverterTest {

    private static final String BGL = "BGL";
	private static final String CHF = "CHF";
	private static final String EUR = "EUR";

	/**
     * Test conversion. The values are provided by
     * {@link org.apache.tamaya.core.internal.converters.ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Currency_Code_CHF() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Currency valueRead = config.get("tests.converter.currency.code1", Currency.class);
        assertThat(valueRead).isNotNull();
        assertEquals(valueRead, Currency.getInstance(CHF));
    }

    /**
     * Test conversion. The values are provided by
     * {@link org.apache.tamaya.core.internal.converters.ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Currency_Code_CHF1() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Currency valueRead = config.get("tests.converter.currency.code2", Currency.class);
        assertThat(valueRead).isNotNull();
        assertEquals(valueRead, Currency.getInstance(CHF));
    }

    /**
     * Test conversion. The values are provided by
     * {@link org.apache.tamaya.core.internal.converters.ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Currency_Code_CHF_Whitespace_Before() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Currency valueRead = config.get("tests.converter.currency.code3", Currency.class);
        assertThat(valueRead).isNotNull();
        assertEquals(Currency.getInstance(CHF), valueRead);
    }

    /**
     * Test conversion. The values are provided by
     * {@link org.apache.tamaya.core.internal.converters.ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Currency_Code_CHF_Whitespace_After() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Currency valueRead = config.get("tests.converter.currency.code4", Currency.class);
        assertThat(valueRead).isNotNull();
        assertEquals(Currency.getInstance(CHF), valueRead);
    }

    /**
     * Test conversion. The values are provided by
     * {@link org.apache.tamaya.core.internal.converters.ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Currency_Code_CHF_Whitespace_Around() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Currency valueRead = config.get("tests.converter.currency.code5", Currency.class);
        assertThat(valueRead).isNotNull();
        assertEquals(Currency.getInstance(CHF), valueRead);
    }

    /**
     * Test conversion. The values are provided by
     * {@link org.apache.tamaya.core.internal.converters.ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Currency_Code_Numeric() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Currency valueRead = config.get("tests.converter.currency.code-numeric1", Currency.class);
        assertThat(valueRead).isNotNull();
        assertEquals(Currency.getInstance(BGL).getNumericCode(), valueRead.getNumericCode());
        valueRead = config.get("tests.converter.currency.code-numeric2", Currency.class);
        assertThat(valueRead).isNotNull();
        assertEquals(Currency.getInstance(BGL).getNumericCode(), valueRead.getNumericCode());
        valueRead = config.get("tests.converter.currency.code-numeric3", Currency.class);
        assertThat(valueRead).isNotNull();
        assertEquals(Currency.getInstance(BGL).getNumericCode(), valueRead.getNumericCode());
        valueRead = config.get("tests.converter.currency.code-numeric4", Currency.class);
        assertThat(valueRead).isNotNull();
        assertEquals(Currency.getInstance(BGL).getNumericCode(), valueRead.getNumericCode());
    }

    /**
     * Test conversion. The values are provided by
     * {@link org.apache.tamaya.core.internal.converters.ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Currency_Code_Locale() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Currency valueRead = config.get("tests.converter.currency.code-locale1", Currency.class);
        assertThat(valueRead).isNotNull();
        assertEquals(EUR, valueRead.getCurrencyCode());
        valueRead = config.get("tests.converter.currency.code-locale2", Currency.class);
        assertThat(valueRead).isNotNull();
        assertEquals(EUR, valueRead.getCurrencyCode());
        valueRead = config.get("tests.converter.currency.code-locale3", Currency.class);
        assertThat(valueRead).isNotNull();
        assertEquals(EUR, valueRead.getCurrencyCode());
        valueRead = config.get("tests.converter.currency.code-locale4", Currency.class);
        assertThat(valueRead).isNotNull();
        assertEquals(EUR, valueRead.getCurrencyCode());
    }

    /**
     * Test conversion. The values are provided by
     * {@link org.apache.tamaya.core.internal.converters.ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_NotPresent() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Byte valueRead = config.get("tests.converter.byte.foo", Byte.class);
        assertThat(valueRead).isNull();
    }
}