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

import org.apache.tamaya.events.Listener;

/**
 * SPI interface to implement the {@link org.apache.tamaya.events.EventSupport} singleton.
 * Implementations of this interface must be registered with the current {@link org.apache.tamaya.spi.ServiceContext},
 * by default this equals to registering it with {@link java.util.ServiceLoader}. Add {@link javax.annotation.Priority}
 * annotations for overriding (higher values overriden lower values).
 */
public interface EventSupportSpi {
    /**
     * Add a listener for observing events. References of this
     * component to the listeners must be managed as weak references.
     *
     * @param l the listener not null.
     */
    <T> void addListener(Listener<T> l);


    /**
     * Removes a listener for observing events.
     *
     * @param l the listener not null.
     */
    <T> void removeListener(Listener<T> l);

    /**
     * Publishes an event to all interested listeners.
     *
     * @param event     the event, not null.
     * @param eventType the event type.
     */
    <T> void fireEvent(T event, Class<T> eventType);

}
