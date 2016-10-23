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

import org.apache.tamaya.ConfigurationProvider;
import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.spi.ConfigurationContext;
import org.apache.tamaya.spi.ConfigurationContextBuilder;
import org.apache.tamaya.spi.PropertyConverter;
import org.apache.tamaya.spi.PropertyFilter;
import org.apache.tamaya.spi.PropertySource;
import org.apache.tamaya.spi.PropertyValueCombinationPolicy;
import org.apache.tamaya.spi.ServiceContextManager;

import javax.annotation.Priority;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;

/**
 * Default Implementation of a simple ConfigurationContext.
 */
public class DefaultConfigurationContext implements ConfigurationContext {
    /** The logger used. */
    private final static Logger LOG = Logger.getLogger(DefaultConfigurationContext.class.getName());
    /**
     * Cubcomponent handling {@link org.apache.tamaya.spi.PropertyConverter} instances.
     */
    private final PropertyConverterManager propertyConverterManager = new PropertyConverterManager();

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
     * Lock for internal synchronization.
     */
    private final ReentrantReadWriteLock propertySourceLock = new ReentrantReadWriteLock();
    /**
     * Lock for internal synchronization.
     */
    private final ReentrantReadWriteLock propertyFilterLock = new ReentrantReadWriteLock();


    DefaultConfigurationContext(DefaultConfigurationContextBuilder builder) {
        List<PropertySource> propertySources = new ArrayList<>();
        // first we load all PropertySources which got registered via java.util.ServiceLoader
        propertySources.addAll(builder.propertySources);
        // now sort them according to their ordinal values
        immutablePropertySources = Collections.unmodifiableList(propertySources);
        LOG.info("Registered " + immutablePropertySources.size() + " property sources: " +
                immutablePropertySources);

        // as next step we pick up the PropertyFilters pretty much the same way
        List<PropertyFilter> propertyFilters = new ArrayList<>(builder.getPropertyFilters());
        immutablePropertyFilters = Collections.unmodifiableList(propertyFilters);
        LOG.info("Registered " + immutablePropertyFilters.size() + " property filters: " +
                immutablePropertyFilters);

        // Finally add the converters
        for(Map.Entry<TypeLiteral<?>, Collection<PropertyConverter<?>>> en:builder.getPropertyConverter().entrySet()) {
            for (PropertyConverter converter : en.getValue()) {
                this.propertyConverterManager.register(en.getKey(), converter);
            }
        }
        LOG.info("Registered " + propertyConverterManager.getPropertyConverters().size() + " property converters: " +
                propertyConverterManager.getPropertyConverters());

        propertyValueCombinationPolicy = builder.combinationPolicy;
        if(propertyValueCombinationPolicy==null){
            propertyValueCombinationPolicy = ServiceContextManager.getServiceContext().getService(PropertyValueCombinationPolicy.class);
        }
        if(propertyValueCombinationPolicy==null){
            propertyValueCombinationPolicy = PropertyValueCombinationPolicy.DEFAULT_OVERRIDING_COLLECTOR;
        }
        LOG.info("Using PropertyValueCombinationPolicy: " + propertyValueCombinationPolicy);
    }


    @Deprecated
    @Override
    public void addPropertySources(PropertySource... propertySourcesToAdd) {
        Lock writeLock = propertySourceLock.writeLock();
        try {
            writeLock.lock();
            List<PropertySource> newPropertySources = new ArrayList<>(this.immutablePropertySources);
            newPropertySources.addAll(Arrays.asList(propertySourcesToAdd));
            Collections.sort(newPropertySources, new PropertySourceComparator());

            this.immutablePropertySources = Collections.unmodifiableList(newPropertySources);
        } finally {
            writeLock.unlock();
        }
    }


    @Deprecated
    @Override
    public void addPropertyFilter(PropertyFilter... propertyFiltersToAdd) {
        Lock writeLock = propertyFilterLock.writeLock();
        try {
            writeLock.lock();
            List<PropertyFilter> newPropertyFilters = new ArrayList<>(this.immutablePropertyFilters);
            newPropertyFilters.addAll(Arrays.asList(propertyFiltersToAdd));
            Collections.sort(newPropertyFilters, new PropertyFilterComparator());

            this.immutablePropertyFilters = Collections.unmodifiableList(newPropertyFilters);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public boolean containsPropertySource(String name) {
        return getPropertySource(name)!=null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DefaultConfigurationContext)) return false;

        DefaultConfigurationContext that = (DefaultConfigurationContext) o;

        if (!propertyConverterManager.equals(that.propertyConverterManager)) return false;
        if (!immutablePropertySources.equals(that.immutablePropertySources)) return false;
        if (!immutablePropertyFilters.equals(that.immutablePropertyFilters)) return false;
        return getPropertyValueCombinationPolicy().equals(that.getPropertyValueCombinationPolicy());

    }

    @Override
    public int hashCode() {
        int result = propertyConverterManager.hashCode();
        result = 31 * result + immutablePropertySources.hashCode();
        result = 31 * result + immutablePropertyFilters.hashCode();
        result = 31 * result + getPropertyValueCombinationPolicy().hashCode();
        return result;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder("ConfigurationContext{\n");
        b.append("  Property Sources\n");
        b.append("  ----------------\n");
        b.append("  CLASS                         NAME                                                                  ORDINAL SCANNABLE SIZE\n");
        for(PropertySource ps:getPropertySources()){
            b.append("  ");
            appendFormatted(b, ps.getClass().getSimpleName(), 30);
            appendFormatted(b, ps.getName(), 70);
            appendFormatted(b, String.valueOf(ps.getOrdinal()), 8);
            appendFormatted(b, String.valueOf(ps.isScannable()), 10);
            if(ps.isScannable()) {
                appendFormatted(b, String.valueOf(ps.getProperties().size()), 8);
            }else{
                appendFormatted(b, "-", 8);
            }
            b.append('\n');
        }
        b.append("\n");
        b.append("  Property Filters\n");
        b.append("  ----------------\n");
        b.append("  CLASS                         INFO\n");
        for(PropertyFilter filter:getPropertyFilters()){
            b.append("  ");
            appendFormatted(b, filter.getClass().getSimpleName(), 30);
            b.append(removeNewLines(filter.toString()));
            b.append('\n');
        }
        b.append("\n\n");
        b.append("  Property Converters\n");
        b.append("  -------------------\n");
        b.append("  CLASS                         TYPE                          INFO\n");
        for(Map.Entry<TypeLiteral<?>, List<PropertyConverter<?>>> converterEntry:getPropertyConverters().entrySet()){
            for(PropertyConverter converter: converterEntry.getValue()){
                b.append("  ");
                appendFormatted(b, converter.getClass().getSimpleName(), 30);
                appendFormatted(b, converterEntry.getKey().getRawType().getSimpleName(), 30);
                b.append(removeNewLines(converter.toString()));
                b.append('\n');
            }
        }
        b.append('}');
        return b.toString();
    }

    private void appendFormatted(StringBuilder b, String text, int length) {
        int padding;
        if(text.length() <= (length)){
            b.append(text);
            padding = length - text.length();
        }else{
            b.append(text.substring(0, length-1));
            padding = 1;
        }
        for(int i=0;i<padding;i++){
            b.append(' ');
        }
    }

    private String removeNewLines(String s) {
        return s.replace('\n', ' ').replace('\r', ' ');
    }

    private static class PropertySourceComparator implements Comparator<PropertySource>, Serializable {

        private static final long serialVersionUID = 1L;

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

        @Override
        public int compare(PropertySource source1, PropertySource source2) {
            return comparePropertySources(source1, source2);
        }
    }

    private static class PropertyFilterComparator implements Comparator<PropertyFilter>, Serializable{

        private static final long serialVersionUID = 1L;

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
        public int compare(PropertyFilter filter1, PropertyFilter filter2) {
            return comparePropertyFilters(filter1, filter2);
        }
    }

    @Override
    public List<PropertySource> getPropertySources() {
        return immutablePropertySources;
    }

    @Override
    public PropertySource getPropertySource(String name) {
        for(PropertySource ps:getPropertySources()){
            if(name.equals(ps.getName())){
                return ps;
            }
        }
        return null;
    }

    @Override
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
    public PropertyValueCombinationPolicy getPropertyValueCombinationPolicy(){
        return propertyValueCombinationPolicy;
    }

    @Override
    public ConfigurationContextBuilder toBuilder() {
        return ConfigurationProvider.getConfigurationContextBuilder().setContext(this);
    }

}
