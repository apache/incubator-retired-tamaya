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
package org.apache.tamaya.clsupport.internal;

import org.apache.tamaya.ConfigException;
import org.apache.tamaya.spi.ServiceContext;

import javax.annotation.Priority;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Anatole on 06.09.2015.
 */
@Priority(250)
public class CLAwareServiceContext extends AbstractClassloaderAwareItemLoader<ServiceContainer>
        implements ServiceContext{

    public static final String PREFIX = "META-INF/services/";

    /**
     * Singletons as valid for this classloader. Other classloaders or parent contexts may have alternate
     * singleton instances being active.
     */
    private final Map<Class<?>, Object> singletons = new ConcurrentHashMap<>();

    public CLAwareServiceContext(){
        super();
    }

    public CLAwareServiceContext(String resourceLocation) {
        super();
    }

    public CLAwareServiceContext(String resourceLocation, ClassLoader classLoader) {
        super(classLoader);
    }


    @Override
    protected ServiceContainer createItem(ClassLoader classLoader) {
        return new ServiceContainer(classLoader);
    }

    @Override
    protected void updateItem(ServiceContainer currentContainer, ClassLoader classLoader) {

    }

    @Override
    public int ordinal() {
        return 100;
    }

    @Override
    public <T> T getService(Class<T> serviceType) {
        Object cached = singletons.get(serviceType);
        if (cached == null) {
            Collection<? extends T> services = getServices(serviceType);
            if (services.isEmpty()) {
                cached = null;
            } else {
                cached = getServiceWithHighestPriority(services, serviceType);
            }
            if(cached!=null) {
                singletons.put(serviceType, cached);
            }
        }
        return serviceType.cast(cached);
    }

    public <T> T getService(Class<T> serviceType, ClassLoader classLoader) {
        ClassLoader cl = classLoader;
        List<T> services = new ArrayList<>();
        while(cl!=null) {
            ServiceContainer container = getItemNoParent(classLoader, true);
            services.addAll(container.getServices(serviceType));
        }
        return select(services, serviceType);
    }

    private <T> T select(Collection<T> services, Class<T> serviceType) {
        return getServiceWithHighestPriority(services, serviceType);
    }

    @Override
    public <T> Collection<T> getServices(Class<T> serviceType) {
        return getServices(serviceType, AbstractClassloaderAwareItemLoader.getDefaultClassLoader());
    }

    public <T> Collection<T> getServices(Class<T> serviceType, ClassLoader classLoader) {
        List<T> services = new ArrayList<>();
        ClassLoader cl = classLoader;
        List<ServiceContainer> containers = new ArrayList<>();
        while(cl!=null) {
            ServiceContainer container = getItemNoParent(cl, true);
            containers.add(container);
            cl = cl.getParent();
        }
        List<ServiceContainer> prevContainers = new ArrayList<>();
        Collections.reverse(containers);
        for(ServiceContainer container: containers) {
            if (!container.isTypeLoaded(serviceType)) {
                container.loadServices(serviceType, prevContainers);
                prevContainers.add(container);
                services.addAll(container.getServices(serviceType));
            }
        }
        // TODO Sort services
        // TODO Get first
        return services;
    }

    /**
     * @param services to scan
     * @param <T>      type of the service
     *
     * @return the service with the highest {@link javax.annotation.Priority#value()}
     *
     * @throws ConfigException if there are multiple service implementations with the maximum priority
     */
    private <T> T getServiceWithHighestPriority(Collection<? extends T> services, Class<T> serviceType) {

        // we do not need the priority stuff if the list contains only one element
        if (services.size() == 1) {
            return services.iterator().next();
        }

        Integer highestPriority = null;
        int highestPriorityServiceCount = 0;
        T highestService = null;

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

        return highestService;
    }

    /**
     * Checks the given instance for a @Priority annotation. If present the annotation's value s evaluated. If no such
     * annotation is present, a default priority is returned (1);
     * @param o the instance, not null.
     * @return a priority, by default 1.
     */
    public static int getPriority(Object o){
        int prio = 0;
        Priority priority = o.getClass().getAnnotation(Priority.class);
        if (priority != null) {
            prio = priority.value();
        }
        return prio;
    }

}
