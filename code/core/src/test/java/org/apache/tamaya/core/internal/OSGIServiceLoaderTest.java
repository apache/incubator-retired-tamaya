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

import java.util.Set;
import org.junit.Test;
import static org.junit.Assert.*;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;

/**
 *
 * @author William.Lieurance 2018-02-06
 */
public class OSGIServiceLoaderTest {

    /**
     * Test of getBundleContext method, of class OSGIServiceLoader.
     */
    @Test
    public void testGetBundleContext() {
        BundleContext mockBundleContext = new MockBundleContext();
        OSGIServiceLoader instance = new OSGIServiceLoader(mockBundleContext);
        BundleContext result = instance.getBundleContext();
        assertEquals(mockBundleContext, result);
    }

    /**
     * Test of getResourceBundles method, of class OSGIServiceLoader.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGetResourceBundles() throws Exception {
        MockBundleContext mockBundleContext = new MockBundleContext();
        MockBundle startedBundle = new MockBundle();
        startedBundle.setState(Bundle.ACTIVE);
        startedBundle.setBundleId(1);
        startedBundle.setBundleContext(mockBundleContext);
        mockBundleContext.installBundle(startedBundle);
        OSGIServiceLoader instance = new OSGIServiceLoader(mockBundleContext);
        Set<Bundle> result = instance.getResourceBundles();
        assertFalse(result.isEmpty());
    }

    /**
     * Test of bundleChanged method, of class OSGIServiceLoader.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testBundleChanged() throws Exception {
        //Set up mocks
        Set<Bundle> resultBundles;
        MockBundleContext mockBundleContext = new MockBundleContext();
        MockBundle startedBundle = new MockBundle();
        startedBundle.setState(Bundle.ACTIVE);
        startedBundle.setBundleId(1);
        startedBundle.setBundleContext(mockBundleContext);
        MockBundle stoppedBundle = new MockBundle();
        stoppedBundle.setState(Bundle.INSTALLED);
        stoppedBundle.setBundleId(2);
        stoppedBundle.setBundleContext(mockBundleContext);
        MockBundle flipBundle = new MockBundle();
        flipBundle.setState(Bundle.INSTALLED);
        flipBundle.setBundleId(3);
        flipBundle.setBundleContext(mockBundleContext);
        mockBundleContext.installBundle(startedBundle);
        mockBundleContext.installBundle(stoppedBundle);
        mockBundleContext.installBundle(flipBundle);

        //Default case
        mockBundleContext.setServiceCount(0);
        OSGIServiceLoader instance = new OSGIServiceLoader(mockBundleContext);
        resultBundles = instance.getResourceBundles();
        assertEquals(1, resultBundles.size());
        assertEquals(2, mockBundleContext.getServiceCount());

        //After start
        mockBundleContext.setServiceCount(0);
        BundleEvent startedEvent = new BundleEvent(BundleEvent.STARTED, flipBundle);
        instance.bundleChanged(startedEvent);
        resultBundles = instance.getResourceBundles();
        assertEquals(2, resultBundles.size());
        assertEquals(2, mockBundleContext.getServiceCount());

        //After stop
        mockBundleContext.setServiceCount(0);
        BundleEvent stoppedEvent = new BundleEvent(BundleEvent.STOPPED, flipBundle);
        instance.bundleChanged(stoppedEvent);
        resultBundles = instance.getResourceBundles();
        assertEquals(1, resultBundles.size());
        assertEquals(0, mockBundleContext.getServiceCount());
    }

    
}
