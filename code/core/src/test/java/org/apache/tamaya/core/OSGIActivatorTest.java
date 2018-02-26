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

import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;
import org.apache.tamaya.core.internal.MockBundle;
import org.apache.tamaya.core.internal.MockBundleContext;
import org.apache.tamaya.spi.ServiceContext;
import org.apache.tamaya.spi.ServiceContextManager;
import org.junit.After;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.Bundle;

/**
 *
 * @author William.Lieurance 2018-02-05
 */
public class OSGIActivatorTest {

    ServiceContext prevServiceContext;
    Configuration prevConfiguration;

    @Before
    public void setUp() throws Exception {
        prevServiceContext = ServiceContextManager.getServiceContext();
        prevConfiguration = ConfigurationProvider.getConfiguration();
    }

    @After
    public void tearDown() throws Exception {
        ServiceContextManager.set(prevServiceContext);
        ConfigurationProvider.setConfiguration(prevConfiguration);
    }

    /**
     * Test of start and stop methods, of class OSGIActivator.
     */
    @Test
    public void testStartThenStop() {
        //Set up the mock
        MockBundleContext mockBundleContext = new MockBundleContext();
        MockBundle startedBundle = new MockBundle();
        startedBundle.setState(Bundle.ACTIVE);
        startedBundle.setBundleId(1);
        startedBundle.setBundleContext(mockBundleContext);
        mockBundleContext.installBundle(startedBundle);
        OSGIActivator instance = new OSGIActivator();

        //Start
        instance.start(mockBundleContext);
        assertThat(mockBundleContext.getBundleListenersCount()).isEqualTo(1);
        assertThat(ConfigurationProvider.getConfiguration().getContext().getPropertyConverters().isEmpty()).isFalse();
        assertThat(ConfigurationProvider.getConfiguration()).isNotSameAs(prevConfiguration);

        //Stop
        instance.stop(mockBundleContext);
        assertThat(mockBundleContext.getBundleListenersCount()).isEqualTo(0);
    }

}
