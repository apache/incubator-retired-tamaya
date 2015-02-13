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

import java.net.URL;
import java.util.Collection;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Implementation of a {@link org.apache.tamaya.spi.PropertySourceProvider} that reads configuration from
 * a given resource and in a given format.
 */
public abstract class BaseSimpleFormatPropertySourceProvider implements PropertySourceProvider {
    /**
     * The logger used.
     */
    private static final Logger LOG = Logger.getLogger(BaseSimpleFormatPropertySourceProvider.class.getName());
    /**
     * The config formats supported, not null.
     */
    private ConfigurationFormat configFormat;
    /**
     * The resource to be read, not null.
     */
    private URL resource;

    /**
     * Creates a new instance.
     *
     * @param format   the formas to be used, not null.
     * @param resource the resource to be read, not null.
     */
    public BaseSimpleFormatPropertySourceProvider(
            ConfigurationFormat format,
            URL resource) {
        this.configFormat = Objects.requireNonNull(format);
        this.resource = Objects.requireNonNull(resource);
    }

    /**
     * Method to create a {@link org.apache.tamaya.spi.PropertySource} based on the given entries read.
     *
     * @param configData the config data read.
     * @return the {@link org.apache.tamaya.spi.PropertySource} instance ready to be registered.
     */
    protected abstract Collection<PropertySource> createPropertySourcea(ConfigurationData configData);

}
