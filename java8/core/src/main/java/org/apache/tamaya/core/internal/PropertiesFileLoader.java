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
package org.apache.tamaya.core.internal;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Objects;
import java.util.Properties;

/**
 * Minimalistic loader for property files from the classpath.
 */
public final class PropertiesFileLoader {


    private PropertiesFileLoader() {
        // no instantiation
    }


    /**
     * loads all properties-files with the given name.
     * If the name do not end with {@code .properties} it will be appended
     *
     * @param name of the properties file
     *
     * @return {@link Enumeration} of {@link URL}s for properties-files
     *
     * @throws IOException in case of problems loading the properties-files
     */
    public static Enumeration<URL> resolvePropertiesFiles(String name) throws IOException {
        Objects.requireNonNull(name);

        if (!name.endsWith(".properties")) {
            name = name + ".properties";
        }

        return Thread.currentThread().getContextClassLoader().getResources(name);
    }


    /**
     * loads the Properties from the given URL
     *
     * @param propertiesFile {@link URL} to load Properties from
     *
     * @return loaded {@link Properties}
     *
     * @throws IllegalStateException in case of an error while reading properties-file
     */
    public static Properties load(URL propertiesFile) {

        Properties properties = new Properties();

        InputStream stream = null;
        try {
            stream = propertiesFile.openStream();

            if (stream != null) {
                properties.load(stream);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Error loading Properties " + propertiesFile, e);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    // bad luck -> stream is already closed
                }
            }
        }

        return properties;
    }

}
