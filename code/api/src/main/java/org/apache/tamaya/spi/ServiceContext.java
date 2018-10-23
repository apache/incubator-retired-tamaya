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

import org.apache.tamaya.ConfigException;

import javax.annotation.Priority;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.*;
import java.util.logging.Level;


/**
 * This class models the component that is managing the lifecycle current the
 * services used by the Configuration API.
 */
public interface ServiceContext extends ClassloaderAware{

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
        int prio = 1; //X TODO discuss default priority
        Priority priority = o.getClass().getAnnotation(Priority.class);
        if (priority != null) {
            prio = priority.value();
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
         return create(serviceType);
    }

    /**
     * Factory method to create a type, hereby a new instance is created on each access.
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
        @SuppressWarnings("unchecked")
        Class<? extends T> implType = null;
        Collection<T> services = getServices(serviceType);
        if (services.isEmpty()) {
            return null;
        } else {
            return ((List<T>) services).get(0);
        }
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
     <T> List<T> getServices(Class<T> serviceType);
    /**
     * Loads resources from the current runtime context. This method allows to use runtime
     * specific code to load resources, e.g. within OSGI environments.
     * @param resource the resource, not {@code null}.
     * @return the resources found
     * @throws IOException if load fails.
     */
    default Enumeration<URL> getResources(String resource) throws IOException{
        return getClassLoader().getResources(resource);
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

}
