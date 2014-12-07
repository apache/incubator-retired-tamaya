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

import org.apache.tamaya.spi.Bootstrap;
import org.apache.tamaya.spi.PropertyProviderBuilderSpi;

import java.net.URL;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Logger;

/**
 * Builder for assembling non trivial property providers.
 */
public final class PropertyProviderBuilder {
    /**
     * SPI backing up this builder.
     */
    private static final PropertyProviderBuilderSpi spi = loadPropertyProvidersSpi();
    /**
     * THe logger used.
     */
    private static final Logger LOG = Logger.getLogger(PropertyProviderBuilder.class.getName());
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
    private PropertyProvider current;
    /**
     * The current aggregation policy used, when aggregating providers.
     */
    private AggregationPolicy aggregationPolicy = AggregationPolicy.OVERRIDE;

    /**
     * Private singleton constructor.
     */
    private PropertyProviderBuilder(MetaInfo metaInfo) {
        this.metaInfoBuilder = MetaInfoBuilder.of(Objects.requireNonNull(metaInfo)).setInfo("Built by PropertyProviderBuilder.");
    }

    /**
     * Private singleton constructor.
     */
    private PropertyProviderBuilder(String name) {
        this.metaInfoBuilder = MetaInfoBuilder.of(name);
    }

    /**
     * Private singleton constructor.
     */
    private PropertyProviderBuilder(PropertyProvider provider) {
        this.metaInfoBuilder = MetaInfoBuilder.of(Objects.requireNonNull(provider).getMetaInfo());
        this.current = provider;
    }

    /**
     * Method to initially load the singleton SPI fromMap the {@link org.apache.tamaya.spi.Bootstrap} mechanism.
     * The instance loaded will be used until the VM is shutdown. In case use cases require more flexibility
     * it should be transparently implemented in the SPI implementation. This singleton will simply delegate calls
     * and not cache any responses.
     *
     * @return the SPI, never null.
     */
    private static PropertyProviderBuilderSpi loadPropertyProvidersSpi() {
        return Bootstrap.getService(PropertyProviderBuilderSpi.class);
    }

    /**
     * Creates a new builder instance.
     *
     * @param provider the base provider to be used, not null.
     * @return a new builder instance, never null.
     */
    public static PropertyProviderBuilder create(PropertyProvider provider) {
        return new PropertyProviderBuilder(provider);
    }

    /**
     * Creates a new builder instance.
     *
     * @param metaInfo the meta information, not null.
     * @return a new builder instance, never null.
     */
    public static PropertyProviderBuilder create(MetaInfo metaInfo) {
        return new PropertyProviderBuilder(metaInfo);
    }

    /**
     * Creates a new builder instance.
     *
     * @param name the provider name, not null.
     * @return a new builder instance, never null.
     */
    public static PropertyProviderBuilder create(String name) {
        return new PropertyProviderBuilder(Objects.requireNonNull(name));
    }

    /**
     * Creates a new builder instance.
     *
     * @return a new builder instance, never null.
     */
    public static PropertyProviderBuilder create() {
        return new PropertyProviderBuilder("<noname>");
    }




    /**
     * Sets the aggregation policy to be used, when adding additional property sets. The policy will
     * be active a slong as the builder is used or it is reset to another value.
     *
     * @param aggregationPolicy the aggregation policy, not null.
     * @return the builder for chaining.
     */
    public PropertyProviderBuilder withAggregationPolicy(AggregationPolicy aggregationPolicy) {
        this.aggregationPolicy = Objects.requireNonNull(aggregationPolicy);
        return this;
    }

    /**
     * Sets the meta info to be used for the next operation.
     *
     * @param metaInfo the meta info, not null.
     * @return the builder for chaining.
     */
    public PropertyProviderBuilder withMetaInfo(MetaInfo metaInfo) {
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
    public PropertyProviderBuilder addProviders(PropertyProvider... providers) {
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
    public PropertyProviderBuilder addProviders(List<PropertyProvider> providers) {
        if(providers.isEmpty()){
            return this;
        }
        List<PropertyProvider> allProviders = new ArrayList<>(providers);
        if (this.current != null) {
            allProviders.add(0, this.current);
        }
        StringBuilder b = new StringBuilder();
        providers.forEach(p -> b.append(p.getMetaInfo().toString()).append(','));
        b.setLength(b.length()-1);
        String source = b.toString();
        MetaInfo mi = this.metaInfo;
        if (mi == null) {
            mi = MetaInfoBuilder.of("aggregate").setEnvironment(Environment.current())
                    .set(MetaInfoBuilder.SOURCE,source).build();
        }
        this.current = Optional.of(spi).orElseThrow(() -> new IllegalStateException("No PropertyProvidersSingletonSpi available."))
                .aggregate(mi, this.aggregationPolicy, allProviders);

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
     * Creates a new {@link PropertyProvider} using the given command line arguments and adds it
     * using the current aggregation policy in place.
     *
     * @param args the command line arguments, not null.
     * @return the builder for chaining.
     */
    public PropertyProviderBuilder addArgs(String... args) {
        if(args.length==0){
            return this;
        }
        MetaInfo mi = this.metaInfo;
        if (mi == null) {
            mi = MetaInfoBuilder.of("args").setEnvironment(Environment.current()).build();
        }
        PropertyProvider argProvider = Optional.of(spi).orElseThrow(() -> new IllegalStateException("No PropertyProvidersSingletonSpi available."))
                .fromArgs(mi, args);
        return addProviders(argProvider);
    }

    /**
     * Creates a new read-only {@link PropertyProvider} by reading the according path resources. The effective resources read
     * hereby are determined by the {@code PathResolverService} configured into the {@code Bootstrap} SPI.
     * Properties read are aggregated using the current aggregation policy active.
     *
     * @param paths the paths to be resolved by the {@code PathResolverService} , not null.
     * @return the builder for chaining.
     */
    public PropertyProviderBuilder addPaths(String... paths) {
        if(paths.length==0){
            return this;
        }
        return addPaths(Arrays.asList(paths));
    }


    /**
     * Creates a new read-only {@link PropertyProvider} by reading the according path resources. The effective resources read
     * hereby are determined by the {@code PathResolverService} configured into the {@code Bootstrap} SPI.
     * Properties read are aggregated using the current aggregation policy active.
     *
     * @param paths the paths to be resolved by the {@code PathResolverService} , not null.
     * @return the builder for chaining.
     */
    public PropertyProviderBuilder addPaths(List<String> paths) {
        if(paths.isEmpty()){
            return this;
        }
        MetaInfo mi = this.metaInfo;
        if (mi == null) {
            mi = MetaInfoBuilder.of("aggregate").set("paths", paths.toString()).setEnvironment(Environment.current()).build();
        } else {
            mi = MetaInfoBuilder.of(metaInfo).setEnvironment(Environment.current()).set("paths", paths.toString()).build();
        }
        return addProviders(Optional.of(spi).orElseThrow(() -> new IllegalStateException("No PropertyProvidersSingletonSpi available."))
                .fromPaths(mi, aggregationPolicy, paths));
    }

    /**
     * Creates a new read-only {@link PropertyProvider} by reading the according URL resources.
     * Properties read are aggregated using the current aggregation policy active.
     *
     * @param urls the urls to be read, not null.
     * @return the builder for chaining.
     */
    public PropertyProviderBuilder addURLs(URL... urls) {
        if(urls.length==0){
            return this;
        }
        return addURLs(Arrays.asList(urls));
    }

    /**
     * Creates a new read-only {@link PropertyProvider} by reading the according URL resources.
     * Properties read are aggregated using the current aggregation policy active.
     *
     * @param urls the urls to be read, not null.
     * @return the builder for chaining.
     */
    public PropertyProviderBuilder addURLs(List<URL> urls) {
        if(urls.isEmpty()){
            return this;
        }
        MetaInfo mi = this.metaInfo;
        if (mi == null) {
            mi = MetaInfoBuilder.of("aggregate").set("urls", urls.toString()).setEnvironment(Environment.current()).build();
        } else {
            mi = MetaInfoBuilder.of(metaInfo).setEnvironment(Environment.current()).set("urls", urls.toString()).build();
        }
        return addProviders(Optional.of(spi).orElseThrow(() -> new IllegalStateException("No PropertyProvidersSingletonSpi available."))
                .fromURLs(mi, this.aggregationPolicy, urls));
    }


    /**
     * Creates a new read-only {@link PropertyProvider} based on the given map.
     * Properties read are aggregated using the current aggregation policy active.
     *
     * @param map the map to be added, not null.
     * @return the builder for chaining.
     */
    public PropertyProviderBuilder addMap(Map<String, String> map) {
        if(map.isEmpty()){
            return this;
        }
        MetaInfo mi = this.metaInfo;
        if (mi == null) {
            mi = MetaInfoBuilder.of("map").setEnvironment(Environment.current()).build();
        } else {
            mi = MetaInfoBuilder.of(metaInfo).setEnvironment(Environment.current()).build();
        }
        return addProviders(Optional.of(spi).orElseThrow(() -> new IllegalStateException("No PropertyProvidersSingletonSpi available."))
                .fromMap(mi, map));
    }


    /**
     * Add the current environment properties. Aggregation is based on the current {@link org.apache.tamaya.AggregationPolicy} acvtive.
     *
     * @return the builder for chaining.
     */
    public PropertyProviderBuilder addEnvironmentProperties() {
        MetaInfo mi = this.metaInfo;
        if (mi == null) {
            mi = MetaInfoBuilder.of("environment.properties").setEnvironment(Environment.current()).build();
        } else {
            mi = MetaInfoBuilder.of(metaInfo).setEnvironment(Environment.current()).build();
        }
        return addProviders(Optional.of(spi).orElseThrow(() -> new IllegalStateException("No PropertyProvidersSingletonSpi available."))
                .fromEnvironmentProperties());
    }

    /**
     * Add the current system properties. Aggregation is based on the current {@link org.apache.tamaya.AggregationPolicy} acvtive.
     *
     * @return the builder for chaining.
     */
    public PropertyProviderBuilder addSystemProperties() {
        MetaInfo mi = this.metaInfo;
        if (mi == null) {
            mi = MetaInfoBuilder.of("system.properties").setEnvironment(Environment.current()).build();
        } else {
            mi = MetaInfoBuilder.of(metaInfo).setEnvironment(Environment.current()).build();
        }
        return addProviders(Optional.of(spi).orElseThrow(() -> new IllegalStateException("No PropertyProvidersSingletonSpi available."))
                .fromSystemProperties());
    }

    /**
     * Adds the given {@link org.apache.tamaya.PropertyProvider} instances using the current {@link org.apache.tamaya.AggregationPolicy}
     * active.
     *
     * @param providers the maps to be included, not null.
     * @return the builder for chaining.
     */
    public PropertyProviderBuilder aggregate(PropertyProvider... providers) {
        if(providers.length==0){
            return this;
        }
        MetaInfo mi = this.metaInfo;
        if (mi == null) {
            mi = MetaInfoBuilder.of("aggregate").setEnvironment(Environment.current()).build();
        } else {
            mi = MetaInfoBuilder.of(metaInfo).setEnvironment(Environment.current()).build();
        }
        return addProviders(Optional.of(spi).orElseThrow(() -> new IllegalStateException("No PropertyProvidersSingletonSpi available."))
                .aggregate(mi, aggregationPolicy, Arrays.asList(providers)));
    }


    /**
     * Adds the given {@link org.apache.tamaya.PropertyProvider} instances using the current {@link org.apache.tamaya.AggregationPolicy}
     * active.
     *
     * @param providers the maps to be included, not null.
     * @return the builder for chaining.
     */
    public PropertyProviderBuilder aggregate(List<PropertyProvider> providers) {
        if(providers.isEmpty()){
            return this;
        }
        MetaInfo mi = this.metaInfo;
        if (mi == null) {
            mi = MetaInfoBuilder.of("aggregate").setEnvironment(Environment.current()).build();
        } else {
            mi = MetaInfoBuilder.of(metaInfo).setEnvironment(Environment.current()).build();
        }
        return addProviders(Optional.of(spi).orElseThrow(() -> new IllegalStateException("No PropertyProvidersSingletonSpi available."))
                .aggregate(mi, aggregationPolicy, providers));
    }


    /**
     * Creates a new {@link org.apache.tamaya.PropertyProvider} that is mutable by adding a map based instance that overrides
     * values fromMap the original map.
     *
     * @param provider the provider to be made mutable, not null.
     * @return the mutable instance.
     */
    public static PropertyProvider mutable(PropertyProvider provider) {
        return Optional.of(spi).orElseThrow(() -> new IllegalStateException("No PropertyProvidersSingletonSpi available."))
                .mutable(null, provider);
    }


    /**
     * Intersetcs the current properties with the given {@link org.apache.tamaya.PropertyProvider} instance.
     *
     * @param providers the maps to be intersected, not null.
     * @return the builder for chaining.
     */
    public PropertyProviderBuilder intersect(PropertyProvider... providers) {
        if(providers.length==0){
            return this;
        }
        MetaInfo mi = this.metaInfo;
        if (mi == null) {
            mi = MetaInfoBuilder.of("intersect").setEnvironment(Environment.current()).build();
        } else {
            mi = MetaInfoBuilder.of(metaInfo).setEnvironment(Environment.current()).build();
        }
        return addProviders(Optional.of(spi).orElseThrow(() -> new IllegalStateException("No PropertyProvidersSingletonSpi available."))
                .intersected(mi,aggregationPolicy,  Arrays.asList(providers)));
    }


    /**
     * Subtracts with the given {@link org.apache.tamaya.PropertyProvider} instance from the current properties.
     *
     * @param providers the maps to be subtracted, not null.
     * @return the builder for chaining.
     */
    public PropertyProviderBuilder subtract(PropertyProvider... providers) {
        if(providers.length==0){
            return this;
        }
        MetaInfo mi = this.metaInfo;
        if (mi == null) {
            mi = MetaInfoBuilder.of("subtract").setEnvironment(Environment.current()).build();
        } else {
            mi = MetaInfoBuilder.of(metaInfo).setEnvironment(Environment.current()).build();
        }
        current = Optional.of(spi).orElseThrow(() -> new IllegalStateException("No PropertyProvidersSingletonSpi available."))
                .subtracted(mi, current, Arrays.asList(providers));
        return this;
    }


    /**
     * Filters the current properties based on the given predicate..
     *
     * @param filter the filter to be applied, not null.
     * @return the new filtering instance.
     */
    public PropertyProviderBuilder filter(Predicate<String> filter) {
        MetaInfo mi = this.metaInfo;
        if (mi == null) {
            mi = MetaInfoBuilder.of("filtered").setEnvironment(Environment.current()).set("filter", filter.toString()).build();
        } else {
            mi = MetaInfoBuilder.of(metaInfo).set("filter", filter.toString()).setEnvironment(Environment.current()).build();
        }
        current = Optional.of(spi).orElseThrow(() -> new IllegalStateException("No PropertyProvidersSingletonSpi available."))
                .filtered(mi, filter, current);
        addProviderChainInfo("filter->" + filter.toString());
        this.metaInfo = null;
        return this;
    }

    /**
     * Creates a new contextual {@link org.apache.tamaya.PropertyProvider}. Contextual maps delegate to different instances current PropertyMap depending
     * on the keys returned fromMap the isolationP
     *
     * @param mapSupplier          the supplier creating new provider instances
     * @param isolationKeySupplier the supplier providing contextual keys based on the current environment.
     */
    public PropertyProviderBuilder addContextual(Supplier<PropertyProvider> mapSupplier,
                                                 Supplier<String> isolationKeySupplier) {
        MetaInfo mi = this.metaInfo;
        if (mi == null) {
            mi = MetaInfoBuilder.of("contextual").setEnvironment(Environment.current()).set("mapSupplier", mapSupplier.toString()).set("isolationKeySupplier", isolationKeySupplier.toString()).build();
        } else {
            mi = MetaInfoBuilder.of(metaInfo).set("mapSupplier", mapSupplier.toString()).set("isolationKeySupplier", isolationKeySupplier.toString()).setEnvironment(Environment.current()).build();
        }
        return addProviders(Optional.of(spi).orElseThrow(() -> new IllegalStateException("No PropertyProvidersSingletonSpi available."))
                .contextual(mi, mapSupplier, isolationKeySupplier));
    }

    /**
     * Replaces all keys in the current provider by the given map.
     *
     * @param replacementMap the map instance, that will replace all corresponding entries in {@code mainMap}, not null.
     * @return the new delegating instance.
     */
    public PropertyProviderBuilder replace(Map<String, String> replacementMap) {
        MetaInfo mi = this.metaInfo;
        if (mi == null) {
            mi = MetaInfoBuilder.of("replace").setEnvironment(Environment.current()).build();
        } else {
            mi = MetaInfoBuilder.of(metaInfo).setEnvironment(Environment.current()).build();
        }
        current = Optional.of(spi).orElseThrow(() -> new IllegalStateException("No PropertyProvidersSingletonSpi available."))
                .replacing(mi, current, replacementMap);
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
    public PropertyProviderBuilder setMeta(String key, String value){
        this.metaInfoBuilder.set(key, value);
        return this;
    }

    /**
     * Build a new property provider based on the input.
     * @return a new property provider, or null.
     */
    public PropertyProvider build() {
        if (current != null) {
            return Optional.of(spi).orElseThrow(() -> new IllegalStateException("No PropertyProvidersSingletonSpi available."))
                .build(metaInfoBuilder.build(), current);
        }
        return Optional.of(spi).orElseThrow(() -> new IllegalStateException("No PropertyProvidersSingletonSpi available."))
                .empty(metaInfoBuilder.build());
    }

    /**
     * Creates a {@link PropertyProvider} instance that is serializable and immutable,
     * so it can be sent over a network connection.
     *
     * @return the freezed instance, never null.
     */
    public PropertyProvider buildFreezed() {
        MetaInfo mi = this.metaInfo;
        if (mi == null) {
            mi = MetaInfoBuilder.of("freezed").set("freezed", "true").setEnvironment(Environment.current()).build();
        } else {
            mi = MetaInfoBuilder.of(metaInfo).set("freezed", "true").setEnvironment(Environment.current()).build();
        }
        PropertyProvider prov = Optional.of(spi).orElseThrow(() -> new IllegalStateException("No PropertyProvidersSingletonSpi available."))
                .freezed(mi, current);
        this.metaInfo = null;
        return prov;
    }

}
