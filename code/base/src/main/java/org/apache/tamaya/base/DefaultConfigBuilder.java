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
package org.apache.tamaya.base;

import org.apache.tamaya.base.configsource.*;
import org.apache.tamaya.base.convert.ConverterManager;
import org.apache.tamaya.base.filter.Filter;
import org.apache.tamaya.base.filter.FilterManager;

import javax.config.Config;
import javax.config.spi.ConfigSource;
import javax.config.spi.Converter;
import java.lang.reflect.Type;
import java.util.*;
import javax.config.spi.ConfigBuilder;

/**
 * Default implementation of {@link TamayaConfigBuilder}.
 */
public class DefaultConfigBuilder implements TamayaConfigBuilder {

    protected final ConfigSourceManager configSourceManager = new ConfigSourceManager();
    protected final ConverterManager converterManager = new ConverterManager();
    protected final FilterManager filterManager = new FilterManager();

    /**
     * Creates a new builder instance.
     */
    public DefaultConfigBuilder() {
    }

    /**
     * Creates a new builder instance.
     */
    public DefaultConfigBuilder(ConfigContext context) {
        this.configSourceManager.addSources(context.getConfigSources());
        this.configSourceManager.setConfigValueCombinationPolicy(context.getConfigValueCombinationPolicy());
        this.filterManager.addFilter(context.getFilters());
        context.getConverters().forEach((t,c) -> converterManager.addConverter(t, Collection.class.cast(c)));
    }

    /**
     * Creates a new builder instance initializing it with the given context.
     * @param contextSupplier the context supplier to be used, not null.
     */
    public DefaultConfigBuilder(ConfigContextSupplier contextSupplier) {
        this(((ConfigContextSupplier)contextSupplier).getConfigContext());
    }

//    /**
//     * Allows to set configuration context during unit tests.
//     */
//    @Override
//    public TamayaConfigBuilder setConfiguration(Config configuration) {
//        this.contextBuilder.withContext(((DefaultConfig)configuration).getConfigContext());
//        return this;
//    }


    @Override
    public TamayaConfigBuilder withSources(ConfigSource... sources){
        this.configSourceManager.addSources(sources);
        return this;
    }

    @Override
    public <T> TamayaConfigBuilder withConverter(Class<T> type, Converter<T> converter) {
        this.converterManager.addConverter(type, converter);
        return this;
    }

    @Override
    public TamayaConfigBuilder withConverters(Converter<?>... converters) {
        this.converterManager.addConverters(converters);
        return this;
    }

    @Override
    public TamayaConfigBuilder withConverters(Collection<Converter<?>> converters) {
        this.converterManager.addConverters(Collection.class.cast(converters));
        return this;
    }

    @Override
    public TamayaConfigBuilder withSources(Collection<ConfigSource> sources){
        this.configSourceManager.addSources(sources);
        return this;
    }

    @Override
    public TamayaConfigBuilder addDiscoveredFilters() {
        this.filterManager.addDefaultFilters();
        return this;
    }

    @Override
    public TamayaConfigBuilder addDefaultSources() {
        this.configSourceManager.addSources(
                new EnvironmentConfigSource(),
                new JavaConfigurationConfigSource(),
                new CLIConfigSource(),
                new SystemConfigSource());
        return this;
    }

    @Override
    public TamayaConfigBuilder addDiscoveredSources() {
        this.configSourceManager.addDiscoveredSources();
        return this;
    }

    @Override
    public TamayaConfigBuilder addDiscoveredConverters() {
        this.converterManager.addDiscoveredConverters();
        return this;
    }

    @Override
    public TamayaConfigBuilder forClassLoader(ClassLoader loader) {
        this.configSourceManager.setClassloader(loader);
        this.filterManager.setClassloader(loader);
        this.converterManager.setClassloader(loader);
        return this;
    }

    @Override
    public TamayaConfigBuilder removeSources(ConfigSource... propertySources) {
        this.configSourceManager.removeSources(propertySources);
        return this;
    }

    @Override
    public TamayaConfigBuilder removeSources(Collection<ConfigSource> propertySources) {
        this.configSourceManager.removeSources(propertySources);
        return this;
    }

    @Override
    public List<ConfigSource> getSources() {
        return this.configSourceManager.getSources();
    }

    @Override
    public TamayaConfigBuilder increasePriority(ConfigSource propertySource) {
        this.configSourceManager.increasePriority(propertySource);
        return this;
    }

    @Override
    public TamayaConfigBuilder decreasePriority(ConfigSource propertySource) {
        this.configSourceManager.decreasePriority(propertySource);
        return this;
    }

    @Override
    public TamayaConfigBuilder highestPriority(ConfigSource propertySource) {
        this.configSourceManager.highestPriority(propertySource);
        return this;
    }

    @Override
    public TamayaConfigBuilder lowestPriority(ConfigSource propertySource) {
        this.configSourceManager.lowestPriority(propertySource);
        return this;
    }

    @Override
    public TamayaConfigBuilder withFilters(Filter... filters){
        this.filterManager.addFilter(filters);
        return this;
    }

    @Override
    public TamayaConfigBuilder withFilters(Collection<Filter> filters){
        this.filterManager.addFilter(filters);
        return this;
    }

    @Override
    public TamayaConfigBuilder removeFilters(Filter... filters) {
        this.filterManager.removeFilters(filters);
        return this;
    }

    @Override
    public TamayaConfigBuilder removeFilters(Collection<Filter> filters) {
        this.filterManager.removeFilters(filters);
        return this;
    }


    @Override
    public <T> TamayaConfigBuilder removeConverters(Class<T> typeToConvert,
                                                    Converter<T>... converters) {
        this.converterManager.removeConverters(typeToConvert, converters);
        return this;
    }

    @Override
    public <T> TamayaConfigBuilder removeConverters(Class<T> typeToConvert,
                                                    Collection<Converter<T>> converters) {
        this.converterManager.removeConverters(typeToConvert, Collection.class.cast(converters));
        return this;
    }

    @Override
    public <T> TamayaConfigBuilder removeConverters(Class<T> typeToConvert) {
        this.converterManager.removeConverters(typeToConvert);
        return this;
    }


    @Override
    public TamayaConfigBuilder withPropertyValueCombinationPolicy(ConfigValueCombinationPolicy combinationPolicy){
        this.configSourceManager.setConfigValueCombinationPolicy(combinationPolicy);
        return this;
    }

    @Override
    public <T> TamayaConfigBuilder withConverters(Class<T> type, Converter<T>... converters){
        this.converterManager.addConverter(type, converters);
        return this;
    }

    @Override
    public <T> TamayaConfigBuilder withConverters(Class<T> type, Collection<Converter<T>> converters){
        this.converterManager.addConverter(type, Collection.class.cast(converters));
        return this;
    }

    @Override
    public <T> ConfigBuilder withConverter(Class<T> type, int i, Converter<T> cnvrtr) {
        this.converterManager.addConverter(type, cnvrtr);
        return this;
    }

    @Override
    public TamayaConfigBuilder sortFilter(Comparator<Filter> comparator) {
        this.filterManager.sortFilter(comparator);
        return this;
    }

    @Override
    public TamayaConfigBuilder sortSources(Comparator<ConfigSource> comparator) {
        this.configSourceManager.sortSources(comparator);
        return this;
    }

    @Override
    public List<Filter> getFilters() {
        return this.filterManager.getFilters();
    }

    @Override
    public Map<Type, List<Converter>> getConverter() {
        return this.converterManager.getConverters();
    }

    /**
     * Builds a new configuration based on the configuration of this builder instance.
     *
     * @return a new {@link Config configuration instance},
     *         never {@code null}.
     */
    @Override
    public Config build() {
        return new DefaultConfig(getConfigContext());
    }

    @Override
    public ConfigContext getConfigContext() {
        return new ConfigContext() {
            @Override
            public List<ConfigSource> getConfigSources() {
                return configSourceManager.getSources();
            }

            @Override
            public List<Filter> getFilters() {
                return filterManager.getFilters();
            }

            @Override
            public Map<Type, List<Converter>> getConverters() {
                return converterManager.getConverters();
            }

            @Override
            public ConfigValueCombinationPolicy getConfigValueCombinationPolicy() {
                return configSourceManager.getConfigValueCombinationPolicy();
            }
        };
    }

}
