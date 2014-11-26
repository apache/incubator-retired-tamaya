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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tamaya.MetaInfo;
import org.apache.tamaya.MetaInfoBuilder;
import org.apache.tamaya.PropertyProvider;

import java.net.URI;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Accessor factory for several standard {@link org.apache.tamaya.PropertyProvider} instances, usable for creating {@code Configuration}
 * parts.
 */
public final class PropertyProviders {

    private static final PropertyProvider EMPTY_PROPERTYPROVIDER = from(Collections.emptyMap());
    private static final PropertyProvider ENV_PROPERTYPROVIDER = new EnvironmentPropertyProvider();

    private static final Logger LOG = LogManager.getLogger(PropertyProviders.class);

    /**
     * Private singleton constructor.
     */
    private PropertyProviders() {
    }

    /**
     * Creates a new {@link }PropertyMap} using the given command line arguments
     *
     * @param args the command line arguments, not null.
     * @return a new {@link }PropertyMap} instance with the given arguments contained as properties.
     */
    public static PropertyProvider fromArgs(String... args) {
        return fromArgs(MetaInfo.of("Built from Args"), args);
    }

    /**
     * Creates a new {@link }PropertyMap} using the given command line arguments
     *
     * @param metaInfo the meta information to be provided additionally.
     * @param args     the command line arguments, not null.
     * @return a new {@link }PropertyMap} instance with the given arguments contained as properties.
     */
    public static PropertyProvider fromArgs(MetaInfo metaInfo, String... args) {
        Objects.requireNonNull(metaInfo);
        // TODO read the CLI with some better library, e.g. move parsing service to ext. service SPI
        Map<String, String> properties = new HashMap<>();
        for (int base = 0; base < args.length; base++) {
            if (args[base].startsWith("--")) {
                String argKey = args[base].substring(2);
                String value = "true"; // flag only
                if (base != args.length - 1) {
                    if (args[base + 1].startsWith("-")) {
                        base++;
                        int eqIndex = argKey.indexOf('=');
                        if (eqIndex > 0) {
                            value = argKey.substring(eqIndex + 1);
                            argKey = argKey.substring(0, eqIndex);
                        }
                    } else {
                        value = args[base + 1];
                        base += 2;
                    }
                }
                properties.put(argKey, value);
            } else if (args[base].startsWith("-")) {
                String argKey = args[base].substring(1);
                String value = "true"; // flag only
                if (base != args.length - 1) {
                    if (args[base + 1].startsWith("-")) {
                        base++;
                        int eqIndex = argKey.indexOf('=');
                        if (eqIndex > 0) {
                            value = argKey.substring(eqIndex + 1);
                            argKey = argKey.substring(0, eqIndex);
                        }
                    } else {
                        value = args[base + 1];
                        base += 2;
                    }
                }
                properties.put(argKey, value);
            }
        }
        return from(metaInfo, properties);
    }

    /**
     * Creates a new read-only {@link org.apache.tamaya.PropertyProvider} by reading the according path resources. The effective resources read
     * hereby are determined by the {@code PathResolverService} configured into the {@code Bootstrap} SPI.
     * Properties read from resources evaluated on
     * paths with lower order are overriding any duplicate values from previous paths hereby.
     *
     * @param paths the paths to be resolved by the {@code PathResolverService} , not null.
     * @return a new {@link }PropertyMap} instance with the given paths contained as properties.
     */
    public static PropertyProvider fromPaths(String... paths) {
        return fromPaths(MetaInfo.of("Built from Paths"), paths);
    }


    /**
     * Creates a new read-only {@link org.apache.tamaya.PropertyProvider} by reading the according path resources. The effective resources read
     * hereby are determined by the {@code PathResolverService} configured into the {@code Bootstrap} SPI.
     * Properties read from resources evaluated on
     * paths with lower order are overriding any duplicate values from previous paths hereby.
     *
     * @param paths the paths to be resolved by the {@code PathResolverService} , not null.
     * @return a new {@link }PropertyMap} instance with the given paths contained as properties.
     */
    public static PropertyProvider fromPaths(Collection<String> paths) {
        return fromPaths(MetaInfo.of("Built from Paths"), paths);
    }

    /**
     * Creates a new read-only {@link org.apache.tamaya.PropertyProvider} by reading the according path resources. The effective resources read
     * hereby are determined by the {@code PathResolverService} configured into the {@code Bootstrap} SPI.
     * Properties read from resources evaluated on
     * paths with lower order are overriding any duplicate values from previous paths hereby.
     *
     * @param metaInfo the meat information to be provided additionally.
     * @param paths    the paths to be resolved by the {@code PathResolverService} , not null.
     * @return a new {@link }PropertyMap} instance with the given paths contained as properties.
     */
    public static PropertyProvider fromPaths(MetaInfo metaInfo, String... paths) {
        return fromPaths(metaInfo, Arrays.asList(paths));
    }

    /**
     * Creates a new read-only {@link org.apache.tamaya.PropertyProvider} by reading the according path resources. The effective resources read
     * hereby are determined by the {@code PathResolverService} configured into the {@code Bootstrap} SPI.
     * Properties read from resources evaluated on
     * paths with lower order are overriding any duplicate values from previous paths hereby.
     *
     * @param metaInfo the meat information to be provided additionally.
     * @param paths    the paths to be resolved by the {@code PathResolverService} , not null.
     * @return a new {@link }PropertyMap} instance with the given paths contained as properties.
     */
    public static PropertyProvider fromPaths(MetaInfo metaInfo, Collection<String> paths) {
        return new PathBasedPropertyProvider(metaInfo, paths);
    }

    /**
     * Creates a new read-only {@link org.apache.tamaya.PropertyProvider} based on the resources defined by the given paths. The effective resources
     * read hereby are determined by the {@code PathResolverService} configured into the {@code Bootstrap} SPI.
     *
     * @param uris the uris to be read, not null.
     * @return a new {@link }PropertyMap} instance based on the given paths/resources found.
     */
    public static PropertyProvider fromUris(URI... uris) {
        return fromUris(MetaInfo.of("Built from URIs"), uris);
    }

    /**
     * Creates a new read-only {@link org.apache.tamaya.PropertyProvider} based on the resources defined by the given paths. The effective resources
     * read hereby are determined by the {@code PathResolverService} configured into the {@code Bootstrap} SPI.
     *
     * @param uris the uris to be read, not null.
     * @return a new {@link }PropertyMap} instance based on the given paths/resources found.
     */
    public static PropertyProvider fromUris(Collection<URI> uris) {
        return fromUris(MetaInfo.of("Built from URIs"), uris);
    }

    /**
     * Creates a new read-only {@link org.apache.tamaya.PropertyProvider} based on the resources defined by the given paths. The effective resources
     * read hereby are determined by the {@code PathResolverService} configured into the {@code Bootstrap} SPI.
     *
     * @param metaInfo the meat information to be provided additionally.
     * @param uris     the uris to be read, not null.
     * @return a new {@link }PropertyMap} instance based on the given paths/resources found.
     */
    public static PropertyProvider fromUris(MetaInfo metaInfo, URI... uris) {
        Objects.requireNonNull(metaInfo);
        return fromUris(metaInfo, Arrays.asList(uris));
    }

    /**
     * Creates a new read-only {@link org.apache.tamaya.PropertyProvider} based on the resources defined by the given paths. The effective resources
     * read hereby are determined by the {@code PathResolverService} configured into the {@code Bootstrap} SPI.
     *
     * @param metaInfo the meat information to be provided additionally.
     * @param uris     the uris to be read, not null.
     * @return a new {@link }PropertyMap} instance based on the given paths/resources found.
     */
    public static PropertyProvider fromUris(MetaInfo metaInfo, Collection<URI> uris) {
        return new URIBasedPropertyProvider(metaInfo, uris);
    }

    /**
     * Creates a new read-only {@link PropertyProvider} by using the given Map.
     *
     * @param map the properties to be included, not null.
     * @return a new {@link }PropertyMap} instance with the given properties from the Map instance passed.
     */
    public static PropertyProvider from(Map<String, String> map) {
        return from(MetaInfo.of("Built from Map"), map);
    }


    /**
     * Creates a new read-only {@link PropertyProvider} by using the given Map.
     *
     * @param metaInfo the meat information to be provided additionally.
     * @param map      the properties to be included, not null.
     * @return a new {@link }PropertyMap} instance with the given properties from the Map instance passed.
     */
    public static PropertyProvider from(MetaInfo metaInfo, Map<String, String> map) {
        return new MapBasedPropertyProvider(metaInfo, map);
    }

    /**
     * Create meta information for CLI arguments passed.
     *
     * @param args the CLI arguments, not null.
     * @return the corresponding meta information.
     */
    private static MetaInfo createArgsMetaInfo(String... args) {
        MetaInfoBuilder metaBuilder = MetaInfoBuilder.of();
        return metaBuilder.setType("cli").set("args", Arrays.toString(args)).build();
    }


    /**
     * Create meta information for CLI arguments passed.
     *
     * @param paths the paths to be resolved by the {@code PathResolverService} , not null.
     * @return the corresponding meta information.
     */
    private static MetaInfo createPathMetaInfo(String... paths) {
        MetaInfoBuilder metaBuilder = MetaInfoBuilder.of();
        return metaBuilder.setSourceExpressions(paths).build();
    }

    /**
     * Get an empty and immutable PropertyProvider instance.
     *
     * @return an empty and immutable PropertyProvider instance, never null.
     */
    public static PropertyProvider empty() {
        return EMPTY_PROPERTYPROVIDER;
    }

    /**
     * Get an empty and immutable PropertyProvider instance.
     *
     * @return an empty and mutable PropertyProvider instance, never null.
     */
    public static PropertyProvider emptyMutable() {
        return PropertyProviders.from(new ConcurrentHashMap<>());
    }

    /**
     * Get an empty and immutable PropertyProvider instance. The meta-information contains the given String
     * under the key 'info'.
     *
     * @return an empty and immutable PropertyProvider instance, never null, with the given Strings as info meta-data..
     */
    public static PropertyProvider empty(MetaInfo metaInfo) {
        return from(metaInfo, Collections.emptyMap());
    }

    /**
     * Get an empty and mutable PropertyProvider instance. The meta-information contains the given String
     * under the key 'info'.
     *
     * @return an empty and immutable PropertyProvider instance, never null, with the given Strings as info meta-data..
     */
    public static PropertyProvider emptyMutable(MetaInfo metaInfo) {
        return from(metaInfo, new ConcurrentHashMap<>());
    }

    /**
     * Returns a read-only {@link PropertyProvider} reflecting the current runtime environment properties.
     *
     * @return a new read-only {@link PropertyProvider} instance based on the current runtime environment properties.
     */
    public static PropertyProvider fromEnvironmentProperties() {
        return ENV_PROPERTYPROVIDER;
    }

    /**
     * Creates a new read-only {@link PropertyProvider} reflecting the current system properties.
     *
     * @return a new read-only {@link PropertyProvider} instance based on the current system properties.
     */
    public static PropertyProvider fromSystemProperties() {
        return new SystemPropertiesPropertyProvider();
    }

    /**
     * Converts a given {@link org.apache.tamaya.PropertyProvider} instance into a serializable and immutable form,
     * so it can be sent over a network connection.
     *
     * @param provider the PropertyProvider to be freezed.
     * @return the serializable instance.
     */
    public static PropertyProvider freezed(PropertyProvider provider) {
        return FreezedPropertyProvider.of(provider);
    }

    /**
     * Creates a new {@link PropertyProvider} containing all property maps given, hereby later maps in the array override
     * properties from previous instances.
     *
     * @param propertyMaps the maps to be included, not null.
     * @return the union instance containing all given maps.
     */
    public static PropertyProvider union(PropertyProvider... propertyMaps) {
        return union(AggregationPolicy.OVERRIDE, propertyMaps);
    }

    /**
     * Creates a new {@link PropertyProvider} containing all property maps given, hereby using the given AggregationPolicy.
     *
     * @param policy       the AggregationPolicy to be used, not null.
     * @param propertyMaps the maps to be included, not null.
     * @return the aggregated instance containing all given maps.
     */
    public static PropertyProvider union(AggregationPolicy policy, PropertyProvider... propertyMaps) {
        return new AggregatedPropertyProvider(null, policy, propertyMaps);
    }

    /**
     * Creates a new {@link PropertyProvider} that is mutable by adding a map based instance that overrides
     * values from the original map.
     * @param provider the provider to be made mutable, not null.
     * @return the mutable instance.
     */
    public static PropertyProvider mutable(PropertyProvider provider) {
        PropertyProvider mutableProvider = PropertyProviders.emptyMutable();
        return mutableUnion(mutableProvider, AggregationPolicy.OVERRIDE, provider, mutableProvider);
    }

    /**
     * Creates a new {@link PropertyProvider} containing all property maps given, hereby later maps in the array override
     * properties from previous instances.
     * @param mutableProvider the provider used for delegating change requests.
     * @param propertyMaps the maps to be included, not null.
     * @return the union instance containing all given maps.
     */
    public static PropertyProvider mutableUnion(PropertyProvider mutableProvider, PropertyProvider... propertyMaps) {
        return mutableUnion(mutableProvider, AggregationPolicy.OVERRIDE, propertyMaps);
    }

    /**
     * Creates a new {@link PropertyProvider} containing all property maps given, hereby using the given AggregationPolicy.
     * @param mutableProvider the provider used for delegating change requests.
     * @param policy       the AggregationPolicy to be used, not null.
     * @param propertyMaps the maps to be included, not null.
     * @return the aggregated instance containing all given maps.
     */
    public static PropertyProvider mutableUnion(PropertyProvider mutableProvider, AggregationPolicy policy, PropertyProvider... propertyMaps) {
        return new AggregatedPropertyProvider(mutableProvider, policy, propertyMaps);
    }

    /**
     * Creates a new {@link PropertyProvider} containing only properties that are shared by all given maps,
     * hereby later maps in the array override  properties from previous instances.
     *
     * @param propertyMaps the maps to be included, not null.
     * @return the intersecting instance containing all given maps.
     */
    public static PropertyProvider intersected(AggregationPolicy policy, PropertyProvider... propertyMaps) {
        return new IntersectingPropertyProvider(policy, propertyMaps);
    }

    /**
     * Creates a new {@link PropertyProvider} containing only properties that are shared by all given maps,
     * hereby later maps in the array override  properties from previous instances.
     *
     * @param propertyMaps the maps to be included, not null.
     * @return the intersecting instance containing all given maps.
     */
    public static PropertyProvider intersected(PropertyProvider... propertyMaps) {
        return new IntersectingPropertyProvider(AggregationPolicy.OVERRIDE, propertyMaps);
    }

    /**
     * Creates a new {@link PropertyProvider} containing only properties from the target instance, that are not contained
     * in one of the other maps passed.
     *
     * @param target         the base map, not null.
     * @param subtrahendSets the maps to be subtracted, not null.
     * @return the intersecting instance containing all given maps.
     */
    public static PropertyProvider subtracted(PropertyProvider target, PropertyProvider... subtrahendSets) {
        return new SubtractingPropertyProvider(target, subtrahendSets);
    }


    /**
     * Creates a filtered {@link PropertyProvider} (a view) of a given base {@link }PropertyMap}. The filter hereby is
     * applied dynamically on access, so also runtime changes of the base map are reflected appropriately.
     *
     * @param propertyMap the base map instance, not null.
     * @param filter      the filtger to be applied, not null.
     * @return the new filtering instance.
     */
    public static PropertyProvider filtered(Predicate<String> filter, PropertyProvider propertyMap) {
        return new FilteredPropertyProvider(propertyMap, filter);
    }

    /**
     * Creates a new contextual {@link PropertyProvider}. Contextual maps delegate to different instances of PropertyMap depending
     * on the keys returned from the isolationP
     *
     * @param mapSupplier          the supplier creating new provider instances
     * @param isolationKeySupplier the supplier providing contextual keys based on the current environment.
     */
    public static PropertyProvider contextual(Supplier<PropertyProvider> mapSupplier,
                                                 Supplier<String> isolationKeySupplier) {
        return new ContextualPropertyProvider(mapSupplier, isolationKeySupplier);
    }


    /**
     * Creates a filtered {@link PropertyProvider} (a view) of a given base {@link }PropertyMap}. The filter hereby is
     * applied dynamically on access, so also runtime changes of the base map are reflected appropriately.
     *
     * @param mainMap   the main map instance, not null.
     * @param parentMap the delegated parent map instance, not null.
     * @return the new delegating instance.
     */
    public static PropertyProvider delegating(PropertyProvider mainMap, Map<String, String> parentMap) {
        return new DelegatingPropertyProvider(mainMap, parentMap);
    }

    /**
     * Creates a {@link org.apache.tamaya.PropertyProvider} where all keys of a current map,
     * existing in another map are replaced
     * with the ones from the other {@link org.apache.tamaya.PropertyProvider}. The filter hereby is
     * applied dynamically on access, so also runtime changes of the base map are reflected appropriately.
     * Keys not existing in the {@code mainMap}, but present in {@code replacementMao} will be hidden.
     *
     * @param mainMap        the main map instance, which keys, present in {@code replacementMap} will be replaced
     *                       with the ones
     *                       in {@code replacementMap}, not null.
     * @param replacementMap the map instance, that will replace all corresponding entries in {@code mainMap}, not null.
     * @return the new delegating instance.
     */
    public static PropertyProvider replacing(PropertyProvider mainMap, Map<String, String> replacementMap) {
        return new ReplacingPropertyProvider(mainMap, replacementMap);
    }

}
