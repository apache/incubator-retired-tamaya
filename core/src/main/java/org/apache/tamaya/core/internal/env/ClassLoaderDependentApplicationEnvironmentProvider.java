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
package org.apache.tamaya.core.internal.env;

import org.apache.tamaya.Environment;
import org.apache.tamaya.core.config.ConfigurationFormats;
import org.apache.tamaya.core.env.EnvironmentBuilder;
import org.apache.tamaya.core.resource.Resource;
import org.apache.tamaya.spi.ServiceContext;
import org.apache.tamaya.core.spi.ConfigurationFormat;
import org.apache.tamaya.core.spi.EnvironmentProvider;
import org.apache.tamaya.core.resource.ResourceLoader;


import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Application environment provider that is dependent on the current context classloader and tries to
 * evaluate {@code META-INF/env/application.properties, META-INF/env/application.xml and META-INF/env/application.ini}.
 * Only if a property named {@code org.apache.tamaya.env.applicationId} is found, it will
 * be used as the {@code environmentId} and a corresponding {@link org.apache.tamaya.Environment} instance
 * is created and attached.
 */
public class ClassLoaderDependentApplicationEnvironmentProvider implements EnvironmentProvider {

    private static  final Logger LOG = Logger.getLogger(ClassLoaderDependentApplicationEnvironmentProvider.class.getName());

    private static final String WARID_PROP = "environment.applicationId";

    private Map<ClassLoader, Map<String,String>> environments = new ConcurrentHashMap<>();
    private Map<ClassLoader, Boolean> environmentAvailable = new ConcurrentHashMap<>();

    @Override
    public boolean isActive() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if(cl==null){
            return false;
        }
        Boolean available = this.environmentAvailable.get(cl);
        if(available!=null && !available){
            return false;
        }
        List<Resource> propertyUris = ServiceContext.getInstance().getSingleton(ResourceLoader.class).getResources(cl,
                "classpath:META-INF/env/application.properties", "classpath:META-INF/env/application.xml", "classpath:META-INF/env/application.ini");
        available = !propertyUris.isEmpty();
        this.environmentAvailable.put(cl, available);
        return available;
    }

    @Override
    public Map<String,String> getEnvironmentData() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if(cl==null){
            return null;
        }
        Map<String,String> data = this.environments.get(cl);
        if(data!=null){
            return data;
        }
        List<Resource> propertyUris = ServiceContext.getInstance().getSingleton(ResourceLoader.class).getResources(cl,
                "classpath:META-INF/env/application.properties", "classpath:META-INF/env/application.xml", "classpath:META-INF/env/application.ini");
        data = new HashMap();

        for(Resource resource:propertyUris){
            try{
                ConfigurationFormat format = ConfigurationFormats.getFormat(resource);
                data.putAll(format.readConfiguration(resource));
            }
            catch(Exception e){
                LOG.log(Level.SEVERE, e, () -> "Error reading application environment data fromMap " + resource);
            }
        }
        String applicationId = data.getOrDefault(WARID_PROP, cl.toString());
        data.put("classloader.type", cl.getClass().getName());
        data.put("classloader.info", cl.toString());
        Set<Resource> uris = new HashSet<>();
        uris.addAll(propertyUris);
        data.put("environment.sources", uris.toString());
        data = Collections.unmodifiableMap(data);
        this.environments.put(cl, data);
        return data;
    }
}
