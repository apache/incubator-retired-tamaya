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
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;

/**
 *
 * @author William.Lieurance 2018-02-05
 */
public class MockBundle implements Bundle {

    private int state = Bundle.ACTIVE;

    @Override
    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    @Override
    public void start(int i) throws BundleException {
        throw new UnsupportedOperationException("Not supported (MockBundle)");
    }

    @Override
    public void start() throws BundleException {
        throw new UnsupportedOperationException("Not supported (MockBundle)");
    }

    @Override
    public void stop(int i) throws BundleException {
        throw new UnsupportedOperationException("Not supported (MockBundle)");
    }

    @Override
    public void stop() throws BundleException {
        throw new UnsupportedOperationException("Not supported (MockBundle)");
    }

    @Override
    public void update(InputStream in) throws BundleException {
        throw new UnsupportedOperationException("Not supported (MockBundle)");
    }

    @Override
    public void update() throws BundleException {
        throw new UnsupportedOperationException("Not supported (MockBundle)");
    }

    @Override
    public void uninstall() throws BundleException {
        throw new UnsupportedOperationException("Not supported (MockBundle)");
    }

    @Override
    public Dictionary<String, String> getHeaders() {
        return new Hashtable<>();
    }

    private long bundleId = 1L;

    @Override
    public long getBundleId() {
        return bundleId;
    }

    public void setBundleId(long bundleId) {
        this.bundleId = bundleId;
    }

    @Override
    public String getLocation() {
        throw new UnsupportedOperationException("Not supported (MockBundle)");
    }

    @Override
    public ServiceReference<?>[] getRegisteredServices() {
        throw new UnsupportedOperationException("Not supported (MockBundle)");
    }

    @Override
    public ServiceReference<?>[] getServicesInUse() {
        throw new UnsupportedOperationException("Not supported (MockBundle)");
    }

    @Override
    public boolean hasPermission(Object o) {
        throw new UnsupportedOperationException("Not supported (MockBundle)");
    }

    @Override
    public URL getResource(String string) {
        throw new UnsupportedOperationException("Not supported (MockBundle)");
    }

    @Override
    public Dictionary<String, String> getHeaders(String string) {
        return new Hashtable<>();
    }

    @Override
    public String getSymbolicName() {
        return "MockBundle";
    }

    @Override
    public Class<?> loadClass(String string) throws ClassNotFoundException {
        if (string.contains("org.something.else") || string.endsWith("/")) {
            throw new UnsupportedOperationException("Requested class that should not be requested: " + string);
        }
        return String.class;
    }

    @Override
    public Enumeration<URL> getResources(String string) throws IOException {
        throw new UnsupportedOperationException("Not supported (MockBundle)");
    }

    @Override
    public Enumeration<String> getEntryPaths(String string) {
        Vector<String> v = new Vector<>();
        v.add("META-INF/services/" + "someslash/");
        v.add("META-INF/services/" + "org.apache.tamaya");
        v.add("META-INF/services/" + "org.something.else");
        return v.elements();
    }

    @Override
    public URL getEntry(String string) {
        if (string.equals("META-INF/services/")) {
            try {
                return new URL("file:///");
            } catch (MalformedURLException ex) {
                return null;
            }
        }
        if (string.contains("org.something.else") || string.endsWith("/")) {
            throw new UnsupportedOperationException("Requested entry that should not be requested: " + string);
        }
        return getClass().getClassLoader().getResource("mockbundle.service");
    }

    @Override
    public long getLastModified() {
        throw new UnsupportedOperationException("Not supported (MockBundle)");
    }

    @Override
    public Enumeration<URL> findEntries(String string, String string1, boolean bln) {
        throw new UnsupportedOperationException("Not supported (MockBundle)");
    }

    private BundleContext bundleContext = new MockBundleContext();

    @Override
    public BundleContext getBundleContext() {
        return bundleContext;
    }

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    @Override
    public Map<X509Certificate, List<X509Certificate>> getSignerCertificates(int i) {
        throw new UnsupportedOperationException("Not supported (MockBundle)");
    }

    @Override
    public Version getVersion() {
        return new Version(0, 0, 1);
    }

    @Override
    public <A> A adapt(Class<A> type) {
        throw new UnsupportedOperationException("Not supported (MockBundle)");
    }

    @Override
    public File getDataFile(String string) {
        throw new UnsupportedOperationException("Not supported (MockBundle)");
    }

    @Override
    public int compareTo(Bundle o) {
        return Long.compare(this.getBundleId(), o.getBundleId());
    }

}
