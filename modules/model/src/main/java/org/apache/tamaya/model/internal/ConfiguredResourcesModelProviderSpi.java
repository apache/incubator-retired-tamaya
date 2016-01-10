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
import org.apache.tamaya.format.ConfigurationData;
import org.apache.tamaya.format.ConfigurationFormats;
import org.apache.tamaya.model.ConfigModel;
import org.apache.tamaya.model.spi.ConfigModelReader;
import org.apache.tamaya.model.spi.ModelProviderSpi;
import org.apache.tamaya.resource.ConfigResources;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ConfigModel provider that reads model metadata from property files from
 * {@code classpath*:META-INF/configmodel.json} in the following format:
 * <pre>
 * </pre>
 */
public class ConfiguredResourcesModelProviderSpi implements ModelProviderSpi {

    /** The logger. */
    private static final Logger LOG = Logger.getLogger(ConfiguredResourcesModelProviderSpi.class.getName());
    /** The parameter that can be used to configure the location of the configuration model resources. */
    private static final String MODEL_RESOURCE_PARAM = "org.apache.tamaya.model.resources";
    /** The resource class to checked for testing the availability of the resources extension module. */
    private static final String CONFIG_RESOURCE_CLASS = "org.apache.tamaya.resource.ConfigResource";
    /** The resource class to checked for testing the availability of the formats extension module. */
    private static final String CONFIGURATION_FORMATS_CLASS = "org.apache.tamaya.format.ConfigurationFormats";
    /** Initializes the flag showing if the formats module is present (required). */
    private static final boolean AVAILABLE = checkAvailabilityFormats();
    /** Initializes the flag showing if the resources module is present (optional). */
    private static final boolean RESOURCES_EXTENSION_AVAILABLE = checkAvailabilityResources();

    /** The configModels read. */
    private List<ConfigModel> configModels = new ArrayList<>();

    /** Initializes the flag showing if the formats module is present (required). */
    private static boolean checkAvailabilityFormats() {
        try {
            Class.forName(CONFIGURATION_FORMATS_CLASS);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /** Initializes the flag showing if the resources module is present (optional). */
    private static boolean checkAvailabilityResources() {
        try {
            Class.forName(CONFIG_RESOURCE_CLASS);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Constructor, mostly called from {@link java.util.ServiceLoader}
     */
    public ConfiguredResourcesModelProviderSpi() {
        if (!AVAILABLE) {
            LOG.info("tamaya-format extension is required to read model configuration, No extended model support AVAILABLE.");
        } else {
            String resources = ConfigurationProvider.getConfiguration().get(MODEL_RESOURCE_PARAM);
            if(resources==null || resources.trim().isEmpty()){
                LOG.info("Mo model resources location configured in " + MODEL_RESOURCE_PARAM + ".");
                return;
            }
            Collection<URL> urls;
            if(RESOURCES_EXTENSION_AVAILABLE){
                LOG.info("Using tamaya-resources extension to read model configuration from " + resources);
                urls = ConfigResources.getResourceResolver().getResources(resources.split(","));
            } else{
                LOG.info("Using default classloader resource location to read model configuration from " + resources);
                urls = new ArrayList<>();
                for(String resource:resources.split(",")){
                    if(!resource.trim().isEmpty()){
                        Enumeration<URL> configs;
                        try {
                            configs = getClass().getClassLoader().getResources(resource);
                            while (configs.hasMoreElements()) {
                                urls.add(configs.nextElement());
                            }
                        } catch (IOException e) {
                            Logger.getLogger(getClass().getName()).log(Level.SEVERE,
                                    "Error evaluating config model locations from "+resource, e);
                        }
                    }
                }
            }
            // Reading configs
            for(URL config:urls){
                try (InputStream is = config.openStream()) {
                    ConfigurationData data = ConfigurationFormats.readConfigurationData(config);
                    configModels.addAll(ConfigModelReader.loadValidations(data.getCombinedProperties(), config.toString()));
                } catch (Exception e) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE,
                            "Error loading config model data from " + config, e);
                }
            }
        }
        configModels = Collections.unmodifiableList(configModels);
    }


    public Collection<ConfigModel> getConfigModels() {
        return configModels;
    }
}
