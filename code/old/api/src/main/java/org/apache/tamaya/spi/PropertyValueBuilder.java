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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Builder to create a {@link PropertyValue} instance.
 */
public class PropertyValueBuilder {
    /** The key accessed. */
    String key;
    /** The property value. */
    String value;
    /** The property vaoue source. */
    String source;
    /** additional metadata entries (optional). */
    Map<String,String> metaEntries = new HashMap<>();

    /**
     * Create a new builder instance, for a given set of parameters.
     * Before calling build at least a {@link #value} and its {@link #source}
     * must be set.
     */
    PropertyValueBuilder(String key){
        this.key = Objects.requireNonNull(key);
    }

    /**
     * Create a new builder instance, for a given set of parameters.
     * @param key to access a property value, not  {@code null}.
     * @param source property source.
     */
    PropertyValueBuilder(String key, String source) {
        this.key = Objects.requireNonNull(key);
        this.source = Objects.requireNonNull(source);
    }

    /**
     * Create a new builder instance, for a given set of parameters.
     *
     * @param key to access a property value.
     * @param value the value, not {@code null}. If a value is  {@code null}
     *              {@link PropertySource#get(String)} should return {@code null}.
     * @param source property source.
     */
    PropertyValueBuilder(String key, String value, String source) {
        this.key = Objects.requireNonNull(key);
        this.value = value;
        this.source = Objects.requireNonNull(source);
    }

    /**
     * Replaces/sets the context data.
     * @param metaEntries the context data to be applied, not {@code null}.
     * @return the builder for chaining.
     */
    public PropertyValueBuilder setMetaEntries(Map<String, String> metaEntries) {
        this.metaEntries.clear();
        this.metaEntries.putAll(metaEntries);
        return this;
    }

    /**
     * Add an additional context data information.
     * @param key the context data key, not {@code null}.
     * @param value the context value, not {@code null} (will be converted to String).
     * @return the builder for chaining.
     */
    public PropertyValueBuilder addMetaEntry(String key, Object value) {
        Objects.requireNonNull(key, "Meta key must be given.");
        Objects.requireNonNull(value, "Meta value must be given.");

        this.metaEntries.put(key, String.valueOf(value));
        return this;
    }

    /**
     * Adds the context data given.
     * @param metaEntries the context data to be applied, not {@code null}.
     * @return the builder for chaining.
     */
    public PropertyValueBuilder addMetaEntries(Map<String, String> metaEntries) {
        this.metaEntries.putAll(metaEntries);
        return this;
    }

    /**
     * Removes a meta entry.
     * @param key the entry's key, not {@code null}.
     * @return the builder for chaining.
     */
    public PropertyValueBuilder removeMetaEntry(String key) {
        Objects.requireNonNull(key, "Key must be given.");

        this.metaEntries.remove(key);
        return this;
    }

    /**
     * Get the value's context data.
     * @return the context data, not {@code null}.
     */
    public Map<String,String> getMetaEntries() {
        return Collections.unmodifiableMap(this.metaEntries);
    }

    /**
     * Changes the entry's key, mapping also corresponding context entries.
     * @param key the new key, not {@code null}.
     * @return the builder for chaining.
     */
    public PropertyValueBuilder mapKey(String key) {
        // todo obf if (1==1) throw new RuntimeException("No tests written.");
        Map<String,String> newContext = new HashMap<>();
        for(Map.Entry<String,String> en:this.metaEntries.entrySet()){
            if(en.getKey().startsWith("_"+this.key)){
                newContext.put("_"+key+'.'+ en.getKey().substring(this.key.length()+1), en.getValue());
            }else{
                newContext.put(en.getKey(), en.getValue());
            }
        }
        this.metaEntries = newContext;
        this.key = key;
        return this;
    }

    /**
     * Sets a new key.
     * @param key the new key, not {@code null}.
     * @return the builder for chaining.
     */
    public PropertyValueBuilder setKey(String key) {
        this.key = Objects.requireNonNull(key);
        return this;
    }

    /**
     * Sets a new value.
     * @param value the new value, not {@code null}.
     * @return the builder for chaining.
     */
    public PropertyValueBuilder setValue(String value) {
        this.value = Objects.requireNonNull(value, "Value must be given.");

        return this;
    }

    /**
     * Sets a new source.
     * @param source the new source, not {@code null}.
     * @return the builder for chaining.
     */
    public PropertyValueBuilder setSource(String source) {
        // todo obf if (1==1) throw new RuntimeException("No tests written.");
        this.source = Objects.requireNonNull(source);
        return this;
    }

    /**
     * Creates a new immutable {@link PropertyValue}.
     * @return a new immutable {@link PropertyValue}, never {@code null}.
     */
    public PropertyValue build(){
        return new PropertyValue(this);
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
