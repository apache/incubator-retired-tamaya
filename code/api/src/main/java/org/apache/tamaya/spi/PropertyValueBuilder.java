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
    /** additional metadata entries (optional). */
    Map<String,String> metaEntries = new HashMap<>();

    /**
     * Create a new builder instance, for a given set of parameters.
     * @param key to access a property value.
     * @param value the value, not null. If a value is null {@link PropertySource#get(String)} should return
     * {@code null}.
     * @param source property source.
     */
    public PropertyValueBuilder(String key, String value, String source) {
        this.key = Objects.requireNonNull(key);
        this.value = Objects.requireNonNull(value);
        this.metaEntries.put("source", Objects.requireNonNull(source));
    }

    /**
     * Creates a new builder from data from a {@link PropertyValue}.
     * @param key to access a property value.
     * @param value the value, not null. If a value is null {@link PropertySource#get(String)} should return
     * {@code null}.
     * @param metaEntries the context data, not null.
     */
    PropertyValueBuilder(String key, String value, Map<String,String> metaEntries) {
        this.key = Objects.requireNonNull(key);
        this.value = Objects.requireNonNull(value);
        this.metaEntries.putAll(metaEntries);
    }

    /**
     * Replaces/sets the context data.
     * @param metaEntries the context data to be applied, not null. Note that all keys should only identify the context
     *                    data item. the builder does create a corresponding metadata entry, e.g.
     *                    <pre>
     *                    provider=myProviderName
     *                    ttl=250
     *                    creationIndex=1
     *                    modificationIndex=23
     *                    </pre>
     *                    will be mapped, given a key {@code test.env.name} to
     *                    <pre>
     *                    _test.env.name.provider=myProviderName
     *                    _test.env.name.ttl=250
     *                    _test.env.name.creationIndex=1
     *                    _test.env.name.modificationIndex=23
     *                    </pre>
     * @return the builder for chaining.
     */
    public PropertyValueBuilder setMetaEntries(Map<String, String> metaEntries) {
        this.metaEntries.clear();
        this.metaEntries.putAll(metaEntries);
        return this;
    }

    /**
     * Add an additional context data information.
     * @param key the context data key, not null.
     * @param value the context value, not null (will be converted to String).
     * @return the builder for chaining.
     */
    public PropertyValueBuilder addContextData(String key, Object value) {
        this.metaEntries.put(key, String.valueOf(Objects.requireNonNull(value, "Meta value is null.")));
        return this;
    }

    /**
     * Get the value's context data.
     * @return the context data.
     */
    public Map<String,String> getMetaEntries(){
        return Collections.unmodifiableMap(this.metaEntries);
    }

    /**
     * Changes the entry's key, mapping also corresponding context entries.
     * @param key the new key, not null.
     * @return the builder for chaining.
     */
    public PropertyValueBuilder mapKey(String key) {
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
     * Sets a new value.
     * @param value the new value.
     * @return the builder for chaining.
     */
    public PropertyValueBuilder setValue(String value) {
        this.value = value;
        return this;
    }

    /**
     * Creates a new immutable {@link PropertyValue}.
     * @return a new immutable {@link PropertyValue}, never null.
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
