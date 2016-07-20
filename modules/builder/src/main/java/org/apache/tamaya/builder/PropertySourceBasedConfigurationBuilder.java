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
import org.apache.tamaya.builder.propertysource.SimplePropertySource;
import org.apache.tamaya.builder.spi.*;
import org.apache.tamaya.format.ConfigurationData;
import org.apache.tamaya.format.ConfigurationFormats;
import org.apache.tamaya.format.FlattenedDefaultPropertySource;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

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
 * The configuration builder allows you to put property resources
 * via a URL, as shown in the code example above, without implementing
 * a {@link PropertySource PropertySource} or providing an
 * instance of a {@link PropertySource PropertySource}.
 * If a property resource in
 * a specific format can be added to configuration builder or not depends
 * on the available implementations of
 * {@link org.apache.tamaya.format.ConfigurationFormat} in the classpath.
 * Which formats are available can be checked via
 * {@link org.apache.tamaya.format.ConfigurationFormats#getFormats()}.
 */
public class PropertySourceBasedConfigurationBuilder {
    private static final Logger LOG = Logger.getLogger(PropertySourceBasedConfigurationBuilder.class.getName());
    /**
     * The current unmodifiable list of loaded {@link PropertySource} instances.
     */
    private final List<PropertySource> propertySources = new ArrayList<>();

    /**
     * The current unmodifiable list of loaded {@link PropertyFilter} instances.
     */
    private final List<PropertyFilter> propertyFilters = new ArrayList<>();

    /**
     * The overriding policy used when combining PropertySources registered to evalute the final configuration
     * values.
     */
    private PropertyValueCombinationPolicy propertyValueCombinationPolicy =
            PropertyValueCombinationPolicy.DEFAULT_OVERRIDING_COLLECTOR;

    private boolean loadProvidedPropertySources;
    private boolean loadProvidedPropertySourceProviders;
    private boolean loadProvidedPropertyFilters;

    /**
     * Flag if the config has already been built.
     * Configuration can be built only once
     */
    private boolean built;

    public PropertySourceBasedConfigurationBuilder(){
    }

    public PropertySourceBasedConfigurationBuilder setPropertyValueCombinationPolicy(PropertyValueCombinationPolicy policy) {
        this.propertyValueCombinationPolicy = Objects.requireNonNull(policy);
        return this;
    }

    public PropertySourceBasedConfigurationBuilder addPropertySources(PropertySource... propertySources) {
        for (PropertySource ps : propertySources) {
            if (ps != null) {
                this.propertySources.add(ps);
            }
        }
        return this;
    }

    public PropertySourceBasedConfigurationBuilder addPropertySources(Collection<PropertySource> propertySources) {
        for (PropertySource ps : propertySources) {
            if (ps != null) {
                this.propertySources.add(ps);
            }
        }
        return this;
    }

    public PropertySourceBasedConfigurationBuilder addPropertySourceProviders(PropertySourceProvider... propertySourceProviders) {
        for (PropertySourceProvider ps : propertySourceProviders) {
            if (ps != null) {
                this.propertySources.addAll(ps.getPropertySources());
            }
        }
        return this;
    }

    public PropertySourceBasedConfigurationBuilder addPropertySourceProviders(Collection<PropertySourceProvider> propertySourceProviders) {
        for (PropertySourceProvider ps : propertySourceProviders) {
            if (ps != null) {
                this.propertySources.addAll(ps.getPropertySources());
            }
        }
        return this;
    }

    public PropertySourceBasedConfigurationBuilder addPropertyFilters(PropertyFilter... propertyFIlter) {
        for (PropertyFilter pf : propertyFIlter) {
            if (pf != null) {
                this.propertyFilters.add(pf);
            }
        }
        return this;
    }

    public PropertySourceBasedConfigurationBuilder addPropertyFilters(Collection<PropertyFilter> propertyFIlter) {
        for (PropertyFilter pf : propertyFIlter) {
            if (pf != null) {
                this.propertyFilters.add(pf);
            }
        }
        return this;
    }

    /**
     * Should be never used.
     */
    @Deprecated
    public PropertySourceBasedConfigurationBuilder usePropertySourceBasedConfiguration(PropertySourceBasedConfiguration configurationContext) {
        addPropertySources(configurationContext.getPropertySources());
        addPropertyFilters(configurationContext.getPropertyFilters());
        this.propertyValueCombinationPolicy = Objects.requireNonNull(
                configurationContext.getPropertyValueCombinationPolicy());
        return this;
    }

    public void loadProvidedPropertySources(boolean state) {
        loadProvidedPropertySources = state;
    }

    public void loadProvidedPropertySourceProviders(boolean state) {
        loadProvidedPropertySourceProviders = state;
    }

    public void loadProvidedPropertyFilters(boolean state) {
        loadProvidedPropertyFilters = state;
    }

    public PropertySourceBasedConfigurationBuilder setConfiguration(PropertySourceBasedConfiguration context) {
        this.propertySources.clear();
        for(PropertySource ps:context.getPropertySources()) {
            addPropertySources(ps);
        }
        this.propertyFilters.clear();
        this.propertyFilters.addAll(context.getPropertyFilters());
        this.propertyValueCombinationPolicy = context.getPropertyValueCombinationPolicy();
        return this;
    }

    public PropertySourceBasedConfigurationBuilder addPropertySourceURLs(Collection<URL> propertySourceURLsToAdd) {
        for(URL url:propertySourceURLsToAdd){
            try {
                ConfigurationData data = ConfigurationFormats.readConfigurationData(url);
                addPropertySources(new SimplePropertySource(url.toString(),data.getCombinedProperties()));
            }catch(Exception e){
                LOG.log(Level.SEVERE, "Failed to load config from: " + url, e);
            }
        }
        for(URL url:propertySourceURLsToAdd) {
            try {
                this.propertySources.add(new SimplePropertySource(url.toString(), getConfigurationDataFromURL(url).getCombinedProperties()));
            } catch (IOException e) {
                LOG.log(Level.SEVERE, "Error loading config from: " + url, e);
            }
        }
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
    public PropertySourceBasedConfigurationBuilder addPropertySource(URL url) {
        try {
            ConfigurationData data = getConfigurationDataFromURL(url);
            addPropertySources(new SimplePropertySource(url.toString(), data.getCombinedProperties()));
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

    public PropertySourceBasedConfigurationBuilder addPropertySourceURLs(URL... propertySourcesToAdd) {
        return addPropertySourceURLs(Arrays.asList(propertySourcesToAdd));
    }

    public PropertySourceBasedConfigurationBuilder removePropertySources(Collection<String> propertySourcesToRemove) {
        for(String key: propertySourcesToRemove){
            this.propertySources.remove(key);
        }
        return this;
    }

    public PropertySourceBasedConfigurationBuilder removePropertySources(String... propertySourcesToRemove) {
        return removePropertySources(Arrays.asList(propertySourcesToRemove));
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
    public PropertySourceBasedConfigurationBuilder addPropertySourceURLs(String... urls) {
        for(String url:urls) {
            if (url != null) {
                try{
                    addPropertySource(new URL(url));
                } catch(Exception e){
                    throw new ConfigException("Invalid URL: " + url);
                }
            }
        }
        return this;
    }


    /**
     * Enables the automatic loading of all {@link PropertySource}
     * service providers.
     *
     * @return the builder instance currently used
     *
     * @see PropertySource
     * @see #disableProvidedPropertySources()
     */
    public PropertySourceBasedConfigurationBuilder enableProvidedPropertySources() {
        checkBuilderState();

        loadProvidedPropertySources = true;

        return this;
    }

    /**
     * Enables the automatic loading of all {@link PropertySource}
     * service providers.
     *
     * @return the builder instance currently used
     *
     * @see PropertySource
     * @see #enableProvidedPropertySources()
     */
    public PropertySourceBasedConfigurationBuilder disableProvidedPropertySources() {
        checkBuilderState();

        loadProvidedPropertySources = false;

        return this;
    }

    /**
     * Checks if the automatic loading of all {@link PropertySource
     * PropertySource} service providers is enabled or disabled.
     *
     * @return {@code true} if the automatic loading is enabled,
     *         otherwise {@code false}.
     */
    public boolean isPropertySourcesLoadingEnabled() {
        return loadProvidedPropertySources;
    }


    /**
     * Checks if the automatic loading of all {@link PropertyFilter
     * PropertyFilter} service providers is enabled or disabled.
     *
     * @return {@code true} if the automatic loading is enabled,
     *         otherwise {@code false}.
     */
    public boolean isPropertyFilterLoadingEnabled() {
        return loadProvidedPropertyFilters;
    }

    /**
     * Enables the automatic loading of all {@link PropertyFilter}
     * service providers.
     *
     * @return the builder instance currently used
     *
     * @see PropertyFilter
     * @see #disableProvidedPropertyFilters()
     * @see #addPropertyFilters(PropertyFilter...)
     */
    public PropertySourceBasedConfigurationBuilder enabledProvidedPropertyFilters() {
        checkBuilderState();

        loadProvidedPropertyFilters = true;

        return this;
    }

    /**
     * Disables the automatic loading of all {@link PropertyFilter}
     * service providers.
     *
     * @see PropertyFilter
     * @see #enabledProvidedPropertyFilters()
     * @see #addPropertyFilters(PropertyFilter...)
     *
     * @return the builder instance currently used
     */
    public PropertySourceBasedConfigurationBuilder disableProvidedPropertyFilters() {
        checkBuilderState();

        loadProvidedPropertyFilters = false;

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
     * @return the builder instance currently used
     *
     * @see PropertySourceProvider
     */
    public PropertySourceBasedConfigurationBuilder enableProvidedPropertySourceProviders() {
        checkBuilderState();

        loadProvidedPropertySourceProviders = true;

        return this;
    }

    /**
     * Disables the automatic loading of {@link PropertySourceProvider
     * property source providers} provided via the SPI API.
     *
     * @return the builder instance currently used
     */
    public PropertySourceBasedConfigurationBuilder disableProvidedPropertySourceProviders() {
        checkBuilderState();

        loadProvidedPropertySourceProviders = false;

        return this;
    }

    /**
     * Checks if the automatic loading of {@link PropertySourceProvider
     * PropertySourceProviders} is enabled or disabled.
     *
     * @return {@code true} if the automatic loading is enabled,
     *         otherwise {@code false}.
     */
    public boolean isPropertySourceProvidersLoadingEnabled() {
        return loadProvidedPropertySourceProviders;
    }

    /**
     * Builds a new configuration based on the configuration of this builder instance.
     *
     * @return a new {@link org.apache.tamaya.Configuration configuration instance},
     *         never {@code null}.
     */
    public PropertySourceBasedConfiguration build() {
        checkBuilderState();
        loadProvidedPropertySources(isPropertySourcesLoadingEnabled());
        loadProvidedPropertySourceProviders(isPropertySourceProvidersLoadingEnabled());
        loadProvidedPropertyFilters(isPropertyFilterLoadingEnabled());

        return new DefaultPropertySourceBasedConfiguration(this);
    }

    /**
     * Mapper to map a URL given as string to an URL instance.
     */
    private static class StringToURLMapper {
        public URL apply(String u) {
            try {
                return new URL(u);
            } catch (MalformedURLException e) {
                throw new ConfigException(u + " is not a valid URL", e);
            }
        }
    }

}
