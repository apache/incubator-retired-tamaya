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

import org.apache.tamaya.ConfigurationProvider;
import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.spi.ConversionContext;
import org.junit.Test;

import java.math.RoundingMode;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Test class testing the {@link EnumConverter} class.
 */
public class EnumConverterTest {

    private final EnumConverter<RoundingMode> testConverter = new EnumConverter<>(RoundingMode.class);

    private final ConversionContext DUMMY_CONTEXT = new ConversionContext.Builder("someKey", TypeLiteral.of(Enum.class)).build();

    @Test
    public void testConvert() {
        assertEquals(testConverter.convert(RoundingMode.CEILING.toString(),
                DUMMY_CONTEXT), RoundingMode.CEILING);
    }

    @Test
    public void testConvert_LowerCase() {
        assertEquals(testConverter.convert("ceiling", DUMMY_CONTEXT), RoundingMode.CEILING);
    }

    @Test
    public void testConvert_MixedCase()  {
        assertEquals(testConverter.convert("CeiLinG", DUMMY_CONTEXT), RoundingMode.CEILING);
    }

    @Test
    public void testConvert_OtherValue() {
        assertNull(testConverter.convert("fooBars", DUMMY_CONTEXT));
    }
}