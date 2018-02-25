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

import java.util.List;
import org.apache.tamaya.ConfigException;
import org.junit.Test;

import java.util.Optional;
import org.apache.tamaya.ConfigurationProvider;
import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.spi.ConversionContext;
import static org.junit.Assert.*;

public class OptionalConverterTest {

    @Test
    public void nullConversionYieldsEmptyOptional() {
        final Optional<?> result = new OptionalConverter().convert(null, null);
        assertNotNull(result);
        assertFalse(result.isPresent());
    }

    @Test(expected = ConfigException.class)
    public void emulateExceptionWhenGivenContextIsNull() {
        new OptionalConverter().convert("JustATestValueThatIsIgnored", null);
    }

    @Test
    public void testOptionalString() {
        TypeLiteral<List<String>> listOfStringTypeLiteral = new TypeLiteral<List<String>>() {
        };
        ConversionContext ctx = new ConversionContext.Builder("testOptionalString", listOfStringTypeLiteral).build();

        final Optional<String> result = new OptionalConverter().convert("astring", ctx);
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals("astring", result.get());
    }

    @Test
    public void testOptionalInteger() {
        TypeLiteral<List<Integer>> listOfIntegerTypeLiteral = new TypeLiteral<List<Integer>>() {
        };
        ConversionContext ctx = new ConversionContext.Builder("testOptionalInteger", listOfIntegerTypeLiteral)
                .setConfiguration(ConfigurationProvider.getConfiguration())
                .build();

        final Optional<Integer> result = new OptionalConverter().convert("11", ctx);
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(11, result.get().intValue());
    }
    
    
    @Test
    public void testHashCode() {
        OptionalConverter instance = new OptionalConverter();
        assertEquals(OptionalConverter.class.hashCode(), instance.hashCode());
    }
}
