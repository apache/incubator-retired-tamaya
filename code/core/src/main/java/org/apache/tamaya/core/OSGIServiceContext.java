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
package org.apache.tamaya.core;

import org.apache.tamaya.base.ServiceContext;
import org.osgi.framework.Bundle;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import javax.config.spi.ConfigProviderResolver;
import java.io.IOException;
import java.net.URL;
import java.util.*;
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
    public int ordinal() {
        return 10;
    }

    @Override
    public <T> T getService(Class<T> serviceType) {
        LOG.finest("TAMAYA  Loading service: " + serviceType.getName());
        ServiceReference<T> ref = this.osgiServiceLoader.getBundleContext().getServiceReference(serviceType);
        if(ref!=null){
            return this.osgiServiceLoader.getBundleContext().getService(ref);
        }
        if(ConfigProviderResolver.class==serviceType){
            @SuppressWarnings("unchecked")
			T service = (T)new TamayaConfigProviderResolver();
            this.osgiServiceLoader.getBundleContext().registerService(
                    serviceType.getName(),
                    service,
                    new Hashtable<String, Object>());
            return service;
        }
        return null;
    }

    @Override
    public <T> T getService(Class<T> serviceType, ClassLoader classLoader) {
        return getService(serviceType);
    }

    @SuppressWarnings("unchecked")
	@Override
    public <T> T create(Class<T> serviceType) {
        LOG.finest("TAMAYA  Creating service: " + serviceType.getName());
        ServiceReference<T> ref = this.osgiServiceLoader.getBundleContext().getServiceReference(serviceType);
        if(ref!=null){
            try {
                return (T)this.osgiServiceLoader.getBundleContext().getService(ref).getClass().newInstance();
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    @Override
    public <T> T create(Class<T> serviceType, ClassLoader classLoader) {
        return create(serviceType);
    }

    @Override
    public <T> List<T> getServices(Class<T> serviceType) {
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
            e.printStackTrace();
        }
        try{
            for(T service:ServiceLoader.load(serviceType)){
                services.add(service);
            }
            return services;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return services;
    }

    @Override
    public <T> List<T> getServices(Class<T> serviceType, ClassLoader classLoader) {
        return getServices(serviceType);
    }

    @Override
    public Enumeration<URL> getResources(String resource, ClassLoader cl) throws IOException{
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
        return Collections.enumeration(result);
    }

    @Override
    public URL getResource(String resource, ClassLoader cl){
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
}
