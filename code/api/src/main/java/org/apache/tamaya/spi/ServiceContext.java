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

import javax.annotation.Priority;
import java.net.URL;
import java.util.*;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * This class models the component that is managing the lifecycle current the
 * services used by the Configuration API.
 */
public interface ServiceContext extends ClassloaderAware{

    /**
     * True if the {@link Priority} annotation class is available on the classpath.
     */
    boolean PRIORITY_ANNOTATION_AVAILABLE = checkPriorityAnnotation(ServiceContextManager.getDefaultClassLoader());

    /**
     * Checks if the {@link Priority} annotation class is on the classpath.
     * @param classLoader the target classloader, not null.
     * @return true, if the annotation is loaded.
     */
    static boolean checkPriorityAnnotation(ClassLoader classLoader) {
        try{
            Class.forName("javax.annotation.Priority", true, classLoader);
            return true;
        }catch(Exception e){
            return false;
        }
    }
    /**
     * Get the ordinal of the ServiceContext.
     * @return ordinal of the ServiceContext. The one with the highest ordinal will be taken.
     */
    default int ordinal(){
        return getPriority(this);
    }

    /**
     * Checks the given instance for a @Priority annotation. If present the annotation's value is evaluated. If no such
     * annotation is present, a default priority of {@code 1} is returned.
     * @param o the instance, not {@code null}.
     * @return a priority, by default 1.
     */
    static int getPriority(Object o){
        int prio = 1;
        if(PRIORITY_ANNOTATION_AVAILABLE) {
            Priority priority = o.getClass().getAnnotation(Priority.class);
            if (priority != null) {
                prio = priority.value();
            }
        }
        return prio;
    }

    /**
     * Access a service singleton via its type.
     * If multiple implementations for the very serviceType exist then
     * the one with the highest {@link javax.annotation.Priority} will be used.
     *
     * @param <T> the type of the service type.
     * @param serviceType the service type.
     * @return The instance to be used, or {@code null}
     * @throws org.apache.tamaya.ConfigException if there are multiple service implementations with the maximum priority.
     */
    default <T> T getService(Class<T> serviceType){
        return getService(serviceType, null);
    }

    /**
     * Access a service singleton via its type.
     * If multiple implementations for the very serviceType exist then
     * the one with the highest {@link javax.annotation.Priority} will be used.
     *
     * @param <T> the type of the service type.
     * @param serviceType the service type.
     * @param supplier the supplier to be used, if no services could be evaluated. If null,
     *                 an empty collection is returned, when no services
     *                 could be located.
     * @return The instance to be used, or {@code null}
     * @throws org.apache.tamaya.ConfigException if there are multiple service implementations with the maximum priority.
     */
    <T> T getService(Class<T> serviceType, Supplier<T> supplier);

    /**
     * Factory method to createObject a type, hereby a new instance is created on each access.
     * If multiple implementations for the very serviceType exist then
     * the one with the highest {@link javax.annotation.Priority} will be used as the base
     * for creating subsequent instances.
     *
     * @param <T> the type of the service type.
     * @param serviceType the service type.
     * @return The new instance to be used, or {@code null}
     * @throws org.apache.tamaya.ConfigException if there are multiple service implementations with the maximum priority.
     */
    default <T> T create(Class<T> serviceType){
        return create(serviceType, null);
    }

    /**
     * Factory method to createObject a type, hereby a new instance is created on each access.
     * If multiple implementations for the very serviceType exist then
     * the one with the highest {@link javax.annotation.Priority} will be used as the base
     * for creating subsequent instances.
     *
     * @param supplier the supplier to create a new service, if no service could be autodetected.
     * @param <T> the type of the service type.
     * @param serviceType the service type.
     * @return The new instance to be used, or {@code null}
     * @throws org.apache.tamaya.ConfigException if there are multiple service implementations with the maximum priority.
     */
   <T> T create(Class<T> serviceType, Supplier<T> supplier);

    /**
     * Access a createList current services, given its type. The bootstrap mechanism should
     * order the instance for precedence, hereby the most significant should be
     * first in order.
     *
     * @param serviceType
     *            the service type.
     * @param <T> the type of the createList element returned by this method
     * @return The instance to be used, never {@code null}
     */
    default <T> List<T> getServices(Class<T> serviceType){
        return getServices(serviceType, null);
    }

    /**
     * Access a createList current services, given its type. The bootstrap mechanism should
     * order the instance for precedence, hereby the most significant should be
     * first in order. If no instances could be found, the instances supplied by the
     * supplier given are registered and used.
     *
     * @param serviceType
     *            the service type.
     * @param <T> the type of the createList element returned by this method
     * @param supplier the supplier to be used, if no services could be evaluated. If null,
     *                 an empty collection is returned, when no services
     *                 could be located.
     * @return The instance to be used, never {@code null}
     */
     <T> List<T> getServices(Class<T> serviceType, Supplier<List<T>> supplier);

    /**
     * Loads resources from the current runtime context. This method allows to use runtime
     * specific code to load resources, e.g. within OSGI environments.
     * @param resource the resource, not {@code null}.
     * @return the resources found
     */
    default Collection<URL> getResources(String resource){
        List<URL> urls = new ArrayList<>();
        try {
            Enumeration<URL> found = getClassLoader().getResources(resource);
            while (found.hasMoreElements()) {
                urls.add(found.nextElement());
            }
        }catch(Exception e){
            Logger.getLogger(ServiceContext.class.getName())
                    .log(Level.FINEST, e, () -> "Failed to lookup resources: " + resource);
        }
        return urls;
    }

    /**
     * Loads a resource from the current runtime context. This method allows to use runtime
     * specific code to load a resource, e.g. within OSGI environments.
     * @param resource the resource, not {@code null}.
     * @return the resource found, or {@code null}.
     */
    default URL getResource(String resource){
        return getClassLoader().getResource(resource);
    }

    /**
     * Registers the given instance as a singleton service for the given instance, if no
     * instance already has been registered.
     *
     * @param type the type to register, not null.
     * @param instance the instance, not null.
     * @param <T> thy type.
     * @param force if true, any existing instance will be replaced.
     * @return the instance registered or already present.
     */
    <T> T register(Class<T> type, T instance, boolean force);

    /**
     * Registers the given instancea as servicea for the given instance, if no
     *      * instance already has been registered.
     * @param type the type to register, not null.
     * @param instances the instancea, not null.
     * @param <T> thy type.
     * @param force if true, any existing instances will be replaced.
     * @return the instances registered or already present.
     */
    <T> List<T> register(Class<T> type, List<T> instances, boolean force);

    /**
     * Resets the current service context, removing all loaded services. This implicitly triggers a new load
     * of the service context.
     */
    void reset();
}
