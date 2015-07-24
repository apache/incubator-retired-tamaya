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

import java.util.*;

/**
 * Data that abstracts the data read from a configuration resources using a certain format. The data can be divided
 * into different sections, similar to ini-files. Herebey different sections the best map to entries with different
 * priorities to be applied, when integrated into PropertySource instances.<br/>
 * New instances of this class can be created using a {@link org.apache.tamaya.format.ConfigurationDataBuilder}.
 * <h3>Implementation Specification</h3>
 * This class is
 * <ul>
 *     <li>immutable</li>
 *     <li>thread-safe</li>
 * </ul>
 */
public final class ConfigurationData {
    /**
     * The properties of the default section (no name).
     */
    private Map<String, String> defaultProperties;
    /**
     * A normalized flattened set of this configuration data.
     */
    private Map<String, String> combinedProperties;
    /**
     * The sections read.
     */
    private Map<String, Map<String, String>> namedSections;
    /** The format instance used to read this instance. */
    private ConfigurationFormat format;
    /** The resource read. */
    private String resource;


    /**
     * COnstructor used by builder.
     * @param builder the builder instance passing the read configuration data.
     */
    ConfigurationData(ConfigurationDataBuilder builder){
        this.format = builder.format;
        this.resource = builder.resource;
        if (builder.defaultProperties != null) {
            this.defaultProperties = new HashMap<>();
            this.defaultProperties.putAll(builder.defaultProperties);
        }
        if (builder.combinedProperties != null) {
            this.combinedProperties = new HashMap<>();
            this.combinedProperties.putAll(builder.combinedProperties);
        }
        if (builder.namedSections != null) {
            this.namedSections = new HashMap<>();
            this.namedSections.putAll(builder.namedSections);
        }
        if (this.combinedProperties == null || this.combinedProperties.isEmpty()) {
            this.combinedProperties = new HashMap<>();
            this.combinedProperties.putAll(getDefaultProperties());
            // popuilate it with sections...
            for (String sectionName : getSectionNames()) {
                Map<String, String> section = getSection(sectionName);
                for (Map.Entry<String, String> en : section.entrySet()) {
                    String key = sectionName + '.' + en.getKey();
                    combinedProperties.put(key, en.getValue());
                }
            }
        }
    }

    /**
     * Get the {@link org.apache.tamaya.format.ConfigurationFormat} that read this data.
     * @return the {@link org.apache.tamaya.format.ConfigurationFormat} that read this data, never null.
     */
    public ConfigurationFormat getFormat(){
        return format;
    }

    /**
     * Get the resource from which this data was read.
     * @return the resource from which this data was read, never null.
     */
    public String getResource(){
        return resource;
    }

    /**
     * Access an immutable Set of all present section names, including the default section (if any).
     * @return the set of present section names, never null.
     */
    public Set<String> getSectionNames() {
        if (namedSections == null) {
            return Collections.emptySet();
        }
        return namedSections.keySet();
    }

    /**
     * Get a section's data.
     * @param name the section name, not null.
     * @return the data of this section, or null, if no such section exists.
     */
    public Map<String, String> getSection(String name) {
        return this.namedSections.get(name);
    }

    /**
     * Convenience accessor for accessing the default section.
     * @return the default section's data, or null, if no such section exists.
     */
    public Map<String, String> getDefaultProperties() {
        if (defaultProperties == null) {
            return Collections.emptyMap();
        }
        return defaultProperties;
    }

    /**
     * Get combined properties for this config data instance. If
     *
     * @return the normalized properties.
     */
    public Map<String, String> getCombinedProperties() {
        if (combinedProperties == null) {
            return Collections.emptyMap();
        }
        return combinedProperties;
    }

    /**
     * Accessor used for easily creating a new builder based on a given data instance.
     *
     * @return the data contained, never null.
     */
    public Map<String, Map<String, String>> getSections() {
        if (namedSections == null) {
            return Collections.emptyMap();
        }
        return namedSections;
    }

    /**
     * Immutable accessor to ckeck, if there are default properties present.
     *
     * @return true, if default properties are present.
     */
    public boolean hasDefaultProperties() {
        return this.defaultProperties != null && !this.defaultProperties.isEmpty();
    }

    /**
     * Immutable accessor to ckeck, if there are combined properties set.
     *
     * @return true, if combined properties are set.
     */
    public boolean hasCombinedProperties() {
        return this.combinedProperties != null && !this.combinedProperties.isEmpty();
    }

    /**
     * Immutable accessor to ckeck, if there are named sections present.
     *
     * @return true, if at least one named section is present.
     */
    private boolean hasSections() {
        return this.namedSections != null && !this.namedSections.isEmpty();
    }

    /**
     * Checks if no properties are contained in this data item.
     *
     * @return true, if no properties are contained in this data item.
     */
    public boolean isEmpty() {
        return !hasCombinedProperties() && !hasDefaultProperties() && !hasSections();
    }

    @Override
    public String toString() {
        return "ConfigurationData{" +
                "format=" + format +
                ", default properties=" + defaultProperties +
                ", combined properties=" + combinedProperties +
                ", sections=" + namedSections +
                ", resource=" + resource +
                '}';
    }


}
