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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tamaya.*;
import org.apache.tamaya.core.properties.AbstractPropertyProvider;
import org.apache.tamaya.core.properties.Store;
import org.apache.tamaya.core.spi.AdapterProviderSpi;
import org.apache.tamaya.spi.Bootstrap;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;

public abstract class AbstractConfiguration extends AbstractPropertyProvider implements Configuration{

    private static final Logger LOG = LogManager.getLogger(AbstractConfiguration.class);

    private static final long serialVersionUID = 503764580971917964L;

    /**
     * The registered change listeners, or null.
     */
    private volatile Store<PropertyChangeListener> changeListeners;

    private final Object LOCK = new Object();

    private String version = UUID.randomUUID().toString();

    protected AbstractConfiguration(MetaInfo metaInfo){
        super(metaInfo);
    }


    @Override
    public <T> Optional<T> get(String key, Class<T> type){
        AdapterProviderSpi as = Bootstrap.getService(AdapterProviderSpi.class);
        PropertyAdapter<T> adapter = as.getAdapter(type);
        if(adapter == null){
            throw new ConfigException(
                    "Can not adapt config property '" + key + "' to " + type.getName() + ": no such " +
                            "adapter.");
        }
        return getAdapted(key, adapter);
    }

    @Override
    public String getVersion(){
        return version;
    }

    /**
     * This method reloads the content of this PropertyMap by reloading the contents delegate.
     */
    protected void reload(){ }

    /**
     * This method reloads the content of this PropertyMap by reloading the contents delegate.
     */
    @Override
    public void load(){
        Configuration oldState = null;
        Configuration newState = null;
        synchronized(LOCK) {
            oldState = FreezedConfiguration.of(this);
            reload();
            newState = FreezedConfiguration.of(this);
            if(oldState.hasSameProperties(newState)){
                return;
            }
            this.version = UUID.randomUUID().toString();
        }
        publishPropertyChangeEvents(ConfigChangeSetBuilder.of(oldState).addChanges(newState).build().getEvents());
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener l){
        if(this.changeListeners == null){
            synchronized(LOCK){
                if(this.changeListeners == null){
                    this.changeListeners = new Store<>();
                }
            }
        }
        this.changeListeners.addWeak(l);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener l){
        if(changeListeners == null){
            return;
        }
        this.changeListeners.remove(l);
    }

    protected void publishPropertyChangeEvents(Collection<PropertyChangeEvent> events){
        if(changeListeners == null){
            return;
        }
        for(PropertyChangeListener l : changeListeners){
            for(PropertyChangeEvent evt: events) {
                try {
                    l.propertyChange(evt);
                } catch (Exception e) {
                    LOG.error("Error thrown by ConfigChangeListener: " + l, e);
                }
            }
        }
    }
}
