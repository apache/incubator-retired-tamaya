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
package org.apache.tamaya;

import org.apache.tamaya.spi.ConfigurationContext;
import org.apache.tamaya.spi.ConfigurationContextBuilder;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Simple tests for the }@link ConfigurationProvider} singleton class.
 */
public class ConfigurationProviderTest {

    @Test
    public void testGetConfiguration() throws Exception {
        Configuration cfg = ConfigurationProvider.getConfiguration();
        assertNotNull(cfg);
    }

//    @Test
//    public void testSetConfigurationContext() throws Exception {
//        ConfigurationContextBuilder builder = ConfigurationProvider.getConfigurationContextBuilder();
//        ConfigurationContext ctx = builder.build();
//        ConfigurationContext prevCtx = ConfigurationProvider.getConfigurationContext();
//        assertNotNull(prevCtx);
//        ConfigurationProvider.setConfigurationContext(ctx);
//        assertTrue(ConfigurationProvider.getConfigurationContext() == ctx);
//        ConfigurationProvider.setConfigurationContext(prevCtx);
//        assertTrue(ConfigurationProvider.getConfigurationContext() == prevCtx);
//
//    }
//
//    @Test
//    public void testIsConfigurationContextSettable() throws Exception {
//        assertTrue(ConfigurationProvider.isConfigurationContextSettable());
//    }
//
//    @Test
//    public void testGetConfigurationContextBuilder() throws Exception {
//        ConfigurationContextBuilder builder = ConfigurationProvider.getConfigurationContextBuilder();
//        assertNotNull(builder);
//        assertTrue(builder.build().getPropertyConverters().isEmpty());
//        assertTrue(builder.build().getPropertyFilters().isEmpty());
//        assertTrue(builder.build().getPropertySources().isEmpty());
//
//    }
}