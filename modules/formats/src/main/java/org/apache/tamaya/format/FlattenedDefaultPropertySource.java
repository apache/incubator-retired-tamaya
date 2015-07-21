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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Flattened default PropertySource that uses the flattened config data read from an URL by a
 * ${@link org.apache.tamaya.format.ConfigurationFormat}.
 */
public class FlattenedDefaultPropertySource implements PropertySource {
    private static final Logger LOG = Logger.getLogger(FlattenedDefaultPropertySource.class.getName());
    private Map<String, String> properties;
    private ConfigurationData data;
    private int defaultOrdinal = 0;


    /*
     * Constructor, uses hereby the flattened config data read from an URL by a
     * ${@link org.apache.tamaya.format.ConfigurationFormat}, and if not present falls back to the default section.
     */
    public FlattenedDefaultPropertySource(ConfigurationData data) {
        this.properties = populateData(data);
        this.data = data;
    }

    /*
     * Constructor, uses hereby the flattened config data read from an URL by a
     * ${@link org.apache.tamaya.format.ConfigurationFormat}, and if not present falls back to the default section.
     */
    public FlattenedDefaultPropertySource(int defaultOrdinal, ConfigurationData data) {
        this.properties = populateData(data);
        this.data = data;
        this.defaultOrdinal = defaultOrdinal;
    }

    protected Map<String, String> populateData(ConfigurationData data) {
        Map<String, String> result = data.getCombinedProperties();
        if (result.isEmpty()) {
            result = data.getDefaultProperties();
        }
        if (result.isEmpty()) {
            result = new HashMap<>();
        }
        if(result.isEmpty()){
            for (String section : data.getSectionNames()) {
                Map<String,String> sectionMap = data.getSection(section);
                for(Map.Entry<String,String> en: sectionMap.entrySet()){
                    result.put(section + '.' + en.getKey(), en.getValue());
                }
            }
        }
        return Collections.unmodifiableMap(result);
    }

    @Override
    public String getName() {
        String name = this.properties.get("[meta].name");
        if (name == null) {
            name = this.data.getResource();
        }
        if (name == null) {
            name = getClass().getSimpleName();
        }
        return name;
    }

    @Override
    public int getOrdinal() {
        String ordinalValue = this.properties.get(PropertySource.TAMAYA_ORDINAL);
        if (ordinalValue != null) {
            try {
                return Integer.parseInt(ordinalValue.trim());
            } catch (Exception e) {
                LOG.log(Level.WARNING, "Failed to parse Tamaya ordinal from " + data.getResource(), e);
            }
        }
        return defaultOrdinal;
    }

    @Override
    public String get(String key) {
        return properties.get(key);
    }

    @Override
    public Map<String, String> getProperties() {
        return properties;
    }

    @Override
    public boolean isScannable() {
        return true;
    }
}
