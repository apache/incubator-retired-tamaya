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
import org.junit.Test;
import static org.assertj.core.api.Assertions.*;
import org.mockito.Mockito;

/**
 *
 * Test the {@link ConfigurationProvider} class. The tests end up being tests of
 * the default methods in the {@link ConfigurationProvider} interface as they
 * pass through to the {@link TestConfigurationProvider} mocked createObject.
 */
public class ConfigurationProviderTest {


    /**
     * Test of createConfiguration method, of class ConfigurationProvider.
     */
    @Test
    public void testCreateConfiguration() {
        Configuration result = ConfigurationProvider.createConfiguration(Configuration.current().getContext());
        assertThat(result).isNotNull();
    }
    
    /**
     * Test of getConfiguration method, of class ConfigurationProvider.
     */
    @Test
    public void testGetSetConfiguration() {
        Configuration currentConfig = Configuration.current();
        assertThat(currentConfig instanceof Configuration).isTrue();
        Configuration newConfig = Mockito.mock(Configuration.class);
        try{
            ConfigurationProvider.setConfiguration(newConfig);
            assertThat(Configuration.current()).isEqualTo(newConfig);
        }finally{
            ConfigurationProvider.setConfiguration(currentConfig);
        }
        assertThat(Configuration.current()).isEqualTo(currentConfig);
    }

    /**
     * Test of createConfigurationBuilder method, of class ConfigurationProvider.
     */
    @Test
    public void testGetConfigurationBuilder() {
        ConfigurationBuilder result = ConfigurationProvider.getConfigurationBuilder();
        assertThat(result instanceof ConfigurationBuilder).isTrue();
    }

    @Test
    public void testConstructorFails(){
        assertThat(ConfigurationProvider.class.getConstructors().length == 0).isTrue();
    }
}
