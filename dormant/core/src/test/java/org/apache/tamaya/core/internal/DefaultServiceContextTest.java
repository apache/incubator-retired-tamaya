/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tamaya.core.internal;

import org.apache.tamaya.ConfigException;
import org.apache.tamaya.spi.ConfigurationContext;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Priority;
import java.util.List;
import java.util.Optional;

public class DefaultServiceContextTest {

    /**
     * context to test
     */
    private DefaultServiceContext context = new DefaultServiceContext();


//    @Test
//    public void testGetService() {
//        Optional<ConfigurationContext> configurationContext = context.getService(ConfigurationContext.class);
//
//        Assert.assertNotNull(configurationContext);
//        Assert.assertTrue(configurationContext.isPresent());
//        Assert.assertTrue(configurationContext.get() instanceof DefaultConfigurationContext);
//    }
//
//    @Test(expected = ConfigException.class)
//    public void testGetService_multipleServicesWithoutPriority_shouldThrowConfigException() {
//        context.getService(InvalidPriorityInterface.class);
//    }
//
//    @Test
//    public void testGetService_multipleService_shouldReturnServiceWithHighestPriority() {
//        Optional<MultiImplsInterface> service = context.getService(MultiImplsInterface.class);
//
//        Assert.assertTrue(service.isPresent());
//        Assert.assertTrue(service.get() instanceof MultiImpl2);
//    }
//
//    @Test
//    public void testGetService_noImpl_shouldReturnEmptyOpional() {
//        Optional<NoImplInterface> service = context.getService(NoImplInterface.class);
//        Assert.assertFalse(service.isPresent());
//    }


    @Test
    public void testGetServices_shouldReturnServices() {
        {
            List<InvalidPriorityInterface> services = context.getServices(InvalidPriorityInterface.class);
            Assert.assertNotNull(services);
            Assert.assertEquals(2, services.size());

            for (InvalidPriorityInterface service : services) {
                Assert.assertTrue(service instanceof InvalidPriorityImpl1 || service instanceof InvalidPriorityImpl2);
            }
        }

        {
            List<MultiImplsInterface> services = context.getServices(MultiImplsInterface.class);
            Assert.assertNotNull(services);
            Assert.assertEquals(3, services.size());

            for (MultiImplsInterface service : services) {
                Assert.assertTrue(service instanceof MultiImpl1 ||
                                          service instanceof MultiImpl2 ||
                                          service instanceof MultiImpl3);
            }
        }
    }

    @Test
    public void testGetServices_noImpl_shouldReturnEmptyList() {
        List<NoImplInterface> services = context.getServices(NoImplInterface.class);
        Assert.assertNotNull(services);
        Assert.assertTrue(services.isEmpty());
    }


    // some test interfaces and classes

    public static interface InvalidPriorityInterface {
    }

    @Priority(value = 50)
    public static class InvalidPriorityImpl1 implements InvalidPriorityInterface {
    }

    @Priority(value = 50)
    public static class InvalidPriorityImpl2 implements InvalidPriorityInterface {
    }


    public static interface MultiImplsInterface {
    }

    public static class MultiImpl1 implements MultiImplsInterface {
    }

    @Priority(value = 500)
    public static class MultiImpl2 implements MultiImplsInterface {
    }

    @Priority(value = -10)
    public static class MultiImpl3 implements MultiImplsInterface {
    }


    private static interface NoImplInterface {
    }
}
