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
import org.apache.tamaya.spi.PropertySource;
import org.apache.tamaya.spi.PropertySourceProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class ResourcePropertySourceProvider implements PropertySourceProvider {

    private static final Logger LOG = Logger.getLogger(ResourcePropertySourceProvider.class.getName());

    private List<ConfigurationFormat> formats = new ArrayList<>();

    private Resource resource;

    public ResourcePropertySourceProvider(Resource resource, ConfigurationFormat... formats) {
        this.resource = Objects.requireNonNull(resource);
        this.formats.addAll(Arrays.asList(formats));
    }

    public ResourcePropertySourceProvider(Resource resource, List<ConfigurationFormat> formats) {
        this.resource = Objects.requireNonNull(resource);
        this.formats.addAll(formats);
    }


    /**
     * Get the underlying resource.
     *
     * @return
     */
    public Resource getResource() {
        return this.resource;
    }


    @Override
    public String toString() {
        return "ResourcePropertySourceProvider{" +
                "resource=" + resource +
                ", formats=+" + formats +
                '}';
    }

    @Override
    public Collection<PropertySource> getPropertySources() {
        List<PropertySource> propertySources = new ArrayList<>();
        for (ConfigurationFormat format : formats) {
            try {
                propertySources.addAll(format.readConfiguration(resource));
            } catch (Exception e) {
                LOG.info(() -> "Format was not matching: " + format + " for resource: " + resource.getDisplayName());
            }
        }
        return propertySources;
    }

}
