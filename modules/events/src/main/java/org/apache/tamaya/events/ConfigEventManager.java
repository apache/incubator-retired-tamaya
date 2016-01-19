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
     * Adds a Config listener that listens to all kind of {@link ConfigEvent}.
     * @param l the listener not null.
     */
    public static void addListener(ConfigEventListener l) {
        if (SPI == null) {
            throw new ConfigException("No SPI registered for " +
                    ConfigEventManager.class.getName());
        }
        SPI.addListener(l);
    }

    /**
     * Adds a Config listener that listens to all kind of {@link ConfigEvent}.
     * @param <T> the type of the event.
     * @param l the listener not null.
     * @param eventType the event type to which this listener listens to.
     */
    public static <T extends ConfigEvent> void addListener(ConfigEventListener l, Class<T> eventType) {
        if (SPI == null) {
            throw new ConfigException("No SPI registered for " +
                    ConfigEventManager.class.getName());
        }
        SPI.addListener(l);
    }

    /**
     * Removes a listener registered globally.
     *
     * @param l the listener not null.
     */
    public static void removeListener(ConfigEventListener l) {
        if (SPI == null) {
            throw new ConfigException("No SPI registered for " +
                    ConfigEventManager.class.getName());
        }
        SPI.removeListener(l);
    }

    /**
     * Removes a listener registered for the given event type.
     *
     * @param <T> the type of the event.
     * @param l the listener, not null.
     * @param eventType the event type to which this listener listens to.
     */
    public static <T extends ConfigEvent> void removeListener(ConfigEventListener l, Class<T> eventType) {
        if (SPI == null) {
            throw new ConfigException("No SPI registered for " +
                    ConfigEventManager.class.getName());
        }
        SPI.removeListener(l);
    }

    /**
     * Access all registered ConfigEventListeners listening to a given event type.
     * @param type the event type
     * @param <T> type param
     * @return a list with the listeners found, never null.
     */
    public static <T extends ConfigEvent>
        Collection<? extends ConfigEventListener> getListeners(Class<T> type) {
        return SPI.getListeners(type);
    }

    /**
     * Access all registered ConfigEventListeners listening to a all kind of event types globally.
     * 
     * @param <T> the type of the event.
     * @return a list with the listeners found, never null.
     */
    public static <T extends ConfigEvent>
    Collection<? extends ConfigEventListener> getListeners() {
        return SPI.getListeners();
    }

    /**
     * Publishes a {@link ConfigurationChange} synchronously to all interested listeners.
     * 
     * @param <T> the type of the event.
     * @param event the event, not null.
     */
    public static <T> void fireEvent(ConfigEvent<?> event) {
        SPI.fireEvent(event);
    }

    /**
     * Publishes a {@link ConfigurationChange} asynchronously/multithreaded to all interested listeners.
     *
     * @param <T> the type of the event.
     * @param event the event, not null.
     */
    public static <T> void fireEventAsynch(ConfigEvent<?> event) {
        SPI.fireEventAsynch(event);
    }

    /**
     * Start/Stop the change monitoring service, which will observe/reevaluate the current configuration regularly
     * and trigger ConfigurationChange events if something changed. This is quite handy for publishing
     * configuration changes to whatever systems are interested in. Hereby the origin of a configuration change
     * can be on this machine, or also remotely. For handling corresponding {@link ConfigEventListener} have
     * to be registered, e.g. listening on {@link org.apache.tamaya.events.ConfigurationChange} events.
     * 
     * @param enable whether to enable or disable the change monitoring.
     * 
     * @see #isChangeMonitoring()
     * @see #getChangeMonitoringPeriod()
     */
    public static void enableChangeMonitoring(boolean enable) {
        SPI.enableChangeMonitor(enable);
    }

    /**
     * Check if the observer is running currently.
     *
     * @return true, if the change monitoring service is currently running.
     * @see #enableChangeMonitoring(boolean)
     */
    public static boolean isChangeMonitoring() {
        return SPI.isChangeMonitorActive();
    }

    /**
     * Get the current check period to check for configuration changes.
     *
     * @return the check period in ms.
     */
    public long getChangeMonitoringPeriod(){
        return SPI.getChangeMonitoringPeriod();
    }

    /**
     * Sets the current monitoring period and restarts the monitor. You still have to enable the monitor if
     * it is currently not enabled.
     * @param millis the monitoring period in ms.
     * @see #enableChangeMonitoring(boolean)
     * @see #isChangeMonitoring()
     */
    public void setChangeMonitoringPeriod(long millis){
        SPI.setChangeMonitoringPeriod(millis);
    }

}
