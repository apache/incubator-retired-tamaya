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
import org.apache.tamaya.model.ConfigModel;
import org.apache.tamaya.model.spi.ConfigModelReader;
import org.apache.tamaya.model.spi.ModelProviderSpi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * ConfigModel provider that reads model metadata from the current {@link org.apache.tamaya.Configuration}.
 */
public class ConfiguredInlineModelProviderSpi implements ModelProviderSpi {

    /** The logger. */
    private static final Logger LOG = Logger.getLogger(ConfiguredInlineModelProviderSpi.class.getName());
    /** parameter to disable this provider. By default the provider is active. */
    private static final String MODEL_EANABLED_PARAM = "org.apache.tamaya.model.integrated.enabled";

    /** The configModels read. */
    private List<ConfigModel> configModels = new ArrayList<>();


    /**
     * Constructor, typically called by the {@link java.util.ServiceLoader}.
     */
    public ConfiguredInlineModelProviderSpi() {
        String enabledVal = ConfigurationProvider.getConfiguration().get(MODEL_EANABLED_PARAM);
        boolean enabled = enabledVal == null || "true".equalsIgnoreCase(enabledVal);
        if (enabled) {
            LOG.info("Reading model configuration from config...");
            Map<String,String> config = ConfigurationProvider.getConfiguration().getProperties();
            String owner = config.get("_model.provider");
            if(owner==null){
                owner = config.toString();
            }
            configModels.addAll(ConfigModelReader.loadValidations(owner, config));
        }
        configModels = Collections.unmodifiableList(configModels);
    }


    public Collection<ConfigModel> getConfigModels() {
        return configModels;
    }
}
