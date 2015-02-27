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

import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.spi.ConfigurationContext;
import org.apache.tamaya.PropertyConverter;
import org.apache.tamaya.spi.ConfigurationContextBuilder;
import org.apache.tamaya.spi.PropertyFilter;
import org.apache.tamaya.spi.PropertySource;
import org.apache.tamaya.spi.PropertySourceProvider;
import org.apache.tamaya.spi.PropertyValueCombinationPolicy;
import org.apache.tamaya.spi.ServiceContext;

import javax.annotation.Priority;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * Default Implementation of a simple ConfigurationContext.
 */
public class DefaultConfigurationContext implements ConfigurationContext {
    /** The logger used. */
    private final static Logger LOG = Logger.getLogger(DefaultConfigurationContext.class.getName());
    /**
     * Cubcomponent handling {@link org.apache.tamaya.PropertyConverter} instances.
     */
    private PropertyConverterManager propertyConverterManager = new PropertyConverterManager();

    /**
     * The current unmodifiable list of loaded {@link org.apache.tamaya.spi.PropertySource} instances.
     */
    private List<PropertySource> immutablePropertySources;

    /**
     * The current unmodifiable list of loaded {@link org.apache.tamaya.spi.PropertyFilter} instances.
     */
    private List<PropertyFilter> immutablePropertyFilters;

    /**
     * The overriding policy used when combining PropertySources registered to evalute the final configuration
     * values.
     */
    private PropertyValueCombinationPolicy propertyValueCombinationPolicy;

    /**
     * The first time the Configuration system gets invoked we do initialize
     * all our {@link org.apache.tamaya.spi.PropertySource}s and
     * {@link org.apache.tamaya.spi.PropertyFilter}s which are known at startup.
     */
    public DefaultConfigurationContext() {
        List<PropertySource> propertySources = new ArrayList<>();

        // first we load all PropertySources which got registered via java.util.ServiceLoader
        propertySources.addAll(ServiceContext.getInstance().getServices(PropertySource.class));

        // after that we add all PropertySources which get dynamically registered via their PropertySourceProviders
        propertySources.addAll(evaluatePropertySourcesFromProviders());

        // now sort them according to their ordinal values
        Collections.sort(propertySources, this::comparePropertySources);
        immutablePropertySources = Collections.unmodifiableList(propertySources);
        LOG.info(() -> "Registered " + immutablePropertySources.size() + " property sources: " +
                createStringList(immutablePropertySources,ps -> ps.getName() + '[' + ps.getClass().getName()+']'));

        // as next step we pick up the PropertyFilters pretty much the same way
        List<PropertyFilter> propertyFilters = new ArrayList<>();
        propertyFilters.addAll(ServiceContext.getInstance().getServices(PropertyFilter.class));
        Collections.sort(propertyFilters, this::comparePropertyFilters);
        immutablePropertyFilters = Collections.unmodifiableList(propertyFilters);
        LOG.info(() -> "Registered " + immutablePropertyFilters.size() + " property filters: " +
                createStringList(immutablePropertyFilters,f -> f.getClass().getName()));

        propertyValueCombinationPolicy = ServiceContext.getInstance().getService(PropertyValueCombinationPolicy.class)
                .orElse(PropertyValueCombinationPolicy.DEFAULT_OVERRIDING_COLLECTOR);
        LOG.info(() -> "Using PropertyValueCombinationPolicy: " + propertyValueCombinationPolicy);
    }

    DefaultConfigurationContext(DefaultConfigurationContextBuilder builder) {
        List<PropertySource> propertySources = new ArrayList<>();
        // first we load all PropertySources which got registered via java.util.ServiceLoader
        propertySources.addAll(builder.propertySources);
        // now sort them according to their ordinal values
        Collections.sort(propertySources, this::comparePropertySources);
        immutablePropertySources = Collections.unmodifiableList(propertySources);
        LOG.info(() -> "Registered " + immutablePropertySources.size() + " property sources: " +
                createStringList(immutablePropertySources,ps -> ps.getName() + '[' + ps.getClass().getName()+']'));

        // as next step we pick up the PropertyFilters pretty much the same way
        List<PropertyFilter> propertyFilters = new ArrayList<>();
        propertyFilters.addAll(ServiceContext.getInstance().getServices(PropertyFilter.class));
        Collections.sort(propertyFilters, this::comparePropertyFilters);
        immutablePropertyFilters = Collections.unmodifiableList(propertyFilters);
        LOG.info(() -> "Registered " + immutablePropertyFilters.size() + " property filters: " +
                createStringList(immutablePropertyFilters,f -> f.getClass().getName()));

        propertyValueCombinationPolicy = ServiceContext.getInstance().getService(PropertyValueCombinationPolicy.class)
                .orElse(PropertyValueCombinationPolicy.DEFAULT_OVERRIDING_COLLECTOR);
        LOG.info(() -> "Using PropertyValueCombinationPolicy: " + propertyValueCombinationPolicy);
    }

    /**
     * Pick up all {@link org.apache.tamaya.spi.PropertySourceProvider}s and return all the
     * {@link org.apache.tamaya.spi.PropertySource}s they like to register.
     */
    private Collection<? extends PropertySource> evaluatePropertySourcesFromProviders() {
        List<PropertySource> propertySources = new ArrayList<>();
        List<PropertySourceProvider> propertySourceProviders = ServiceContext.getInstance().getServices(PropertySourceProvider.class);
        for (PropertySourceProvider propertySourceProvider : propertySourceProviders) {
            Collection<PropertySource> sources = propertySourceProvider.getPropertySources();
                LOG.finer(() -> "PropertySourceProvider " + propertySourceProvider.getClass().getName() +
                        " provided the following property sources: " +
                        createStringList(sources,ps -> ps.getName() + '[' + ps.getClass().getName()+']'));
                propertySources.addAll(sources);
        }
        return propertySources;
    }

//    @Override
//    public void addPropertySources(PropertySource... propertySourcesToAdd) {
//        Lock writeLock = propertySourceLock.asWriteLock();
//        try {
//            writeLock.lock();
//            List<PropertySource> newPropertySources = new ArrayList<>(this.immutablePropertySources);
//            newPropertySources.addAll(Arrays.asList(propertySourcesToAdd));
//            Collections.sort(newPropertySources, this::comparePropertySources);
//
//            this.immutablePropertySources = Collections.unmodifiableList(newPropertySources);
//        } finally {
//            writeLock.unlock();
//        }
//    }

    /**
     * Order property source reversely, the most important come first.
     *
     * @param source1 the first PropertySource
     * @param source2 the second PropertySource
     * @return the comparison result.
     */
    private int comparePropertySources(PropertySource source1, PropertySource source2) {
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

//    @Override
//    public <T> void addPropertyConverter(TypeLiteral<T> typeToConvert, PropertyConverter<T> propertyConverter) {
//        propertyConverterManager.register(typeToConvert, propertyConverter);
//        LOG.info(() -> "Added PropertyConverter: " + propertyConverter.getClass().getName());
//    }

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
    public PropertyValueCombinationPolicy getPropertyValueCombinationPolicy(){
        return propertyValueCombinationPolicy;
    }

    @Override
    public ConfigurationContextBuilder toBuilder() {
        return new DefaultConfigurationContextBuilder().setContext(this);
    }

    private <T> String createStringList(Collection<T> propertySources, Function<T,String> mapper){
        StringJoiner joiner = new StringJoiner(", ");
        propertySources.forEach(t -> joiner.add(mapper.apply(t)));
        return joiner.toString();
    }
}
