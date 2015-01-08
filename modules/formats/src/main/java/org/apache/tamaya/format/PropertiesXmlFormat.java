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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
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
     * Creates a new format instance.
     */
    public PropertiesXmlFormat() { }

    @Override
    public Set<String> getEntryTypes() {
        Set<String> set = new HashSet<>();
        set.add(ConfigurationFormat.DEFAULT_ENTRY_TYPE);
        return set;
    }


    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Map<String, String>> readConfiguration(URL url) {
        final String name;
        if (Objects.requireNonNull(url).getQuery() == null) {
            name = "XML-Properties(" + Objects.requireNonNull(url).toString() + ')';
        } else {
            name = Objects.requireNonNull(url).getQuery();
        }
        Map<String, Map<String, String>> result = new HashMap<>();
        try (InputStream is = url.openStream()) {
            if (is != null) {
                final Properties p = new Properties();
                p.loadFromXML(is);
                result.put(ConfigurationFormat.DEFAULT_ENTRY_TYPE, Map.class.cast(p));
            }
        } catch (Exception e) {
            LOG.log(Level.FINEST, e, () -> "Failed to read config from resource: " + url);
        }
        return result;
    }
}
