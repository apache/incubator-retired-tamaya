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
package org.apache.tamaya.base.configsource;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Simple implementation of a {@link javax.config.spi.ConfigSource} for
 * simple property files and XML property files.
 */
public class SimpleConfigSource extends BaseConfigSource {

    private static final Logger LOG = Logger.getLogger(SimpleConfigSource.class.getName());

    /**
     * The current properties.
     */
    private Map<String, String> properties = new HashMap<>();

    /**
     * Creates a new Properties based PropertySource based on the given URL.
     *
     * @param propertiesLocation the URL encoded location, not null.
     */
    public SimpleConfigSource(File propertiesLocation) {
        super(propertiesLocation.toString(), 0);
        try {
            this.properties = load(propertiesLocation.toURI().toURL());
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to load properties from " + propertiesLocation, e);
        }
    }

    /**
     * Creates a new Properties based PropertySource based on the given URL.
     *
     * @param propertiesLocation the URL encoded location, not null.
     */
    public SimpleConfigSource(URL propertiesLocation) {
        super(propertiesLocation.toString(), 0);
        this.properties = load(Objects.requireNonNull(propertiesLocation));
    }

    /**
     * Creates a new Properties based PropertySource.
     *
     * @param name the property source name, not null.
     * @param properties the properties, not null
     * @param defaultOrdinal the default ordinal
     */
    public SimpleConfigSource(String name, Map<String, String> properties, int defaultOrdinal){
        super(name, defaultOrdinal);
        for(Map.Entry<String,String> en: properties.entrySet()) {
            this.properties.put(en.getKey(), en.getValue());
        }
    }

    /**
     * Creates a new Properties based PropertySource based on the given properties map.
     *
     * @param name       the name, not null.
     * @param properties the properties, not null.
     */
    public SimpleConfigSource(String name, Map<String, String> properties) {
        this(name, properties, 0);
    }

    /**
     * Creates a new Properties based PropertySource based on the given URL.
     *
     * @param name               The property source name
     * @param propertiesLocation the URL encoded location, not null.
     */
    public SimpleConfigSource(String name, URL propertiesLocation) {
        super(name, 0);
        this.properties = load(propertiesLocation);
    }

    private SimpleConfigSource(Builder builder) {
        properties = builder.properties;
        if(builder.defaultOrdinal!=null){
            setDefaultOrdinal(builder.defaultOrdinal);
        }
        if(builder.ordinal!=null){
            setOrdinal(builder.ordinal);
        }
        setName(builder.name);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @Override
    public Map<String, String> getProperties() {
        return this.properties;
    }

    /**
     * loads the Properties from the given URL
     *
     * @param propertiesFile {@link URL} to load Properties from
     * @return loaded {@link Properties}
     * @throws IllegalStateException in case of an error while reading properties-file
     */
    private static Map<String, String> load(URL propertiesFile) {
        boolean isXML = isXMLPropertieFiles(propertiesFile);

        Map<String, String> properties = new HashMap<>();
        try (InputStream stream = propertiesFile.openStream()) {
            Properties props = new Properties();
            if (stream != null) {
                if (isXML) {
                    props.loadFromXML(stream);
                } else {
                    props.load(stream);
                }
            }
            String source = propertiesFile.toString();
            for (String key : props.stringPropertyNames()) {
                properties.put(key, props.getProperty(key));
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Error loading properties from " + propertiesFile, e);
        }

        return properties;
    }

    private static boolean isXMLPropertieFiles(URL url) {
        return url.getFile().endsWith(".xml");
    }


    /**
     * {@code SimplePropertySource} builder static inner class.
     */
    public static final class Builder {
        private String name;
        private Integer defaultOrdinal;
        private Integer ordinal;
        private Map<String, String> properties = new HashMap<>();

        private Builder() {
        }

        /**
         * Sets the {@code name} to a new UUID and returns a reference to this Builder so that the methods
         * can be chained together.
         *
         * @return a reference to this Builder
         */
        public Builder withUuidName() {
            this.name = UUID.randomUUID().toString();
            return this;
        }

        /**
         * Sets the {@code name} and returns a reference to this Builder so that the methods
         * can be chained together.
         *
         * @param val the {@code name} to set, not null.
         * @return a reference to this Builder
         */
        public Builder withName(String val) {
            this.name = Objects.requireNonNull(name);
            return this;
        }

        /**
         * Sets the {@code ordinal} and returns a reference to this Builder so that the methods
         * can be chained together.
         *
         * @param val the {@code ordinal} to set
         * @return a reference to this Builder
         */
        public Builder withOrdinal(int val) {
            this.ordinal = val;
            return this;
        }

        /**
         * Sets the {@code defaultOrdinal} and returns a reference to this Builder so that the methods
         * can be chained together.
         *
         * @param val the {@code defaultOrdinal} to set
         * @return a reference to this Builder
         */
        public Builder withDefaultOrdinal(int val) {
            this.defaultOrdinal = val;
            return this;
        }

        /**
         * Reads the {@code properties} from the given resource and returns a reference
         * to this Builder so that the methods can be chained together.
         *
         * @param resource the {@code resource} to read
         * @return a reference to this Builder
         */
        public Builder withProperties(URL resource) {
            this.properties.putAll(load(resource));
            return this;
        }

        /**
         * Reads the {@code properties} from the given resource and returns a reference
         * to this Builder so that the methods can be chained together.
         *
         * @param file the {@code file} to read from (xml or properties format).
         * @return a reference to this Builder
         */
        public Builder withProperties(File file) {
            try {
                this.properties.putAll(load(file.toURI().toURL()));
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException("Failed to read file: " + file, e);
            }
            return this;
        }

        /**
         * Sets the {@code properties} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code properties} to set
         * @return a reference to this Builder
         */
        public Builder withProperties(Map<String, String> val) {
            for(Map.Entry<String,String> en: val.entrySet()) {
                this.properties.put(en.getKey(), en.getValue());
            }
            return this;
        }

        /**
         * Sets the {@code properties} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code properties} to set
         * @return a reference to this Builder
         */
        public Builder withProperty(String key, String val) {
            this.properties.put(key, val);
            return this;
        }

        /**
         * Returns a {@code SimplePropertySource} built from the parameters previously set.
         *
         * @return a {@code SimplePropertySource} built with parameters of this {@code SimplePropertySource.Builder}
         */
        public SimpleConfigSource build() {
            return new SimpleConfigSource(this);
        }
    }
}
