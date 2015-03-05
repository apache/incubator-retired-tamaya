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
package org.apache.tamaya.modules.builder;

import org.apache.tamaya.ConfigException;
import org.apache.tamaya.Configuration;
import org.apache.tamaya.PropertyConverter;
import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.core.internal.DefaultConfiguration;
import org.apache.tamaya.format.ConfigurationData;
import org.apache.tamaya.format.ConfigurationFormats;
import org.apache.tamaya.format.FlattenedDefaultPropertySource;
import org.apache.tamaya.spi.ConfigurationContext;
import org.apache.tamaya.spi.PropertyFilter;
import org.apache.tamaya.spi.PropertySource;
import org.apache.tamaya.spi.PropertySourceProvider;
import org.apache.tamaya.spi.PropertyValueCombinationPolicy;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;

/* TODO LIST FOR TAMAYA-60
 *
 * - configurable loading of provided PropertyConverter DONE
 * - configurable loading of provided PropertySources DONE
 * - configurable loading of provided PropertySourceProviders DONE
 * - configurable loading of provided PropertyFilters DONE
 * - I can not overhand null in varargs
 * - Rethink the default behaviour for SPI loading
 * - Work on all TODOs for TAMAYA-60
 * - Write JavaDoc
 * - adding sources via URL DONE
 *
 *
 *
 *
 */

/**
 * Builder that allows to build a Configuration completely manually.
 */
public class ConfigurationBuilder {
    /** Builder used to create new ConfigurationContext instances. */
    private ProgrammaticConfigurationContext.Builder contextBuilder = new ProgrammaticConfigurationContext.Builder();

    /**
     * Flag if the config has already been built.
     * Configuration can be built only once
     */
    private boolean built;

    /**
     * Flag if all existing property converter service providers
     * should be loaded if the configuration is build.
     */
    private boolean loadProvidedPropertyConverters = true;

    /**
     * Flag if all existing property source service providers
     * will be loaded if the configuration is build.
     */
    private boolean loadProvidedPropertySources = false;
    private boolean loadProvidedPropertySourceProviders = false;

    private boolean isLoadProvidedPropertyFilters = false;


    /**
     * Allows to set configuration context during unit tests.
     */
    ConfigurationBuilder setConfigurationContext(ConfigurationContext configurationContext) {
        contextBuilder.setConfigurationContext(configurationContext);
        return this;
    }

    public ConfigurationBuilder addPropertySource(URL url) {
        try {
            ConfigurationData data = getConfigurationDataFromURL(url);

            FlattenedDefaultPropertySource propertySource = new FlattenedDefaultPropertySource(data);
            addPropertySources(propertySource);
        } catch (IOException e) {
            throw new ConfigException("Failed to read " + url.toString(), e);
        }

        return this;
    }

    private ConfigurationData getConfigurationDataFromURL(URL url) throws IOException {
        ConfigurationData data = ConfigurationFormats.readConfigurationData(url);

        if (null == data) {
            String mesg = format("No configuration format found which is able " +
                                 "to read properties from %s.", url.toString());

            throw new ConfigException(mesg);
        }

        return data;
    }

    public ConfigurationBuilder addPropertySource(URL url, URL... urls) {
        Stream.of(Collections.singletonList(url), Arrays.asList(urls))
              .flatMap(Collection::stream)
              .filter(entry -> entry != null)
              .collect(Collectors.toList())
              .forEach(this::addPropertySource);

        return this;
    }

    public ConfigurationBuilder addPropertySource(String url, String... urls) {
        Stream.of(Collections.singletonList(url), Arrays.asList(urls))
              .flatMap(Collection::stream)
              .filter(entry -> entry != null)
              .map(new StringToURLMapper())
              .collect(Collectors.toList())
              .forEach(this::addPropertySource);

        return this;
    }

    public ConfigurationBuilder addPropertySources(PropertySource... sources){
        checkBuilderState();

        contextBuilder.addPropertySources(Objects.requireNonNull(sources));
        return this;
    }

    private void checkBuilderState() {
        if (built) {
            throw new IllegalStateException("Configuration has already been build.");
        }
    }

    public ConfigurationBuilder addPropertySourceProviders(PropertySourceProvider... propertySourceProviders){
        contextBuilder.addPropertySourceProviders(propertySourceProviders);
        return this;
    }

    public ConfigurationBuilder addPropertyFilters(PropertyFilter... propertyFilters){
        Objects.requireNonNull(propertyFilters);

        contextBuilder.addPropertyFilters(propertyFilters);
        return this;
    }

    public ConfigurationBuilder setPropertyValueCombinationPolicy(PropertyValueCombinationPolicy propertyValueCombinationPolicy){
        contextBuilder.setPropertyValueCombinationPolicy(propertyValueCombinationPolicy);
        return this;
    }

    public <T> ConfigurationBuilder addPropertyConverter(Class<T> type, PropertyConverter<T> propertyConverter) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(propertyConverter);

        return addPropertyConverter(TypeLiteral.of(type), propertyConverter);
    }

    public <T> ConfigurationBuilder addPropertyConverter(TypeLiteral<T> type, PropertyConverter<T> propertyConverter){
        Objects.requireNonNull(type);
        Objects.requireNonNull(propertyConverter);

        contextBuilder.addPropertyConverter(type, propertyConverter);
        return this;
    }


    public boolean isPropertyConverterLoadingEnabled() {
        return loadProvidedPropertyConverters;
    }

    /**
     * Enables the loading of all {@link org.apache.tamaya.PropertyConverter}
     * service providers.
     *
     * @see org.apache.tamaya.PropertyConverter
     * @see #disableProvidedPropertyConverters()
     */
    public ConfigurationBuilder enableProvidedPropertyConverters() {
        checkBuilderState();

        loadProvidedPropertyConverters = true;

        return this;
    }

    /**
     * Disables the loading of all {@link org.apache.tamaya.PropertyConverter}
     * service providers.
     *
     * @see org.apache.tamaya.PropertyConverter
     * @see #enableProvidedPropertyConverters()
     */
    public ConfigurationBuilder disableProvidedPropertyConverters() {
        checkBuilderState();

        loadProvidedPropertyConverters = false;

        return this;
    }


    public ConfigurationBuilder enableProvidedPropertySources() {
        checkBuilderState();

        loadProvidedPropertySources = true;

        return this;
    }

    public boolean isPropertySourcesLoadingEnabled() {
        return loadProvidedPropertySources;
    }


    public boolean isPropertyFilterLoadingEnabled() {
        return isLoadProvidedPropertyFilters;
    }


    public ConfigurationBuilder enabledProvidedPropertyFilters() {
        checkBuilderState();

        isLoadProvidedPropertyFilters = true;

        return this;
    }

    public ConfigurationBuilder disableProvidedPropertyFilters() {
        checkBuilderState();

        isLoadProvidedPropertyFilters = false;

        return this;
    }

    public ConfigurationBuilder disableProvidedPropertySources() {
        checkBuilderState();

        loadProvidedPropertySources = false;

        return this;
    }

    public ConfigurationBuilder enableProvidedPropertySourceProviders() {
        checkBuilderState();

        loadProvidedPropertySourceProviders = true;

        return this;
    }

    public ConfigurationBuilder disableProvidedPropertySourceProviders() {
        checkBuilderState();

        loadProvidedPropertySourceProviders = false;

        return this;
    }

    public boolean isPropertySourceProvidersLoadingEnabled() {
        return loadProvidedPropertySourceProviders;
    }

    //X TODO think on a functonality/API for using the default PropertyConverters and use the configured ones here
    //X TODO as overrides used first.


    /**
     * Creates a new Configuration instance based on the configured data.
     * @return a new Configuration instance.
     */
    public Configuration build() {
        checkBuilderState();

        built = true;

        contextBuilder.loadProvidedPropertyConverters(isPropertyConverterLoadingEnabled());
        contextBuilder.loadProvidedPropertySources(isPropertySourcesLoadingEnabled());
        contextBuilder.loadProvidedPropertySourceProviders(isPropertySourceProvidersLoadingEnabled());
        contextBuilder.loadProvidedPropertyFilters(isLoadProvidedPropertyFilters);

        return new DefaultConfiguration(contextBuilder.build());
    }

    /**
     * Mapper to map a URL given as string to an URL instance.
     */
    private static class StringToURLMapper implements Function<String, URL> {
        @Override
        public URL apply(String u) {
            try {
                return new URL(u);
            } catch (MalformedURLException e) {
                throw new ConfigException(u + " is not a valid URL", e);
            }
        }
    }
}
