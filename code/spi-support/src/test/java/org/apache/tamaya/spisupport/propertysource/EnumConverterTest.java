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
package org.apache.tamaya.spisupport.propertysource;

import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.spi.ConversionContext;
import org.apache.tamaya.spisupport.EnumConverter;
import org.junit.Test;

import java.math.RoundingMode;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class testing the {@link EnumConverter} class.
 */
public class EnumConverterTest {

    private final EnumConverter testConverter = new EnumConverter(RoundingMode.class);

    private final ConversionContext DUMMY_CONTEXT = new ConversionContext.Builder("someKey", TypeLiteral.of(Enum.class)).build();

    @Test
    public void testConvert() {
        assertThat(testConverter.convert(RoundingMode.CEILING.toString(), DUMMY_CONTEXT)).isEqualTo(RoundingMode.CEILING);
    }

    @Test
    public void testConvert_LowerCase() {
        assertThat(RoundingMode.CEILING).isEqualTo(testConverter.convert("ceiling", DUMMY_CONTEXT));
    }

    @Test
    public void testConvert_MixedCase()  {
        assertThat(RoundingMode.CEILING).isEqualTo(testConverter.convert("CeiLinG", DUMMY_CONTEXT));
    }

    @Test
    public void testConvert_OtherValue() {
        assertThat(testConverter.convert("fooBars", DUMMY_CONTEXT)).isNull();
    }
}