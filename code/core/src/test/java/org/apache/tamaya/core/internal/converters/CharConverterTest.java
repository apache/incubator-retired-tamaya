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

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;

/**
 * Tests conversion of the {@link CharConverter}.
 */
public class CharConverterTest {

    @Test
    public void testConvert_Character() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Character valueRead = config.get("tests.converter.char.f", Character.class);
        assertThat(valueRead).isNotNull();
        assertEquals(valueRead.charValue(), 'f');
    }

    @Test
    public void testConvert_Character_Numeric() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Character valueRead = config.get("tests.converter.char.f-numeric", Character.class);
        assertThat(valueRead).isNotNull();
        assertEquals(valueRead.charValue(), (char) 101);
    }

    @Test
    public void testConvert_Character_Quoted() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Character valueRead = config.get("tests.converter.char.d", Character.class);
        assertThat(valueRead).isNotNull();
        assertEquals(valueRead.charValue(), 'd');

    }

    @Test
    public void testConvert_Character_SingleQuote() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Character valueRead = config.get("tests.converter.char.single-quote", Character.class);
        assertThat(valueRead).isNotNull();
        assertEquals(valueRead.charValue(), '\'');
    }

    @Test(expected = ConfigException.class)
    public void testConvert_Character_TwoSingleQuotes() throws ConfigException {
        Configuration config = ConfigurationProvider.getConfiguration();
        config.get("tests.converter.char.two-single-quotes", Character.class);
    }

    @Test
    public void testConvert_Character_ThreeSingleQuotes() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Character valueRead = config.get("tests.converter.char.three-single-quotes", Character.class);
        assertThat(valueRead).isNotNull();
        assertEquals(valueRead.charValue(), '\'');
    }

    @Test
    public void testConvert_Character_WithWhitespace_Before() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Character valueRead = config.get("tests.converter.char.f-before", Character.class);
        assertThat(valueRead).isNotNull();
        assertEquals(valueRead.charValue(), 'f');
    }

    @Test
    public void testConvert_Character_WithWhitespace_After() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Character valueRead = config.get("tests.converter.char.f-after", Character.class);
        assertThat(valueRead).isNotNull();
        assertEquals(valueRead.charValue(), 'f');
    }

    @Test
    public void testConvert_Character_WithWhitespace_Around() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Character valueRead = config.get("tests.converter.char.f-around", Character.class);
        assertThat(valueRead).isNotNull();
        assertEquals(valueRead.charValue(), 'f');
    }

    @Test
    public void testConvert_NotPresent() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Character valueRead = config.get("tests.converter.char.foo", Character.class);
        assertNull(valueRead);
    }

    /**
     * Test conversion. The value are provided by
     * {@link ConverterTestsPropertySource}.
     *
     * @throws ConfigException
     */
    @Test
    public void testConvert_CharString() throws ConfigException {
        Configuration config = ConfigurationProvider.getConfiguration();
        Character valueRead = config.get("tests.converter.char.invalid", Character.class);
        assertThat(valueRead).isNotNull();
        assertEquals(valueRead.charValue(), 'i'); //Strings return the first character
    }

    @Test
    public void testConvert_CharQuotedString() throws ConfigException {
        Configuration config = ConfigurationProvider.getConfiguration();
        Character valueRead = config.get("tests.converter.char.quoted-invalid", Character.class);
        assertThat(valueRead).isNotNull();
        assertEquals(valueRead.charValue(), 'i'); //Strings return the first character
    }

    @Test
    public void testConvert_CharUnicode() throws ConfigException {
        Configuration config = ConfigurationProvider.getConfiguration();
        Character valueRead = config.get("tests.converter.char.あ", Character.class);
        assertThat(valueRead).isNotNull();
        assertEquals(valueRead.charValue(), 'あ');
    }

    @Test
    public void testConvert_CharUnicodeString() throws ConfigException {
        Configuration config = ConfigurationProvider.getConfiguration();
        Character valueRead = config.get("tests.converter.char.กขฃคฅฆงจฉช", Character.class);
        assertThat(valueRead).isNotNull();
        assertEquals(valueRead.charValue(), 'ก'); //Strings return the first character
    }

    @Test
    public void callToConvertAddsMoreSupportedFormatsToTheContext() throws Exception {
        ConversionContext context = new ConversionContext.Builder(TypeLiteral.of(Character.class)).build();
        CharConverter converter = new CharConverter();
        converter.convert("", context);

        assertTrue(context.getSupportedFormats().contains("<char> (CharConverter)"));
        assertTrue(context.getSupportedFormats().contains("\\'<char>\\' (CharConverter)"));
    }

    @Test
    public void testHashCode() {
        CharConverter instance = new CharConverter();
        assertEquals(CharConverter.class.hashCode(), instance.hashCode());
    }
}
