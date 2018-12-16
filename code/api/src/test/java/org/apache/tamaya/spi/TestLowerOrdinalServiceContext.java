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

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.function.Supplier;

/**
 * This class implements the (default)
 * {@link org.apache.tamaya.spi.ServiceContext} interface and hereby uses the
 * JDK {@link java.util.ServiceLoader} to load the services required.
 */
public final class TestLowerOrdinalServiceContext implements ServiceContext {

    private final RuntimeException ex = new RuntimeException("Lower ordinal Service Context was used.");

    @Override
    public ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    @Override
    public void init(ClassLoader classLoader) {
    }

    @Override
    public int ordinal() {
        return 1;
    }

    @Override
    public <T> T getService(Class<T> serviceType) {
        throw ex;
    }

    @Override
    public <T> T getService(Class<T> serviceType, Supplier<T> supplier) {
        return null;
    }

    @Override
    public <T> T create(Class<T> serviceType) {
        throw ex;
    }

    @Override
    public <T> T create(Class<T> serviceType, Supplier<T> supplier) {
        throw ex;
    }

    @Override
    public <T> List<T> getServices(Class<T> serviceType) {
        throw ex;
    }

    @Override
    public <T> List<T> getServices(Class<T> serviceType, Supplier<List<T>> supplier) {
        throw ex;
    }

    @Override
    public Enumeration<URL> getResources(String resource) throws IOException {
        throw ex;
    }

    @Override
    public URL getResource(String resource) {
        throw ex;
    }

    @Override
    public <T> T register(Class<T> type, T instance, boolean force) {
        throw ex;
    }

    @Override
    public <T> List<T> register(Class<T> type, List<T> instancea, boolean force) {
        throw ex;
    }

    @Override
    public void reset() {

    }

}
