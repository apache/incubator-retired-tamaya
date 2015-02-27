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
package org.apache.tamaya.events.internal;

import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.events.Listener;
import org.apache.tamaya.events.spi.EventSupportSpi;
import org.apache.tamaya.spi.ServiceContext;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Default implementation of {@link org.apache.tamaya.events.internal.DefaultEventSupportSpi} just forwarding all
 * events synchronously to the listeners.
 */
public class DefaultEventSupportSpi implements EventSupportSpi {

    private static final Logger LOG = Logger.getLogger(DefaultEventSupportSpi.class.getName());

    private Map<Type, List<Listener>> listenerMap = new ConcurrentHashMap<>();


    /**
     * Constructor. Also loads all registered listeners.
     */
    public DefaultEventSupportSpi() {
        try {
            for (Listener<?> l : ServiceContext.getInstance().getServices(Listener.class)) {
                try{
                    addListener(l);
                }
                catch(Exception e){
                    LOG.log(Level.WARNING, e, () -> "Failed to load configured listener: " + l.getClass().getName());
                }
            }
        } catch (Exception e) {
            LOG.log(Level.WARNING, e, () -> "Failed to load configured listeners.");
        }
    }

    @Override
    public <T> void addListener(Listener<T> l) {
        Type type = TypeLiteral.getTypeParameter(l.getClass());
        List<Listener> listeners = listenerMap.computeIfAbsent(type,
                (k) -> Collections.synchronizedList(new ArrayList<>()));
        synchronized (listeners) {
            if (!listeners.contains(l)) {
                listeners.add(l);
            }
        }
    }

    @Override
    public <T> void removeListener(Listener<T> l) {
        Type type = TypeLiteral.getTypeParameter(l.getClass());
        List<Listener> listeners = listenerMap.get(type);
        if (listeners != null) {
            synchronized (listeners) {
                listeners.remove(l);
            }
        }
    }

    @Override
    public <T> void fireEvent(T event, Class<T> eventType) {
        List<Listener> listeners = listenerMap.get(eventType);
        if (listeners != null) {
            synchronized (listeners) {
                for (Listener l : listeners) {
                    l.onEvent(event);
                }
            }
        }
    }
}
