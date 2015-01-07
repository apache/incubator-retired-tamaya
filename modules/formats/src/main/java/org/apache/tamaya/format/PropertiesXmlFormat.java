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

import org.apache.tamaya.spi.PropertySource;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of a {@link ConfigurationFormat} for xml property
 * files.
 *
 * @see java.util.Properties#loadFromXML(java.io.InputStream)
 */
public class PropertiesXmlFormat implements ConfigurationFormat {
    /**
     * The logger.
     */
    private final static Logger LOG = Logger.getLogger(PropertiesXmlFormat.class.getName());

    /**
     * The target ordinal.
     */
    private int ordinal;

    /**
     * Creates a new format instance, producing entries for the given ordinal, if not overridden by a
     * config entry itself.
     * TODO document and implement override feature
     *
     * @param ordinal the target ordinal.
     */
    public PropertiesXmlFormat(int ordinal) {
        this.ordinal = ordinal;
    }

    /**
     * Get the target ordinal, produced by this format.
     *
     * @return the target ordinal
     */
    public int getOrdinal() {
        return ordinal;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<PropertySource> readConfiguration(URL url) {
        final String name;
        if (Objects.requireNonNull(url).getQuery() == null) {
            name = "XML-Properties(" + Objects.requireNonNull(url).toString() + ')';
        } else {
            name = Objects.requireNonNull(url).getQuery();
        }
        List<PropertySource> propertySources = new ArrayList<>();
        try (InputStream is = url.openStream()) {
            if (is != null) {
                final Properties p = new Properties();
                p.loadFromXML(is);
                propertySources.add(new PropertySource() {
                    @Override
                    public int getOrdinal() {
                        return ordinal;
                    }

                    @Override
                    public String getName() {
                        return name;
                    }

                    @Override
                    public String get(String key) {
                        return p.getProperty(key);
                    }

                    @Override
                    public Map<String, String> getProperties() {
                        return Map.class.cast(p);
                    }
                });
                return propertySources;
            }
        } catch (Exception e) {
            LOG.log(Level.FINEST, e, () -> "Failed to read config from resource: " + url);
        }
        return Collections.emptyList();
    }

    @Override
    public String toString() {
        return "PropertiesXmlFormat{" +
                "ordinal=" + ordinal +
                '}';
    }
}
