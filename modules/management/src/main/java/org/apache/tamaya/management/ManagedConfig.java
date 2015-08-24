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
package org.apache.tamaya.management;


import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;
import org.apache.tamaya.functions.ConfigurationFunctions;

import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Default implementation of the {@link ManagedConfigMBean} interface. Each bean binds to the
 * current Configuration instance on creation.
 */
public class ManagedConfig implements ManagedConfigMBean {

    /**
     * The logger used.
     */
    private final static Logger LOG = Logger.getLogger(ManagedConfig.class.getName());

    /**
     * Classloader that was active when this instance was created.
     */
    private ClassLoader classLoader;

    /**
     * Constructor, which binds this instance to the current TCCL. In the rare cases where
     * the TCCL is null, this class's classloader is used.
     */
    public ManagedConfig() {
        this.classLoader = Thread.currentThread().getContextClassLoader();
        if (this.classLoader == null) {
            this.classLoader = ManagedConfigMBean.class.getClassLoader();
        }
    }

    @Override
    public String getJsonConfigurationInfo() {
        return getConfigurationInternal().query(ConfigurationFunctions.jsonInfo());
    }

    @Override
    public String getXmlConfigurationInfo() {
        return getConfigurationInternal().query(ConfigurationFunctions.xmlInfo());
    }

    @Override
    public Map<String, String> getConfiguration() {
        return getConfigurationInternal().getProperties();
    }

    @Override
    public Map<String, String> getSection(String area, boolean recursive) {
        return getConfigurationInternal().with(ConfigurationFunctions.section(area, recursive)).getProperties();
    }

    @Override
    public Set<String> getSections() {
        return getConfigurationInternal().query(ConfigurationFunctions.sections());
    }

    @Override
    public Set<String> getTransitiveSections() {
        return getConfigurationInternal().query(ConfigurationFunctions.transitiveSections());
    }

    @Override
    public boolean isAreaExisting(String area) {
        return !getConfigurationInternal().with(
                ConfigurationFunctions.section(area)).getProperties().isEmpty();
    }

    /**
     * Evaluate the current configuration. By default this class is temporarely setting the
     * TCCL to the instance active on bean creation and then calls {@link ConfigurationProvider#getConfiguration()}.
     *
     * @return the configuration instance to be used.
     */
    protected Configuration getConfigurationInternal() {
        ClassLoader currentCL = Thread.currentThread().getContextClassLoader();
        try{
            Thread.currentThread().setContextClassLoader(this.classLoader);
            return ConfigurationProvider.getConfiguration();
        }
        finally{
            Thread.currentThread().setContextClassLoader(currentCL);
        }
    }

}

