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
package org.apache.tamaya.format;

import org.apache.tamaya.resource.ResourceResolver;
import org.apache.tamaya.spi.PropertySource;
import org.apache.tamaya.spi.PropertySourceProvider;
import org.apache.tamaya.spi.ServiceContext;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of a {@link PropertySourceProvider} that reads configuration from some given resource paths
 * and using the given formats. The resource path are resolved using the current
 * {@link org.apache.tamaya.resource.ResourceResolver} active. For each resource found the configuration formats
 * passed get a chance to read the resource, if they succeed the result is taken as the providers PropertySources
 * to be exposed.
 */
public abstract class BasePathBasedMultiFormatPropertySourceProvider implements PropertySourceProvider {
    /**
     * The logger used.
     */
    private static final Logger LOG = Logger.getLogger(BasePathBasedMultiFormatPropertySourceProvider.class.getName());
    /**
     * The config formats supported for the given location/resource paths.
     */
    private List<ConfigurationFormat> configFormats = new ArrayList<>();
    /**
     * The paths to be evaluated.
     */
    private List<String> paths = new ArrayList<>();
    /**
     * The ClassLoader to use.
     */
    private Optional<ClassLoader> classLoader;

    /**
     * Creates a new instance.
     *
     * @param formats the formats to be used, not null, not empty.
     * @param paths   the paths to be resolved, not null, not empty.
     */
    public BasePathBasedMultiFormatPropertySourceProvider(
            List<ConfigurationFormat> formats,
            String... paths) {
        this.configFormats.addAll(Objects.requireNonNull(formats));
        this.paths.addAll(Arrays.asList(Objects.requireNonNull(paths)));
    }

    /**
     * Creates a new instance.
     *
     * @param formats the formats to be used, not null, not empty.
     * @param paths   the paths to be resolved, not null, not empty.
     */
    public BasePathBasedMultiFormatPropertySourceProvider(
            ClassLoader classLoader,
            List<ConfigurationFormat> formats, String... paths) {
        this.classLoader = Optional.ofNullable(classLoader);
        this.configFormats.addAll(Objects.requireNonNull(formats));
        this.paths.addAll(Arrays.asList(Objects.requireNonNull(paths)));
    }

    /**
     * Method to create a {@link org.apache.tamaya.spi.PropertySource} based on the given entries read.
     *
     * @param data the configuration data, not null.
     * @return the {@link org.apache.tamaya.spi.PropertySource} instance ready to be registered.
     */
    protected abstract Collection<PropertySource> getPropertySources(ConfigurationData data);

    /**
     * This method does dynamically resolve the paths using the current ClassLoader set. If no ClassLoader was
     * explcitly set during creation the current Thread context ClassLoader is used. If none of the supported
     * formats is able to parse a resource a WARNING log is written.
     *
     * @return the PropertySources successfully read
     */
    @Override
    public Collection<PropertySource> getPropertySources() {
        ResourceResolver resourceResolver = ServiceContext.getInstance().getService(ResourceResolver.class).get();
        List<PropertySource> propertySources = new ArrayList<>();
        paths.forEach((path) -> {
            for (URL res : resourceResolver.getResources(
                    this.classLoader.orElse(Thread.currentThread().getContextClassLoader()),
                    path)) {
                try(InputStream is = res.openStream()) {
                    for (ConfigurationFormat format : configFormats) {
                        ConfigurationData entries = format.readConfiguration(res.toString(), is);
                        propertySources.addAll(getPropertySources(entries));
                    }
                } catch (Exception e) {
                    LOG.log(Level.WARNING, "Failed to add resource based config: " + res, e);
                }
            }
        });
        return propertySources;
    }

}
