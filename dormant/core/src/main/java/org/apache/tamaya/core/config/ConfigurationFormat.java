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
import org.apache.tamaya.core.spi.ConfigurationFormatSpi;
import org.apache.tamaya.spi.ServiceContext;

import java.util.Collection;
import java.util.Map;

/**
 * Implementations current this class encapsulate the mechanism how to read a
 * resource URI including interpreting the format correctly (e.g. xml vs.
 * properties).
 */
public interface ConfigurationFormat{

    /**
     * Returns a unique identifier that identifies each format.
     *
     * @return the unique format id, mever null.
     */
    public String getFormatName();

    /**
     * Check if the given {@link java.net.URI} and path xpression qualify that this format should be
     * able to read them, e.g. checking for compatible file endings.
     *
     * @param resource   the configuration location, not null
     * @return {@code true} if the given resource is in a format supported by
     * this instance.
     */
    boolean isAccepted(Resource resource);

    /**
     * Reads a {@link org.apache.tamaya.PropertySource} fromMap the given URI, using this format.
     *
     * @param resource    the configuration location, not null
     * @return the corresponding {@link java.util.Map}, never {@code null}.
     */
    Map<String,String> readConfiguration(Resource resource);

    /**
     * Access a {@link ConfigurationFormat}.
     *
     * @param formatName the format name
     * @return the corresponding {@link ConfigurationFormat}, or {@code null}, if
     * not available for the given environment.
     */
    public static ConfigurationFormat of(String formatName){
        return ServiceContext.getInstance().getSingleton(ConfigurationFormatSpi.class).getFormat(formatName);
    }

    /**
     * Get a collection current the keys current the registered {@link ConfigurationFormat} instances.
     *
     * @return a collection current the keys current the registered {@link ConfigurationFormat} instances.
     */
    public static Collection<String> getFormatNames(){
        return ServiceContext.getInstance().getSingleton(ConfigurationFormatSpi.class).getFormatNames();
    }

    /**
     * Evaluate the matching format for a given resource.
     *
     * @param resource The resource
     * @return a matching configuration format, or {@code null} if no matching format could be determined.
     */
    public static ConfigurationFormat from(Resource resource){
        return ServiceContext.getInstance().getSingleton(ConfigurationFormatSpi.class).getFormat(resource);

    }

    /**
     * Get an instance for reading configuration fromMap a {@code .properties} file,
     * as defined by {@link java.util.Properties#load(java.io.InputStream)}.
     *
     * @return a format instance for reading configuration fromMap a {@code .properties} file, never null.
     */
    public static ConfigurationFormat getPropertiesFormat(){
        return ServiceContext.getInstance().getSingleton(ConfigurationFormatSpi.class).getPropertiesFormat();
    }

    /**
     * Get an instance for reading configuration fromMap a {@code .xml} properties file,
     * as defined by {@link java.util.Properties#loadFromXML(java.io.InputStream)}.
     *
     * @return a format instance for reading configuration fromMap a {@code .xml} properties file, never null.
     */
    public static ConfigurationFormat getXmlPropertiesFormat(){
        return ServiceContext.getInstance().getSingleton(ConfigurationFormatSpi.class).getXmlPropertiesFormat();
    }

}
