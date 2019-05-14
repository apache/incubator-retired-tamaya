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

import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
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
    private static final Map<ClassLoader, ServiceContext> SERVICE_CONTEXTS = new ConcurrentHashMap<>();

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
    private static ServiceContext loadDefaultServiceProvider(ClassLoader classLoader) {
        ServiceContext highestServiceContext = null;
        try {
            int highestOrdinal = 0;
            for (ServiceContext serviceContext : ServiceLoader.load(ServiceContext.class, classLoader)) {
                if (highestServiceContext == null
                        || serviceContext.ordinal() > highestOrdinal) {
                    highestServiceContext = serviceContext;
                    highestOrdinal = serviceContext.ordinal();
                }
            }
        } catch (Exception e) {
            throw new ConfigException("ServiceContext not loadable", e);
        }
        if (highestServiceContext==null){
            throw new ConfigException("No ServiceContext found");
        }
        highestServiceContext.init(classLoader);
        LOG.info("Using Service Context of type: " + highestServiceContext.getClass().getName());
        return highestServiceContext;
    }

    /**
     * Replace the current {@link ServiceContext} in use.
     *
     * @param serviceContext the new {@link ServiceContext}, not {@code null}.
     * @return the currently used context after setting it.
     */
    public static ServiceContext set(ServiceContext serviceContext) {
        Objects.requireNonNull(serviceContext);
        return setWithClassLoader(serviceContext, serviceContext.getClassLoader());
    }
    
    /**
     * Replace the current {@link ServiceContext} for the ServiceContextManager's classloader in use.
     *
     * @param serviceContext the new {@link ServiceContext}, not {@code null}.
     * @return the currently used context after setting it.
     */
    public static ServiceContext setToStaticClassLoader(ServiceContext serviceContext) {
        Objects.requireNonNull(serviceContext);
        return setWithClassLoader(serviceContext, ServiceContextManager.class.getClassLoader());
    }
    
    private static ServiceContext setWithClassLoader(ServiceContext serviceContext, ClassLoader cl) {
        Objects.requireNonNull(serviceContext);
        Objects.requireNonNull(cl);

        ServiceContext previousContext;
        synchronized (ServiceContextManager.class) {
            previousContext = ServiceContextManager.SERVICE_CONTEXTS
                    .put(cl, serviceContext);
        }
        if(previousContext!=null) {
            LOG.log(Level.WARNING, "Replaced ServiceProvider " +
                    previousContext.getClass().getName() +
                    " with: " + serviceContext.getClass().getName() + " for classloader: " +
                    serviceContext.getClassLoader());
        }else{
            LOG.log(Level.INFO, "Using ServiceProvider: " + serviceContext.getClass().getName() +
                    " for classloader: " +  serviceContext.getClassLoader());
        }
        return serviceContext;
    }
    
    /**
     * Ge {@link ServiceContext}. If necessary the {@link ServiceContext} will be laziliy loaded.
     *
     * @param classLoader the classloader to be used, not null.
     * @return the {@link ServiceContext} used.
     */
    public static ServiceContext getServiceContext(ClassLoader classLoader) {
        Objects.requireNonNull(classLoader, "Classloader required.");
        return SERVICE_CONTEXTS.computeIfAbsent(classLoader, ServiceContextManager::loadDefaultServiceProvider);
    }

    /**
     * Ge {@link ServiceContext}. If necessary the {@link ServiceContext} will be laziliy loaded.
     *
     * @return the {@link ServiceContext} used.
     */
    public static ServiceContext getServiceContext() {
        return getServiceContext(getDefaultClassLoader());
    }

    /**
     * Evaluate the default classloader: This return the current thread context classloader, or this
     * class's classloader as fallback.
     * @return the classloder, not null.
     */
    public static ClassLoader getDefaultClassLoader() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if(cl==null) {
            cl = ServiceContextManager.class.getClassLoader();
        }
        return cl;
    }
}
