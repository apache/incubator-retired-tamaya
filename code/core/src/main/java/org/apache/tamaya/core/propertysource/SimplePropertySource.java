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
import org.apache.tamaya.spi.PropertyValue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Simple implementation of a {@link org.apache.tamaya.spi.PropertySource} for
 * simple property files and XML property files.
 */
public class SimplePropertySource extends BasePropertySource {

    private static final Logger LOG = Logger.getLogger(SimplePropertySource.class.getName());

    /**
     * The current properties.
     */
    private Map<String, PropertyValue> properties = new HashMap<>();

    /**
     * Creates a new Properties based PropertySource based on the given URL.
     *
     * @param propertiesLocation the URL encoded location, not null.
     */
    public SimplePropertySource(File propertiesLocation) {
        super(0);
        try {
            setName(propertiesLocation.toString());
            this.properties = load(propertiesLocation.toURI().toURL());
        } catch (IOException e) {
            throw new ConfigException("Failed to load properties from " + propertiesLocation, e);
        }
    }

    /**
     * Creates a new Properties based PropertySource based on the given URL.
     *
     * @param propertiesLocation the URL encoded location, not null.
     */
    public SimplePropertySource(URL propertiesLocation) {
        super(0);
        this.properties = load(Objects.requireNonNull(propertiesLocation));
        setName(propertiesLocation.toString());
    }

    /**
     * Creates a new Properties based PropertySource based on the given properties map.
     *
     * @param name       the name, not null.
     * @param properties the properties, not null.
     */
    public SimplePropertySource(String name, Map<String, String> properties) {
        super(0);
        setName(Objects.requireNonNull(name));
        for(Map.Entry<String,String> en:properties.entrySet()){
            this.properties.put(en.getKey(), PropertyValue.of(en.getKey(), en.getValue(), name));
        }
    }

    /**
     * Creates a new Properties based PropertySource based on the given URL.
     *
     * @param name               The property source name
     * @param propertiesLocation the URL encoded location, not null.
     */
    public SimplePropertySource(String name, URL propertiesLocation) {
        super(0);
        this.properties = load(propertiesLocation);
        setName(name);
    }

    @Override
    public Map<String, PropertyValue> getProperties() {
        return this.properties;
    }

    /**
     * loads the Properties from the given URL
     *
     * @param propertiesFile {@link java.net.URL} to load Properties from
     * @return loaded {@link java.util.Properties}
     * @throws IllegalStateException in case of an error while reading properties-file
     */
    private Map<String, PropertyValue> load(URL propertiesFile) {
        setName(propertiesFile.toString());
        boolean isXML = isXMLPropertieFiles(propertiesFile);

        Map<String, PropertyValue> properties = new HashMap<>();
        try (InputStream stream = propertiesFile.openStream()) {
            Properties props = new Properties();
            if (stream != null) {
                if (isXML) {
                    props.loadFromXML(stream);
                } else {
                    props.load(stream);
                }
            }

            for (String key : props.stringPropertyNames()) {
                properties.put(key, PropertyValue.of(key, props.getProperty(key), getName()));
            }
        } catch (IOException e) {
            throw new ConfigException("Error loading properties from " + propertiesFile, e);
        }

        return properties;
    }

    private boolean isXMLPropertieFiles(URL url) {
        return url.getFile().endsWith(".xml");
    }

}
