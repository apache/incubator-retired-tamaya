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
 * Class modelling the result of a request for a property value. A property value is basically identified by its key.
 * There might be reasons, where one want to further analyze, which PropertySources provided a value and which not, so
 * it is possible to create a PropertyValue with a null value. Nevertheless in all cases the provider source (typically
 * the name of the PropertySource) must be set.
 */
public final class PropertyValue {
    /** The requested key. */
    private String key;
    /** Additional metadata provided by thhe provider. */
    private Map<String,String> configEntries = new HashMap<>();

    PropertyValue(PropertyValueBuilder builder){
        this.key = builder.key;
        if(builder.contextData!=null) {
            this.configEntries.putAll(builder.contextData);
        }
        this.configEntries.put(key, Objects.requireNonNull(builder.value));
    }

    /**
     * Creates a new instance
     * @param key the key, not null.
     * @param value the value, not null.
     * @param source the source, typically the name of the {@link PropertySource} providing the value, not null.
     */
    private PropertyValue(String key, String value, String source){
        this.key = Objects.requireNonNull(key, "key is required.");
        this.configEntries.put(key, value);
        this.configEntries.put("_"+key+".source", Objects.requireNonNull(source, "source is required."));
    }

    /**
     * The requested key.
     * @return the, key never null.
     */
    public String getKey() {
        return key;
    }

    /**
     * THe value.
     * @return the value, in case a value is null it is valid to return {#code null} as result for
     * {@link PropertySource#get(String)}.
     */
    public String getValue() {
        return configEntries.get(key);
    }

    /**
     * Creates a full configuration map for this key, value pair and all its meta context data. This map
     * is also used for subsequent processing, like value filtering.
     * @return the property value entry map.
     */
    public Map<String, String> getConfigEntries() {
        return Collections.unmodifiableMap(configEntries);
    }

    /**
     * Creates a new builder instance.
     * @param key the key, not null.
     * @param value the value.
     * @param source the source, typically the name of the {@link PropertySource} providing the value, not null.
     * @return a new builder instance.
     */
    public static PropertyValueBuilder builder(String key, String value, String source){
        return new PropertyValueBuilder(key, value, source);
    }

    /**
     * Creates a new PropertyValue without any metadata..
     * @param key the key, not null.
     * @param value the value.
     * @param source the source, typically the name of the {@link PropertySource} providing the value, not null.
     * @return a new builder instance.
     */
    public static PropertyValue of(String key, String value, String source){
        return new PropertyValue(key, value, source);
    }

    /**
     * Access the given key from this value. Valid keys are the key or any meta-context key.
     * @param key the key, not null.
     * @return the value found, or null.
     */
    public String get(String key) {
        return this.configEntries.get(key);
    }
}
