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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * System environment provider (loaded only once using the system class loader) that loads additional environment properties fromMap the classpath evaluating
 * {@code META-INF/env/system.properties, META-INF/env/system.xml and META-INF/env/system.ini}.
 */
public class SystemClassLoaderEnvironmentProvider implements EnvironmentProvider {

    private static  final Logger LOG = Logger.getLogger(SystemClassLoaderEnvironmentProvider.class.getName());

    private Map<String,Environment> environments = new HashMap<>();

    @Override
    public String getEnvironmentType() {
        return "system";
    }

    @Override
    public boolean isEnvironmentActive() {
        return true;
    }

    @Override
    public Environment getEnvironment(Environment parentEnvironment) {
        Environment env = this.environments.get("system");
        if(env!=null){
            return env;
        }
        List<Resource> propertyResources = Bootstrap.getService(ResourceLoader.class).getResources(ClassLoader.getSystemClassLoader(),
                "classpath:META-INF/env/system.properties", "classpath:META-INF/env/system.xml", "classpath:META-INF/env/system.ini");
        EnvironmentBuilder builder = EnvironmentBuilder.of("system", getEnvironmentType());
        for(Resource resource:propertyResources){
            try{
                ConfigurationFormat format = ConfigurationFormats.getFormat(resource);
                Map<String,String> data = format.readConfiguration(resource);
                builder.setAll(data);
            }
            catch(Exception e){
                LOG.log(Level.INFO, e, () -> "Could not read environment data from " + resource);
            }
        }
        builder.setParent(parentEnvironment);
        String stageValue =  builder.getProperty(InitialEnvironmentProvider.STAGE_PROP);
        Stage stage = InitialEnvironmentProvider.DEFAULT_STAGE;
        if (stageValue != null) {
            stage = Stage.valueOf(stageValue);
        }
        builder.setStage(stage);
        builder.set("classloader.type", ClassLoader.getSystemClassLoader().getClass().getName());
        builder.set("classloader.info", ClassLoader.getSystemClassLoader().toString());
        Set<Resource> resourceSet = new HashSet<>();
        resourceSet.addAll(propertyResources);
        builder.set("environment.sources", resourceSet.toString());
        env = builder.build();
        this.environments.put("system", env);
        return env;
    }

    @Override
    public Set<String> getEnvironmentContexts() {
        return this.environments.keySet();
    }

}
