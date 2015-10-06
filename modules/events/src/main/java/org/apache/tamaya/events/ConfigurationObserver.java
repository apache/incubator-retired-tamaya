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
package org.apache.tamaya.events;

import org.apache.tamaya.ConfigException;
import org.apache.tamaya.events.spi.ConfigObserverSpi;
import org.apache.tamaya.spi.ServiceContextManager;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by Anatole on 04.10.2015.
 */
public class ConfigurationObserver {

    /**
     * Private singleton constructor.
     */
    private ConfigurationObserver() {
    }

    /**
     * The backing SPI.
     */
    private static final ConfigObserverSpi SPI = ServiceContextManager.getServiceContext()
            .getService(ConfigObserverSpi.class);


    /**
     * Add a listener for observing change events on {@link org.apache.tamaya.Configuration}. References of this
     * component to the listeners must be managed as weak references.
     *
     * @param l the listener not null.
     */
    public static <T> void addListener(ConfigListener l) {
        if (SPI == null) {
            throw new ConfigException("No SPI registered for " +
                    ConfigurationObserver.class.getName());
        }
        SPI.addListener(l);
    }

    /**
     * Add a listener for observing change events on {@link org.apache.tamaya.spi.PropertySource}. References of this
     * component to the listeners must be managed as weak references.
     *
     * @param l the listener not null.
     */
    public static <T> void removeListener(ConfigListener l) {
        if (SPI == null) {
            throw new ConfigException("No SPI registered for " +
                    ConfigurationObserver.class.getName());
        }
        SPI.removeListener(l);
    }

    /**
     * Access all registered ConfigEventListeners listening to the given event key(s).
     *
     * @return a list with the listeners found, never null.
     */
    public static Collection<ConfigListener> getListeners(Collection<String> keys) {
        return SPI.getListeners(keys);
    }

    /**
     * Access all registered ConfigEventListeners listening to the given event key(s).
     *
     * @return a list with the listeners found, never null.
     */
    public static Collection<ConfigListener> getListeners(String... keys) {
        return SPI.getListeners(Arrays.asList(keys));
    }
}
