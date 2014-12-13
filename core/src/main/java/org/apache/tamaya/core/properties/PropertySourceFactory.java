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

import org.apache.tamaya.AggregationPolicy;
import org.apache.tamaya.MetaInfo;
import org.apache.tamaya.MetaInfoBuilder;
import org.apache.tamaya.PropertySource;

import java.net.URL;
import java.time.Instant;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Logger;

/**
 * Default implementation current the singleton backing bean for the {@link org.apache.tamaya.core.properties.PropertySourceBuilder}.
 */
public final class PropertySourceFactory {

    private static final PropertySource EMPTY_PROPERTYSOURCE = fromMap(MetaInfo.of("<empty>"), Collections.emptyMap());
    private static final PropertySource ENV_PROPERTYSOURCE = new EnvironmentPropertySource();

    private static final Logger LOG = Logger.getLogger(PropertySourceFactory.class.getName());

    /**
     * Singleton constructor.
     */
    private PropertySourceFactory(){}

    public static PropertySource fromArgs(MetaInfo metaInfo, String... args) {
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

    public static PropertySource fromPaths(MetaInfo metaInfo, AggregationPolicy aggregationPolicy, List<String> paths) {
        if(metaInfo == null){
            metaInfo = MetaInfoBuilder.of().setInfo("From Paths").set("paths", paths.toString()).build();
        }
        return new PathBasedPropertySource(metaInfo, paths, aggregationPolicy);
    }

    public static PropertySource fromURLs(MetaInfo metaInfo, AggregationPolicy aggregationPolicy, List<URL> resources) {
        if(metaInfo == null){
            metaInfo = MetaInfoBuilder.of().setInfo("From Resources").set("resources", resources.toString()).build();
        }
        return new URLBasedPropertySource(metaInfo, resources, aggregationPolicy);
    }

    public static PropertySource fromMap(MetaInfo metaInfo, Map<String, String> map) {
        if(metaInfo == null){
            metaInfo = MetaInfoBuilder.of().setInfo("From Map").set("map", map.toString()).build();
        }
        return new MapBasedPropertySource(metaInfo, map);
    }

    public static PropertySource empty(MetaInfo metaInfo) {
        if(metaInfo==null) {
            return EMPTY_PROPERTYSOURCE;
        }
        return fromMap(metaInfo, Collections.emptyMap());
    }

    /**
     * Returns a read-only {@link org.apache.tamaya.PropertySource} reflecting the current runtime environment properties.
     *
     * @return a new read-only {@link org.apache.tamaya.PropertySource} instance based on the current runtime environment properties.
     */
    public static PropertySource fromEnvironmentProperties() {
        return ENV_PROPERTYSOURCE;
    }

    /**
     * Creates a new read-only {@link org.apache.tamaya.PropertySource} reflecting the current system properties.
     *
     * @return a new read-only {@link org.apache.tamaya.PropertySource} instance based on the current system properties.
     */
    public static PropertySource fromSystemProperties() {
        return new SystemPropertiesPropertySource();
    }

    public static PropertySource freezed(MetaInfo metaInfo, PropertySource provider) {
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
        return FreezedPropertySource.of(metaInfo, provider);
    }

    /**
     * Creates a new {@link org.apache.tamaya.PropertySource} containing all property maps given, hereby using the given AggregationPolicy.
     *
     * @param policy       the AggregationPolicy to be used, not null.
     * @param providers the maps to be included, not null.
     * @return the aggregated instance containing all given maps.
     */
    public static PropertySource aggregate(MetaInfo metaInfo, AggregationPolicy policy, List<PropertySource> providers) {
        if(metaInfo==null){
            metaInfo = MetaInfoBuilder.of().setInfo("Aggregated")
                    .set("AggregationPolicy", policy.toString())
                    .set("config", providers.toString())
                    .build();
        }
        return new AggregatedPropertySource(metaInfo, null, policy, providers);
    }

    /**
     * Creates a new {@link org.apache.tamaya.PropertySource} that is mutable by adding a map based instance that overrides
     * values fromMap the original map.
     * @param provider the provider to be made mutable, not null.
     * @return the mutable instance.
     */
    public static PropertySource mutable(MetaInfo metaInfo, PropertySource provider) {
        if(metaInfo==null){
            metaInfo = MetaInfoBuilder.of(provider.getMetaInfo())
                    .set("mutableSince", Date.from(Instant.now()).toString())
                    .build();
        }
        PropertySource mutableProvider = fromMap(metaInfo,new HashMap<>());
        List<PropertySource> providers = new ArrayList<>(2);
        providers.add(provider);
        providers.add(mutableProvider);
        return new AggregatedPropertySource(metaInfo, mutableProvider, AggregationPolicy.OVERRIDE, providers);
    }

    /**
     * Creates a new {@link org.apache.tamaya.PropertySource} containing only properties that are shared by all given maps,
     * hereby later maps in the array override  properties fromMap previous instances.
     * @param aggregationPolicy the policy to resolve aggregation conflicts.
     * @param providers the maps to be included, not null.
     * @return the intersecting instance containing all given maps.
     */
    public static PropertySource intersected(MetaInfo metaInfo, AggregationPolicy aggregationPolicy, List<PropertySource> providers) {
        return new IntersectingPropertySource(metaInfo, aggregationPolicy, providers);
    }

    /**
     * Creates a new {@link org.apache.tamaya.PropertySource} containing only properties fromMap the target instance, that are not contained
     * in one current the other maps passed.
     *
     * @param target         the base map, not null.
     * @param subtrahendSets the maps to be subtracted, not null.
     * @return the intersecting instance containing all given maps.
     */
    public static PropertySource subtracted(MetaInfo metaInfo, PropertySource target, List<PropertySource> subtrahendSets) {
        return new SubtractingPropertySource(metaInfo, target,subtrahendSets);
    }


    /**
     * Creates a filtered {@link org.apache.tamaya.PropertySource} (a view) current a given base {@link }PropertyMap}. The filter hereby is
     * applied dynamically on access, so also runtime changes current the base map are reflected appropriately.
     *
     * @param propertyMap the base map instance, not null.
     * @param filter      the filtger to be applied, not null.
     * @return the new filtering instance.
     */
    public static PropertySource filtered(MetaInfo metaInfo, Predicate<String> filter, PropertySource propertyMap) {
        return new FilteredPropertySource(metaInfo, propertyMap, filter);
    }

    /**
     * Creates a new contextual {@link org.apache.tamaya.PropertySource}. Contextual maps delegate to different instances current PropertyMap depending
     * on the keys returned fromMap the isolationP
     *
     * @param mapSupplier          the supplier creating new provider instances
     * @param isolationKeySupplier the supplier providing contextual keys based on the current environment.
     */
    public static PropertySource contextual(MetaInfo metaInfo, Supplier<PropertySource> mapSupplier,
                                              Supplier<String> isolationKeySupplier) {
        return new ContextualPropertySource(metaInfo, mapSupplier, isolationKeySupplier);
    }


    /**
     * Creates a filtered {@link org.apache.tamaya.PropertySource} (a view) current a given base {@link }PropertyMap}. The filter hereby is
     * applied dynamically on access, so also runtime changes current the base map are reflected appropriately.
     *
     * @param mainMap   the main map instance, not null.
     * @param parentMap the delegated parent map instance, not null.
     * @return the new delegating instance.
     */
    public static PropertySource delegating(MetaInfo metaInfo, PropertySource mainMap, Map<String, String> parentMap) {
        return new DelegatingPropertySource(metaInfo, mainMap, parentMap);
    }

    /**
     * Creates a {@link org.apache.tamaya.PropertySource} where all keys current a current map,
     * existing in another map are replaced
     * with the ones fromMap the other {@link org.apache.tamaya.PropertySource}. The filter hereby is
     * applied dynamically on access, so also runtime changes current the base map are reflected appropriately.
     * Keys not existing in the {@code mainMap}, but present in {@code replacementMao} will be hidden.
     *
     * @param mainMap        the main map instance, which keys, present in {@code replacementMap} will be replaced
     *                       with the ones
     *                       in {@code replacementMap}, not null.
     * @param replacementMap the map instance, that will replace all corresponding entries in {@code mainMap}, not null.
     * @return the new delegating instance.
     */
    public static PropertySource replacing(MetaInfo metaInfo, PropertySource mainMap, Map<String, String> replacementMap) {
        return new ReplacingPropertySource(metaInfo, mainMap, replacementMap);
    }

    /**
     * Creates a new {@link org.apache.tamaya.PropertySource} given an existing one, and an alternate
     * meta-info.
     * @param metaInfo the new meta-information, not null.
     * @param baseProvider the property source, not null.
     * @return the new property source.never null.
     */
    public static PropertySource build(MetaInfo metaInfo, PropertySource baseProvider) {
        return new BuildablePropertySource(metaInfo, baseProvider);
    }

}
