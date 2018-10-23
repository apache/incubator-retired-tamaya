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

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Builder to create a {@link org.apache.tamaya.spi.PropertyValue} instance.
 * @deprecated Use {@link PropertyValue} directly.
 */
@Deprecated
public final class PropertyValueBuilder {
    /** The key accessed. */
    protected String key;
    /** The property value. */
    protected String value;
    /** The property value. */
    protected String source;
    /** additional metadata entries (optional). */
    protected Map<String,Object> metaEntries = new HashMap<>();
    /** The getParent getChild, null if it's a root getChild. */
    protected PropertyValue parent;
    /** The getChild's getIndex, if the getChild is participating in a list structure. */
    protected int index = -1;
    /** Helper structure used for indexing new list getChildren. */
    protected Map<String, AtomicInteger> indices = new HashMap<>();

    /**
     * Create a new builder instance, for a given setCurrent of parameters.
     * Before calling build at least a {@link #value}
     * must be setCurrent.
     */
    PropertyValueBuilder(String key, String value){
        this.key = Objects.requireNonNull(key);
        this.value = value;
    }

    /**
     * Replaces/sets the context data.
     * @param metaEntries the context data to be applied, not {@code null}.
     * @return the builder for chaining.
     */
    public PropertyValueBuilder setMeta(Map<String, Object> metaEntries) {
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

        this.metaEntries.put(key, value);
        return this;
    }

    /**
     * Add an additional context data information.
     * @param type the context data type, used as key, not {@code null}.
     * @param value the context value, not {@code null}.
     * @param <T> the type of the class modeled by the type parameter
     * @return the builder for chaining.
     */
    public <T> PropertyValueBuilder addMetaEntry(Class<T> type, T value) {
        Objects.requireNonNull(type, "Meta key must be given.");
        Objects.requireNonNull(value, "Meta value must be given.");

        this.metaEntries.put(type.toString(), value);
        return this;
    }

    /**
     * Add an additional context data information, using the data's class name as key.
     * @param value the context value, not {@code null}.
     * @param <T> the type of the class modeled by the type parameter
     * @return the builder for chaining.
     */
    public <T> PropertyValueBuilder addMetaEntry(T value) {
        Objects.requireNonNull(value, "Meta value must be given.");

        this.metaEntries.put(value.getClass().toString(), value);
        return this;
    }

    /**
     * Adds the context data given.
     * @param metaEntries the context data to be applied, not {@code null}.
     * @return the builder for chaining.
     */
    public PropertyValueBuilder addMetaEntries(Map<String, Object> metaEntries) {
        this.metaEntries.putAll(metaEntries);
        return this;
    }

    /**
     * Removes a getMeta entry.
     * @param key the entry's key, not {@code null}.
     * @return the builder for chaining.
     */
    public PropertyValueBuilder removeMeta(String key) {
        Objects.requireNonNull(key, "Key must be given.");

        this.metaEntries.remove(key);
        return this;
    }

    /**
     * Get the value's context data.
     * @return the context data, not {@code null}.
     */
    public Map<String,Object> getMetaEntries() {
        return Collections.unmodifiableMap(this.metaEntries);
    }

    /**
     * Get the value's context data.
     * @param <T> the type of the class modeled by the type parameter
     * @return the context data, not {@code null}.
     */
    public <T> T getMeta(String key) {
        return (T)this.metaEntries.get(key);
    }

    /**
     * Get the value's context data.
     * @param <T> the type of the class modeled by the type parameter
     * @param type the target type, not null.
     * @return the context data, not {@code null}.
     */
    public <T> T getMeta(Class<T> type) {
        return (T)this.metaEntries.get(type.toString());
    }

    /**
     * Changes the entry's key, mapping also corresponding context entries.
     * @param key the new key, not {@code null}.
     * @return the builder for chaining.
     */
    public PropertyValueBuilder mapKey(String key) {
        // todo obf if (1==1) throw new RuntimeException("No tests written.");
        Map<String,Object> newContext = new HashMap<>();
        for(Map.Entry<String,Object> en:this.metaEntries.entrySet()){
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
     * @deprecated Use {@link #addMetaEntry(String, Object)} (String, Object)}
     */
    @Deprecated
    public PropertyValueBuilder setSource(String source) {
        if(source!=null) {
            this.source = source;
        }
        return this;
    }

    /**
     * Creates a new immutable {@link org.apache.tamaya.spi.PropertyValue}.
     * @return a new immutable {@link org.apache.tamaya.spi.PropertyValue}, never {@code null}.
     */
    public PropertyValue build(){
        return PropertyValue.of(key, value, source).setMeta(metaEntries);
    }

    @Override
    public String toString() {
        return "PropertyValueBuilder{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                ", source='" + source + '\'' +
                ", metaEntries=" + metaEntries +
                '}';
    }

}
