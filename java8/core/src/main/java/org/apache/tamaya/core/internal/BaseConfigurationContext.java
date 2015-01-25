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

import org.apache.tamaya.PropertyConverter;
import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.spi.ConfigurationContext;
import org.apache.tamaya.spi.PropertyFilter;
import org.apache.tamaya.spi.PropertySource;
import org.apache.tamaya.spi.PropertySourceProvider;
import org.apache.tamaya.spi.ServiceContext;

import javax.annotation.Priority;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.StampedLock;

/**
 * Base class for all implementations of {@code ConfigurationContext}.
 *
 * @see ConfigurationContext
 */
public abstract class BaseConfigurationContext
    implements ConfigurationContext {

    /**
     * Subcomponent handling {@link org.apache.tamaya.PropertyConverter}
     * instances.
     */
    private PropertyConverterManager propertyConverterManager = new PropertyConverterManager();

    /**
     * The current unmodifiable list of loaded {@link org.apache.tamaya.spi.PropertySource}
     * instances.
     */
    private List<PropertySource> immutablePropertySources;

    /**
     * The current unmodifiable list of loaded {@link org.apache.tamaya.spi.PropertyFilter}
     * instances.
     */
    private List<PropertyFilter> immutablePropertyFilters;

    /**
     * Lock for internal synchronization.
     */
    private StampedLock propertySourceLock = new StampedLock();

    /**
     * Pick up all {@link org.apache.tamaya.spi.PropertySourceProvider}s and
     * return all the {@link org.apache.tamaya.spi.PropertySource}s they like to
     * register.
     */
    protected Collection<? extends PropertySource> evaluatePropertySourcesFromProviders() {
        List<PropertySource> propertySources = new ArrayList<>();
        List<PropertySourceProvider> propertySourceProviders = ServiceContext.getInstance().getServices(PropertySourceProvider.class);
        for (PropertySourceProvider propertySourceProvider : propertySourceProviders) {
            propertySources.addAll(propertySourceProvider.getPropertySources());
        }

        return propertySources;
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
    protected int comparePropertySources(PropertySource source1, PropertySource source2) {
        if (source1.getOrdinal() < source2.getOrdinal()) {
            return 1;
        } else if (source1.getOrdinal() > source2.getOrdinal()) {
            return -1;
        } else {
            return source2.getClass().getName().compareTo(source1.getClass().getName());
        }
    }

    /**
     * Compare 2 filters for ordering the filter chain.
     *
     * @param filter1 the first filter
     * @param filter2 the second filter
     * @return the comparison result
     */
    protected int comparePropertyFilters(PropertyFilter filter1, PropertyFilter filter2) {
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
    public <T> void addPropertyConverter(TypeLiteral<T> typeToConvert, PropertyConverter<T> converter) {
        propertyConverterManager.register(typeToConvert, converter);
    }

    @Override
    public Map<TypeLiteral<?>, List<PropertyConverter<?>>> getPropertyConverters() {
        return propertyConverterManager.getPropertyConverters();
    }

    @Override
    public <T> List<PropertyConverter<T>> getPropertyConverters(TypeLiteral<T> type) {
        return propertyConverterManager.getPropertyConverters(type);
    }

    @Override
    public List<PropertyFilter> getPropertyFilters() {
        return immutablePropertyFilters;
    }

    protected void setImmutablePropertySources(List<PropertySource> sources) {
        immutablePropertySources = sources;
    }

    protected void setImmutablePropertyFilters(List<PropertyFilter> filter) {
        immutablePropertyFilters = filter;
    }
}
