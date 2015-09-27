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
import org.apache.tamaya.clsupport.AbstractClassloaderAwareItemLoader;
import org.apache.tamaya.spi.ServiceContext;

import javax.annotation.Priority;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * This class implements a {@link ServiceContext}, which basically provides a similar loading mechanism as used
 * by the {@link java.util.ServiceLoader}. Whereas the {@link java.util.ServiceLoader} only loads configurations
 * and instances from one classloader, this loader manages configs found and the related instances for each
 * classloader along the classloader hierarchies individually. It ensures instances are loaded on the classloader
 * level, where they first are visible. Additionally it ensures the same configuration resource (and its
 * declared services) are loaded multiple times, when going up the classloader hierarchy.<p/>
 * Finally classloaders are not stored by reference by this class, to ensure they still can be garbage collected.
 * Refer also the inherited parent class for further details.<p/>
 * This class uses an ordinal of {@code 10}, so it overrides any default {@link ServiceContext} implementations
 * provided with the Tamaya core modules.
 */
@Priority(10)
public class CLAwareServiceContext extends AbstractClassloaderAwareItemLoader<ServiceContainer>
        implements ServiceContext{

    private static final Logger LOG = Logger.getLogger(CLAwareServiceContext.class.getName());

    /**
     * Default location for service loader files.
     */
    private static final String PREFIX = "META-INF/services/";

    /**
     * Constructor, using the current default classloader as defined by
     * {@link AbstractClassloaderAwareItemLoader#getDefaultClassLoader()}.
     */
    public CLAwareServiceContext(){
        super();
    }

    /**
     * Constructor, using the given classloader.
     * @param classLoader the target classloader for initializing of services, not null.
     */
    public CLAwareServiceContext(ClassLoader classLoader) {
        super(classLoader);
    }


    /**
     * Implementation that creates a {@link ServiceContainer}, which manages all configs and instances loaded
     * for a given classloader.
     * @param classLoader the classloader, not null.
     * @return a new empty, {@link ServiceContainer} instance.
     */
    @Override
    protected ServiceContainer createItem(ClassLoader classLoader) {
        if(LOG.isLoggable(Level.INFO)) {
            LOG.info("Loading services for classloader: " + classLoader);
        }
        return new ServiceContainer(classLoader);
    }

    @Override
    protected void updateItem(ServiceContainer currentContainer, ClassLoader classLoader) {
        // nothing to be done here, since we dont have a specific target type.
    }

    @Override
    public int ordinal() {
        return 10;
    }

    /**
     * This method tries to evaluate the current singleton from the {@link ServiceContainer} attached to the
     * current classloader. If not found the singleton instance is evaluated based on the priorities
     * assigned for all known providers. The resulting instance is then cached and always returned as
     * singleton instance fomr this loader, when the same current classloader instance is active.
     * @param serviceType the service type.
     * @param <T> the type
     * @return the item found, or null.
     */
    @Override
    public <T> T getService(Class<T> serviceType) {
        return getService(serviceType, getDefaultClassLoader());
    }

    /**
     * Evaluates the current singleton instance using the given classloader context.
     * @param serviceType the service type.
     * @param classLoader the classloader, not null.
     * @param <T> the type
     * @return the item found, or null.
     */
    public <T> T getService(Class<T> serviceType, ClassLoader classLoader) {
        if(LOG.isLoggable(Level.INFO)) {
            LOG.info("Evaluating services for classloader: " + classLoader);
        }
        ServiceContainer container = getItemNoParent(classLoader, true);
        T singleton = container.getSingleton(serviceType);
        if(singleton!=null){
            if(LOG.isLoggable(Level.FINEST)) {
                LOG.finest("Evaluated singleton of type " + serviceType.getName() + " to " + singleton);
            }
            return singleton;
        }
        Collection<? extends T> services = getServices(serviceType, classLoader);
        if (services.isEmpty()) {
            singleton = null;
        } else {
            singleton = getServiceWithHighestPriority(services, serviceType);
        }
        if(singleton!=null) {
            container.setSingleton(serviceType, singleton);
        }
        if(LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Evaluated singleton of type " + serviceType.getName() + " to " + singleton);
        }
        return singleton;
    }

    /**
     * Gets the services visible.
     * @param serviceType
     *            the service type.
     * @param <T> the type param
     * @return the services visible for the current classloader.
     */
    @Override
    public <T> List<T> getServices(Class<T> serviceType) {
        return getServices(serviceType, AbstractClassloaderAwareItemLoader.getDefaultClassLoader());
    }

    /**
     * Gets the services visible.
     * @param serviceType the service type.
     * @param classLoader the classloader
     * @param <T> the type param
     * @return the services visible for the current classloader.
     */
    public <T> List<T> getServices(Class<T> serviceType, ClassLoader classLoader) {
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
            }
            services.addAll(container.getServices(serviceType));
            prevContainers.add(container);
        }
        if(LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Evaluated services of type " + serviceType.getName() + " to " + services);
        }
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
        if(LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Evaluated priority for " + o.getClass().getName() + " to " + prio);
        }
        return prio;
    }

}
