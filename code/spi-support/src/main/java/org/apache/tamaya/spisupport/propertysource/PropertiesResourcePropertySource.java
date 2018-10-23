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

import org.apache.tamaya.spi.ServiceContextManager;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Simple {@link org.apache.tamaya.spi.PropertySource}, with a fixed ordinal that reads a .properties file from a given URL.
 */
public class PropertiesResourcePropertySource extends MapPropertySource {
    /** The logger used. */
    private static final Logger LOGGER = Logger.getLogger(PropertiesResourcePropertySource.class.getName());

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
        super(url.toExternalForm(), loadProps(url), prefix);
    }

    /**
     * Creates a new instance.
     * @param prefix the (optional) prefix context for mapping (prefixing) the properties loaded.
     * @param path the resource path, not null.
     */
    public PropertiesResourcePropertySource(String path, String prefix){
        super(path, loadProps(path, Thread.currentThread().getContextClassLoader()), prefix);
    }

    /**
     * Creates a new instance.
     * @param prefix the (optional) prefix context for mapping (prefixing) the properties loaded.
     * @param path the resource path, not null.
     * @param cl the class loader.
     */
    public PropertiesResourcePropertySource(String path, String prefix, ClassLoader cl){
        super(path, loadProps(path, cl), prefix);
    }

    /**
     * Loads the properties using the JDK's Property loading mechanism.
     * @param path the resource classpath, not null.
     * @return the loaded properties.
     */
    private static Map<String, String> loadProps(String path, ClassLoader cl) {
        URL url = ServiceContextManager.getServiceContext(cl).getResource(path);
        return loadProps(url);
    }

    /**
     * Loads the properties using the JDK's Property loading mechanism.
     * @param url the resource URL, not null.
     * @return the loaded properties.
     */
    private static Map<String, String> loadProps(URL url) {
        Map<String,String> result = new HashMap<>();
        if(url!=null) {
            try (InputStream is = url.openStream()) {
                Properties props = new Properties();
                props.load(is);
                for (Map.Entry<?,?> en : props.entrySet()) {
                    result.put(en.getKey().toString(), en.getValue().toString());
                }
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Failed to read properties from " + url, e);
            }
        }else{
            LOGGER.log(Level.WARNING, "No properties found at " + url);
        }
        return result;
    }

}
