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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Class modelling the result of a request for a property value. A property value is basically identified by its key.
 * There might be reasons, where one want to further analyze, which PropertySources provided a value and which not, so
 * it is possible to create a PropertyValue with a null value. Nevertheless in all cases the provider source (typically
 * the name of the PropertySource) must be set.
 */
public final class ConfigValue implements Serializable{
    private static final long serialVersionUID = 1L;
    /** The requested key. */
    private String key;
    /** The value. */
    private String value;
    /** Additional metadata provided by the provider. */
    private String metaEntry;

    ConfigValue(ConfigValueBuilder builder){
        this.key = Objects.requireNonNull(builder.key);
        this.value = Objects.requireNonNull(builder.value);
        this.metaEntry = builder.metaEntry;
    }

    /**
     * Creates a new instance
     * @param key the key, not {@code null}.
     * @param value the value, not {@code null}.
     * @param metaEntry the source, typically the name of the {@link javax.config.spi.ConfigSource} providing
     *               the value, not {@code null}.
     */
    private ConfigValue(String key, String value, String metaEntry){
        this.key = Objects.requireNonNull(key, "Key is required.");
        this.value = Objects.requireNonNull(value, "Value is required.");
        this.metaEntry = metaEntry;
    }

    /**
     * The requested key.
     * @return the, key never {@code null}.
     */
    public String getKey() {
        return key;
    }

    /**
     * The value.
     * @return the value, in case a value is null it is valid to return {#code null} as result for
     * {@link javax.config.spi.ConfigSource#getValue(String)}}.
     */
    public String getValue() {
        return this.value;
    }

    /**
     * Creates a full configuration map for this key, value pair and all its meta context data. This map
     * is also used for subsequent processing, like value filtering.
     * @return the property value entry map.
     */
    public String getMetaEntry() {
        return metaEntry;
    }

    /**
     * Creates a new builder instance.
     * @param key the key, not {@code null}.
     * @return a new builder instance.
     */
    public static ConfigValueBuilder builder(String key){
        Objects.requireNonNull(key, "Key must be given.");
        return new ConfigValueBuilder(key);
    }

    /**
     * Creates a new builder instance.
     * @param key the key, not {@code null}.
     * @param value the property value, not {@code null}.
     * @return a new builder instance.
     */
    public static ConfigValueBuilder builder(String key, String value) {
        Objects.requireNonNull(key, "Key must be given.");
        Objects.requireNonNull(value, "Value must be given");
        return new ConfigValueBuilder(key, value);
    }


    /**
     * Creates a new PropertyValue without any metadata..
     * @param key the key, not {@code null}.
     * @param value the value.
     * @param metaEntry the metaEntry, typically the name of the {@link javax.config.spi.ConfigSource}
     *               providing the value, not  {@code null}.
     * @return a new property value instance, or {@code null},
     *         if the value passed is {@code null}..
     */
    public static ConfigValue of(String key, String value, String metaEntry) {
        if (value==null) {
            return null;
        }
        return new ConfigValue(key, value, metaEntry);
    }

    /**
     * Creates a new builder instance based on this item.
     * @return a new builder, never null.
     */
    public ConfigValueBuilder toBuilder() {
        return new ConfigValueBuilder(this.getKey())
                .setValue(this.getValue())
        .setMetaEntry(this.metaEntry);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConfigValue)) return false;
        ConfigValue that = (ConfigValue) o;
        return Objects.equals(getKey(), that.getKey()) &&
                Objects.equals(getValue(), that.getValue()) &&
                Objects.equals(getMetaEntry(), that.getMetaEntry());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKey(), getValue(),
                getMetaEntry());
    }

    @Override
    public String toString() {
        return "PropertyValue{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                ", metaEntry='" + metaEntry + '\'' +
                '}';
    }

    /**
     * Maps a map of {@code Map<String,String>} to a {@code Map<String,PropertyValue>}.
     * @param config the String based map, not {@code null}.
     * @return the corresponding value based map.
     */
    public static Map<String,ConfigValue> map(Map<String, String> config) {
        Map<String,ConfigValue> result = new HashMap<>(config.size());
        for(Map.Entry<String,String> en:config.entrySet()){
            result.put(en.getKey(), ConfigValue.of(en.getKey(), en.getValue(), config.get(en.getKey()+"[meta]")));
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
    public static Map<String,ConfigValue> map(Map<String, String> config, String source,
                                              Map<String,String> metaData) {
        Objects.requireNonNull(config, "Config must be given.");
        Objects.requireNonNull(source, "Source must be given.");
        Objects.requireNonNull(metaData, "Meta data must be given.");

        Map<String,ConfigValue> result = new HashMap<>(config.size());

        for(Map.Entry<String,String> en:config.entrySet()){
            ConfigValue value = new ConfigValueBuilder(en.getKey(), source).setValue(en.getValue())
                                                                               .addMetaEntries(metaData).build();
            result.put(en.getKey(), value);
        }
        return result;
    }

    public Map<? extends String, ? extends String> asMap() {
        Map<String,String> map = new HashMap<>();
        map.put(key, value);
        map.put(key+"[meta]", this.metaEntry);
        return map;
    }

    public static ConfigValue of(String key, Map<String, String> rawProperties) {
        String value = rawProperties.get(key);
        String meta = rawProperties.get(key+"[meta]");
        return new ConfigValue(key, value, meta);
    }
}
