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

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.apache.tamaya.Configuration;
import org.apache.tamaya.PropertySource;
import org.apache.tamaya.core.properties.AggregationPolicy;
import org.apache.tamaya.core.properties.PropertySourceBuilder;

/**
* Builder for assembling non trivial {@link org.apache.tamaya.Configuration} instances.
*/
public final class ConfigurationBuilder {

    /**
     * The final meta info to be used, or null, if a default should be generated.
     */
    private PropertySourceBuilder builderDelegate;

    /**
     * Private singleton constructor.
     */
    private ConfigurationBuilder(String name) {
        this.builderDelegate = PropertySourceBuilder.of(name);
    }

    /**
     * Private singleton constructor.
     */
    private ConfigurationBuilder(String name, PropertySource source) {
        this.builderDelegate = PropertySourceBuilder.of(name, source);
    }

    /**
     * Private singleton constructor.
     */
    private ConfigurationBuilder(PropertySource source) {
        this.builderDelegate = PropertySourceBuilder.of(source);
    }


    /**
     * Creates a new builder instance.
     *
     * @param provider the base provider to be used, not null.
     * @return a new builder instance, never null.
     */
    public static ConfigurationBuilder of(PropertySource provider) {
        return new ConfigurationBuilder(provider);
    }

    /**
     * Creates a new builder instance.
     *
     * @param name the provider name, not null.
     * @return a new builder instance, never null.
     */
    public static ConfigurationBuilder of(String name) {
        return new ConfigurationBuilder(Objects.requireNonNull(name));
    }

    /**
     * Creates a new builder instance.
     *
     * @return a new builder instance, never null.
     */
    public static ConfigurationBuilder of() {
        return new ConfigurationBuilder("<noname>");
    }




    /**
     * Sets the aggregation policy to be used, when adding additional property sets. The policy will
     * be active a slong as the builder is used or it is reset to another keys.
     *
     * @param aggregationPolicy the aggregation policy, not null.
     * @return the builder for chaining.
     */
    public ConfigurationBuilder withAggregationPolicy(AggregationPolicy aggregationPolicy) {
        this.builderDelegate.withAggregationPolicy(aggregationPolicy);
        return this;
    }

    /**
     * Sets the meta info to be used for the next operation.
     *
     * @param name the name, not null.
     * @return the builder for chaining.
     */
    public ConfigurationBuilder withName(String name) {
        this.builderDelegate.withName(name);
        return this;
    }

    /**
     * Adds the given providers with the current active {@link AggregationPolicy}. By
     * default {@link AggregationPolicy#OVERRIDE} is used.
     * @see #withAggregationPolicy(AggregationPolicy)
     * @param providers providers to be added, not null.
     * @return the builder for chaining.
     */
    public ConfigurationBuilder addProviders(PropertySource... providers) {
        this.builderDelegate.addProviders(providers);
        return this;
    }

    /**
     * Adds the given providers with the current active {@link AggregationPolicy}. By
     * default {@link AggregationPolicy#OVERRIDE} is used.
     * @see #withAggregationPolicy(AggregationPolicy)
     * @param providers providers to be added, not null.
     * @return the builder for chaining.
     */
    public ConfigurationBuilder addProviders(List<PropertySource> providers) {
        this.builderDelegate.addProviders(providers);
        return this;
    }


    /**
     * Creates a new {@link org.apache.tamaya.PropertySource} using the given command line arguments and adds it
     * using the current aggregation policy in place.
     *
     * @param args the command line arguments, not null.
     * @return the builder for chaining.
     */
    public ConfigurationBuilder addArgs(String... args) {
        this.builderDelegate.addArgs(args);
        return this;
    }

    /**
     * Creates a new read-only {@link org.apache.tamaya.PropertySource} by reading the according path resources. The effective resources read
     * hereby are determined by the {@code PathResolverService} configured into the {@code Bootstrap} SPI.
     * Properties read are aggregated using the current aggregation policy active.
     *
     * @param paths the paths to be resolved by the {@code PathResolverService} , not null.
     * @return the builder for chaining.
     */
    public ConfigurationBuilder addPaths(String... paths) {
        this.builderDelegate.addPaths(paths);
        return this;
    }


    /**
     * Creates a new read-only {@link org.apache.tamaya.PropertySource} by reading the according path resources. The effective resources read
     * hereby are determined by the {@code PathResolverService} configured into the {@code Bootstrap} SPI.
     * Properties read are aggregated using the current aggregation policy active.
     *
     * @param paths the paths to be resolved by the {@code PathResolverService} , not null.
     * @return the builder for chaining.
     */
    public ConfigurationBuilder addPaths(List<String> paths) {
        this.builderDelegate.addPaths(paths);
        return this;
    }

    /**
     * Creates a new read-only {@link org.apache.tamaya.PropertySource} by reading the according URL resources.
     * Properties read are aggregated using the current aggregation policy active.
     *
     * @param urls the urls to be read, not null.
     * @return the builder for chaining.
     */
    public ConfigurationBuilder addURLs(URL... urls) {
        this.builderDelegate.addURLs(urls);
        return this;
    }

    /**
     * Creates a new read-only {@link org.apache.tamaya.PropertySource} by reading the according URL resources.
     * Properties read are aggregated using the current aggregation policy active.
     *
     * @param urls the urls to be read, not null.
     * @return the builder for chaining.
     */
    public ConfigurationBuilder addURLs(List<URL> urls) {
        this.builderDelegate.addURLs(urls);
        return this;
    }


    /**
     * Creates a new read-only {@link org.apache.tamaya.PropertySource} based on the given map.
     * Properties read are aggregated using the current aggregation policy active.
     *
     * @param map the map to be added, not null.
     * @return the builder for chaining.
     */
    public ConfigurationBuilder addMap(Map<String, String> map) {
        this.builderDelegate.addMap(map);
        return this;
    }


    /**
     * Add the current environment properties. Aggregation is based on the current {@link AggregationPolicy} acvtive.
     *
     * @return the builder for chaining.
     */
    public ConfigurationBuilder addEnvironmentProperties() {
        this.builderDelegate.addEnvironmentProperties();
        return this;
    }

    /**
     * Add the current system properties. Aggregation is based on the current {@link AggregationPolicy} acvtive.
     *
     * @return the builder for chaining.
     */
    public ConfigurationBuilder addSystemProperties() {
        this.builderDelegate.addSystemProperties();
        return this;
    }

    /**
     * Adds the given {@link org.apache.tamaya.PropertySource} instances using the current {@link AggregationPolicy}
     * active.
     *
     * @param providers the maps to be included, not null.
     * @return the builder for chaining.
     */
    public ConfigurationBuilder aggregate(PropertySource... providers) {
        this.builderDelegate.aggregate(providers);
        return this;
    }


    /**
     * Adds the given {@link org.apache.tamaya.PropertySource} instances using the current {@link AggregationPolicy}
     * active.
     *
     * @param providers the maps to be included, not null.
     * @return the builder for chaining.
     */
    public ConfigurationBuilder aggregate(List<PropertySource> providers) {
        this.builderDelegate.aggregate(providers);
        return this;
    }


    /**
     * Intersetcs the current properties with the given {@link org.apache.tamaya.PropertySource} instance.
     *
     * @param providers the maps to be intersected, not null.
     * @return the builder for chaining.
     */
    public ConfigurationBuilder intersect(PropertySource... providers) {
        this.builderDelegate.intersect(providers);
        return this;
    }


    /**
     * Subtracts with the given {@link org.apache.tamaya.PropertySource} instance from the current properties.
     *
     * @param providers the maps to be subtracted, not null.
     * @return the builder for chaining.
     */
    public ConfigurationBuilder subtract(PropertySource... providers) {
        this.builderDelegate.subtract(providers);
        return this;
    }


    /**
     * Filters the current properties based on the given predicate..
     *
     * @param filter the filter to be applied, not null.
     * @return the new filtering instance.
     */
    public ConfigurationBuilder filter(Predicate<String> filter) {
        this.builderDelegate.filter(filter);
        return this;
    }

    /**
     * Filters the current {@link org.apache.tamaya.Configuration} with the given valueFilter.
     * @param valueFilter the value filter, not null.
     * @return the (dynamically) filtered source instance, never null.
     */
    public ConfigurationBuilder filterValues(BiFunction<String, String, String> valueFilter){
        this.builderDelegate.filterValues(valueFilter);
        return this;
    }

    /**
     * Creates a new contextual {@link org.apache.tamaya.PropertySource}. Contextual maps delegate to different instances current PropertyMap depending
     * on the keys returned fromMap the isolationP
     *
     * @param mapSupplier          the supplier creating new provider instances
     * @param isolationKeySupplier the supplier providing contextual keys based on the current environment.
     */
    public ConfigurationBuilder addContextual(Supplier<PropertySource> mapSupplier,
                                                 Supplier<String> isolationKeySupplier) {
        this.builderDelegate.addContextual(mapSupplier, isolationKeySupplier);
        return this;
    }

    /**
     * Replaces all keys in the current provider by the given map.
     *
     * @param replacementMap the map instance, that will replace all corresponding entries in {@code mainMap}, not null.
     * @return the new delegating instance.
     */
    public ConfigurationBuilder replace(Map<String, String> replacementMap) {
        this.builderDelegate.replace(replacementMap);
        return this;
    }

    /**
     * Build a new property provider based on the input.
     * @return a new property provider, or null.
     */
    public PropertySource buildPropertySource(){
        return this.builderDelegate.build();
    }

    /**
     * Build a new property provider based on the input.
     * @return a new property provider, or null.
     */
    public Configuration build(){
        return this.buildPropertySource().toConfiguration();
    }

    /**
     * Creates a {@link org.apache.tamaya.PropertySource} instance that is serializable and immutable,
     * so it can be sent over a network connection.
     *
     * @return the freezed instance, never null.
     */
    public PropertySource buildFreezedPropertySource() {
        return this.builderDelegate.buildFrozen();
    }

    /**
     * Creates a {@link org.apache.tamaya.PropertySource} instance that is serializable and immutable,
     * so it can be sent over a network connection.
     *
     * @return the freezed instance, never null.
     */
    public Configuration buildFreezed() {
        return FreezedConfiguration.of(this.buildFreezedPropertySource().toConfiguration());
    }

}
