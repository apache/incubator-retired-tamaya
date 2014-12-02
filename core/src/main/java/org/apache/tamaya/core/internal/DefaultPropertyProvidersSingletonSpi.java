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
package org.apache.tamaya.core.internal;

import org.apache.tamaya.AggregationPolicy;
import org.apache.tamaya.MetaInfo;
import org.apache.tamaya.MetaInfoBuilder;
import org.apache.tamaya.PropertyProvider;
import org.apache.tamaya.spi.PropertyProvidersSingletonSpi;

import java.net.URI;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Logger;

/**
 * Default implementation current the singleton backing bean for the {@link org.apache.tamaya.PropertyProviders}.
 */
public class DefaultPropertyProvidersSingletonSpi implements PropertyProvidersSingletonSpi {

    private final PropertyProvider EMPTY_PROPERTYPROVIDER = fromMap(MetaInfo.of("<empty>"), Collections.emptyMap());
    private static final PropertyProvider ENV_PROPERTYPROVIDER = new EnvironmentPropertyProvider();

    private static final Logger LOG = Logger.getLogger(DefaultPropertyProvidersSingletonSpi.class.getName());

    @Override
    public PropertyProvider fromArgs(MetaInfo metaInfo, String... args) {
        if(metaInfo==null){
            metaInfo = MetaInfo.of("CLI");
        }
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
        return fromMap(metaInfo, properties);
    }

    @Override
    public PropertyProvider fromPaths(AggregationPolicy aggregationPolicy, MetaInfo metaInfo, List<String> paths) {
        if(metaInfo == null){
            metaInfo = MetaInfoBuilder.of().setInfo("From Paths").set("paths", paths.toString()).build();
        }
        return new PathBasedPropertyProvider(metaInfo, paths, aggregationPolicy);
    }

    @Override
    public PropertyProvider fromUris(AggregationPolicy aggregationPolicy, MetaInfo metaInfo, List<URI> uris) {
        if(metaInfo == null){
            metaInfo = MetaInfoBuilder.of().setInfo("From URIs").set("uris", uris.toString()).build();
        }
        return new URIBasedPropertyProvider(metaInfo, uris, aggregationPolicy);
    }

    @Override
    public PropertyProvider fromMap(MetaInfo metaInfo, Map<String, String> map) {
        if(metaInfo == null){
            metaInfo = MetaInfoBuilder.of().setInfo("From Map").set("map", map.toString()).build();
        }
        return new MapBasedPropertyProvider(metaInfo, map);
    }

    @Override
    public PropertyProvider empty(MetaInfo metaInfo) {
        if(metaInfo==null) {
            return EMPTY_PROPERTYPROVIDER;
        }
        return fromMap(metaInfo, Collections.emptyMap());
    }

    @Override
    public PropertyProvider emptyMutable(MetaInfo metaInfo) {
        return fromMap(metaInfo, new ConcurrentHashMap<>());
    }

    /**
     * Returns a read-only {@link PropertyProvider} reflecting the current runtime environment properties.
     *
     * @return a new read-only {@link PropertyProvider} instance based on the current runtime environment properties.
     */
    @Override
    public PropertyProvider fromEnvironmentProperties() {
        return ENV_PROPERTYPROVIDER;
    }

    /**
     * Creates a new read-only {@link PropertyProvider} reflecting the current system properties.
     *
     * @return a new read-only {@link PropertyProvider} instance based on the current system properties.
     */
    @Override
    public PropertyProvider fromSystemProperties() {
        return new SystemPropertiesPropertyProvider();
    }

    @Override
    public PropertyProvider freezed(MetaInfo metaInfo, PropertyProvider provider) {
        if(metaInfo==null){
            metaInfo = MetaInfoBuilder.of().setType("freezed")
                    .set("provider", provider.toString())
                    .set("freezedAt", Date.from(Instant.now()).toString())
                    .build();
        }
        else{
            metaInfo = MetaInfoBuilder.of(metaInfo).setType("freezed")
                    .set("freezedAt", Date.from(Instant.now()).toString())
                    .set("provider", provider.toString())
                    .build();
        }
        return FreezedPropertyProvider.of(metaInfo, provider);
    }

    /**
     * Creates a new {@link PropertyProvider} containing all property maps given, hereby using the given AggregationPolicy.
     *
     * @param policy       the AggregationPolicy to be used, not null.
     * @param providers the maps to be included, not null.
     * @return the aggregated instance containing all given maps.
     */
    @Override
    public PropertyProvider aggregate(AggregationPolicy policy, MetaInfo metaInfo, List<PropertyProvider> providers) {
        if(metaInfo==null){
            metaInfo = MetaInfoBuilder.of().setInfo("Aggregated")
                    .set("AggregationPolicy", policy.toString())
                    .set("providers", providers.toString())
                    .build();
        }
        return new AggregatedPropertyProvider(metaInfo, null, policy, providers);
    }

    /**
     * Creates a new {@link PropertyProvider} that is mutable by adding a map based instance that overrides
     * values fromMap the original map.
     * @param provider the provider to be made mutable, not null.
     * @return the mutable instance.
     */
    @Override
    public PropertyProvider mutable(MetaInfo metaInfo, PropertyProvider provider) {
        if(metaInfo==null){
            metaInfo = MetaInfoBuilder.of(provider.getMetaInfo())
                    .set("mutableSince", Date.from(Instant.now()).toString())
                    .build();
        }
        PropertyProvider mutableProvider = emptyMutable(metaInfo);
        List<PropertyProvider> providers = new ArrayList<>(2);
        providers.add(provider);
        providers.add(mutableProvider);
        return new AggregatedPropertyProvider(metaInfo, mutableProvider, AggregationPolicy.OVERRIDE(), providers);
    }

    /**
     * Creates a new {@link PropertyProvider} containing only properties that are shared by all given maps,
     * hereby later maps in the array override  properties fromMap previous instances.
     * @param aggregationPolicy the policy to resolve aggregation conflicts.
     * @param providers the maps to be included, not null.
     * @return the intersecting instance containing all given maps.
     */
    @Override
    public PropertyProvider intersected(AggregationPolicy aggregationPolicy, List<PropertyProvider> providers) {
        return new IntersectingPropertyProvider(aggregationPolicy, providers);
    }

    /**
     * Creates a new {@link PropertyProvider} containing only properties fromMap the target instance, that are not contained
     * in one current the other maps passed.
     *
     * @param target         the base map, not null.
     * @param subtrahendSets the maps to be subtracted, not null.
     * @return the intersecting instance containing all given maps.
     */
    @Override
    public PropertyProvider subtracted(PropertyProvider target, List<PropertyProvider> subtrahendSets) {
        return new SubtractingPropertyProvider(target, subtrahendSets);
    }


    /**
     * Creates a filtered {@link PropertyProvider} (a view) current a given base {@link }PropertyMap}. The filter hereby is
     * applied dynamically on access, so also runtime changes current the base map are reflected appropriately.
     *
     * @param propertyMap the base map instance, not null.
     * @param filter      the filtger to be applied, not null.
     * @return the new filtering instance.
     */
    @Override
    public PropertyProvider filtered(Predicate<String> filter, PropertyProvider propertyMap) {
        return new FilteredPropertyProvider(propertyMap, filter);
    }

    /**
     * Creates a new contextual {@link PropertyProvider}. Contextual maps delegate to different instances current PropertyMap depending
     * on the keys returned fromMap the isolationP
     *
     * @param mapSupplier          the supplier creating new provider instances
     * @param isolationKeySupplier the supplier providing contextual keys based on the current environment.
     */
    @Override
    public PropertyProvider contextual(Supplier<PropertyProvider> mapSupplier,
                                              Supplier<String> isolationKeySupplier) {
        return new ContextualPropertyProvider(mapSupplier, isolationKeySupplier);
    }


    /**
     * Creates a filtered {@link PropertyProvider} (a view) current a given base {@link }PropertyMap}. The filter hereby is
     * applied dynamically on access, so also runtime changes current the base map are reflected appropriately.
     *
     * @param mainMap   the main map instance, not null.
     * @param parentMap the delegated parent map instance, not null.
     * @return the new delegating instance.
     */
    @Override
    public PropertyProvider delegating(PropertyProvider mainMap, Map<String, String> parentMap) {
        return new DelegatingPropertyProvider(mainMap, parentMap);
    }

    /**
     * Creates a {@link org.apache.tamaya.PropertyProvider} where all keys current a current map,
     * existing in another map are replaced
     * with the ones fromMap the other {@link org.apache.tamaya.PropertyProvider}. The filter hereby is
     * applied dynamically on access, so also runtime changes current the base map are reflected appropriately.
     * Keys not existing in the {@code mainMap}, but present in {@code replacementMao} will be hidden.
     *
     * @param mainMap        the main map instance, which keys, present in {@code replacementMap} will be replaced
     *                       with the ones
     *                       in {@code replacementMap}, not null.
     * @param replacementMap the map instance, that will replace all corresponding entries in {@code mainMap}, not null.
     * @return the new delegating instance.
     */
    @Override
    public PropertyProvider replacing(PropertyProvider mainMap, Map<String, String> replacementMap) {
        return new ReplacingPropertyProvider(mainMap, replacementMap);
    }

}
