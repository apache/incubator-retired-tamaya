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
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;

/**
 * Tests the default converter for bytes.
 */
public class ByteConverterTest {

    /**
     * Test conversion. The value are provided by
     * {@link org.apache.tamaya.core.internal.converters.ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Byte() throws Exception {
        Configuration config = Configuration.current();
        Optional<Byte> valueRead = config.getOptional("tests.converter.byte.decimal", Byte.class);
        assertTrue(valueRead.isPresent());
        assertEquals(valueRead.get().byteValue(), 101);
        valueRead = config.getOptional("tests.converter.byte.octal", Byte.class);
        assertTrue(valueRead.isPresent());
        assertEquals(valueRead.get().byteValue(), Byte.decode("02").byteValue());
        valueRead = config.getOptional("tests.converter.byte.hex.lowerX", Byte.class);
        assertTrue(valueRead.isPresent());
        assertEquals(valueRead.get().byteValue(), Byte.decode("0x2F").byteValue());
        valueRead = config.getOptional("tests.converter.byte.hex.upperX", Byte.class);
        assertTrue(valueRead.isPresent());
        assertEquals(valueRead.get().byteValue(), Byte.decode("0X3F").byteValue());
        valueRead = config.getOptional("tests.converter.byte.foo", Byte.class);
        assertFalse(valueRead.isPresent());
    }
}