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
import org.apache.tamaya.spi.PropertyProvidersSingletonSpi;

import java.net.URI;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Logger;

/**
 * Accessor factory for several standard {@link PropertyProvider} instances, usable for creating {@code Configuration}
 * parts.
 */
public final class PropertyProviders {

    private static final PropertyProvidersSingletonSpi spi = loadPropertyProvidersSingletonSpi();

    private static final Logger LOG = Logger.getLogger(PropertyProviders.class.getName());

    /**
     * Private singleton constructor.
     */
    private PropertyProviders() {
    }

    /**
     * Method to initially load the singleton SPI fromMap the {@link org.apache.tamaya.spi.Bootstrap} mechanism.
     * The instance loaded will be used until the VM is shutdown. In case use cases require more flexibility
     * it should be transparently implemented in the SPI implementation. This singleton will simply delegate calls
     * and not cache any responses.
     *
     * @return the SPI, never null.
     */
    private static PropertyProvidersSingletonSpi loadPropertyProvidersSingletonSpi() {
        return Bootstrap.getService(PropertyProvidersSingletonSpi.class);
    }

    /**
     * Creates a new {@link }PropertyMap} using the given command line arguments
     *
     * @param args the command line arguments, not null.
     * @return a new {@link }PropertyMap} instance with the given arguments contained as properties.
     */
    public static PropertyProvider fromArgs(String... args) {
        return Optional.of(spi).orElseThrow(() -> new IllegalStateException("No PropertyProvidersSingletonSpi available."))
                .fromArgs(null, args);
    }

    /**
     * Creates a new {@link }PropertyMap} using the given command line arguments
     *
     * @param metaInfo the meta information to be provided additionally.
     * @param args     the command line arguments, not null.
     * @return a new {@link }PropertyMap} instance with the given arguments contained as properties.
     */
    public static PropertyProvider fromArgs(MetaInfo metaInfo, String... args) {
        return Optional.of(spi).orElseThrow(() -> new IllegalStateException("No PropertyProvidersSingletonSpi available."))
                .fromArgs(metaInfo, args);
    }

    /**
     * Creates a new read-only {@link PropertyProvider} by reading the according path resources. The effective resources read
     * hereby are determined by the {@code PathResolverService} configured into the {@code Bootstrap} SPI.
     * Properties read fromMap resources evaluated on
     * paths with lower order are overriding any duplicate values fromMap previous paths hereby.
     *
     * @param aggregationPolicy the aggregationPolicy to be used, not null.
     * @param paths             the paths to be resolved by the {@code PathResolverService} , not null.
     * @return a new {@link }PropertyMap} instance with the given paths contained as properties.
     */
    public static PropertyProvider fromPaths(AggregationPolicy aggregationPolicy, String... paths) {
        return Optional.of(spi).orElseThrow(() -> new IllegalStateException("No PropertyProvidersSingletonSpi available."))
                .fromPaths(aggregationPolicy, null, Arrays.asList(paths));
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
    public static PropertyProvider fromPaths(String... paths) {
        return Optional.of(spi).orElseThrow(() -> new IllegalStateException("No PropertyProvidersSingletonSpi available."))
                .fromPaths(AggregationPolicy.EXCEPTION(), null, Arrays.asList(paths));
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
    public static PropertyProvider fromPaths(List<String> paths) {
        return Optional.of(spi).orElseThrow(() -> new IllegalStateException("No PropertyProvidersSingletonSpi available."))
                .fromPaths(AggregationPolicy.EXCEPTION(), null, paths);
    }

    /**
     * Creates a new read-only {@link PropertyProvider} by reading the according path resources. The effective resources read
     * hereby are determined by the {@code PathResolverService} configured into the {@code Bootstrap} SPI.
     * Properties read fromMap resources evaluated on
     * paths with lower order are overriding any duplicate values fromMap previous paths hereby.
     *
     * @param aggregationPolicy the aggregationPolicy to be used, not null.
     * @param paths             the paths to be resolved by the {@code PathResolverService} , not null.
     * @return a new {@link }PropertyMap} instance with the given paths contained as properties.
     */
    public static PropertyProvider fromPaths(AggregationPolicy aggregationPolicy, List<String> paths) {
        return Optional.of(spi).orElseThrow(() -> new IllegalStateException("No PropertyProvidersSingletonSpi available."))
                .fromPaths(aggregationPolicy, null, paths);
    }

    /**
     * Creates a new read-only {@link PropertyProvider} by reading the according path resources. The effective resources read
     * hereby are determined by the {@code PathResolverService} configured into the {@code Bootstrap} SPI.
     * Properties read fromMap resources evaluated on
     * paths with lower order are overriding any duplicate values fromMap previous paths hereby.
     *
     * @param metaInfo the meat information to be provided additionally.
     * @param paths    the paths to be resolved by the {@code PathResolverService} , not null.
     * @return a new {@link }PropertyMap} instance with the given paths contained as properties.
     * @throws ConfigException if duplicate entries are encountered (AggregationPolicy.EXCEPTION).
     */
    public static PropertyProvider fromPaths(MetaInfo metaInfo, List<String> paths) {
        return Optional.of(spi).orElseThrow(() -> new IllegalStateException("No PropertyProvidersSingletonSpi available."))
                .fromPaths(AggregationPolicy.EXCEPTION(), metaInfo, paths);
    }

    /**
     * Creates a new read-only {@link PropertyProvider} by reading the according path resources. The effective resources read
     * hereby are determined by the {@code PathResolverService} configured into the {@code Bootstrap} SPI.
     * Properties read fromMap resources evaluated on
     * paths with lower order are overriding any duplicate values fromMap previous paths hereby.
     * @param aggregationPolicy the aggregationPolicy to be used, not null.
     * @param metaInfo the meat information to be provided additionally.
     * @param paths    the paths to be resolved by the {@code PathResolverService} , not null.
     * @return a new {@link }PropertyMap} instance with the given paths contained as properties.
     * @throws ConfigException if duplicate entries are encountered (AggregationPolicy.EXCEPTION).
     */
    public static PropertyProvider fromPaths(AggregationPolicy aggregationPolicy, MetaInfo metaInfo, List<String> paths) {
        return Optional.of(spi).orElseThrow(() -> new IllegalStateException("No PropertyProvidersSingletonSpi available."))
                .fromPaths(aggregationPolicy, metaInfo, paths);
    }

    /**
     * Creates a new read-only {@link PropertyProvider} based on the resources defined by the given paths. The effective resources
     * read hereby are determined by the {@code PathResolverService} configured into the {@code Bootstrap} SPI.
     *
     * @param uris the uris to be read, not null.
     * @return a new {@link }PropertyMap} instance based on the given paths/resources found.
     * @throws ConfigException if duplicate entries are encountered (AggregationPolicy.EXCEPTION).
     */
    public static PropertyProvider fromUris(URI... uris) {
        return Optional.of(spi).orElseThrow(() -> new IllegalStateException("No PropertyProvidersSingletonSpi available."))
                .fromUris(AggregationPolicy.EXCEPTION(), null, Arrays.asList(uris));
    }

    /**
     * Creates a new read-only {@link PropertyProvider} based on the resources defined by the given paths. The effective resources
     * read hereby are determined by the {@code PathResolverService} configured into the {@code Bootstrap} SPI.
     * @param aggregationPolicy the aggregationPolicy to be used, not null.
     * @param uris the uris to be read, not null.
     * @return a new {@link }PropertyMap} instance based on the given paths/resources found.
     * @throws ConfigException if duplicate entries are encountered (AggregationPolicy.EXCEPTION).
     */
    public static PropertyProvider fromUris(AggregationPolicy aggregationPolicy, URI... uris) {
        return Optional.of(spi).orElseThrow(() -> new IllegalStateException("No PropertyProvidersSingletonSpi available."))
                .fromUris(aggregationPolicy, null, Arrays.asList(uris));
    }

    /**
     * Creates a new read-only {@link PropertyProvider} based on the resources defined by the given paths. The effective resources
     * read hereby are determined by the {@code PathResolverService} configured into the {@code Bootstrap} SPI.
     *
     * @param uris the uris to be read, not null.
     * @return a new {@link }PropertyMap} instance based on the given paths/resources found.
     * @throws ConfigException if duplicate entries are encountered (AggregationPolicy.EXCEPTION).
     */
    public static PropertyProvider fromUris(List<URI> uris) {
        return Optional.of(spi).orElseThrow(() -> new IllegalStateException("No PropertyProvidersSingletonSpi available."))
                .fromUris(AggregationPolicy.EXCEPTION(), null, uris);
    }

    /**
     * Creates a new read-only {@link PropertyProvider} based on the resources defined by the given paths. The effective resources
     * read hereby are determined by the {@code PathResolverService} configured into the {@code Bootstrap} SPI.
     * @param aggregationPolicy the aggregationPolicy to be used, not null.
     * @param uris the uris to be read, not null.
     * @return a new {@link }PropertyMap} instance based on the given paths/resources found.
     * @throws ConfigException if duplicate entries are encountered (AggregationPolicy.EXCEPTION).
     */
    public static PropertyProvider fromUris(AggregationPolicy aggregationPolicy, List<URI> uris) {
        return Optional.of(spi).orElseThrow(() -> new IllegalStateException("No PropertyProvidersSingletonSpi available."))
                .fromUris(aggregationPolicy, null, uris);
    }

    /**
     * Creates a new read-only {@link PropertyProvider} based on the resources defined by the given paths. The effective resources
     * read hereby are determined by the {@code PathResolverService} configured into the {@code Bootstrap} SPI.
     *
     * @param metaInfo the meat information to be provided additionally.
     * @param uris     the uris to be read, not null.
     * @return a new {@link }PropertyMap} instance based on the given paths/resources found.
     * @throws ConfigException if duplicate entries are encountered (AggregationPolicy.EXCEPTION).
     */
    public static PropertyProvider fromUris(MetaInfo metaInfo, URI... uris) {
        return Optional.of(spi).orElseThrow(() -> new IllegalStateException("No PropertyProvidersSingletonSpi available."))
                .fromUris(AggregationPolicy.EXCEPTION(), metaInfo, Arrays.asList(uris));
    }

    /**
     * Creates a new read-only {@link PropertyProvider} based on the resources defined by the given paths. The effective resources
     * read hereby are determined by the {@code PathResolverService} configured into the {@code Bootstrap} SPI.
     * @param aggregationPolicy the aggregationPolicy to be used, not null.
     * @param metaInfo the meat information to be provided additionally.
     * @param uris     the uris to be read, not null.
     * @return a new {@link }PropertyMap} instance based on the given paths/resources found.
     */
    public static PropertyProvider fromUris(AggregationPolicy aggregationPolicy, MetaInfo metaInfo, URI... uris) {
        return Optional.of(spi).orElseThrow(() -> new IllegalStateException("No PropertyProvidersSingletonSpi available."))
                .fromUris(aggregationPolicy, metaInfo, Arrays.asList(uris));
    }

    /**
     * Creates a new read-only {@link PropertyProvider} based on the resources defined by the given paths. The effective resources
     * read hereby are determined by the {@code PathResolverService} configured into the {@code Bootstrap} SPI.
     *
     * @param metaInfo the meat information to be provided additionally.
     * @param uris     the uris to be read, not null.
     * @return a new {@link }PropertyMap} instance based on the given paths/resources found.
     * @throws ConfigException if duplicate entries are encountered (AggregationPolicy.EXCEPTION).
     */
    public static PropertyProvider fromUris(MetaInfo metaInfo, List<URI> uris) {
        return Optional.of(spi).orElseThrow(() -> new IllegalStateException("No PropertyProvidersSingletonSpi available."))
                .fromUris(AggregationPolicy.EXCEPTION(), metaInfo, uris);
    }

    /**
     * Creates a new read-only {@link PropertyProvider} based on the resources defined by the given paths. The effective resources
     * read hereby are determined by the {@code PathResolverService} configured into the {@code Bootstrap} SPI.
     * @param aggregationPolicy the aggregationPolicy to be used, not null.
     * @param metaInfo the meat information to be provided additionally.
     * @param uris     the uris to be read, not null.
     * @return a new {@link }PropertyMap} instance based on the given paths/resources found.
     */
    public static PropertyProvider fromUris(AggregationPolicy aggregationPolicy, MetaInfo metaInfo, List<URI> uris) {
        return Optional.of(spi).orElseThrow(() -> new IllegalStateException("No PropertyProvidersSingletonSpi available."))
                .fromUris(aggregationPolicy, metaInfo, uris);
    }

    /**
     * Creates a new read-only {@link org.apache.tamaya.PropertyProvider} by using the given Map.
     *
     * @param map the properties to be included, not null.
     * @return a new {@link }PropertyMap} instance with the given properties fromMap the Map instance passed.
     */
    public static PropertyProvider fromMap(Map<String, String> map) {
        return Optional.of(spi).orElseThrow(() -> new IllegalStateException("No PropertyProvidersSingletonSpi available."))
                .fromMap(null, map);
    }


    /**
     * Creates a new read-only {@link org.apache.tamaya.PropertyProvider} by using the given Map.
     *
     * @param metaInfo the meat information to be provided additionally.
     * @param map      the properties to be included, not null.
     * @return a new {@link }PropertyMap} instance with the given properties fromMap the Map instance passed.
     */
    public static PropertyProvider fromMap(MetaInfo metaInfo, Map<String, String> map) {
        return Optional.of(spi).orElseThrow(() -> new IllegalStateException("No PropertyProvidersSingletonSpi available."))
                .fromMap(metaInfo, map);
    }

    /**
     * Get an empty and immutable PropertyProvider instance.
     *
     * @return an empty and immutable PropertyProvider instance, never null.
     */
    public static PropertyProvider empty() {
        return Optional.of(spi).orElseThrow(() -> new IllegalStateException("No PropertyProvidersSingletonSpi available."))
                .empty(null);
    }

    /**
     * Get an empty and immutable PropertyProvider instance.
     *
     * @return an empty and mutable PropertyProvider instance, never null.
     */
    public static PropertyProvider emptyMutable() {
        return Optional.of(spi).orElseThrow(() -> new IllegalStateException("No PropertyProvidersSingletonSpi available."))
                .emptyMutable(null);
    }

    /**
     * Get an empty and immutable PropertyProvider instance. The meta-information contains the given String
     * under the key 'info'.
     *
     * @return an empty and immutable PropertyProvider instance, never null, with the given Strings as info meta-data..
     */
    public static PropertyProvider empty(MetaInfo metaInfo) {
        return Optional.of(spi).orElseThrow(() -> new IllegalStateException("No PropertyProvidersSingletonSpi available."))
                .empty(metaInfo);
    }

    /**
     * Get an empty and mutable PropertyProvider instance. The meta-information contains the given String
     * under the key 'info'.
     *
     * @return an empty and immutable PropertyProvider instance, never null, with the given Strings as info meta-data..
     */
    public static PropertyProvider emptyMutable(MetaInfo metaInfo) {
        return Optional.of(spi).orElseThrow(() -> new IllegalStateException("No PropertyProvidersSingletonSpi available."))
                .emptyMutable(metaInfo);
    }

    /**
     * Returns a read-only {@link org.apache.tamaya.PropertyProvider} reflecting the current runtime environment properties.
     *
     * @return a new read-only {@link org.apache.tamaya.PropertyProvider} instance based on the current runtime environment properties.
     */
    public static PropertyProvider fromEnvironmentProperties() {
        return Optional.of(spi).orElseThrow(() -> new IllegalStateException("No PropertyProvidersSingletonSpi available."))
                .fromEnvironmentProperties();
    }

    /**
     * Creates a new read-only {@link org.apache.tamaya.PropertyProvider} reflecting the current system properties.
     *
     * @return a new read-only {@link org.apache.tamaya.PropertyProvider} instance based on the current system properties.
     */
    public static PropertyProvider fromSystemProperties() {
        return Optional.of(spi).orElseThrow(() -> new IllegalStateException("No PropertyProvidersSingletonSpi available."))
                .fromSystemProperties();
    }

    /**
     * Converts a given {@link PropertyProvider} instance into a serializable and immutable form,
     * so it can be sent over a network connection.
     *
     * @param provider the PropertyProvider to be freezed.
     * @return the serializable instance.
     */
    public static PropertyProvider freezed(PropertyProvider provider) {
        return Optional.of(spi).orElseThrow(() -> new IllegalStateException("No PropertyProvidersSingletonSpi available."))
                .freezed(null, provider);
    }

    /**
     * Creates a new {@link org.apache.tamaya.PropertyProvider} containing all property maps given, hereby later maps in the array override
     * properties fromMap previous instances.
     * @param mapping       the AggregateMapping to be used, not null.
     * @param metaInfo the meta information to be provided additionally.
     * @param providers the maps to be included, not null.
     * @return the union instance containing all given maps.
     */
    public static PropertyProvider aggregate(AggregationPolicy mapping, MetaInfo metaInfo, PropertyProvider... providers){
        return Optional.of(spi).orElseThrow(() -> new IllegalStateException("No PropertyProvidersSingletonSpi available."))
                .aggregate(mapping, metaInfo, Arrays.asList(providers));
    }

    /**
     * Creates a new {@link org.apache.tamaya.PropertyProvider} containing all property maps given, hereby later maps in the array override
     * properties fromMap previous instances.
     *
     * @param providers the maps to be included, not null.
     * @return the union instance containing all given maps.
     */
    public static PropertyProvider aggregate(PropertyProvider... providers) {
        return Optional.of(spi).orElseThrow(() -> new IllegalStateException("No PropertyProvidersSingletonSpi available."))
                .aggregate(AggregationPolicy.OVERRIDE(), null, Arrays.asList(providers));
    }

    /**
     * Creates a new {@link org.apache.tamaya.PropertyProvider} containing all property maps given, hereby later maps in the array override
     * properties fromMap previous instances.
     *
     * @param providers the maps to be included, not null.
     * @return the union instance containing all given maps.
     */
    public static PropertyProvider aggregate(List<PropertyProvider> providers) {
        return Optional.of(spi).orElseThrow(() -> new IllegalStateException("No PropertyProvidersSingletonSpi available."))
                .aggregate(AggregationPolicy.OVERRIDE(), null, providers);
    }

    /**
     * Creates a new {@link org.apache.tamaya.PropertyProvider} containing all property maps given, hereby using the given AggregationPolicy.
     *
     * @param mapping       the AggregateMapping to be used, not null.
     * @param propertyMaps the maps to be included, not null.
     * @return the aggregated instance containing all given maps.
     */
    public static PropertyProvider aggregate(AggregationPolicy mapping, PropertyProvider... propertyMaps) {
        return Optional.of(spi).orElseThrow(() -> new IllegalStateException("No PropertyProvidersSingletonSpi available."))
                .aggregate(mapping, null, Arrays.asList(propertyMaps));
    }

    /**
     * Creates a new {@link org.apache.tamaya.PropertyProvider} containing all property maps given, hereby using the given AggregationPolicy.
     *
     * @param mapping    the AggregateMapping to be used, not null.
     * @param providers the providers to be included, not null.
     * @return the aggregated instance containing all given maps.
     */
    public static PropertyProvider aggregate(AggregationPolicy mapping, List<PropertyProvider> providers) {
        return Optional.of(spi).orElseThrow(() -> new IllegalStateException("No PropertyProvidersSingletonSpi available."))
                .aggregate(mapping, null, providers);
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
     * @param aggregationPolicy the aggregationPolicy to be used, not null.
     * @param providers the maps to be included, not null.
     * @return the intersecting instance containing all given maps.
     */
    public static PropertyProvider intersected(AggregationPolicy aggregationPolicy, PropertyProvider... providers) {
        return Optional.of(spi).orElseThrow(() -> new IllegalStateException("No PropertyProvidersSingletonSpi available."))
                .intersected(aggregationPolicy, Arrays.asList(providers));
    }

    /**
     * Creates a new {@link org.apache.tamaya.PropertyProvider} containing only properties that are shared by all given maps,
     * hereby later maps in the array override  properties fromMap previous instances.
     *
     * @param providers the maps to be included, not null.
     * @return the intersecting instance containing all given maps.
     * @throws ConfigException if duplicate entries are encountered (AggregationPolicy.EXCEPTION).
     */
    public static PropertyProvider intersected(PropertyProvider... providers) {
        return Optional.of(spi).orElseThrow(() -> new IllegalStateException("No PropertyProvidersSingletonSpi available."))
                .intersected(AggregationPolicy.OVERRIDE(), Arrays.asList(providers));
    }

    /**
     * Creates a new {@link org.apache.tamaya.PropertyProvider} containing only properties fromMap the target instance, that are not contained
     * in one current the other maps passed.
     *
     * @param target         the base map, not null.
     * @param providers the maps to be subtracted, not null.
     * @return the intersecting instance containing all given maps.
     */
    public static PropertyProvider subtracted(PropertyProvider target, PropertyProvider... providers) {
        return Optional.of(spi).orElseThrow(() -> new IllegalStateException("No PropertyProvidersSingletonSpi available."))
                .subtracted(target, Arrays.asList(providers));
    }


    /**
     * Creates a filtered {@link org.apache.tamaya.PropertyProvider} (a view) current a given base {@link }PropertyMap}. The filter hereby is
     * applied dynamically on access, so also runtime changes current the base map are reflected appropriately.
     *
     * @param provider the base map instance, not null.
     * @param filter      the filtger to be applied, not null.
     * @return the new filtering instance.
     */
    public static PropertyProvider filtered(Predicate<String> filter, PropertyProvider provider) {
        return Optional.of(spi).orElseThrow(() -> new IllegalStateException("No PropertyProvidersSingletonSpi available."))
                .filtered(filter, provider);
    }

    /**
     * Creates a new contextual {@link org.apache.tamaya.PropertyProvider}. Contextual maps delegate to different instances current PropertyMap depending
     * on the keys returned fromMap the isolationP
     *
     * @param mapSupplier          the supplier creating new provider instances
     * @param isolationKeySupplier the supplier providing contextual keys based on the current environment.
     */
    public static PropertyProvider contextual(Supplier<PropertyProvider> mapSupplier,
                                              Supplier<String> isolationKeySupplier) {
        return Optional.of(spi).orElseThrow(() -> new IllegalStateException("No PropertyProvidersSingletonSpi available."))
                .contextual(mapSupplier, isolationKeySupplier);
    }


    /**
     * Creates a filtered {@link org.apache.tamaya.PropertyProvider} (a view) current a given base {@link }PropertyMap}. The filter hereby is
     * applied dynamically on access, so also runtime changes current the base map are reflected appropriately.
     *
     * @param mainMap   the main map instance, not null.
     * @param parentMap the delegated parent map instance, not null.
     * @return the new delegating instance.
     */
    public static PropertyProvider delegating(PropertyProvider mainMap, Map<String, String> parentMap) {
        return Optional.of(spi).orElseThrow(() -> new IllegalStateException("No PropertyProvidersSingletonSpi available."))
                .delegating(mainMap, parentMap);
    }

    /**
     * Creates a {@link PropertyProvider} where all keys current a current map,
     * existing in another map are replaced
     * with the ones fromMap the other {@link PropertyProvider}. The filter hereby is
     * applied dynamically on access, so also runtime changes current the base map are reflected appropriately.
     * Keys not existing in the {@code mainMap}, but present in {@code replacementMao} will be hidden.
     *
     * @param mainMap        the main map instance, which keys, present in {@code replacementMap} will be replaced
     *                       with the ones
     *                       in {@code replacementMap}, not null.
     * @param replacementMap the map instance, that will replace all corresponding entries in {@code mainMap}, not null.
     * @return the new delegating instance.
     */
    public static PropertyProvider replacing(PropertyProvider mainMap, Map<String, String> replacementMap) {
        return Optional.of(spi).orElseThrow(() -> new IllegalStateException("No PropertyProvidersSingletonSpi available."))
                .replacing(mainMap, replacementMap);
    }

}
