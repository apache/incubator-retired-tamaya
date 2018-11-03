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

import java.time.Duration;
import org.apache.tamaya.TypeLiteral;

import static org.assertj.core.api.Assertions.*;

/**
 * Created by atsti on 02.10.2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class DurationConverterTest {

    @Mock
    ConversionContext context;

    /**
     * Examples:
     * <pre>
     *    "PT20.345S" -- parses as "20.345 seconds"
     *    "PT15M"     -- parses as "15 minutes" (where a minute is 60 seconds)
     *    "PT10H"     -- parses as "10 hours" (where an hour is 3600 seconds)
     *    "P2D"       -- parses as "2 days" (where a day is 24 hours or 86400 seconds)
     *    "P2DT3H4M"  -- parses as "2 days, 3 hours and 4 minutes"
     *    "P-6H3M"    -- parses as "-6 hours and +3 minutes"
     *    "-P6H3M"    -- parses as "-6 hours and -3 minutes"
     *    "-P-6H+3M"  -- parses as "+6 hours and -3 minutes"
     * </pre>
     * @throws Exception
     */
    @Test
    public void convert() throws Exception {
        DurationConverter conv = new DurationConverter();
        Duration duration = conv.convert("PT20.345S", context);
        assertThat(duration).isEqualTo(Duration.parse("PT20.345S"));
        duration = conv.convert("PT15M", context);
        assertThat(duration).isEqualTo(Duration.parse("PT15M"));
        duration = conv.convert("PT10H", context);
        assertThat(duration).isEqualTo(Duration.parse("PT10H"));
        duration = conv.convert("P2D", context);
        assertThat(duration).isEqualTo(Duration.parse("P2D"));
        duration = conv.convert("P2DT3H4M", context);
        assertThat(duration).isEqualTo(Duration.parse("P2DT3H4M"));
        duration = conv.convert("foo", context);
        assertThat(duration).isNull();
    }

    @Test
    public void equalsAndHashcode() throws Exception {
        DurationConverter conv1 = new DurationConverter();
        DurationConverter conv2 = new DurationConverter();
        assertThat(conv2).isEqualTo(conv1);
        assertThat(conv2.hashCode()).isEqualTo(conv1.hashCode());
    }

    @Test
    public void callToConvertAddsMoreSupportedFormatsToTheContext() throws Exception {
        ConversionContext context = new ConversionContext.Builder(TypeLiteral.of(Duration.class)).build();

        DurationConverter converter = new DurationConverter();
        converter.convert("", context);


        assertThat(context.getSupportedFormats().contains("PT20M34S (DurationConverter)")).isTrue();
    }

    @Test
    public void testHashCode() {
        DurationConverter instance = new DurationConverter();
        assertThat(instance.hashCode()).isEqualTo(DurationConverter.class.hashCode());
    }

    @Test
    public void testEquality() {
        DurationConverter converter = new DurationConverter();

        assertThat(converter).isEqualTo(new DurationConverter());
        assertThat(converter).isNotEqualTo(new FileConverter());
        assertThat(converter).isNotEqualTo(null);
    }
}
