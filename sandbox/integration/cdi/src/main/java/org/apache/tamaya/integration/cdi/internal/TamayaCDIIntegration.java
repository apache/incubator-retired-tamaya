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
package org.apache.tamaya.integration.cdi.internal;


import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;
import org.apache.tamaya.integration.cdi.annot.System;
import org.apache.tamaya.spi.ConfigurationContext;
import org.apache.tamaya.spi.ConfigurationContextBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.New;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;

/**
 * Tamaya main integreation with CDI, especially storing the BeanManager reference for implementation, where no
 * JNDI is available or {@code java:comp/env/BeanManager} is not set correctly.
 */
public class TamayaCDIIntegration implements Extension{
    /** The BeanManager references stored. */
    private static BeanManager beanManager;

    /**
     * Initializes the current BeanMaanager with the instance passed.
     * @param validation the event
     * @param beanManager the BeanManager instance
     */
    public void initBeanManager(@Observes AfterDeploymentValidation validation, BeanManager beanManager){
        TamayaCDIIntegration.beanManager = beanManager;
    }

    /**
     * Get the current {@link  BeanManager} instance.
     * @return
     */
    public static BeanManager getBeanManager(){
        return beanManager;
    }

    @Produces
    @org.apache.tamaya.integration.cdi.annot.System
    public Configuration getSystemConfiguration(){
        return ConfigurationProvider.getConfiguration();
    }

    @Produces @ApplicationScoped
    public Configuration getConfiguration(ConfigurationContext context){
        if(ConfigurationProvider.class.getClassLoader() == Thread.currentThread().getContextClassLoader()){
            return getSystemConfiguration();
        }
        return new DefaultConfiguration(context);
    }

    @Produces @System
    public ConfigurationContext getSystemConfigurationContext(){
        return ConfigurationProvider.getConfigurationContext();
    }

    @Produces @ApplicationScoped
    public ConfigurationContext getConfigurationContext(){
        if(ConfigurationProvider.class.getClassLoader() == Thread.currentThread().getContextClassLoader()){
            return getSystemConfigurationContext();
        }
        return new DefaultConfigurationContext();
    }

    @Produces @System @New
    public ConfigurationContextBuilder getSystemConfigurationContextBuilder(){
        return ConfigurationProvider.getConfigurationContextBuilder();
    }

    @Produces @New
    public ConfigurationContextBuilder getConfigurationContextBuilder(){
        if(ConfigurationProvider.class.getClassLoader() == Thread.currentThread().getContextClassLoader()){
            return getSystemConfigurationContextBuilder();
        }
        return new DefaultConfigurationContextBuilder();
    }

}
