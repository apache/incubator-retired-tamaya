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
package org.apache.tamaya.core.properties.factories;

import java.lang.IllegalArgumentException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.tamaya.core.properties.AggregationPolicy;
import org.apache.tamaya.core.properties.PropertySourceFactory;
import org.apache.tamaya.spi.PropertySource;

/**
 * Builder for assembling non trivial property providers.
 */
public final class PropertySourcesBuilder {

    /**
     * Name used for the final result.
     */
    private Map<String, PropertySource> propertySources = new HashMap<>();

    /**
     * The current aggregation policy used, when aggregating providers.
     */
    private AggregationPolicy aggregationPolicy = AggregationPolicy.OVERRIDE;

    /**
     * The current ordinal used for creating property source instances.
     */
    private int currentOrdinal;
    /** The increment added to {@code currentOrdinal}, when new property sources are added and no
     * explcit ordinal is used. */
    private int ordinalIncrement;

    /** The name to be used for the next PropertySource constructed, or null, for creating a defau,t name. */
    private String currentName;

    /**
     * Private singleton constructor.
     */
    private PropertySourcesBuilder(int ordinal, int ordinalIncrement) {
        this.ordinalIncrement = ordinalIncrement;
        this.currentOrdinal = ordinal;
    }

    /**
     * Creates a new builder instance.
     *
     * @param ordinal The initial starting ordinal to be used when new property sources are created and added.
     * @param ordinalIncrement the ordinal increment to be added when no new property sources are created and added.
     * @return a new builder instance, never null.
     */
    public static PropertySourcesBuilder of(int ordinal, int ordinalIncrement) {
        return new PropertySourcesBuilder(ordinal, ordinalIncrement);
    }

    /**
     * Creates a new builder instance.
     *
     * @param ordinal The initial starting ordinal to be used when new property sources are created and added.
     *                The default ordinal increment used is 10.
     * @return a new builder instance, never null.
     */
    public static PropertySourcesBuilder of(int ordinal) {
        return new PropertySourceBuilder(ordinal, 10);
    }


    /**
     * Creates a new builder instance, starting with ordinal of 1 and an ordinal increment of 10.
     *
     * @param name the provider name, not null.
     * @return a new builder instance, never null.
     */
    public static PropertySourcesBuilder of() {
        return new PropertySourceBuilder(1, 10);
    }

    /**
     * Sets the current ordinal used, when additional property sets are added. The ordinal will normally implicitly
     * incresed for each property set by {@code ordinalIncrement}. If the ordinal is passed explcitly to a method
     * all created sources will have the same ordinal.
     *
     * @param ordinal the ordinal to be used.
     * @return the builder for chaining.
     */
    public PropertySourcesBuilder withOrdinal(int ordinal) {
        this.currentOrdinal = ordinal;
        return this;
    }

    /**
     * Sets the current name used, when the next property sets is added. If set to null a default name will be
     * constructed.
     *
     * @param name the name to be used for the next property set created, or null.
     * @return the builder for chaining.
     */
    public PropertySourcesBuilder withName(String name) {
        this.currentName = name;
        return this;
    }

    /**
     * Aggregates the given {@code sources} (at least 2) into a new
     * {@link PropertySource} and adds it with the given current ordinal and name.
     *
     * @param aggregationPolicy the aggregation policy, not null.
     * @param sources the property sources to be aggregated.
     * @return the builder for chaining.
     */
    public PropertySourcesBuilder addAggregation(AggregationPolicy aggregationPolicy, PropertySource... sources) {
        this.aggregationPolicy = Objects.requireNonNull(aggregationPolicy);
        String name = this.currentName;
        if(name==null){
            name = "Aggregate["+ Arrays.stream(sources).map(s -> s.getName()).collect(
                    () -> "",
                    (s,o) -> s + ',' + o,
                    (s1, s2) -> s1 + ',' + s2) +"]"
        }
        ProperttySource source = new new AggregatedPropertySource(currentOrdinal, name, aggregationPolicy, sources);
        this.propertySources.put(source.getName(), source);
        this.currentOrdinal +=  ordinalIncrement;
        return this;
    }

    /**
     * Adds the given providers with the current active {@link AggregationPolicy}. By
     * default {@link AggregationPolicy#OVERRIDE} is used.
     *
     * @param propertySources the property sources to be added, not null.
     * @return the builder for chaining.
     * @see #withAggregationPolicy(AggregationPolicy)
     */
    public PropertySourcesBuilder addPropertySources(PropertySource... propertySources) {
        if (propertySources.length == 0) {
            return this;
        }
        return addPropertySources(Arrays.asList(propertySources));
    }

    /**
     * Adds the given providers with the current active {@link AggregationPolicy}. By
     * default {@link AggregationPolicy#OVERRIDE} is used.
     *
     * @param providers providers to be added, not null.
     * @return the builder for chaining.
     * @see #withAggregationPolicy(AggregationPolicy)
     */
    public PropertySourcesBuilder addPropertySources(List<PropertySource> providers) {
        if (providers.isEmpty()) {
            return this;
        }
        for (PropertySource src : providers) {
            PropertySource current = this.propertySources.get(src.getName());
            if (src != null) {
                throw new IllegalArgumentException("PropertySource with that name is already existing: " + src.getName())
            }
        }
        for (PropertySource src : providers) {
            this.propertySources.put(sec.getName(), src);
        }
        return this;
    }

    /**
     * Creates a new {@link PropertySource} using the given command line arguments and adds it
     * using the current aggregation policy in place.
     *
     * @param args the command line arguments, not null.
     * @return the builder for chaining.
     */
    public PropertySourcesBuilder addArgs(String... args) {
        if (args.length == 0) {
            return this;
        }
        String name = this.currentName;
        if (currentName == null) {
            name = "CLI-Args";
        }
        PropertySource argProvider = PropertySourceFactory.fromArgs(currentOrdinal, name, args);
        currentOrdinal+=ordinalIncrement;
        return addPropertySources(argProvider);
    }

    /**
     * Creates a new read-only {@link PropertySource} by reading the according path format. The effective format read
     * hereby are determined by the {@code PathResolverService} configured into the {@code Bootstrap} SPI.
     * Properties read are aggregated using the current aggregation policy active.
     *
     * @param paths the paths to be resolved by the {@code PathResolverService} , not null.
     * @return the builder for chaining.
     */
    public PropertySourcesBuilder addPaths(String... paths) {
        if (paths.length == 0) {
            return this;
        }
        return addPaths(Arrays.asList(paths));
    }


    /**
     * Creates a new read-only {@link PropertySource} by reading the according path format. The effective format read
     * hereby are determined by the {@code PathResolverService} configured into the {@code Bootstrap} SPI.
     * Properties read are aggregated using the current aggregation policy active.
     *
     * @param paths the paths to be resolved by the {@code PathResolverService} , not null.
     * @return the builder for chaining.
     */
    public PropertySourcesBuilder addPaths(List<String> paths) {
        if (paths.isEmpty()) {
            return this;
        }
        String name = this.currentName;
        if (currentName == null) {
            name = "paths=" + paths.toString();
        }
        addPropertySources(PropertySourceFactory.fromPaths(currentOrdinal, name, paths));
    }

    /**
     * Creates a new read-only {@link PropertySource} by reading the according URL format.
     * Properties read are aggregated using the current aggregation policy active.
     *
     * @param urls the urls to be read, not null.
     * @return the builder for chaining.
     */
    public PropertySourcesBuilder addURLs(URL... urls) {
        if (urls.length == 0) {
            return this;
        }
        return addURLs(Arrays.asList(urls));
    }

    /**
     * Creates a new read-only {@link PropertySource} by reading the according URL format.
     * Properties read are aggregated using the current aggregation policy active.
     *
     * @param urls the urls to be read, not null.
     * @return the builder for chaining.
     */
    public PropertySourcesBuilder addURLs(List<URL> urls) {
        if (urls.isEmpty()) {
            return this;
        }
        String name = this.currentName;
        if (currentName == null) {
            name = "URL: =" + urls;
        }
        return addPropertySources(PropertySourceFactory.fromURLs(name, this.aggregationPolicy, urls));
    }


    /**
     * Creates a new read-only {@link PropertySource} based on the given map.
     * Properties read are aggregated using the current aggregation policy active.
     *
     * @param map the map to be added, not null.
     * @return the builder for chaining.
     */
    public PropertySourcesBuilder addMap(Map<String, String> map) {
        if (map.isEmpty()) {
            return this;
        }
        String name = this.currentName;
        if (currentName == null) {
            name = "Map;
        }
        return addPropertySources(PropertySourceFactory.fromMap(currentOrdinal, name, map));
    }


    /**
     * Add the current environment properties. Aggregation is based on the current {@link AggregationPolicy} acvtive.
     *
     * @return the builder for chaining.
     */
    public PropertySourcesBuilder addEnvironmentProperties() {
        String name = this.currentName;
        if (currentName == null) {
            name = "Environment-Properties";
        }
        return addPropertySources(PropertySourceFactory.fromEnvironmentProperties());
    }

    /**
     * Add the current system properties. Aggregation is based on the current {@link AggregationPolicy} acvtive.
     *
     * @return the builder for chaining.
     */
    public PropertySourcesBuilder addSystemProperties() {
        String name = this.currentName;
        if (currentName == null) {
            name = "System-Properties";
        }
        return addPropertySources(PropertySourceFactory.fromSystemProperties());
    }


//
//    /**
//     * Filters the current {@link org.apache.tamaya.PropertySource} with the given valueFilter.
//     *
//     * @param valueFilter the value filter, not null.
//     * @return the (dynamically) filtered source instance, never null.
//     */
//    public PropertySourceBuilder filterValues(BiFunction<String, String, String> valueFilter) {
//        String name = this.currentName;
//        if (currentName == null) {
//            name = "<filteredValues> -> " + valueFilter;
//        }
//        this.current = PropertySourceFactory.filterValues(name, valueFilter, this.current);
//        return this;
//    }


    /**
     * Subtracts with the given {@link org.apache.tamaya.PropertySource} instance from the current properties.
     *
     * @param providers the maps to be subtracted, not null.
     * @return the builder for chaining.
     */
    public PropertySourcesBuilder subtract(PropertySource... providers) {
        if (providers.length == 0) {
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
     * Build a new property provider based on the input.
     *
     * @return a new property provider, or null.
     */
    public Collection<PropertySource> build() {
        return this.propertySources.values();
    }

}
