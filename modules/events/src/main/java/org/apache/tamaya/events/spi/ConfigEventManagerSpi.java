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
package org.apache.tamaya.events.spi;

import org.apache.tamaya.events.ConfigEvent;
import org.apache.tamaya.events.ConfigEventListener;

import java.util.Collection;

/**
 * SPI interface to implement the {@link org.apache.tamaya.events.ConfigEventManager} singleton.
 * Implementations of this interface must be registered with the current {@link org.apache.tamaya.spi.ServiceContext},
 * by default this equals to registering it with {@link java.util.ServiceLoader}. Add {@link javax.annotation.Priority}
 * annotations for overriding (higher values override lower values).
 */
public interface ConfigEventManagerSpi {
    /**
     * Adds a listener for observing events. References of this
     * component to the listeners must be managed as weak references.
     * 
     * @param <T> the type of the events listened to.
     * @param l the listener not null.
     */
    <T> void addListener(ConfigEventListener l);

    /**
     * Adds a listener for observing events of a given type.
     *
     * @param <T> the type of the events listened to.
     * @param l the listener not null.
     * @param eventType the type of concrete configuration event this listeners should be informed about. All other
     *                  event types will never be delivered to this listener instance.
     */
    <T extends ConfigEvent> void addListener(ConfigEventListener l, Class<T> eventType);

    /**
     * Removes a listener for observing events.
     *
     * @param l the listener not null.
     */
    void removeListener(ConfigEventListener l);

    /**
     * Removes a listener for observing events of a certain type.
     *
     * @param <T> the type of the events listened to.
     * @param l the listener not null.
     * @param eventType the type of concrete configuration event this listeners should be informed about. All other
     *                  event types will never be delivered toe this listener instance.
     */
    <T extends ConfigEvent> void removeListener(ConfigEventListener l, Class<T> eventType);

    /**
     * Access all globally registered listeners.
     *
     * @return the listeners found, never null.
     */
    Collection<? extends ConfigEventListener> getListeners();

    /**
     * Access all listeners listening for a certain event type, including any global listeners.
     * @param eventType the type of concrete configuration event this listeners should be informed about. All other
     *                  event types will never be delivered toe this listener instance.
     * @return the listeners found, never null.
     */
    Collection<? extends ConfigEventListener> getListeners(Class<? extends ConfigEvent> eventType);

    /**
     * Publishes an event to all interested listeners, hereby executing all registered listeners sequentually and
     * synchronously.,
     *
     * @param event the event, not null.
     */
    void fireEvent(ConfigEvent<?> event);

    /**
     * Publishes an event to all interested listeners, hereby publishing the change events asynchrously and in
     * parallel (multithreaded).
     *
     * @param event the event, not null.
     */
    void fireEventAsynch(ConfigEvent<?> event);

    /**
     * Get the current check period to check for configuration changes.
     *
     * @return the check period in ms.
     */
    long getChangeMonitoringPeriod();

    void setChangeMonitoringPeriod(long millis);

    /**
     * Check if the observer is running currently.
     *
     * @return true, if the change monitoring service is currently running.
     */
    boolean isChangeMonitorActive();

    /**
     * Start/stop the change monitoring service, which will observe/reevaluate the current configuration regularly
     * and trigger ConfigurationChange events if something is changed. This is quite handy for publishing
     * configuration changes to whatever systems are interested in. Hereby the origin of a configuration change
     * can be on this machine, or also remotedly. For handling corresponding {@link ConfigEventListener} have
     * to be registered, e.g. listening on {@link org.apache.tamaya.events.ConfigurationChange} events.
     * 
     * @param enable whether to enable or disable the change monitoring.
     */
    void enableChangeMonitor(boolean enable);


}
