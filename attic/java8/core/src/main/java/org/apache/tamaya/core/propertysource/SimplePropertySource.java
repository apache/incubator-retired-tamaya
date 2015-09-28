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
package org.apache.tamaya.core.propertysource;

import org.apache.tamaya.ConfigException;
import org.apache.tamaya.spi.PropertySource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * Simple implementation of a {@link org.apache.tamaya.spi.PropertySource} for properties-files.
 */
public class SimplePropertySource implements PropertySource{
    /** The property source name. */
    private String name;
    /** The current properties. */
    private Map<String,String> properties;

    /**
     * Creates a new Properties based PropertySource based on the given URL.
     * @param propertiesLocation the URL encoded location, not null.
     */
    public SimplePropertySource(File propertiesLocation) {
        try{
            this.properties = load(propertiesLocation.toURI().toURL());
            this.name = propertiesLocation.toString();
        } catch(IOException e){
            throw new ConfigException("Failed to load properties from " + propertiesLocation,e);
        }
    }

    /**
     * Creates a new Properties based PropertySource based on the given URL.
     * @param propertiesLocation the URL encoded location, not null.
     */
    public SimplePropertySource(URL propertiesLocation) {
        this.properties = load(propertiesLocation);
        this.name = propertiesLocation.toExternalForm();
    }

    /**
     * Creates a new Properties based PropertySource based on the given URL.
     * @param name The property source name
     * @param propertiesLocation the URL encoded location, not null.
     */
    public SimplePropertySource(String name, URL propertiesLocation) {
        this.properties = load(propertiesLocation);
        this.name = Objects.requireNonNull(name);
    }

    /**
     * Creates a new Properties based PropertySource based on the given properties map.
     *
     * @param name       the name, not null.
     * @param properties the properties, not null.
     */
    public SimplePropertySource(String name, Map<String, String> properties) {
        this.properties = new HashMap<>(properties);
        this.name = Objects.requireNonNull(name);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Map<String, String> getProperties() {
        return this.properties;
    }

    /**
     * loads the Properties from the given URL
     *
     * @param propertiesFile {@link URL} to load Properties from
     *
     * @return loaded {@link java.util.Properties}
     *
     * @throws IllegalStateException in case of an error while reading properties-file
     */
    private Map<String,String> load(URL propertiesFile) {
        Map<String,String> properties = new HashMap<>();
        try (InputStream stream = propertiesFile.openStream()) {
            Properties props = new Properties();
            if (stream != null) {
                props.load(stream);
            }
            props.forEach((k,v) -> properties.put(k.toString(), v.toString()));
        } catch (IOException e) {
            throw new ConfigException("Error loading properties " + propertiesFile, e);
        }
        return properties;
    }

}
