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

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

/**
 *
 * @author William.Lieurance 2018-02-05
 */
public class MockBundleContext implements BundleContext {

    private ArrayList<Bundle> bundles = new ArrayList<>();

    @Override
    public String getProperty(String string) {
        throw new UnsupportedOperationException("Not supported (MockBundleContext getProperty)");
    }

    @Override
    public Bundle getBundle() {
        return bundles.get(0);
    }

    @Override
    public Bundle installBundle(String string, InputStream in) throws BundleException {
        throw new UnsupportedOperationException("Not supported (MockBundleContext installBundle)");
    }

    @Override
    public Bundle installBundle(String string) throws BundleException {
        throw new UnsupportedOperationException("Not supported (MockBundleContext installBundle)");
    }

    public Bundle installBundle(Bundle bundle) {
        bundles.add(bundle);
        return bundle;
    }

    @Override
    public Bundle getBundle(long l) {
        return bundles.get(0);
    }

    @Override
    public Bundle[] getBundles() {
        return bundles.toArray(new Bundle[bundles.size()]);
    }

    @Override
    public void addServiceListener(ServiceListener sl, String string) throws InvalidSyntaxException {
        throw new UnsupportedOperationException("Not supported (MockBundleContext addServiceListener)");
    }

    @Override
    public void addServiceListener(ServiceListener sl) {
        throw new UnsupportedOperationException("Not supported (MockBundleContext addServiceListener)");
    }

    @Override
    public void removeServiceListener(ServiceListener sl) {
        throw new UnsupportedOperationException("Not supported (MockBundleContext removeServiceListener)");
    }
    
    int bundleListenersCount = 0;

    public int getBundleListenersCount() {
        return bundleListenersCount;
    }

    public void setBundleListenersCount(int bundleListenersCount) {
        this.bundleListenersCount = bundleListenersCount;
    }

    @Override
    public void addBundleListener(BundleListener bl) {
        bundleListenersCount++;
    }

    @Override
    public void removeBundleListener(BundleListener bl) {
        bundleListenersCount--;
    }

    @Override
    public void addFrameworkListener(FrameworkListener fl) {
        throw new UnsupportedOperationException("Not supported (MockBundleContext addFrameworkListener)");
    }

    @Override
    public void removeFrameworkListener(FrameworkListener fl) {
        throw new UnsupportedOperationException("Not supported (MockBundleContext removeFrameworkListener)");
    }

    int serviceCount = 0;

    public int getServiceCount() {
        return serviceCount;
    }

    public void setServiceCount(int serviceCount) {
        this.serviceCount = serviceCount;
    }

    @Override
    public ServiceRegistration<?> registerService(String[] strings, Object o, Dictionary<String, ?> dctnr) {
        serviceCount++;
        return null;
    }

    @Override
    public ServiceRegistration<?> registerService(String string, Object o, Dictionary<String, ?> dctnr) {
        serviceCount++;
        return null;
    }

    @Override
    public <S> ServiceRegistration<S> registerService(Class<S> type, S s, Dictionary<String, ?> dctnr) {
        serviceCount++;
        return null;
    }

    @Override
    public ServiceReference<?>[] getServiceReferences(String string, String string1) throws InvalidSyntaxException {
        return new ServiceReference[0];
    }

    @Override
    public ServiceReference<?>[] getAllServiceReferences(String string, String string1) throws InvalidSyntaxException {
        throw new UnsupportedOperationException("Not supported (MockBundleContext getAllServiceReferences)");
    }

    @Override
    public ServiceReference<?> getServiceReference(String string) {
        throw new UnsupportedOperationException("Not supported (MockBundleContext getServiceReference)");
    }

    @Override
    public <S> ServiceReference<S> getServiceReference(Class<S> type) {
        return null;
    }

    @Override
    public <S> Collection<ServiceReference<S>> getServiceReferences(Class<S> type, String string) throws InvalidSyntaxException {
        return new ArrayList();
    }

    @Override
    public <S> S getService(ServiceReference<S> sr) {
        throw new UnsupportedOperationException("Not supported (MockBundleContext getService)");
    }

    @Override
    public boolean ungetService(ServiceReference<?> sr) {
        throw new UnsupportedOperationException("Not supported (MockBundleContext ungetService)");
    }

    @Override
    public File getDataFile(String string) {
        throw new UnsupportedOperationException("Not supported (MockBundleContext getDataFile)");
    }

    @Override
    public Filter createFilter(String string) throws InvalidSyntaxException {
        throw new UnsupportedOperationException("Not supported (MockBundleContext createFilter)");
    }

    @Override
    public Bundle getBundle(String string) {
        return bundles.get(0);
    }
};
