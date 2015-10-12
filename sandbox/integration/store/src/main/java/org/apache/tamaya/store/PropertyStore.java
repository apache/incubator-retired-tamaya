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

import org.apache.tamaya.spi.PropertySource;

import java.util.Map;

/**
 * Simple abstraction of a property synchronization backend. Most methods are similar
 * to a {@link org.apache.tamaya.spi.PropertySource}, enhanced with methods for
 * updating the property storage.
 */
public interface PropertyStore {

    /**
     * Get the store's name.
     * @return the name, never null.
     */
    String getName();

    /**
     * Stores the given PropertySource into the store.
     * @param propertySource
     */
    void store(PropertySource propertySource);

    /**
     * Read all properties.
     * @return the properties from the remote store, never null.
     */
    Map<String,String> read();

    /**
     * Reads a single key fromt hthe store.
     * @param key the key, not null.
     * @return the value, or null.
     */
    String read(String key);

    /**
     * Determines if {@link #read()} returns all contained properties, or optionally only
     * a subset of the accessible and defined properties are returned.
     * @return true, if this store is scannable.
     */
    boolean isScannable();

}
