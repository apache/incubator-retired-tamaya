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
import org.apache.tamaya.events.ConfigurationChange;
import org.apache.tamaya.events.ConfigurationChangeBuilder;
import org.apache.tamaya.events.FrozenConfiguration;

import java.util.*;
import java.util.logging.Logger;

/**
 * Timer task that regularly checks the configuration for changes.
 */
public class DefaultConfigChangeObserver {

    private static final long START_DELAY = 5000L;

    private static final Logger LOG = Logger.getLogger(DefaultConfigChangeObserver.class.getName());

    private Timer timer = new Timer("DefaultConfigChangeObserver", true);

    private long checkPeriod = 2000L;

    private volatile FrozenConfiguration lastConfig;

    private volatile boolean running;

    /**
     * Constructor. Also loads all registered listeners.
     */
    public DefaultConfigChangeObserver() {
        LOG.info("Registering config change observer, rechecking config changes every " + checkPeriod + " ms.");
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
        if(!changes.isEmpty()) {
            LOG.info("Identified configuration changes, publishing change event...");
            ConfigEventManager.fireEvent(changes);
        }
    }

    public long getCheckPeriod() {
        return checkPeriod;
    }

    public boolean isMonitoring(){
        return running;
    }

    public void enableMonitoring(boolean enable){
        this.running = true;
    }

    /**
     * Sets the new check period, cancels the currently running timer and schedules a new task with the new checkperiod
     * and a startup delay of 500ms.
     * @param checkPeriod
     */
    public void setCheckPeriod(long checkPeriod) {
        LOG.finest("Resetting check period to " + checkPeriod + " ms, reregistering timer.");
        this.checkPeriod = checkPeriod;
        timer.cancel();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(running) {
                    checkConfigurationUpdate();
                }
            }
        }, 500L, checkPeriod);
    }
}
