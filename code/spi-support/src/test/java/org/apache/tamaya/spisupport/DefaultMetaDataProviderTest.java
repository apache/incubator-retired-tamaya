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
package org.apache.tamaya.spisupport;

import org.apache.tamaya.spi.ConfigurationContext;
import org.apache.tamaya.spi.ServiceContextManager;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Tests for {@link DefaultMetaDataProvider}.
 */
public class DefaultMetaDataProviderTest {

    @Test
    public void cretion() {
        new DefaultMetaDataProvider();
    }

    @Test
    public void init() {
        DefaultMetaDataProvider provider = new DefaultMetaDataProvider();
        assertEquals(provider, provider.init(ConfigurationContext.EMPTY));
    }

    @Test
    public void getMetaData() {
        DefaultMetaDataProvider provider = new DefaultMetaDataProvider();
        assertEquals(provider, provider.init(ConfigurationContext.EMPTY));
        assertNotNull(provider.getMetaData("foo"));

    }

    @Test
    public void setMeta() {
        DefaultMetaDataProvider provider = new DefaultMetaDataProvider();
        assertEquals(provider, provider.init(ConfigurationContext.EMPTY));
        provider.setMeta("foo", "a", "b");
        assertNotNull(provider.getMetaData("foo"));
        assertEquals(1, provider.getMetaData("foo").size());
    }

    @Test
    public void setMeta_Map() {
        DefaultMetaDataProvider provider = new DefaultMetaDataProvider();
        assertEquals(provider, provider.init(ConfigurationContext.EMPTY));
        Map<String,String> map = new HashMap<>();
        map.put("a", "b");
        provider.setMeta("foo", map);
        assertNotNull(provider.getMetaData("foo"));
        assertEquals(1, provider.getMetaData("foo").size());

    }

    @Test
    public void reset() {
        DefaultMetaDataProvider provider = new DefaultMetaDataProvider();
        assertEquals(provider, provider.init(ConfigurationContext.EMPTY));
        provider.reset();
        assertNotNull(provider.getMetaData("foo"));
        assertTrue(provider.getMetaData("foo").isEmpty());
    }

    @Test
    public void reset1() {
        DefaultMetaDataProvider provider = new DefaultMetaDataProvider();
        assertEquals(provider, provider.init(ConfigurationContext.EMPTY));
        provider.reset();
        assertNotNull(provider.getMetaData("foo"));
        assertTrue(provider.getMetaData("foo").isEmpty());
    }


    @Test
    public void testToString() {
    }
}