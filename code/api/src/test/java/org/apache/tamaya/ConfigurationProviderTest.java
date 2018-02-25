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

import org.apache.tamaya.spi.ConfigurationBuilder;
import org.apache.tamaya.spi.ConfigurationContext;
import org.apache.tamaya.spi.ConfigurationContextBuilder;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mockito.Mockito;

/**
 *
 * Test the {@link ConfigurationProivder} class. The tests end up being tests of
 * the default methods in the {@link ConfigurationProivder} interface as they
 * pass through to the {@link TestConfigurationProvider} mocked object.
 */
public class ConfigurationProviderTest {


    /**
     * Test of createConfiguration method, of class ConfigurationProvider.
     */
    @Test
    public void testCreateConfiguration() {
        Configuration result = ConfigurationProvider.createConfiguration(ConfigurationProvider.getConfiguration().getContext());
        assertNotNull(result);
    }
    
    /**
     * Test of getConfigurationContext and setConfigurationContext method, of
     * class ConfigurationProvider.
     */
    @Test
    public void testGetSetConfigurationContext() {
        ConfigurationContext currentContext = ConfigurationProvider.getConfigurationContext();
        assertTrue(currentContext instanceof ConfigurationContext);
        ConfigurationContext newContext = Mockito.mock(ConfigurationContext.class);
        try{
            ConfigurationProvider.setConfigurationContext(newContext);
            assertEquals(newContext, ConfigurationProvider.getConfigurationContext());
        }finally{
            ConfigurationProvider.setConfigurationContext(currentContext);
        }
        assertEquals(currentContext, ConfigurationProvider.getConfigurationContext());
    }

    /**
     * Test of getConfiguration method, of class ConfigurationProvider.
     */
    @Test
    public void testGetSetConfiguration() {
        Configuration currentConfig = ConfigurationProvider.getConfiguration();
        assertTrue(currentConfig instanceof Configuration);
        Configuration newConfig = Mockito.mock(Configuration.class);
        try{
            ConfigurationProvider.setConfiguration(newConfig);
            assertEquals(newConfig, ConfigurationProvider.getConfiguration());
        }finally{
            ConfigurationProvider.setConfiguration(currentConfig);
        }
        assertEquals(currentConfig, ConfigurationProvider.getConfiguration());
    }

    /**
     * Test of getConfigurationBuilder method, of class ConfigurationProvider.
     */
    @Test
    public void testGetConfigurationBuilder() {
        ConfigurationBuilder result = ConfigurationProvider.getConfigurationBuilder();
        assertTrue(result instanceof ConfigurationBuilder);
    }

    /**
     * Test of getConfigurationContextBuilder method, of class ConfigurationProvider.
     */
    @Test
    public void testGetConfigurationContextBuilder() {
        ConfigurationContextBuilder result = ConfigurationProvider.getConfigurationContextBuilder();
        assertTrue(result instanceof ConfigurationContextBuilder);
    }

    @Test
    public void testConstructorFails(){
        assertTrue(ConfigurationProvider.class.getConstructors().length == 0);
    }
}
