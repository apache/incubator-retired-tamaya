/*
 * Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.apache.tamaya.integration.cdi;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;

/**
 * Tamaya main integration with CDI, storing the BeanManager reference for implementation, where no
 * JNDI is available or {@code java:comp/env/BeanManager} is not set correctly.
 */
public class TamayaCDIIntegration implements Extension {
    /** The BeanManager references stored. */
    private static BeanManager beanManager;

    /**
     * Initializes the current BeanManager with the instance passed.
     * @param validation the event
     * @param beanManager the BeanManager instance
     */
    @SuppressWarnings("all")
    public void initBeanManager(@Observes AfterDeploymentValidation validation, BeanManager beanManager){
        TamayaCDIIntegration.beanManager = beanManager;
    }

    /**
     * Get the current {@link BeanManager} instance.
     * @return the currently used bean manager.
     */
    public static BeanManager getBeanManager(){
        return beanManager;
    }

}
