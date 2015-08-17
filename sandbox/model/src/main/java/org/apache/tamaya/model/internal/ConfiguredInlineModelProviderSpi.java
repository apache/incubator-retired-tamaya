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
package org.apache.tamaya.model.internal;

import org.apache.tamaya.ConfigurationProvider;
import org.apache.tamaya.model.Validation;
import org.apache.tamaya.model.spi.ConfigValidationsReader;
import org.apache.tamaya.model.spi.ValidationProviderSpi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Validation provider that reads model metadata from the current {@link org.apache.tamaya.Configuration}.
 */
public class ConfiguredInlineModelProviderSpi implements ValidationProviderSpi {

    /** The logger. */
    private static final Logger LOG = Logger.getLogger(ConfiguredInlineModelProviderSpi.class.getName());
    /** parameter to disable this provider. By default the provider is active. */
    private static final String MODEL_EANABLED_PARAM = "org.apache.tamaya.model.integrated.enabled";

    /** The validations read. */
    private List<Validation> validations = new ArrayList<>();


    /**
     * Constructor, typically called by the {@link java.util.ServiceLoader}.
     */
    public ConfiguredInlineModelProviderSpi() {
        String enabledVal = ConfigurationProvider.getConfiguration().get(MODEL_EANABLED_PARAM);
        boolean disabled = enabledVal==null? true: "false".equalsIgnoreCase(enabledVal);
        if (!disabled) {
            LOG.info("Reading model configuration from config...");
            Map<String,String> config = ConfigurationProvider.getConfiguration().getProperties();
            validations.addAll(ConfigValidationsReader.loadValidations(config,
                    "<Inline Configuration Model>"));
        }
        validations = Collections.unmodifiableList(validations);
    }


    @Override
    public Collection<Validation> getValidations() {
        return validations;
    }
}
