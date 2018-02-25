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

import org.apache.tamaya.ConfigException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;
import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.spi.ConversionContext;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Tests the default converter for bytes.
 */
public class BooleanConverterTest {

    /**
     * Test conversion. The value are provided by
     * {@link ConverterTestsPropertySource}.
     *
     * @throws Exception
     */
    @Test
    public void testConvert_BooleanTrue() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Boolean valueRead = config.get("tests.converter.boolean.y1", Boolean.class);
        assertNotNull(valueRead);
        assertEquals(valueRead, Boolean.TRUE);
        valueRead = config.get("tests.converter.boolean.y2", Boolean.class);
        assertNotNull(valueRead);
        assertEquals(valueRead, Boolean.TRUE);
        valueRead = config.get("tests.converter.boolean.yes1", Boolean.class);
        assertNotNull(valueRead);
        assertEquals(valueRead, Boolean.TRUE);
        valueRead = config.get("tests.converter.boolean.yes2", Boolean.class);
        assertNotNull(valueRead);
        assertEquals(valueRead, Boolean.TRUE);
        valueRead = config.get("tests.converter.boolean.yes3", Boolean.class);
        assertNotNull(valueRead);
        assertEquals(valueRead, Boolean.TRUE);
        valueRead = config.get("tests.converter.boolean.true1", Boolean.class);
        assertNotNull(valueRead);
        assertEquals(valueRead, Boolean.TRUE);
        valueRead = config.get("tests.converter.boolean.true2", Boolean.class);
        assertNotNull(valueRead);
        assertEquals(valueRead, Boolean.TRUE);
        valueRead = config.get("tests.converter.boolean.true3", Boolean.class);
        assertNotNull(valueRead);
        assertEquals(valueRead, Boolean.TRUE);
        valueRead = config.get("tests.converter.boolean.t1", Boolean.class);
        assertNotNull(valueRead);
        assertEquals(valueRead, Boolean.TRUE);
        valueRead = config.get("tests.converter.boolean.t2", Boolean.class);
        assertNotNull(valueRead);
        assertEquals(valueRead, Boolean.TRUE);
    }

    /**
     * Test conversion. The value are provided by
     * {@link ConverterTestsPropertySource}.
     *
     * @throws Exception
     */
    @Test
    public void testConvert_BooleanFalse() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Boolean valueRead = config.get("tests.converter.boolean.y1", Boolean.class);
        assertNotNull(valueRead);
        valueRead = config.get("tests.converter.boolean.n1", Boolean.class);
        assertNotNull(valueRead);
        assertFalse(valueRead);
        valueRead = config.get("tests.converter.boolean.n2", Boolean.class);
        assertNotNull(valueRead);
        assertFalse(valueRead);
        valueRead = config.get("tests.converter.boolean.no1", Boolean.class);
        assertFalse(valueRead);
        assertFalse(valueRead);
        valueRead = config.get("tests.converter.boolean.no2", Boolean.class);
        assertNotNull(valueRead);
        assertFalse(valueRead);
        valueRead = config.get("tests.converter.boolean.no3", Boolean.class);
        assertNotNull(valueRead);
        assertFalse(valueRead);
        valueRead = config.get("tests.converter.boolean.false1", Boolean.class);
        assertNotNull(valueRead);
        assertFalse(valueRead);
        valueRead = config.get("tests.converter.boolean.false2", Boolean.class);
        assertNotNull(valueRead);
        assertFalse(valueRead);
        valueRead = config.get("tests.converter.boolean.false3", Boolean.class);
        assertNotNull(valueRead);
        assertFalse(valueRead);
        valueRead = config.get("tests.converter.boolean.f1", Boolean.class);
        assertNotNull(valueRead);
        assertFalse(valueRead);
        valueRead = config.get("tests.converter.boolean.f2", Boolean.class);
        assertNotNull(valueRead);
        assertFalse(valueRead);
        valueRead = config.get("tests.converter.boolean.foo", Boolean.class);
        assertNull(valueRead);
    }

    /**
     * Test conversion. The value are provided by
     * {@link ConverterTestsPropertySource}.
     *
     * @throws ConfigException
     */
    @Test(expected = ConfigException.class)
    public void testConvert_BooleanInvalid() throws ConfigException {
        Configuration config = ConfigurationProvider.getConfiguration();
        Boolean valueRead = config.get("tests.converter.boolean.invalid", Boolean.class);
        assertNull(valueRead);
    }

    @Test
    public void callToConvertAddsMoreSupportedFormatsToTheContext() throws Exception {
        ConversionContext context = new ConversionContext.Builder(TypeLiteral.of(Boolean.class)).build();
        BooleanConverter converter = new BooleanConverter();
        converter.convert("", context);

        assertTrue(context.getSupportedFormats().contains("true (ignore case) (BooleanConverter)"));
        assertTrue(context.getSupportedFormats().contains("false (ignore case) (BooleanConverter)"));
    }
    
    @Test
    public void testHashCode() {
        BooleanConverter instance = new BooleanConverter();
        assertEquals(BooleanConverter.class.hashCode(), instance.hashCode());
    }

}
