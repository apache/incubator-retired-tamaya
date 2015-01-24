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
package org.apache.tamaya.core.provider;

import static org.apache.tamaya.core.internal.PropertiesFileLoader.resolvePropertiesFiles;

import org.apache.tamaya.ConfigException;
import org.apache.tamaya.core.propertysource.PropertiesFilePropertySource;
import org.apache.tamaya.spi.PropertySource;
import org.apache.tamaya.spi.PropertySourceProvider;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Provider which reads all {@code javaconfiguration.properties} files from classpath
 */
public class JavaConfigurationProvider implements PropertySourceProvider {


    @Override
    public Collection<PropertySource> getPropertySources() {

        List<PropertySource> propertySources = new ArrayList<>();

        //X TODO maybe put javaconf... in META-INF

        try {
            for (URL url : resolvePropertiesFiles("javaconfiguration.properties")) {
                propertySources.add(new PropertiesFilePropertySource(url));
            }
        } catch (IOException e) {
            throw new ConfigException("Error while loading javaconfiguration.properties", e);
        }

        return Collections.unmodifiableList(propertySources);
    }
}
