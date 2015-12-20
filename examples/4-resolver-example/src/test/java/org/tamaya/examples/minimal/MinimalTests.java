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
package org.tamaya.examples.minimal;

import org.apache.tamaya.ConfigException;
import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigInteger;

import static org.junit.Assert.assertEquals;

/**
 * Created by Anatole on 20.03.2015.
 */
public class MinimalTests {

    private static Configuration config;

    @BeforeClass
    public static void before() throws InterruptedException {
        config = ConfigurationProvider.getConfiguration();
        Thread.sleep(100L);
    }

    @Test
    public void printMetaInfo() {
        System.out.println("****************************************************");
        System.out.println("Minimal Example");
        System.out.println("****************************************************");
        System.out.println();
        System.out.println("Example Metadata:");
        System.out.println("  Type        :  " + config.get("example.type"));
        System.out.println("  Name        :  " + config.get("example.name"));
        System.out.println("  Description :  " + config.get("example.description"));
        System.out.println("  Version     :  " + config.get("example.version"));
        System.out.println("  Author      :  " + config.get("example.author"));
        System.out.println();
    }

    @Test(expected = ConfigException.class)
    public void getNumberValueTooLong() {
        String value = config.get("example.number");
        assertEquals(value, "350322222222222222222266666666666666666666622222222222222222");
        int number = config.get("example.number", int.class);
    }
    @Test
    public void getNumberValueAsInt_BadCase() {
        String value = config.get("example.numberAsHex");
        int number = config.get("example.numberAsHex", int.class);
        print("example.numberAsHex", number);
    }

    @Test
    public void getNumberValueAsBigInteger() {
        String value = config.get("example.number");
        BigInteger number = config.get("example.number", BigInteger.class);
        print("example.number", number);
    }

    @Test(expected = ConfigException.class)
    public void getNumberValueAsLongHex() {
        String value = config.get("example.numberAsLongHex");
        long number = config.get("example.numberAsLongHex", int.class);
        print("example.numberAsLongHex", number);
    }

    @Test
    public void getEnum() {
        String value = config.get("example.testEnum");
        TestEnum en = config.get("example.testEnum", TestEnum.class);
        print("example.testEnum", en);
    }

    protected void print(String key, Object value) {
        System.out.println("----\n" +
                "  " + key + "(String)=" + config.get(key)
                + "\n  " + key + "(" + value.getClass().getSimpleName() + ")=" + value);
    }
}
