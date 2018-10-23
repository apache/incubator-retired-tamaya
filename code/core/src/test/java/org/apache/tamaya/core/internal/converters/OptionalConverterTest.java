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
import org.apache.tamaya.Configuration;
import org.junit.Test;

import java.util.Optional;

import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.spi.ConversionContext;
import static org.assertj.core.api.Assertions.*;

public class OptionalConverterTest {

    @Test
    public void nullConversionYieldsEmptyOptional() {
        final Optional<?> result = new OptionalConverter().convert(null);
        assertThat(result).isNotNull();
        assertThat(result.isPresent()).isFalse();
    }

    @Test(expected = ConfigException.class)
    public void emulateExceptionWhenGivenContextIsNull() {
        new OptionalConverter().convert("JustATestValueThatIsIgnored");
    }

    @Test
    public void testOptionalString() {
        TypeLiteral<List<String>> listOfStringTypeLiteral = new TypeLiteral<List<String>>() {
        };
        ConversionContext context = new ConversionContext.Builder("testOptionalString", listOfStringTypeLiteral).build();
        ConversionContext.set(context);
        final Optional<String> result = new OptionalConverter().convert("astring");
        ConversionContext.reset();

        assertThat(result).isNotNull();
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get()).isEqualTo("astring");
    }

    @Test
    public void testOptionalInteger() {
        TypeLiteral<List<Integer>> listOfIntegerTypeLiteral = new TypeLiteral<List<Integer>>() {
        };
        ConversionContext context = new ConversionContext.Builder("testOptionalInteger", listOfIntegerTypeLiteral)
                .setConfiguration(Configuration.current())
                .build();
        ConversionContext.set(context);

        final Optional<Integer> result = new OptionalConverter().convert("11");
        ConversionContext.reset();

        assertThat(result).isNotNull();
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().intValue()).isEqualTo(11);
    }
    
    
    @Test
    public void testHashCode() {
        OptionalConverter instance = new OptionalConverter();
        assertThat(instance.hashCode()).isEqualTo(OptionalConverter.class.hashCode());
    }

    @Test
    public void testEquality() {
        OptionalConverter converter = new OptionalConverter();

        assertThat(converter).isEqualTo(new OptionalConverter());
        assertThat(converter).isNotEqualTo(new PathConverter());
        assertThat(converter).isNotEqualTo(null);
    }
}
