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

import org.apache.tamaya.base.convert.ConversionContext;
import org.apache.tamaya.core.converters.OptionalConverter;
import org.junit.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class OptionalConverterTest {

    @Test
    public void nullConversionYieldsEmptyOptional() {
        final Optional<?> result = new OptionalConverter().convert(null);
        assertThat(result).isNotNull();
        assertThat(result.isPresent()).isFalse();
    }

    @Test(expected = IllegalStateException.class)
    public void emulateExceptionWhenGivenConfigIsNull() {
        ConversionContext ctx = new ConversionContext.Builder("someKey", String.class).build();
        ConversionContext.setContext(ctx);
        new OptionalConverter().convert("JustATestValueThatIsIgnored");
    }

}
