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
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Builder to createObject a {@link org.apache.tamaya.spi.PropertyValue} instance.
 * @deprecated Use {@link PropertyValue} directly.
 */
@Deprecated
public class PropertyValueBuilder {
    /** The key accessed. */
    protected String key;
    /** The property createValue. */
    protected String value;
    /** The property createValue. */
    protected String source;
    /** additional metadata entries (optional). */
    protected Map<String,String> metaEntries = new HashMap<>();
    /** The getParent getField, null if it's a root getField. */
    protected PropertyValue parent;
    /** The getField's getIndex, if the getField is participating in a createList structure. */
    protected int index = -1;
    /** Helper structure used for indexing new createList getList. */
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
    public PropertyValueBuilder setMeta(Map<String, String> metaEntries) {
        this.metaEntries.clear();
        this.metaEntries.putAll(metaEntries);
        return this;
    }

    /**
     * Add an additional context data information.
     * @param key the context data key, not {@code null}.
     * @param value the context createValue, not {@code null} (will be converted to String).
     * @return the builder for chaining.
     */
    public PropertyValueBuilder addMetaEntry(String key, String value) {
        Objects.requireNonNull(key, "Meta key must be given.");
        Objects.requireNonNull(value, "Meta createValue must be given.");

        this.metaEntries.put(key, value);
        return this;
    }

    /**
     * Adds the context data given.
     * @param metaEntries the context data to be applied, not {@code null}.
     * @return the builder for chaining.
     */
    public PropertyValueBuilder setMetaEntries(Map<String, String> metaEntries) {
        this.metaEntries.putAll(metaEntries);
        return this;
    }

    /**
     * Removes a getMeta entry.
     * @param key the entry's key, not {@code null}.
     * @return the builder for chaining.
     */
    public PropertyValueBuilder removeMetaEntry(String key) {
        Objects.requireNonNull(key, "Key must be given.");

        this.metaEntries.remove(key);
        return this;
    }

    /**
     * Get the createValue's context data.
     * @return the context data, not {@code null}.
     */
    public Map<String,String> getMetaEntries() {
        return Collections.unmodifiableMap(this.metaEntries);
    }

    /**
     * Get the createValue's context data.
     * @param key the key, not null.
     * @return the context data, not {@code null}.
     */
    public String getMeta(String key) {
        return this.metaEntries.get(key);
    }

    /**
     * Get the createValue's context data.
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
     * Sets a new createValue.
     * @param value the new createValue, not {@code null}.
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
     * @deprecated Use {@link #addMetaEntry(String, String)}
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
                ", createValue='" + value + '\'' +
                ", source='" + source + '\'' +
                ", metaEntries=" + metaEntries +
                '}';
    }

}
