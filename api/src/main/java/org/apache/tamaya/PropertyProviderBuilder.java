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

import java.net.URI;
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
     * The current meta info, or null, if a default should be generated.
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
        this.metaInfo = Objects.requireNonNull(metaInfo);
    }

    /**
     * Private singleton constructor.
     */
    private PropertyProviderBuilder(PropertyProvider provider) {
        this.metaInfo = Objects.requireNonNull(provider).getMetaInfo();
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
        return create(MetaInfo.of(name));
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

    public PropertyProviderBuilder addProviders(PropertyProvider... providers) {
        List<PropertyProvider> allProviders = Arrays.asList(providers);
        if (this.current != null) {
            allProviders.add(0, this.current);
        }
        MetaInfo mi = this.metaInfo;
        if (mi == null) {
            mi = MetaInfoBuilder.of("aggregate").setEnvironment(Environment.current()).build();
        }
        this.current = Optional.of(spi).orElseThrow(() -> new IllegalStateException("No PropertyProvidersSingletonSpi available."))
                .aggregate(this.aggregationPolicy, mi, allProviders);
        this.metaInfo = null;
        return this;
    }

    /**
     * Creates a new {@link }PropertyMap} using the given command line arguments
     *
     * @param args the command line arguments, not null.
     * @return a new {@link }PropertyMap} instance with the given arguments contained as properties.
     */
    public PropertyProviderBuilder addArgs(String... args) {
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
     * Properties read fromMap resources evaluated on
     * paths with lower order are overriding any duplicate values fromMap previous paths hereby.
     *
     * @param paths the paths to be resolved by the {@code PathResolverService} , not null.
     * @return a new {@link }PropertyMap} instance with the given paths contained as properties.
     */
    public PropertyProviderBuilder addPaths(String... paths) {
        MetaInfo mi = this.metaInfo;
        if (mi == null) {
            mi = MetaInfoBuilder.of("aggregate").setEnvironment(Environment.current()).build();
        } else {
            mi = MetaInfoBuilder.of(metaInfo).setEnvironment(Environment.current()).set("paths", Arrays.toString(paths)).build();
        }
        return addProviders(Optional.of(spi).orElseThrow(() -> new IllegalStateException("No PropertyProvidersSingletonSpi available."))
                .fromPaths(aggregationPolicy, mi, Arrays.asList(paths)));
    }


    /**
     * Creates a new read-only {@link PropertyProvider} by reading the according path resources. The effective resources read
     * hereby are determined by the {@code PathResolverService} configured into the {@code Bootstrap} SPI.
     * Properties read fromMap resources evaluated on
     * paths with lower order are overriding any duplicate values fromMap previous paths hereby.
     *
     * @param paths the paths to be resolved by the {@code PathResolverService} , not null.
     * @return a new {@link }PropertyMap} instance with the given paths contained as properties.
     * @throws ConfigException if duplicate entries are encountered (AggregationPolicy.EXCEPTION).
     */
    public PropertyProviderBuilder addPaths(List<String> paths) {
        MetaInfo mi = this.metaInfo;
        if (mi == null) {
            mi = MetaInfoBuilder.of("aggregate").set("paths", paths.toString()).setEnvironment(Environment.current()).build();
        } else {
            mi = MetaInfoBuilder.of(metaInfo).setEnvironment(Environment.current()).set("paths", paths.toString()).build();
        }
        return addProviders(Optional.of(spi).orElseThrow(() -> new IllegalStateException("No PropertyProvidersSingletonSpi available."))
                .fromPaths(aggregationPolicy, metaInfo, paths));
    }

    /**
     * Creates a new read-only {@link PropertyProvider} based on the resources defined by the given paths. The effective resources
     * read hereby are determined by the {@code PathResolverService} configured into the {@code Bootstrap} SPI.
     *
     * @param uris the uris to be read, not null.
     * @return a new {@link }PropertyMap} instance based on the given paths/resources found.
     * @throws ConfigException if duplicate entries are encountered (AggregationPolicy.EXCEPTION).
     */
    public PropertyProviderBuilder addUris(URI... uris) {
        MetaInfo mi = this.metaInfo;
        if (mi == null) {
            mi = MetaInfoBuilder.of("aggregate").set("uris", Arrays.toString(uris)).setEnvironment(Environment.current()).build();
        } else {
            mi = MetaInfoBuilder.of(metaInfo).setEnvironment(Environment.current()).set("paths", uris.toString()).build();
        }
        return addProviders(Optional.of(spi).orElseThrow(() -> new IllegalStateException("No PropertyProvidersSingletonSpi available."))
                .fromUris(this.aggregationPolicy, mi, Arrays.asList(uris)));
    }


    /**
     * Creates a new read-only {@link org.apache.tamaya.PropertyProvider} by using the given Map.
     *
     * @param map the properties to be included, not null.
     * @return a new {@link }PropertyMap} instance with the given properties fromMap the Map instance passed.
     */
    public PropertyProviderBuilder addMap(Map<String, String> map) {
        MetaInfo mi = this.metaInfo;
        if (mi == null) {
            mi = MetaInfoBuilder.of("map").setEnvironment(Environment.current()).build();
        } else {
            mi = MetaInfoBuilder.of(metaInfo).setEnvironment(Environment.current()).build();
        }
        return addProviders(Optional.of(spi).orElseThrow(() -> new IllegalStateException("No PropertyProvidersSingletonSpi available."))
                .fromMap(metaInfo, map));
    }


    /**
     * Returns a read-only {@link org.apache.tamaya.PropertyProvider} reflecting the current runtime environment properties.
     *
     * @return a new read-only {@link org.apache.tamaya.PropertyProvider} instance based on the current runtime environment properties.
     */
    public PropertyProviderBuilder addEnvironmentProperties() {
        MetaInfo mi = this.metaInfo;
        if (mi == null) {
            mi = MetaInfoBuilder.of("with env-props").setEnvironment(Environment.current()).build();
        } else {
            mi = MetaInfoBuilder.of(metaInfo).setEnvironment(Environment.current()).build();
        }
        return addProviders(Optional.of(spi).orElseThrow(() -> new IllegalStateException("No PropertyProvidersSingletonSpi available."))
                .fromEnvironmentProperties());
    }

    /**
     * Creates a new read-only {@link org.apache.tamaya.PropertyProvider} reflecting the current system properties.
     *
     * @return a new read-only {@link org.apache.tamaya.PropertyProvider} instance based on the current system properties.
     */
    public PropertyProviderBuilder addSystemProperties() {
        MetaInfo mi = this.metaInfo;
        if (mi == null) {
            mi = MetaInfoBuilder.of("with sys-props").setEnvironment(Environment.current()).build();
        } else {
            mi = MetaInfoBuilder.of(metaInfo).setEnvironment(Environment.current()).build();
        }
        return addProviders(Optional.of(spi).orElseThrow(() -> new IllegalStateException("No PropertyProvidersSingletonSpi available."))
                .fromSystemProperties());
    }

    /**
     * Creates a new {@link org.apache.tamaya.PropertyProvider} containing all property maps given, hereby later maps in the array override
     * properties fromMap previous instances.
     *
     * @param providers the maps to be included, not null.
     * @return the union instance containing all given maps.
     */
    public PropertyProviderBuilder aggregate(PropertyProvider... providers) {
        MetaInfo mi = this.metaInfo;
        if (mi == null) {
            mi = MetaInfoBuilder.of("aggregate").setEnvironment(Environment.current()).build();
        } else {
            mi = MetaInfoBuilder.of(metaInfo).setEnvironment(Environment.current()).build();
        }
        return addProviders(Optional.of(spi).orElseThrow(() -> new IllegalStateException("No PropertyProvidersSingletonSpi available."))
                .aggregate(aggregationPolicy, mi, Arrays.asList(providers)));
    }


    /**
     * Creates a new {@link org.apache.tamaya.PropertyProvider} containing all property maps given, hereby later maps in the array override
     * properties fromMap previous instances.
     *
     * @param providers the maps to be included, not null.
     * @return the union instance containing all given maps.
     */
    public PropertyProviderBuilder aggregate(List<PropertyProvider> providers) {
        MetaInfo mi = this.metaInfo;
        if (mi == null) {
            mi = MetaInfoBuilder.of("aggregate").setEnvironment(Environment.current()).build();
        } else {
            mi = MetaInfoBuilder.of(metaInfo).setEnvironment(Environment.current()).build();
        }
        return addProviders(Optional.of(spi).orElseThrow(() -> new IllegalStateException("No PropertyProvidersSingletonSpi available."))
                .aggregate(aggregationPolicy, metaInfo, providers));
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
     * Creates a new {@link org.apache.tamaya.PropertyProvider} containing only properties that are shared by all given maps,
     * hereby later maps in the array override  properties fromMap previous instances.
     *
     * @param providers the maps to be included, not null.
     * @return the intersecting instance containing all given maps.
     */
    public PropertyProviderBuilder intersect(PropertyProvider... providers) {
        MetaInfo mi = this.metaInfo;
        if (mi == null) {
            mi = MetaInfoBuilder.of("intersect").setEnvironment(Environment.current()).build();
        } else {
            mi = MetaInfoBuilder.of(metaInfo).setEnvironment(Environment.current()).build();
        }
        return addProviders(Optional.of(spi).orElseThrow(() -> new IllegalStateException("No PropertyProvidersSingletonSpi available."))
                .intersected(aggregationPolicy, mi, Arrays.asList(providers)));
    }


    /**
     * Creates a new {@link org.apache.tamaya.PropertyProvider} containing only properties fromMap the target instance, that are not contained
     * in one current the other maps passed.
     *
     * @param providers the maps to be subtracted, not null.
     * @return the intersecting instance containing all given maps.
     */
    public PropertyProviderBuilder subtract(PropertyProvider... providers) {
        MetaInfo mi = this.metaInfo;
        if (mi == null) {
            mi = MetaInfoBuilder.of("subtract").setEnvironment(Environment.current()).build();
        } else {
            mi = MetaInfoBuilder.of(metaInfo).setEnvironment(Environment.current()).build();
        }
        current = Optional.of(spi).orElseThrow(() -> new IllegalStateException("No PropertyProvidersSingletonSpi available."))
                .subtracted(current, mi, Arrays.asList(providers));
        return this;
    }


    /**
     * Creates a filtered {@link org.apache.tamaya.PropertyProvider} (a view) current a given base {@link }PropertyMap}. The filter hereby is
     * applied dynamically on access, so also runtime changes current the base map are reflected appropriately.
     *
     * @param filter the filtger to be applied, not null.
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
                .filtered(filter, mi, current);
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
                .contextual(mapSupplier, mi, isolationKeySupplier));
    }

    /**
     * Creates a filtered {@link org.apache.tamaya.PropertyProvider} (a view) current a given base {@link }PropertyMap}. The filter hereby is
     * applied dynamically on access, so also runtime changes current the base map are reflected appropriately.
     *
     * @param delegates the delegated parent map instance, not null.
     * @return the new delegating instance.
     */
    public PropertyProviderBuilder addDefaults(Map<String, String> delegates) {
        MetaInfo mi = this.metaInfo;
        if (mi == null) {
            mi = MetaInfoBuilder.of("filtered").setEnvironment(Environment.current()).set("delegates", delegates.toString()).build();
        } else {
            mi = MetaInfoBuilder.of(metaInfo).set("delegates", delegates.toString()).setEnvironment(Environment.current()).build();
        }
        current = Optional.of(spi).orElseThrow(() -> new IllegalStateException("No PropertyProvidersSingletonSpi available."))
                .delegating(current, mi, delegates);
        return this;
    }

    /**
     * Creates a {@link PropertyProvider} where all keys current a current map,
     * existing in another map are replaced
     * with the ones fromMap the other {@link PropertyProvider}. The filter hereby is
     * applied dynamically on access, so also runtime changes current the base map are reflected appropriately.
     * Keys not existing in the {@code mainMap}, but present in {@code replacementMao} will be hidden.
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
                .replacing(current, mi, replacementMap);
        return this;
    }

    public PropertyProvider build() {
        if (current != null) {
            return current;
        }
        return Optional.of(spi).orElseThrow(() -> new IllegalStateException("No PropertyProvidersSingletonSpi available."))
                .empty(metaInfo);
    }

    /**
     * Creates a {@link PropertyProvider} instance that is serializable and immutable,
     * so it can be sent over a network connection.
     *
     * @return the freezed instance, never null.
     */
    public PropertyProvider freeze() {
        MetaInfo mi = this.metaInfo;
        if (mi == null) {
            mi = MetaInfoBuilder.of("freezed").set("freezed", "true").setEnvironment(Environment.current()).build();
        } else {
            mi = MetaInfoBuilder.of(metaInfo).set("freezed", "true").setEnvironment(Environment.current()).build();
        }
        return Optional.of(spi).orElseThrow(() -> new IllegalStateException("No PropertyProvidersSingletonSpi available."))
                .freezed(mi, current);
    }

}
