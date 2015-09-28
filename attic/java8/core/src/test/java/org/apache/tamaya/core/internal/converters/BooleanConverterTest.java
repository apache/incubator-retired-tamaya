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

import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests the default converter for bytes.
 */
public class BooleanConverterTest {

    /**
     * Test conversion. The value are provided by
     * {@link ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Byte() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        // trues
        Optional<Boolean> valueRead = config.getOptional("tests.converter.boolean.y1", Boolean.class);
        assertTrue(valueRead.isPresent());
        assertTrue(valueRead.get().booleanValue());
        valueRead = config.getOptional("tests.converter.boolean.y2", Boolean.class);
        assertTrue(valueRead.isPresent());
        assertTrue(valueRead.get().booleanValue());
        valueRead = config.getOptional("tests.converter.boolean.yes1", Boolean.class);
        assertTrue(valueRead.isPresent());
        assertTrue(valueRead.get().booleanValue());
        valueRead = config.getOptional("tests.converter.boolean.yes2", Boolean.class);
        assertTrue(valueRead.isPresent());
        assertTrue(valueRead.get().booleanValue());
        valueRead = config.getOptional("tests.converter.boolean.yes3", Boolean.class);
        assertTrue(valueRead.isPresent());
        assertTrue(valueRead.get().booleanValue());
        valueRead = config.getOptional("tests.converter.boolean.true1", Boolean.class);
        assertTrue(valueRead.isPresent());
        assertTrue(valueRead.get().booleanValue());
        valueRead = config.getOptional("tests.converter.boolean.true2", Boolean.class);
        assertTrue(valueRead.isPresent());
        assertTrue(valueRead.get().booleanValue());
        valueRead = config.getOptional("tests.converter.boolean.true3", Boolean.class);
        assertTrue(valueRead.isPresent());
        assertTrue(valueRead.get().booleanValue());
        valueRead = config.getOptional("tests.converter.boolean.t1", Boolean.class);
        assertTrue(valueRead.isPresent());
        assertTrue(valueRead.get().booleanValue());
        valueRead = config.getOptional("tests.converter.boolean.t2", Boolean.class);
        assertTrue(valueRead.isPresent());
        assertTrue(valueRead.get().booleanValue());
        // falses
        valueRead = config.getOptional("tests.converter.boolean.n1", Boolean.class);
        assertTrue(valueRead.isPresent());
        assertFalse(valueRead.get().booleanValue());
        valueRead = config.getOptional("tests.converter.boolean.n2", Boolean.class);
        assertTrue(valueRead.isPresent());
        assertFalse(valueRead.get().booleanValue());
        valueRead = config.getOptional("tests.converter.boolean.no1", Boolean.class);
        assertTrue(valueRead.isPresent());
        assertFalse(valueRead.get().booleanValue());
        valueRead = config.getOptional("tests.converter.boolean.no2", Boolean.class);
        assertTrue(valueRead.isPresent());
        assertFalse(valueRead.get().booleanValue());
        valueRead = config.getOptional("tests.converter.boolean.no3", Boolean.class);
        assertTrue(valueRead.isPresent());
        assertFalse(valueRead.get().booleanValue());
        valueRead = config.getOptional("tests.converter.boolean.false1", Boolean.class);
        assertTrue(valueRead.isPresent());
        assertFalse(valueRead.get().booleanValue());
        valueRead = config.getOptional("tests.converter.boolean.false2", Boolean.class);
        assertTrue(valueRead.isPresent());
        assertFalse(valueRead.get().booleanValue());
        valueRead = config.getOptional("tests.converter.boolean.false3", Boolean.class);
        assertTrue(valueRead.isPresent());
        assertFalse(valueRead.get().booleanValue());
        valueRead = config.getOptional("tests.converter.boolean.f1", Boolean.class);
        assertTrue(valueRead.isPresent());
        assertFalse(valueRead.get().booleanValue());
        valueRead = config.getOptional("tests.converter.boolean.f2", Boolean.class);
        assertTrue(valueRead.isPresent());
        assertFalse(valueRead.get().booleanValue());
        valueRead = config.getOptional("tests.converter.boolean.foo", Boolean.class);
        assertFalse(valueRead.isPresent());
    }
    /*

            case "tests.converter.boolean.n1":
                return "n";
            case "tests.converter.boolean.n2":
                return "N";
            case "tests.converter.boolean.no1":
                return "no";
            case "tests.converter.boolean.no2":
                return "No";
            case "tests.converter.boolean.no3":
                return "nO";
            case "tests.converter.boolean.false1":
                return "false";
            case "tests.converter.boolean.false2":
                return "False";
            case "tests.converter.boolean.false3":
                return "falSe";
            case "tests.converter.boolean.f1":
                return "f";
            case "tests.converter.boolean.f2":
                return "F";
     */
}