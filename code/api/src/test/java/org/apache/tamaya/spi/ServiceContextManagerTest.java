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

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Additional tests for {@link ServiceContextManager}, created by atsticks on 20.08.16.
 */
public class ServiceContextManagerTest {

    @Test
    public void setGetServiceContext() throws Exception {
        ServiceContext prev = ServiceContextManager.getServiceContext(getClass().getClassLoader());
        try {
            MyServiceContext mine = new MyServiceContext();
            ServiceContextManager.set(mine);
            assertThat(ServiceContextManager.getServiceContext(getClass().getClassLoader()) == mine).isTrue();
            ServiceContextManager.set(mine);
            assertThat(ServiceContextManager.getServiceContext(getClass().getClassLoader()) == mine).isTrue();
        } finally {
            ServiceContextManager.set(prev);
            assertThat(ServiceContextManager.getServiceContext(getClass().getClassLoader()) == prev).isTrue();
        }

    }

    @Test(expected = NullPointerException.class)
    public void setRequiresNonNullParameter() {
        ServiceContextManager.set(null);
    }

    private static final class MyServiceContext implements ServiceContext{

        @Override
        public ClassLoader getClassLoader() {
            return getClass().getClassLoader();
        }

        @Override
        public void init(ClassLoader classLoader) {

        }

        @Override
        public int ordinal() {
            return 0;
        }

        @Override
        public <T> T getService(Class<T> serviceType, Supplier<T> supplier) {
            return null;
        }

        @Override
        public <T> T create(Class<T> serviceType, Supplier<T> supplier) {
            return null;
        }

        @Override
        public <T> List<T> getServices(Class<T> serviceType, Supplier<List<T>> supplier) {
            return Collections.emptyList();
        }

        @Override
        public Collection<URL> getResources(String resource) {
            return Collections.emptySet();
        }

        @Override
        public URL getResource(String resource) {
            return null;
        }

        @Override
        public <T> T register(Class<T> type, T instance, boolean force) {
            return instance;
        }

        @Override
        public <T> List<T> register(Class<T> type, List<T> instancea, boolean force) {
            return instancea;
        }

        @Override
        public void reset() {

        }
    }

}
