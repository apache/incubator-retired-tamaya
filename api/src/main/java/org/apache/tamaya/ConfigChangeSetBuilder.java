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
package org.apache.tamaya;

import java.beans.PropertyChangeEvent;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;

/**
 * Models a set current changes to be applied to a configuration/property provider.  Such a set can be applied
 * to any {@link PropertySource} instance. If the provider is mutable it may check the
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
     * The base version, if any. Used for optimistic version checking.
     */
    String baseVersion;
    /**
     * Codesc provider, or null.
     */
    Function<String, Codec> codecs;

    /**
     * Constructor.
     *
     * @param source      the underlying configuration/provider, not null.
     * @param baseVersion the base version, used for optimistic version checking.
     * @param codecs      function to provide customized codecs, when according values are changed, if not set the
     *                    default codecs provided by {@link Codec#getInstance(Class)} are used.
     */
    private ConfigChangeSetBuilder(PropertySource source, String baseVersion, Function<String, Codec> codecs) {
        this.source = Objects.requireNonNull(source);
        this.baseVersion = baseVersion;
        this.codecs = codecs;
    }

    /**
     * Creates a new instance current this builder.
     *
     * @param source the underlying property provider/configuration, not null.
     * @return the builder for chaining.
     */
    public static ConfigChangeSetBuilder of(PropertySource source) {
        return new ConfigChangeSetBuilder(source, Instant.now().toString(), null);
    }

    /**
     * Creates a new instance current this builder.
     *
     * @param source the underlying property provider/configuration, not null.
     * @param codecs function to provide customized codecs, when according values are changed, if not set the
     *               default codecs provided by {@link Codec#getInstance(Class)} are used.
     * @return the builder for chaining.
     */
    public static ConfigChangeSetBuilder of(PropertySource source, Function<String, Codec> codecs) {
        return new ConfigChangeSetBuilder(source, Instant.now().toString(), codecs);
    }

    /**
     * Creates a new instance current this builder.
     *
     * @param source      the underlying property provider/configuration, not null.
     * @param baseVersion the base version to be used.
     * @return the builder for chaining.
     */
    public static ConfigChangeSetBuilder of(PropertySource source, String baseVersion) {
        return new ConfigChangeSetBuilder(source, baseVersion, null);
    }

    /**
     * Creates a new instance current this builder.
     *
     * @param source      the underlying property provider/configuration, not null.
     * @param baseVersion the base version to be used.
     * @param codecs      function to provide customized codecs, when according values are changed, if not set the
     *                    default codecs provided by {@link Codec#getInstance(Class)} are used.
     * @return the builder for chaining.
     */
    public static ConfigChangeSetBuilder of(PropertySource source, String baseVersion, Function<String, Codec> codecs) {
        return new ConfigChangeSetBuilder(source, baseVersion, codecs);
    }

    /**
     * Creates a new instance current this builder.
     *
     * @param configuration the base configuration, not null.
     * @return the builder for chaining.
     */
    public static ConfigChangeSetBuilder of(Configuration configuration) {
        return new ConfigChangeSetBuilder(configuration, configuration.getVersion(), null);
    }

    /**
     * Creates a new instance current this builder.
     *
     * @param configuration the base configuration, not null.
     * @param codecs        function to provide customized codecs, when according values are changed, if not set the
     *                      default codecs provided by {@link Codec#getInstance(Class)} are used.
     * @return the builder for chaining.
     */
    public static ConfigChangeSetBuilder of(Configuration configuration, Function<String, Codec> codecs) {
        return new ConfigChangeSetBuilder(configuration, configuration.getVersion(), codecs);
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
        Codec<Boolean> codec = this.codecs != null ? this.codecs.apply(key) : null;
        if (codec == null) codec = Codec.getInstance(Boolean.class);
        this.delta.put(key, new PropertyChangeEvent(this.source, key, this.source.get(key).orElse(null), codec.serialize(value)));
        return this;
    }

    /**
     * Applies the given keys.
     *
     * @param key   the key current the entry, not null.
     * @param value the keys to be applied, not null.
     * @return the builder for chaining.
     */
    public ConfigChangeSetBuilder put(String key, byte value) {
        Codec<Byte> codec = this.codecs != null ? this.codecs.apply(key) : null;
        if (codec == null) codec = Codec.getInstance(Byte.class);
        this.delta.put(key, new PropertyChangeEvent(this.source, key, this.source.get(key).orElse(null), codec.serialize(value)));
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
        Codec<Character> codec = this.codecs != null ? this.codecs.apply(key) : null;
        if (codec == null) codec = Codec.getInstance(Character.class);
        this.delta.put(key, new PropertyChangeEvent(this.source, key, this.source.get(key).orElse(null), codec.serialize(value)));
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
        Codec<Short> codec = this.codecs != null ? this.codecs.apply(key) : null;
        if (codec == null) codec = Codec.getInstance(Short.class);
        this.delta.put(key, new PropertyChangeEvent(this.source, key, this.source.get(key).orElse(null), codec.serialize(value)));
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
        Codec<Integer> codec = this.codecs != null ? this.codecs.apply(key) : null;
        if (codec == null) codec = Codec.getInstance(Integer.class);
        this.delta.put(key, new PropertyChangeEvent(this.source, key, this.source.get(key).orElse(null), codec.serialize(value)));
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
        Codec<Long> codec = this.codecs != null ? this.codecs.apply(key) : null;
        if (codec == null) codec = Codec.getInstance(Long.class);
        this.delta.put(key, new PropertyChangeEvent(this.source, key, this.source.get(key).orElse(null), codec.serialize(value)));
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
        Codec<Float> codec = this.codecs != null ? this.codecs.apply(key) : null;
        if (codec == null) codec = Codec.getInstance(Float.class);
        this.delta.put(key, new PropertyChangeEvent(this.source, key, this.source.get(key).orElse(null), codec.serialize(value)));
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
        Codec<Double> codec = this.codecs != null ? this.codecs.apply(key) : null;
        if (codec == null) codec = Codec.getInstance(Double.class);
        this.delta.put(key, new PropertyChangeEvent(this.source, key, this.source.get(key).orElse(null), codec.serialize(value)));
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
        Codec<String> codec = this.codecs != null ? this.codecs.apply(key) : null;
        this.delta.put(key, new PropertyChangeEvent(this.source, key, this.source.get(key).orElse(null), codec != null?codec.serialize(value):value));
        return this;
    }

    /**
     * Applies the given keys.
     *
     * @param key   the key current the entry, not null.
     * @param value the keys to be applied, not null.
     * @return the builder for chaining.
     * @throws ConfigException if no matching Codec could be found.
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
     * @param codec the codec to be used, if set overrides any other codecs that may apply. If null an appropriate
     *              codec is tried to be evaluated as needed.
     * @return the builder for chaining.
     * @throws ConfigException if no matching Codec could be found.
     */
    public <T> ConfigChangeSetBuilder put(String key, Class<T> type, T value, Codec codec) {
        Codec<T> targetCodec = codec;
        if(targetCodec==null){
            targetCodec = this.codecs != null ? this.codecs.apply(key) : null;
        }
        if (targetCodec == null){
            targetCodec = Codec.getInstance(type);
        }
        this.delta.put(key, new PropertyChangeEvent(this.source, key, this.source.get(key).orElse(null), targetCodec.serialize(Objects.requireNonNull(value))));
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
    public ConfigChangeSetBuilder putAll(Map<String, ?> changes) {
        changes.forEach((k, v) ->
                put(k, (Class) v.getClass(), v));
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
     * @param codecs      function to provide customized overriding codecs, when according values are changed,
     *                    if not set the default codecs are used.
     * @return the builder for chaining.
     */
    public ConfigChangeSetBuilder putAll(Map<String, ?> changes, Function<String, Codec<?>> codecs) {
        changes.forEach((k, v) ->
                put(k, (Class) v.getClass(), v, codecs.apply(k)));
        return this;
    }

    /**
     * This method will create a change set that clears all entries fromMap the given base configuration/properties.
     *
     * @return the builder for chaining.
     */
    public ConfigChangeSetBuilder deleteAll() {
        this.delta.clear();
        this.source.toMap().forEach((k, v) ->
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
        return new ConfigChangeSet(this.source, baseVersion, Collections.unmodifiableCollection(this.delta.values()));
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
        for (Map.Entry<String, String> en : map1.toMap().entrySet()) {
            Optional<String> val = map2.get(en.getKey());
            if (!val.isPresent()) {
                changes.add(new PropertyChangeEvent(map1, en.getKey(), null, en.getValue()));
            } else if (!val.get().equals(en.getValue())) {
                changes.add(new PropertyChangeEvent(map1, en.getKey(), val.get(), en.getValue()));
            }
        }
        for (Map.Entry<String, String> en : map2.toMap().entrySet()) {
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
