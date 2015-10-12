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
package org.apache.tamaya.store;

import org.apache.tamaya.ConfigException;
import org.apache.tamaya.ConfigurationProvider;
import org.apache.tamaya.spi.PropertySource;
import org.apache.tamaya.spi.ServiceContextManager;
import org.apache.tamaya.store.spi.PropertyStoreProviderSpi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

/**
 * Simple provider accessor singleton for accessing {@link PropertyStore}
 * instances.
 */
public final class PropertyStoreProvider {
    /**
     * The logger used.
     */
    private static final Logger LOG = Logger.getLogger(PropertyStoreProvider.class.getName());


    /**
     * Singleton constructor.
     */
    private PropertyStoreProvider() {
    }


    /**
     * Access a {@link PropertyStore} using it's id.
     *
     * @param storeId the unique id of the store to use.
     * @return
     */
    public static PropertyStore getPropertyStore(String storeId) {
        for (PropertyStoreProviderSpi propertyStoreProviderSpi : ServiceContextManager.getServiceContext()
                .getServices(PropertyStoreProviderSpi.class)) {
            PropertyStore store = propertyStoreProviderSpi.getPropertyStore(storeId);
            if (store != null) {
                LOG.finest("DataGrid '" + storeId + "' used: " + store);
                return store;
            }
        }
        throw new ConfigException("No such Store: " + storeId);
    }

    /**
     * Access a {@link PropertyStore} using it's id, hereby returning the most significant of the configured
     * {@link PropertyStore} instances.
     *
     * @return the default PropertyStore instance.
     */
    public static PropertyStore getDefaultPropertyStore() {
        return getPropertyStore("default");
    }

}
