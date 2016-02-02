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
    Map<String,String> contextData;

    /**
     * Create a new builder instance, for a given
     * @param value the value, not null. If a value is null {@link PropertySource#get(String)} should return
     * {@code null}.
     */
    public PropertyValueBuilder(String key, String value) {
        this.key = Objects.requireNonNull(key);
        this.value = Objects.requireNonNull(value);
    }

    /**
     * Replaces/sets the context data.
     * @param contextData the context data to be applied, not null. Note that all keys should only identify the context
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
    public PropertyValueBuilder setContextData(Map<String, String> contextData) {
        if(this.contextData==null){
            this.contextData = new HashMap<>();
        } else{
            this.contextData.clear();
        }
        for(Map.Entry<String,String> en:contextData.entrySet()) {
            this.contextData.put("_"+this.key+'.'+en.getKey(), en.getValue());
        }
        return this;
    }

    /**
     * Add an additional context data information.
     * @param key the context data key, not null.
     * @param value the context value, not null (will be converted to String).
     * @return the builder for chaining.
     */
    public PropertyValueBuilder addContextData(String key, Object value) {
        if(this.contextData==null){
            this.contextData = new HashMap<>();
        }
        this.contextData.put("_"+this.key+'.'+key, String.valueOf(Objects.requireNonNull(value)));
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
                "value='" + value + '\'' +
                ", contextData=" + contextData +
                '}';
    }
}
