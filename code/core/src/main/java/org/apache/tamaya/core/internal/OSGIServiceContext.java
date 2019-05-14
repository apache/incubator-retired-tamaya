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
package org.apache.tamaya.core.internal;

import org.apache.tamaya.spi.ClassloaderAware;
import org.apache.tamaya.spi.ConfigurationProviderSpi;
import org.apache.tamaya.spi.ServiceContext;
import org.osgi.framework.Bundle;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import java.net.URL;
import java.util.*;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ServiceContext implementation based on OSGI Service mechanisms.
 */
public class OSGIServiceContext implements ServiceContext{

    private static final Logger LOG = Logger.getLogger(OSGIServiceContext.class.getName());
    private static final OSGIServiceComparator REF_COMPARATOR = new OSGIServiceComparator();

    private final OSGIServiceLoader osgiServiceLoader;

    public OSGIServiceContext(OSGIServiceLoader osgiServiceLoader){
        this.osgiServiceLoader = Objects.requireNonNull(osgiServiceLoader);
    }

    public boolean isInitialized(){
        return osgiServiceLoader != null;
    }


    @Override
    public ClassLoader getClassLoader() {
        return getClass().getClassLoader();
    }

    @Override
    public void init(ClassLoader classLoader) {
        throw new IllegalStateException("Classloader already setCurrent on this context.");
    }


    @Override
    public int ordinal() {
        return 10;
    }

    @Override
    public <T> T getService(Class<T> serviceType, Supplier<T> supplier) {
        LOG.finest("TAMAYA  Loading service: " + serviceType.getName());
        ServiceReference<T> ref = this.osgiServiceLoader.getBundleContext().getServiceReference(serviceType);
        if(ref!=null){
            return this.osgiServiceLoader.getBundleContext().getService(ref);
        }
        if(ConfigurationProviderSpi.class==serviceType){
            @SuppressWarnings("unchecked")
            T service = (T)new CoreConfigurationProvider();
            this.osgiServiceLoader.getBundleContext().registerService(
                    serviceType.getName(),
                    service,
                    new Hashtable<String, Object>());
            return service;
        }
        if(supplier!=null){
            T t = supplier.get();
            if(t instanceof ClassloaderAware){
                ((ClassloaderAware)t).init(getClassLoader());
            }
            this.osgiServiceLoader.getBundleContext().registerService(
                    serviceType.getName(),
                    t,
                    new Hashtable<String, Object>());
            return t;
        }
        return null;
    }


    @Override
    public <T> T create(Class<T> serviceType, Supplier<T> supplier) {
        LOG.finest("TAMAYA  Creating service: " + serviceType.getName());
        ServiceReference<T> ref = this.osgiServiceLoader.getBundleContext().getServiceReference(serviceType);
        if(ref!=null){
            try {
                return (T)this.osgiServiceLoader.getBundleContext().getService(ref).getClass().getConstructor()
                        .newInstance();
            } catch (Exception e) {
                if(supplier!=null){
                    return supplier.get();
                }
                return null;
            }
        }
        if(supplier!=null){
            return supplier.get();
        }
        return null;
    }

    @Override
    public <T> List<T> getServices(Class<T> serviceType, Supplier<List<T>> supplier) {
        LOG.finest("TAMAYA  Loading services: " + serviceType.getName());
        List<T> services = loadServices(serviceType, null);
        if(services.isEmpty() && supplier!=null) {
            services = supplier.get();
            try {
                for (T t : services) {
                    this.osgiServiceLoader.getBundleContext().registerService(serviceType, t, new Hashtable<>());
                }
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "Error while getting service.", e);
            }
        }
        return services;
    }

    private <T> List<T> loadServices(Class<T> serviceType, Supplier<List<T>> supplier) {
        LOG.finest("TAMAYA  Loading services: " + serviceType.getName());
        List<ServiceReference<T>> refs = new ArrayList<>();
        List<T> services = new ArrayList<>(refs.size());
        try {
            refs.addAll(this.osgiServiceLoader.getBundleContext().getServiceReferences(serviceType, null));
            Collections.sort(refs, REF_COMPARATOR);
            for(ServiceReference<T> ref:refs){
                T service = osgiServiceLoader.getBundleContext().getService(ref);
                if(service!=null) {
                    services.add(service);
                }
            }
        } catch (InvalidSyntaxException e) {
            LOG.log(Level.INFO,"No services found in OSGI: " + serviceType, e);
        }
        try{
            for(T service:ServiceLoader.load(serviceType)){
                services.add(service);
            }
            return services;
        } catch (Exception e) {
            LOG.log(Level.INFO, "No services found in ServiceLoader: " + serviceType, e);
        }
        if(services.isEmpty() && supplier!=null){
            return supplier.get();
        }
        return services;
    }

    @Override
    public Collection<URL> getResources(String resource){
        LOG.finest("TAMAYA  Loading resources: " + resource);
        List<URL> result = new ArrayList<>();
        URL url = osgiServiceLoader.getBundleContext().getBundle()
                .getEntry(resource);
        if(url != null) {
            LOG.finest("TAMAYA  Resource: " + resource + " found in unregistered bundle " +
                    osgiServiceLoader.getBundleContext().getBundle().getSymbolicName());
            result.add(url);
        }
        for(Bundle bundle: osgiServiceLoader.getResourceBundles()) {
            url = bundle.getEntry(resource);
            if (url != null && !result.contains(url)) {
                LOG.finest("TAMAYA  Resource: " + resource + " found in registered bundle " + bundle.getSymbolicName());
                result.add(url);
            }
        }
        for(Bundle bundle: osgiServiceLoader.getBundleContext().getBundles()) {
            url = bundle.getEntry(resource);
            if (url != null && !result.contains(url)) {
                LOG.finest("TAMAYA  Resource: " + resource + " found in unregistered bundle " + bundle.getSymbolicName());
                result.add(url);
            }
        }
        return result;
    }

    @Override
    public URL getResource(String resource){
        LOG.finest("TAMAYA  Loading resource: " + resource);
        URL url = osgiServiceLoader.getBundleContext().getBundle()
                .getEntry(resource);
        if(url!=null){
            LOG.finest("TAMAYA  Resource: " + resource + " found in bundle " +
                    osgiServiceLoader.getBundleContext().getBundle().getSymbolicName());
            return url;
        }
        for(Bundle bundle: osgiServiceLoader.getResourceBundles()) {
            url = bundle.getEntry(resource);
            if(url != null){
                LOG.finest("TAMAYA  Resource: " + resource + " found in registered bundle " + bundle.getSymbolicName());
                return url;
            }
        }
        for(Bundle bundle: osgiServiceLoader.getBundleContext().getBundles()) {
            url = bundle.getEntry(resource);
            if(url != null){
                LOG.finest("TAMAYA  Resource: " + resource + " found in unregistered bundle " + bundle.getSymbolicName());
                return url;
            }
        }
        return null;
    }

    @Override
    public <T> T register(Class<T> serviceType, T instance, boolean force) {
        Collection<ServiceReference<T>> refs;
        try {
            refs = this.osgiServiceLoader.getBundleContext().getServiceReferences(serviceType, null);
            if(refs!=null && force){
                for(ServiceReference ref:refs) {
                    osgiServiceLoader.getBundleContext().ungetService(ref);
                }
                refs = null;
            }
        } catch (InvalidSyntaxException e) {
            throw new IllegalStateException("Failed to access OSGI services", e);
        }
        if(refs!=null && !refs.isEmpty()){
            return (T)this.osgiServiceLoader.getBundleContext().getService(refs.iterator().next());
        }
        this.osgiServiceLoader.getBundleContext().registerService(
                serviceType, instance, new Hashtable<>()
        );
        return instance;
    }

    @Override
    public <T> List<T> register(Class<T> serviceType, List<T> instances, boolean force) {
        Collection<ServiceReference<T>> refs;
        try {
            refs = this.osgiServiceLoader.getBundleContext().getServiceReferences(serviceType, null);
            if(refs!=null && force){
                for(ServiceReference ref:refs) {
                    osgiServiceLoader.getBundleContext().ungetService(ref);
                }
                refs = null;
            }
        } catch (InvalidSyntaxException e) {
            throw new IllegalStateException("Failed to access OSGI services", e);
        }
        if(refs!=null && !refs.isEmpty()){
            List<T> result = new ArrayList<>();
            for(ServiceReference ref:refs) {
                T item = (T) this.osgiServiceLoader.getBundleContext().getService(ref);
                if (item != null) {
                    result.add(item);
                }
            }
            return result;
        }else{
            for(T instance:instances) {
                this.osgiServiceLoader.getBundleContext().registerService(
                        serviceType, instance, new Hashtable<>()
                );
            }
            return instances;
        }
    }

    @Override
    public void reset() {

    }

}
