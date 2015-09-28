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

import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * Tests the default converter for Number.
 */
public class NumberConverterTest {

    /**
     * Test conversion. The value are provided by
     * {@link org.apache.tamaya.core.internal.converters.ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Decimal() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Optional<Number> valueRead = config.getOptional("tests.converter.bd.decimal", Number.class);
        assertTrue(valueRead.isPresent());
        assertEquals(valueRead.get(), Long.valueOf(101));
    }


    /**
     * Test conversion. The value are provided by
     * {@link org.apache.tamaya.core.internal.converters.ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Hex() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Optional<Number> valueRead = config.getOptional("tests.converter.bd.hex.lowerX", Number.class);
        assertTrue(valueRead.isPresent());
        assertEquals(valueRead.get(), Long.valueOf("47"));
        valueRead = config.getOptional("tests.converter.bd.hex.upperX", Number.class);
        assertTrue(valueRead.isPresent());
        assertEquals(valueRead.get(), Long.valueOf("63"));
    }

    /**
     * Test conversion. The value are provided by
     * {@link org.apache.tamaya.core.internal.converters.ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_NotPresent() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Optional<Number> valueRead = config.getOptional("tests.converter.bd.foo", Number.class);
        assertFalse(valueRead.isPresent());
    }

    /**
     * Test conversion. The value are provided by
     * {@link ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_BigValue() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Optional<Number> valueRead = config.getOptional("tests.converter.bd.big", Number.class);
        assertTrue(valueRead.isPresent());
        assertEquals(new BigDecimal("101666666666666662333337263723628763821638923628193612983618293628763"),
                valueRead.get());
    }

    /**
     * Test conversion. The value are provided by
     * {@link ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_BigFloatValue() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Optional<Number> valueRead = config.getOptional("tests.converter.bd.bigFloat", Number.class);
        assertTrue(valueRead.isPresent());
        assertEquals(new BigDecimal("1016666666666666623333372637236287638216389293628763.1016666666666666623333372" +
                "63723628763821638923628193612983618293628763"), valueRead.get());
    }
}