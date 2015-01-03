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

import org.apache.tamaya.spi.ConfigurationContext;
import org.apache.tamaya.spi.PropertyConverter;
import org.apache.tamaya.spi.PropertyFilter;
import org.apache.tamaya.spi.PropertySource;
import org.apache.tamaya.spi.PropertySourceProvider;
import org.apache.tamaya.spi.ServiceContext;

import javax.annotation.Priority;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.StampedLock;

/**
 * Default Implementation of a simple ConfigurationContext.
 */
public class DefaultConfigurationContext implements ConfigurationContext {

    private PropertyConverterManager propertyConverterManager = new PropertyConverterManager();

    private List<PropertySource> propertySources = new ArrayList<>();
    private List<PropertySourceProvider> propertySourceProviders = new ArrayList<>();
    private List<PropertyFilter> propertyFilters = new ArrayList<>();
    private boolean loaded = false;

    private StampedLock propertySourceLock = new StampedLock();


    @Override
    public void addPropertySources(PropertySource... propertySourcesToAdd) {
        Lock writeLock = propertySourceLock.asWriteLock();
        try {
            writeLock.lock();
            List<PropertySource> newPropertySources = new ArrayList<>(this.propertySources);
            newPropertySources.addAll(Arrays.asList(propertySourcesToAdd));
            Collections.sort(newPropertySources, this::comparePropertySources);
            this.propertySources = newPropertySources;
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Order property source reversely, the most important come first.
     *
     * @param source1
     * @param source2
     * @return
     */
    private int comparePropertySources(PropertySource source1, PropertySource source2) {
        if (source1.getOrdinal() < source2.getOrdinal()) {
            return 1;
        } else if (source1.getOrdinal() > source2.getOrdinal()) {
            return -1;
        } else {
            return source2.getClass().getName().compareTo(source1.getClass().getName());
        }
    }

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
        if (!loaded) {
            Lock writeLock = propertySourceLock.asWriteLock();
            try {
                writeLock.lock();
                if (!loaded) {
                    this.propertySources.addAll(ServiceContext.getInstance().getServices(PropertySource.class));
                    this.propertySourceProviders.addAll(ServiceContext.getInstance().getServices(PropertySourceProvider.class));
                    for (PropertySourceProvider prov : this.propertySourceProviders) {
                        try {
                            this.propertySources.addAll(prov.getPropertySources());
                        } catch (Exception e) {
                            //X TODO Log!
                        }
                    }
                    Collections.sort(this.propertySources, this::comparePropertySources);
                    this.propertyFilters.addAll(ServiceContext.getInstance().getServices(PropertyFilter.class));
                    Collections.sort(this.propertyFilters, this::comparePropertyFilters);
                    loaded = true;
                }
            } finally {
                writeLock.unlock();
            }
        }
        Lock readLock = propertySourceLock.asReadLock();
        try {
            readLock.lock();
            return Collections.unmodifiableList(propertySources);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public <T> void addPropertyConverter(Class<T> typeToConvert, PropertyConverter<T> propertyConverter) {
        propertyConverterManager.register(typeToConvert, propertyConverter);
    }

    @Override
    public Map<Class<?>, List<PropertyConverter<?>>> getPropertyConverters() {
        return propertyConverterManager.getPropertyConverters();
    }

    @Override
    public <T> List<PropertyConverter<T>> getPropertyConverters(Class<T> targetType) {
        return propertyConverterManager.getPropertyConverters(targetType);
    }

    @Override
    public List<PropertyFilter> getPropertyFilters() {
        return Collections.unmodifiableList(this.propertyFilters);
    }

}
