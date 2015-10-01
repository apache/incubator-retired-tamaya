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
package org.apache.tamaya.functions;

import org.apache.tamaya.functions.PropertySourceFunctions;
import org.apache.tamaya.spi.PropertySource;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Anatole on 01.10.2015.
 */
public class PropertySourceFunctionsTest {

    @Test
    public void testAddMetaData() throws Exception {

    }

    @Test
    public void testIsKeyInSection() throws Exception {

    }

    @Test
    public void testIsKeyInSections() throws Exception {

    }

    @Test
    public void testSections() throws Exception {

    }

    @Test
    public void testTransitiveSections() throws Exception {

    }

    @Test
    public void testSections1() throws Exception {

    }

    @Test
    public void testTransitiveSections1() throws Exception {

    }

    @Test
    public void testSectionsRecursive() throws Exception {

    }

    @Test
    public void testSectionRecursive() throws Exception {

    }

    @Test
    public void testStripSectionKeys() throws Exception {

    }

    @Test
    public void testAddItems() throws Exception {

    }

    @Test
    public void testAddItems1() throws Exception {

    }

    @Test
    public void testReplaceItems() throws Exception {

    }

    @Test
    public void testEmptyPropertySource() throws Exception {
        PropertySource ps = PropertySourceFunctions.emptyPropertySource();
        assertNotNull(ps);
        assertNotNull(ps.getProperties());
        assertTrue(ps.getProperties().isEmpty());
        assertEquals(ps.getName(), "<empty>" );
        assertTrue(ps.isScannable());
    }
}