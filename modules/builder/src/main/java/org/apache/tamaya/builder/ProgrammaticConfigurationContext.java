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


import org.apache.tamaya.builder.spi.PropertyConverter;
import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.builder.spi.ConfigurationContext;
import org.apache.tamaya.builder.spi.ConfigurationContextBuilder;
import org.apache.tamaya.builder.spi.PropertyFilter;
import org.apache.tamaya.builder.spi.PropertySource;
import org.apache.tamaya.builder.spi.PropertySourceProvider;
import org.apache.tamaya.builder.spi.PropertyValueCombinationPolicy;
import org.apache.tamaya.builder.spi.ServiceContextManager;
import org.apache.tamaya.spisupport.PriorityServiceComparator;
import org.apache.tamaya.spisupport.PropertyConverterManager;
import org.apache.tamaya.spisupport.PropertySourceComparator;

import javax.annotation.Priority;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;

/**
 * Implementation of the {@link ConfigurationContext}
 * used by the {@link org.apache.tamaya.builder.ConfigurationBuilder}
 * internally.
 */
class ProgrammaticConfigurationContext implements ConfigurationContext {

    private static final Comparator<PropertySource> PS_COMPARATOR = new PropertySourceComparator();
    private static final Comparator<Object> COMP_COMPARATOR = new PriorityServiceComparator();
    /**
     * The logger used.
     */
    private final static Logger LOG = Logger.getLogger(ProgrammaticConfigurationContext.class.getName());
    /**
     * Cubcomponent handling {@link PropertyConverter} instances.
     */
    private PropertyConverterManager propertyConverterManager = new PropertyConverterManager();

    /**
     * The current unmodifiable list of loaded {@link PropertySource} instances.
     */
    private List<PropertySource> immutablePropertySources = new ArrayList<>();

    /**
     * The current unmodifiable list of loaded {@link PropertyFilter} instances.
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
    private final ReadWriteLock propertySourceLock = new ReentrantReadWriteLock();


    /**
     * The first time the Configuration system gets invoked we do initialize
     * all our {@link PropertySource}s and
     * {@link PropertyFilter}s which are known at startup.
     */
    @SuppressWarnings("unchecked")
    public ProgrammaticConfigurationContext(Builder builder) {
        propertyConverterManager = new PropertyConverterManager(builder.loadProvidedPropertyConverters);

        List<PropertySource> sources = getAllPropertySources(builder);
        Collections.sort(sources, PS_COMPARATOR);
        immutablePropertySources = Collections.unmodifiableList(sources);


        List<PropertyFilter> filters = getPropertyFilters(builder);
        Collections.sort(filters, COMP_COMPARATOR);
        immutablePropertyFilters = Collections.unmodifiableList(filters);


        propertyValueCombinationPolicy = builder.propertyValueCombinationPolicy;
        for(Map.Entry<TypeLiteral<?>, List<PropertyConverter<?>>> en: builder.propertyConverters.entrySet()){
            if(en!=null){
                for(PropertyConverter pv:en.getValue()) {
                    propertyConverterManager.register(en.getKey(), pv);
                }
            }
        }

        LOG.info("Using " + immutablePropertySources.size() + " property sources: " + immutablePropertySources);
        LOG.info("Using " + immutablePropertyFilters.size() + " property filters: " + immutablePropertyFilters);
        LOG.info("Using PropertyValueCombinationPolicy: " + propertyValueCombinationPolicy);
    }

    private List<PropertyFilter> getPropertyFilters(Builder builder) {
        List<PropertyFilter> provided = new ArrayList<>();
        if(builder.loadProvidedPropertyFilters) {
            provided.addAll(ServiceContextManager.getServiceContext().getServices(PropertyFilter.class));
        }
        for(PropertyFilter pf:builder.propertyFilters) {
            if (pf != null) {
                provided.add(pf);
            }
        }
        return provided;
    }

    private List<PropertySource> getAllPropertySources(Builder builder) {
        List<PropertySource> provided = new ArrayList<>();
        if(builder.loadProvidedPropertySources) {
            provided.addAll(ServiceContextManager.getServiceContext().getServices(PropertySource.class));
        }
        for(PropertySource ps:builder.propertySources){
            if(ps!=null){
                provided.add(ps);
            }
        }
        if (builder.loadProvidedPropertySourceProviders) {
            List<PropertySourceProvider> providers = ServiceContextManager.getServiceContext()
                                                                  .getServices(PropertySourceProvider.class);
            for (PropertySourceProvider provider : providers) {
                for(PropertySource ps:provider.getPropertySources()) {
                    if(ps!=null) {
                        provided.addAll(provider.getPropertySources());
                    }
                }
            }
        }
        return provided;
    }

    public void addPropertySources(PropertySource... propertySourcesToAdd) {
        Lock writeLock = propertySourceLock.writeLock();
        try {
            writeLock.lock();
            List<PropertySource> provided = new ArrayList<>();
            for(PropertySource ps:propertySourcesToAdd){
                if(ps!=null){
                    provided.add(ps);
                }
            }
            this.immutablePropertySources = Collections.unmodifiableList(provided);
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

        //X TODO this method duplicates DefaultConfigurationContext.PropertySourceComparator.comparePropertySources()
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

        //X TODO this method duplicates DefaultConfigurationContext.PropertySourceComparator.comparePropertyFilters()
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

    public <T> void addPropertyConverter(TypeLiteral<T> typeToConvert, PropertyConverter<T> propertyConverter) {
        propertyConverterManager.register(typeToConvert, propertyConverter);
        LOG.info("Added PropertyConverter: " + propertyConverter.getClass().getName());
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


    @Override
    public ConfigurationContextBuilder toBuilder() {
        // @todo Check if it could be useful to support this method, Oliver B. Fischer
        throw new RuntimeException("This method is currently not supported.");
    }

    /**
     * The Builder for {@link ProgrammaticConfigurationContext}
     */
    public final static class Builder {
        /**
         * The current unmodifiable list of loaded {@link PropertySource} instances.
         */
        private final List<PropertySource> propertySources = new ArrayList<>();

        /**
         * The current unmodifiable list of loaded {@link PropertyFilter} instances.
         */
        private final List<PropertyFilter> propertyFilters = new ArrayList<>();

        private final Map<TypeLiteral<?>, List<PropertyConverter<?>>> propertyConverters = new HashMap<>();

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
            for (PropertySource ps : propertySources) {
                if (ps != null) {
                    this.propertySources.add(ps);
                }
            }
            return this;
        }

        public Builder addPropertySources(Collection<PropertySource> propertySources) {
            for (PropertySource ps : propertySources) {
                if (ps != null) {
                    this.propertySources.add(ps);
                }
            }
            return this;
        }

        public Builder addPropertySourceProviders(PropertySourceProvider... propertySourceProviders) {
            for (PropertySourceProvider ps : propertySourceProviders) {
                if (ps != null) {
                    this.propertySources.addAll(ps.getPropertySources());
                }
            }
            return this;
        }

        public Builder addPropertySourceProviders(Collection<PropertySourceProvider> propertySourceProviders) {
            for (PropertySourceProvider ps : propertySourceProviders) {
                if (ps != null) {
                    this.propertySources.addAll(ps.getPropertySources());
                }
            }
            return this;
        }

        public Builder addPropertyFilters(PropertyFilter... propertyFIlter) {
            for (PropertyFilter pf : propertyFIlter) {
                if (pf != null) {
                    this.propertyFilters.add(pf);
                }
            }
            return this;
        }

        public Builder addPropertyFilters(Collection<PropertyFilter> propertyFIlter) {
            for (PropertyFilter pf : propertyFIlter) {
                if (pf != null) {
                    this.propertyFilters.add(pf);
                }
            }
            return this;
        }

        /**
         * Should be never used.
         */
        @Deprecated
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
            if(!propertyConverters.containsKey(type)){
                List<PropertyConverter<?>> convList = new ArrayList<>();
                convList.add(propertyConverter);
                propertyConverters.put(type, convList);
            }
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
