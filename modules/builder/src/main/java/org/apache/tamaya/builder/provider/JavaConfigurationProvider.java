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
package org.apache.tamaya.builder.provider;

import org.apache.tamaya.ConfigException;
import org.apache.tamaya.builder.propertysource.SimplePropertySource;
import org.apache.tamaya.builder.spi.PropertySource;
import org.apache.tamaya.builder.spi.PropertySourceProvider;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

/**
 * Provider which reads all {@code javaconfiguration.properties} files from classpath. By setting
 * {@code tamaya.defaultprops.disable} or {@code tamaya.defaults.disable} as system or environment property this feature
 * can be disabled.
 */
public class JavaConfigurationProvider implements PropertySourceProvider {
    /** Default location in the classpath, where Tamaya looks for configuration by default. */
    public static final String DEFAULT_PROPERTIES_FILE_NAME="META-INF/javaconfiguration.properties";

    private final boolean disabled = evaluateDisabled();

    private boolean evaluateDisabled() {
        String value = System.getProperty("tamaya.defaultprops.disable");
        if(value==null){
            value = System.getenv("tamaya.defaultprops.disable");
        }
        if(value==null){
            value = System.getProperty("tamaya.defaults.disable");
        }
        if(value==null){
            value = System.getenv("tamaya.defaults.disable");
        }
        if(value==null){
            return false;
        }
        return value.isEmpty() || Boolean.parseBoolean(value);
    }

    @Override
    public Collection<PropertySource> getPropertySources() {
        if(disabled){
            return Collections.emptySet();
        }
        List<PropertySource> propertySources = new ArrayList<>();
        try {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            if(cl!=null) {
                Enumeration<URL> urls = Thread.currentThread().getContextClassLoader()
                        .getResources(DEFAULT_PROPERTIES_FILE_NAME);
                while (urls.hasMoreElements()) {
                    propertySources.add(new SimplePropertySource(urls.nextElement()));
                }
            }

        } catch (IOException e) {
            throw new ConfigException("Error while loading javaconfiguration.properties", e);
        }
        return Collections.unmodifiableList(propertySources);
    }


}
