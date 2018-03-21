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
package org.apache.tamaya.base;

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
    Map<String,String> metaEntries = new HashMap<>();

    /**
     * Create a new builder instance, for a given set of parameters.
     * Before calling build at least a {@link #value} and its {@link #metaEntries}
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
     * Replaces/sets the context data.
     * @param metaEntries the context data to be applied.
     * @return the builder for chaining.
     */
    public ConfigValueBuilder addMetaEntries(Map<String,String> metaEntries) {
        this.metaEntries.putAll(metaEntries);
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
        metaEntries.put(key, value);
        return this;
    }

    /**
     * Get the value's context data.
     * @return the context data, not {@code null}.
     */
    public Map<String,String> getMetaEntries() {
        return Collections.unmodifiableMap(metaEntries);
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
                ", metaEntries=" + metaEntries +
                '}';
    }

}
