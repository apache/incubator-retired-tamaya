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

import org.apache.tamaya.spi.ConfigurationProviderSpi;
import org.apache.tamaya.spi.ServiceContext;
import org.osgi.framework.Bundle;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * ServiceContext implementation based on OSGI Service mechanisms.
 */
public class OSGIServiceContext implements ServiceContext{

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
        ServiceReference<T> ref = this.osgiServiceLoader.getBundleContext().getServiceReference(serviceType);
        if(ref!=null){
            return this.osgiServiceLoader.getBundleContext().getService(ref);
        }
        if(ConfigurationProviderSpi.class==serviceType){
            T service = (T)new DefaultConfigurationProvider();
            this.osgiServiceLoader.getBundleContext().registerService(
                    serviceType.getName(),
                    service,
                    new Hashtable<String, Object>());
            return service;
        }
        return null;
    }

    @Override
    public <T> T create(Class<T> serviceType) {
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
    public <T> List<T> getServices(Class<T> serviceType) {
        List<ServiceReference<T>> refs = new ArrayList<>();
        try {
            refs.addAll(this.osgiServiceLoader.getBundleContext().getServiceReferences(serviceType, null));
            Collections.sort(refs, REF_COMPARATOR);
            List<T> services = new ArrayList<>(refs.size());
            for(ServiceReference<T> ref:refs){
                T service = osgiServiceLoader.getBundleContext().getService(ref);
                if(service!=null) {
                    services.add(service);
                }
            }
            return services;
        } catch (InvalidSyntaxException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Override
    public Enumeration<URL> getResources(String resource, ClassLoader cl) throws IOException{
        List<URL> result = new ArrayList<>();
        URL url = osgiServiceLoader.getBundleContext().getBundle()
                .getEntry(resource);
        if(url != null) {
            result.add(url);
        }
        for(Bundle bundle: osgiServiceLoader.getResourceBundles()) {
            url = bundle.getEntry(resource);
            if (url != null) {
                if(!result.contains(url)) {
                    result.add(url);
                }
            }
        }
        return Collections.enumeration(result);
    }

    @Override
    public URL getResource(String resource, ClassLoader cl){
        URL url = osgiServiceLoader.getBundleContext().getBundle()
                .getEntry(resource);
        if(url!=null){
            return url;
        }
        for(Bundle bundle: osgiServiceLoader.getResourceBundles()) {
            url = bundle.getEntry(resource);
            if(url != null){
                return url;
            }
        }
        return null;
    }
}
