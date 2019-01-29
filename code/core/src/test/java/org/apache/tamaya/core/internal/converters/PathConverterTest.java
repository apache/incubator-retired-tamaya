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

import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.tamaya.TypeLiteral;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by atsti on 02.10.2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class PathConverterTest {

    @Mock
    ConversionContext context;

    @Test
    public void convert() throws Exception {
        PathConverter conv = new PathConverter();
        Path value = conv.convert("testRoot", context);
        assertThat(Paths.get("testRoot")).isEqualTo(value);
        value = conv.convert("foo", context);
        assertThat(value).isNotNull();
    }

    @Test
    public void convertNull() throws Exception {
        PathConverter conv = new PathConverter();
        Path value = conv.convert(null, context);
        assertThat(value).isNull();
        value = conv.convert("", context);
        assertThat(value).isNull();
    }
    
    @Test
    public void convertInvalidPath() throws Exception {
        PathConverter conv = new PathConverter();
        Path value = conv.convert("/invalid:/\u0000", context);
        assertThat(value).isNull();
    }

    @Test
    public void equalsAndHashcode() throws Exception {
        PathConverter conv1 = new PathConverter();
        PathConverter conv2 = new PathConverter();
        assertThat(conv2).isEqualTo(conv1);
        assertThat(conv2).isNotEqualTo(new ShortConverter());
        assertThat(conv2).isNotEqualTo(null);
        assertThat(conv2.hashCode()).isEqualTo(conv1.hashCode());
    }

    @Test
    public void callToConvertAddsMoreSupportedFormatsToTheContext() throws Exception {
        ConversionContext context = new ConversionContext.Builder(TypeLiteral.of(Path.class)).build();

        PathConverter converter = new PathConverter();
        converter.convert("notempty", context);

        assertThat(context.getSupportedFormats()).contains("<File> (PathConverter)");
    }

    @Test
    public void testHashCode() {
        PathConverter instance = new PathConverter();
        assertThat(instance.hashCode()).isEqualTo(PathConverter.class.hashCode());
    }

}
