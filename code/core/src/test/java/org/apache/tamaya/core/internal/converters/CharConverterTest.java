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



import org.apache.tamaya.core.converters.CharConverter;
import org.junit.Test;

import javax.config.Config;
import javax.config.ConfigProvider;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;
/**
 * Tests conversion of the {@link CharConverter}.
 */
public class CharConverterTest {

    @Test
    public void testConvert_Character() throws Exception {
        Config config = ConfigProvider.getConfig();
        Character valueRead = config.getValue("tests.converter.char.f", Character.class);
        assertThat(valueRead).isNotNull();
        assertEquals(valueRead.charValue(), 'f');
    }

    @Test
    public void testConvert_Character_Numeric() throws Exception {
        Config config = ConfigProvider.getConfig();
        Character valueRead = config.getValue("tests.converter.char.f-numeric", Character.class);
        assertThat(valueRead).isNotNull();
        assertEquals(valueRead.charValue(), (char)101);
    }

    @Test
    public void testConvert_Character_Quoted() throws Exception {
        Config config = ConfigProvider.getConfig();
        Character valueRead = config.getValue("tests.converter.char.d", Character.class);
        assertThat(valueRead).isNotNull();
        assertEquals(valueRead.charValue(), 'd');
    }

    @Test
    public void testConvert_Character_WithWhitespace_Before() throws Exception {
        Config config = ConfigProvider.getConfig();
        Character valueRead = config.getValue("tests.converter.char.f-before", Character.class);
        assertThat(valueRead).isNotNull();
        assertEquals(valueRead.charValue(), 'f');
    }

    @Test
    public void testConvert_Character_WithWhitespace_After() throws Exception {
        Config config = ConfigProvider.getConfig();
        Character valueRead = config.getValue("tests.converter.char.f-after", Character.class);
        assertThat(valueRead).isNotNull();
        assertEquals(valueRead.charValue(), 'f');
    }

    @Test
    public void testConvert_Character_WithWhitespace_Around() throws Exception {
        Config config = ConfigProvider.getConfig();
        Character valueRead = config.getValue("tests.converter.char.f-around", Character.class);
        assertThat(valueRead).isNotNull();
        assertEquals(valueRead.charValue(), 'f');
    }

    @Test
    public void testConvert_NotPresent() throws Exception {
        Config config = ConfigProvider.getConfig();
        Character valueRead = config.getValue("tests.converter.char.foo", Character.class);
        assertNull(valueRead);
    }
}
