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

import org.apache.tamaya.Configuration;
import org.apache.tamaya.PropertySource;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This service class manages the configured instances that are currently attached to the configuration
 * system. References to instances are rest as WeakReference instances, and cleanup current internal structures
 * is performed implictly during event triggering for configuration changes.
 * Created by Anatole on 03.10.2014.
 */
public final class ConfiguredInstancesManager implements PropertyChangeListener{

    private static final ConfiguredInstancesManager INSTANCE = new ConfiguredInstancesManager();
    private Map<ConfiguredType,List<WeakReference<Object>>> configuredInstances = new ConcurrentHashMap<>();

    private ConfiguredInstancesManager(){
//        Configuration.addConfigChangeListener(this);
    }

    public static <T> void register(ConfiguredType configuredType, Object instance) {
        List<WeakReference<Object>> instances = INSTANCE.configuredInstances.get(configuredType);
        if(instances==null){
            synchronized(INSTANCE.configuredInstances){
                instances = INSTANCE.configuredInstances.get(configuredType);
                if(instances==null){
                    instances = Collections.synchronizedList(new ArrayList<>());
                    INSTANCE.configuredInstances.put(configuredType, instances);
                }
            }
        }
        synchronized(instances) {
            instances.add(new WeakReference<>(instance));
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        for(Map.Entry<ConfiguredType,List<WeakReference<Object>>> en: configuredInstances.entrySet()){
            PropertySource propertyProvider = (PropertySource)propertyChangeEvent.getSource();
            if((propertyProvider instanceof Configuration) && en.getKey().isConfiguredBy((Configuration)propertyProvider)){
                List<WeakReference<Object>> instances = en.getValue();
                synchronized (instances){
                    Iterator<WeakReference<Object>> iterator = instances.iterator();
                    while (iterator.hasNext()) {
                        WeakReference<Object> ref = iterator.next();
                        Object instance = ref.get();
                        if(instance==null){
                            iterator.remove();
                        }
                        else{
                            en.getKey().triggerConfigUpdate(propertyChangeEvent, instance);
                        }
                    }
                }
            }
        }
    }

}
