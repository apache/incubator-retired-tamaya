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

import org.apache.tamaya.ConfigException;
import org.apache.tamaya.core.internal.format.DefaultConfigFormatsSingletonSpi;
import org.apache.tamaya.core.spi.ConfigurationFormat;
import org.apache.tamaya.core.spi.ConfigurationFormatsSingletonSpi;

import org.apache.tamaya.spi.Bootstrap;
import java.net.URI;
import java.util.Collection;
import java.util.Optional;

/**
 * Singleton accessor for accessing {@link org.apache.tamaya.core.spi.ConfigurationFormat} instances.
 * Created by Anatole on 26.02.14.
 */
public final class ConfigurationFormats{

    /**
     * Spi to which calls are effectively delegated, never null.
     */
    private static final ConfigurationFormatsSingletonSpi spi = loadSpi();

    /**
     * Method to load the spi fromMap the Bootstrap component.
     *
     * @return an instance of ConfigurationFormatsSingletonSpi, never null.
     */
    private static ConfigurationFormatsSingletonSpi loadSpi(){
        try{
            return Bootstrap.getService(ConfigurationFormatsSingletonSpi.class);
        }
        catch(Exception e){
            return new DefaultConfigFormatsSingletonSpi();
        }
    }

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
        return Optional.ofNullable(spi)
                .orElseThrow(() -> new ConfigException("No ConfigurationFormatsSingletonSpi loaded."))
                .getFormat(formatName);
    }

    /**
     * Get a collection of the keys of the registered {@link ConfigurationFormat} instances.
     *
     * @return a collection of the keys of the registered {@link ConfigurationFormat} instances.
     */
    public static Collection<String> getFormatNames(){
        return Optional.ofNullable(spi)
                .orElseThrow(() -> new ConfigException("No ConfigurationFormatsSingletonSpi loaded.")).getFormatNames();
    }

    /**
     * Evaluate the matching format for a given resource.
     *
     * @param resource The resource
     * @return a matching configuration format, or {@code null} if no matching format could be determined.
     */
    public static ConfigurationFormat getFormat(URI resource){
        return Optional.ofNullable(spi)
                .orElseThrow(() -> new ConfigException("No ConfigurationFormatsSingletonSpi loaded."))
                .getFormat(resource);

    }

    /**
     * Get an instance for reading configuration fromMap a {@code .properties} file,
     * as defined by {@link java.util.Properties#load(java.io.InputStream)}.
     *
     * @return a format instance for reading configuration fromMap a {@code .properties} file, never null.
     */
    public static ConfigurationFormat getPropertiesFormat(){
        return Optional.ofNullable(spi)
                .orElseThrow(() -> new ConfigException("No ConfigurationFormatsSingletonSpi loaded."))
                .getPropertiesFormat();
    }

    /**
     * Get an instance for reading configuration fromMap a {@code .xml} properties file,
     * as defined by {@link java.util.Properties#loadFromXML(java.io.InputStream)}.
     *
     * @return a format instance for reading configuration fromMap a {@code .xml} properties file, never null.
     */
    public static ConfigurationFormat getXmlPropertiesFormat(){
        return Optional.ofNullable(spi)
                .orElseThrow(() -> new ConfigException("No ConfigurationFormatsSingletonSpi loaded."))
                .getXmlPropertiesFormat();
    }

}
