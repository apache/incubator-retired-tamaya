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
package org.apache.tamaya.spisupport;

import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.spi.ConversionContext;
import org.junit.Test;

import java.math.RoundingMode;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class testing the {@link EnumConverter} class.
 */
public class EnumConverterTest {

    private final EnumConverter<RoundingMode> testConverter = new EnumConverter<>(RoundingMode.class);

    private final ConversionContext DUMMY_CONTEXT = new ConversionContext.Builder("someKey", TypeLiteral.of(Enum.class))
            .build();

    private enum TEST_ENUM {
        A, B, C, D
    };

    @Test
    public void testConversionWithMixedCasing() {
        for (String input : Arrays.asList(RoundingMode.CEILING.toString(), "ceiling", "CeiLinG")) {
            assertThat(RoundingMode.CEILING).isEqualTo(testConverter.convert(input, DUMMY_CONTEXT));
        }
    }

    @Test
    public void testConvert_OtherValue() {
        assertThat(testConverter.convert("fooBars", DUMMY_CONTEXT)).isNull();
    }

    @Test
    public void callToConvertAddsMoreSupportedFormatsToTheContext() throws Exception {
        ConversionContext context = new ConversionContext.Builder("someKey", TypeLiteral.of(Enum.class)).build();
        EnumConverter<RoundingMode> converter = new EnumConverter<>(RoundingMode.class);
        converter.convert("fooBars", context);

        assertThat(context.getSupportedFormats().contains("<enumValue> (EnumConverter)")).isTrue();
    }

    @Test
    public void testEqualsAndHash() {
        EnumConverter converter1 = new EnumConverter<>(RoundingMode.class);
        EnumConverter converter2 = new EnumConverter<>(RoundingMode.class);
        EnumConverter converter3 = new EnumConverter<>(TEST_ENUM.class);

        assertThat(converter1).isEqualTo(converter1);
        assertThat(converter1).isNotEqualTo(null);
        assertThat(converter1).isNotEqualTo("aString");
        assertThat("aString").isNotEqualTo(converter1);
        assertThat(converter2).isEqualTo(converter1);
        assertThat(converter1).isNotEqualTo(converter3);
        assertThat(converter2.hashCode()).isEqualTo(converter1.hashCode());
        assertThat(converter1.hashCode()).isNotEqualTo(converter3.hashCode());
    }
}
