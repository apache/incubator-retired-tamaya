/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tamaya.spi;

import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

/**
 * Builder to create a {@link ConfigValue} instance.
 */
public class ConfigValueBuilder {
    /** The key accessed. */
    String key;
    /** The property value. */
    String value;
    /** additional metadata entries (optional). */
    String metaEntry;

    /**
     * Create a new builder instance, for a given set of parameters.
     * Before calling build at least a {@link #value} and its {@link #metaEntry}
     * must be set.
     */
    ConfigValueBuilder(String key){
        this.key = Objects.requireNonNull(key);
    }

    /**
     * Create a new builder instance, for a given set of parameters.
     * @param key to access a property value, not  {@code null}.
     * @param value property value.
     */
    ConfigValueBuilder(String key, String value) {
        this.key = Objects.requireNonNull(key);
        this.value = Objects.requireNonNull(value);
    }

    /**
     * Create a new builder instance, for a given set of parameters.
     *
     * @param key to access a property value.
     * @param value the value, not {@code null}. If a value is  {@code null}
     *              {@link javax.config.spi.ConfigSource#getValue(String)} should return {@code null}.
     * @param metaEntry property metaEntry.
     */
    ConfigValueBuilder(String key, String value, String metaEntry) {
        this.key = Objects.requireNonNull(key);
        this.value = value;
        this.metaEntry = Objects.requireNonNull(metaEntry);
    }

    /**
     * Replaces/sets the context data.
     * @param metaEntry the context data to be applied.
     * @return the builder for chaining.
     */
    public ConfigValueBuilder setMetaEntry(String metaEntry) {
        this.metaEntry = metaEntry;
        return this;
    }

    /**
     * Add an additional context data information.
     * @param key the context data key, not {@code null}.
     * @param value the context value, not {@code null} (will be converted to String).
     * @return the builder for chaining.
     */
    public ConfigValueBuilder addMetaEntry(String key, String value) {
        Objects.requireNonNull(key, "Meta key must be given.");
        Objects.requireNonNull(value, "Meta value must be given.");
        if(metaEntry==null){
            metaEntry = key+"="+value;
        }else{
            metaEntry = "\n" + key+"="+value;
        }
        return this;
    }

    /**
     * Adds the context data given.
     * @param metaEntries the context data to be applied, not {@code null}.
     * @return the builder for chaining.
     */
    public ConfigValueBuilder addMetaEntries(Map<String, String> metaEntries) {
        Properties props = new Properties();
        props.putAll(metaEntries);
        StringWriter stringWriter = new StringWriter();
        try {
            props.store(stringWriter, null);
            stringWriter.flush();
            if(metaEntry==null){
                metaEntry = stringWriter.toString();
            }else{
                metaEntry += '\n' + stringWriter.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * Adds the context data given as JSON object.
     * @param meta the context data in JSON format, not {@code null}.
     * @return the builder for chaining.
     */
    public ConfigValueBuilder addMetaEntry(String meta) {
        if(metaEntry==null){
            metaEntry = meta;
        }else{
            metaEntry += '\n' + meta;
        }
        return this;
    }

    /**
     * Get the value's context data.
     * @return the context data, not {@code null}.
     */
    public String getMetaEntry() {
        return metaEntry;
    }

    /**
     * Sets a new key.
     * @param key the new key, not {@code null}.
     * @return the builder for chaining.
     */
    public ConfigValueBuilder setKey(String key) {
        this.key = Objects.requireNonNull(key);
        return this;
    }

    /**
     * Sets a new value.
     * @param value the new value, not {@code null}.
     * @return the builder for chaining.
     */
    public ConfigValueBuilder setValue(String value) {
        this.value = Objects.requireNonNull(value, "Value must be given.");

        return this;
    }

    /**
     * Creates a new immutable {@link ConfigValue}.
     * @return a new immutable {@link ConfigValue}, never {@code null}.
     */
    public ConfigValue build(){
        return new ConfigValue(this);
    }

    @Override
    public String toString() {
        return "PropertyValueBuilder{" +
                "key='" + key + '\'' +
                "value='" + value + '\'' +
                ", metaEntry=" + metaEntry +
                '}';
    }

}
