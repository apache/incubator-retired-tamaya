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

import org.apache.tamaya.Configuration;
import org.apache.tamaya.builder.spi.PropertyFilter;
import org.apache.tamaya.builder.spi.PropertySource;
import org.apache.tamaya.builder.spi.PropertyValueCombinationPolicy;

import java.util.List;

/**
 * Central SPI for programmatically dealing with the setup of the configuration system.
 * This includes adding and enlisting {@link PropertySource}s,
 * ConfigFilters, etc.
 */
public interface PropertySourceBasedConfiguration extends Configuration {

    /**
     * This method can be used for programmatically adding {@link PropertySource}s.
     * It is not needed for normal 'usage' by end users, but only for Extension Developers!
     *
     * @param propertySourcesToAdd the PropertySources to add
     */
    void addPropertySources(PropertySource... propertySourcesToAdd);

    /**
     * This method returns the current list of registered PropertySources ordered via their ordinal.
     * PropertySources with a lower ordinal come last. The PropertySource with the
     * highest ordinal comes first.
     * If two PropertySources have the same ordinal number they will get sorted
     * using their class name just to ensure the user at least gets the same ordering
     * after a JVM restart, hereby names before are added last.
     * PropertySources are loaded when this method is called the first time, which basically is
     * when the first time configuration is accessed.
     *
     * @return a sorted list of registered PropertySources.  The returned list need not be modifiable
     */
    List<PropertySource> getPropertySources();

    /**
     * Access the current PropertyFilter instances.
     * @return the list of registered PropertyFilters, never null.
     */
    List<PropertyFilter> getPropertyFilters();

    /**
     * Access the {@link PropertyValueCombinationPolicy} used to evaluate the final
     * property values.
     * @return the {@link PropertyValueCombinationPolicy} used, never null.
     */
    PropertyValueCombinationPolicy getPropertyValueCombinationPolicy();

    /**
     * Creates a {@link PropertySourceBasedConfigurationBuilder} preinitialized with the data from this instance.
     * @return a new builder instance, never null.
     */
    PropertySourceBasedConfigurationBuilder toBuilder();

}
