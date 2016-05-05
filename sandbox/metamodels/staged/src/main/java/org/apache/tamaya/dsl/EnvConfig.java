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
package org.apache.tamaya.dsl;

import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;
import org.apache.tamaya.format.ConfigurationData;
import org.apache.tamaya.format.ConfigurationFormat;
import org.apache.tamaya.format.ConfigurationFormats;
import org.apache.tamaya.json.YAMLFormat;
import org.apache.tamaya.resource.ConfigResources;
import org.apache.tamaya.spi.ConfigurationContextBuilder;
import org.apache.tamaya.spisupport.DefaultConfiguration;
import org.apache.tamaya.spisupport.MapPropertySource;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Meta environment configuration builder and accessor. Normally this class shoulds never be accessed
 * by client code. But it could be useful for extensions that extend the meta-configuration capabilities
 * of tamaya having access to the meta-configuration, so they can read their own meta-entries to
 * setup whatever features they implement.
 */
public final class EnvConfig {

    private static final Logger LOGGER = Logger.getLogger(EnvConfig.class.getName());
    private static EnvConfig INSTANCE = new EnvConfig();

    private Configuration config;

    private EnvConfig(){
        ConfigurationFormat[] formats = loadFormats();
        ConfigurationContextBuilder builder = ConfigurationProvider.getConfigurationContextBuilder();
        for(URL url:ConfigResources.getResourceResolver().getResources("tamaya-config.*")) {
            for(ConfigurationFormat format:formats) {
                if(format.accepts(url)){
                    try(InputStream is = url.openStream()){
                        ConfigurationData data = format.readConfiguration(url.toString(), is);
                        builder.addPropertySources(new MapPropertySource(
                                url.toString(), data.getCombinedProperties()));
                    }catch(Exception e){
                        LOGGER.log(Level.INFO, "Failed to read " + url + " with format " + format, e);
                    }
                }
            }
        }
        this.config = new DefaultConfiguration(builder.build());
        LOGGER.info("Meta-Configuration read: " + this.config.getProperties().size() + " entries.");
    }

    private ConfigurationFormat[] loadFormats() {
        List<ConfigurationFormat> formats = new ArrayList<>();
        String metaFormats = System.getProperty("tamaya.meta-formats");
        if(metaFormats!=null){
            String[] formatNames = metaFormats.split(",");
            for(String formatName:formatNames){
                formats.addAll(ConfigurationFormats.getFormats(formatName));
            }
        }
        if(formats.isEmpty()){
            formats.addAll(ConfigurationFormats.getFormats("yaml"));
        }
        if(formats.isEmpty()){
            formats.add(new YAMLFormat());
        }
        return formats.toArray(new ConfigurationFormat[formats.size()]);
    }

    /**
     * Access the system's meta-configuration. Normally this class shoulds never be accessed
     * by client code. But it could be useful for extensions that extend the meta-configuration capabilities
     * of tamaya having access to the meta-configuration, so they can read their own meta-entries to
     * setup whatever features they implement.
     * @return the meta-configuration instance used for setting up the Tamaya's application configuration
     * model.
     */
    public static Configuration getMetaConfiguration(){
        return INSTANCE.config;
    }

}
