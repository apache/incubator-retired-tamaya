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

import org.apache.tamaya.model.spi.AreaValidation;
import org.apache.tamaya.model.spi.ParameterValidation;
import org.apache.tamaya.model.spi.ValidationGroup;
import org.apache.tamaya.model.spi.ValidationProviderSpi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by Anatole on 09.08.2015.
 */
public class TestConfigValidationProvider implements ValidationProviderSpi{

    private List<Validation> validations = new ArrayList<>(1);

    public TestConfigValidationProvider(){
        validations.add(new TestConfigValidation());
        validations = Collections.unmodifiableList(validations);
    }

    @Override
    public Collection<Validation> getValidations() {
        return validations;
    }

    private static final class TestConfigValidation extends ValidationGroup{

        public TestConfigValidation(){
            super("TestConfig", "test", new AreaValidation.Builder("a.test.existing").setRequired(true).build(),
                    ParameterValidation.of("a.test.existing.aParam", true),
                    ParameterValidation.of("a.test.existing.optionalParam"),
                    ParameterValidation.of("a.test.existing.aABCParam", false, "[ABC].*"),
                    new AreaValidation.Builder("a.test.notexisting").setRequired(true).build(),
                    ParameterValidation.of("a.test.notexisting.aParam", true),
                    ParameterValidation.of("a.test.notexisting.optionalParam"),
                    ParameterValidation.of("a.test.existing.aABCParam2", false, "[ABC].*"));
        }
        @Override
        public String getName() {
            return "TestConfigValidation";
        }

    }
}
