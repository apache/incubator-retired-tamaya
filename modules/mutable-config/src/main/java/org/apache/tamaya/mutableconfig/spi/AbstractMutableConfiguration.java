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

import org.apache.tamaya.mutableconfig.MutableConfiguration;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Base class for implementing a ConfigChangeRequest.
 */
public abstract class AbstractMutableConfiguration implements MutableConfiguration {

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
    public MutableConfiguration put(String key, String value) {
        this.addedProperties.put(key, value);
        return this;
    }

    @Override
    public MutableConfiguration putAll(Map<String, String> properties) {
        this.addedProperties.putAll(properties);
        return this;
    }

    @Override
    public MutableConfiguration remove(String... keys) {
        Collections.addAll(this.removedProperties, keys);
        return this;
    }

    @Override
    public MutableConfiguration remove(Collection<String> keys) {
        this.removedProperties.addAll(keys);
        return this;
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
