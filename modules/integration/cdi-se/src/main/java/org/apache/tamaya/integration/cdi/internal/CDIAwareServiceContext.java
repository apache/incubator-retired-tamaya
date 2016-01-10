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
package org.apache.tamaya.integration.cdi.internal;

import org.apache.tamaya.ConfigException;
import org.apache.tamaya.clsupport.CLAwareServiceContext;
import org.apache.tamaya.spi.ServiceContext;

import javax.annotation.Priority;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


/**
 * <p>This class implements a {@link ServiceContext}, which basically provides a similar loading mechanism as used
 * by the {@link java.util.ServiceLoader}. Whereas the {@link java.util.ServiceLoader} only loads configurations
 * and instances from one classloader, this loader manages configs found and the related instances for each
 * classloader along the classloader hierarchies individually. It ensures instances are loaded on the classloader
 * level, where they first are visible. Additionally it ensures the same configuration resource (and its
 * declared services) are loaded multiple times, when going up the classloader hierarchy.</p>
 *
 * <p>Finally classloaders are not stored by reference by this class, to ensure they still can be garbage collected.
 * Refer also the inherited parent class for further details.</p>
 *
 * <p>This class uses an ordinal of {@code 10}, so it overrides any default {@link ServiceContext} implementations
 * provided with the Tamaya core modules.</p>
 */
public class CDIAwareServiceContext implements ServiceContext {
    /**
     * List current services loaded, per classloader.
     */
    private final CLAwareServiceContext clAwareServiceContext = new CLAwareServiceContext();

    /**
     * Singletons.
     */
    private final Map<Class<?>, Object> singletons = new ConcurrentHashMap<>();


    @Override
    public <T> T getService(Class<T> serviceType) {
        Object cached = singletons.get(serviceType);
        if (cached == null) {
            Collection<T> services = getServices(serviceType);
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

    /**
     * Loads and registers services.
     *
     * @param <T>         the concrete type.
     * @param serviceType The service type.
     * @return the items found, never {@code null}.
     */
    @Override
    public <T> List<T> getServices(final Class<T> serviceType) {
        List<T> found = clAwareServiceContext.getServices(serviceType);
        BeanManager beanManager = TamayaCDIIntegration.getBeanManager();
        Instance<T> cdiInstances = null;
        if(beanManager!=null){
            Set<Bean<?>> instanceBeans = beanManager.getBeans(Instance.class);
            cdiInstances = (Instance<T>)beanManager.getReference(instanceBeans.iterator().next(), Instance.class, null);
        }
        if(cdiInstances!=null){
            for(T t:cdiInstances.select(serviceType)){
                found.add(t);
            }
        }
        return found;
    }

    /**
     * Checks the given instance for a @Priority annotation. If present the annotation's value s evaluated. If no such
     * annotation is present, a default priority is returned (1);
     * @param o the instance, not null.
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
     * @return the service with the highest {@link javax.annotation.Priority#value()}
     *
     * @throws ConfigException if there are multiple service implementations with the maximum priority
     */
    private <T> T getServiceWithHighestPriority(Collection<T> services, Class<T> serviceType) {

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
     * Returns ordinal of 20, overriding defaults as well as the inherited (internally used) CLAwareServiceContext
     * instance.
     * @return ordinal of 20.
     */
    @Override
    public int ordinal() {
        return 20;
    }

}
