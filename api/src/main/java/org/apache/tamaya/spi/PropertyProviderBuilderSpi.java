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
package org.apache.tamaya.spi;

import org.apache.tamaya.AggregationPolicy;
import org.apache.tamaya.MetaInfo;
import org.apache.tamaya.PropertyProvider;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Singleton backing bean for providing the functionality for {@link org.apache.tamaya.PropertyProviderBuilder}.
 */
public interface PropertyProviderBuilderSpi {

    /**
     * Creates a new read-only {@link org.apache.tamaya.PropertyProvider} by using the given Map.
     *
     * @param metaInfo the meat information to be provided additionally.
     * @param map      the properties to be included, not null.
     * @return a new {@link }PropertyMap} instance with the given properties fromMap the Map instance passed.
     */
    PropertyProvider fromMap(MetaInfo metaInfo, Map<String, String> map);

    /**
     * Creates a new {@link }PropertyMap} using the given command line arguments
     *
     * @param metaInfo the meta information to be provided additionally.
     * @param args     the command line arguments, not null.
     * @return a new {@link }PropertyMap} instance with the given arguments contained as properties.
     */
    PropertyProvider fromArgs(MetaInfo metaInfo, String... args);

    /**
     * Creates a new read-only {@link PropertyProvider} by reading the according path resources. The effective resources read
     * hereby are determined by the {@code PathResolverService} configured into the {@code Bootstrap} SPI.
     * Properties read fromMap resources evaluated on
     * paths with lower order are overriding any duplicate values fromMap previous paths hereby.
     *
     * @param metaInfo the meat information to be provided additionally.
     * @param paths    the paths to be resolved by the {@code PathResolverService} , not null.
     * @param aggregationPolicy the {@link org.apache.tamaya.AggregationPolicy} to be used to resolve conflicts.
     * @return a new {@link }PropertyMap} instance with the given paths contained as properties.
     */
    PropertyProvider fromPaths(MetaInfo metaInfo, AggregationPolicy aggregationPolicy, List<String> paths);


    /**
     * Creates a new read-only {@link PropertyProvider} based on the resources defined by the given paths. The effective resources
     * read hereby are determined by the {@code PathResolverService} configured into the {@code Bootstrap} SPI.
     *
     * @param metaInfo the meat information to be provided additionally.
     * @param urls     the urls to be read, not null.
     * @return a new {@link }PropertyMap} instance based on the given paths/resources found.
     */
    PropertyProvider fromURLs(MetaInfo metaInfo, AggregationPolicy aggregationPolicy, List<URL> urls);

    /**
     * Get an empty and immutable PropertyProvider instance. The meta-information contains the given String
     * under the key 'info'.
     *
     * @return an empty and immutable PropertyProvider instance, never null, with the given Strings as info meta-data..
     */
    PropertyProvider empty(MetaInfo metaInfo);

    /**
     * Returns a read-only {@link org.apache.tamaya.PropertyProvider} reflecting the current runtime environment properties.
     *
     * @return a new read-only {@link org.apache.tamaya.PropertyProvider} instance based on the current runtime environment properties.
     */
    PropertyProvider fromEnvironmentProperties();

    /**
     * Creates a new read-only {@link org.apache.tamaya.PropertyProvider} reflecting the current system properties.
     *
     * @return a new read-only {@link org.apache.tamaya.PropertyProvider} instance based on the current system properties.
     */
    PropertyProvider fromSystemProperties();

    /**
     * Converts a given {@link PropertyProvider} instance into a serializable and immutable form,
     * so it can be sent over a network connection.
     *
     * @param provider the PropertyProvider to be freezed.
     * @return the serializable instance.
     */
    PropertyProvider freezed(MetaInfo metaInfo, PropertyProvider provider);

    /**
     * Creates a new {@link org.apache.tamaya.PropertyProvider} containing all property maps given, hereby using the given AggregationPolicy.
     *
     * @param policy       the annotation to be used, not null.
     * @param propertyMaps the maps to be included, not null.
     * @return the aggregated instance containing all given maps.
     */
    PropertyProvider aggregate(MetaInfo metaInfo, AggregationPolicy policy, List<PropertyProvider> propertyMaps);

    /**
     * Creates a new {@link org.apache.tamaya.PropertyProvider} that is mutable by adding a map based instance that overrides
     * values fromMap the original map.
     * @param provider the provider to be made mutable, not null.
     * @return the mutable instance.
     */
    PropertyProvider mutable(MetaInfo metaInfo, PropertyProvider provider);

    /**
     * Creates a new {@link org.apache.tamaya.PropertyProvider} containing only properties that are shared by all given maps,
     * hereby later maps in the array override  properties fromMap previous instances.
     *
     * @param metaInfo the meta information to be provided additionally.
     * @param propertyMaps the maps to be included, not null.
     * @return the intersecting instance containing all given maps.
     */
    PropertyProvider intersected(MetaInfo metaInfo, AggregationPolicy policy, List<PropertyProvider> propertyMaps);

    /**
     * Creates a new {@link org.apache.tamaya.PropertyProvider} containing only properties fromMap the target instance, that are not contained
     * in one current the other maps passed.
     *
     * @param metaInfo the meta information to be provided additionally.
     * @param target         the base map, not null.
     * @param subtrahendSets the maps to be subtracted, not null.
     * @return the intersecting instance containing all given maps.
     */
    PropertyProvider subtracted(MetaInfo metaInfo, PropertyProvider target, List<PropertyProvider> subtrahendSets);


    /**
     * Creates a filtered {@link org.apache.tamaya.PropertyProvider} (a view) current a given base {@link }PropertyMap}. The filter hereby is
     * applied dynamically on access, so also runtime changes current the base map are reflected appropriately.
     *
     * @param metaInfo the meta information to be provided additionally.
     * @param propertyMap the base map instance, not null.
     * @param filter      the filtger to be applied, not null.
     * @return the new filtering instance.
     */
    PropertyProvider filtered(MetaInfo metaInfo, Predicate<String> filter, PropertyProvider propertyMap);

    /**
     * Creates a new contextual {@link org.apache.tamaya.PropertyProvider}. Contextual maps delegate to different instances current PropertyMap depending
     * on the keys returned fromMap the isolationP
     * @param metaInfo the meta information to be provided additionally.
     * @param mapSupplier          the supplier creating new provider instances
     * @param isolationKeySupplier the supplier providing contextual keys based on the current environment.
     */
    PropertyProvider contextual(MetaInfo metaInfo, Supplier<PropertyProvider> mapSupplier,
                                Supplier<String> isolationKeySupplier);


    /**
     * Creates a filtered {@link org.apache.tamaya.PropertyProvider} (a view) current a given base {@link }PropertyMap}. The filter hereby is
     * applied dynamically on access, so also runtime changes current the base map are reflected appropriately.
     *
     * @param metaInfo the meta information to be provided additionally.
     * @param mainMap   the main map instance, not null.
     * @param parentMap the delegated parent map instance, not null.
     * @return the new delegating instance.
     */
    PropertyProvider delegating(MetaInfo metaInfo, PropertyProvider mainMap, Map<String, String> parentMap);

    /**
     * Creates a {@link PropertyProvider} where all keys current a current map,
     * existing in another map are replaced
     * with the ones fromMap the other {@link PropertyProvider}. The filter hereby is
     * applied dynamically on access, so also runtime changes current the base map are reflected appropriately.
     * Keys not existing in the {@code mainMap}, but present in {@code replacementMao} will be hidden.
     *
     * @param metaInfo the meta information to be provided additionally.
     * @param mainMap        the main map instance, which keys, present in {@code replacementMap} will be replaced
     *                       with the ones
     *                       in {@code replacementMap}, not null.
     * @param replacementMap the map instance, that will replace all corresponding entries in {@code mainMap}, not null.
     * @return the new delegating instance.
     */
    PropertyProvider replacing(MetaInfo metaInfo, PropertyProvider mainMap, Map<String, String> replacementMap);

    /**
     * Create a new PropertyProvider instance given the metaInfo and the baseProvider, masking hereby the base provider's
     * meta information.
     * @param metaInfo the meta information to be provided, not null.
     * @param baseProvider the base provider to be used.
     * @return a PropertyProvider with the given meta info, providing data from the baseProvider, never null.
     */
    PropertyProvider build(MetaInfo metaInfo, PropertyProvider baseProvider);
}
