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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.tamaya.annot.ConfiguredProperty;
import org.apache.tamaya.annot.DefaultValue;
import org.apache.tamaya.core.internal.DefaultConfigurationManagerSingletonSpi;
import org.junit.Test;

import java.beans.PropertyChangeListener;

/**
 * Test class for {@link org.apache.tamaya.core.internal.DefaultConfigurationManagerSingletonSpi}.
 */
public class DefaultConfigurationManagerSingletonSpiSingletonSpiTest {

    private static final PropertyChangeListener LISTENER = System.out::println;

    @Test
    public void testSEConfigurationService() {
        new DefaultConfigurationManagerSingletonSpi();
    }

    @Test
    public void testGetConfigurationString() {
        Configuration config = Configuration.current("default");
        assertNotNull(config);
        assertTrue(config.toString().contains("default"));
        assertNotNull(config.getMetaInfo());
        assertTrue(config.getMetaInfo().toString().contains("default"));
        System.out.println("CONFIG: " + config);
        assertEquals(System.getProperty("java.version"),
                config.get("java.version").get());

        config = Configuration.current("system.properties");
        assertNotNull(config);
        assertNotNull(config.getMetaInfo());
        assertTrue(config.getMetaInfo().toString().contains("system.properties"));
        assertEquals(System.getProperty("java.version"),
                config.get("java.version").get());
    }

    @Test
    public void testIsConfigurationDefined() {
        assertTrue(Configuration.isDefined("test"));
        assertFalse(Configuration.isDefined("sdksajdsajdlkasj dlkjaslkd"));
    }

    @Test
    public void testGetCurrentEnvironment() {
        Environment env = Environment.current();
        assertNotNull(env);
        assertEquals(System.getProperty("java.version"),
                env.get("java.version").get());
    }

    @Test
    public void testGetRootEnvironment() {
        DefaultConfigurationManagerSingletonSpi s = new DefaultConfigurationManagerSingletonSpi();
        Environment env = Environment.getRootEnvironment();
        assertNotNull(env);
        assertEquals(System.getProperty("java.version"),
                env.get("java.version").get());
    }

    @Test
    public void testAddRemoveGlobalConfigChangeListener() {
        Configuration.addConfigChangeListener(LISTENER);
        Configuration.removeConfigChangeListener(LISTENER);
        Configuration.addConfigChangeListener(LISTENER);
        Configuration.addConfigChangeListener(LISTENER);
        Configuration.removeConfigChangeListener(LISTENER);
        Configuration.removeConfigChangeListener(LISTENER);
        Configuration.removeConfigChangeListener(LISTENER);
    }

    @Test
    public void testConfigure() {
        ConfigureTest test = new ConfigureTest();
        Configuration.configure(test);
        assertEquals(test.mustBeTrue, true);
        assertEquals(test.val1, "YES, it works!");
    }

    private static class ConfigureTest {
        @ConfiguredProperty
        @DefaultValue("YES, it works!")
        String val1;

        @ConfiguredProperty
        @DefaultValue("true")
        boolean mustBeTrue;
    }

}
