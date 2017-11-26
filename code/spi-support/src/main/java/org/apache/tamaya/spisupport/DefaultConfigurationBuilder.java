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

import org.apache.tamaya.Configuration;
import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.spi.*;
import org.apache.tamaya.spi.ConfigurationBuilder;

import java.util.*;

/**
 * Default implementation of {@link ConfigurationBuilder}.
 */
public class DefaultConfigurationBuilder implements ConfigurationBuilder {

    protected final DefaultConfigurationContextBuilder contextBuilder;

    /**
     * Creates a new builder instance.
     */
    public DefaultConfigurationBuilder() {
        this.contextBuilder = new DefaultConfigurationContextBuilder();
    }

    /**
     * Creates a new builder instance.
     */
    public DefaultConfigurationBuilder(ConfigurationContext context) {
        this.contextBuilder = new DefaultConfigurationContextBuilder(context);
    }

    /**
     * Creates a new builder instance initializing it with the given context.
     * @param configuration the configuration to be used, not null.
     */
    public DefaultConfigurationBuilder(Configuration configuration) {
        this.contextBuilder = new DefaultConfigurationContextBuilder(configuration.getContext());
    }

    /**
     * Allows to set configuration context during unit tests.
     */
    public ConfigurationBuilder setConfiguration(Configuration configuration) {
        this.contextBuilder.setContext(configuration.getContext());
        return this;
    }


    @Override
    public ConfigurationBuilder setContext(ConfigurationContext context) {
        this.contextBuilder.setContext(context);
        return this;
    }

    @Override
    public ConfigurationBuilder addPropertySources(PropertySource... sources){
        this.contextBuilder.addPropertySources(sources);
        return this;
    }

    @Override
    public ConfigurationBuilder addPropertySources(Collection<PropertySource> sources){
        this.contextBuilder.addPropertySources(sources);
        return this;
    }

    public ConfigurationBuilder addDefaultPropertyFilters() {
        this.contextBuilder.addDefaultPropertyFilters();
        return this;
    }

    public ConfigurationBuilder addDefaultPropertySources() {
        this.contextBuilder.addDefaultPropertySources();
        return this;
    }

    public ConfigurationBuilder addDefaultPropertyConverters() {
        this.contextBuilder.addDefaultPropertyConverters();
        return this;
    }

    @Override
    public ConfigurationBuilder removePropertySources(PropertySource... propertySources) {
        this.contextBuilder.removePropertySources(propertySources);
        return this;
    }

    @Override
    public ConfigurationBuilder removePropertySources(Collection<PropertySource> propertySources) {
        this.contextBuilder.removePropertySources(propertySources);
        return this;
    }

    @Override
    public List<PropertySource> getPropertySources() {
        return this.contextBuilder.getPropertySources();
    }

    @Override
    public ConfigurationBuilder increasePriority(PropertySource propertySource) {
        this.contextBuilder.increasePriority(propertySource);
        return this;
    }

    @Override
    public ConfigurationBuilder decreasePriority(PropertySource propertySource) {
        this.contextBuilder.decreasePriority(propertySource);
        return this;
    }

    @Override
    public ConfigurationBuilder highestPriority(PropertySource propertySource) {
        this.contextBuilder.highestPriority(propertySource);
        return this;
    }

    @Override
    public ConfigurationBuilder lowestPriority(PropertySource propertySource) {
        this.contextBuilder.lowestPriority(propertySource);
        return this;
    }

    @Override
    public ConfigurationBuilder addPropertyFilters(PropertyFilter... filters){
        this.contextBuilder.addPropertyFilters(filters);
        return this;
    }

    @Override
    public ConfigurationBuilder addPropertyFilters(Collection<PropertyFilter> filters){
        this.contextBuilder.addPropertyFilters(filters);
        return this;
    }

    @Override
    public ConfigurationBuilder removePropertyFilters(PropertyFilter... filters) {
        this.contextBuilder.removePropertyFilters(filters);
        return this;
    }

    @Override
    public ConfigurationBuilder removePropertyFilters(Collection<PropertyFilter> filters) {
        this.contextBuilder.removePropertyFilters(filters);
        return this;
    }


    @Override
    public <T> ConfigurationBuilder removePropertyConverters(TypeLiteral<T> typeToConvert,
                                                                    PropertyConverter<T>... converters) {
        this.contextBuilder.removePropertyConverters(typeToConvert, converters);
        return this;
    }

    @Override
    public <T> ConfigurationBuilder removePropertyConverters(TypeLiteral<T> typeToConvert,
                                                                    Collection<PropertyConverter<T>> converters) {
        this.contextBuilder.removePropertyConverters(typeToConvert, converters);
        return this;
    }

    @Override
    public ConfigurationBuilder removePropertyConverters(TypeLiteral<?> typeToConvert) {
        this.contextBuilder.removePropertyConverters(typeToConvert);
        return this;
    }


    @Override
    public ConfigurationBuilder setPropertyValueCombinationPolicy(PropertyValueCombinationPolicy combinationPolicy){
        this.contextBuilder.setPropertyValueCombinationPolicy(combinationPolicy);
        return this;
    }

    @Override
    public <T> ConfigurationBuilder addPropertyConverters(TypeLiteral<T> type, PropertyConverter<T>... propertyConverters){
        this.contextBuilder.addPropertyConverters(type, propertyConverters);
        return this;
    }

    @Override
    public <T> ConfigurationBuilder addPropertyConverters(TypeLiteral<T> type, Collection<PropertyConverter<T>> propertyConverters){
        this.contextBuilder.addPropertyConverters(type, propertyConverters);
        return this;
    }

    /**
     * Builds a new configuration based on the configuration of this builder instance.
     *
     * @return a new {@link org.apache.tamaya.Configuration configuration instance},
     *         never {@code null}.
     */
    @Override
    public Configuration build() {
        return new DefaultConfiguration(this.contextBuilder.build());
    }

    @Override
    public ConfigurationBuilder sortPropertyFilter(Comparator<PropertyFilter> comparator) {
        this.contextBuilder.sortPropertyFilter(comparator);
        return this;
    }

    @Override
    public ConfigurationBuilder sortPropertySources(Comparator<PropertySource> comparator) {
        this.contextBuilder.sortPropertySources(comparator);
        return this;
    }

    @Override
    public List<PropertyFilter> getPropertyFilters() {
        return this.contextBuilder.getPropertyFilters();
    }

    @Override
    public Map<TypeLiteral<?>, Collection<PropertyConverter<?>>> getPropertyConverter() {
        return this.contextBuilder.getPropertyConverter();
    }
}
