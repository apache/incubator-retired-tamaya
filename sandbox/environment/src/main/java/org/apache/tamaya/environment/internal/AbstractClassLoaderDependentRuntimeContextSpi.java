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

import org.apache.tamaya.environment.RuntimeContext;
import org.apache.tamaya.environment.RuntimeContextBuilder;
import org.apache.tamaya.environment.spi.ContextProviderSpi;
import org.apache.tamaya.format.ConfigurationFormat;
import org.apache.tamaya.format.ConfigurationFormats;
import org.apache.tamaya.resource.ConfigResources;

import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Application environment provider that is dependent on the current context classloader and tries to
 * evaluate {@code META-INF/env/application.properties, META-INF/env/application.xml and META-INF/env/application.ini}.
 * Only if a property named {@code org.apache.tamaya.env.applicationId} is found, it will
 * be used as the {@code environmentId} and a corresponding {@link org.apache.tamaya.environment.RuntimeContext} instance
 * is created and attached.
 */
public abstract class AbstractClassLoaderDependentRuntimeContextSpi implements ContextProviderSpi {

    private static final Logger LOG = Logger.getLogger(AbstractClassLoaderDependentRuntimeContextSpi.class.getName());

    private String contextId;
    private Map<ClassLoader, Map<String, String>> contexts = new ConcurrentHashMap<>();
    private Map<ClassLoader, Boolean> contextsAvailable = new ConcurrentHashMap<>();

    protected AbstractClassLoaderDependentRuntimeContextSpi(String contextId) {
        this.contextId = Objects.requireNonNull(contextId);
    }

    private boolean isActive() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) {
            return false;
        }
        Boolean available = this.contextsAvailable.get(cl);
        if (available != null) {
            return available;
        }
        return true;
    }

    protected Collection<URL> getConfigLocations() {
        return ConfigResources.getResourceResolver().getResources(Thread.currentThread().getContextClassLoader(),
                "classpath:META-INF/context/" + contextId + ".properties", "classpath:META-INF/context/\"+contextId+\".xml", "classpath:META-INF/context/\"+contextId+\".ini");
    }

    protected List<ConfigurationFormat> getConfigFormats(URL url) {
        return ConfigurationFormats.getFormats(url);
    }

    @Override
    public void setupContext(RuntimeContextBuilder contextBuilder) {
        if (isActive()) {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            if (cl == null) {
                return;
            }
            Map<String, String> data = this.contexts.get(cl);
            if (data == null) {
                Collection<URL> propertyUris = getConfigLocations();
                data = new HashMap<>();
                for (URL resource : propertyUris) {
                    for (ConfigurationFormat format : getConfigFormats(resource)) {
                        try (InputStream is = resource.openStream()) {
                            data.putAll(format.readConfiguration(resource.toExternalForm(), is).getDefaultSection());
                        } catch (Exception e) {
                            LOG.log(Level.SEVERE, e, () -> "Error reading application context data fromMap " + resource);
                        }
                    }
                }
                data.put("classloader.type", cl.getClass().getName());
                data.put("classloader.info", cl.toString());
                Set<URL> uris = new HashSet<>();
                uris.addAll(propertyUris);
                data.put("context.sources", uris.toString());
                data = Collections.unmodifiableMap(data);
                contextBuilder.setContextId(contextId);
                this.contexts.put(cl, data);
            }
            contextBuilder.setAll(data);
        }
    }

}
