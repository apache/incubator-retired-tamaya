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
package org.apache.tamaya.core;

import org.apache.tamaya.core.formats.ConfigurationFormat;
import org.apache.tamaya.core.resources.Resource;
import org.apache.tamaya.core.resources.ResourceLoader;
import org.apache.tamaya.spi.PropertySource;
import org.apache.tamaya.spi.PropertySourceProvider;
import org.apache.tamaya.spi.ServiceContext;

import java.util.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of a PropertySourceProvider that reads configuration from some given resource paths
 * and using the goven formats.
 */
public class PathBasedPropertySourceProvider implements PropertySourceProvider {

    private static final Logger LOG = Logger.getLogger(PathBasedPropertySourceProvider.class.getName());

    private String baseName;
    private List<ConfigurationFormat> configFormats = new ArrayList<>();
    private List<String> paths = new ArrayList<>();

    public PathBasedPropertySourceProvider(String baseName, List<ConfigurationFormat> formats, String... paths) {
        this.baseName = Objects.requireNonNull(baseName);
        this.configFormats.addAll(Objects.requireNonNull(formats));
        this.paths.addAll(Arrays.asList(Objects.requireNonNull(paths)));
    }

    public PathBasedPropertySourceProvider(String baseName, ConfigurationFormat format, String... paths) {
        this.baseName = Objects.requireNonNull(baseName);
        this.configFormats.add(Objects.requireNonNull(format));
        this.paths.addAll(Arrays.asList(Objects.requireNonNull(paths)));
    }

    @Override
    public Collection<PropertySource> getPropertySources() {
        List<PropertySource> propertySources = new ArrayList<>();
        paths.forEach((path) -> {
            for (Resource res : ServiceContext.getInstance().getService(ResourceLoader.class).get().getResources(path)) {
                try {
                    for (ConfigurationFormat format : configFormats) {
                        propertySources.addAll(format.readConfiguration(res));
                    }
                } catch (Exception e) {
                    LOG.log(Level.WARNING, "Failed to add resource based config: " + res.getDisplayName(), e);
                }
            }
        });
        return propertySources;
    }

}
