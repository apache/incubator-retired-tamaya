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
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the (default) {@link org.apache.tamaya.spi.ServiceContext} interface and hereby uses the JDK
 * {@link java.util.ServiceLoader} to load the services required.
 */
public final class TestServiceContext implements ServiceContext {

    private ClassLoader classLoader;

    /** List current services loaded, per class. */
    private final ConcurrentHashMap<Class<?>, List<Object>> servicesLoaded = new ConcurrentHashMap<>();

    private final Map<Class<?>, Object> singletons = new ConcurrentHashMap<>();

    @Override
    public ClassLoader getClassLoader() {
        return classLoader;
    }

    @Override
    public void init(ClassLoader classLoader) {
        if(this.classLoader==null){
            this.classLoader = classLoader;
        }else{
            throw new IllegalStateException("Classloader already setCurrent on this context.");
        }
    }

    @Override
    public int ordinal() {
        return 5;
    }

    @Override
    public <T> T getService(Class<T> serviceType, Supplier<T> supplier) {
        T cached = serviceType.cast(singletons.get(serviceType));
        if(cached==null) {
            cached = create(serviceType, supplier);
            singletons.put((Class)serviceType, cached);
        }
        if (cached == Object.class) {
            cached = null;
        }
        return cached;
    }

    @Override
    public <T> T create(Class<T> serviceType, Supplier<T> supplier) {
        try {
            for (T t : ServiceLoader.load(serviceType)) {
                return t;
            }
        }catch(Exception e){
            if(supplier!=null) {
                T instance = supplier.get();
                if(instance instanceof ClassloaderAware){
                    ((ClassloaderAware)instance).init(this.classLoader);
                }
                return instance;
            }
            throw new IllegalArgumentException("Failed to instantiate instance:");
        }
        return null;
    }

    @Override
    public <T> List<T> getServices(Class<T> serviceType, Supplier<List<T>> supplier) {
        List<T> services;
        try {
            services = new ArrayList<>();
            for (T t : ServiceLoader.load(serviceType)) {
                services.add(t);
            }
            services = Collections.unmodifiableList(services);
        } catch (Exception e) {
            if(supplier==null){
                Logger.getLogger(TestServiceContext.class.getName()).log(Level.WARNING,
                        "Error loading services current type " + serviceType, e);
                return Collections.emptyList();
            }
            services = Collections.unmodifiableList(supplier.get());
        }
        @SuppressWarnings("unchecked")
        final List<T> previousServices = List.class.cast(servicesLoaded.putIfAbsent(serviceType, (List<Object>)services));
        return previousServices != null ? previousServices : services;
    }

    @Override
    public Collection<URL> getResources(String resource) {
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

    @Override
    public URL getResource(String resource) {
        return classLoader.getResource(resource);
    }

    @Override
    public <T> T register(Class<T> type, T instance, boolean force) {
        if(force){
            servicesLoaded.put(type, Collections.singletonList(instance));
        }else{
            servicesLoaded.putIfAbsent(type, Collections.singletonList(instance));
        }
        return (T)servicesLoaded.get(type).get(0);
    }

    @Override
    public <T> List<T> register(Class<T> type, List<T> instances, boolean force) {
        if(force){
            servicesLoaded.put(type, Collections.singletonList(instances));
        }else {
            servicesLoaded.putIfAbsent(type, Collections.singletonList(instances));
        }
        return (List<T>)servicesLoaded.get(type);
    }

    @Override
    public void reset() {

    }

}
