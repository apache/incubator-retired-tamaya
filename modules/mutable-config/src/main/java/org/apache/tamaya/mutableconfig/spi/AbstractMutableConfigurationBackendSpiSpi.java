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

import java.net.URI;
import java.util.*;

/**
 * Base class for implementing a MutableConfigurationBackend.
 */
public abstract class AbstractMutableConfigurationBackendSpiSpi implements MutableConfigurationBackendSpi {

    private final URI backendURI;

    /**
     * The Properties.
     */
    protected final Map<String,String> addedProperties = new HashMap<>();
    /**
     * The Removed.
     */
    protected final Set<String> removedProperties = new HashSet<>();

    protected Set<String> getRemovedProperties() {
        return removedProperties;
    }

    protected Map<String,String> getAddedProperties() {
        return addedProperties;
    }

    protected AbstractMutableConfigurationBackendSpiSpi(URI backendURI){
        this.backendURI = Objects.requireNonNull(backendURI);
    }

    @Override
    public URI getBackendURI() {
        return backendURI;
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
