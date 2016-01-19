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
package org.apache.tamaya.events;

import org.apache.tamaya.ConfigurationProvider;
import org.apache.tamaya.spi.ConfigurationContext;
import org.apache.tamaya.spi.PropertySource;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Models a set of current changes applied to a {@link org.apache.tamaya.spi.PropertySource}. Consumers of these events
 * can observe changes to property sources and
 * <ol>
 *     <li>check if their current configuration instance ({@link org.apache.tamaya.spi.ConfigurationContext}
 *     contains the changed {@link org.apache.tamaya.spi.PropertySource} (Note: the reference to a property source is never affected by a
 *     change, it is the data of the property source only).</li>
 *     <li>if so, a corresponding action may be taken, such as reevaluating the configuration values (depending on
 *     the update policy) or reevaluating the complete {@link org.apache.tamaya.Configuration} to create a change
 *     event on configuration level.
 * </ol>
 */
public final class ConfigurationContextChangeBuilder {
    /**
     * The recorded changes.
     */
    final List<PropertySourceChange> changedPropertySources = new ArrayList<>();
    /**
     * The version configured, or null, for generating a default.
     */
    String version;
    /**
     * The optional timestamp in millis of this epoch.
     */
    Long timestamp;

    final ConfigurationContext configurationContext;

    /**
     * Constructor.
     */
    private ConfigurationContextChangeBuilder(ConfigurationContext configurationContext) {
        this.configurationContext = Objects.requireNonNull(configurationContext);
    }

    /**
     * Just creates a new ConfigurationContextBuilder using the current COnfigurationContext has root resource.
     * @return a new ConfigurationContextBuilder, never null.
     */
    public static ConfigurationContextChangeBuilder of() {
        return of(ConfigurationProvider.getConfigurationContext());
    }

    /**
     * Creates a new instance current this builder.
     *
     * @param context context to use for creating changesets.
     * @return the builder for chaining.
     */
    public static ConfigurationContextChangeBuilder of(ConfigurationContext context) {
        return new ConfigurationContextChangeBuilder(context);
    }

    /**
     * Apply a version/UUID to the set being built.
     * @param version the version to apply, or null, to let the system generate a version for you.
     * @return the builder for chaining.
     */
    public ConfigurationContextChangeBuilder setVersion(String version) {
        this.version = version;
        return this;
    }

    /**
     * Apply given timestamp to the set being built.
     * @param timestamp timestamp to set.
     * @return the builder for chaining.
     */
    public ConfigurationContextChangeBuilder setTimestamp(long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    /**
     * This method records all changes to be applied to the base property provider/configuration to
     * achieve the given target state.
     *
     * @param propertySource the new target state, not null.
     * @return the builder for chaining.
     */
    public ConfigurationContextChangeBuilder newPropertySource(PropertySource propertySource) {
        this.changedPropertySources.add(PropertySourceChange.ofAdded(propertySource));
        return this;
    }

    /**
     * This method records all changes to be applied to the base property provider/configuration to
     * achieve the given target state.
     *
     * @param propertySource the new target state, not null.
     * @return the builder for chaining.
     */
    public ConfigurationContextChangeBuilder removedPropertySource(PropertySource propertySource) {
        this.changedPropertySources.add(PropertySourceChange.ofDeleted(propertySource));
        return this;
    }

    /**
     * This method records all changes to be applied to the base property provider/configuration to
     * achieve the given target state.
     *
     * @param propertySourceChange the change state, not null.
     * @return the builder for chaining.
     */
    public ConfigurationContextChangeBuilder changedPropertySource(PropertySourceChange propertySourceChange) {
        this.changedPropertySources.add(Objects.requireNonNull(propertySourceChange));
        return this;
    }

    /**
     * Checks if the change set is empty, i.e. does not contain any changes.
     *
     * @return true, if the set is empty.
     */
    public boolean isEmpty() {
        return this.changedPropertySources.isEmpty();
    }

    /**
     * Resets this change set instance. This will clear all changes done to this builder, so the
     * set will be empty.
     */
    public void reset() {
        this.changedPropertySources.clear();
    }

    /**
     * Builds the corresponding change set.
     *
     * @return the new change set, never null.
     */
    public ConfigurationContextChange build() {
        return new ConfigurationContextChange(this);
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ConfigurationContextChangeBuilder [propertySources=" + changedPropertySources + "]";
    }


}
