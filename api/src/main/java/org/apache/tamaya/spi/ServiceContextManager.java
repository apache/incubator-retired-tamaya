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
package org.apache.tamaya.spi;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This singleton provides access to the services available in the current {@link ServiceContext}. The
 * behaviour can be adapted, by calling {@link ServiceContextManager#set(ServiceContext)} before accessing any
 * services.
 */
final class ServiceContextManager {
    /**
     * The ServiceProvider used.
     */
    private static volatile ServiceContext serviceContextProviderDelegate;
    /**
     * The shared lock instance user.
     */
    private static final Object LOCK = new Object();

    /**
     * Private singletons constructor.
     */
    private ServiceContextManager() {
    }

    /**
     * Load the {@link ServiceContext} to be used.
     *
     * @return {@link ServiceContext} to be used for loading the services.
     */
    private static ServiceContext loadDefaultServiceProvider() {
        try {
            for (ServiceContext sp : ServiceLoader.load(ServiceContext.class)) {
                return sp;
            }
        } catch (Exception e) {
            Logger.getLogger(ServiceContextManager.class.getName()).log(Level.INFO, "Using default ServiceProvider.");
        }
        return new DefaultServiceContextProvider();
    }

    /**
     * Replace the current {@link ServiceContext} in use.
     *
     * @param serviceContextProvider the new {@link ServiceContext}, not null.
     */
    public static ServiceContext set(ServiceContext serviceContextProvider) {
        ServiceContext currentContext = ServiceContextManager.serviceContextProviderDelegate;
        Objects.requireNonNull(serviceContextProvider);
        synchronized (LOCK) {
            if (ServiceContextManager.serviceContextProviderDelegate == null) {
                ServiceContextManager.serviceContextProviderDelegate = serviceContextProvider;
                Logger.getLogger(ServiceContextManager.class.getName())
                        .log(Level.INFO, "Using ServiceProvider: " + serviceContextProvider.getClass().getName());
            } else {
                Logger.getLogger(ServiceContextManager.class.getName())
                        .log(Level.WARNING, "Replacing ServiceProvider " + ServiceContextManager.serviceContextProviderDelegate.getClass().getName() + " with: " + serviceContextProvider.getClass().getName());
                ServiceContextManager.serviceContextProviderDelegate = serviceContextProvider;
            }
        }
        return currentContext;
    }

    /**
     * Ge {@link ServiceContext}. If necessary the {@link ServiceContext} will be laziliy loaded.
     *
     * @return the {@link ServiceContext} used.
     */
    public static ServiceContext getServiceContext() {
        if (serviceContextProviderDelegate == null) {
            synchronized (LOCK) {
                if (serviceContextProviderDelegate == null) {
                    serviceContextProviderDelegate = loadDefaultServiceProvider();
                }
            }
        }
        return serviceContextProviderDelegate;
    }

}