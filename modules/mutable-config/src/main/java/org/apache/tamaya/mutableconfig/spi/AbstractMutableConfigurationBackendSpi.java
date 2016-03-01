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
package org.apache.tamaya.mutableconfig.spi;

import org.apache.tamaya.spi.PropertySource;

import java.net.URI;
import java.util.*;

/**
 * Base class for implementing a MutableConfigurationBackend.
 */
public abstract class AbstractMutableConfigurationBackendSpi implements MutableConfigurationBackendSpi {

    /** The URI identifying the current backend. */
    private final URI backendURI;

    /**
     * The property source containing the current backend property data.
     */
    private PropertySource backendPropertySource;

    /**
     * The added or changed properties (uncommitted)..
     */
    protected final Map<String,String> addedProperties = new HashMap<>();
    /**
     * The removed properties (uncommitted).
     */
    protected final Set<String> removedProperties = new HashSet<>();

    /**
     * Get the uncommitted removed properties.
     * @return the uncommitted removed properties, never null.
     */
    protected Set<String> getRemovedProperties() {
        return removedProperties;
    }

    /**
     * Get the uncommitted properties added or updated so far.
     * @return the uncommitted properties added or updated, never null.
     */
    protected Map<String,String> getAddedProperties() {
        return addedProperties;
    }

    /**
     * Creates a new instance.
     * @param backendURI the backend URI, not null.
     * @param backendPropertySource the backend property source, not null.
     */
    protected AbstractMutableConfigurationBackendSpi(URI backendURI, PropertySource backendPropertySource){
        this.backendURI = Objects.requireNonNull(backendURI);
        this.backendPropertySource = Objects.requireNonNull(backendPropertySource);
    }

    @Override
    public URI getBackendURI() {
        return backendURI;
    }

    @Override
    public PropertySource getBackendPropertySource(){
        return backendPropertySource;
    }

    @Override
    public boolean isWritable(String keyExpression) {
        return true;
    }

    @Override
    public boolean isRemovable(String keyExpression) {
        return true;
    }

    @Override
    public abstract boolean isExisting(String keyExpression);

    @Override
    public void put(String key, String value) {
        this.addedProperties.put(key, value);
    }

    @Override
    public void putAll(Map<String, String> properties) {
        this.addedProperties.putAll(properties);
    }

    @Override
    public void remove(String... keys) {
        Collections.addAll(this.removedProperties, keys);
    }

    @Override
    public void remove(Collection<String> keys) {
        this.removedProperties.addAll(keys);
    }

    @Override
    public final void commit() {
        commitInternal();
        this.removedProperties.clear();
        this.addedProperties.clear();
    }

    /**
     * Commit internal.
     */
    protected abstract void commitInternal();

    @Override
    public final void rollback() {
        this.removedProperties.clear();
        this.addedProperties.clear();
    }
}
