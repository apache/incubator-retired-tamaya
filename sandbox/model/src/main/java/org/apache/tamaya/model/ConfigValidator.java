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
import org.apache.tamaya.spi.ServiceContextManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Validator accessor to validate the current configuration.
 */
public final class ConfigValidator {

    /**
     * Singleton constructor.
     */
    private ConfigValidator() {
    }

    /**
     * Validates the current configuration.
     *
     * @return the validation results, never null.
     */
    public static Collection<ConfigValidationResult> validate() {
        return validate(ConfigurationProvider.getConfiguration());
    }

    /**
     * Validates the given configuration.
     *
     * @param config the configuration to be validated against, not null.
     * @return the validation results, never null.
     */
    public static Collection<ConfigValidationResult> validate(Configuration config) {
        List<ConfigValidationResult> result = new ArrayList<>();
        for (ConfigModel model : ServiceContextManager.getServiceContext().getServices(ConfigModel.class)) {
            result.addAll(model.validate(config));
        }
        return result;
    }

}
