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
import org.apache.tamaya.events.spi.ConfigEventManagerSpi;
import org.apache.tamaya.spi.ServiceContextManager;

import java.util.Collection;

/**
 * Singleton accessor for accessing the event support component that distributes change events of
 * {@link org.apache.tamaya.spi.PropertySource} and {@link org.apache.tamaya.Configuration}.
 */
public final class ConfigEventManager {
    /**
     * The backing SPI.
     */
    private static final ConfigEventManagerSpi SPI = ServiceContextManager.getServiceContext()
            .getService(ConfigEventManagerSpi.class);

    /**
     * Private singleton constructor.
     */
    private ConfigEventManager() {
    }

    /**
     * Add a listener for observing change events on {@link org.apache.tamaya.Configuration}. References of this
     * component to the listeners must be managed as weak references.
     *
     * @param l the listener not null.
     */
    public static <T> void addListener(ConfigEventListener<T> l) {
        if (SPI == null) {
            throw new ConfigException("No SPI registered for " +
                    ConfigEventManager.class.getName());
        }
        SPI.addListener(l);
    }

    /**
     * Add a listener for observing change events on {@link org.apache.tamaya.spi.PropertySource}. References of this
     * component to the listeners must be managed as weak references.
     *
     * @param l the listener not null.
     */
    public static <T> void removeListener(ConfigEventListener<T> l) {
        if (SPI == null) {
            throw new ConfigException("No SPI registered for " +
                    ConfigEventManager.class.getName());
        }
        SPI.removeListener(l);
    }

    /**
     * Access all registered ConfigEventListeners listening to the given event type.
     * @param type the event type
     * @param <T> type param
     * @return a list with the listeners found, never null.
     */
    public static <T>
        Collection<? extends ConfigEventListener<T>> getListeneters(Class<T> type) {
        return SPI.getListeners(type);
    }


    /**
     * Publishes sn event to all interested listeners.
     *
     * @param event the event, not null.
     */
    public static void fireEvent(Object event) {
        fireEvent(event, (Class)event.getClass());
    }

    /**
     * Publishes a {@link org.apache.tamaya.events.delta.ConfigurationChange} to all interested listeners.
     *
     * @param event the event, not null.
     *              @param eventType the event type, the vent may be a subclass.
     */
    public static <T> void fireEvent(T event, Class<T> eventType) {
        if (SPI == null) {
            throw new ConfigException("No SPI registered for " +
                    ConfigEventManager.class.getName());
        }
        SPI.fireEvent(event, eventType);
    }

}
