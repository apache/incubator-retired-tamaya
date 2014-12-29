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

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * This class models the component that is managing the lifecycle current the
 * services used by the Configuration API.
 */
public interface ServiceContext {

    /**
     * @return ordinal of the ServiceContext. The one with the highest ordinal will be taken.
     */
    int ordinal();

    /**
     * Delegate method for {@link ServiceContext#getService(Class)}.
     *
     * @param serviceType the service type.
     * @return the service found, never {@code null}.
     * @see ServiceContext#getService(Class)
     */
    default <T> T getSingleton(Class<T> serviceType) {
        return getService(serviceType)
                .orElseThrow(() -> new IllegalStateException("Singleton missing: " + serviceType.getName()));
    }

    /**
     * Delegate method for {@link ServiceContext#getService(Class)}.
     *
     * @param serviceType the service type.
     * @return the service found, never {@code null}.
     * @see ServiceContext#getService(Class)
     */
    default <T> T getSingleton(Class<T> serviceType, Supplier<T> defaultSupplier) {
        return getService(serviceType)
                .orElse((defaultSupplier.get()));
    }

    /**
     * Access a singleton, given its type.
     *
     * @param serviceType
     *            the service type.
     * @return The instance to be used, never {@code null}
     */
    <T> Optional<T> getService(Class<T> serviceType);

    /**
     * Access a list current services, given its type. The bootstrap mechanism should
     * order the instance for precedence, hereby the most significant should be
     * first in order.
     *
     * @param serviceType
     *            the service type.
     * @param defaultList
     *            the lis returned, if no services could be found.
     * @return The instance to be used, never {@code null}
     */
    <T> List<? extends T> getServices(Class<T> serviceType, List<? extends T> defaultList);

    /**
     * Access a list current services, given its type. The bootstrap mechanism should
     * order the instance for precedence, hereby the most significant should be
     * first in order.
     *
     * @param serviceType
     *            the service type.
     * @return The instance to be used, never {@code null}
     */
    default <T> List<? extends T> getServices(Class<T> serviceType){
        return getServices(serviceType, Collections.emptyList());
    }

    /**
     * Get the current {@link ServiceContext}. If necessary the {@link ServiceContext} will be laziliy loaded.
     *
     * @return the {@link ServiceContext} to be used.
     */
    public static ServiceContext getInstance(){
        return ServiceContextManager.getServiceContext();
    }

    /**
     * Replace the current {@link ServiceContext} in use.
     *
     * @param serviceContext the new {@link ServiceContext}, not null.
     */
    public static ServiceContext set(ServiceContext serviceContext){
        return ServiceContextManager.set(Objects.requireNonNull(serviceContext));
    }
}
