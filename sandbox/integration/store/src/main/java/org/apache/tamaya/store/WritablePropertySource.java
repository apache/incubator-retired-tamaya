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
public interface WritablePropertySource extends PropertySource{

    /**
     * Loads the property source from its store..
     */
    void load();

    /**
     * Sets the store to the given properties, replacing all previous state.
     * @param props the new properties.
     */
    void setAll(Map<String, String> props);

    /**
     * Updates/adds all the given entries.
     * @param props the entries to be added/updated.
     */
    void putAll(Map<String, String> props);

    /**
     * Writes a single entry.
     * @param key the key, not null.
     * @param value the new value, not null.
     */
    void put(String key, String value);

    /**
     * Removes all entries.
     */
    void clear();

    /**
     * Synchronize the current state with the remote version.
     */
    void synch();

}
