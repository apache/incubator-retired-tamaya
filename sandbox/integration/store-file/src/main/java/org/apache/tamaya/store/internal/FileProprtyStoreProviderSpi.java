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
package org.apache.tamaya.store.internal;

import com.hazelcast.config.Config;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.apache.tamaya.spi.ServiceContextManager;
import org.apache.tamaya.store.PropertyStore;
import org.apache.tamaya.store.spi.PropertyStoreProviderSpi;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * SPI implmentation for a providing Hazelcast based PropertyStores.
 */
public class FileProprtyStoreProviderSpi implements PropertyStoreProviderSpi {
    private static final String CONFIG_CLASS_SYS_PROP = "tamaya.store.file.configClass";
    private static final String CONFIG_GROUP_SYS_PROP = "tamaya.store.file.groupName";

    private static final Logger LOG = Logger.getLogger(HazelcastProprtyStoreProviderSpi.class.getName());

    private File file;
    private Map<String,HazelcastProprtyStore> stores = new ConcurrentHashMap<>();

    public HazelcastProprtyStoreProviderSpi() {
        String customConfig = System.getProperty(CONFIG_CLASS_SYS_PROP);
        Config config = null;
        if(customConfig!=null){
            try {
                config = (Config)Class.forName(customConfig).newInstance();
                LOG.info("Successfully created custom store config for HazelCast store: " + customConfig);
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "Failed to instantiate custom store config for HazelCast store: " + customConfig, e);
            }
        }
        if(config==null){
            config = ServiceContextManager.getServiceContext().getService(Config.class);
        }
        if(config==null) {
            config = new Config();
            GroupConfig gc = new GroupConfig();
            String groupName = System.getProperty(CONFIG_GROUP_SYS_PROP, "Tamaya");
            gc.setName(groupName);
            config.setGroupConfig(gc);
        }
        LOG.info("Starting HazelCast storage with config: " + config);
        store = Hazelcast.getOrCreateHazelcastInstance(config);
    }

    @Override
    public PropertyStore getPropertyStore(String storeId) {
        HazelcastProprtyStore propertyStore = stores.get(storeId);
        if(propertyStore==null){
            LOG.info("Creating new distributed configuration map in HazelCast store for " + storeId + "...");
            propertyStore = new HazelcastProprtyStore(store, storeId);
            this.stores.put(storeId, propertyStore);
        }
        return propertyStore;
    }



}
