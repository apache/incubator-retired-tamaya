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
 * Builder for creating {@link org.apache.tamaya.format.ConfigurationData} instances. This class is not thread-safe.
 */
public final class ConfigurationDataBuilder {

    /** The format instance used to read this instance. */
    final ConfigurationFormat format;
    /** The resource read. */
    final String resource;
    /**
     * The properties of the default section (no name).
     */
    Map<String, String> defaultProperties;
    /**
     * A normalized flattened set of this configuration data.
     */
    Map<String, String> combinedProperties;
    /**
     * The sections read.
     */
    Map<String, Map<String, String>> namedSections;

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
     * @return new instance of this class.
     */
    public static ConfigurationDataBuilder of(String resource, ConfigurationFormat format){
        return new ConfigurationDataBuilder(resource, format);
    }

    /**
     * Creates a new instance.
     * @param data an existing ConfigurationData instances used to initialize the builder.
     * @return new instance of this class from the given configuration.
     */
    public static ConfigurationDataBuilder of(ConfigurationData data){
        ConfigurationDataBuilder b = new ConfigurationDataBuilder(data.getResource(), data.getFormat());
        if (data.hasDefaultProperties()) {
            b.getDefaultProperties().putAll(data.getDefaultProperties());
        }
        if (data.hasCombinedProperties()) {
            b.getCombinedProperties().putAll(data.getCombinedProperties());
        }
        if (!data.getSections().isEmpty()) {
            b.getSections().putAll(data.getSections());
        }
        return b;
    }

    /**
     * Adds (empty) sections,if they are not yet existing. Already existing sections will not be touched.
     * @param sections the new sections to put.
     * @return the builder for chaining.
     */
    public ConfigurationDataBuilder addSections(String... sections){
        for (String section : sections) {
            if (!getSections().containsKey(section)) {
                getSections().put(section, new HashMap<String, String>());
            }
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
    public ConfigurationDataBuilder addSectionProperty(String section, String key, String value) {
        Map<String, String> map = getSections().get(section);
        if (map == null) {
            map = new HashMap<>();
            getSections().put(section, map);
        }
        map.put(key, value);
        return this;
    }

    /**
     * Adds a single entry to the <i>default</i> section.
     * @param key the entry's key
     * @param value the entry's value
     * @return the builder for chaining.
     */
    public ConfigurationDataBuilder addProperty(String key, String value) {
        getDefaultProperties().put(key, value);
        return this;
    }

    /**
     * Adds the given entries to the given section, all existing values will be overridden.
     * @param section the target section (will be created if not existing).
     * @param properties the entry's data
     * @return the builder for chaining.
     */
    public ConfigurationDataBuilder addSectionProperties(String section, Map<String, String> properties) {
        Map<String, String> map = getSections().get(section);
        if (map == null) {
            map = new HashMap<>();
            getSections().put(section, map);
        }
        map.putAll(properties);
        return this;
    }

    /**
     * Adds the given entries to the <i>default</i> section, all existing values will be overridden.
     * @param properties the entry's data
     * @return the builder for chaining.
     */
    public ConfigurationDataBuilder addProperties(Map<String, String> properties) {
        getDefaultProperties().putAll(properties);
        return this;
    }

    /**
     * Sets the given entries as the <i>combined</i> properties map, all existing properties of the
     * combined map will be overridden.
     *
     * @param properties the entry's data
     * @return the builder for chaining.
     */
    public ConfigurationDataBuilder setCombinedProperties(Map<String, String> properties) {
        this.combinedProperties = new HashMap<>(properties);
        return this;
    }

    /**
     * Access the current default section, if not present a new instance is initialized.
     *
     * @return the current default section, never null.
     */
    public Map<String, String> getDefaultProperties() {
        if (defaultProperties == null) {
            defaultProperties = new HashMap<>();
        }
        return defaultProperties;
    }

    /**
     * Access the current combined properties, if not present a new instance is initialized.
     *
     * @return the current combined properties, never null.
     */
    public Map<String, String> getCombinedProperties() {
        if (combinedProperties == null) {
            combinedProperties = new HashMap<>();
        }
        return combinedProperties;
    }

    /**
     * Access the current named sections, if not present a new instance is initialized.
     *
     * @return the current named sections, never null.
     */
    public Map<String, Map<String, String>> getSections() {
        if (namedSections == null) {
            namedSections = new HashMap<>();
        }
        return namedSections;
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
                ", default properties=" + defaultProperties +
                ", sections=" + namedSections +
                ", combined properties=" + combinedProperties +
                ", resource=" + resource +
                '}';
    }
}
