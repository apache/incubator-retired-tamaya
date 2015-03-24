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

import org.apache.tamaya.spi.PropertySource;
import org.apache.tamaya.spi.PropertySourceProvider;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
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
public abstract class BaseFormatPropertySourceProvider implements PropertySourceProvider {
    /**
     * The logger used.
     */
    private static final Logger LOG = Logger.getLogger(BaseFormatPropertySourceProvider.class.getName());
    /**
     * The config formats supported for the given location/resource paths.
     */
    private List<ConfigurationFormat> configFormats = new ArrayList<>();
    /**
     * The paths to be evaluated.
     */
    private Collection<URL> paths = new ArrayList<>();
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
    public BaseFormatPropertySourceProvider(
            List<ConfigurationFormat> formats,
            URL... paths) {
        this.configFormats.addAll(Objects.requireNonNull(formats));
        this.paths.addAll(Arrays.asList(Objects.requireNonNull(paths)));
    }

    /**
     * Creates a new instance, hereby using the current thread context classloader, or if not available the classloader
     * that loaded this class.
     * @param formats the formats to be used, not null, not empty.
     * @param paths   the paths to be resolved, not null, not empty.
     */
    public BaseFormatPropertySourceProvider(
            List<ConfigurationFormat> formats, String... paths) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if(cl==null){
            cl = getClass().getClassLoader();
        }
        this.configFormats.addAll(Objects.requireNonNull(formats));
        for(String path:paths) {
            Enumeration<URL> urls = null;
            try {
                urls = cl.getResources(path);
            } catch (IOException e) {
                LOG.log(Level.WARNING, e, () -> "Failed to read resource: " + path);
                continue;
            }
            while(urls.hasMoreElements()) {
                this.paths.add(urls.nextElement());
            }
        }
    }

    /**
     * Creates a new instance.
     *
     * @param classLoader the ClassLoader to be used, not null, not empty.
     * @param formats the formats to be used, not null, not empty.
     * @param paths   the paths to be resolved, not null, not empty.
     */
    public BaseFormatPropertySourceProvider(
            List<ConfigurationFormat> formats,
            ClassLoader classLoader, String... paths) {
        this.configFormats.addAll(Objects.requireNonNull(formats));
        for(String path:paths) {
            Enumeration<URL> urls = null;
            try {
                urls = classLoader.getResources(path);
            } catch (IOException e) {
                LOG.log(Level.WARNING, e, () -> "Failed to read resource: " + path);
                continue;
            }
            while(urls.hasMoreElements()) {
                this.paths.add(urls.nextElement());
            }
        }
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
        List<PropertySource> propertySources = new ArrayList<>();
        this.paths.forEach(res -> {
            try(InputStream is = res.openStream()) {
                for (ConfigurationFormat format : configFormats) {
                    ConfigurationData data = format.readConfiguration(res.toString(), is);
                    propertySources.addAll(getPropertySources(data));
                }
            } catch (Exception e) {
                LOG.log(Level.WARNING, "Failed to add resource based config: " + res, e);
            }
        });
        return propertySources;
    }

}
