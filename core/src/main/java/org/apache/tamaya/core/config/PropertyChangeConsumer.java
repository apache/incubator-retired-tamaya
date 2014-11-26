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
package org.apache.tamaya.core.config;

import java.beans.PropertyChangeEvent;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Simple interface to be implemented by components that can be used for handling changes done
 * on a {@link org.apache.tamaya.MutablePropertyProvider} instance.
 */
public interface PropertyChangeConsumer extends Consumer<Collection<PropertyChangeEvent>> {

    /**
     * Checks if the given change is accepted by this consumer.
     * @param evt the change event
     * @return true, if the event is accepted.
     * @throws java.beans.PropertyVetoException if the change is not acceptable.
     */
    void validatePropertyChange(PropertyChangeEvent evt);

    /**
     * Basically passes all events to the acceptPropertyChange method.
     *
     * @param events the input events
     */
    default void accept(Collection<PropertyChangeEvent> events){
        Objects.requireNonNull(events);
        events.forEach(this::validatePropertyChange);
    }

}
