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

import org.apache.tamaya.format.ConfigurationData;
import org.apache.tamaya.format.ConfigurationDataBuilder;
import org.apache.tamaya.format.ConfigurationFormat;

import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of a {@link org.apache.tamaya.format.ConfigurationFormat} for xml property
 * files.
 *
 * @see java.util.Properties#loadFromXML(java.io.InputStream)
 */
public class PropertiesXmlFormat implements ConfigurationFormat {
    /**
     * The logger.
     */
    private final static Logger LOG = Logger.getLogger(PropertiesXmlFormat.class.getName());

    @Override
    public String getName() {
        return "xml-properties";
    }

    @Override
    public boolean accepts(URL url) {
        String fileName = url.getFile();
        return fileName.endsWith(".xml") || fileName.endsWith(".XML");
    }

    @SuppressWarnings("unchecked")
    @Override
    public ConfigurationData readConfiguration(String resource, InputStream inputStream) {
        Objects.requireNonNull(inputStream);
        Objects.requireNonNull(resource);

        try {
            final Properties p = new Properties();
            p.loadFromXML(inputStream);
            return ConfigurationDataBuilder.of(resource, this).addProperties(Map.class.cast(p)).build();
        } catch (Exception e) {
            LOG.log(Level.FINEST, e, () -> "Failed to read config from resource: " + resource);
        }
        return null;
    }
}
