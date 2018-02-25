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
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import static org.junit.Assert.*;

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
        assertEquals(List.class, listTypeLiteral.getRawType());
        assertEquals(String.class, TypeLiteral.getTypeParameters(listTypeLiteral.getType())[0]);
    }

    @Test
    public void test_of() {
        class MyListClass extends ArrayList<String> { }
        TypeLiteral<MyListClass> listTypeLiteral = TypeLiteral.of(MyListClass.class);
        assertEquals(MyListClass.class, listTypeLiteral.getRawType());
        assertEquals(MyListClass.class, listTypeLiteral.getType());
    }

    @Test(expected = NullPointerException.class)
    public void ofDoesNotAcceptNullAsParamter() {
        TypeLiteral.of(null);
    }

    @Test
    public void test_getTypeParameters() {
        TypeLiteral<List<String>> listTypeLiteral = new TypeLiteral<List<String>>() { };
        assertEquals(List.class, listTypeLiteral.getRawType());
        assertEquals(String.class, TypeLiteral.getTypeParameters(listTypeLiteral.getType())[0]);
    }

    @Test
    public void testGetTypeParametersNoGenerics() {
        assertEquals(0, getTypeParameters(String.class).length);
    }

    @Test
    public void test_getGenericInterfaceTypeParameter() {
        class MyListClass extends ArrayList<String> implements List<String> { }
        assertEquals(String.class, getGenericInterfaceTypeParameters(MyListClass.class, List.class)[0]);
    }

    @Test
    public void testGetGenericInterfaceTypeParameterNoGenerics() {
        assertEquals(0, getGenericInterfaceTypeParameters(String.class, String.class).length);
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
        assertEquals(t.getTypeName(), "java.lang.String");
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
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a.hashCode(), c.hashCode());
        assertTrue(a.equals(a));
        assertTrue(a.equals(b));
        assertFalse(a.equals(null));
        assertFalse(a.equals("SomeString"));
        assertFalse(a.equals(c));
    }

}
