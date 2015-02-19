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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
     * The default entry type returned if a format implementation does not support any explicit entry types.
     */
    public static final String DEFAULT_SECTION_NAME = "default";

    /**
     * Name of a section that should be written by every format implementation if possible, hereby mapping the
     * complete configuration structure to one flattened key/value layer. This section is used for creating
     * default PropertySource for a ConfigurationData instance.
     */
    public static final String FLATTENED_SECTION_NAME = "_flattened";
    /** The format instance used to read this instance. */
    private ConfigurationFormat format;
    /** The resource read. */
    private String resource;
    /** The data read. */
    private Map<String,Map<String,String>> data = new HashMap<>();

    /**
     * COnstructor used by builder.
     * @param builder the builder instance passing the read configuration data.
     */
    ConfigurationData(ConfigurationDataBuilder builder){
        this.format = builder.format;
        this.data.putAll(builder.data);
        this.resource = builder.resource;
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
    public Set<String> getSections(){
        return data.keySet();
    }

    /**
     * Get a section's data.
     * @param name the section name, not null.
     * @return the data of this section, or null, if no such section exists.
     */
    public Map<String,String> getSection(String name){
        return this.data.get(name);
    }

    /**
     * Convenience accessor for accessing the default section.
     * @return the default section's data, or null, if no such section exists.
     */
    public Map<String,String> getDefaultSection(){
        return getSection(DEFAULT_SECTION_NAME);
    }

    @Override
    public String toString() {
        return "ConfigurationData{" +
                "format=" + format +
                ", data=" + data +
                ", resource=" + resource +
                '}';
    }

    /**
     * Accessor used for easily creating a new builder based on a given data instance.
     * @return the data contained, never null.
     */
    Map<String,Map<String,String>> getData() {
        return data;
    }
}
