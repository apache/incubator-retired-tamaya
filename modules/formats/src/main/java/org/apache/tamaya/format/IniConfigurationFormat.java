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

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.apache.commons.configuration.SubnodeConfiguration;
import org.apache.tamaya.ConfigException;

import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Implements a ini file format based on the APache Commons
 * {@link org.apache.commons.configuration.HierarchicalINIConfiguration}.
 */
public class IniConfigurationFormat implements ConfigurationFormat{

    private Set<String> entryTypes = new HashSet<>();

    public IniConfigurationFormat(){
        entryTypes.add(ConfigurationFormat.DYNAMIC_ENTRY_TYPE);
        entryTypes = Collections.unmodifiableSet(entryTypes);
    }

    @Override
    public Set<String> getEntryTypes() {
        return entryTypes;
    }

    @Override
    public Map<String, Map<String, String>> readConfiguration(URL url) {
        Map<String, Map<String, String>> result = new HashMap<>();
        try {
            HierarchicalINIConfiguration commonIniConfiguration = new HierarchicalINIConfiguration(url);
            for(String section:commonIniConfiguration.getSections()){
                SubnodeConfiguration sectionConfig = commonIniConfiguration.getSection(section);
                Map<String, String> properties = new HashMap<>();
                Iterator<String> keyIter = sectionConfig.getKeys();
                while(keyIter.hasNext()){
                    String key = keyIter.next();
                    properties.put(key, sectionConfig.getString(key));
                }
                result.put(section, properties);
            }
            return result;
        } catch (ConfigurationException e) {
            throw new ConfigException("Failed to parse ini-file format from " + url, e);
        }
    }
}
