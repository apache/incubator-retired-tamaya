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
import org.apache.tamaya.Stage;
import org.apache.tamaya.core.config.ConfigurationFormats;
import org.apache.tamaya.core.env.EnvironmentBuilder;
import org.apache.tamaya.core.resource.Resource;
import org.apache.tamaya.spi.Bootstrap;
import org.apache.tamaya.core.spi.ConfigurationFormat;
import org.apache.tamaya.core.spi.EnvironmentProvider;
import org.apache.tamaya.core.resource.ResourceLoader;


import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements a {@link org.apache.tamaya.core.spi.EnvironmentProvider} that tries
 * to read configuration for an ear deployment located under {@code META-INF/env/ear.properties,
 * META-INF/env/ear.xml or META-INF/env/ear.ini}. The environment id hereby is defined by a
 * configuration entry named {@code org.apache.tamaya.core.env.earId}.
 *
 * Only if such a configuration with such an {@code earId} is found an {@link org.apache.tamaya.Environment}
 * is created and attached to the corresponding ear classloader.
 */
public class ClassLoaderDependentEarEnvironmentProvider implements EnvironmentProvider {

    private static  final Logger LOG = Logger.getLogger(ClassLoaderDependentEarEnvironmentProvider.class.getName());

    private static final String EARID_PROP = "environment.earId";

    private Map<ClassLoader, Environment> environments = new ConcurrentHashMap<>();
    private Map<ClassLoader, Boolean> environmentAvailable = new ConcurrentHashMap<>();
    private Map<String, Environment> environmentsByEarId = new ConcurrentHashMap<>();

    @Override
    public String getEnvironmentType() {
        return "ear";
    }

    @Override
    public boolean isEnvironmentActive() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if(cl==null){
            return false;
        }
        Boolean available = this.environmentAvailable.get(cl);
        if(available!=null && !available){
            return false;
        }
        List<Resource> propertyUris = Bootstrap.getService(ResourceLoader.class).getResources(cl,
                "classpath:META-INF/env/ear.properties", "classpath:META-INF/env/ear.xml", "classpath:META-INF/env/ear.ini");
        available = !propertyUris.isEmpty();
        this.environmentAvailable.put(cl, available);
        return available;
    }

    @Override
    public Environment getEnvironment(Environment parentEnvironment) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if(cl==null){
            return null;
        }
        Environment environment = this.environments.get(cl);
        if(environment!=null){
            return environment;
        }
        List<Resource> resources = Bootstrap.getService(ResourceLoader.class).getResources(cl,
                "classpath:META-INF/env/ear.properties", "classpath:META-INF/env/ear.xml", "classpath:META-INF/env/ear.ini");
        Map<String,String> data = new HashMap<>();

        for(Resource resource:resources){
            try{
                ConfigurationFormat format = ConfigurationFormats.getFormat(resource);
                Map<String,String> read = format.readConfiguration(resource);
                data.putAll(read);
            }
            catch(Exception e){
                LOG.log(Level.SEVERE, e, () -> "Error reading ear environment data fromMap " + resource);
            }
        }
        String earId = data.getOrDefault(EARID_PROP, cl.toString());
        EnvironmentBuilder builder = EnvironmentBuilder.of(earId, getEnvironmentType());
        builder.setParent(parentEnvironment);
        String stageValue =  data.get(InitialEnvironmentProvider.STAGE_PROP);
        if (stageValue != null) {
            Stage stage = Stage.of(stageValue);
            builder.setStage(stage);
        }
        builder.set("classloader.type", cl.getClass().getName());
        builder.set("classloader.info", cl.toString());
        Set<Resource> resourceSet = new HashSet<>();
        resourceSet.addAll(resources);
        builder.set("environment.sources", resourceSet.toString());
        builder.setAll(data);
        environment = builder.build();
        this.environments.put(cl, environment);
        if(earId!=null) {
            this.environmentsByEarId.put(earId, environment);
        }
        return environment;
    }

    @Override
    public Set<String> getEnvironmentContexts() {
        return environmentsByEarId.keySet();
    }
}
