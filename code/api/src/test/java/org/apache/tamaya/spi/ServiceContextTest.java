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

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.function.Supplier;

import org.junit.Test;

public class ServiceContextTest {

    private final ServiceContext serviceContext = new ServiceContext(){

        @Override
        public ClassLoader getClassLoader() {
            return null;
        }

        @Override
        public void init(ClassLoader classLoader) {

        }

        @Override
        public int ordinal() {
            return 1;
        }

        @Override
        public <T> T getService(Class<T> serviceType, Supplier<T> supplier) {
            if(String.class.equals(serviceType)){
                return serviceType.cast("ServiceContextTest");
            }
            return null;
        }

        @Override
        public <T> T create(Class<T> serviceType, Supplier<T> supplier) {
            return getService(serviceType);
        }

        @Override
        public <T> List<T> getServices(Class<T> serviceType, Supplier<List<T>> supplier) {
            if(String.class.equals(serviceType)){
                List<String> list = new ArrayList<>();
                list.add("ServiceContextTest");
                return List.class.cast(list);
            }
            return Collections.emptyList();
        }

        @Override
        public <T> T register(Class<T> type, T instance, boolean force) {
            return instance;
        }

        @Override
        public <T> List<T> register(Class<T> type, List<T> instances, boolean force) {
            return instances;
        }

        @Override
        public void reset() {

        }

    };

    @Test
    public void testOrdinal() throws Exception {
        assertThat(serviceContext.ordinal()).isEqualTo(1);
    }

    @Test
    public void testgetService() throws Exception {
        assertThat(serviceContext.getService(String.class)).isEqualTo("ServiceContextTest");
        assertThat(serviceContext.getService(Integer.class)).isNull();
    }

    @Test
    public void testGetService() throws Exception {
        String service = serviceContext.getService(String.class);
        assertThat(service).isNotNull().isEqualTo("ServiceContextTest");
        Integer intService = serviceContext.getService(Integer.class);
        assertThat(intService).isNull();
    }

    @Test
    public void testGetServices() throws Exception {
        Collection<String> services = serviceContext.getServices(String.class);
        assertThat(services).isNotNull().isNotEmpty();
        assertThat(services.iterator().next()).isEqualTo("ServiceContextTest");
        List<Integer> intServices = serviceContext.getServices(Integer.class);
        assertThat(intServices).isNotNull().isEmpty();
    }

    @Test
    public void testGetInstance() throws Exception {
        assertThat(ServiceContextManager.getServiceContext(getClass().getClassLoader())).isNotNull();
    }

}
