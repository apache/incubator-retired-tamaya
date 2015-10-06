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
package org.apache.tamaya.environment.spi;


import org.apache.tamaya.ConfigException;
import org.apache.tamaya.spi.PropertySource;
import org.apache.tamaya.spi.PropertySourceProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

/**
 * Implements a base property source that defines a multilayered environment setup.
 */
public abstract class BaseEnvironmentPropertySourceProvider implements PropertySourceProvider {

    private static final Logger LOGGER = Logger.getLogger(BaseEnvironmentPropertySourceProvider.class.getName());

    private int basePriority;

    private int priorityIncrease;

    private List<String> environmentIds = new ArrayList<>();

    private String environmentRootContext;

    private List<PropertySource> propertySources = new ArrayList<>();


    /**
     * Creates a new Environment provider, hereby using 10 for basePriority and priorityIncrease.
     * @param environmentRootContext the environment target root context, e.g. ENV. or null
     *                               for not remapping the environment properties.
     * @param environmentIds the context ids, that build up the environment.
     */
    public BaseEnvironmentPropertySourceProvider(String environmentRootContext,
                                                 String... environmentIds) {
        this(environmentRootContext, 10,10,environmentIds);
    }
        /**
         * Creates a new Environment provider.
         * @param environmentRootContext the environment target root context, e.g. ENV. or null
         *                               for not remapping the environment properties.
         * @param basePriority the base priority used for the weakest environment properties set.
         * @param priorityIncrease the value the property source's priority should be increased with each
         *                         environment context level added.
         * @param environmentIds the context ids, that build up the environment.
         */
    public BaseEnvironmentPropertySourceProvider(String environmentRootContext, int basePriority, int priorityIncrease,
                                                 String... environmentIds) {
        this.basePriority = basePriority;
        this.priorityIncrease = priorityIncrease;
        if (environmentIds.length == 0) {
            throw new ConfigException("At least one environment context id must be defined.");
        }
        if (environmentRootContext == null) {
            LOGGER.finest("No environment mapping is applied.");
        }
        this.environmentRootContext = environmentRootContext;
        this.environmentIds.addAll(Arrays.asList(environmentIds));
        int priority = basePriority;
        for (String contextId : environmentIds) {
            propertySources.addAll(loadEnvProperties(environmentRootContext, contextId, priority));
            priority += priorityIncrease;
        }
    }

    /**
     * Method that loads the environment properties for the given contextId.
     * @param environmentRootContext the root context, where entries should be mapped to.
     * @param contextId the contextId.
     * @param priority  the target priority the created PropertySource should have. This priority is important, since it reflects the
     *                  environment context overriding rules for the environment part created.
     * @return the corresponding PrioritySources to be added, never null.
     */
    protected abstract Collection<PropertySource> loadEnvProperties(
            String environmentRootContext, String contextId, int priority);

    /**
     * Get the environment context ids that define how this environment configuration
     * is setup, in order of priority increasing.
     * @return the ordered list of context ids.
     */
    public List<String> getEnvironmentIds() {
        return environmentIds;
    }

    @Override
    public String toString() {
        return "EnvironmentPropertySourceProvider{" +
                "environmentIds=" + environmentIds +
                ", environmentRootContext='" + environmentRootContext + '\'' +
                '}';
    }

    @Override
    public Collection<PropertySource> getPropertySources() {
        return propertySources;
    }

}