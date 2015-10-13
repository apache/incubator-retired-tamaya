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

import org.apache.tamaya.ConfigurationProvider;
import org.apache.tamaya.events.ConfigEventManager;
import org.apache.tamaya.events.ConfigListener;
import org.apache.tamaya.events.FrozenConfiguration;
import org.apache.tamaya.events.delta.ConfigurationChange;
import org.apache.tamaya.events.delta.ConfigurationChangeBuilder;
import org.apache.tamaya.events.spi.ConfigObserverSpi;
import org.apache.tamaya.spi.ServiceContextManager;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Default implementation of {@link org.apache.tamaya.events.spi.ConfigObserverSpi} just forwarding all
 * events synchronously to the listeners.
 */
public class DefaultConfigObserverSpi implements ConfigObserverSpi {

    private static final long START_DELAY = 5000L;

    private static final Logger LOG = Logger.getLogger(DefaultConfigObserverSpi.class.getName());

    private Set<String> keys = new HashSet<>();

    private Timer timer = new Timer("ConfigurationObserver", true);

    private long checkPeriod = 2000L;

    private volatile FrozenConfiguration lastConfig;

    private ExecutorService publisher = Executors.newCachedThreadPool();

    private volatile boolean running;

    /**
     * Constructor. Also loads all registered listeners.
     */
    public DefaultConfigObserverSpi() {
        try {
            // Load and register ConfigListener from the current ServiceContext
            for (ConfigListener l : ServiceContextManager.getServiceContext().getServices(ConfigListener.class)) {
                ConfigEventManager.addListener(l);
            }
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Failed to load configured listeners.", e);
        }
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(running) {
                    checkConfigurationUpdate();
                }
            }
        }, START_DELAY, checkPeriod);
    }

    public void checkConfigurationUpdate() {
        LOG.finest("Checking configuration for changes...");
        FrozenConfiguration newConfig = FrozenConfiguration.of(ConfigurationProvider.getConfiguration());
        ConfigurationChange changes = null;
        if(lastConfig==null){
            changes = ConfigurationChangeBuilder.of(newConfig).putAll(newConfig.getProperties())
                    .build();
        }else{
            changes = ConfigurationChangeBuilder.of(lastConfig).addChanges(newConfig)
                    .build();
        }
        Set<ConfigListener> affected = new HashSet<>();
        for(PropertyChangeEvent evt: changes.getChanges()) {
            for (String key : keys) {
                if (evt.getPropertyName().matches(key)) {
                    ConfigEventManager.fireEvent(changes);
                    return;
                }
            }
        }
    }

    @Override
    public synchronized <T> void addObservedKeys(Collection<String> keys) {
        this.keys.addAll(keys);
    }

    @Override
    public synchronized <T> void removeObservedKeys(Collection<String> keys) {
        this.keys.removeAll(keys);
    }


    @Override
    public synchronized Set<String> getObservedKeys() {
        return Collections.unmodifiableSet(this.keys);
    }

    @Override
    public long getCheckPeriod() {
        return checkPeriod;
    }

    @Override
    public boolean isRunning(){
        return running;
    }

    @Override
    public void enableObservation(boolean enable){
        this.running = true;
    }

    /**
     * Tasks to inform observers on detected configuration changes.
     */
    private static final class PublishConfigChangeTask implements Runnable{

        private ConfigListener l;
        private ConfigurationChange changes;

        public PublishConfigChangeTask(ConfigListener l, ConfigurationChange changes) {
            this.l = Objects.requireNonNull(l);
            this.changes = Objects.requireNonNull(changes);
        }

        @Override
        public void run() {
            l.onConfigEvent(changes);
        }
    }
}
