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
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;

/**
 * <p>Builder class used for building a configuration manually without relying
 * only on the Service Provider Interface API.</p>
 *
 * <p><strong>Features of the builder</strong></p>
 *
 * <ol>
 *   <li>Adding of property converters manually</li>
 *   <li>Adding of property sources directly</li>
 *   <li>Adding of property sources via URL</li>
 *   <li>Adding of property source providers directly</li>
 *   <li>Enabling and disabling of via SPI mechanism provided resources as converters,
 *       property sources, etc.</li>
 * </ol>
 *
 * <p><strong>Example</strong></p>
 *
 * <pre>{@code ConfigurationBuilder builder = new ConfigurationBuilder();
 * builder.disableProvidedPropertySources()           // Do not load provided property
 *        .disableProvidedPropertySourceProviders()   // sources and providers automatically
 *        .addPropertySource("file:/etc/conf.properties"); // Load properties from conf.properties
 *
 * Configuration config = builder.build();
 * }</pre>
 *
 * <p><strong>Support for configuration formats</strong></p>
 *
 * The configuration builder allows you to add property resources
 * via a URL, as shown in the code example above, without implementing
 * a {@link org.apache.tamaya.spi.PropertySource PropertySource} or providing an
 * instance of a {@link org.apache.tamaya.spi.PropertySource PropertySource}.
 * If a property resource in
 * a specific format can be added to configuration builder or not depends
 * on the available implementations of
 * {@link org.apache.tamaya.format.ConfigurationFormat} in the classpath.
 * Which formats are available can be checked via
 * {@link org.apache.tamaya.format.ConfigurationFormats#getFormats()}.
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
     * Creates a new builder instance.
     */
    public ConfigurationBuilder() {
    }

    /**
     * Allows to set configuration context during unit tests.
     */
    ConfigurationBuilder setConfigurationContext(ConfigurationContext configurationContext) {
        contextBuilder.setConfigurationContext(configurationContext);
        return this;
    }

    /**
     * Adds one resources with properties in an arbitrary format
     * to the configuration to be build.
     *
     * <p>If a specific format is supported depends on the available
     * {@link org.apache.tamaya.format.ConfigurationFormat} implementations.</p>
     *
     * <pre>{@code URL resource = new URL("file:/etc/service/config.json");
     *
     * builder.addPropertySources(resource);}
     * </pre>
     *
     * @param url resource with properties for the the configuration to be build.
     *
     * @return the builder instance currently used
     *
     * @see org.apache.tamaya.format.ConfigurationFormat
     * @see org.apache.tamaya.format.ConfigurationFormats#getFormats()
     */
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

    protected ConfigurationData getConfigurationDataFromURL(URL url) throws IOException {
        ConfigurationData data = ConfigurationFormats.readConfigurationData(url);

        if (null == data) {
            String mesg = format("No configuration format found which is able " +
                                 "to read properties from %s.", url.toString());

            throw new ConfigException(mesg);
        }

        return data;
    }

    /**
     * Adds one or more resources with properties in an arbitrary format
     * to the configuration to be build.
     *
     * <p>If a specific format is supported depends on the available
     * {@link org.apache.tamaya.format.ConfigurationFormat} implementations.</p>
     *
     *<pre>{@code URL first = new URL("file:/etc/service/config.json");
     * URL second = new URL("file:/etc/defaults/values.properties");
     *
     * builder.addPropertySources(first, second);}
     *</pre>
     *
     * @param urls list of resources with properties for the configuration to be
     *             build.
     *
     * @return the builder instance currently used
     *
     * @see org.apache.tamaya.format.ConfigurationFormat
     * @see org.apache.tamaya.format.ConfigurationFormats#getFormats()
     */
    public ConfigurationBuilder addPropertySource(URL... urls) {
        Stream.of(Arrays.asList(urls))
              .flatMap(Collection::stream)
              .filter(entry -> entry != null)
              .collect(Collectors.toList())
              .forEach(this::addPropertySource);

        return this;
    }


    /**
     * Adds one or more resources with properties in an arbitrary format
     * to the configuration to be build.
     *
     * <p>If a specific format is supported depends on the available
     * {@link org.apache.tamaya.format.ConfigurationFormat} implementations.</p>
     *
     *<pre>{@code builder.addPropertySources("file:/etc/service/config.json",
     *                            "file:/etc/defaults/values.properties");}
     *</pre>
     *
     * @param urls list of resources with properties for the configuration to be
     *             build.
     *
     * @return the builder instance currently used
     *
     * @see org.apache.tamaya.format.ConfigurationFormat
     * @see org.apache.tamaya.format.ConfigurationFormats#getFormats()
     */
    public ConfigurationBuilder addPropertySource(String... urls) {
        Stream.of(Arrays.asList(urls))
              .flatMap(Collection::stream)
              .filter(entry -> entry != null)
              .map(new StringToURLMapper())
              .collect(Collectors.toList())
              .forEach(this::addPropertySource);

        return this;
    }

    /**
     * Adds one or more property source instances to the configuration to be build.
     *
     *<pre>{@code PropertySource first = new CustomPropertySource();
     * PropertySource second = new YetAnotherPropertySource();
     *
     * builder.addPropertySources(first, second)};
     *</pre>
     *
     * @param sources list of property source instances with properties for the
     *                configuration to be build.
     *
     * @return the builder instance currently used
     *
     * @see org.apache.tamaya.spi.PropertySource
     */
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

    /**
     * Adds one or more property source provider instances to the configuration to be build.
     *
     * <pre>{@code PropertySourceProvider jc = new JavaConfigurationProvider();
     *
     * builder.addPropertySources(jc)};
     * </pre>
     *
     * @param providers list of property source provider instances each providing a set
     *                  of property source instances for the configuration to be build.
     *
     * @return the builder instance currently used
     *
     * @see org.apache.tamaya.spi.PropertySourceProvider
     */
    public ConfigurationBuilder addPropertySourceProviders(PropertySourceProvider... providers){
        contextBuilder.addPropertySourceProviders(providers);
        return this;
    }

    /**
     * Adds one or more property filter instances to the configuration to be build.
     *
     * <pre>{@code PropertyFilter quoteReplacingFilter = new QuoteFilter();
     * PropertyFilter commaRemovingFilter = new CommaFilter();
     *
     * builder.addPropertyFilters(commaRemovingFilter, quoteReplacingFilter)};
     * </pre>
     *
     * @param filters list of property filter instances which should be applied
     *                to the properties of the configuration to be build.
     *
     * @return the builder instance currently used
     *
     * @see org.apache.tamaya.spi.PropertyFilter
     * @see #disableProvidedPropertyFilters()
     * @see #enabledProvidedPropertyFilters()
     */
    public ConfigurationBuilder addPropertyFilters(PropertyFilter... filters){
        Objects.requireNonNull(filters);

        contextBuilder.addPropertyFilters(filters);
        return this;
    }


    /**
     * @return the builder instance currently used
     */
    public ConfigurationBuilder setPropertyValueCombinationPolicy(PropertyValueCombinationPolicy propertyValueCombinationPolicy){
        contextBuilder.setPropertyValueCombinationPolicy(propertyValueCombinationPolicy);
        return this;
    }

    /**
     * Adds a property converter for the a given type to the configuration to
     * be build.
     *
     * <pre>{@code PropertyConverter<MyType> converter = value -> new MyType(value, 42);
     *
     * builder.addPropertyConverter(MyType.class, converter}
     * </pre>
     *
     * @param type the required target type the converter should be applied to
     * @param converter the converter to be used to convert the string property
     *                  to the given target type.
     *
     * @return the builder instance currently used
     *
     * @see org.apache.tamaya.PropertyConverter
     * @see #enableProvidedPropertyConverters()
     * @see #disableProvidedPropertyConverters()
     */
    public <T> ConfigurationBuilder addPropertyConverter(Class<T> type, PropertyConverter<T> converter) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(converter);

        return addPropertyConverter(TypeLiteral.of(type), converter);
    }

    /**
     * @return the builder instance currently used
     */
    public <T> ConfigurationBuilder addPropertyConverter(TypeLiteral<T> type, PropertyConverter<T> propertyConverter){
        Objects.requireNonNull(type);
        Objects.requireNonNull(propertyConverter);

        contextBuilder.addPropertyConverter(type, propertyConverter);
        return this;
    }

    /**
     * Checks if the automatic loading of all {@link org.apache.tamaya.PropertyConverter
     * PropertyConverter} service providers is enabled or disabled.
     *
     * @return {@code true} if the automatic loading is enabled,
     *         otherwise {@code false}.
     *
     * @see #enableProvidedPropertyConverters()
     * @see #disableProvidedPropertyConverters()
     * @see #addPropertyConverter(Class, org.apache.tamaya.PropertyConverter)
     * @see #addPropertyConverter(org.apache.tamaya.TypeLiteral, org.apache.tamaya.PropertyConverter)
     */
    public boolean isPropertyConverterLoadingEnabled() {
        return loadProvidedPropertyConverters;
    }

    /**
     * Enables the loading of all {@link org.apache.tamaya.PropertyConverter}
     * service providers.
     *
     * @return the builder instance currently used
     *
     * @see org.apache.tamaya.PropertyConverter
     * @see #disableProvidedPropertyConverters()
     * @see #enableProvidedPropertyConverters()
     */
    public ConfigurationBuilder enableProvidedPropertyConverters() {
        checkBuilderState();

        loadProvidedPropertyConverters = true;

        return this;
    }

    /**
     * Disables the automatic loading of all {@link org.apache.tamaya.PropertyConverter}
     * service providers.
     *
     * @return the builder instance currently used
     *
     * @see org.apache.tamaya.PropertyConverter
     * @see #enableProvidedPropertyConverters()
     * @see #addPropertyConverter(Class, org.apache.tamaya.PropertyConverter)
     */
    public ConfigurationBuilder disableProvidedPropertyConverters() {
        checkBuilderState();

        loadProvidedPropertyConverters = false;

        return this;
    }


    /**
     * Enables the automatic loading of all {@link org.apache.tamaya.spi.PropertySource}
     * service providers.
     *
     * @return the builder instance currently used
     *
     * @see org.apache.tamaya.spi.PropertySource
     * @see #disableProvidedPropertySources()
     */
    public ConfigurationBuilder enableProvidedPropertySources() {
        checkBuilderState();

        loadProvidedPropertySources = true;

        return this;
    }

    /**
     * Checks if the automatic loading of all {@link org.apache.tamaya.spi.PropertySource
     * PropertySource} service providers is enabled or disabled.
     *
     * @return {@code true} if the automatic loading is enabled,
     *         otherwise {@code false}.
     */
    public boolean isPropertySourcesLoadingEnabled() {
        return loadProvidedPropertySources;
    }


    /**
     * Checks if the automatic loading of all {@link org.apache.tamaya.spi.PropertyFilter
     * PropertyFilter} service providers is enabled or disabled.
     *
     * @return {@code true} if the automatic loading is enabled,
     *         otherwise {@code false}.
     */
    public boolean isPropertyFilterLoadingEnabled() {
        return isLoadProvidedPropertyFilters;
    }

    /**
     * Enables the automatic loading of all {@link org.apache.tamaya.spi.PropertyFilter}
     * service providers.
     *
     * @return the builder instance currently used
     *
     * @see org.apache.tamaya.spi.PropertyFilter
     * @see #disableProvidedPropertyFilters()
     * @see #addPropertyFilters(org.apache.tamaya.spi.PropertyFilter...)
     */
    public ConfigurationBuilder enabledProvidedPropertyFilters() {
        checkBuilderState();

        isLoadProvidedPropertyFilters = true;

        return this;
    }

    /**
     * Disables the automatic loading of all {@link org.apache.tamaya.spi.PropertyFilter}
     * service providers.
     *
     * @return the builder instance currently used
     *
     * @see org.apache.tamaya.spi.PropertyFilter
     * @see #enabledProvidedPropertyFilters()
     * @see #addPropertyFilters(org.apache.tamaya.spi.PropertyFilter...)
     *
     * @return the builder instance currently used
     */
    public ConfigurationBuilder disableProvidedPropertyFilters() {
        checkBuilderState();

        isLoadProvidedPropertyFilters = false;

        return this;
    }

    /**
     * Disables the automatic loading of all {@link org.apache.tamaya.spi.PropertySource}
     * service providers.
     *
     * @return the builder instance currently used
     *
     * @see org.apache.tamaya.spi.PropertySource
     * @see #enableProvidedPropertySources()
     */
    public ConfigurationBuilder disableProvidedPropertySources() {
        checkBuilderState();

        loadProvidedPropertySources = false;

        return this;
    }

    /**
     * Enables the automatic loading of {@link org.apache.tamaya.spi.PropertySourceProvider
     * property source providers} provided via the SPI API.
     *
     * @return the builder instance currently used
     *
     * @see org.apache.tamaya.spi.PropertySourceProvider
     * @see
     */
    public ConfigurationBuilder enableProvidedPropertySourceProviders() {
        checkBuilderState();

        loadProvidedPropertySourceProviders = true;

        return this;
    }

    /**
     * Disables the automatic loading of {@link org.apache.tamaya.spi.PropertySourceProvider
     * property source providers} provided via the SPI API.
     *
     * @return the builder instance currently used
     */
    public ConfigurationBuilder disableProvidedPropertySourceProviders() {
        checkBuilderState();

        loadProvidedPropertySourceProviders = false;

        return this;
    }

    /**
     * Checks if the automatic loading of {@link org.apache.tamaya.spi.PropertySourceProvider
     * PropertySourceProviders} is enabled or disabled.
     *
     * @return {@code true} if the automatic loading is enabled,
     *         otherwise {@code false}.
     */
    public boolean isPropertySourceProvidersLoadingEnabled() {
        return loadProvidedPropertySourceProviders;
    }

    //X TODO think on a functonality/API for using the default PropertyConverters and use the configured ones here
    //X TODO as overrides used first.


    /**
     * Builds a new configuration based on the configuration of this builder instance.
     *
     * @return a new {@link org.apache.tamaya.Configuration configuration instance},
     *         never {@code null}.
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
