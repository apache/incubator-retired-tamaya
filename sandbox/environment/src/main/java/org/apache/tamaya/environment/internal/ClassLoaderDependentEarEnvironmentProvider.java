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
package org.apache.tamaya.metamodel.environment.internal;

import org.apache.tamaya.core.config.ConfigurationFormats;
import org.apache.tamaya.core.resource.Resource;
import org.apache.tamaya.metamodel.environment.RuntimeContextBuilder;
import org.apache.tamaya.metamodel.environment.spi.EnvironmentProvider;
import org.apache.tamaya.spi.ServiceContext;
import org.apache.tamaya.core.properties.ConfigurationFormat;
import org.apache.tamaya.core.resource.ResourceLoader;


import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements a {@link EnvironmentProvider} that tries
 * to read configuration for an ear deployment located under {@code META-INF/env/ear.properties,
 * META-INF/env/ear.xml or META-INF/env/ear.ini}. The environment id hereby is defined by a
 * configuration entry named {@code org.apache.tamaya.core.env.earId}.
 *
 * Only if such a configuration with such an {@code earId} is found an {@link org.apache.tamaya.metamodel.environment.RuntimeContext}
 * is created and attached to the corresponding ear classloader.
 */
public class ClassLoaderDependentEarEnvironmentProvider implements EnvironmentProvider {

    private static  final Logger LOG = Logger.getLogger(ClassLoaderDependentEarEnvironmentProvider.class.getName());

//    private static final String EARID_PROP = "environment.earId";

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
        List<Resource> propertyUris = ServiceContext.getInstance().getSingleton(ResourceLoader.class).getResources(cl,
                "classpath:META-INF/context/ear.properties", "classpath:META-INF/context/ear.xml", "classpath:META-INF/context/ear.ini");
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
        List<Resource> resources = ServiceContext.getInstance().getSingleton(ResourceLoader.class).getResources(cl,
                "classpath:META-INF/context/ear.properties", "classpath:META-INF/context/ear.xml", "classpath:META-INF/context/ear.ini");
        data = new HashMap<>();
        for(Resource resource:resources){
            try{
                ConfigurationFormat format = ConfigurationFormats.getFormat(resource);
                Map<String,String> read = format.readConfiguration(resource);
                data.putAll(read);
            }
            catch(Exception e){
                LOG.log(Level.SEVERE, e, () -> "Error reading ear context data fromMap " + resource);
            }
        }
//        String earId = data.getOrDefault(EARID_PROP, cl.toString());
        String stageValue =  data.get(RuntimeContextBuilder.STAGE_PROP);
        if (stageValue != null) {
            data.put(RuntimeContextBuilder.STAGE_PROP,stageValue);
        }
        data.put("classloader.type", cl.getClass().getName());
        data.put("classloader.info", cl.toString());
        Set<Resource> resourceSet = new HashSet<>();
        resourceSet.addAll(resources);
        data.put("context.sources", resourceSet.toString());
        data = Collections.unmodifiableMap(data);
        this.contexts.put(cl, data);
        return data;
    }

}
