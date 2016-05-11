///*
// * Licensed to the Apache Software Foundation (ASF) under one or more
// * contributor license agreements.  See the NOTICE file distributed with
// * this work for additional information regarding copyright ownership.
// * The ASF licenses this file to You under the Apache License, Version 2.0
// * (the "License"); you may not use this file except in compliance with
// * the License.  You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// *  Unless required by applicable law or agreed to in writing, software
// *  distributed under the License is distributed on an "AS IS" BASIS,
// *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// *  See the License for the specific language governing permissions and
// *  limitations under the License.
// */
//package org.apache.tamaya.staged.spi;
//
//
//import org.apache.tamaya.spisupport.MapPropertySource;
//import org.apache.tamaya.spisupport.PropertiesResourcePropertySource;
//import org.apache.tamaya.resource.ConfigResources;
//import org.apache.tamaya.spi.PropertySource;
//
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.List;
//import java.util.logging.Logger;
//
///**
// * Configuration provider that resolves to a location in the classpath.
// * Hereby the following system properties can be set to configure the provider
// * (all entries are optional):
// * <pre>
// *     env.STAGE   :   ordered list of configs to be loaded, e.g. sys-env,GLOBAL,TEST,DEV
// * </pre>
// * Adding {@code sys-env} as stage maps the current environment properties using
// * the priority to be aliged with the context ordering, defined by {@code env.STAGE}.
// * Similarly the same thing can be done by passing {@code sys-props} as context id for
// * adding the current System properties to the configuration tree.
// *
// * The rootContext can be used to remap the whole property space to an alternate subtree in
// * the configuration tree overall. This is very handy, if multiple instances of this class
// * are registered into the same configuration, but with different location setups. Remapping
// * configuration allows to separate these entries clearly.<br/>
// * Finally the resource location can be adapted by overriding {@link #getBaseResourcePath()}.
// * Different formats and loading mechanisms can be implemented by overriding
// * {@link #loadProperties(String, String, int, List)}.
// */
//public class StagedConfigPropertiesProvider extends BaseStagedPropertySourceProvider {
//
//    /** The system property to define the stages used. */
//    private static final String STAGE_PROP = "env.STAGE";
//    /** The logger used. */
//
//    private static final Logger LOGGER = Logger.getLogger(StagedConfigPropertiesProvider.class.getName());
//
//    /** The context id for adding the system's environment properties. */
//    private static final String DEFAULT_ENV = "sys-env";
//
//    /** The context id for adding the system's properties. */
//    private static final String DEFAULT_SYSPROPS = "sys-props";
//
//    /**
//     * Creates a new instance.
//     * @param rootContext the (optional) root context, can be null.
//     * @param stages the comma separated list of stages.
//     */
//    public StagedConfigPropertiesProvider(String rootContext, String... stages){
//        super(rootContext, evaluateStages(stages));
//    }
//
//    /**
//     * Creates a default instance. the stages are read from the {@code env.STAGE} system¨propertx
//     * or a default is applied.
//     */
//    public StagedConfigPropertiesProvider(){
//        super(null, evaluateStages(null));
//    }
//
//    /**
//     * Evaluates the stages or returns the default STAGE entry.
//     * @return the stages to be used, never null.
//     */
//    private static String[] evaluateStages(String[] stages) {
//        if(stages!=null && stages.length>0){
//            return stages.clone();
//        }
//        String value = System.getProperty(STAGE_PROP);
//        if(value==null) {
//            value = System.getenv(STAGE_PROP);
//        }
//        if(value==null){
//            value = "sys-env,GLOBAL,DEVELOPMENT,sys-props";
//        }
//        return value.split(",");
//    }
//
//    @Override
//    protected Collection<PropertySource> loadStageProperties(
//            String rootContext, String contextId, int priority) {
//        List<PropertySource> result = new ArrayList<>();
//        if (DEFAULT_ENV.equals(contextId)){
//            result.add(new MapPropertySource(DEFAULT_ENV, System.getenv(),
//                    rootContext, priority));
//        }else if (DEFAULT_SYSPROPS.equals(contextId)){
//            result.add(new MapPropertySource(DEFAULT_SYSPROPS, System.getProperties(),
//                    rootContext, priority));
//        }
//        else{
//            loadProperties(rootContext, contextId, priority, result);
//        }
//        return result;
//    }
//
//    private void loadProperties(String rootContext, String contextId, int priority,
//                                List<PropertySource> result) {
//        String cpExp = getBaseResourcePath()+'/' +contextId+".properties";
//        if(cpExp.startsWith("/")){
//            cpExp = cpExp.substring(1);
//        }
//        for(URL url: ConfigResources.getResourceResolver().getResources(cpExp)){
//            result.add(new PropertiesResourcePropertySource(rootContext, url, priority));
//        }
//    }
//
//    /**
//     * Get the basic resource path used for lookup of properties files.
//     * @return the basic resource path, never null.
//     */
//    protected String getBaseResourcePath() {
//        return "";
//    }
//
//
//}