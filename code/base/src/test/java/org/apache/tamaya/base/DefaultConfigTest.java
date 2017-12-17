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
package org.apache.tamaya.base;

import org.apache.tamaya.spi.TypeLiteral;
import org.junit.Test;


import static org.junit.Assert.assertNull;

public class DefaultConfigTest {

    /**
     * Tests for get(String)
     */
    @Test(expected = NullPointerException.class)
    public void getDoesNotAcceptNull1() {
        DefaultConfig c =  new DefaultConfig(new DefaultConfigBuilder().getConfigContext());
        c.getValue("a", (Class)null);
    }

    /**
     * Tests for get(String)
     */
    @Test(expected = NullPointerException.class)
    public void getDoesNotAcceptNull2() {
        DefaultConfig c =  new DefaultConfig(new DefaultConfigBuilder().getConfigContext());
        c.getValue(null, String.class);
    }

    /**
     * Tests for get(String, Class)
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Test(expected = NullPointerException.class)
    public void getDoesNotAcceptNullForClassTargetType() {
        DefaultConfig c = new DefaultConfig(new DefaultConfigBuilder().getConfigContext());
        c.getValue("a", (Class) null);
    }

    /**
     * Tests for get(String, TypeLiteral)
     */
    @Test(expected = NullPointerException.class)
    public void getDoesNotAcceptNullForTypeLiteralTargetType() {
        DefaultConfig c =  new DefaultConfig(new DefaultConfigBuilder().getConfigContext());
        c.getValue("a", (Class)null);
    }

    /**
     * Tests for getOrDefault(String, Class, String)
     */
    @Test(expected = NullPointerException.class)
    public void getOrDefaultDoesNotAcceptNullAsKeyForThreeParameterVariant() {
        DefaultConfig c = new DefaultConfig(new DefaultConfigBuilder().getConfigContext());

        c.getOptionalValue(null, String.class).orElse("ok");
    }

    @Test
    public void getOrDefaultDoesAcceptNullAsDefaultValueForThreeParameterVariant() {
        DefaultConfig c = new DefaultConfig(new DefaultConfigBuilder().getConfigContext());

        assertNull(c.getOptionalValue("a", String.class).orElse(null));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	@Test(expected = NullPointerException.class)
    public void getOrDefaultDoesAcceptNullAsTargetTypeForThreeParameterVariant() {
        DefaultConfig c = new DefaultConfig(new DefaultConfigBuilder().getConfigContext());
        c.getOptionalValue("a", (Class)null).orElse("b");
    }

//    /**
//     * Tests for getOrDefault(String, TypeLiteral, String)
//     */
//    @Test(expected = NullPointerException.class)
//    public void getOrDefaultDoesNotAcceptNullAsKeyForThreeParameterVariantSecondIsTypeLiteral() {
//        DefaultConfiguration c = new DefaultConfiguration(new ConfigurationContextBuilder().build());
//
//        c.getOptionalValue(null, TypeLiteral.of(String.class)).orElse("ok");
//    }
//
//    @Test
//    public void getOrDefaultDoesAcceptNullAsDefaultValueForThreeParameterVariantSecondIsTypeLiteral() {
//        DefaultConfiguration c = new DefaultConfiguration(new ConfigurationContextBuilder().build());
//
//        assertNull(c.getOptionalValue("a", TypeLiteral.of(String.class)).orElse( null));
//    }
//
//    @Test(expected = NullPointerException.class)
//    public void getOrDefaultDoesNotAcceptNullAsTargetTypeForThreeParameterVariantSecondIsTypeLiteral() {
//        DefaultConfiguration c = new DefaultConfiguration(new ConfigurationContextBuilder().build());
//
//        c.getOptionalValue("a", (TypeLiteral<String>) null).orElse( "b");
//    }

    /**
     * Tests for getOrDefault(String, String)
     */
    @Test(expected = NullPointerException.class)
    public void getOrDefaultDoesNotAcceptNullAsKeyForTwoParameterVariantDefaultValueIsSecond() {
        DefaultConfig c = new DefaultConfig(new DefaultConfigBuilder().getConfigContext());

        c.getOptionalValue(null, String.class).orElse("ok");
    }

    @Test(expected = NullPointerException.class)
    public void getOrDefaultDoesAcceptNullAsDefaultValueForTwoParameterVariantDefaultValueIsSecond() {
        DefaultConfig c = new DefaultConfig(new DefaultConfigBuilder().getConfigContext());
       assertNull(c.getOptionalValue("a", null));
    }

}