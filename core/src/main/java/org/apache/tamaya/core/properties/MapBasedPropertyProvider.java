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
package org.apache.tamaya.core.properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tamaya.ConfigChangeSet;
import org.apache.tamaya.MetaInfo;

import java.beans.PropertyChangeEvent;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Models a {@link org.apache.tamaya.PropertyProvider} that can be build using a builder pattern.
 */
class MapBasedPropertyProvider extends AbstractPropertyProvider{

    private static final long serialVersionUID = 7601389831472839249L;

    private static final Logger LOG = LogManager.getLogger(MapBasedPropertyProvider.class);
    /**
     * The unit's entries.
     */
    private Map<String,String> entries = new ConcurrentHashMap<>();

    /**
     * Constructor used by {@link MapBasedPropertyProviderBuilder}, or subclasses.
     *
     * @param entries
     */
    MapBasedPropertyProvider(MetaInfo metaInfo, Map<String,String> entries){
        super(metaInfo);
        Objects.requireNonNull(entries, "entries required.");
        this.entries.putAll(entries);
    }


    /**
     * Constructor used by {@link MapBasedPropertyProviderBuilder}, or subclasses.
     *
     * @param entries the entries
     * @param sources the sources
     * @param errors  the errors
     */
    MapBasedPropertyProvider(MetaInfo metaInfo, Map<String,String> entries, Set<String> sources,
                             Collection<Throwable> errors){
        super(metaInfo);
        Objects.requireNonNull(entries, "entries required.");
        this.entries.putAll(entries);
        addSources(sources);
    }

    MapBasedPropertyProvider(MetaInfo metaInfo, Set<String> sources){
        super(metaInfo);
        addSources(sources);
    }

    @Override
    public Map<String, String> toMap() {
        return new HashMap(this.entries);
    }

    @Override
    public ConfigChangeSet load(){
        // Can not reload...
        return ConfigChangeSet.emptyChangeSet(this);
    }

    /**
     * Apply a config change to this item. Hereby the change must be related to the same instance.
     * @param change the config change
     * @throws org.apache.tamaya.ConfigException if an unrelated change was passed.
     * @throws UnsupportedOperationException when the configuration is not writable.
     */
    @Override
    public void apply(ConfigChangeSet change){
        change.getEvents().forEach(this::applyChange);
    }

    private void applyChange(PropertyChangeEvent propertyChangeEvent) {
        LOG.debug("Applying change to map provider: " + propertyChangeEvent);
        if(propertyChangeEvent.getNewValue()==null){
            this.entries.remove(propertyChangeEvent.getPropertyName());
        }
        else{
            this.entries.put(propertyChangeEvent.getPropertyName(), propertyChangeEvent.getNewValue().toString());
        }
    }

}
