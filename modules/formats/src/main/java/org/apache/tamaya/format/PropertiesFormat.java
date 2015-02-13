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

import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of a {@link ConfigurationFormat} for -properties files.
 *
 * @see java.util.Properties#load(java.io.InputStream)
 */
public class PropertiesFormat implements ConfigurationFormat {
    /**
     * The logger.
     */
    private final static Logger LOG = Logger.getLogger(PropertiesFormat.class.getName());


    @SuppressWarnings("unchecked")
    @Override
    public ConfigurationData readConfiguration(URL url) {
        Objects.requireNonNull(url);

        try (InputStream is = url.openStream()) {
            if (is != null) {
                final Properties p = new Properties();
                p.load(is);
                return ConfigurationDataBuilder.of(url, this).addProperties( Map.class.cast(p)).build();
            }
        } catch (Exception e) {
            LOG.log(Level.FINEST, e, () -> "Failed to read config from resource: " + url);
        }
        return ConfigurationDataBuilder.of(url, this).build();
    }
}
