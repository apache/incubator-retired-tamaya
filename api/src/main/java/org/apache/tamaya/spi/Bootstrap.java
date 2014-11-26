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

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This singleton provides access to the services available in the current runtime environment and context. The
 * behaviour can be adapted, by calling {@link org.apache.tamaya.spi.Bootstrap#init(ServiceProvider)} before accessing any moneteray
 * services.
 */
public final class Bootstrap{
    /**
     * The ServiceProvider used.
     */
    private static volatile ServiceProvider serviceProviderDelegate;
    /**
     * The shared lock instance user.
     */
    private static final Object LOCK = new Object();

    /**
     * Private singletons constructor.
     */
    private Bootstrap(){
    }

    /**
     * Load the {@link ServiceProvider} to be used.
     *
     * @return {@link ServiceProvider} to be used for loading the services.
     */
    private static ServiceProvider loadDefaultServiceProvider(){
        try{

            for(ServiceProvider sp : ServiceLoader.load(ServiceProvider.class)){
                return sp;
            }
        }
        catch(Exception e){
            Logger.getLogger(Bootstrap.class.getName()).log(Level.INFO, "No ServiceProvider loaded, using default.");
        }
        return new DefaultServiceProvider();
    }

    /**
     * Replace the current {@link ServiceProvider} in use.
     *
     * @param serviceProvider the new {@link ServiceProvider}
     */
    public static void init(ServiceProvider serviceProvider){
        Objects.requireNonNull(serviceProvider);
        synchronized(LOCK){
            if (Bootstrap.serviceProviderDelegate==null) {
                Bootstrap.serviceProviderDelegate = serviceProvider;
                Logger.getLogger(Bootstrap.class.getName())
                        .log(Level.INFO, "Money Bootstrap: new ServiceProvider set: " + serviceProvider.getClass().getName());
            } else {
                throw new IllegalStateException("Services are already initialized.");
            }
        }
    }

    /**
     * Ge {@link ServiceProvider}. If necessary the {@link ServiceProvider} will be laziliy loaded.
     *
     * @return the {@link ServiceProvider} used.
     */
    static ServiceProvider getServiceProvider(){
        if (serviceProviderDelegate==null) {
            synchronized(LOCK){
                if (serviceProviderDelegate==null) {
                    serviceProviderDelegate = loadDefaultServiceProvider();
                }
            }
        }
        return serviceProviderDelegate;
    }

    /**
     * Delegate method for {@link ServiceProvider#getServices(Class)}.
     *
     * @param serviceType the service type.
     * @return the services found.
     * @see ServiceProvider#getServices(Class)
     */
    public static <T> Collection<T> getServices(Class<T> serviceType){
        return getServiceProvider().getServices(serviceType);
    }

    /**
     * Delegate method for {@link ServiceProvider#getServices(Class)}.
     *
     * @param serviceType     the service type.
     * @param defaultServices the default service list.
     * @return the services found.
     * @see ServiceProvider#getServices(Class, java.util.List)
     */
    public static <T> List<T> getServices(Class<T> serviceType, List<T> defaultServices){
        return getServiceProvider().getServices(serviceType, defaultServices);
    }

    /**
     * Delegate method for {@link ServiceProvider#getServices(Class)}.
     *
     * @param serviceType the service type.
     * @return the service found, never {@code null}.
     * @see ServiceProvider#getServices(Class)
     */
    public static <T> T getService(Class<T> serviceType){
        List<T> services = getServiceProvider().getServices(serviceType);
        if(services.isEmpty()){
            throw new IllegalStateException("No such service found: " + serviceType);
        }
        return services.get(0);
    }

    /**
     * Delegate method for {@link ServiceProvider#getServices(Class)}.
     *
     * @param serviceType    the service type.
     * @param defaultService returned if no service was found.
     * @return the service found, only {@code null}, if no service was found and
     * {@code defaultService==null}.
     * @see ServiceProvider#getServices(Class, java.util.List)
     */
    public static <T> T getService(Class<T> serviceType, T defaultService){
        List<T> services = getServiceProvider().getServices(serviceType);
        if(services.isEmpty()){
            return defaultService;
        }
        return services.get(0);
    }

}