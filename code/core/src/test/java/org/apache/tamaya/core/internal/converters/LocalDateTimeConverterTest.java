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

import org.apache.tamaya.spi.ConversionContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDateTime;
import org.apache.tamaya.TypeLiteral;

import static org.junit.Assert.*;

/**
 * Created by atsti on 02.10.2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class LocalDateTimeConverterTest {

    @Mock
    ConversionContext context;

    @Test
    public void convert() throws Exception {
        LocalDateTimeConverter conv = new LocalDateTimeConverter();
        LocalDateTime value = conv.convert("2007-12-03T10:15:30", context);
        assertEquals(value, LocalDateTime.parse("2007-12-03T10:15:30"));
        value = conv.convert("foo", context);
        assertNull(value);
    }

    @Test
    public void equalsAndHashcode() throws Exception {
        LocalDateTimeConverter conv1 = new LocalDateTimeConverter();
        LocalDateTimeConverter conv2 = new LocalDateTimeConverter();
        assertEquals(conv1, conv2);
        assertEquals(conv1.hashCode(), conv2.hashCode());
    }

    @Test
    public void callToConvertAddsMoreSupportedFormatsToTheContext() throws Exception {
        ConversionContext localcontext = new ConversionContext.Builder(TypeLiteral.of(LocalDateTime.class)).build();
        LocalDateTimeConverter converter = new LocalDateTimeConverter();
        converter.convert("", localcontext);

        assertTrue(localcontext.getSupportedFormats().toString().contains(" (LocalDateTimeConverter)"));
    }

    @Test
    public void testHashCode() {
        LocalDateTimeConverter instance = new LocalDateTimeConverter();
        assertEquals(LocalDateTimeConverter.class.hashCode(), instance.hashCode());
    }
}