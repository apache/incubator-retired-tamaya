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

import org.apache.tamaya.base.ServiceContext;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the (default) {@link ServiceContext} interface and hereby uses the JDK
 * {@link java.util.ServiceLoader} to load the services required.
 */
public final class TestServiceContext implements ServiceContext {
    /** List current services loaded, per class. */
    private final ConcurrentHashMap<Class<?>, List<Object>> servicesLoaded = new ConcurrentHashMap<>();

    private final Map<Class<?>, Object> singletons = new ConcurrentHashMap<>();

    @Override
    public int ordinal() {
        return 1;
    }

    @SuppressWarnings("rawtypes")
	@Override
    public <T> T getService(Class<T> serviceType) {
        T cached = serviceType.cast(singletons.get(serviceType));
        if(cached==null) {
            cached = create(serviceType);
            singletons.put((Class)serviceType, cached);
        }
        if (cached == Object.class) {
            cached = null;
        }
        return cached;
    }

    @Override
    public <T> T getService(Class<T> serviceType, ClassLoader classLoader) {
        return getService(serviceType, ServiceContext.defaultClassLoader());
    }

    @SuppressWarnings("unchecked")
	@Override
    public <T> T create(Class<T> serviceType) {
        Collection<T> services = getServices(serviceType);
        if (services.isEmpty()) {
            return (T) Object.class; // as marker for 'nothing here'
        }
        else{
            return services.iterator().next();
        }
    }

    @Override
    public <T> T create(Class<T> serviceType, ClassLoader classLoader) {
        return create(serviceType, ServiceContext.defaultClassLoader());
    }

    /**
     * Loads and registers services.
     *
     * @param   <T>          the concrete type.
     *
     * @param   serviceType  The service type.
     * @return  the items found, never {@code null}.
     */
    @Override
    public <T> List<T> getServices(Class<T> serviceType) {
        try {
            List<T> services = new ArrayList<>();
            for (T t : ServiceLoader.load(serviceType)) {
                services.add(t);
            }
            services = Collections.unmodifiableList(services);
            @SuppressWarnings("unchecked")
			final List<T> previousServices = List.class.cast(servicesLoaded.putIfAbsent(serviceType, (List<Object>)services));
            return previousServices != null ? previousServices : services;
        } catch (Exception e) {
            Logger.getLogger(TestServiceContext.class.getName()).log(Level.WARNING,
                                      "Error loading services current type " + serviceType, e);
            return Collections.emptyList();
        }
    }

    @Override
    public <T> List<T> getServices(Class<T> serviceType, ClassLoader classLoader) {
        return getServices(serviceType, ServiceContext.defaultClassLoader());
    }

    @Override
    public Enumeration<URL> getResources(String resource, ClassLoader cl) throws IOException {
        return cl.getResources(resource);
    }

    @Override
    public URL getResource(String resource, ClassLoader cl) {
        return cl.getResource(resource);
    }

}
