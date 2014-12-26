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
import java.time.Instant;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.apache.tamaya.PropertySource;

/**
 * Default implementation current the singleton backing bean for the {@link org.apache.tamaya.core.properties.PropertySourceBuilder}.
 */
public final class PropertySourceFactory {

    private static final PropertySource EMPTY_PROPERTYSOURCE = fromMap("<empty>", Collections.emptyMap());
    private static final PropertySource ENV_PROPERTYSOURCE = new EnvironmentPropertySource();


    /**
     * Singleton constructor.
     */
    private PropertySourceFactory(){}

    public static PropertySource fromArgs(String name, String... args) {
        if(name==null){
            name ="<CLI> " + Arrays.toString(args);
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
        return fromMap(name, properties);
    }

    public static PropertySource fromPaths(String name, AggregationPolicy aggregationPolicy, List<String> paths) {
        if(name==null){
            name ="<Paths> " + paths.toString();
        }
        return new PathBasedPropertySource(name, paths, aggregationPolicy);
    }

    public static PropertySource fromURLs(String name, AggregationPolicy aggregationPolicy, List<URL> urls) {
        if(name==null){
            name ="<URLs> " + urls.toString();
        }
        return new URLBasedPropertySource(name, urls, aggregationPolicy);
    }

    public static PropertySource fromMap(String name, Map<String, String> map) {
        if(name==null){
            name ="<Map> " + map.toString();
        }
        return new MapBasedPropertySource(name, map);
    }

    public static PropertySource empty(String name) {
        if(name==null) {
            return EMPTY_PROPERTYSOURCE;
        }
        return fromMap(name, Collections.emptyMap());
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

    public static PropertySource freezed(String name, PropertySource source) {
        if(name==null){
            name ="<Freezed> source=" + source.toString()+", at="+Instant.now().toString();
        }
        return FreezedPropertySource.of(name, source);
    }

    /**
     * Creates a new {@link org.apache.tamaya.PropertySource} containing all property maps given, hereby using the given AggregationPolicy.
     *
     * @param policy       the AggregationPolicy to be used, not null.
     * @param providers the maps to be included, not null.
     * @return the aggregated instance containing all given maps.
     */
    public static PropertySource aggregate(String name, AggregationPolicy policy, List<PropertySource> providers) {
        if(name==null){
            name ="<Aggregate> policy=" + policy.toString()+", providers="+providers.toString();
        }
        return new AggregatedPropertySource(name, null, policy, providers);
    }

    /**
     * Creates a new {@link org.apache.tamaya.PropertySource} that is mutable by adding a map based instance that overrides
     * values fromMap the original map.
     * @param provider the provider to be made mutable, not null.
     * @return the mutable instance.
     */
    public static PropertySource mutable(String name, PropertySource provider) {
        if(name==null){
            name ="<Mutable> provider="+provider.getName();
        }
        PropertySource mutableProvider = fromMap(name,new HashMap<>());
        List<PropertySource> providers = new ArrayList<>(2);
        providers.add(provider);
        providers.add(mutableProvider);
        return new AggregatedPropertySource(name, mutableProvider, AggregationPolicy.OVERRIDE, providers);
    }

    /**
     * Creates a new {@link org.apache.tamaya.PropertySource} containing only properties that are shared by all given maps,
     * hereby later maps in the array override  properties fromMap previous instances.
     * @param aggregationPolicy the policy to resolve aggregation conflicts.
     * @param providers the maps to be included, not null.
     * @return the intersecting instance containing all given maps.
     */
    public static PropertySource intersected(String name, AggregationPolicy aggregationPolicy, List<PropertySource> providers) {
        return new IntersectingPropertySource(name, aggregationPolicy, providers);
    }

    /**
     * Creates a new {@link org.apache.tamaya.PropertySource} containing only properties fromMap the target instance, that are not contained
     * in one current the other maps passed.
     *
     * @param target         the base map, not null.
     * @param subtrahendSets the maps to be subtracted, not null.
     * @return the intersecting instance containing all given maps.
     */
    public static PropertySource subtracted(String name, PropertySource target, List<PropertySource> subtrahendSets) {
        return new SubtractingPropertySource(name, target,subtrahendSets);
    }


    /**
     * Creates a filtered {@link org.apache.tamaya.PropertySource} (a view) current a given base {@link }PropertyMap}. The filter hereby is
     * applied dynamically on access, so also runtime changes current the base map are reflected appropriately.
     *
     * @param name the base map instance, not null.
     * @param filter      the filtger to be applied, not null.
     * @return the new filtering instance.
     */
    public static PropertySource filtered(String name, Predicate<String> filter, PropertySource source) {
        if(name==null){
            name ="<Filtered> filter="+filter+", source="+source.getName();
        }
        return new FilteredPropertySource(name, source, filter);
    }

    /**
     * Creates a new contextual {@link org.apache.tamaya.PropertySource}. Contextual maps delegate to different instances current PropertyMap depending
     * on the keys returned fromMap the isolationP
     *
     * @param name the base name instance, not null.
     * @param mapSupplier          the supplier creating new provider instances
     * @param isolationKeySupplier the supplier providing contextual keys based on the current environment.
     */
    public static PropertySource contextual(String name, Supplier<PropertySource> mapSupplier,
                                              Supplier<String> isolationKeySupplier) {
        if(name==null){
            name ="<Contextual> mapSupplier="+mapSupplier+", isolationKeyProvider="+isolationKeySupplier;
        }
        return new ContextualPropertySource(name, mapSupplier, isolationKeySupplier);
    }


    /**
     * Creates a filtered {@link org.apache.tamaya.PropertySource} (a view) current a given base {@link }PropertyMap}. The filter hereby is
     * applied dynamically on access, so also runtime changes current the base map are reflected appropriately.
     *
     * @param name the base name instance, not null.
     * @param source   the main map instance, not null.
     * @param parentMap the delegated parent map instance, not null.
     * @return the new delegating instance.
     */
    public static PropertySource delegating(String name, PropertySource source, Map<String, String> parentMap) {
        if(name==null){
            name ="<Delegating> source="+source+", delegates="+parentMap;
        }
        return new DelegatingPropertySource(name, source, parentMap);
    }

    /**
     * Creates a {@link org.apache.tamaya.PropertySource} where all keys current a current map,
     * existing in another map are replaced
     * with the ones fromMap the other {@link org.apache.tamaya.PropertySource}. The filter hereby is
     * applied dynamically on access, so also runtime changes current the base map are reflected appropriately.
     * Keys not existing in the {@code mainMap}, but present in {@code replacementMao} will be hidden.
     *
     * @param name the base name instance, not null.
     * @param source the main source instance, which keys, present in {@code replacementMap} will be replaced
     *                       with the ones
     *                       in {@code replacementMap}, not null.
     * @param replacementMap the map instance, that will replace all corresponding entries in {@code mainMap}, not null.
     * @return the new delegating instance.
     */
    public static PropertySource replacing(String name, PropertySource source, Map<String, String> replacementMap) {
        if(name==null){
            name ="<Replacement> source="+source+", replacements="+replacementMap;
        }
        return new ReplacingPropertySource(name, source, replacementMap);
    }

    /**
     * Creates a new {@link org.apache.tamaya.PropertySource} given an existing one, and an alternate
     * meta-info.
     * @param name the new meta-information, not null.
     * @param baseProvider the property source, not null.
     * @return the new property source.never null.
     */
    public static PropertySource build(String name, PropertySource baseProvider) {
        return new BuildablePropertySource(name, baseProvider);
    }

    /**
     * Creates a new filtered {@link org.apache.tamaya.PropertySource} using the given filter.
     * @param name the base name instance, not null.
     * @param valueFilter the value filter function, null result will remove the given entries.
     * @param current the source to be filtered
     */
    public static PropertySource filterValues(String name, BiFunction<String, String, String> valueFilter, PropertySource current) {
        return new ValueFilteredPropertySource(name, valueFilter, current);
    }
}
