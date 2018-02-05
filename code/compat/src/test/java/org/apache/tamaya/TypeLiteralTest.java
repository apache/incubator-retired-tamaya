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

import static org.apache.tamaya.spi.TypeLiteral.getGenericInterfaceTypeParameters;
import static org.apache.tamaya.spi.TypeLiteral.getTypeParameters;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.tamaya.spi.TypeLiteral;
import org.junit.Test;

/**
 * Tests for the {@link TypeLiteral} class.
 */
@SuppressWarnings("serial")
public class TypeLiteralTest {

	@Test(expected = NullPointerException.class)
    public void constructorRequiresNonNullParameter() {
       new TypeLiteral<List<String>>(null){};
    }

    @Test
    public void test_constrcutor(){
        TypeLiteral<List<String>> listTypeLiteral = new TypeLiteral<List<String>>(){};
        assertEquals(List.class, listTypeLiteral.getRawType());
        assertEquals(String.class, TypeLiteral.getTypeParameters(listTypeLiteral.getType())[0]);
    }

    @Test
    public void test_of(){
        class MyListClass extends ArrayList<String>{}
        TypeLiteral<MyListClass> listTypeLiteral = TypeLiteral.of(MyListClass.class);
        assertEquals(MyListClass.class, listTypeLiteral.getRawType());
        assertEquals(MyListClass.class, listTypeLiteral.getType());
    }

    @Test(expected = NullPointerException.class)
    public void ofDoesNotAcceptNullAsParamter() {
        TypeLiteral.of(null);
    }

    @Test
    public void test_getTypeParameter(){
        TypeLiteral<List<String>> listTypeLiteral = new TypeLiteral<List<String>>(){};
        assertEquals(List.class, listTypeLiteral.getRawType());
        assertEquals(String.class, TypeLiteral.getTypeParameters(listTypeLiteral.getType())[0]);
    }

    @Test
    public void test_getGenericInterfaceTypeParameter(){
        class MyListClass extends ArrayList<String> implements List<String>{}
        assertEquals(String.class, getGenericInterfaceTypeParameters(MyListClass.class, List.class)[0]);
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

}
