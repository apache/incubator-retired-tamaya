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

import org.apache.tamaya.model.spi.AreaConfigModel;
import org.apache.tamaya.model.spi.ParameterModel;
import org.apache.tamaya.model.spi.ConfigModelGroup;
import org.apache.tamaya.model.spi.ModelProviderSpi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by Anatole on 09.08.2015.
 */
public class TestConfigModelProvider implements ModelProviderSpi {

    private List<ConfigModel> configModels = new ArrayList<>(1);

    public TestConfigModelProvider(){
        configModels.add(new TestConfigConfigModel());
        configModels = Collections.unmodifiableList(configModels);
    }

    public Collection<ConfigModel> getConfigModels() {
        return configModels;
    }

    private static final class TestConfigConfigModel extends ConfigModelGroup {

        public TestConfigConfigModel(){
            super("TestConfig", "test", new AreaConfigModel.Builder("a.test.existing").setRequired(true).build(),
                    ParameterModel.of("a.test.existing.aParam", true),
                    ParameterModel.of("a.test.existing.optionalParam"),
                    ParameterModel.of("a.test.existing.aABCParam", false, "[ABC].*"),
                    new AreaConfigModel.Builder("a.test.notexisting").setRequired(true).build(),
                    ParameterModel.of("a.test.notexisting.aParam", true),
                    ParameterModel.of("a.test.notexisting.optionalParam"),
                    ParameterModel.of("a.test.existing.aABCParam2", false, "[ABC].*"));
        }
        @Override
        public String getName() {
            return "TestConfigConfigModel";
        }

    }
}
