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
package org.apache.tamaya.spisupport.propertysource;

import org.apache.tamaya.spi.ChangeSupport;
import org.apache.tamaya.spi.PropertySource;
import org.apache.tamaya.spi.PropertyValue;
import org.apache.tamaya.spi.ServiceContextManager;
import org.apache.tamaya.spisupport.PropertySourceChangeSupport;

import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Simple {@link org.apache.tamaya.spi.PropertySource}, with a fixed ordinal that reads a .properties file from a given URL.
 */
public class PropertiesResourcePropertySource extends BasePropertySource {
    /** The logger used. */
    private static final Logger LOGGER = Logger.getLogger(PropertiesResourcePropertySource.class.getName());

    private volatile PropertySourceChangeSupport cachedProperties = new PropertySourceChangeSupport(
            ChangeSupport.SUPPORTED, this);

    /**
     * Creates a new instance.
     * @param url the resource URL, not null.
     */
    public PropertiesResourcePropertySource(URL url){
        this(url, null);
    }

    /**
     * Creates a new instance.
     * @param prefix the (optional) prefix context for mapping (prefixing) the properties loaded.
     * @param url the resource URL, not null.
     */
    public PropertiesResourcePropertySource(URL url, String prefix){
        super(url.toExternalForm());
        setPrefix(prefix);
        this.cachedProperties.update(loadProps(url));
        this.cachedProperties.scheduleChangeMonitor(() -> loadProps(url),
                120, TimeUnit.SECONDS);
    }

    /**
     * Creates a new instance.
     * @param prefix the (optional) prefix context for mapping (prefixing) the properties loaded.
     * @param path the resource path, not null.
     */
    public PropertiesResourcePropertySource(String path, String prefix){
        this(path, prefix, ServiceContextManager.getDefaultClassLoader());
    }

    /**
     * Creates a new instance.
     * @param prefix the (optional) prefix context for mapping (prefixing) the properties loaded.
     * @param path the resource path, not null.
     * @param cl the class loader.
     */
    public PropertiesResourcePropertySource(String path, String prefix, ClassLoader cl){
        super(path);
        setPrefix(prefix);
        this.cachedProperties.update(loadProps(path, cl));
        this.cachedProperties.scheduleChangeMonitor(() -> loadProps(path, cl),
                120, TimeUnit.SECONDS);
    }

    /**
     * Loads the properties using the JDK's Property loading mechanism.
     * @param path the resource classpath, not null.
     * @return the loaded properties.
     */
    private Map<String, PropertyValue> loadProps(String path, ClassLoader cl) {
        URL url = ServiceContextManager.getServiceContext(cl).getResource(path);
        return loadProps(url);
    }

    /**
     * Loads the properties using the JDK's Property loading mechanism.
     * @param url the resource URL, not null.
     * @return the loaded properties.
     */
    private Map<String, PropertyValue> loadProps(URL url) {
        if(url!=null) {
            try (InputStream is = url.openStream()) {
                Properties props = new Properties();
                props.load(is);
                return mapProperties(MapPropertySource.getMap(props), System.currentTimeMillis());
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Failed to read properties from " + url, e);
            }
        }else{
            LOGGER.log(Level.WARNING, "No properties found at " + url);
        }
        return Collections.emptyMap();
    }

    @Override
    public void addChangeListener(BiConsumer<Set<String>, PropertySource> l) {
        this.cachedProperties.addChangeListener(l);
    }

    @Override
    public void removeChangeListener(BiConsumer<Set<String>, PropertySource> l) {
        this.cachedProperties.removeChangeListener(l);
    }

    @Override
    public void removeAllChangeListeners() {
        this.cachedProperties.removeAllChangeListeners();
    }

    @Override
    public Map<String, PropertyValue> getProperties() {
        return cachedProperties.getProperties();
    }

    @Override
    public String getVersion() {
        return cachedProperties.getVersion();
    }

    @Override
    public ChangeSupport getChangeSupport() {
        return ChangeSupport.SUPPORTED;
    }
}
