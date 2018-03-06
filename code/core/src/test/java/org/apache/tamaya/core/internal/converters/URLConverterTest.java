/*
 * Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.apache.tamaya.core.internal.converters;

import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.spi.ConversionContext;
import org.junit.Test;

import java.net.URI;
import java.net.URL;

import org.apache.tamaya.ConfigException;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests conversion of the {@link URL}-converter.
 */
public class URLConverterTest {

    ConversionContext context = new ConversionContext.Builder(TypeLiteral.of(URI.class))
            .build();

    @Test
    public void testConvert_URL() throws Exception {
        URLConverter converter = new URLConverter();
        assertThat(new URL("http://apache.org:4000/path")).isEqualTo(converter.convert("http://apache.org:4000/path", context));
    }

    @Test
    public void testConvert_URL_WithSpaces() throws Exception {
        URLConverter converter = new URLConverter();
        assertThat(new URL("http://apache.org:4000/path")).isEqualTo(converter.convert("  http://apache.org:4000/path\t", context));
    }

    @Test
    public void testConvert_URL_WithSpacesBefore() throws Exception {
        URLConverter converter = new URLConverter();
        assertThat(new URL("http://apache.org:4000/path")).isEqualTo(converter.convert("  http://apache.org:4000/path", context));
    }

    @Test
    public void testConvert_URL_WithSpacesAfter() throws Exception {
        URLConverter converter = new URLConverter();
        assertThat(new URL("http://apache.org:4000/path")).isEqualTo(converter.convert("http://apache.org:4000/path  ", context));
    }

    @Test
    public void testConvert_NotPresent() throws Exception {
        URLConverter converter = new URLConverter();
        assertThat(converter.convert("", context)).isNull();
        assertThat(converter.convert(null, context)).isNull();
    }
    
    @Test
    public void testConvert_URLInvalid() throws ConfigException {
        URLConverter converter = new URLConverter();
        assertThat(converter.convert("not a url", context)).isNull();
    }

    @Test
    public void callToConvertAddsMoreSupportedFormatsToTheContext() throws Exception {
        ConversionContext localcontext = new ConversionContext.Builder(TypeLiteral.of(URL.class)).build();
        URLConverter converter = new URLConverter();
        converter.convert("http://localhost", localcontext);

        assertThat(localcontext.getSupportedFormats().contains("<URL> (URLConverter)")).isTrue();
    }

    @Test
    public void testHashCode() {
        URLConverter instance = new URLConverter();
        assertThat(instance.hashCode()).isEqualTo(URLConverter.class.hashCode());
    }

    @Test
    public void testEquality() {
        URLConverter converter = new URLConverter();

        assertThat(converter).isEqualTo(new URLConverter());
        assertThat(converter).isNotEqualTo(new BigDecimalConverter());
        assertThat(converter).isNotEqualTo(null);
    }
}
