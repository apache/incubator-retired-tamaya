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
package org.apache.tamaya.format;

import org.apache.tamaya.spi.ServiceContext;

import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Small accessor and management class dealing with {@link org.apache.tamaya.format.ConfigurationFormat}
 * instances.
 */
public class ConfigurationFormats {
    /**
     * The logger used.
     */
    private static final Logger LOG = Logger.getLogger(ConfigurationFormats.class.getName());

    /**
     * Singleton constructor.
     */
    private ConfigurationFormats() {
    }

    /**
     * Get all currently available formats, ordered by priority.
     *
     * @return the currently available formats, never null.
     */
    public static List<ConfigurationFormat> getFormats() {
        return ServiceContext.getInstance().getServices(ConfigurationFormat.class);
    }

    /**
     * Tries to read configuration data from a given URL, hereby trying all formats in order of precedence.
     *
     * @param url the url from where to read, not null.
     * @return the ConfigurationData read, or null.
     */
    public static ConfigurationData readConfigurationData(URL url) {
        List<ConfigurationFormat> formats = getFormats();
        return readConfigurationData(url, formats.toArray(new ConfigurationFormat[formats.size()]));
    }

    /**
     * Tries to read configuration data from a given URL, hereby trying all given formats in order.
     *
     * @param url     the url from where to read, not null.
     * @param formats the formats to try.
     * @return the ConfigurationData read, or null.
     */
    public static ConfigurationData readConfigurationData(URL url, ConfigurationFormat... formats) {
        ConfigurationData data = null;
        String resource = url.toString();
        for (ConfigurationFormat format : formats) {
            try {
                data = format.readConfiguration(url);
                if (data != null) {
                    return data;
                }
            } catch (Exception e) {
                LOG.log(Level.INFO, e,
                        () -> "Format "+format.getClass().getName()+" failed to read resource " + resource);
            }
        }
        return null;
    }

}
