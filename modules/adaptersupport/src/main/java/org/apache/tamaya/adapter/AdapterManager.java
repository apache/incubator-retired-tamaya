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
package org.apache.tamaya.adapter;

import org.apache.tamaya.ConfigException;
import org.apache.tamaya.Configuration;
import org.apache.tamaya.adapter.spi.ConfigurationAdapter;
import org.apache.tamaya.servicecontext.ServiceContext;
import org.apache.tamaya.servicecontext.ServiceContextManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manager that deals with {@link ConfigurationAdapter} instances.
 * This class is thread-safe.
 */
public final class AdapterManager implements ConfigurationAdapter{
    /**
     * The logger used.
     */
    private static final Logger LOG = Logger.getLogger(AdapterManager.class.getName());
    /**
     * The registered converters.
     */
    private final List<ConfigurationAdapter> adapters = new ArrayList<>();

    private static final AdapterManager INSTANCE = new AdapterManager();

    /**
     * The lock used.
     */
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private static final Comparator<Object> PRIORITY_COMPARATOR = new Comparator<Object>() {

        @Override
        public int compare(Object o1, Object o2) {
            ServiceContext serviceContext = ServiceContextManager.getServiceContext();
            int prio = serviceContext.getPriority(o1) - serviceContext.getPriority(o2);
            if (prio < 0) {
                return 1;
            } else if (prio > 0) {
                return -1;
            } else {
                return o1.getClass().getSimpleName().compareTo(o2.getClass().getSimpleName());
            }
        }
    };

    /**
     * Get the instance of the adapter manager.
     * @return the instance of the adapter manager, not null.
     */
    public static final AdapterManager getInstance(){
        return INSTANCE;
    }

    /**
     * Constructor.
     */
    private AdapterManager() {
        this.adapters.addAll(ServiceContextManager.getServiceContext().getServices(ConfigurationAdapter.class));
        Collections.sort(this.adapters, PRIORITY_COMPARATOR);
    }

    /**
     * Registers a new adapter instance.
     *
     * @param adapter the new adapter, not null.
     */
    public void registerAdapter(ConfigurationAdapter adapter) {
        Lock writeLock = lock.writeLock();
        try {
            writeLock.lock();
            this.adapters.addAll(ServiceContextManager.getServiceContext().getServices(ConfigurationAdapter.class));
            Collections.sort(this.adapters, PRIORITY_COMPARATOR);
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Get a list of all currently loaded {@link ConfigurationAdapter} in order of relevance.
     *
     * @return the list of currently available adapters.
     */
    public List<ConfigurationAdapter> getAdapters() {
        Lock readLock = lock.readLock();
        try {
            readLock.lock();
            return new ArrayList<>(this.adapters);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public <T> T adapt(Configuration configuration, Class<T> targetType) {
        for(ConfigurationAdapter adapter:adapters){
            try {
                Object o = adapter.adapt(configuration, targetType);
                if(o!=null){
                    return (T)o;
                }
            }catch(Exception e){
                LOG.log(Level.SEVERE, "Error adapting configuration to: " + targetType.getName(), e);
            }
        }
        throw new ConfigException("Cannot adapt configuration to " + targetType.getName());
    }


    @Override
    public String toString() {
        return "AdapterManager{" +
                "adapters=" + adapters +
                '}';
    }
}
