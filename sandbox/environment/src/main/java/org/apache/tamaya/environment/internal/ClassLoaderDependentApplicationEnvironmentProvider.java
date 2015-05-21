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

import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tamaya.core.config.ConfigurationFormats;
import org.apache.tamaya.core.resource.Resource;
import org.apache.tamaya.core.resource.ResourceLoader;
import org.apache.tamaya.core.properties.ConfigurationFormat;
import org.apache.tamaya.environment.spi.ContextDataProvider;
import org.apache.tamaya.metamodel.environment.spi.EnvironmentProvider;
import org.apache.tamaya.spi.ServiceContext;

/**
 * Application environment provider that is dependent on the current context classloader and tries to
 * evaluate {@code META-INF/env/application.properties, META-INF/env/application.xml and META-INF/env/application.ini}.
 * Only if a property named {@code org.apache.tamaya.env.applicationId} is found, it will
 * be used as the {@code environmentId} and a corresponding {@link org.apache.tamaya.metamodel.environment.RuntimeContext} instance
 * is created and attached.
 */
public class ClassLoaderDependentApplicationEnvironmentProvider implements ContextDataProvider {

    private static  final Logger LOG = Logger.getLogger(ClassLoaderDependentApplicationEnvironmentProvider.class.getName());

    private Map<ClassLoader, Map<String,String>> contexts = new ConcurrentHashMap<>();
    private Map<ClassLoader, Boolean> contextsAvailable = new ConcurrentHashMap<>();

    @Override
    public boolean isActive() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if(cl==null){
            return false;
        }
        Boolean available = this.contextsAvailable.get(cl);
        if(available!=null && !available){
            return false;
        }
        List<URL> propertyUris = ServiceContext.getInstance().getSingleton(ResourceLoader.class).getResources(cl,
                "classpath:META-INF/context/application.properties", "classpath:META-INF/context/application.xml", "classpath:META-INF/context/application.ini");
        available = !propertyUris.isEmpty();
        this.contextsAvailable.put(cl, available);
        return available;
    }

    @Override
    public Map<String,String> getEnvironmentData() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if(cl==null){
            return null;
        }
        Map<String,String> data = this.contexts.get(cl);
        if(data!=null){
            return data;
        }
        List<URL> propertyUris = ServiceContext.getInstance().getSingleton(ResourceLoader.class).getResources(cl,
                "classpath:META-INF/context/application.properties", "classpath:META-INF/context/application.xml", "classpath:META-INF/context/application.ini");
        data = new HashMap<>();

        for(URL resource:propertyUris){
            try{
                ConfigurationFormat format = ConfigurationFormats.getFormat(resource);
                data.putAll(format.readConfiguration(resource));
            }
            catch(Exception e){
                LOG.log(Level.SEVERE, e, () -> "Error reading application context data fromMap " + resource);
            }
        }
        data.put("classloader.type", cl.getClass().getName());
        data.put("classloader.info", cl.toString());
        Set<URL> uris = new HashSet<>();
        uris.addAll(propertyUris);
        data.put("context.sources", uris.toString());
        data = Collections.unmodifiableMap(data);
        this.contexts.put(cl, data);
        return data;
    }
}
