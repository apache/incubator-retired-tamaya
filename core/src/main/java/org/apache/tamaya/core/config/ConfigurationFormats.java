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
package org.apache.tamaya.core.config;

import org.apache.tamaya.core.resource.Resource;
import org.apache.tamaya.core.spi.ConfigurationFormat;
import org.apache.tamaya.core.spi.ConfigurationFormatsSingletonSpi;

import org.apache.tamaya.spi.ServiceContext;

import java.util.Collection;

/**
 * Singleton accessor for accessing {@link org.apache.tamaya.core.spi.ConfigurationFormat} instances.
 */
public final class ConfigurationFormats{

    /**
     * Private singleton constructor.
     */
    private ConfigurationFormats(){
    }

    /**
     * Access a {@link org.apache.tamaya.core.spi.ConfigurationFormat}.
     *
     * @param formatName the format name
     * @return the corresponding {@link org.apache.tamaya.core.spi.ConfigurationFormat}, or {@code null}, if
     * not available for the given environment.
     */
    public static ConfigurationFormat getFormat(String formatName){
        return ServiceContext.getInstance().getSingleton(ConfigurationFormatsSingletonSpi.class).getFormat(formatName);
    }

    /**
     * Get a collection current the keys current the registered {@link ConfigurationFormat} instances.
     *
     * @return a collection current the keys current the registered {@link ConfigurationFormat} instances.
     */
    public static Collection<String> getFormatNames(){
        return ServiceContext.getInstance().getSingleton(ConfigurationFormatsSingletonSpi.class).getFormatNames();
    }

    /**
     * Evaluate the matching format for a given resource.
     *
     * @param resource The resource
     * @return a matching configuration format, or {@code null} if no matching format could be determined.
     */
    public static ConfigurationFormat getFormat(Resource resource){
        return ServiceContext.getInstance().getSingleton(ConfigurationFormatsSingletonSpi.class).getFormat(resource);

    }

    /**
     * Get an instance for reading configuration fromMap a {@code .properties} file,
     * as defined by {@link java.util.Properties#load(java.io.InputStream)}.
     *
     * @return a format instance for reading configuration fromMap a {@code .properties} file, never null.
     */
    public static ConfigurationFormat getPropertiesFormat(){
        return ServiceContext.getInstance().getSingleton(ConfigurationFormatsSingletonSpi.class).getPropertiesFormat();
    }

    /**
     * Get an instance for reading configuration fromMap a {@code .xml} properties file,
     * as defined by {@link java.util.Properties#loadFromXML(java.io.InputStream)}.
     *
     * @return a format instance for reading configuration fromMap a {@code .xml} properties file, never null.
     */
    public static ConfigurationFormat getXmlPropertiesFormat(){
        return ServiceContext.getInstance().getSingleton(ConfigurationFormatsSingletonSpi.class).getXmlPropertiesFormat();
    }

}
