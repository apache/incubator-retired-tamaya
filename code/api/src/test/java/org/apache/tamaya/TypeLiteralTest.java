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
package org.apache.tamaya;

import java.lang.reflect.Type;
import static org.apache.tamaya.TypeLiteral.getGenericInterfaceTypeParameters;
import static org.apache.tamaya.TypeLiteral.getTypeParameters;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Test;

/**
 * Tests for the {@link TypeLiteral} class.
 */
@SuppressWarnings("serial")
public class TypeLiteralTest {

    @Test(expected = NullPointerException.class)
    public void constructorRequiresNonNullParameter() {
        new TypeLiteral<List<String>>(null) { };
    }

    @Test
    public void test_constructor() {
        TypeLiteral<List<String>> listTypeLiteral = new TypeLiteral<List<String>>() { };
        assertThat(listTypeLiteral.getRawType()).isEqualTo(List.class);
        assertThat(TypeLiteral.getTypeParameters(listTypeLiteral.getType())[0]).isEqualTo(String.class);
    }

    @Test
    public void test_of() {
        class MyListClass extends ArrayList<String> { }
        TypeLiteral<MyListClass> listTypeLiteral = TypeLiteral.of(MyListClass.class);
        assertThat(listTypeLiteral.getRawType()).isEqualTo(MyListClass.class);
        assertThat(listTypeLiteral.getType()).isEqualTo(MyListClass.class);
    }

    @Test(expected = NullPointerException.class)
    public void ofDoesNotAcceptNullAsParamter() {
        TypeLiteral.of(null);
    }

    @Test
    public void test_getTypeParameters() {
        TypeLiteral<List<String>> listTypeLiteral = new TypeLiteral<List<String>>() { };
        assertThat(listTypeLiteral.getRawType()).isEqualTo(List.class);
        assertThat(TypeLiteral.getTypeParameters(listTypeLiteral.getType())[0]).isEqualTo(String.class);
    }

    @Test
    public void testGetTypeParametersNoGenerics() {
        assertThat(getTypeParameters(String.class).length).isEqualTo(0);
    }

    @Test
    public void test_getGenericInterfaceTypeParameter() {
        class MyListClass extends ArrayList<String> implements List<String> { }
        assertThat(getGenericInterfaceTypeParameters(MyListClass.class, List.class)[0]).isEqualTo(String.class);
    }

    @Test
    public void testGetGenericInterfaceTypeParameterNoGenerics() {
        assertThat(getGenericInterfaceTypeParameters(String.class, String.class).length).isEqualTo(0);
    }

    @Test(expected = NullPointerException.class)
    public void getGenericInterfaceTypeParametersRequiredNonNullValueForClassParameter() {
        getGenericInterfaceTypeParameters(null, Iterator.class);
    }

    @Test(expected = NullPointerException.class)
    public void getGenericInterfaceTypeParametersRequiredNonNullValueForInterfaceParameter() {
        getGenericInterfaceTypeParameters(String.class, null);
    }

    @Test(expected = NullPointerException.class)
    public void getTypeParametersRequiresNonNullParameter() {
        getTypeParameters(null);
    }

    @Test
    public void testTypeTakingParametersMustBeSubclassOfParameterizedType() {
        //Reflection on ArrayList<String> gives a ParameterizedType
        class A extends ArrayList<String> { };
        class B extends A { };
        TypeLiteral<List<String>> checker = new TypeLiteral<List<String>>() { };
        Type t = checker.getDefinedType(B.class);
        assertThat("java.lang.String").isEqualTo(t.getTypeName());
    }

    @Test(expected = RuntimeException.class)
    public void testTypeTakingParametersMustNotBeSubclassOfObject() {
        //Create a class hierarchy where B is a subclass of Object and not
        // ParameterizedType, but still takes parameters.
        class A<T> { };
        class B extends A { };
        TypeLiteral<List<String>> checker = new TypeLiteral<List<String>>() { };
        checker.getDefinedType(B.class);
    }
    
    @Test
    public void testHashAndEquals(){
        TypeLiteral a = TypeLiteral.of(List.class);
        TypeLiteral b = TypeLiteral.of(List.class);
        TypeLiteral c = TypeLiteral.of(Map.class);
        assertThat(b.hashCode()).isEqualTo(a.hashCode());
        assertThat(a.hashCode()).isNotEqualTo(c.hashCode());
        assertThat(a.equals(a)).isTrue();
        assertThat(a.equals(b)).isTrue();
        assertThat(a.equals(null)).isFalse();
        assertThat(a.equals("SomeString")).isFalse();
        assertThat(a.equals(c)).isFalse();
    }

}
