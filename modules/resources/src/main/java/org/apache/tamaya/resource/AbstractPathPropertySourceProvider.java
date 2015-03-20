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

import org.apache.tamaya.spi.PropertySource;
import org.apache.tamaya.spi.PropertySourceProvider;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Abstract base class that uses a descriptive resource path to define the locations of configuration files to be
 * included into the configuration. This is especially useful, when the current configuration policy in place
 * does not define the exact file names, but the file locations, where configuration can be provided.
 */
public abstract class AbstractPathPropertySourceProvider implements PropertySourceProvider{
    /** The log used. */
    private static final Logger LOG = Logger.getLogger(AbstractPathPropertySourceProvider.class.getName());
    /** The resource paths. */
    private String[] resourcePaths;


    /**
     * Creates a new instance using the given resource paths.
     * @param resourcePaths the resource paths, not null, not empty.
     */
    public AbstractPathPropertySourceProvider(String... resourcePaths){
        if(resourcePaths.length==0){
            throw new IllegalArgumentException("At least one resource path should be configured.");
        }
        this.resourcePaths = resourcePaths.clone();
    }

    @Override
    public Collection<PropertySource> getPropertySources() {
        List<PropertySource> propertySources = new ArrayList<>();
        for(String resource:resourcePaths) {
            try {
                Collection<URL> resources = ConfigResources.getResourceResolver().getResources(resource);
                for (URL url : resources) {
                    try {
                        PropertySource propertySource = getPropertySource(url);
                        if(propertySource!=null){
                            propertySources.add(propertySource);
                        }
                    } catch (Exception e) {
                        LOG.log(Level.WARNING, "Failed to read configuration from " + url, e);
                    }
                }
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "Invalid resource path" + resource, e);
            }
        }
        return propertySources;
    }

    /**
     * Factory method that creates a {@link org.apache.tamaya.spi.PropertySource} based on the URL found by
     * the resource locator.
     * @param url the URL, not null.
     * @return the {@link org.apache.tamaya.spi.PropertySource} to be included into the current provider's sources
     * list. It is safe to return {@code null} here, in case the content of the URL has shown to be not relevant
     * as configuration input. In case the input is not valid or accessible an exception can be thrown or logged.
     */
    protected abstract PropertySource getPropertySource(URL url);

    /**
     * Utility method that reads a .properties file from the given url and creates a corresponding
     * {@link org.apache.tamaya.spi.PropertySource}.
     * @param url the url to read, not null.
     * @return the corresponding PropertySource, or null.
     */
    public static PropertySource createPropertiesPropertySource(URL url) {
        Properties props = new Properties();
        try(InputStream is = url.openStream()){
            props.load(is);
            return new PropertiesBasedPropertySource(url.toString(), props);
        }
        catch(Exception e){
            LOG.log(Level.WARNING, "Failed to read properties from " + url, e);
            return null;
        }
    }

    /**
     * Minimal {@link PropertySource} implementation based on {@link Properties} or
     * {@link Map}.
     */
    private final static class PropertiesBasedPropertySource implements PropertySource{
        /** The property source's name. */
        private String name;
        /** The properties. */
        private Map<String,String> properties = new HashMap<>();

        /**
         * Constructor for a simple properties configuration.
         * @param name the source's name, not null
         * @param props the properties, not null
         */
        public PropertiesBasedPropertySource(String name, Properties props) {
            this.name = name;
            props.forEach((k,v) -> this.properties.put(k.toString(), v.toString()));
        }

        /**
         * Constructor for a simple properties configuration.
         * @param name the source's name, not null
         * @param props the properties, not null
         */
        public PropertiesBasedPropertySource(String name, Map<String,String> props) {
            this.name = Objects.requireNonNull(name);
            this.properties.putAll(props);
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String get(String key) {
            return properties.get(key);
        }

        @Override
        public Map<String, String> getProperties() {
            return properties;
        }

        @Override
        public String toString(){
            return "PropertiesBasedPropertySource["+name+']';
        }
    }

}
