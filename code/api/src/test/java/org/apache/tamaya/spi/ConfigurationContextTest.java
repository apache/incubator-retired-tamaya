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
package org.apache.tamaya.spi;

import org.apache.tamaya.TypeLiteral;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests the abstract functionality of {@link ConfigurationContext}.
 */
public class ConfigurationContextTest {

    @Test
    public void test_EMPTY(){
        assertNull(ConfigurationContext.EMPTY.getPropertySource("foo"));
        assertNotNull(ConfigurationContext.EMPTY.getPropertySources());
        assertTrue(ConfigurationContext.EMPTY.getPropertySources().isEmpty());
        assertNotNull(ConfigurationContext.EMPTY.getMetaData("foo"));
        assertNotNull(ConfigurationContext.EMPTY.getPropertyConverters());
        assertTrue(ConfigurationContext.EMPTY.getPropertyConverters().isEmpty());
        assertNotNull(ConfigurationContext.EMPTY.getPropertyFilters());
        assertTrue(ConfigurationContext.EMPTY.getPropertyFilters().isEmpty());
        assertNotNull(ConfigurationContext.EMPTY.getServiceContext());
        assertNotNull(ConfigurationContext.EMPTY.getPropertyConverters(TypeLiteral.of(Boolean.class)));
        assertTrue(ConfigurationContext.EMPTY.getPropertyConverters(TypeLiteral.of(Boolean.class)).isEmpty());
        assertNotNull(ConfigurationContext.EMPTY.toString());
    }

}