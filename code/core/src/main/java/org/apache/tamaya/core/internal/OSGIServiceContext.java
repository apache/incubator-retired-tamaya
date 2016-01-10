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

import org.apache.tamaya.spi.ServiceContext;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * ServiceContext implementation based on OSGI Service mechanisms.
 */
public class OSGIServiceContext implements ServiceContext{

    private static final OSGIServiceComparator REF_COMPARATOR = new OSGIServiceComparator();

    private final BundleContext bundleContext;

    public OSGIServiceContext(BundleContext bundleContext){
        this.bundleContext = Objects.requireNonNull(bundleContext);
    }

    public boolean isInitialized(){
        return bundleContext != null;
    }


    @Override
    public int ordinal() {
        return 10;
    }

    @Override
    public <T> T getService(Class<T> serviceType) {
        ServiceReference<T> ref = this.bundleContext.getServiceReference(serviceType);
        if(ref!=null){
            return this.bundleContext.getService(ref);
        }
        return null;
    }

    @Override
    public <T> List<T> getServices(Class<T> serviceType) {
        List<ServiceReference<T>> refs = new ArrayList<>();
        try {
            refs.addAll(this.bundleContext.getServiceReferences(serviceType, null));
            Collections.sort(refs, REF_COMPARATOR);
            List<T> services = new ArrayList<>(refs.size());
            for(ServiceReference<T> ref:refs){
                T service = bundleContext.getService(ref);
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
}
