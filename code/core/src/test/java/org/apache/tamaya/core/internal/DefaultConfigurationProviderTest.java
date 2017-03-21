/*
 * Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.apache.tamaya.core.internal;

import org.apache.tamaya.spi.ConfigurationContext;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by atsticks on 11.09.16.
 */
public class DefaultConfigurationProviderTest {

    @Test
    public void testInstantiation() throws Exception {
        new DefaultConfigurationProvider();
    }

    @Test
    public void getConfiguration() throws Exception {
        assertNotNull(new DefaultConfigurationProvider().getConfiguration());
    }

    @Test
    public void createConfiguration() throws Exception {
        ConfigurationContext ctx = new DefaultConfigurationContextBuilder().build();
        assertNotNull(new DefaultConfigurationProvider().createConfiguration(ctx));
        assertEquals(ctx,
                new DefaultConfigurationProvider().createConfiguration(ctx).getContext());
    }

    @Test
    public void getConfigurationContext() throws Exception {
        assertNotNull(new DefaultConfigurationProvider().getConfigurationContext());
    }

    @Test
    public void getConfigurationContextBuilder() throws Exception {
        assertNotNull(new DefaultConfigurationProvider().getConfigurationContextBuilder());
    }

    @Test
    public void setConfigurationContext() throws Exception {
        new DefaultConfigurationProvider()
                .setConfigurationContext(new DefaultConfigurationProvider().getConfigurationContext());
    }

    @Test
    public void isConfigurationContextSettable() throws Exception {
        assertTrue(new DefaultConfigurationProvider().isConfigurationContextSettable());
    }

}