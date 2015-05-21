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
package org.apache.tamaya.environment.internal;

import org.apache.tamaya.environment.RuntimeContextBuilder;
import org.apache.tamaya.environment.spi.ContextProviderSpi;
import org.apache.tamaya.format.ConfigurationFormat;
import org.apache.tamaya.format.ConfigurationFormats;
import org.apache.tamaya.resource.ConfigResources;

import javax.annotation.Priority;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * System environment provider (loaded only once using the system class loader) that loads additional environment properties fromMap the classpath evaluating
 * {@code META-INF/env/system.properties, META-INF/env/system.xml and META-INF/env/system.ini}.
 */
@Priority(1000)
public class SystemClassLoaderEnvironmentProviderSpi implements ContextProviderSpi {

    private static final Logger LOG = Logger.getLogger(SystemClassLoaderEnvironmentProviderSpi.class.getName());


    private Map<String,String> data = new HashMap<>();

    public SystemClassLoaderEnvironmentProviderSpi(){
        if (data == null) {
            Collection<URL> propertyUris = getConfigLocations();
            data = new HashMap<>();
            for (URL resource : propertyUris) {
                for (ConfigurationFormat format : getConfigFormats(resource)) {
                    try (InputStream is = resource.openStream()) {
                        data.putAll(format.readConfiguration(resource.toExternalForm(), is).getDefaultSection());
                    } catch (Exception e) {
                        LOG.log(Level.SEVERE, e, () -> "Error reading application context data from " + resource);
                    }
                }
            }
            data.put("classloader.type", ClassLoader.getSystemClassLoader().getClass().getName());
            data.put("classloader.info", "System-Classloader");
            Set<URL> uris = new HashSet<>();
            uris.addAll(propertyUris);
            data.put("context.sources", uris.toString());
            data = Collections.unmodifiableMap(data);
            this.data = Collections.unmodifiableMap(data);
        }
    }

    protected List<ConfigurationFormat> getConfigFormats(URL url) {
        return ConfigurationFormats.getFormats(url);
    }

    protected Collection<URL> getConfigLocations() {
        return ConfigResources.getResourceResolver().getResources(ClassLoader.getSystemClassLoader(),
                "classpath:META-INF/context/system.properties", "classpath:META-INF/context/system.xml",
                "classpath:META-INF/context/system.ini");
    }


    @Override
    public void setupContext(RuntimeContextBuilder contextBuilder) {
        if (!data.isEmpty()) {
            contextBuilder.setAll(data).build();
            contextBuilder.setContextId("system");
        }
    }

}
