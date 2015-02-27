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
import org.apache.tamaya.events.spi.EventSupportSpi;
import org.apache.tamaya.spi.ServiceContext;

/**
 * Singleton accessor for accessing the event support component that distributes change events of
 * {@link org.apache.tamaya.spi.PropertySource} and {@link org.apache.tamaya.Configuration}.
 */
public class EventSupport {
    /**
     * The backing spi.
     */
    private static final EventSupportSpi spi = ServiceContext.getInstance()
            .getService(EventSupportSpi.class)
            .orElseThrow(() -> new ConfigException("No SPI registered for " +
                    EventSupport.class.getName()));

    /**
     * Private singleton constructor.
     */
    private EventSupport() {
    }

    /**
     * Add a listener for observing change events on {@link org.apache.tamaya.Configuration}. References of this
     * component to the listeners must be managed as weak references.
     *
     * @param l the listener not null.
     */
    public static <T> void addListener(Listener<T> l) {
        spi.addListener(l);
    }

    /**
     * Add a listener for observing change events on {@link org.apache.tamaya.spi.PropertySource}. References of this
     * component to the listeners must be managed as weak references.
     *
     * @param l the listener not null.
     */
    public static <T> void removeListener(Listener<T> l) {
        spi.removeListener(l);
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
        spi.fireEvent(event, eventType);
    }

}
