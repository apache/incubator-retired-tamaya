/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tamaya.environment.internal;


import org.apache.tamaya.environment.spi.BaseEnvironmentPropertySourceProvider;
import org.apache.tamaya.spisupport.SimplePropertiesPropertySource;
import org.apache.tamaya.resource.ConfigResources;
import org.apache.tamaya.spi.PropertySource;
import org.apache.tamaya.spisupport.BasePropertySource;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Configuration provider that resolves to a location in the classpath.
 * Hereby the following system properties can be set to configure the provider
 * (all entries are optional):
 * <pre>
 *     env.STAGE   :   ordered list of configs to be loaded, e.g. GLOBAL,TEST,DEV
 *     env.ROOT    :   root context of the environment, by default ENV
 * </pre>
 * Adding {@code System.getenv()} as stage maps the current environment properties using
 * the priority to be aliged with the context ordering, defined by {@code env.STAGE}.
 */
public class ClasspathPropertiesEnvironmentProvider extends BaseEnvironmentPropertySourceProvider {

    private static final String STAGE_PROP = "env.STAGE";

    private static final String ROOT_PROP = "env.ROOT";

    private static final Logger LOGGER = Logger.getLogger(ClasspathPropertiesEnvironmentProvider.class.getName());

    private String rootLocation;

    private static final String DEFAULT_ENV = "System.getenv()";


    public ClasspathPropertiesEnvironmentProvider(){
        super(evaluateRoot(), evaluateStages());
    }

    private static String evaluateRoot() {
        String value = System.getProperty(ROOT_PROP);
        if(value==null) {
            value = System.getenv(ROOT_PROP);
        }
        if(value==null){
            value = "ENV";
        }
        return value;
    }

    private static String[] evaluateStages() {
        String value = System.getProperty(STAGE_PROP);
        if(value==null) {
            value = System.getenv(STAGE_PROP);
        }
        if(value==null){
            value = "System.getenv(),GLOBAL,DEVELOPMENT";
        }
        return value.split(",");
    }

    @Override
    protected Collection<PropertySource> loadEnvProperties(
            String environmentRootContext, String contextId, int priority) {
        List<PropertySource> result = new ArrayList<>();
        if (DEFAULT_ENV.equals(contextId)){
            result.add(new EnvPropertiesPropertySource(environmentRootContext, priority));
        }
        else{
            loadProperties(environmentRootContext, contextId, priority, result);
        }
        return result;
    }

    private void loadProperties(String environmentRootContext, String contextId, int priority, List<PropertySource> result) {
        String cpExp = getBaseResourcePath()+'/' +contextId+".properties";
        if(cpExp.startsWith("/")){
            cpExp = cpExp.substring(1);
        }
        for(URL url: ConfigResources.getResourceResolver().getResources(cpExp)){
            result.add(new SimplePropertiesPropertySource(environmentRootContext, url, priority));
        }
    }

    /**
     * Get the basic resource path used for lookup of properties files.
     * @return the basic resource path, never null.
     */
    protected String getBaseResourcePath() {
        return "";
    }

    private static final class EnvPropertiesPropertySource extends BasePropertySource{

        private final int priority;

        private Map<String, String> envProps = new HashMap<>();

        public EnvPropertiesPropertySource(int priority) {
            this(null, priority);
        }

        public EnvPropertiesPropertySource(String environmentRootContext, int priority){
            this.priority = priority;
            if(environmentRootContext==null){
                envProps.putAll(System.getenv());
            }
            else{
                for(Map.Entry<String,String> en: System.getenv().entrySet()){
                    String prefix = environmentRootContext;
                    if(!prefix.endsWith(".")){
                        prefix += ".";
                    }
                    envProps.put(prefix+en.getKey(), en.getValue());
                }
            }
            this.envProps = Collections.unmodifiableMap(envProps);
        }

        @Override
        public String getName() {
            return "System.getenv()";
       }

        @Override
        public Map<String, String> getProperties() {
            return this.envProps;
        }

        @Override
        public int getOrdinal(){
            return priority;
        }

        @Override
        public String toString() {
            return "EnvPropertiesPropertySource{" +
                    "priority=" + priority +
                    '}';
        }
    }


}