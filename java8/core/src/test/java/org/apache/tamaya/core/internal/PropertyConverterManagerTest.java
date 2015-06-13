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
package org.apache.tamaya.core.internal;


import org.apache.tamaya.spi.PropertyConverter;
import org.apache.tamaya.TypeLiteral;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

public class PropertyConverterManagerTest {
    @Test
    public void customTypeWithFactoryMethodOfIsRecognizedAsSupported() {
        PropertyConverterManager manager = new PropertyConverterManager();

        assertThat(manager.isTargetTypeSupported(TypeLiteral.of(MyType.class)),
                   is(true));
    }

    @Test
    public void factoryMethodOfIsUsedAsConverter() {
        PropertyConverterManager manager = new PropertyConverterManager();

        List<PropertyConverter<Object>> converters = manager.getPropertyConverters(TypeLiteral.of(MyType.class));

        assertThat(converters, hasSize(1));

        PropertyConverter<Object> converter = converters.get(0);

        Object result = converter.convert("IN");

        assertThat(result, notNullValue());
        assertThat(result, instanceOf(MyType.class));
        assertThat(((MyType)result).getValue(), equalTo("IN"));
    }

    public static class MyType {
        private String typeValue;

        private MyType(String value) {
            typeValue = value;
        }

        public static MyType of(String source) {
            return new MyType(source);
        }

        public String getValue() {
            return typeValue;
        }

    }

    @Test
    public void testDirectConverterMapping(){
        PropertyConverterManager manager = new PropertyConverterManager();
        List<PropertyConverter<C>> converters = manager.getPropertyConverters(TypeLiteral.of(C.class));
        assertThat(converters, hasSize(1));

        PropertyConverter<C> converter = converters.get(0);
        C result = converter.convert("testDirectConverterMapping");

        assertThat(result, notNullValue());
        assertThat(result, instanceOf(C.class));
        assertThat(((C)result).getInValue(), equalTo("testDirectConverterMapping"));
    }

    @Test
    public void testDirectSuperclassConverterMapping(){
        PropertyConverterManager manager = new PropertyConverterManager();
        List<PropertyConverter<B>> converters = manager.getPropertyConverters(TypeLiteral.of(B.class));
        assertThat(converters, hasSize(1));

        PropertyConverter<B> converter = converters.get(0);
        B result = converter.convert("testDirectSuperclassConverterMapping");

        assertThat(result, notNullValue());
        assertThat(result, instanceOf(C.class));
        assertThat(((C)result).getInValue(), equalTo("testDirectSuperclassConverterMapping"));
    }

    @Test
    public void testTransitiveSuperclassConverterMapping(){
        PropertyConverterManager manager = new PropertyConverterManager();
        List<PropertyConverter<A>> converters = manager.getPropertyConverters(TypeLiteral.of(A.class));
        assertThat(converters, hasSize(1));

        PropertyConverter<A> converter = converters.get(0);
        A result = converter.convert("testTransitiveSuperclassConverterMapping");

        assertThat(result, notNullValue());
        assertThat(result, instanceOf(C.class));
        assertThat(((C)result).getInValue(), equalTo("testTransitiveSuperclassConverterMapping"));
    }

    @Test
    public void testDirectInterfaceMapping(){
        PropertyConverterManager manager = new PropertyConverterManager();
        List<PropertyConverter<Readable>> converters = manager.getPropertyConverters(TypeLiteral.of(Readable.class));
        assertThat(converters, hasSize(1));

        PropertyConverter<Readable> converter = converters.get(0);
        Readable result = converter.convert("testDirectInterfaceMapping");

        assertThat(result, notNullValue());
        assertThat(result, instanceOf(C.class));
        assertThat(((C)result).getInValue(), equalTo("testDirectInterfaceMapping"));
    }

    @Test
    public void testTransitiveInterfaceMapping1(){
        PropertyConverterManager manager = new PropertyConverterManager();
        List<PropertyConverter<Runnable>> converters = manager.getPropertyConverters(TypeLiteral.of(Runnable.class));
        assertThat(converters, hasSize(1));

        PropertyConverter<Runnable> converter = converters.get(0);
        Runnable result = converter.convert("testTransitiveInterfaceMapping1");

        assertThat(result, notNullValue());
        assertThat(result, instanceOf(C.class));
        assertThat(((C)result).getInValue(), equalTo("testTransitiveInterfaceMapping1"));
    }

    @Test
    public void testTransitiveInterfaceMapping2(){
        PropertyConverterManager manager = new PropertyConverterManager();
        List<PropertyConverter<AutoCloseable>> converters = manager.getPropertyConverters(TypeLiteral.of(AutoCloseable.class));
        assertThat(converters, hasSize(1));

        PropertyConverter<AutoCloseable> converter = converters.get(0);
        AutoCloseable result = converter.convert("testTransitiveInterfaceMapping2");

        assertThat(result, notNullValue());
        assertThat(result, instanceOf(C.class));
        assertThat(((C)result).getInValue(), equalTo("testTransitiveInterfaceMapping2"));
    }

}