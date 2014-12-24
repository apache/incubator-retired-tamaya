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
package org.apache.tamaya.core.properties;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.oracle.webservices.internal.api.message.PropertySet;
import org.apache.tamaya.AggregationPolicy;
import org.apache.tamaya.MetaInfo;
import org.apache.tamaya.MetaInfoBuilder;
import org.apache.tamaya.PropertySource;

/**
* Builder for assembling non trivial property providers.
*/
public final class PropertySourceBuilder {

    /**
     * Name used for the final result.
     */
    private String name;

    /**
     * Name used for the next operation.
     */
    private String currentName;

    /**
     * the current property provider, or null.
     */
    private PropertySource current;
    /**
     * The current aggregation policy used, when aggregating providers.
     */
    private AggregationPolicy aggregationPolicy = AggregationPolicy.OVERRIDE;

    /**
     * Private singleton constructor.
     */
    private PropertySourceBuilder(String name) {
        this.name = Objects.requireNonNull(name);
    }

    /**
     * Private singleton constructor.
     */
    private PropertySourceBuilder(String name, PropertySource propertySource) {
        this.name = Objects.requireNonNull(name);
        this.current = propertySource;
    }

    /**
     * Creates a new builder instance.
     *
     * @param name the provider name, not null.
     * @param provider the base provider to be used, not null.
     * @return a new builder instance, never null.
     */
    public static PropertySourceBuilder of(String name, PropertySource provider) {
        return new PropertySourceBuilder(name, provider);
    }

    /**
     * Creates a new builder instance.
     *
     * @param provider the base provider to be used, not null.
     * @return a new builder instance, never null.
     */
    public static PropertySourceBuilder of(PropertySource provider) {
        return new PropertySourceBuilder(provider.getName(), provider);
    }


    /**
     * Creates a new builder instance.
     *
     * @param name the provider name, not null.
     * @return a new builder instance, never null.
     */
    public static PropertySourceBuilder of(String name) {
        return new PropertySourceBuilder(Objects.requireNonNull(name));
    }

    /**
     * Creates a new builder instance.
     *
     * @return a new builder instance, never null.
     */
    public static PropertySourceBuilder of() {
        return new PropertySourceBuilder("<noname>");
    }




    /**
     * Sets the aggregation policy to be used, when adding additional property sets. The policy will
     * be active a slong as the builder is used or it is reset to another keys.
     *
     * @param aggregationPolicy the aggregation policy, not null.
     * @return the builder for chaining.
     */
    public PropertySourceBuilder withAggregationPolicy(AggregationPolicy aggregationPolicy) {
        this.aggregationPolicy = Objects.requireNonNull(aggregationPolicy);
        return this;
    }

    /**
     * Adds the given providers with the current active {@link org.apache.tamaya.AggregationPolicy}. By
     * default {@link org.apache.tamaya.AggregationPolicy#OVERRIDE} is used.
     * @see #withAggregationPolicy(AggregationPolicy)
     * @param providers providers to be added, not null.
     * @return the builder for chaining.
     */
    public PropertySourceBuilder addProviders(PropertySource... providers) {
        if(providers.length==0){
            return this;
        }
        return addProviders(Arrays.asList(providers));
    }

    /**
     * Adds the given providers with the current active {@link org.apache.tamaya.AggregationPolicy}. By
     * default {@link org.apache.tamaya.AggregationPolicy#OVERRIDE} is used.
     * @see #withAggregationPolicy(AggregationPolicy)
     * @param providers providers to be added, not null.
     * @return the builder for chaining.
     */
    public PropertySourceBuilder addProviders(List<PropertySource> providers) {
        if(providers.isEmpty()){
            return this;
        }
        List<PropertySource> allProviders = new ArrayList<>(providers);
        if (this.current != null) {
            allProviders.add(0, this.current);
        }
        StringBuilder b = new StringBuilder();
        providers.forEach(p -> b.append(p.getName()).append(','));
        b.setLength(b.length()-1);
        String source = b.toString();
        String name = this.currentName;
        if (currentName == null) {
            name = "<aggregate> -> source=" + source;
        }
        this.current = PropertySourceFactory.aggregate(name, this.aggregationPolicy, allProviders);
        this.currentName = null;
        return this;
    }

    /**
     * Creates a new {@link PropertySource} using the given command line arguments and adds it
     * using the current aggregation policy in place.
     *
     * @param args the command line arguments, not null.
     * @return the builder for chaining.
     */
    public PropertySourceBuilder addArgs(String... args) {
        if(args.length==0){
            return this;
        }
        String name = this.currentName;
        if (currentName == null) {
            name = "<CLI-args>";
        }
        PropertySource argProvider = PropertySourceFactory.fromArgs(name, args);
        return addProviders(argProvider);
    }

    /**
     * Creates a new read-only {@link PropertySource} by reading the according path resources. The effective resources read
     * hereby are determined by the {@code PathResolverService} configured into the {@code Bootstrap} SPI.
     * Properties read are aggregated using the current aggregation policy active.
     *
     * @param paths the paths to be resolved by the {@code PathResolverService} , not null.
     * @return the builder for chaining.
     */
    public PropertySourceBuilder addPaths(String... paths) {
        if(paths.length==0){
            return this;
        }
        return addPaths(Arrays.asList(paths));
    }


    /**
     * Creates a new read-only {@link PropertySource} by reading the according path resources. The effective resources read
     * hereby are determined by the {@code PathResolverService} configured into the {@code Bootstrap} SPI.
     * Properties read are aggregated using the current aggregation policy active.
     *
     * @param paths the paths to be resolved by the {@code PathResolverService} , not null.
     * @return the builder for chaining.
     */
    public PropertySourceBuilder addPaths(List<String> paths) {
        if(paths.isEmpty()){
            return this;
        }
        String name = this.currentName;
        if (currentName == null) {
            name = "<aggregate> -> paths=" + paths.toString();
        }
        return addProviders(PropertySourceFactory.fromPaths(name, aggregationPolicy, paths));
    }

    /**
     * Creates a new read-only {@link PropertySource} by reading the according URL resources.
     * Properties read are aggregated using the current aggregation policy active.
     *
     * @param urls the urls to be read, not null.
     * @return the builder for chaining.
     */
    public PropertySourceBuilder addURLs(URL... urls) {
        if(urls.length==0){
            return this;
        }
        return addURLs(Arrays.asList(urls));
    }

    /**
     * Creates a new read-only {@link PropertySource} by reading the according URL resources.
     * Properties read are aggregated using the current aggregation policy active.
     *
     * @param urls the urls to be read, not null.
     * @return the builder for chaining.
     */
    public PropertySourceBuilder addURLs(List<URL> urls) {
        if(urls.isEmpty()){
            return this;
        }
        String name = this.currentName;
        if (currentName == null) {
            name = "<aggregate> -> urls=" + urls;
        }
        return addProviders(PropertySourceFactory.fromURLs(name, this.aggregationPolicy, urls));
    }


    /**
     * Creates a new read-only {@link PropertySource} based on the given map.
     * Properties read are aggregated using the current aggregation policy active.
     *
     * @param map the map to be added, not null.
     * @return the builder for chaining.
     */
    public PropertySourceBuilder addMap(Map<String, String> map) {
        if(map.isEmpty()){
            return this;
        }
        String name = this.currentName;
        if (currentName == null) {
            name = "<Map> -> map=" + map;
        }
        return addProviders(PropertySourceFactory.fromMap(name, map));
    }


    /**
     * Add the current environment properties. Aggregation is based on the current {@link org.apache.tamaya.AggregationPolicy} acvtive.
     *
     * @return the builder for chaining.
     */
    public PropertySourceBuilder addEnvironmentProperties() {
        String name = this.currentName;
        if (currentName == null) {
            name = "<System.getenv()>";
        }
        return addProviders(PropertySourceFactory.fromEnvironmentProperties());
    }

    /**
     * Add the current system properties. Aggregation is based on the current {@link org.apache.tamaya.AggregationPolicy} acvtive.
     *
     * @return the builder for chaining.
     */
    public PropertySourceBuilder addSystemProperties() {
        String name = this.currentName;
        if (currentName == null) {
            name = "<System.getProperties()>";
        }
        return addProviders(PropertySourceFactory.fromSystemProperties());
    }

    /**
     * Add the name used for the next aggregation/adding operation on this builder.
     * @param name the name to be used, not null.
     * @return the builder for chaining.
     */
    public PropertySourceBuilder withName(String name) {
        this. currentName = Objects.requireNonNull(name);
        return this;
    }

    /**
     * Adds the given {@link org.apache.tamaya.PropertySource} instances using the current {@link org.apache.tamaya.AggregationPolicy}
     * active.
     *
     * @param providers the maps to be included, not null.
     * @return the builder for chaining.
     */
    public PropertySourceBuilder aggregate(PropertySource... providers) {
        if(providers.length==0){
            return this;
        }
        String name = this.currentName;
        if (currentName == null) {
            name = "<aggregate> -> " + Arrays.toString(providers);
        }
        return addProviders(PropertySourceFactory.aggregate(name, aggregationPolicy, Arrays.asList(providers)));
    }


    /**
     * Adds the given {@link org.apache.tamaya.PropertySource} instances using the current {@link org.apache.tamaya.AggregationPolicy}
     * active.
     *
     * @param providers the maps to be included, not null.
     * @return the builder for chaining.
     */
    public PropertySourceBuilder aggregate(List<PropertySource> providers) {
        if(providers.isEmpty()){
            return this;
        }
        String name = this.currentName;
        if (currentName == null) {
            name = "<aggregate> -> " + providers;
        }
        return addProviders(PropertySourceFactory.aggregate(name, aggregationPolicy, providers));
    }


    /**
     * Creates a new {@link org.apache.tamaya.PropertySource} that is mutable by adding a map based instance that overrides
     * values fromMap the original map.
     *
     * @param provider the provider to be made mutable, not null.
     * @return the mutable instance.
     */
    public static PropertySource mutable(PropertySource provider) {
        return PropertySourceFactory.mutable(null, provider);
    }


    /**
     * Intersetcs the current properties with the given {@link org.apache.tamaya.PropertySource} instance.
     *
     * @param providers the maps to be intersected, not null.
     * @return the builder for chaining.
     */
    public PropertySourceBuilder intersect(PropertySource... providers) {
        if(providers.length==0){
            return this;
        }
        String name = this.currentName;
        if (currentName == null) {
            name = "<intersection> -> " + Arrays.toString(providers);
        }
        return addProviders(PropertySourceFactory.intersected(name, aggregationPolicy, Arrays.asList(providers)));
    }


    /**
     * Subtracts with the given {@link org.apache.tamaya.PropertySource} instance from the current properties.
     *
     * @param providers the maps to be subtracted, not null.
     * @return the builder for chaining.
     */
    public PropertySourceBuilder subtract(PropertySource... providers) {
        if(providers.length==0){
            return this;
        }
        String name = this.currentName;
        if (currentName == null) {
            name = "<subtraction> -> " + Arrays.toString(providers);
        }
        current = PropertySourceFactory.subtracted(name, current, Arrays.asList(providers));
        return this;
    }


    /**
     * Filters the current properties based on the given predicate..
     *
     * @param filter the filter to be applied, not null.
     * @return the new filtering instance.
     */
    public PropertySourceBuilder filter(Predicate<String> filter) {
        String name = this.currentName;
        if (currentName == null) {
            name = "<filtered> -> " + filter;
        }
        current = PropertySourceFactory.filtered(name, filter, current);
        this.currentName = null;
        return this;
    }

    /**
     * Creates a new contextual {@link org.apache.tamaya.PropertySource}. Contextual maps delegate to different instances current PropertyMap depending
     * on the keys returned fromMap the isolationP
     *
     * @param mapSupplier          the supplier creating new provider instances
     * @param isolationKeySupplier the supplier providing contextual keys based on the current environment.
     */
    public PropertySourceBuilder addContextual(Supplier<PropertySource> mapSupplier,
                                                 Supplier<String> isolationKeySupplier) {
        String name = this.currentName;
        if (currentName == null) {
            name = "<contextual> -> map="+mapSupplier+",isolationKeySupplier="+isolationKeySupplier;
        }
        return addProviders(PropertySourceFactory.contextual(name, mapSupplier, isolationKeySupplier));
    }

    /**
     * Replaces all keys in the current provider by the given map.
     *
     * @param replacementMap the map instance, that will replace all corresponding entries in {@code mainMap}, not null.
     * @return the new delegating instance.
     */
    public PropertySourceBuilder replace(Map<String, String> replacementMap) {
        String name = this.currentName;
        if (currentName == null) {
            name = "<replacement> -> current="+current.getName()+" with ="+replacementMap;
        }
        current = PropertySourceFactory.replacing(name, current, replacementMap);
        this.currentName = null;
        return this;
    }

    /**
     * Build a new property provider based on the input.
     * @return a new property provider, or null.
     */
    public PropertySource build(){
        if (current != null) {
            return PropertySourceFactory.build(name, current);
        }
        return PropertySourceFactory.empty(name);
    }

    /**
     * Creates a {@link PropertySource} instance that is serializable and immutable,
     * so it can be sent over a network connection.
     *
     * @return the freezed instance, never null.
     */
    public PropertySource buildFreezed() {
        String name = this.currentName;
        if (currentName == null) {
            name = "<freezed> -> source="+current.getName();
        }
        PropertySource prov = PropertySourceFactory.freezed(name, current);
        this.currentName = null;
        return prov;
    }

}
