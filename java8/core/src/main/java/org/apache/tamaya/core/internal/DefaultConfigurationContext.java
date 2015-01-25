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

import org.apache.tamaya.spi.PropertyFilter;
import org.apache.tamaya.spi.PropertySource;
import org.apache.tamaya.spi.ServiceContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Default Implementation of a simple ConfigurationContext.
 */
public class DefaultConfigurationContext extends BaseConfigurationContext {

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

        setImmutablePropertySources(Collections.unmodifiableList(propertySources));

        // as next step we pick up the PropertyFilters pretty much the same way
        List<PropertyFilter> propertyFilters = new ArrayList<>();
        propertyFilters.addAll(ServiceContext.getInstance().getServices(PropertyFilter.class));
        Collections.sort(propertyFilters, this::comparePropertyFilters);

        setImmutablePropertyFilters(Collections.unmodifiableList(propertyFilters));
    }

}
