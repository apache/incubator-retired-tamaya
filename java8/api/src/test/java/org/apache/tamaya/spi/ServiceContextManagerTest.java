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
package org.apache.tamaya.spi;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ServiceContextManagerTest {

    private static URLClassLoader classLoader;
    private static Field delegateField;

    @BeforeClass
    public static void init() throws Exception {

        // setup the environment for our ugly hacks

        // replace classloader with our own
        classLoader = (URLClassLoader) Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(new UglyHackClassLoader(classLoader));

        // clear the caching field
        delegateField = ServiceContextManager.class.getDeclaredField("serviceContextProviderDelegate");
        delegateField.setAccessible(true);

        delegateField.set(null, null);
    }

    @AfterClass
    public static void clean() throws Exception {

        // clean our hacks

        delegateField.set(null, null);
        Thread.currentThread().setContextClassLoader(classLoader);
    }

    @Test
    public void testGetServiceContext() {

        ServiceContext context = ServiceContextManager.getServiceContext();
        Assert.assertEquals(1, context.ordinal());

    }

    @Test
    public void testSetServiceContext(){
        ServiceContext ctx = new ServiceContext() {
            @Override
            public <T> Optional<T> getService(Class<T> serviceType) {
                return Optional.empty();
            }

            @Override
            public <T> List<T> getServices(Class<T> serviceType) {
                return Collections.emptyList();
            }
        };
        ServiceContext prevContext = ServiceContextManager.set(ctx);
        if(prevContext!=null) {
            ServiceContextManager.set(prevContext);
            assertTrue(ServiceContextManager.getServiceContext() == prevContext);
        }
    }


    // has to be public because ServiceLoader won't find it otherwise
    public static class ServiceContextWithOrdinal implements ServiceContext {

        @Override
        public int ordinal() {
            return 100;
        }

        @Override
        public <T> Optional<T> getService(Class<T> serviceType) {
            return Optional.empty();
        }

        @Override
        public <T> List<T> getServices(Class<T> serviceType) {
            return null;
        }
    }

    // to override the getResources method to use our own 'ServiceLoader'-file we have to this ugly hack
    private static class UglyHackClassLoader extends URLClassLoader {

        private UglyHackClassLoader(URLClassLoader urlClassLoader) {
            super(urlClassLoader.getURLs());
        }


        @Override
        public Enumeration<URL> getResources(String name) throws IOException {
            if ("META-INF/services/org.apache.tamaya.spi.ServiceContext".equals(name)) {
                return super.getResources("ServiceContextWithOrdinal");
            }

            return super.getResources(name);
        }
    }
}
