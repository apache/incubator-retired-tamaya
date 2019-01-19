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

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.assertj.core.api.Assertions.*;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 *
 * @author William.Lieurance 2018-02-10
 */
public class OSGIServiceContextTest {

    /**
     * Test of isInitialized method, of class OSGIServiceContext.
     */
    @Test
    public void testIsInitialized() {
        OSGIServiceContext instance = new OSGIServiceContext(Mockito.mock(OSGIServiceLoader.class));
        assertThat(instance.isInitialized()).isTrue();
    }

    /**
     * Test of ordinal method, of class OSGIServiceContext.
     */
    @Test
    public void testOrdinal() {
        OSGIServiceContext instance = new OSGIServiceContext(Mockito.mock(OSGIServiceLoader.class));
        assertThat(instance.ordinal()).isEqualTo(10);
    }

    /**
     * Test of createObject method, of class OSGIServiceContext.
     */
    @Test
    public void testCreateThenGet() {
        BundleContext mockBundleContext = new MockBundleContext();
        OSGIServiceLoader loader = new OSGIServiceLoader(mockBundleContext);
        OSGIServiceContext instance = new OSGIServiceContext(loader);

        Integer value = instance.create(Integer.class);
        assertThat(value).isNull();
        value = instance.getService(Integer.class);
        assertThat(value).isNull();
    }

    /**
     * Test of getServices method, of class OSGIServiceContext.
     */
    @Test
    public void testGetServices() {
        BundleContext mockBundleContext = new MockBundleContext();
        OSGIServiceLoader loader = new OSGIServiceLoader(mockBundleContext);
        OSGIServiceContext instance = new OSGIServiceContext(loader);

        List services = instance.getServices(Integer.class);
        assertThat(services).isNotNull();
        assertThat(services).isEmpty();
    }

    /**
     * Test of getResources method, of class OSGIServiceContext.
     * @throws java.io.IOException
     */
    @Test
    public void testGetResources() throws IOException {
        MockBundleContext mockBundleContext = new MockBundleContext();
        OSGIServiceLoader loader = new OSGIServiceLoader(mockBundleContext);
        MockBundle startedBundle = new MockBundle();
        startedBundle.setState(Bundle.ACTIVE);
        startedBundle.setBundleId(1);
        startedBundle.setBundleContext(mockBundleContext);
        mockBundleContext.installBundle(startedBundle);
        OSGIServiceContext instance = new OSGIServiceContext(loader);

        Enumeration<URL> resources = instance.getResources("dummy");
        assertThat(resources).isNotNull();
        URL resource = (URL)resources.nextElement();
        assertThat(resource.toString()).contains("mockbundle.service");
        assertThat(resources.hasMoreElements()).isFalse();
    }

    /**
     * Test of getResource method, of class OSGIServiceContext.
     */
    @Test
    public void testGetResource() {
        MockBundleContext mockBundleContext = new MockBundleContext();
        OSGIServiceLoader loader = new OSGIServiceLoader(mockBundleContext);
        MockBundle startedBundle = new MockBundle();
        startedBundle.setState(Bundle.ACTIVE);
        startedBundle.setBundleId(1);
        startedBundle.setBundleContext(mockBundleContext);
        mockBundleContext.installBundle(startedBundle);
        OSGIServiceContext instance = new OSGIServiceContext(loader);

        URL resource = instance.getResource("mockbundle.service");
        assertThat(resource).isNotNull();
        assertThat(resource.toString()).contains("mockbundle.service");
    }

}
