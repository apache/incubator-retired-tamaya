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
package org.apache.tamaya.spisupport;

import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationSnapshot;
import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.spi.ConfigurationContext;
import org.apache.tamaya.spi.PropertyConverter;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * /**
 * Configuration implementation that stores all current values of a given (possibly dynamic, contextual and non server
 * capable instance) and is fully serializable. Note that hereby only the scannable key/createValue pairs are considered.
 */
public class DefaultConfigurationSnapshot implements ConfigurationSnapshot, Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * The properties frozen.
     */
    private Configuration snapshot;
    private long frozenAt = System.nanoTime();
    private UUID id = UUID.randomUUID();
    private transient ConfigurationContext context;
    private Set<String> keys = new HashSet<>();

    /**
     * Constructor.
     *
     * @param config The base configuration.
     * @param keys The keys to evaluate, not null.
     */
    public DefaultConfigurationSnapshot(Configuration config, Iterable<String> keys) {
        for(String k:keys) {
            this.keys.add(k);
        }
        ConfigurationContext ctx = config.getContext();
        MetadataProvider metadataProvider = ctx.getServiceContext().getService(MetadataProvider.class,
                DefaultMetaDataProvider::new);
        context = new DefaultConfigurationContext(ctx.getServiceContext(),
                ctx.getPropertyFilters(),
                ctx.getPropertySources().stream()
                        .map(ps -> DefaultPropertySourceSnapshot.of(ps, this.keys)).collect(Collectors.toList()),
                ctx.getPropertyConverters(),
                metadataProvider);
        this.snapshot = new DefaultConfiguration(context);
        if(this.keys.isEmpty()){
            this.keys.addAll(this.snapshot.getProperties().keySet());
        }
        this.keys = Collections.unmodifiableSet(this.keys);
    }

    /**
     * Constructor.
     *
     * @param config The base configuration.
     */
    public DefaultConfigurationSnapshot(Configuration config) {
        ConfigurationContext ctx = config.getContext();
        MetadataProvider metadataProvider = ctx.getServiceContext().getService(MetadataProvider.class,
                DefaultMetaDataProvider::new);
        context = new DefaultConfigurationContext(ctx.getServiceContext(),
                ctx.getPropertyFilters(),
                ctx.getPropertySources().stream()
                        .map(ps -> new DefaultPropertySourceSnapshot(ps)).collect(Collectors.toList()),
                ctx.getPropertyConverters(),
                metadataProvider);
        this.snapshot = new DefaultConfiguration(context);
        this.keys = Collections.unmodifiableSet(this.snapshot.getProperties().keySet());
    }


    @Override
    public ConfigurationSnapshot getSnapshot(Iterable<String> keys) {
        return new DefaultConfigurationSnapshot(this, keys);
    }

    /**
     * Get the evaluated keys of this frozen coinfiguration.
     * @return the keys, not null.
     */
    public Set<String> getKeys() {
        return keys;
    }

    @Override
    public String get(String key) {
        return this.snapshot.get(key);
    }

    @Override
    public String getOrDefault(String key, String defaultValue) {
        return this.snapshot.getOrDefault(key, defaultValue);
    }

    @Override
    public <T> T getOrDefault(String key, Class<T> type, T defaultValue) {
        return this.snapshot.getOrDefault(key, type, defaultValue);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(String key, Class<T> type) {
        return snapshot.get(key, type);
    }

    /**
     * Accesses the current String createValue for the given key and tries to convert it
     * using the {@link PropertyConverter} instances provided by the current
     * {@link ConfigurationContext}.
     *
     * @param key  the property's absolute, or relative path, e.g. @code
     *             a/b/c/d.myProperty}.
     * @param type The target type required, not null.
     * @param <T>  the createValue type
     * @return the converted createValue, never null.
     */
    @Override
    public <T> T get(String key, TypeLiteral<T> type) {
        return snapshot.get(key, type);
    }

    @Override
    public <T> T getOrDefault(String key, TypeLiteral<T> type, T defaultValue) {
        return snapshot.getOrDefault(key, type, defaultValue);
    }

    @Override
    public Map<String, String> getProperties() {
        return snapshot.getProperties();
    }

    @Override
    public ConfigurationContext getContext() {
        return snapshot.getContext();
    }

    /**
     * <p>Returns the moment in time when this frozen configuration has been created.</p>
     *
     * <p>The time is taken from {@linkplain System#currentTimeMillis()}</p>
     *
     * @see System#currentTimeMillis()
     * @return the moment in time when this configuration has been created
     */
    @Override
    public long getTimestamp() {
        return frozenAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DefaultConfigurationSnapshot that = (DefaultConfigurationSnapshot) o;

        if (frozenAt != that.frozenAt) {
            return false;
        }
        return Objects.equals(snapshot, that.snapshot);
    }

    @Override
    public int hashCode() {
        return Objects.hash(frozenAt, snapshot);
    }

    @Override
    public String toString() {
        return "FrozenConfiguration{" +
                "id=" + getId() + "," +
                "frozenAt=" + frozenAt + "," +
                "config=" + snapshot +
                '}';
    }

    /**
     * <p>Returns the unique id of this frozen configuration.</p>
     *
     * @return the unique id of this frozen configuration, never {@code null}
     */
    public UUID getId() {
        return id;
    }
}
