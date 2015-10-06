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

import org.apache.tamaya.events.ConfigListener;

import java.util.Collection;

/**
 * SPI interface to implement the {@link org.apache.tamaya.events.ConfigurationObserver} singleton.
 * Implementations of this interface must be registered with the current {@link org.apache.tamaya.spi.ServiceContext},
 * by default this equals to registering it with {@link java.util.ServiceLoader}. Add {@link javax.annotation.Priority}
 * annotations for overriding (higher values overriden lower values).
 */
public interface ConfigObserverSpi {
    /**
     * Add a listener for observing events. References of this
     * component to the listeners must be managed as weak references.
     *
     * @param l the listener not null.
     */
    <T> void addListener(ConfigListener l);


    /**
     * Removes a listener for observing events.
     *
     * @param l the listener not null.
     */
    <T> void removeListener(ConfigListener l);

    /**
     * Access all registered ConfigEventListeners listening to the given event type.
     *
     * @return a list with the listeners found, never null.
     */
    Collection<ConfigListener> getListeners(Collection<String> keys);

    /**
     * Get the current check period to check for configuration changes.
     *
     * @return the check period in ms.
     */
    long getCheckPeriod();

    /**
     * Check if the observer is running currently.
     *
     * @return true, if the observer is running.
     */
    boolean isRunning();

    /**
     * Start/Stop the observer container.
     */
    void enableObservation(boolean enable);

}
