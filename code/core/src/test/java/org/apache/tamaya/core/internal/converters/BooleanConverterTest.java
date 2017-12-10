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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;



import org.junit.Test;

import javax.config.Config;
import javax.config.ConfigProvider;

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
    public void testConvert_BooleanTrue() throws Exception {
        Config config = ConfigProvider.getConfig();
        Boolean valueRead = config.getValue("tests.converter.boolean.y1", Boolean.class);
        assertNotNull(valueRead);
        assertEquals(valueRead, Boolean.TRUE);
        valueRead = config.getValue("tests.converter.boolean.y2", Boolean.class);
        assertNotNull(valueRead);
        assertEquals(valueRead, Boolean.TRUE);
        valueRead = config.getValue("tests.converter.boolean.yes1", Boolean.class);
        assertNotNull(valueRead);
        assertEquals(valueRead, Boolean.TRUE);
        valueRead = config.getValue("tests.converter.boolean.yes2", Boolean.class);
        assertNotNull(valueRead);
        assertEquals(valueRead, Boolean.TRUE);
        valueRead = config.getValue("tests.converter.boolean.yes3", Boolean.class);
        assertNotNull(valueRead);
        assertEquals(valueRead, Boolean.TRUE);
        valueRead = config.getValue("tests.converter.boolean.true1", Boolean.class);
        assertNotNull(valueRead);
        assertEquals(valueRead, Boolean.TRUE);
        valueRead = config.getValue("tests.converter.boolean.true2", Boolean.class);
        assertNotNull(valueRead);
        assertEquals(valueRead, Boolean.TRUE);
        valueRead = config.getValue("tests.converter.boolean.true3", Boolean.class);
        assertNotNull(valueRead);
        assertEquals(valueRead, Boolean.TRUE);
        valueRead = config.getValue("tests.converter.boolean.t1", Boolean.class);
        assertNotNull(valueRead);
        assertEquals(valueRead, Boolean.TRUE);
        valueRead = config.getValue("tests.converter.boolean.t2", Boolean.class);
        assertNotNull(valueRead);
        assertEquals(valueRead, Boolean.TRUE);
    }

    /**
     * Test conversion. The value are provided by
     * {@link ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_BooleanFalse() throws Exception {
        Config config = ConfigProvider.getConfig();
        Boolean valueRead = config.getValue("tests.converter.boolean.y1", Boolean.class);
        assertNotNull(valueRead);
        valueRead = config.getValue("tests.converter.boolean.n1", Boolean.class);
        assertNotNull(valueRead);
        assertFalse(valueRead);
        valueRead = config.getValue("tests.converter.boolean.n2", Boolean.class);
        assertNotNull(valueRead);
        assertFalse(valueRead);
        valueRead = config.getValue("tests.converter.boolean.no1", Boolean.class);
        assertFalse(valueRead);
        assertFalse(valueRead);
        valueRead = config.getValue("tests.converter.boolean.no2", Boolean.class);
        assertNotNull(valueRead);
        assertFalse(valueRead);
        valueRead = config.getValue("tests.converter.boolean.no3", Boolean.class);
        assertNotNull(valueRead);
        assertFalse(valueRead);
        valueRead = config.getValue("tests.converter.boolean.false1", Boolean.class);
        assertNotNull(valueRead);
        assertFalse(valueRead);
        valueRead = config.getValue("tests.converter.boolean.false2", Boolean.class);
        assertNotNull(valueRead);
        assertFalse(valueRead);
        valueRead = config.getValue("tests.converter.boolean.false3", Boolean.class);
        assertNotNull(valueRead);
        assertFalse(valueRead);
        valueRead = config.getValue("tests.converter.boolean.f1", Boolean.class);
        assertNotNull(valueRead);
        assertFalse(valueRead);
        valueRead = config.getValue("tests.converter.boolean.f2", Boolean.class);
        assertNotNull(valueRead);
        assertFalse(valueRead);
        valueRead = config.getValue("tests.converter.boolean.foo", Boolean.class);
        assertNull(valueRead);
    }
}
