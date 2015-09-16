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

import java.util.Objects;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tamaya.ConfigException;


/**
 * This singleton provides access to the services available in the current {@link ServiceContext}. The
 * behaviour can be adapted, by calling {@link ServiceContextManager#set(ServiceContext)} before accessing any
 * services.
 */
public final class ServiceContextManager {

    /** The logger used. */
    private static final Logger LOG = Logger.getLogger(ServiceContextManager.class.getName());

    /**
     * The ServiceProvider used.
     */
    private static volatile ServiceContext serviceContextProviderDelegate;

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
        ServiceContext highestServiceContext = null;
        try {
            int highestOrdinal = 0;
            for (ServiceContext serviceContext : ServiceLoader.load(ServiceContext.class)) {
                if (serviceContext.ordinal() > highestOrdinal) {
                    highestServiceContext = serviceContext;
                }
            }
        } catch (Exception e) {
            throw new ConfigException("ServiceContext not loadable", e);
        }

        if (highestServiceContext==null){
            throw new ConfigException("No ServiceContext found");
        }
        LOG.info("Using Service Context of type: " + highestServiceContext.getClass().getName());
        return highestServiceContext;
    }

    /**
     * Replace the current {@link ServiceContext} in use.
     *
     * @param serviceContextProvider the new {@link ServiceContext}, not null.
     */
    public static ServiceContext set(ServiceContext serviceContextProvider) {
        ServiceContext currentContext = ServiceContextManager.serviceContextProviderDelegate;
        Objects.requireNonNull(serviceContextProvider);

        synchronized (ServiceContextManager.class) {
            if (ServiceContextManager.serviceContextProviderDelegate == null) {
                ServiceContextManager.serviceContextProviderDelegate = serviceContextProvider;
                LOG.log(Level.INFO, "Using ServiceProvider: " + serviceContextProvider.getClass().getName());
            } else {
                LOG.log(Level.WARNING, "Replacing ServiceProvider " +
                                ServiceContextManager.serviceContextProviderDelegate.getClass().getName() +
                                " with: " + serviceContextProvider.getClass().getName());
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
            synchronized (ServiceContextManager.class) {
                if (serviceContextProviderDelegate == null) {
                    serviceContextProviderDelegate = loadDefaultServiceProvider();
                }
            }
        }
        return serviceContextProviderDelegate;
    }

}