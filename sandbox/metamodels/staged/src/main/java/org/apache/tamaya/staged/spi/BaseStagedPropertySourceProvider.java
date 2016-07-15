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
//import org.apache.tamaya.ConfigException;
//import PropertySource;
//import PropertySourceProvider;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Collection;
//import java.util.List;
//import java.util.logging.Logger;
//
///**
// * Implements a base property source provider that defines a multilayered
// * stage setup.
// */
//public abstract class BaseStagedPropertySourceProvider implements PropertySourceProvider {
//
//    /** The logger used. */
//    private static final Logger LOGGER = Logger.getLogger(BaseStagedPropertySourceProvider.class.getName());
//
//    /** the current environment stages in order of precedence (weakest first). */
//    private List<String> contextIds = new ArrayList<>();
//
//    /** Optional root context of the environment in the config tree. All entries loaded will be mapped into
//     * this root conztext.
//     */
//    private String rootContext;
//
//    /** List of PropertySources evaluated and returned to the current ConfigurationContext on load. */
//    private List<PropertySource> propertySources = new ArrayList<>();
//
//
//    /**
//     * Creates a new Environment provider, hereby using 10 for basePriority and priorityIncrease.
//     * @param rootContext the environment target root context, e.g. ENV. or null
//     *                               for not remapping the environment properties.
//     * @param contextIds the context ids, that build up the environment.
//     */
//    public BaseStagedPropertySourceProvider(String rootContext,
//                                            String... contextIds) {
//        this(rootContext, 10,10, contextIds);
//    }
//        /**
//         * Creates a new Environment provider.
//         * @param rootContext the environment target root context, e.g. ENV. or null
//         *                               for not remapping the environment properties.
//         * @param basePriority the base priority used for the weakest environment properties set.
//         * @param priorityIncrease the value the property source's priority should be increased with each
//         *                         environment context level added.
//         * @param contextIds the context ids, that build up the environment.
//         */
//    public BaseStagedPropertySourceProvider(String rootContext, int basePriority, int priorityIncrease,
//                                            String... contextIds) {
//        if (contextIds.length == 0) {
//            throw new ConfigException("At least one environment context id must be defined.");
//        }
//        if (rootContext == null) {
//            LOGGER.finest("No environment mapping is applied.");
//        }
//        this.rootContext = rootContext;
//        this.contextIds.addAll(Arrays.asList(contextIds));
//        int priority = basePriority;
//        for (String contextId : contextIds) {
//            propertySources.addAll(loadStageProperties(rootContext, contextId, priority));
//            priority += priorityIncrease;
//        }
//    }
//
//    /**
//     * Method that loads the environment properties for the given contextId.
//     * @param rootContext the root context, where entries read should be mapped to.
//     * @param contextId the contextId.
//     * @param priority  the target priority the created PropertySource should have. This priority is
//     *                  important, since it reflects the order as defined
//     *                  when configuring this class. Therefore it should not be overridden normally.
//     * @return the corresponding PrioritySources to be added, never null.
//     */
//    protected abstract Collection<PropertySource> loadStageProperties(
//            String rootContext, String contextId, int priority);
//
//    /**
//     * Get the environment context ids that define how this environment configuration
//     * is setup, in order of their increasing priority.
//     * @return the ordered list of context ids.
//     */
//    public List<String> getContextIds() {
//        return contextIds;
//    }
//
//    @Override
//    public String toString() {
//        return "EnvironmentPropertySourceProvider{" +
//                "contextIds=" + contextIds +
//                ", rootContext='" + rootContext + '\'' +
//                '}';
//    }
//
//    @Override
//    public Collection<PropertySource> getPropertySources() {
//        return propertySources;
//    }
//
//}