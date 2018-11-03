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

import org.apache.tamaya.ConfigException;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests conversion of the {@link URI}-converter.
 */
public class URIConverterTest {

    ConversionContext context = new ConversionContext.Builder(TypeLiteral.of(URI.class))
            .build();

    @Test
    public void testConvert_URI() throws Exception {
        URIConverter converter = new URIConverter();
         assertThat(new URI("test:path")).isEqualTo(converter.convert("test:path", context));
    }

    @Test
    public void testConvert_URI_WithSpaces() throws Exception {
        URIConverter converter = new URIConverter();
        assertThat(new URI("test:path")).isEqualTo(converter.convert("  test:path\t", context));
    }

    @Test
    public void testConvert_URI_WithSpacesBefore() throws Exception {
        URIConverter converter = new URIConverter();
        assertThat(new URI("test:path")).isEqualTo(converter.convert("  test:path", context));
    }

    @Test
    public void testConvert_URI_WithSpacesAfter() throws Exception {
        URIConverter converter = new URIConverter();
        assertThat(new URI("test:path")).isEqualTo(converter.convert("test:path  ", context));
    }

    @Test
    public void testConvert_NotPresent() throws Exception {
        URIConverter converter = new URIConverter();
        assertThat(converter.convert("", context)).isNull();
        assertThat(converter.convert(null, context)).isNull();
    }
    
    @Test
    public void testConvert_URIInvalid() throws ConfigException {
        URIConverter converter = new URIConverter();
        assertThat(converter.convert("not a uri", context)).isNull();
    }

    @Test
    public void callToConvertAddsMoreSupportedFormatsToTheContext() throws Exception {
        ConversionContext context = new ConversionContext.Builder(TypeLiteral.of(URI.class)).build();

        URIConverter converter = new URIConverter();
        converter.convert("test:path", context);


        assertThat(context.getSupportedFormats().contains("<uri> -> new URI(uri) (URIConverter)")).isTrue();
    }

    @Test
    public void testHashCode() {
        URIConverter instance = new URIConverter();
        assertThat(instance.hashCode()).isEqualTo(URIConverter.class.hashCode());
    }

    @Test
    public void testEquality() {
        URIConverter converter = new URIConverter();

        assertThat(converter).isEqualTo(new URIConverter());
        assertThat(converter).isNotEqualTo(new URLConverter());
        assertThat(converter).isNotEqualTo(null);
    }
}
