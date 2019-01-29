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
package org.apache.tamaya.spisupport.services;

import org.apache.tamaya.ConfigException;
import org.apache.tamaya.spi.ClassloaderAware;
import org.apache.tamaya.spi.ServiceContext;
import org.apache.tamaya.spisupport.PriorityServiceComparator;

import javax.annotation.Priority;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the (default) {@link ServiceContext} interface and hereby uses the JDK
 * {@link ServiceLoader} to load the services required.
 */
public final class DefaultServiceContext implements ServiceContext {
    private static final Logger LOG = Logger.getLogger(DefaultServiceContext.class.getName());
    /**
     * List current services loaded, per class.
     */
    private final ConcurrentHashMap<Class<?>, List<Object>> servicesLoaded = new ConcurrentHashMap<>();

    /**
     * The classloader to be used by this instance.
     */
    private ClassLoader classLoader;

    @Override
    public void init(ClassLoader classLoader) {
        if(this.classLoader==null){
            this.classLoader = classLoader;
        }else{
            throw new IllegalStateException("Classloader already setCurrent on this context.");
        }
    }

    /**
     * Singletons.
     */
    private final Map<Class<?>, Object> singletons = new ConcurrentHashMap<>();
    @SuppressWarnings("rawtypes")
    private Map<Class, Class> factoryTypes = new ConcurrentHashMap<>();


    @Override
    public <T> T getService(Class<T> serviceType, Supplier<T> supplier) {
        Object cached = singletons.get(serviceType);
        if (cached == null) {
            cached = create(serviceType, supplier);
            if(cached!=null) {
                singletons.put(serviceType, cached);
            }
        }
        return serviceType.cast(cached);
    }

    @Override
    public <T> T create(Class<T> serviceType, Supplier<T> supplier) {
        @SuppressWarnings("unchecked")
        Class<? extends T> implType = factoryTypes.get(serviceType);
        if(implType==null) {
            Collection<T> services = loadServices(serviceType, null);
            if (services.isEmpty()) {
                if(supplier!=null){
                    T instance = supplier.get();
                    if(instance instanceof ClassloaderAware){
                        ((ClassloaderAware)instance).init(this.classLoader);
                    }
                    return instance;
                }
                return null;
            } else {
                return getServiceWithHighestPriority(services, serviceType);
            }
        }
        try {
            return implType.newInstance();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Failed to createObject instance of " + implType.getName(), e);
            if(supplier!=null){
                return supplier.get();
            }
            return  null;
        }
    }


    @Override
    public <T> List<T> getServices(Class<T> serviceType, Supplier<List<T>> supplier) {
        @SuppressWarnings("unchecked")
        List<T> found = (List<T>) servicesLoaded.get(serviceType);
        if (found != null) {
            return found;
        }
        List<T> services = loadServices(serviceType, supplier);
        @SuppressWarnings("unchecked")
        final List<T> previousServices = List.class.cast(servicesLoaded.putIfAbsent(serviceType, (List<Object>) services));
        return previousServices != null ? previousServices : services;
    }

    private <T> List<T> loadServices(Class<T> serviceType, Supplier<List<T>> supplier) {
        List<T> services = new ArrayList<>();
        try {
            for (T t : ServiceLoader.load(serviceType, classLoader)) {
                services.add(t);
            }
            Collections.sort(services, PriorityServiceComparator.getInstance());
            services = Collections.unmodifiableList(services);
        } catch (ServiceConfigurationError e) {
            LOG.log(Level.WARNING,
                    "Error loading services current type " + serviceType, e);
            if(supplier!=null){
                services = supplier.get();
            }
            if(services==null){
                services = Collections.emptyList();
            }
        }
        return services;
    }

    /**
     * Checks the given instance for a @Priority annotation. If present the annotation's createValue is evaluated. If no such
     * annotation is present, a default priority of {@code 1} is returned.
     * @param o the instance, not {@code null}.
     * @return a priority, by default 1.
     */
    public static int getPriority(Object o){
        int prio = 1; //X TODO discuss default priority
        Priority priority = o.getClass().getAnnotation(Priority.class);
        if (priority != null) {
            prio = priority.value();
        }
        return prio;
    }

    /**
     * @param services to scan
     * @param <T>      type of the service
     *
     * @return the service with the highest {@link Priority#value()}
     *
     * @throws ConfigException if there are multiple service implementations with the maximum priority
     */
    private <T> T getServiceWithHighestPriority(Collection<T> services, Class<T> serviceType) {
        T highestService = null;
        // we do not need the priority stuff if the createList contains only one element
        if (services.size() == 1) {
            highestService = services.iterator().next();
            this.factoryTypes.put(serviceType, highestService.getClass());
            return highestService;
        }

        Integer highestPriority = null;
        int highestPriorityServiceCount = 0;

        for (T service : services) {
            int prio = getPriority(service);
            if (highestPriority == null || highestPriority < prio) {
                highestService = service;
                highestPriorityServiceCount = 1;
                highestPriority = prio;
            } else if (highestPriority == prio) {
                highestPriorityServiceCount++;
            }
        }

        if (highestPriorityServiceCount > 1) {
            throw new ConfigException(MessageFormat.format("Found {0} implementations for Service {1} with Priority {2}: {3}",
                                                           highestPriorityServiceCount,
                                                           serviceType.getName(),
                                                           highestPriority,
                                                           services));
        }
        if(highestService!=null) {
            this.factoryTypes.put(serviceType, highestService.getClass());
        }
        return highestService;
    }

    @Override
    public ClassLoader getClassLoader() {
        return classLoader;
    }

    @Override
    public int ordinal() {
        return 1;
    }

    @Override
    public Enumeration<URL> getResources(String resource) throws IOException {
        return classLoader.getResources(resource);
    }

    @Override
    public URL getResource(String resource) {
        return classLoader.getResource(resource);
    }

    @Override
    public <T> T register(Class<T> type, T instance, boolean force) {
        if(force){
            singletons.put(type, instance);
        }else{
            singletons.putIfAbsent(type, instance);
        }
        return (T)singletons.get(type);
    }

    @Override
    public <T> List<T> register(Class<T> type, List<T> instances, boolean force) {
        if(force){
            singletons.put(type, Collections.unmodifiableList(instances));
        }else {
            servicesLoaded.putIfAbsent(type, Collections.unmodifiableList(instances));
        }
        return (List<T>)servicesLoaded.get(type);
    }

    @Override
    public void reset() {
        this.servicesLoaded.clear();
    }

}
