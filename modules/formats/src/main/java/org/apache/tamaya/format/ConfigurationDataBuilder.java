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
import java.util.Objects;


/**
 * Builder for creating {@link org.apache.tamaya.format.ConfigurationData} instances. This class is not thread-safe.
 */
public final class ConfigurationDataBuilder {

    /** The format instance used to read this instance. */
    ConfigurationFormat format;
    /** The resource read. */
    String resource;
    /** The data read. */
    Map<String,Map<String,String>> data = new HashMap<>();

    /**
     * Private constructor.
     * @param resource the configuration resource URL, not null.
     * @param format the format that read this data, not null.
     */
    private ConfigurationDataBuilder(String resource, ConfigurationFormat format){
        this.format = Objects.requireNonNull(format);
        this.resource = Objects.requireNonNull(resource);
    }

    /**
     * Creates a new instance.
     * @param resource the configuration resource URL, not null.
     * @param format the format that read this data, not null.
     */
    public static ConfigurationDataBuilder of(String resource, ConfigurationFormat format){
        return new ConfigurationDataBuilder(resource, format);
    }

    /**
     * Creates a new instance.
     * @param data an existing COnfigurationData instances used to initialize the builder.
     */
    public static ConfigurationDataBuilder of(ConfigurationData data){
        ConfigurationDataBuilder b = new ConfigurationDataBuilder(data.getResource(), data.getFormat());
        b.data.putAll(data.getData());
        return b;
    }

    /**
     * Adds (empty) sections,if they are not yet existing. Already existing sections will not be touched.
     * @param sections the new sections to add.
     * @return the builder for chaining.
     */
    public ConfigurationDataBuilder addSections(String... sections){
        for(String section:sections) {
            this.data.computeIfAbsent(section, (k) -> new HashMap<>());
        }
        return this;
    }

    /**
     * Adds a single entry to a target section.
     * @param section the target section (will be created if not existing).
     * @param key the entry's key
     * @param value the entry's value
     * @return the builder for chaining.
     */
    public ConfigurationDataBuilder addProperty(String section, String key, String value){
        Map<String,String> map = this.data.computeIfAbsent(section, (k) -> new HashMap<>());
        map.put(key, value);
        return this;
    }

    /**
     * Adds a single entry to the <i>default</i> section.
     * @param key the entry's key
     * @param value the entry's value
     * @return the builder for chaining.
     */
    public ConfigurationDataBuilder addProperty(String key, String value){
        return addProperty(ConfigurationData.DEFAULT_SECTION_NAME, key, value);
    }

    /**
     * Adds the given entries to the given section, all existing values will be overridden.
     * @param section the target section (will be created if not existing).
     * @param properties the entry's data
     * @return the builder for chaining.
     */
    public ConfigurationDataBuilder addProperties(String section, Map<String,String> properties){
        Map<String,String> map = this.data.computeIfAbsent(section, (k) -> new HashMap<>());
        map.putAll(properties);
        return this;
    }

    /**
     * Adds the given entries to the <i>default</i> section, all existing values will be overridden.
     * @param properties the entry's data
     * @return the builder for chaining.
     */
    public ConfigurationDataBuilder addProperties( Map<String,String> properties){
        return addProperties(ConfigurationData.DEFAULT_SECTION_NAME, properties);
    }

    /**
     * Builds a new {@link org.apache.tamaya.format.ConfigurationData} instance.
     * @return a new {@link org.apache.tamaya.format.ConfigurationData} instance, not null.
     */
    public ConfigurationData build(){
        return new ConfigurationData(this);
    }

    @Override
    public String toString() {
        return "ConfigurationDataBuilder{" +
                "format=" + format +
                ", data=" + data +
                ", resource=" + resource +
                '}';
    }
}
