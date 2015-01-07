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

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Implementation of a {@link org.apache.tamaya.spi.PropertySourceProvider} that is based on a single resource
 * and a number of formats.
 */
public abstract class AbstractResourcePropertySourceProvider implements PropertySourceProvider {
    /** The logger used. */
    private static final Logger LOG = Logger.getLogger(AbstractResourcePropertySourceProvider.class.getName());
    /** The supported formats. */
    private List<ConfigurationFormat> formats = new ArrayList<>();
    /** The resource. */
    private URL resource;
    /** The source name used for creating the PropertySource names. */
    private String sourceName;

    /**
     * Creates a new instance.
     * @param resource the {@link URL}, not null.
     * @param formats the supported formats, not empty.
     */
    public AbstractResourcePropertySourceProvider(String sourceName, URL resource, ConfigurationFormat... formats) {
        this(sourceName, resource, Arrays.asList(formats));
    }

    /**
     * Creates a new instance.
     * @param resource the {@link URL}, not null.
     * @param formats the supported formats, not empty.
     */
    public AbstractResourcePropertySourceProvider(String sourceName, URL resource, List<ConfigurationFormat> formats) {
        this.resource = Objects.requireNonNull(resource);
        this.sourceName = Objects.requireNonNull(sourceName);
        if(formats.size()==0){
            throw new IllegalArgumentException("Format required.");
        }
        this.formats.addAll(formats);
    }


    /**
     * Get the underlying resource.
     *
     * @return the underlying resource, never null.
     */
    public URL getResource() {
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
                LOG.info(() -> "Format was not matching: " + format + " for resource: " + resource);
            }
        }
        return propertySources;
    }

}
