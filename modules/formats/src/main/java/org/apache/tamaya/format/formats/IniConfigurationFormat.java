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
package org.apache.tamaya.format.formats;

import org.apache.tamaya.ConfigException;
import org.apache.tamaya.format.ConfigurationData;
import org.apache.tamaya.format.ConfigurationDataBuilder;
import org.apache.tamaya.format.ConfigurationFormat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implements a ini file format.
 */
public class IniConfigurationFormat implements ConfigurationFormat {

    /**
     * The logger.
     */
    private final static Logger LOG = Logger.getLogger(IniConfigurationFormat.class.getName());

    @Override
    public ConfigurationData readConfiguration(URL url) {
        ConfigurationDataBuilder builder = ConfigurationDataBuilder.of(url, this);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));) {
            String line = reader.readLine();
            int lineNum = 0;
            String section = null;
            while (line != null) {
                lineNum++;
                line = line.trim();
                if (line.isEmpty()) {
                    line = reader.readLine();
                    continue;
                }
                if (line.startsWith("[")) {
                    int end = line.indexOf(']');
                    if (end < 0) {
                        throw new ConfigException(
                                "Invalid INI-Format, ']' expected, at " + lineNum + " in " + url);
                    }
                    section = line.substring(1, end);
                } else if (line.trim().startsWith("#")) {
                    // comment
                } else {
                    int sep = line.indexOf('=');
                    String key = line.substring(0, sep);
                    String value = line.substring(sep + 1);
                    if (section != null) {
                        builder.addProperty(ConfigurationData.FLATTENED_SECTION_NAME, section + '.' + key, value);
                        builder.addProperty(section, key, value);
                    } else {
                        builder.addProperty(key, value);
                    }
                }
                line = reader.readLine();
            }
            return builder.build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, e, () -> "Could not read configuration: " + url);
        }
        return null;
    }
}
