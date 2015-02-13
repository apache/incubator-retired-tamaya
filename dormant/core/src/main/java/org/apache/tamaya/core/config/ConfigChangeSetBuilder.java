/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tamaya.core.config;

import org.apache.tamaya.Configuration;
import org.apache.tamaya.spi.PropertySource;

import java.beans.PropertyChangeEvent;
import java.util.*;
import java.util.function.Function;

/**
 * Models a set current changes to be applied to a configuration/property provider.  Such a set can be applied
 * to any {@link org.apache.tamaya.spi.PropertySource} instance. If the provider is mutable it may check the
 * version given and applyChanges the changes to the provider/configuration, including triggering current regarding
 * change events.
 * <p>
 * For appropriate conversion a {@code Function<String, Codec>} can be applied, which performs correct conversion,
 * when changed values are set. This function enables connecting e.g. setters on a configuration template with
 * the corresponding conversion logic, so the template calls are correctly converted back.
 */
public final class ConfigChangeSetBuilder {
    /**
     * The recorded changes.
     */
    final SortedMap<String, PropertyChangeEvent> delta = new TreeMap<>();
    /**
     * The underlying configuration/provider.
     */
    PropertySource source;

    /**
     * Constructor.
     *
     * @param source      the underlying configuration/provider, not null.
     */
    private ConfigChangeSetBuilder(PropertySource source) {
        this.source = Objects.requireNonNull(source);
    }

    /**
     * Creates a new instance current this builder.
     *
     * @param source the underlying property provider/configuration, not null.
     * @return the builder for chaining.
     */
    public static ConfigChangeSetBuilder of(PropertySource source) {
        return new ConfigChangeSetBuilder(source);
    }


    /**
     * Creates a new instance current this builder.
     *
     * @param configuration the base configuration, not null.
     * @return the builder for chaining.
     */
    public static ConfigChangeSetBuilder of(Configuration configuration) {
        return new ConfigChangeSetBuilder(configuration);
    }

    /**
     * This method records all changes to be applied to the base property provider/configuration to
     * achieve the given target state.
     *
     * @param newState the new target state, not null.
     * @return the builder for chaining.
     */
    public ConfigChangeSetBuilder addChanges(PropertySource newState) {
        compare(newState, this.source).forEach((c) -> this.delta.put(c.getPropertyName(), c));
        return this;
    }

    /**
     * Get the current values, also considering any changes recorded within this change set.
     *
     * @param key the key current the entry, not null.
     * @return the keys, or null.
     */
    public String get(String key) {
        PropertyChangeEvent change = this.delta.get(key);
        if (change != null && !(change.getNewValue() == null)) {
            return (String) change.getNewValue();
        }
        return null;
    }

    /**
     * Marks the given key(s) fromMap the configuration/properties to be removed.
     *
     * @param key       the key current the entry, not null.
     * @param otherKeys additional keys to be removed (convenience), not null.
     * @return the builder for chaining.
     */
    public ConfigChangeSetBuilder remove(String key, String... otherKeys) {
        String oldValue = this.source.get(key).orElse(null);
        if (oldValue == null) {
            this.delta.remove(key);
        }
        this.delta.put(key, new PropertyChangeEvent(this.source, key, oldValue, null));
        for (String addKey : otherKeys) {
            oldValue = this.source.get(addKey).orElse(null);
            if (oldValue == null) {
                this.delta.remove(addKey);
            }
            this.delta.put(addKey, new PropertyChangeEvent(this.source, addKey, oldValue, null));
        }
        return this;
    }

    /**
     * Applies the given keys.
     *
     * @param key   the key current the entry, not null.
     * @param value the keys to be applied, not null.
     * @return the builder for chaining.
     */
    public ConfigChangeSetBuilder put(String key, boolean value) {
        this.delta.put(key, new PropertyChangeEvent(this.source, key, this.source.get(key).orElse(null), String.valueOf(value)));
        return this;
    }

    /**
     s* Applies the given keys.
     *
     * @param key   the key current the entry, not null.
     * @param value the keys to be applied, not null.
     * @return the builder for chaining.
     */
    public ConfigChangeSetBuilder put(String key, byte value) {
        this.delta.put(key, new PropertyChangeEvent(this.source, key, this.source.get(key).orElse(null), String.valueOf(value)));
        return this;
    }

    /**
     * Applies the given keys.
     *
     * @param key   the key current the entry, not null.
     * @param value the keys to be applied, not null.
     * @return the builder for chaining.
     */
    public ConfigChangeSetBuilder put(String key, char value) {
        this.delta.put(key, new PropertyChangeEvent(this.source, key, this.source.get(key).orElse(null), String.valueOf(value)));
        return this;
    }

    /**
     * Applies the given keys.
     *
     * @param key   the key current the entry, not null.
     * @param value the keys to be applied, not null.
     * @return the builder for chaining.
     */
    public ConfigChangeSetBuilder put(String key, short value) {
        this.delta.put(key, new PropertyChangeEvent(this.source, key, this.source.get(key).orElse(null), String.valueOf(value)));
        return this;
    }

    /**
     * Applies the given keys.
     *
     * @param key   the key current the entry, not null.
     * @param value the keys to be applied, not null.
     * @return the builder for chaining.
     */
    public ConfigChangeSetBuilder put(String key, int value) {
        this.delta.put(key, new PropertyChangeEvent(this.source, key, this.source.get(key).orElse(null), String.valueOf(value)));
        return this;
    }

    /**
     * Applies the given keys.
     *
     * @param key   the key current the entry, not null.
     * @param value the keys to be applied, not null.
     * @return the builder for chaining.
     */
    public ConfigChangeSetBuilder put(String key, long value) {
        this.delta.put(key, new PropertyChangeEvent(this.source, key, this.source.get(key).orElse(null), String.valueOf(value)));
        return this;
    }

    /**
     * Applies the given keys.
     *
     * @param key   the key current the entry, not null.
     * @param value the keys to be applied, not null.
     * @return the builder for chaining.
     */
    public ConfigChangeSetBuilder put(String key, float value) {
        this.delta.put(key, new PropertyChangeEvent(this.source, key, this.source.get(key).orElse(null), String.valueOf(value)));
        return this;
    }

    /**
     * Applies the given keys.
     *
     * @param key   the key current the entry, not null.
     * @param value the keys to be applied, not null.
     * @return the builder for chaining.
     */
    public ConfigChangeSetBuilder put(String key, double value) {
        this.delta.put(key, new PropertyChangeEvent(this.source, key, this.source.get(key).orElse(null), String.valueOf(value)));
        return this;
    }


    /**
     * Applies the given keys.
     *
     * @param key   the key current the entry, not null.
     * @param value the keys to be applied, not null.
     * @return the builder for chaining.
     */
    public ConfigChangeSetBuilder put(String key, String value) {
        this.delta.put(key, new PropertyChangeEvent(this.source, key, this.source.get(key).orElse(null), String.valueOf(value)));
        return this;
    }

    /**
     * Applies the given keys.
     *
     * @param key   the key current the entry, not null.
     * @param value the keys to be applied, not null.
     * @return the builder for chaining.
     * @throws org.apache.tamaya.ConfigException if no matching Codec could be found.
     */
    public <T> ConfigChangeSetBuilder put(String key, Class<T> type, T value) {
        put(key, type, value, null);
        return this;
    }

    /**
     * Applies the given keys.
     *
     * @param key   the key current the entry, not null.
     * @param value the keys to be applied, not null.
     * @param adapter the codec to be used, if set overrides any other codecs that may apply. If null an appropriate
     *              codec is tried to be evaluated as needed.
     * @return the builder for chaining.
     * @throws org.apache.tamaya.ConfigException if no matching Codec could be found.
     */
    public <T> ConfigChangeSetBuilder put(String key, Class<T> type, T value, Function<T,String> adapter) {
        this.delta.put(key, new PropertyChangeEvent(this.source, key, this.source.get(key).orElse(null), adapter.apply(Objects.requireNonNull(value))));
        return this;
    }


    /**
     * Apply all the given values to the base configuration/properties.
     * Note that all values passed must be convertible to String, either
     * <ul>
     * <li>the registered codecs provider provides codecs for the corresponding keys, or </li>
     * <li>default codecs are present for the given type, or</li>
     * <li>the value is an instanceof String</li>
     * </ul>
     *
     * @param changes the changes to be applied, not null.
     * @return the builder for chaining.
     */
    public ConfigChangeSetBuilder putAll(Map<String, String> changes) {
        changes.putAll(changes);
        return this;
    }

    /**
     * This method will create a change set that clears all entries fromMap the given base configuration/properties.
     *
     * @return the builder for chaining.
     */
    public ConfigChangeSetBuilder deleteAll() {
        this.delta.clear();
        this.source.getProperties().forEach((k, v) ->
                this.delta.put(k, new PropertyChangeEvent(this.source, k, v, null)));
        return this;
    }

    /**
     * Checks if the change set is empty, i.e. does not contain any changes.
     *
     * @return true, if the set is empty.
     */
    public boolean isEmpty() {
        return this.delta.isEmpty();
    }

    /**
     * Resets this change set instance. This will clear all changes done to this builder, so the
     * set will be empty.
     */
    public void reset() {
        this.delta.clear();
    }

    /**
     * Builds the corresponding change set.
     *
     * @return the new change set, never null.
     */
    public ConfigChangeSet build() {
        return new ConfigChangeSet(this.source, Collections.unmodifiableCollection(this.delta.values()));
    }

    /**
     * Compares the two property config/configurations and creates a collection current all changes
     * that must be appied to render {@code map1} into {@code map2}.
     *
     * @param map1 the source map, not null.
     * @param map2 the target map, not null.
     * @return a collection current change events, never null.
     */
    public static Collection<PropertyChangeEvent> compare(PropertySource map1, PropertySource map2) {
        List<PropertyChangeEvent> changes = new ArrayList<>();
        for (Map.Entry<String, String> en : map1.getProperties().entrySet()) {
            Optional<String> val = map2.get(en.getKey());
            if (!val.isPresent()) {
                changes.add(new PropertyChangeEvent(map1, en.getKey(), null, en.getValue()));
            } else if (!val.get().equals(en.getValue())) {
                changes.add(new PropertyChangeEvent(map1, en.getKey(), val.get(), en.getValue()));
            }
        }
        for (Map.Entry<String, String> en : map2.getProperties().entrySet()) {
            Optional<String> val = map1.get(en.getKey());
            if (!val.isPresent()) {
                changes.add(new PropertyChangeEvent(map1, en.getKey(), null, en.getValue()));
            } else if (!val.equals(Optional.ofNullable(en.getValue()))) {
                changes.add(new PropertyChangeEvent(map1, en.getKey(), val.get(), en.getValue()));
            }
        }
        return changes;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "PropertyChangeEventBuilder [source=" + source + ", " +
                ", delta=" + delta + "]";
    }

}
