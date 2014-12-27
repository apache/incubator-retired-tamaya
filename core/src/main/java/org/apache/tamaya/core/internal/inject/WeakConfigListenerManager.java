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
package org.apache.tamaya.core.internal.inject;

import org.apache.tamaya.core.properties.PropertyChangeSet;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.StampedLock;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Simple listener container that only holds weak references on the listeners.
 */
public final class WeakConfigListenerManager{

    private static final WeakConfigListenerManager INSTANCE = new WeakConfigListenerManager();

    private static final Logger LOG = Logger.getLogger(WeakConfigListenerManager.class.getName());
    private StampedLock lock = new StampedLock();
    private Map<Object,Consumer<PropertyChangeSet>> listenerReferences = new WeakHashMap<>();

    /** Private singleton constructor. */
    private WeakConfigListenerManager(){}

    public static WeakConfigListenerManager of(){
        return INSTANCE;
    }

    /**
     * Registers the given consumer for the instance. If a consumer already exists for this instance the given
     * consumer is appended.
     * @param instance the instance, not null.
     * @param listener the consumer.
     */
    public void registerConsumer(Object instance, Consumer<PropertyChangeSet> listener){
        Lock writeLock = lock.asWriteLock();
        try {
            writeLock.lock();
            Consumer<PropertyChangeSet> l = listenerReferences.get(instance);
            if (l == null) {
                listenerReferences.put(instance, listener);
            } else {
                listenerReferences.put(instance, l.andThen(listener));
            }
        }
        finally{
            writeLock.unlock();
        }
    }

    /**
     * Unregisters all consumers for the given instance.
     * @param instance the instance, not null.
     */
    public void unregisterConsumer(Object instance) {
        Lock writeLock = lock.asWriteLock();
        try {
            writeLock.lock();
            listenerReferences.remove(instance);
        }
        finally{
            writeLock.unlock();
        }
    }

    /**
     * Publishes a change event to all consumers registered.
     * @param change the change event, not null.
     */
    public void publishChangeEvent(PropertyChangeSet change){
        Lock readLock = lock.asReadLock();
        try{
            readLock.lock();
            listenerReferences.values().parallelStream().forEach(l -> {
                try{
                    l.accept(change);
                }
                catch(Exception e){
                    LOG.log(Level.SEVERE, "ConfigChangeListener failed: " + l.getClass().getName(), e);
                }
            });
        }
        finally{
            readLock.unlock();
        }
    }


    @Override
    public String toString(){
        return "WeakConfigListenerManager{" +
                "listenerReferences=" + listenerReferences +
                '}';
    }


}
