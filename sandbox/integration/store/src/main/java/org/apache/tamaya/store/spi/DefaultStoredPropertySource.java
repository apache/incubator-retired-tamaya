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
package org.apache.tamaya.store.spi;

import org.apache.tamaya.events.ConfigEventManager;
import org.apache.tamaya.events.FrozenPropertySource;
import org.apache.tamaya.events.delta.ChangeType;
import org.apache.tamaya.events.delta.PropertySourceChange;
import org.apache.tamaya.events.delta.PropertySourceChangeBuilder;
import org.apache.tamaya.spisupport.BasePropertySource;
import org.apache.tamaya.store.PropertyStore;
import org.apache.tamaya.store.WritablePropertySource;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * PropertySource that is connected to a {@link PropertyStore}.
 */
public class DefaultStoredPropertySource extends BasePropertySource
        implements WritablePropertySource {

    /**
     * The store to be used, not null.
     */
    private PropertyStore propertyStore;

    /**
     * The last version of stoarge data accessed.
     */
    private String lastVersion;

    private Map<String, String> properties = new HashMap<>();


    private FrozenPropertySource currentConfig;

    /**
     * Creates a new property source.
     *
     * @param propertyStore  the backend store.
     * @param defaultOrdinal the default ordinal to be used.
     */
    public DefaultStoredPropertySource(PropertyStore propertyStore, int defaultOrdinal) {
        super(defaultOrdinal);
        this.propertyStore = Objects.requireNonNull(propertyStore);
        this.properties.putAll(this.propertyStore.read());
    }

    @Override
    public void load() {
        this.properties.clear();
        this.properties.putAll(this.propertyStore.read());
        synch();
    }

    @Override
    public String getName() {
        return this.propertyStore.getName();
    }

    @Override
    public Map<String, String> getProperties() {
        return properties;
    }

    @Override
    public String get(String key) {
        if (!propertyStore.isScannable()) {
            return propertyStore.read(key);
        }
        return properties.get(key);
    }

    @Override
    public boolean isScannable() {
        return propertyStore.isScannable();
    }

    @Override
    public String toString() {
        return "StoredPropertySource{" +
                "propertyStore=" + propertyStore.getName() +
                '}';
    }

    @Override
    public void setAll(Map<String, String> props) {
        this.properties.clear();
        this.properties.putAll(props);
        synch();
    }

    @Override
    public void putAll(Map<String, String> props) {
        this.properties.putAll(props);
        synch();
    }

    @Override
    public void put(String key, String value) {
        this.properties.put(key, value);
        synch();
    }

    @Override
    public void clear() {
        this.properties.clear();
        synch();
    }

    @Override
    public void synch() {
        FrozenPropertySource newConfig = FrozenPropertySource.of(this);
        PropertySourceChange change = PropertySourceChangeBuilder.of(currentConfig, ChangeType.UPDATED)
                .addChanges(newConfig).build();
        currentConfig = newConfig;
        ConfigEventManager.fireEvent(change);
    }
}
