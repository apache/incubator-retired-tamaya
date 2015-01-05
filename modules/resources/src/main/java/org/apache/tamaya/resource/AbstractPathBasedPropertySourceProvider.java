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
package org.apache.tamaya.resource;

import org.apache.tamaya.format.ConfigurationFormat;
import org.apache.tamaya.spi.PropertySource;
import org.apache.tamaya.spi.PropertySourceProvider;
import org.apache.tamaya.spi.ServiceContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of a {@link PropertySourceProvider} that reads configuration from some given resource paths
 * and using the given formats. The resource path are resolved using the current
 * {@link org.apache.tamaya.resource.ResourceResolver} active.
 */
public abstract class AbstractPathBasedPropertySourceProvider implements PropertySourceProvider {
    /** The logger used. */
    private static final Logger LOG = Logger.getLogger(AbstractPathBasedPropertySourceProvider.class.getName());
    /** The property source base name, will be used for creating a useful name of the
     * {@link org.apache.tamaya.spi.PropertySource} created. */
    private String sourceName;
    /** The config formats supported for the given location/resource paths. */
    private List<ConfigurationFormat> configFormats = new ArrayList<>();
    /** The paths to be evaluated. */
    private List<String> paths = new ArrayList<>();

    /**
     * Creates a new instance.
     * @param sourceName the base name of the configuration, used for creating PropertySource child names.
     * @param formats the formats to be used, not null, not empty.
     * @param paths the paths to be resolved, not null, not empty.
     */
    public AbstractPathBasedPropertySourceProvider(String sourceName, List<ConfigurationFormat> formats, String... paths) {
        this.sourceName = Objects.requireNonNull(sourceName);
        this.configFormats.addAll(Objects.requireNonNull(formats));
        this.paths.addAll(Arrays.asList(Objects.requireNonNull(paths)));
    }

    /**
     * Creates a new instance.
     * @param sourceName the base name of the configuration, used for creating PropertySource child names.
     * @param format the format to be used.
     * @param paths the paths to be resolved, not null, not empty.
     */
    public AbstractPathBasedPropertySourceProvider(String sourceName, ConfigurationFormat format, String... paths) {
        this.sourceName = Objects.requireNonNull(sourceName);
        this.configFormats.add(Objects.requireNonNull(format));
        this.paths.addAll(Arrays.asList(Objects.requireNonNull(paths)));
    }

    @Override
    public Collection<PropertySource> getPropertySources() {
        List<PropertySource> propertySources = new ArrayList<>();
        paths.forEach((path) -> {
            for (Resource res : ServiceContext.getInstance().getService(ResourceResolver.class).get().getResources(path)) {
                try {
                    for (ConfigurationFormat format : configFormats) {
                        propertySources.addAll(format.readConfiguration(sourceName, res));
                    }
                } catch (Exception e) {
                    LOG.log(Level.WARNING, "Failed to add resource based config: " + res.getName(), e);
                }
            }
        });
        return propertySources;
    }

}
