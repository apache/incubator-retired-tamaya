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
package org.apache.tamaya.spi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class ServiceContextTest {

    private final ServiceContext serviceContext = new ServiceContext(){

        @Override
        public int ordinal() {
            return 1;
        }

        @Override
        public <T> T getService(Class<T> serviceType) {
            if(String.class.equals(serviceType)){
                return serviceType.cast("ServiceContextTest");
            }
            return null;
        }

        @Override
        public <T> List<T> getServices(Class<T> serviceType) {
            if(String.class.equals(serviceType)){
                List<String> list = new ArrayList<>();
                list.add("ServiceContextTest");
                return List.class.cast(list);
            }
            return Collections.emptyList();
        }
    };

    @Test
    public void testOrdinal() throws Exception {
        assertEquals(1, serviceContext.ordinal());
    }

    @Test
    public void testgetService() throws Exception {
        assertEquals("ServiceContextTest", serviceContext.getService(String.class));
        assertNull(serviceContext.getService(Integer.class));
    }

    @Test
    public void testGetService() throws Exception {
        String service = serviceContext.getService(String.class);
        assertNotNull(service);
        assertEquals("ServiceContextTest", service);
        Integer intService = serviceContext.getService(Integer.class);
        assertNull(intService);
    }

    @Test
    public void testGetServices() throws Exception {
        Collection<String> services = serviceContext.getServices(String.class);
        assertNotNull(services);
        assertFalse(services.isEmpty());
        assertEquals("ServiceContextTest", services.iterator().next());
        List<Integer> intServices = serviceContext.getServices(Integer.class);
        assertNotNull(intServices);
        assertTrue(intServices.isEmpty());
    }

    @Test
    public void testGetInstance() throws Exception {
        assertNotNull(ServiceContextManager.getServiceContext());
    }

}
