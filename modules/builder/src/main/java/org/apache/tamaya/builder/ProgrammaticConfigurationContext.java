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
package org.apache.tamaya.builder;


import org.apache.tamaya.PropertyConverter;
import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.core.internal.PropertyConverterManager;
import org.apache.tamaya.spi.ConfigurationContext;
import org.apache.tamaya.spi.PropertyFilter;
import org.apache.tamaya.spi.PropertySource;
import org.apache.tamaya.spi.PropertySourceProvider;
import org.apache.tamaya.spi.PropertyValueCombinationPolicy;
import org.apache.tamaya.spi.ServiceContext;

import javax.annotation.Priority;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.StampedLock;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * Implementation of the {@link org.apache.tamaya.spi.ConfigurationContext}
 * used by the {@link org.apache.tamaya.builder.ConfigurationBuilder}
 * internally.
 */
class ProgrammaticConfigurationContext implements ConfigurationContext {

    /**
     * The logger used.
     */
    private final static Logger LOG = Logger.getLogger(ProgrammaticConfigurationContext.class.getName());
    /**
     * Cubcomponent handling {@link org.apache.tamaya.PropertyConverter} instances.
     */
    private PropertyConverterManager propertyConverterManager = new PropertyConverterManager();

    /**
     * The current unmodifiable list of loaded {@link org.apache.tamaya.spi.PropertySource} instances.
     */
    private List<PropertySource> immutablePropertySources = new ArrayList<>();

    /**
     * The current unmodifiable list of loaded {@link org.apache.tamaya.spi.PropertyFilter} instances.
     */
    private List<PropertyFilter> immutablePropertyFilters = new ArrayList<>();

    /**
     * The overriding policy used when combining PropertySources registered to evalute the final configuration
     * values.
     */
    private PropertyValueCombinationPolicy propertyValueCombinationPolicy;

    /**
     * Lock for internal synchronization.
     */
    private StampedLock propertySourceLock = new StampedLock();


    /**
     * The first time the Configuration system gets invoked we do initialize
     * all our {@link org.apache.tamaya.spi.PropertySource}s and
     * {@link org.apache.tamaya.spi.PropertyFilter}s which are known at startup.
     */
    public ProgrammaticConfigurationContext(Builder builder) {
        propertyConverterManager = new PropertyConverterManager(builder.loadProvidedPropertyConverters);
        immutablePropertySources = getAllPropertySources(builder);
        Collections.sort(immutablePropertySources, this::comparePropertySources);
        immutablePropertySources = Collections.unmodifiableList(immutablePropertySources);
        LOG.info(() -> "Using " + immutablePropertySources.size() + " property sources: " +
                createStringList(immutablePropertySources, ps -> ps.getName() + '[' + ps.getClass().getName() + ']'));

        immutablePropertyFilters.addAll(getPropertyFilters(builder));
        Collections.sort(immutablePropertyFilters, this::comparePropertyFilters);
        immutablePropertyFilters = Collections.unmodifiableList(immutablePropertyFilters);
        LOG.info(() -> "Using " + immutablePropertyFilters.size() + " property filters: " +
                createStringList(immutablePropertyFilters, f -> f.getClass().getName()));

        propertyValueCombinationPolicy = builder.propertyValueCombinationPolicy;

        Set<Map.Entry<TypeLiteral<?>, List<PropertyConverter<?>>>> converters = builder.propertyConverters.entrySet();

        for (Map.Entry<TypeLiteral<?>, List<PropertyConverter<?>>> entry : converters) {
            TypeLiteral<?> literal = entry.getKey();

            for (PropertyConverter<?> converter : entry.getValue()) {
                propertyConverterManager.register((TypeLiteral<Object>)literal, (PropertyConverter<Object>)converter);
            }
        }

        LOG.info(() -> "Using PropertyValueCombinationPolicy: " + propertyValueCombinationPolicy);
    }

    private List<PropertyFilter> getPropertyFilters(Builder builder) {
        List<PropertyFilter> provided = builder.loadProvidedPropertyFilters
                ? ServiceContext.getInstance().getServices(PropertyFilter.class)
                : new ArrayList<>(0);

        List<PropertyFilter> configured = builder.propertyFilters;

        return join(provided, configured);
    }

    private List<PropertySource> getAllPropertySources(Builder builder) {
        List<PropertySource> provided = builder.loadProvidedPropertySources
                ? ServiceContext.getInstance().getServices(PropertySource.class)
                : new ArrayList<>(0);

        if (builder.loadProvidedPropertySourceProviders) {
            List<PropertySourceProvider> providers = ServiceContext.getInstance()
                                                                  .getServices(PropertySourceProvider.class);
            for (PropertySourceProvider provider : providers) {
                Collection<PropertySource> sources = provider.getPropertySources();
                provided.addAll(sources);
            }
        }

        List<PropertySource> configured = builder.propertySources;

        return join(provided, configured);
    }

    private <T> List<T> join(List<T> a, List<T> b) {
        int sizeA = a.size();
        int sizeB = b.size();

        ArrayList<T> result = new ArrayList<>(sizeA + sizeB);

        result.addAll(a);
        result.addAll(b);

        return result;
    }

    @Override
    public void addPropertySources(PropertySource... propertySourcesToAdd) {
        Lock writeLock = propertySourceLock.asWriteLock();
        try {
            writeLock.lock();
            List<PropertySource> newPropertySources = new ArrayList<>(this.immutablePropertySources);
            newPropertySources.addAll(Arrays.asList(propertySourcesToAdd));
            Collections.sort(newPropertySources, this::comparePropertySources);

            this.immutablePropertySources = Collections.unmodifiableList(newPropertySources);
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Order property source reversely, the most important come first.
     *
     * @param source1 the first PropertySource
     * @param source2 the second PropertySource
     * @return the comparison result.
     */
    private int comparePropertySources(PropertySource source1, PropertySource source2) {

        //X TODO this method duplicates org.apache.tamaya.core.internal.DefaultConfigurationContext.PropertySourceComparator.comparePropertySources()
        //X maybe we should extract the Comperator in an own class for real code-reuse (copy paste == bad code reuse)

        if (source1.getOrdinal() < source2.getOrdinal()) {
            return -1;
        } else if (source1.getOrdinal() > source2.getOrdinal()) {
            return 1;
        } else {
            return source1.getClass().getName().compareTo(source2.getClass().getName());
        }
    }

    /**
     * Compare 2 filters for ordering the filter chain.
     *
     * @param filter1 the first filter
     * @param filter2 the second filter
     * @return the comparison result
     */
    private int comparePropertyFilters(PropertyFilter filter1, PropertyFilter filter2) {

        //X TODO this method duplicates org.apache.tamaya.core.internal.DefaultConfigurationContext.PropertySourceComparator.comparePropertyFilters()
        //X maybe we should extract the Comperator in an own class for real code-reuse (copy paste == bad code reuse)

        Priority prio1 = filter1.getClass().getAnnotation(Priority.class);
        Priority prio2 = filter2.getClass().getAnnotation(Priority.class);
        int ord1 = prio1 != null ? prio1.value() : 0;
        int ord2 = prio2 != null ? prio2.value() : 0;

        if (ord1 < ord2) {
            return -1;
        } else if (ord1 > ord2) {
            return 1;
        } else {
            return filter1.getClass().getName().compareTo(filter2.getClass().getName());
        }
    }

    @Override
    public List<PropertySource> getPropertySources() {
        return immutablePropertySources;
    }

    @Override
    public <T> void addPropertyConverter(TypeLiteral<T> typeToConvert, PropertyConverter<T> propertyConverter) {
        propertyConverterManager.register(typeToConvert, propertyConverter);
        LOG.info(() -> "Added PropertyConverter: " + propertyConverter.getClass().getName());
    }

    @Override
    public Map<TypeLiteral<?>, List<PropertyConverter<?>>> getPropertyConverters() {
        return propertyConverterManager.getPropertyConverters();
    }

    @Override
    public <T> List<PropertyConverter<T>> getPropertyConverters(TypeLiteral<T> targetType) {
        return propertyConverterManager.getPropertyConverters(targetType);
    }

    @Override
    public List<PropertyFilter> getPropertyFilters() {
        return immutablePropertyFilters;
    }

    @Override
    public PropertyValueCombinationPolicy getPropertyValueCombinationPolicy() {
        return propertyValueCombinationPolicy;
    }

    private <T> String createStringList(Collection<T> propertySources, Function<T, String> mapper) {
        StringJoiner joiner = new StringJoiner(", ");
        propertySources.forEach(t -> joiner.add(mapper.apply(t)));
        return joiner.toString();
    }

    /**
     * The Builder for {@link ProgrammaticConfigurationContext}
     */
    public final static class Builder {
        /**
         * The current unmodifiable list of loaded {@link org.apache.tamaya.spi.PropertySource} instances.
         */
        private List<PropertySource> propertySources = new ArrayList<>();

        /**
         * The current unmodifiable list of loaded {@link org.apache.tamaya.spi.PropertyFilter} instances.
         */
        private List<PropertyFilter> propertyFilters = new ArrayList<>();

        private Map<TypeLiteral<?>, List<PropertyConverter<?>>> propertyConverters = new HashMap<>();

        /**
         * The overriding policy used when combining PropertySources registered to evalute the final configuration
         * values.
         */
        private PropertyValueCombinationPolicy propertyValueCombinationPolicy =
                PropertyValueCombinationPolicy.DEFAULT_OVERRIDING_COLLECTOR;

        private boolean loadProvidedPropertyConverters;
        private boolean loadProvidedPropertySources;
        private boolean loadProvidedPropertySourceProviders;
        private boolean loadProvidedPropertyFilters;

        public Builder setPropertyValueCombinationPolicy(PropertyValueCombinationPolicy policy) {
            this.propertyValueCombinationPolicy = Objects.requireNonNull(policy);
            return this;
        }

        public Builder addPropertySources(PropertySource... propertySources) {
            this.propertySources.addAll(Arrays.asList(propertySources));
            return this;
        }

        public Builder addPropertySources(Collection<PropertySource> propertySources) {
            this.propertySources.addAll(propertySources);
            return this;
        }

        public Builder addPropertySourceProviders(PropertySourceProvider... propertySourceProviders) {
            return addPropertySourceProviders(Arrays.asList(propertySourceProviders));
        }

        public Builder addPropertySourceProviders(Collection<PropertySourceProvider> propertySourceProviders) {
            for(PropertySourceProvider prov: propertySourceProviders) {
                this.propertySources.addAll(evaluatePropertySourcesFromProviders(prov));
            }
            return this;
        }

        public Builder addPropertyFilters(PropertyFilter... propertySources) {
            this.propertyFilters.addAll(Arrays.asList(propertySources));
            return this;
        }

        public Builder addPropertyFilters(Collection<PropertyFilter> propertySources) {
            this.propertyFilters.addAll(propertySources);
            return this;
        }

        /**
         * Pick up all {@link org.apache.tamaya.spi.PropertySourceProvider}s and return all the
         * {@link org.apache.tamaya.spi.PropertySource}s they like to register.
         */
        private Collection<PropertySource> evaluatePropertySourcesFromProviders(PropertySourceProvider provider) {
            List<PropertySource> propertySources = new ArrayList<>();
            Collection<PropertySource> sources = provider.getPropertySources();
            propertySources.addAll(sources);
            return propertySources;
        }

        public Builder setConfigurationContext(ConfigurationContext configurationContext) {
            this.addPropertySources(configurationContext.getPropertySources());
            this.addPropertyFilters(configurationContext.getPropertyFilters());
            this.propertyValueCombinationPolicy = Objects.requireNonNull(
                    configurationContext.getPropertyValueCombinationPolicy());
            return this;
        }

        //X TODO think on a functonality/API for using the default PropertyConverters and use the configured ones here
        //X TODO as overrides used first.

        public <T> Builder addPropertyConverter(TypeLiteral<T> type, PropertyConverter<T> propertyConverter) {
            propertyConverters.computeIfAbsent(type, (t) -> new ArrayList<>())
                    .add(propertyConverter);
            return this;
        }

        public ConfigurationContext build() {
            return new ProgrammaticConfigurationContext(this);
        }


        public void loadProvidedPropertyConverters(boolean state) {
            loadProvidedPropertyConverters = state;
        }

        public void loadProvidedPropertySources(boolean state) {
            loadProvidedPropertySources = state;
        }

        public void loadProvidedPropertySourceProviders(boolean state) {
            loadProvidedPropertySourceProviders = state;
        }

        public void loadProvidedPropertyFilters(boolean state) {
            loadProvidedPropertyFilters = state;
        }
    }



}
