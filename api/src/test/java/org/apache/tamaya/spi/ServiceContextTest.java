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

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class ServiceContextTest {

    private ServiceContext serviceContext = new ServiceContext(){

        @Override
        public <T> Optional<T> getService(Class<T> serviceType) {
            if(String.class.equals(serviceType)){
                return Optional.of(serviceType.cast("ServiceContextTest"));
            }
            return Optional.empty();
        }

        @Override
        public <T> List<T> getServices(Class<T> serviceType, Supplier<List<T>> defaultList) {
            if(String.class.equals(serviceType)){
                List<String> list = new ArrayList<>();
                list.add("ServiceContextTest");
                return List.class.cast(list);
            }
            return defaultList.get();
        }
    };

    @Test
    public void testOrdinal() throws Exception {
        assertEquals(1, serviceContext.ordinal());
    }

    @Test
    public void testGetSingleton() throws Exception {
        assertEquals("ServiceContextTest", serviceContext.getSingleton(String.class));
        try{
            serviceContext.getSingleton(Integer.class);
            fail("ServiceContext should throw IllegalStateException");
        }
        catch(IllegalStateException e){
            // OK, as expected
        }
    }

    @Test
    public void testGetSingleton_WithDefault() throws Exception {
        assertEquals("ServiceContextTest", serviceContext.getSingleton(String.class, () -> "blabla"));
        assertEquals(Integer.valueOf(4), serviceContext.getSingleton(Integer.class, () -> Integer.valueOf(4)));
    }

    @Test
    public void testGetService() throws Exception {
        Optional<String> service = serviceContext.getService(String.class);
        assertNotNull(service);
        assertTrue(service.isPresent());
        assertEquals("ServiceContextTest", service.get());
        Optional<Integer> intService = serviceContext.getService(Integer.class);
        assertNotNull(intService);
        assertFalse(intService.isPresent());
    }

    @Test
    public void testGetServices() throws Exception {
        Collection<String> services = serviceContext.getServices(String.class);
        assertNotNull(services);
        assertFalse(services.isEmpty());
        assertEquals("ServiceContextTest", services.iterator().next());
        Collection<Integer> intServices = serviceContext.getServices(Integer.class);
        assertNotNull(intServices);
        assertTrue(intServices.isEmpty());
    }

    @Test
    public void testGetServices_WithSupplier() throws Exception {
        Collection<String> services = serviceContext.getServices(String.class, () -> Collections.emptyList());
        assertNotNull(services);
        assertFalse(services.isEmpty());
        assertEquals("ServiceContextTest", services.iterator().next());
        List<Integer> list = new ArrayList<>();
        list.add(Integer.valueOf(4));
        Collection<Integer> intServices = serviceContext.getServices(Integer.class, () -> list);
        assertNotNull(intServices);
        assertFalse(intServices.isEmpty());
    }

    @Test
    public void testGetInstance() throws Exception {
        assertNotNull(ServiceContext.getInstance());
    }

    @Test
    public void testSet() throws Exception {
        ServiceContext prevContext = ServiceContext.set(serviceContext);
        assertNotNull(ServiceContext.getInstance());
        assertTrue(ServiceContext.getInstance() == serviceContext);
        if(prevContext!=null) {
            ServiceContext.set(prevContext);
            assertNotNull(ServiceContext.getInstance());
            assertTrue(ServiceContext.getInstance() == prevContext);
        }
    }
}