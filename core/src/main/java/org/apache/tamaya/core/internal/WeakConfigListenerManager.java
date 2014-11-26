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
package org.apache.tamaya.core.internal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tamaya.core.properties.Store;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Anatole on 29.03.14.
 */
public class WeakConfigListenerManager{

    private static final Logger LOG = LogManager.getLogger(WeakConfigListenerManager.class);
    private Map<String,Store<PropertyChangeListener>> changeListeners = new ConcurrentHashMap<>();


    private void addPropertyChangeListener(PropertyChangeListener l, String... configIds){
        for(String configId : configIds){
            Store<PropertyChangeListener> items = changeListeners.get(configId);
            if(items != null){
                synchronized(items){
                    items.add(l);
                }
            }
        }
    }

    private void removePropertyChangeListener(PropertyChangeListener l, String... configIds){
        for(String configId : configIds){
            Store<PropertyChangeListener> items = changeListeners.get(configId);
            if(items != null){
                items.remove(l);
            }
        }
    }

    private void publishPropertyChangeEventToGlobalListeners(PropertyChangeEvent evt){
        Store<PropertyChangeListener> items = changeListeners.get("_globalConfigChangeListeners");
        if(items != null){
            synchronized(items){
                for(PropertyChangeListener l : items){
                    try{
                        l.propertyChange(evt);
                    }
                    catch(Exception e){
                        LOG.error("Error thrown by PropertyChangeListener: " + l, e);
                    }
                }

            }
        }
    }


    public void publishPropertyChangeEvent(PropertyChangeEvent evt, String configId){
        Store<PropertyChangeListener> items = changeListeners.get(configId);
        if(items != null){
            synchronized(items){
                for(PropertyChangeListener l : items){
                    try{
                        l.propertyChange(evt);
                    }
                    catch(Exception e){
                        LOG.error("Error thrown by ConfigChangeListener: " + l, e);
                    }
                }

            }
        }
        publishPropertyChangeEventToGlobalListeners(evt);
    }


    @Override
    public String toString(){
        return "WeakConfigListenerManager{" +
                "changeListeners=" + changeListeners +
                '}';
    }

}
