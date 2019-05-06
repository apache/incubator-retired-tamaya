/*
 * Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.apache.tamaya.spisupport;

import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.spi.ConfigurationContext;
import org.apache.tamaya.spi.PropertyConverter;
import org.apache.tamaya.spi.PropertyFilter;
import org.apache.tamaya.spi.PropertySource;
import org.apache.tamaya.spi.PropertyValue;
import org.apache.tamaya.spi.ServiceContext;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;

/**
 * Default implementation of a simple {@link ConfigurationContext}.
 */
public class DefaultConfigurationContext implements ConfigurationContext {

    /** The logger used. */
    private final static Logger LOG = Logger.getLogger(DefaultConfigurationContext.class.getName());
    private final MetadataProvider metaDataProvider;

    /**
     * Subcomponent handling {@link PropertyConverter} instances.
     */
    private PropertyConverterManager propertyConverterManager;

    /**
     * The current unmodifiable createList of loaded {@link PropertySource} instances.
     */
    private List<PropertySource> immutablePropertySources;

    /**
     * The current unmodifiable createList of loaded {@link PropertyFilter} instances.
     */
    private List<PropertyFilter> immutablePropertyFilters;

    /** The corresponding classLoader for this instance. */
    private ServiceContext serviceContext;


    /**
     * Lock for internal synchronization.
     */
    private final ReentrantReadWriteLock propertySourceLock = new ReentrantReadWriteLock();

    @SuppressWarnings("unchecked")
    protected DefaultConfigurationContext(DefaultConfigurationBuilder builder) {
        this.serviceContext = builder.serviceContext;
        this.metaDataProvider = Objects.requireNonNull(builder.metaDataProvider);
        this.metaDataProvider.init(this);
        propertyConverterManager = new PropertyConverterManager(serviceContext);
        List<PropertySource> propertySources = new ArrayList<>();
        // first we load all PropertySources which got registered via java.util.ServiceLoader
        propertySources.addAll(builder.propertySources);
        // now sort them according to their ordinal values
        immutablePropertySources = Collections.unmodifiableList(propertySources);

        // as next step we pick up the PropertyFilters pretty much the same way
        List<PropertyFilter> propertyFilters = new ArrayList<>(builder.getPropertyFilters());
        immutablePropertyFilters = Collections.unmodifiableList(propertyFilters);

        // Finally addPropertyValue the converters
        for(Map.Entry<TypeLiteral<?>, List<PropertyConverter<?>>> en:builder.getPropertyConverter().entrySet()) {
            for (@SuppressWarnings("rawtypes") PropertyConverter converter : en.getValue()) {
                this.propertyConverterManager.register(en.getKey(), converter);
            }
        }
        LOG.info("Registered " + propertyConverterManager.getPropertyConverters().size() + " property converters: " +
                propertyConverterManager.getPropertyConverters());
    }

    public DefaultConfigurationContext(ServiceContext serviceContext,
                                       List<PropertyFilter> propertyFilters, List<PropertySource> propertySources,
                                       Map<TypeLiteral<?>, List<PropertyConverter<?>>> propertyConverters,
                                       MetadataProvider metaDataProvider) {
        this.serviceContext = Objects.requireNonNull(serviceContext);
        this.immutablePropertyFilters = Collections.unmodifiableList(new ArrayList<>(propertyFilters));
        this.immutablePropertySources = Collections.unmodifiableList(new ArrayList<>(propertySources));
        this.metaDataProvider = Objects.requireNonNull(metaDataProvider);
        this.metaDataProvider.init(this);
        propertyConverterManager = new PropertyConverterManager(serviceContext);
        for(Map.Entry<TypeLiteral<?>, List<PropertyConverter<?>>> en:propertyConverters.entrySet()) {
            for (@SuppressWarnings("rawtypes") PropertyConverter converter : en.getValue()) {
                this.propertyConverterManager.register(en.getKey(), converter);
            }
        }
    }


    @Override
    public Map<String,String> getMetaData(String key) {
        return metaDataProvider.getMetaData(key);
    }

    @Override
    public ServiceContext getServiceContext() {
        return serviceContext;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DefaultConfigurationContext)){
            return false;
        }

        DefaultConfigurationContext that = (DefaultConfigurationContext) o;

        if (!propertyConverterManager.equals(that.propertyConverterManager)) {
            return false;
        }
        if (!immutablePropertySources.equals(that.immutablePropertySources)) {
            return false;
        }
        if (!immutablePropertyFilters.equals(that.immutablePropertyFilters)) {
            return false;
        }
        return true;

    }

    @Override
    public int hashCode() {
        int result = propertyConverterManager.hashCode();
        result = 31 * result + immutablePropertySources.hashCode();
        result = 31 * result + immutablePropertyFilters.hashCode();
        return result;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder("ConfigurationContext{\n");
        b.append("  Property Sources\n");
        b.append("  ----------------\n");
        if(immutablePropertySources.isEmpty()){
            b.append("  No property sources loaded.\n\n");
        }else {
            b.append("  CLASS                         NAME                                                                  ORDINAL SCANNABLE SIZE    STATE     ERROR\n\n");
            for (PropertySource ps : immutablePropertySources) {
                b.append("  ");
                appendFormatted(b, ps.getClass().getSimpleName(), 30);
                appendFormatted(b, ps.getName(), 70);
                appendFormatted(b, String.valueOf(PropertySourceComparator.getOrdinal(ps)), 8);
                appendFormatted(b, String.valueOf(ps.isScannable()), 10);
                if (ps.isScannable()) {
                    appendFormatted(b, String.valueOf(ps.getProperties().size()), 8);
                } else {
                    appendFormatted(b, "-", 8);
                }
                PropertyValue state = ps.get("_state");
                if(state==null || state.getValue()==null){
                    appendFormatted(b, "OK", 10);
                }else {
                    appendFormatted(b, state.getValue(), 10);
                    if("ERROR".equals(state.getValue())){
                        PropertyValue val = ps.get("_exception");
                        if(val!=null) {
                            appendFormatted(b, val.getValue(), 30);
                        }
                    }
                }
                b.append('\n');
            }
            b.append("\n");
        }
        b.append("  Property Filters\n");
        b.append("  ----------------\n");
        if(immutablePropertyFilters.isEmpty()){
            b.append("  No property filters loaded.\n\n");
        }else {
            b.append("  CLASS                         INFO\n\n");
            for (PropertyFilter filter : getPropertyFilters()) {
                b.append("  ");
                appendFormatted(b, filter.getClass().getSimpleName(), 30);
                b.append(removeNewLines(filter.toString()));
                b.append('\n');
            }
            b.append("\n\n");
        }
        b.append("  Property Converters\n");
        b.append("  -------------------\n");
        b.append("  CLASS                         TYPE                          INFO\n\n");
        for(Map.Entry<TypeLiteral<?>, List<PropertyConverter<?>>> converterEntry:getPropertyConverters().entrySet()){
            for(PropertyConverter converter: converterEntry.getValue()){
                b.append("  ");
                appendFormatted(b, converter.getClass().getSimpleName(), 30);
                appendFormatted(b, converterEntry.getKey().getRawType().getSimpleName(), 30);
                b.append(removeNewLines(converter.toString()));
                b.append('\n');
            }
        }
        b.append("\n}");
        return b.toString();
    }

    private void appendFormatted(StringBuilder b, String text, int length) {
        int padding;
        if(text==null){
            b.append("<null>");
            return;
        }
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

}
