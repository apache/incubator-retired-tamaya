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

import org.apache.tamaya.TestConfigurationProvider;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mockito.Mockito;

public class ConfigurationProviderSpiTest {
    
TestConfigurationProvider configProvider = new TestConfigurationProvider();

    @Test
    public void testIsConfigurationSettableByDefault(){
        assertTrue(configProvider.isConfigurationSettable());
    }
    
    @Test
    public void testIsConfigurationContextSettable(){
        assertTrue(configProvider.isConfigurationContextSettable());
    }    
    
    /**
     * Test of getConfigurationContext and setConfigurationContext method, of
     * class ConfigurationProviderSpi.
     */
    @Test
    public void testGetSetConfigurationContext() {
        ConfigurationContext currentContext = configProvider.getConfigurationContextFromInterface();
        assertTrue(currentContext instanceof ConfigurationContext);
        ConfigurationContext newContext = Mockito.mock(ConfigurationContext.class);
        try{
            configProvider.setConfigurationContext(newContext);
            //The mocked TestConfigurationProvider doesn't set the context on the
            // inner Configuration object, as that's deprecated.
            assertEquals(newContext, configProvider.getConfigurationContext());
        }finally{
            configProvider.setConfigurationContext(currentContext);
        }
    }
}
