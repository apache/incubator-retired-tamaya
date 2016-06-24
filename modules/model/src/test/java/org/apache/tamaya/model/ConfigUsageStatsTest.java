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
package org.apache.tamaya.model;

import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;
import org.apache.tamaya.model.spi.GroupModel;
import org.apache.tamaya.model.spi.ModelProviderSpi;
import org.apache.tamaya.model.spi.ParameterModel;
import org.apache.tamaya.model.spi.SectionModel;
import org.junit.Test;
import test.model.TestConfigAccessor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Anatole on 09.08.2015.
 */
public class ConfigUsageStatsTest implements ModelProviderSpi {

    private List<ConfigModel> configModels = new ArrayList<>(1);

    public ConfigUsageStatsTest(){
        configModels.add(new TestConfigModel());
        configModels = Collections.unmodifiableList(configModels);
    }

    public Collection<ConfigModel> getConfigModels() {
        return configModels;
    }

    private static final class TestConfigModel extends GroupModel {

        public TestConfigModel(){
            super("TestConfigModel", "TestConfig", new SectionModel.Builder("TestConfigModel",
                    "a.test.existing").setRequired(true).build(),
                    ParameterModel.of("TestConfigModel", "a.test.existing.aParam", true),
                    ParameterModel.of("TestConfigModel", "a.test.existing.optionalParam"),
                    ParameterModel.of("TestConfigModel", "a.test.existing.aABCParam", false, "[ABC].*"),
                    new SectionModel.Builder("TestConfigModel", "a.test.notexisting").setRequired(true).build(),
                    ParameterModel.of("TestConfigModel", "a.test.notexisting.aParam", true),
                    ParameterModel.of("TestConfigModel", "a.test.notexisting.optionalParam"),
                    ParameterModel.of("TestConfigModel", "a.test.existing.aABCParam2", false, "[ABC].*"));
        }
        @Override
        public String getName() {
            return "TestConfigConfigModel";
        }

    }

    @Test
    public void testUsageWhenEnabled(){
        ConfigUsageStats.enableUsageTracking(true);
        TestConfigAccessor.readConfiguration();
        Configuration config = ConfigurationProvider.getConfiguration();
        String info = ConfigUsageStats.getUsageInfo();
        assertFalse(info.contains("java.version"));
        assertNotNull(info);
        config = TestConfigAccessor.readConfiguration();
        config.getProperties();
        TestConfigAccessor.readProperty(config, "java.locale");
        TestConfigAccessor.readProperty(config, "java.version");
        TestConfigAccessor.readProperty(config, "java.version");
        config.get("java.version");
        info = ConfigUsageStats.getUsageInfo();
        System.out.println(info);
        assertTrue(info.contains("java.version"));
        assertNotNull(info);
        ConfigUsageStats.enableUsageTracking(false);
    }

    @Test
    public void testUsageWhenDisabled(){
        ConfigUsageStats.enableUsageTracking(false);
        ConfigUsageStats.clearUsageStats();
        TestConfigAccessor.readConfiguration();
        Configuration config = ConfigurationProvider.getConfiguration();
        String info = ConfigUsageStats.getUsageInfo();
        assertNotNull(info);
        assertFalse(info.contains("java.version"));
        config = TestConfigAccessor.readConfiguration();
        config.getProperties();
        TestConfigAccessor.readProperty(config, "java.locale");
        TestConfigAccessor.readProperty(config, "java.version");
        TestConfigAccessor.readProperty(config, "java.version");
        config.get("java.version");
        info = ConfigUsageStats.getUsageInfo();
        assertFalse(info.contains("java.version"));
        assertNotNull(info);
        ConfigUsageStats.enableUsageTracking(false);
    }
}
