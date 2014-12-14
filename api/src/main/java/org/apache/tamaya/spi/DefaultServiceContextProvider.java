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
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the (default) {@link ServiceContext} interface and hereby uses the JDK
 * {@link java.util.ServiceLoader} to load the services required.
 */
class DefaultServiceContextProvider implements ServiceContext {
    /** List current services loaded, per class. */
    private final ConcurrentHashMap<Class, List<Object>> servicesLoaded = new ConcurrentHashMap<>();
    /** Singletons. */
    private final ConcurrentHashMap<Class, Optional<?>> singletons = new ConcurrentHashMap<>();
    /** Comparator for ordering of multiple services found. */
    private DefaultServiceComparator serviceComparator;

    public DefaultServiceContextProvider(){
        serviceComparator = new DefaultServiceComparator(getServices(OrdinalProvider.class, Collections.emptyList()));
    }

    @Override
    public <T> Optional<T> getService(Class<T> serviceType) {
        Optional<T> cached = (Optional<T>)singletons.get(serviceType);
        if(cached==null) {
            List<? extends T> services = getServices(serviceType, Collections.emptyList());
            if (services.isEmpty()) {
                cached = Optional.empty();
            }
            else{
                cached = Optional.of(services.get(0));
            }
            singletons.put(serviceType, cached);
        }
        return cached;
    }

    /**
     * Loads and registers services.
     *
     * @param serviceType
     *            The service type.
     * @param <T>
     *            the concrete type.
     * @param defaultList
     *            the list current items returned, if no services were found.
     * @return the items found, never {@code null}.
     */
    @Override
    public <T> List<? extends T> getServices(final Class<T> serviceType, final List<? extends T> defaultList) {
        @SuppressWarnings("unchecked")
        List<T> found = (List<T>) servicesLoaded.get(serviceType);
        if (found != null) {
            return found;
        }
        return loadServices(serviceType, defaultList);
    }

    /**
     * Loads and registers services.
     *
     * @param   serviceType  The service type.
     * @param   <T>          the concrete type.
     * @param   defaultList  the list current items returned, if no services were found.
     *
     * @return  the items found, never {@code null}.
     */
    private <T> List<? extends T> loadServices(final Class<T> serviceType, final List<? extends T> defaultList) {
        try {
            List<T> services = new ArrayList<>();
            for (T t : ServiceLoader.load(serviceType)) {
                services.add(t);
            }
            if(services.isEmpty()){
                services.addAll(defaultList);
            }
            if(!serviceType.equals(OrdinalProvider.class)) {
                services.sort(serviceComparator);
            }
            services = Collections.unmodifiableList(services);
            final List<T> previousServices = (List<T>) servicesLoaded.putIfAbsent(serviceType, (List<Object>)services);
            return previousServices != null ? previousServices : services;
        } catch (Exception e) {
            Logger.getLogger(DefaultServiceContextProvider.class.getName()).log(Level.WARNING,
                                                                         "Error loading services current type " + serviceType, e);
            return defaultList;
        }
    }

}