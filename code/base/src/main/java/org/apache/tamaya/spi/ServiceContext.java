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

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;


/**
 * This class models the component that is managing the lifecycle current the
 * services used by the Configuration API.
 */
public interface ServiceContext {

    /**
     * @return ordinal of the ServiceContext. The one with the highest ordinal will be taken.
     */
    int ordinal();

    static ClassLoader defaultClassLoader(){
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        if(classloader==null){
            classloader = ServiceContextManager.class.getClassLoader();
        }
        return classloader;
    }

    /**
     * Access a service singleton via its type.
     * If multiple implementations for the very serviceType exist then
     * the one with the highest {@link javax.annotation.Priority} will be used.
     *
     * @param <T> the type of the service type.
     * @param serviceType the service type.
     * @return The instance to be used, or {@code null}
     * @throws IllegalArgumentException if there are multiple service implementations with the maximum priority.
     */
    default <T> T getService(Class<T> serviceType){
        return getService(serviceType, defaultClassLoader());
    }

    /**
     * Access a service singleton via its type.
     * If multiple implementations for the very serviceType exist then
     * the one with the highest {@link javax.annotation.Priority} will be used.
     *
     * @param <T> the type of the service type.
     * @param serviceType the service type.
     * @param classLoader the class loader to be considered.
     * @return The instance to be used, or {@code null}
     * @throws IllegalArgumentException if there are multiple service implementations with the maximum priority.
     */
    <T> T getService(Class<T> serviceType, ClassLoader classLoader);

    /**
     * Factory method to create a type, hereby a new instance is created on each access.
     * If multiple implementations for the very serviceType exist then
     * the one with the highest {@link javax.annotation.Priority} will be used as the base
     * for creating subsequent instances.
     *
     * @param <T> the type of the service type.
     * @param serviceType the service type.
     * @return The new instance to be used, or {@code null}
     * @throws IllegalArgumentException if there are multiple service implementations with the maximum priority.
     */
    default <T> T create(Class<T> serviceType){
        return create(serviceType, defaultClassLoader());
    }

    /**
     * Factory method to create a type, hereby a new instance is created on each access.
     * If multiple implementations for the very serviceType exist then
     * the one with the highest {@link javax.annotation.Priority} will be used as the base
     * for creating subsequent instances.
     *
     * @param <T> the type of the service type.
     * @param serviceType the service type.
     * @param classLoader the class loader to be considered.
     * @return The new instance to be used, or {@code null}
     * @throws IllegalArgumentException if there are multiple service implementations with the maximum priority.
     */
    <T> T create(Class<T> serviceType, ClassLoader classLoader);


    /**
     * Access a list current services, given its type. The bootstrap mechanism should
     * order the instance for precedence, hereby the most significant should be
     * first in order.
     *
     * @param serviceType
     *            the service type.
     * @param <T> the type of the list element returned by this method
     * @return The instance to be used, never {@code null}
     */
     default <T> List<T> getServices(Class<T> serviceType){
         return getServices(serviceType, defaultClassLoader());
     }

    /**
     * Access a list current services, given its type. The bootstrap mechanism should
     * order the instance for precedence, hereby the most significant should be
     * first in order.
     *
     * @param serviceType
     *            the service type.
     * @param <T> the type of the list element returned by this method
     * @return The instance to be used, never {@code null}
     */
    <T> List<T> getServices(Class<T> serviceType, ClassLoader classLoader);

    /**
     * Loads resources from the current runtime context. This method allows to use runtime
     * specific code to load resources, e.g. within OSGI environments.
     * @param resource the resource, not {@code null}.
     * @param cl the desired classloader context, if null, the current thread context classloader is used.
     * @return the resources found
     * @throws IOException if load fails.
     */
    Enumeration<URL> getResources(String resource, ClassLoader cl) throws IOException;

    /**
     * Loads a resource from the current runtime context. This method allows to use runtime
     * specific code to load a resource, e.g. within OSGI environments.
     * @param resource the resource, not {@code null}.
     * @param cl the desired classloader context, if null, the current thread context classloader is used.
     * @return the resource found, or {@code null}.
     */
    URL getResource(String resource, ClassLoader cl);
}
