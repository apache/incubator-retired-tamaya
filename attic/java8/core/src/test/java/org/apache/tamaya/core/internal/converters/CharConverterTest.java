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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;
import org.junit.Test;

/**
 * Tests conversion of the {@link org.apache.tamaya.core.internal.converters.CharConverter}.
 */
public class CharConverterTest {

    @Test
    public void testConvert_Character() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Optional<Character> valueRead = config.getOptional("tests.converter.char.f", Character.class);
        assertTrue(valueRead.isPresent());
        assertEquals(valueRead.get().charValue(), 'f');
    }

    @Test
    public void testConvert_Character_Numeric() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Optional<Character> valueRead = config.getOptional("tests.converter.char.f-numeric", Character.class);
        assertTrue(valueRead.isPresent());
        assertEquals(valueRead.get().charValue(), (char)101);
    }

    @Test
    public void testConvert_Character_Quoted() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Optional<Character> valueRead = config.getOptional("tests.converter.char.d", Character.class);
        assertTrue(valueRead.isPresent());
        assertEquals(valueRead.get().charValue(), 'd');
    }

    @Test
    public void testConvert_Character_WithWhitspace_Before() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Optional<Character> valueRead = config.getOptional("tests.converter.char.f-before", Character.class);
        assertTrue(valueRead.isPresent());
        assertEquals(valueRead.get().charValue(), 'f');
    }

    @Test
    public void testConvert_Character_WithWhitspace_After() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Optional<Character> valueRead = config.getOptional("tests.converter.char.f-after", Character.class);
        assertTrue(valueRead.isPresent());
        assertEquals(valueRead.get().charValue(), 'f');
    }

    @Test
    public void testConvert_Character_WithWhitspace_Around() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Optional<Character> valueRead = config.getOptional("tests.converter.char.f-around", Character.class);
        assertTrue(valueRead.isPresent());
        assertEquals(valueRead.get().charValue(), 'f');
    }

    @Test
    public void testConvert_NotPresent() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Optional<Character> valueRead = config.getOptional("tests.converter.char.foo", Character.class);
        assertFalse(valueRead.isPresent());
    }
}
