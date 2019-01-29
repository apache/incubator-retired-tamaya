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

import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.spi.ConversionContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

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
        assertThat(LocalDateTime.parse("2007-12-03T10:15:30")).isEqualTo(value);
        value = conv.convert("foo", context);
        assertThat(value).isNull();
    }

    @Test
    public void equalsAndHashcode() throws Exception {
        LocalDateTimeConverter conv1 = new LocalDateTimeConverter();
        LocalDateTimeConverter conv2 = new LocalDateTimeConverter();
        assertThat(conv2).isEqualTo(conv1);
        assertThat(conv2.hashCode()).isEqualTo(conv1.hashCode());
        assertThat(conv2).isNotEqualTo(new LocalTimeConverter());
        assertThat(conv2).isNotEqualTo(null);
    }

    @Test
    public void callToConvertAddsMoreSupportedFormatsToTheContext() throws Exception {
        ConversionContext context = new ConversionContext.Builder(TypeLiteral.of(LocalDateTime.class)).build();

        LocalDateTimeConverter converter = new LocalDateTimeConverter();
        converter.convert("", context);

        assertThat(context.getSupportedFormats().toString()).contains(" (LocalDateTimeConverter)");
    }

    @Test
    public void testHashCode() {
        LocalDateTimeConverter instance = new LocalDateTimeConverter();
        assertThat(instance.hashCode()).isEqualTo(LocalDateTimeConverter.class.hashCode());
    }
}
