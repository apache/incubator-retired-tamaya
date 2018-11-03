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
package org.apache.tamaya.spisupport;

import java.lang.reflect.Method;

import org.apache.tamaya.spi.ConversionContext;
import org.apache.tamaya.spi.PropertyConverter;
import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.spi.ServiceContext;
import org.apache.tamaya.spi.ServiceContextManager;
import org.junit.Test;

import java.math.RoundingMode;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class PropertyConverterManagerTest {

    private final ConversionContext DUMMY_CONTEXT = new ConversionContext.Builder(
            "someKey", TypeLiteral.of(Object.class)).build();

    @Test
    public void customTypeWithFactoryMethodOfIsRecognizedAsSupported() {
        ServiceContext serviceContext = ServiceContextManager.getServiceContext(getClass().getClassLoader());
        PropertyConverterManager manager = new PropertyConverterManager(serviceContext, true);

        assertThat(manager.isTargetTypeSupported(TypeLiteral.of(MyType.class)))
                .isTrue();
    }

    @Test
    public void factoryMethodOfIsUsedAsConverter() {
        ServiceContext serviceContext = ServiceContextManager.getServiceContext(getClass().getClassLoader());
        PropertyConverterManager manager = new PropertyConverterManager(serviceContext, true);

        List<PropertyConverter<MyType>> converters = manager.getPropertyConverters(
                (TypeLiteral) TypeLiteral.of(MyType.class));

        assertThat(converters).hasSize(1);

        PropertyConverter<MyType> converter = converters.get(0);

        ConversionContext ctx = new ConversionContext.Builder(TypeLiteral.of(String.class)).build();
        Object result = converter.convert("IN", ctx);

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(MyType.class);
        assertThat(((MyType) result).getValue()).isEqualTo("IN");
    }

    @Test
    public void testDirectConverterMapping() {
        ServiceContext serviceContext = ServiceContextManager.getServiceContext(getClass().getClassLoader());
        PropertyConverterManager manager = new PropertyConverterManager(serviceContext, true);
        assertThat(manager.isTargetTypeSupported(TypeLiteral.of(C.class))).isTrue();
        List<PropertyConverter<C>> converters = List.class.cast(manager.getPropertyConverters(TypeLiteral.of(C.class)));
        assertThat(converters).hasSize(1);

        PropertyConverter<C> converter = converters.get(0);
        C result = converter.convert("testDirectConverterMapping", null);

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(C.class);
        assertThat((result).getInValue()).isEqualTo("testDirectConverterMapping");
    }

    @Test
    public void testDirectSuperclassConverterMapping() {
        ServiceContext serviceContext = ServiceContextManager.getServiceContext(getClass().getClassLoader());
        PropertyConverterManager manager = new PropertyConverterManager(serviceContext, true);
        assertThat(manager.isTargetTypeSupported(TypeLiteral.of(B.class))).isTrue();
        List<PropertyConverter<B>> converters = List.class.cast(manager.getPropertyConverters(TypeLiteral.of(B.class)));
        assertThat(converters).hasSize(1);
        converters = List.class.cast(manager.getPropertyConverters(TypeLiteral.of(B.class)));
        assertThat(converters).hasSize(1);

        PropertyConverter<B> converter = converters.get(0);
        B result = converter.convert("testDirectSuperclassConverterMapping", null);

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(C.class);
        assertThat(((C) result).getInValue()).isEqualTo("testDirectSuperclassConverterMapping");
    }

    @Test
    public void testMultipleConverterLoad() {
        ServiceContext serviceContext = ServiceContextManager.getServiceContext(getClass().getClassLoader());
        PropertyConverterManager manager = new PropertyConverterManager(serviceContext, true);
        assertThat(manager.isTargetTypeSupported(TypeLiteral.of(B.class))).isTrue();
        List<PropertyConverter<B>> converters = List.class.cast(manager.getPropertyConverters(TypeLiteral.of(B.class)));
        assertThat(converters).hasSize(1);
        manager = new PropertyConverterManager(serviceContext, true);
        converters = List.class.cast(manager.getPropertyConverters(TypeLiteral.of(B.class)));
        assertThat(converters).hasSize(1);
    }

    @Test
    public void testTransitiveSuperclassConverterMapping() {
        ServiceContext serviceContext = ServiceContextManager.getServiceContext(getClass().getClassLoader());
        PropertyConverterManager manager = new PropertyConverterManager(serviceContext, true);
        assertThat(manager.isTargetTypeSupported(TypeLiteral.of(A.class))).isTrue();
        List<PropertyConverter<A>> converters = List.class.cast(manager.getPropertyConverters(TypeLiteral.of(A.class)));
        assertThat(converters).hasSize(1);

        PropertyConverter<A> converter = converters.get(0);
        A result = converter.convert("testTransitiveSuperclassConverterMapping", null);

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(C.class);
        assertThat(((C) result).getInValue()).isEqualTo("testTransitiveSuperclassConverterMapping");
    }

    @Test
    public void testDirectInterfaceMapping() {
        ServiceContext serviceContext = ServiceContextManager.getServiceContext(getClass().getClassLoader());
        PropertyConverterManager manager = new PropertyConverterManager(serviceContext, true);
        assertThat(manager.isTargetTypeSupported(TypeLiteral.of(Readable.class))).isTrue();
        List<PropertyConverter<Readable>> converters = List.class.cast(manager.getPropertyConverters(TypeLiteral.of(Readable.class)));
        assertThat(converters).hasSize(1);

        PropertyConverter<Readable> converter = converters.get(0);
        Readable result = converter.convert("testDirectInterfaceMapping", null);

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(C.class);
        assertThat(((C) result).getInValue()).isEqualTo("testDirectInterfaceMapping");
    }

    @Test
    public void testTransitiveInterfaceMapping1() {
        ServiceContext serviceContext = ServiceContextManager.getServiceContext(getClass().getClassLoader());
        PropertyConverterManager manager = new PropertyConverterManager(serviceContext, true);
        assertThat(manager.isTargetTypeSupported(TypeLiteral.of(Runnable.class))).isTrue();
        List<PropertyConverter<Runnable>> converters = List.class.cast(manager.getPropertyConverters(TypeLiteral.of(Runnable.class)));
        assertThat(converters).hasSize(1);

        PropertyConverter<Runnable> converter = converters.get(0);
        Runnable result = converter.convert("testTransitiveInterfaceMapping1", null);

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(C.class);
        assertThat(((C) result).getInValue()).isEqualTo("testTransitiveInterfaceMapping1");
    }

    @Test
    public void testTransitiveInterfaceMapping2() {
        ServiceContext serviceContext = ServiceContextManager.getServiceContext(getClass().getClassLoader());
        PropertyConverterManager manager = new PropertyConverterManager(serviceContext, true);
        assertThat(manager.isTargetTypeSupported(TypeLiteral.of(AutoCloseable.class))).isTrue();
        List<PropertyConverter<AutoCloseable>> converters = List.class.cast(manager.getPropertyConverters(TypeLiteral.of(AutoCloseable.class)));
        assertThat(converters).hasSize(1);

        PropertyConverter<AutoCloseable> converter = converters.get(0);
        AutoCloseable result = converter.convert("testTransitiveInterfaceMapping2", null);

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(C.class);
        assertThat(((C) result).getInValue()).isEqualTo("testTransitiveInterfaceMapping2");
    }

    @Test
    public void testBoxedConverterMapping() {
        ServiceContext serviceContext = ServiceContextManager.getServiceContext(getClass().getClassLoader());
        PropertyConverterManager manager = new PropertyConverterManager(serviceContext, true);
        assertThat(manager.isTargetTypeSupported(TypeLiteral.of(int.class))).isFalse();
        assertThat(manager.isTargetTypeSupported(TypeLiteral.of(Integer.class))).isTrue();
        List<PropertyConverter<Integer>> converters = List.class.cast(manager.getPropertyConverters(TypeLiteral.of(int.class)));
        assertThat(converters).hasSize(1);

        PropertyConverter<Integer> converter = converters.get(0);
        ConversionContext ctx = new ConversionContext.Builder(TypeLiteral.of(String.class)).build();
        Integer result = converter.convert("101", ctx);

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(Integer.class);
        assertThat(result).isEqualTo(101);
    }

    
    @Test
    public void testCreateEnumPropertyConverter() {
        ServiceContext serviceContext = ServiceContextManager.getServiceContext(getClass().getClassLoader());
        PropertyConverterManager manager = new PropertyConverterManager(serviceContext, false);
        PropertyConverter pc = manager.createDefaultPropertyConverter(TypeLiteral.of(MyEnum.class));
        assertThat(pc instanceof EnumConverter).isTrue();
        assertThat(manager.isTargetTypeSupported(TypeLiteral.of(MyEnum.class))).isTrue();
    }

    @Test
    public void testGetFactoryMethod() throws Exception {
        ServiceContext serviceContext = ServiceContextManager.getServiceContext(getClass().getClassLoader());
        PropertyConverterManager manager = new PropertyConverterManager(serviceContext, false);
        Method getFactoryMethod = PropertyConverterManager.class.getDeclaredMethod("getFactoryMethod", new Class[]{Class.class, String[].class});
        getFactoryMethod.setAccessible(true);

        Method foundMethod = (Method) getFactoryMethod.invoke(manager, MyType.class, new String[]{"instanceOf"});
        assertThat(foundMethod.getName()).isEqualTo("instanceOf");
        
        Method staticOf = (Method) getFactoryMethod.invoke(manager, MyType.class, new String[]{"of"});
        assertThat(staticOf.getName()).isEqualTo("of");

        Method notFoundMethod = (Method) getFactoryMethod.invoke(manager, MyType.class, new String[]{"missingMethod"});
        assertThat(notFoundMethod).isNull();

        Method wrongSignature = (Method) getFactoryMethod.invoke(manager, MyType.class, new String[]{"getValue"});
        assertThat(wrongSignature).isNull();
    }

    @Test
    public void testMapBoxedType() throws Exception {
        ServiceContext serviceContext = ServiceContextManager.getServiceContext(getClass().getClassLoader());
        PropertyConverterManager manager = new PropertyConverterManager(serviceContext, false);

        Class[] boxed = new Class[]{
            Integer[].class, Short[].class, Byte[].class, Long[].class,
            Boolean[].class, Character[].class, Float[].class, Double[].class
        };
        Class[] primitive = new Class[]{
            int[].class, short[].class, byte[].class, long[].class,
            boolean[].class, char[].class, float[].class, double[].class
        };

        Method method = PropertyConverterManager.class.getDeclaredMethod("mapBoxedType", TypeLiteral.class);
        method.setAccessible(true);

        for (int i = 0; i < boxed.length; i++) {
            assertThat(TypeLiteral.of(boxed[i]))
                    .isEqualTo(method.invoke(manager, TypeLiteral.of(primitive[i])));
            assertThat(TypeLiteral.of(boxed[i].getComponentType()))
                    .isEqualTo(method.invoke(manager, TypeLiteral.of(primitive[i].getComponentType())));
        }
    }

    public static class MyType {

        private final String typeValue;

        private MyType(String value) {
            typeValue = value;
        }

        public static MyType of(String source) {
            return new MyType(source);
        }

        public String getValue() {
            return typeValue;
        }

        public String instanceOf(String input) {
            return input;
        }

    }

    private enum MyEnum {
        A, B, C
    }

}
