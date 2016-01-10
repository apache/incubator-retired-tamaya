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

import org.apache.tamaya.events.ConfigEvent;
import org.apache.tamaya.events.ConfigEventListener;
import org.apache.tamaya.events.spi.ConfigEventManagerSpi;
import org.apache.tamaya.spi.ServiceContextManager;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Default implementation of {@link DefaultConfigEventManagerSpi} just forwarding all
 * events synchronously to the listeners.
 */
public class DefaultConfigEventManagerSpi implements ConfigEventManagerSpi {

    private static final Logger LOG = Logger.getLogger(DefaultConfigEventManagerSpi.class.getName());

    private final Map<Class,List<ConfigEventListener>> listeners = new ConcurrentHashMap<>();

    private final ExecutorService publisher = Executors.newCachedThreadPool();

    private final DefaultConfigChangeObserver changeObserver = new DefaultConfigChangeObserver();

    /**
     * Constructor. Also loads all registered listeners.
     */
    public DefaultConfigEventManagerSpi() {
        try {
            for (ConfigEventListener l : ServiceContextManager.getServiceContext().getServices(ConfigEventListener.class)) {
                try {
                    addListener(l);
                } catch (Exception e) {
                    LOG.log(Level.WARNING, "Failed to load configured listener: " + l.getClass().getName(), e);
                }
            }
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Failed to load configured listeners.", e);
        }
    }

    @Override
    public void addListener(ConfigEventListener l){
        addListener(l, ConfigEvent.class);
    }

    @Override
    public <T extends ConfigEvent> void addListener(ConfigEventListener l, Class<T> eventType){
        List<ConfigEventListener> ls = listeners.get(eventType);
        if(ls==null){
            ls = Collections.synchronizedList(new ArrayList<ConfigEventListener>());
            listeners.put(eventType, ls);
        }
        synchronized (ls){
            if(!ls.contains(l)){
                ls.add(l);
            }
        }
    }

    @Override
    public void removeListener(ConfigEventListener l){
        removeListener(l, ConfigEvent.class);
    }

    @Override
    public <T extends ConfigEvent> void removeListener(ConfigEventListener l, Class<T> eventType) {
        List<ConfigEventListener> targets = this.listeners.get(eventType);
        if(targets!=null) {
            // forward to explicit listeners
            synchronized (targets) {
                targets.remove(l);
            }
        }
    }

    @Override
    public Collection<? extends ConfigEventListener> getListeners(Class<? extends ConfigEvent> eventType) {
        List<ConfigEventListener> targets = this.listeners.get(eventType);
        if(targets!=null){
            synchronized(targets){
                return new ArrayList<>(targets);
            }
        }
        return Collections.emptyList();
    }

    @Override
    public Collection<? extends ConfigEventListener> getListeners() {
        Set<ConfigEventListener> targets = new HashSet<>();
        for(List<ConfigEventListener> l:this.listeners.values()){
            targets.addAll(l);
        }
        return targets;
    }

    @Override
    public void fireEvent(ConfigEvent<?> event) {
        List<ConfigEventListener> targets = this.listeners.get(event.getClass());
        if(targets!=null) {
            // forward to explicit listeners
            synchronized (targets) {
                for (ConfigEventListener l : targets) {
                    l.onConfigEvent(event);
                }
            }
        }
        // forward to global listeners
        targets = this.listeners.get(ConfigEvent.class);
        if(targets!=null) {
            synchronized (targets) {
                for (ConfigEventListener l : targets) {
                    l.onConfigEvent(event);
                }
            }
        }
    }

    @Override
    public void fireEventAsynch(ConfigEvent<?> event) {
        List<ConfigEventListener> targets = this.listeners.get(event.getClass());
        if(targets!=null) {
            // forward to explicit listeners
            synchronized (targets) {
                for (ConfigEventListener l : targets) {
                    publisher.execute(new PublishConfigChangeTask(l, event));
                }
            }
        }
        // forward to global listeners
        targets = this.listeners.get(ConfigEvent.class);
        if(targets!=null) {
            synchronized (targets) {
                for (ConfigEventListener l : targets) {
                    publisher.execute(new PublishConfigChangeTask(l, event));
                }
            }
        }
    }

    @Override
    public long getChangeMonitoringPeriod() {
        return changeObserver.getCheckPeriod();
    }

    @Override
    public void setChangeMonitoringPeriod(long millis){
        changeObserver.setCheckPeriod(millis);
    }

    @Override
    public boolean isChangeMonitorActive() {
        return changeObserver.isMonitoring();
    }

    @Override
    public void enableChangeMonitor(boolean enable) {
        changeObserver.enableMonitoring(enable);
    }


    /**
     * Tasks to inform observers on detected configuration changes.
     */
    private static final class PublishConfigChangeTask implements Runnable{

        private final ConfigEventListener l;
        private final ConfigEvent<?> changes;

        public PublishConfigChangeTask(ConfigEventListener l, ConfigEvent<?> changes) {
            this.l = Objects.requireNonNull(l);
            this.changes = Objects.requireNonNull(changes);
        }

        @Override
        public void run() {
            l.onConfigEvent(changes);
        }
    }
}
