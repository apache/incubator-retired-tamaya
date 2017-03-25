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

import java.io.Serializable;
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
public final class PropertyValue implements Serializable{
    private static final long serialVersionUID = 1L;
    /** The requested key. */
    private String key;
    /** The value. */
    private String value;
    /** The source of the value. */
    private String source;
    /** Additional metadata provided by the provider. */
    private Map<String,String> metaEntries = new HashMap<>();

    PropertyValue(PropertyValueBuilder builder){
        this.key = Objects.requireNonNull(builder.key);
        this.value = builder.value;
        this.source = Objects.requireNonNull(builder.source);
        if(builder.metaEntries !=null) {
            this.metaEntries.putAll(builder.metaEntries);
        }
    }

    /**
     * Creates a new instance
     * @param key the key, not {@code null}.
     * @param value the value, not {@code null}.
     * @param source the source, typically the name of the {@link PropertySource} providing
     *               the value, not {@code null}.
     */
    private PropertyValue(String key, String value, String source){
        this.key = Objects.requireNonNull(key, "Key is required.");
        this.value = Objects.requireNonNull(value, "Value is required.");
        this.source = Objects.requireNonNull(source, "Source is required.");
    }

    /**
     * The requested key.
     * @return the, key never {@code null}.
     */
    public String getKey() {
        return key;
    }

    /**
     * The source.
     * @return the source, which provided the value, not {@code null}.
     * @see PropertySource#getName().
     */
    public String getSource() {
        return this.source;
    }


    /**
     * The value.
     * @return the value, in case a value is null it is valid to return {#code null} as result for
     * {@link PropertySource#get(String)}.
     */
    public String getValue() {
        return this.value;
    }

    /**
     * Creates a full configuration map for this key, value pair and all its meta context data. This map
     * is also used for subsequent processing, like value filtering.
     * @return the property value entry map.
     */
    public Map<String, String> getMetaEntries() {
        return Collections.unmodifiableMap(metaEntries);
    }

    /**
     * Creates a new builder instance.
     * @param key the key, not {@code null}.
     * @param source the source, typically the name of the {@link PropertySource}
     *               providing the value, not {@code null}.
     * @return a new builder instance.
     */
    public static PropertyValueBuilder builder(String key, String source){
        Objects.requireNonNull(key, "Key must be given.");
        Objects.requireNonNull(source, "Source must be given");

        return new PropertyValueBuilder(key, source);
    }

    /**
     * Creates a new builder instance.
     * @param key the key, not {@code null}.
     * @param source the source, typically the name of the {@link PropertySource}
     *               providing the value, not {@code null}.
     * @return a new builder instance.
     */
    public static PropertyValueBuilder builder(String key, String value, String source) {
        Objects.requireNonNull(key, "Key must be given.");
        Objects.requireNonNull(value, "Value must be given");
        Objects.requireNonNull(source, "Source must be given.");

        return new PropertyValueBuilder(key, value, source);
    }


    /**
     * Creates a new PropertyValue without any metadata..
     * @param key the key, not {@code null}.
     * @param value the value.
     * @param source the source, typically the name of the {@link PropertySource}
     *               providing the value, not  {@code null}.
     * @return a new property value instance, or {@code null},
     *         if the value passed is {@code null}..
     */
    public static PropertyValue of(String key, String value, String source) {
        if (value==null) {
            return null;
        }
        return new PropertyValue(key, value, source);
    }

    /**
     * Access the given key from this value. Valid keys are the key or any meta-context key.
     * @param key the key, not {@code null}.
     * @return the value found, or {@code null}.
     */
    public String getMetaEntry(String key) {
        return this.metaEntries.get(Objects.requireNonNull(key));
    }

    /**
     * Creates a new builder instance based on this item.
     * @return a new builder, never null.
     */
    public PropertyValueBuilder toBuilder() {
        return new PropertyValueBuilder(this.getKey(), this.getSource())
                .setValue(this.getValue())
        .setMetaEntries(this.metaEntries);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PropertyValue)) return false;
        PropertyValue that = (PropertyValue) o;
        return Objects.equals(getKey(), that.getKey()) &&
                Objects.equals(getValue(), that.getValue()) &&
                Objects.equals(getSource(), that.getSource()) &&
                Objects.equals(getMetaEntries(), that.getMetaEntries());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKey(), getValue(), getSource(),
                getMetaEntries());
    }

    @Override
    public String toString() {
        return "PropertyValue{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                ", source='" + source + '\'' +
                (metaEntries.isEmpty()?"":", metaEntries=" + metaEntries) +
                '}';
    }

    /**
     * Maps a map of {@code Map<String,String>} to a {@code Map<String,PropertyValue>}.
     * @param config the String based map, not {@code null}.
     * @param source the source name, not {@code null}.
     * @return the corresponding value based map.
     */
    public static Map<String,PropertyValue> map(Map<String, String> config, String source) {
        Map<String,PropertyValue> result = new HashMap<>(config.size());
        for(Map.Entry<String,String> en:config.entrySet()){
            result.put(en.getKey(), PropertyValue.of(en.getKey(), en.getValue(), source));
        }
        return result;
    }

    /**
     * Maps a map of {@code Map<String,String>} to a {@code Map<String,PropertyValue>}.
     *
     * @param config the String based map, not {@code null}.
     * @param source the source name, not {@code null}.
     * @param metaData additional metadata, not {@code null}.
     * @return the corresponding value based map.
     */
    public static Map<String,PropertyValue> map(Map<String, String> config, String source,
                                                Map<String,String> metaData) {
        Objects.requireNonNull(config, "Config must be given.");
        Objects.requireNonNull(source, "Source must be given.");
        Objects.requireNonNull(metaData, "Meta data must be given.");

        Map<String,PropertyValue> result = new HashMap<>(config.size());

        for(Map.Entry<String,String> en:config.entrySet()){
            PropertyValue value = new PropertyValueBuilder(en.getKey(), source).setValue(en.getValue())
                                                                               .addMetaEntries(metaData).build();
            result.put(en.getKey(), value);
        }
        return result;
    }
}
