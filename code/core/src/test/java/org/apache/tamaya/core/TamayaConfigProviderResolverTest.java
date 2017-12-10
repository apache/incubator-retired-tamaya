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
package org.apache.tamaya.core;

import org.apache.tamaya.core.TamayaConfigProviderResolver;
import org.apache.tamaya.spi.ConfigContext;
import org.apache.tamaya.spi.ConfigContextSupplier;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by atsticks on 11.09.16.
 */
public class TamayaConfigProviderResolverTest {

    @Test
    public void testInstantiation() throws Exception {
        new TamayaConfigProviderResolver();
    }

    @Test
    public void getConfiguration() throws Exception {
        assertNotNull(new TamayaConfigProviderResolver().getConfig());
    }

    @Test
    public void configIsConfigContextSupplier() throws Exception {
        assertTrue(new TamayaConfigProviderResolver().getConfig() instanceof ConfigContextSupplier);
    }

    @Test
    public void getBuilder() throws Exception {
        assertNotNull(new TamayaConfigProviderResolver().getBuilder());
    }

    @SuppressWarnings("deprecation")
	@Test
    public void registerConfig_CL() throws Exception {
        new TamayaConfigProviderResolver()
                .registerConfig(new TamayaConfigProviderResolver().getConfig(),
                        ClassLoader.getSystemClassLoader());
    }

    @SuppressWarnings("deprecation")
    @Test
    public void registerConfig_Null() throws Exception {
        new TamayaConfigProviderResolver()
                .registerConfig(new TamayaConfigProviderResolver().getConfig(), null);
    }


}