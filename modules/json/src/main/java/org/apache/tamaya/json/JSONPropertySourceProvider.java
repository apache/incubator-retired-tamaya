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
package org.apache.tamaya.json;

import org.apache.tamaya.ConfigException;
import org.apache.tamaya.spi.PropertySource;
import org.apache.tamaya.spi.PropertySourceProvider;

import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Provides all <a href="http://json.org">JSON</a>
 * property sources found in the  classpath
 * in {@code META-INF/javaconfiguration.json}.
 *
 * @see PropertySourceProvider
 */
public class JSONPropertySourceProvider implements PropertySourceProvider {
    public final static String DEFAULT_RESOURCE_NAME = "javaconfiguration.json";

    @Override
    public Collection<PropertySource> getPropertySources() {
        List<PropertySource> sources;

        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Enumeration<URL> urls = classLoader.getResources("META-INF/" + DEFAULT_RESOURCE_NAME);

            sources = Collections.list(urls)
                                 .stream()
                                 .map(JSONPropertySource::new)
                                 .collect(toList());

        } catch (Exception e) {
            String msg = "Failure while loading JSON property sources.";

            throw new ConfigException(msg, e);
        }

        return sources;
    }
}
