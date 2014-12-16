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

import org.apache.tamaya.*;

import java.net.URL;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Logger;

/**
* Builder for assembling non trivial property providers.
*/
public final class PropertySourceBuilder {
    private static final Supplier<IllegalStateException> noPropertyProviderAvailable =
        () -> new IllegalStateException("No PropertyProvidersSingletonSpi available.");

    /**
     * THe logger used.
     */
    private static final Logger LOG = Logger.getLogger(PropertySourceBuilder.class.getName());

    /**
     * The final meta info to be used, or null, if a default should be generated.
     */
    private MetaInfoBuilder metaInfoBuilder;

    /**
     * Meta info used for the next operation.
     */
    private MetaInfo metaInfo;

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
    private PropertySourceBuilder(MetaInfo metaInfo) {
        this.metaInfoBuilder = MetaInfoBuilder.of(Objects.requireNonNull(metaInfo)).setInfo("Built by PropertyProviderBuilder.");
    }

    /**
     * Private singleton constructor.
     */
    private PropertySourceBuilder(String name) {
        this.metaInfoBuilder = MetaInfoBuilder.of(name);
    }

    /**
     * Private singleton constructor.
     */
    private PropertySourceBuilder(PropertySource provider) {
        this.metaInfoBuilder = MetaInfoBuilder.of(Objects.requireNonNull(provider).getMetaInfo());
        this.current = provider;
    }


    /**
     * Creates a new builder instance.
     *
     * @param provider the base provider to be used, not null.
     * @return a new builder instance, never null.
     */
    public static PropertySourceBuilder of(PropertySource provider) {
        return new PropertySourceBuilder(provider);
    }

    /**
     * Creates a new builder instance.
     *
     * @param metaInfo the meta information, not null.
     * @return a new builder instance, never null.
     */
    public static PropertySourceBuilder of(MetaInfo metaInfo) {
        return new PropertySourceBuilder(metaInfo);
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
     * be active a slong as the builder is used or it is reset to another value.
     *
     * @param aggregationPolicy the aggregation policy, not null.
     * @return the builder for chaining.
     */
    public PropertySourceBuilder withAggregationPolicy(AggregationPolicy aggregationPolicy) {
        this.aggregationPolicy = Objects.requireNonNull(aggregationPolicy);
        return this;
    }

    /**
     * Sets the meta info to be used for the next operation.
     *
     * @param metaInfo the meta info, not null.
     * @return the builder for chaining.
     */
    public PropertySourceBuilder withMetaInfo(MetaInfo metaInfo) {
        this.metaInfo = Objects.requireNonNull(metaInfo);
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
        providers.forEach(p -> b.append(p.getMetaInfo().toString()).append(','));
        b.setLength(b.length()-1);
        String source = b.toString();
        MetaInfo mi = this.metaInfo;
        if (mi == null) {
            mi = MetaInfoBuilder.of("aggregate")
                    .set(MetaInfoBuilder.SOURCE,source).build();
        }
        this.current = PropertySourceFactory.aggregate(mi, this.aggregationPolicy, allProviders);

        addProviderChainInfo(source);
        this.metaInfo = null;
        return this;
    }

    private void addProviderChainInfo(String info){
        String providerChain = metaInfoBuilder.get("providerChain");

        if(providerChain == null){
            providerChain = "\n  " + info;
        }
        else{
            providerChain = providerChain + ",\n  " + info;
        }
        metaInfoBuilder.set("providerChain", providerChain);
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
        MetaInfo mi = this.metaInfo;
        if (mi == null) {
            mi = MetaInfoBuilder.of("args").build();
        }
        PropertySource argProvider = PropertySourceFactory.fromArgs(mi, args);
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
        MetaInfo mi = this.metaInfo;
        if (mi == null) {
            mi = MetaInfoBuilder.of("aggregate").set("paths", paths.toString()).build();
        } else {
            mi = MetaInfoBuilder.of(metaInfo).set("paths", paths.toString()).build();
        }
        return addProviders(PropertySourceFactory.fromPaths(mi, aggregationPolicy, paths));
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
        MetaInfo mi = this.metaInfo;
        if (mi == null) {
            mi = MetaInfoBuilder.of("aggregate").set("urls", urls.toString()).build();
        } else {
            mi = MetaInfoBuilder.of(metaInfo).set("urls", urls.toString()).build();
        }

        return addProviders(PropertySourceFactory.fromURLs(mi, this.aggregationPolicy, urls));
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
        MetaInfo mi = this.metaInfo;
        if (mi == null) {
            mi = MetaInfoBuilder.of("map").build();
        } else {
            mi = MetaInfoBuilder.of(metaInfo).build();
        }
        return addProviders(PropertySourceFactory.fromMap(mi, map));
    }


    /**
     * Add the current environment properties. Aggregation is based on the current {@link org.apache.tamaya.AggregationPolicy} acvtive.
     *
     * @return the builder for chaining.
     */
    public PropertySourceBuilder addEnvironmentProperties() {
        MetaInfo mi = this.metaInfo;
        if (mi == null) {
            mi = MetaInfoBuilder.of("environment.properties").build();
        } else {
            mi = MetaInfoBuilder.of(metaInfo).build();
        }

        return addProviders(PropertySourceFactory.fromEnvironmentProperties());
    }

    /**
     * Add the current system properties. Aggregation is based on the current {@link org.apache.tamaya.AggregationPolicy} acvtive.
     *
     * @return the builder for chaining.
     */
    public PropertySourceBuilder addSystemProperties() {
        MetaInfo mi = this.metaInfo;
        if (mi == null) {
            mi = MetaInfoBuilder.of("system.properties").build();
        } else {
            mi = MetaInfoBuilder.of(metaInfo).build();
        }

        return addProviders(PropertySourceFactory.fromSystemProperties());
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
        MetaInfo mi = this.metaInfo;
        if (mi == null) {
            mi = MetaInfoBuilder.of("aggregate").build();
        } else {
            mi = MetaInfoBuilder.of(metaInfo).build();
        }

        return addProviders(PropertySourceFactory.aggregate(mi, aggregationPolicy, Arrays.asList(providers)));
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
        MetaInfo mi = this.metaInfo;
        if (mi == null) {
            mi = MetaInfoBuilder.of("aggregate").build();
        } else {
            mi = MetaInfoBuilder.of(metaInfo).build();
        }

        return addProviders(PropertySourceFactory.aggregate(mi, aggregationPolicy, providers));
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
        MetaInfo mi = this.metaInfo;
        if (mi == null) {
            mi = MetaInfoBuilder.of("intersect").build();
        } else {
            mi = MetaInfoBuilder.of(metaInfo).build();
        }

        return addProviders(PropertySourceFactory.intersected(mi, aggregationPolicy, Arrays.asList(providers)));
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
        MetaInfo mi = this.metaInfo;
        if (mi == null) {
            mi = MetaInfoBuilder.of("subtract").build();
        } else {
            mi = MetaInfoBuilder.of(metaInfo).build();
        }
        current = PropertySourceFactory.subtracted(mi, current, Arrays.asList(providers));
        return this;
    }


    /**
     * Filters the current properties based on the given predicate..
     *
     * @param filter the filter to be applied, not null.
     * @return the new filtering instance.
     */
    public PropertySourceBuilder filter(Predicate<String> filter) {
        MetaInfo mi = this.metaInfo;
        if (mi == null) {
            mi = MetaInfoBuilder.of("filtered").set("filter", filter.toString()).build();
        } else {
            mi = MetaInfoBuilder.of(metaInfo).set("filter", filter.toString()).build();
        }
        current = PropertySourceFactory.filtered(mi, filter, current);
        addProviderChainInfo("filter->" + filter.toString());
        this.metaInfo = null;
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
        MetaInfo mi = this.metaInfo;
        if (mi == null) {
            mi = MetaInfoBuilder.of("contextual").set("mapSupplier", mapSupplier.toString()).set("isolationKeySupplier", isolationKeySupplier.toString()).build();
        } else {
            mi = MetaInfoBuilder.of(metaInfo).set("mapSupplier", mapSupplier.toString()).set("isolationKeySupplier", isolationKeySupplier.toString()).build();
        }

        return addProviders(PropertySourceFactory.contextual(mi, mapSupplier, isolationKeySupplier));
    }

    /**
     * Replaces all keys in the current provider by the given map.
     *
     * @param replacementMap the map instance, that will replace all corresponding entries in {@code mainMap}, not null.
     * @return the new delegating instance.
     */
    public PropertySourceBuilder replace(Map<String, String> replacementMap) {
        MetaInfo mi = this.metaInfo;
        if (mi == null) {
            mi = MetaInfoBuilder.of("replace").build();
        } else {
            mi = MetaInfoBuilder.of(metaInfo).build();
        }
        current = PropertySourceFactory.replacing(mi, current, replacementMap);
        this.metaInfo = null;
        addProviderChainInfo("replace->" + replacementMap.toString());
        return this;
    }

    /**
     * Sets an additional key on the final {@link org.apache.tamaya.MetaInfo} of the provider
     * created.
     *
     * @param key the key to be added, not null.
     * @param value the value to be added, not null.
     * @return this builder for chaining
     */
    public PropertySourceBuilder setMeta(String key, String value){
        this.metaInfoBuilder.set(key, value);
        return this;
    }

    /**
     * Build a new property provider based on the input.
     * @return a new property provider, or null.
     */
    public PropertySource build(){
        if (current != null) {
            return PropertySourceFactory.build(metaInfoBuilder.build(), current);
        }

        return PropertySourceFactory.empty(metaInfoBuilder.build());
    }

    /**
     * Creates a {@link PropertySource} instance that is serializable and immutable,
     * so it can be sent over a network connection.
     *
     * @return the freezed instance, never null.
     */
    public PropertySource buildFreezed() {
        MetaInfo mi = this.metaInfo;
        if (mi == null) {
            mi = MetaInfoBuilder.of("freezed").set("freezed", "true").build();
        } else {
            mi = MetaInfoBuilder.of(metaInfo).set("freezed", "true").build();
        }

        PropertySource prov = PropertySourceFactory.freezed(mi, current);
        this.metaInfo = null;
        return prov;
    }

}
