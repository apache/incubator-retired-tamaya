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
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Meta environment configuration builder and accessor. Normally this class shoulds never be accessed
 * by client code. But it could be useful for extensions that extend the meta-configuration capabilities
 * of tamaya having access to the meta-configuration, so they can read their own meta-entries to
 * setup whatever features they implement.
 */
public final class MetaConfiguration {

    private static final Logger LOGGER = Logger.getLogger(MetaConfiguration.class.getName());
    private static MetaConfiguration INSTANCE = new MetaConfiguration();

    private Configuration config;
    private String resourceExpression;
    private String[] formatNames;

    /**
     * Initializes the metaconfiguration.
     * @param resourceExpression the resource expression that defines the resources to load.
     * @param formatNames the format names to be used.
     */
    private void init(String resourceExpression, String... formatNames){
        if(this.config!=null){
            LOGGER.warning(">>> Reset of Meta-Configuration resource : " + resourceExpression);
            LOGGER.warning(">>> Reset of Meta-Configuration formats  : " + Arrays.toString(formatNames));
        }
        if(resourceExpression==null){
            resourceExpression = "tamaya-config.*";
        }
        LOGGER.info(">>> Meta-Configuration resource : " + resourceExpression);
        ConfigurationFormat[] formats = loadFormats(formatNames);
        ConfigurationContextBuilder builder = ConfigurationProvider.getConfigurationContextBuilder();
        for(URL url:ConfigResources.getResourceResolver().getResources(resourceExpression)) {
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

    private ConfigurationFormat[] loadFormats(String... formatNames) {
        List<ConfigurationFormat> formats = new ArrayList<>();
        if(formatNames.length==0) {
            String metaFormats = System.getProperty("tamaya.meta-formats");
            if (metaFormats != null) {
                formatNames = metaFormats.split(",");
            }
        }
        for (String formatName : formatNames) {
            formats.addAll(ConfigurationFormats.getFormats(formatName));
        }
        if(formats.isEmpty()){
            formats.addAll(ConfigurationFormats.getFormats("yaml"));
        }
        if(formats.isEmpty()){
            formats.add(new YAMLFormat());
        }
        LOGGER.info(">>> Meta-Configuration formats  : " + formats);
        return formats.toArray(new ConfigurationFormat[formats.size()]);
    }

    /**
     * Access the system's meta-configuration, initialize if necessary. Normally this class shoulds never be accessed
     * by client code. But it could be useful for extensions that extend the meta-configuration capabilities
     * of tamaya having access to the meta-configuration, so they can read their own meta-entries to
     * setup whatever features they implement.
     * @return the meta-configuration instance used for setting up the Tamaya's application configuration
     * model.
     */
    public static Configuration getConfiguration(){
        if(INSTANCE.config==null) {
            INSTANCE.init(null);
        }
        return INSTANCE.config;
    }

    /**
     * Access the system's meta-configuration, initialize if necessary. Normally this class shoulds never be accessed
     * by client code. But it could be useful for extensions that extend the meta-configuration capabilities
     * of tamaya having access to the meta-configuration, so they can read their own meta-entries to
     * setup whatever features they implement.
     *
     * @param resourceExpression the resource expression that defines where the metaconfiguration
     *                           files/resources are located.
     * @param formatNames        the formats supported, if null all formats found are tried for each resource(=URL).
     * @return the meta-configuration instance used for setting up the Tamaya's application configuration
     * model.
     */
    public static Configuration getConfiguration(String resourceExpression,
                                                 String... formatNames){
        if(INSTANCE.config==null) {
            INSTANCE.init(resourceExpression, formatNames);
        }
        return INSTANCE.config;
    }

}
