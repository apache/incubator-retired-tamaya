/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tamaya.mutableconfig.internal;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Simple implementation of a {@link org.apache.tamaya.spi.PropertySource} for properties-files.
 */
class SimplePropertySource extends BasePropertySource {

    /**
     * The logger.
     */
    private static final Logger LOG = Logger.getLogger(SimplePropertySource.class.getName());
    /**
     * Default update interval is 1 minute.
     */
    private static final long DEFAULT_UPDATE_INTERVAL = 60000L;

    /**
     * The property source name.
     */
    private String name;

    /**
     * The configuration resource's URL.
     */
    private URL resource;

    /**
     * Timestamp of last read.
     */
    private long lastRead;

    /**
     * Interval, when the resource should try to update its contents.
     */
    private long updateInterval = DEFAULT_UPDATE_INTERVAL;
    /**
     * The current properties.
     */
    private Map<String, String> properties;

    /**
     * Creates a new Properties based PropertySource based on the given URL.
     *
     * @param propertiesLocation the URL encoded location, not null.
     */
    public SimplePropertySource(File propertiesLocation) {
        super(0);
        this.name = propertiesLocation.toString();
        try {
            this.resource = propertiesLocation.toURI().toURL();
            load();
        } catch (MalformedURLException e) {
            LOG.log(Level.SEVERE, "Cannot convert file to URL: " + propertiesLocation, e);
        }
    }

    /**
     * Creates a new Properties based PropertySource based on the given URL.
     *
     * @param propertiesLocation the URL encoded location, not null.
     */
    public SimplePropertySource(URL propertiesLocation) {
        super(0);
        this.name = propertiesLocation.toString();
        this.resource = propertiesLocation;
        load();
    }

    /**
     * Creates a new Properties based PropertySource based on the given properties map.
     *
     * @param name       the name, not null.
     * @param properties the properties, not null.
     */
    public SimplePropertySource(String name, Map<String, String> properties) {
        super(0);
        this.name = Objects.requireNonNull(name);
        this.properties = new HashMap<>(properties);
    }

    /**
     * Creates a new Properties based PropertySource based on the given URL.
     *
     * @param name               The property source name
     * @param propertiesLocation the URL encoded location, not null.
     */
    public SimplePropertySource(String name, URL propertiesLocation) {
        super(0);
        this.name = Objects.requireNonNull(name);
        this.resource = propertiesLocation;
        load();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Map<String, String> getProperties() {
        checkLoad();
        return this.properties;
    }

    private void checkLoad() {
        if(resource!=null && (lastRead+updateInterval)<System.currentTimeMillis()){
            load();
        }
    }

    /**
     * loads the Properties from the given URL
     *
     * @return loaded {@link Properties}
     * @throws IllegalStateException in case of an error while reading properties-file
     */
    private void load() {
        Map<String, String> properties = new HashMap<>();
        try (InputStream stream = resource.openStream()) {
            Properties props = new Properties();
            if (stream != null) {
                props.load(stream);
            }
            for (String key : props.stringPropertyNames()) {
                properties.put(key, props.getProperty(key));
            }
            this.lastRead = System.currentTimeMillis();
            this.properties = properties;
            LOG.log(Level.FINEST, "Loaded properties from " + resource);
        } catch (IOException e) {
            LOG.log(Level.FINEST, "Cannot load properties from " + resource, e);
            this.properties = Collections.emptyMap();
        }
    }

}
