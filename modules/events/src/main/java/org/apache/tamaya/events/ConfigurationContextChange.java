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
package org.apache.tamaya.events;

import org.apache.tamaya.spi.ConfigurationContext;
import org.apache.tamaya.spi.PropertySource;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Event that contains a set current changes that were applied or could be applied.
 * This class is immutable and thread-safe. To create instances use
 * {@link PropertySourceChangeBuilder}.
 *
 * Created by Anatole on 22.10.2014.
 */
public final class ConfigurationContextChange implements ConfigEvent<ConfigurationContext>, Serializable{

    private static final long serialVersionUID = 1L;
    /** The base property provider/configuration. */
    private List<PropertySourceChange> changedPropertySources = new ArrayList<>();
    /** The base version, usable for optimistic locking. */
    private String version = UUID.randomUUID().toString();
    /** The timestamp of the change set in millis from the epoch. */
    private long timestamp = System.currentTimeMillis();
    /** The configuration context. */
    private ConfigurationContext configurationContext;

    /**
     * Get an empty change set for the given provider.
     * @return an empty ConfigurationContextChange instance.
     */
    public static ConfigurationContextChange emptyChangeSet(ConfigurationContext configurationContext){
        return ConfigurationContextChangeBuilder.of(configurationContext).build();
    }

    /**
     * Constructor used by {@link PropertySourceChangeBuilder}.
     * @param builder The builder used, not null.
     */
    ConfigurationContextChange(ConfigurationContextChangeBuilder builder) {
        this.changedPropertySources.addAll(builder.changedPropertySources);
        if(builder.version!=null){
            this.version = builder.version;
        }
        if(builder.timestamp!=null){
            this.timestamp = builder.timestamp;
        }
        this.configurationContext = builder.configurationContext;
    }

    @Override
    public Class<ConfigurationContext> getResourceType() {
        return ConfigurationContext.class;
    }

    @Override
    public ConfigurationContext getResource() {
        return configurationContext;
    }

    /**
     * Get the base version, usable for optimistic locking.
     * @return the base version.
     */
    @Override
    public String getVersion(){
        return version;
    }

    /**
     * Get the timestamp in millis from the current epoch. it is expected that the timestamp and the version are unique to
     * identify a changeset.
     * @return the timestamp, when this changeset was created.
     */
    @Override
    public long getTimestamp(){
        return timestamp;
    }

    /**
     * Get the changes recorded.
     * @return the recorded changes, never null.
     */
    public Collection<PropertySourceChange> getPropertySourceChanges(){
        return Collections.unmodifiableCollection(this.changedPropertySources);
    }

    /**
     * Get the property source updates.
     * @return the recorded changes, never null.
     */
    public Collection<PropertySourceChange> getPropertySourceUpdates(){
        List<PropertySourceChange> result = new ArrayList<>();
        for (PropertySourceChange pc : this.changedPropertySources) {
            if (pc.getChangeType() == ChangeType.UPDATED) {
                result.add(pc);
            }
        }
        return result;
//        return Collections.unmodifiableCollection(this.changedPropertySources).stream()
//                .filter(pc -> pc.getChangeType()==ChangeType.UPDATED).collect(Collectors.toList());
    }

    /**
     * Get the property sources to be removed.
     * @return the recorded changes, never null.
     */
    public Collection<PropertySource> getRemovedPropertySources(){
        List<PropertySource> result = new ArrayList<>();
        for (PropertySourceChange pc : this.changedPropertySources) {
            if (pc.getChangeType() == ChangeType.DELETED) {
                result.add(pc.getResource());
            }
        }
        return result;
//        return getPropertySourceChanges().stream().filter(pc -> pc.getChangeType()==ChangeType.DELETED).
//                map(ps -> ps.getPropertySource()).collect(Collectors.toList());
    }

    /**
     * Get the property sources to be added.
     * @return the recorded changes, never null.
     */
    public Collection<PropertySource> getAddedPropertySources(){
        List<PropertySource> result = new ArrayList<>();
        for (PropertySourceChange pc : this.changedPropertySources) {
            if (pc.getChangeType() == ChangeType.NEW) {
                result.add(pc.getResource());
            }
        }
        return result;
//        return getPropertySourceChanges().stream().filter(pc -> pc.getChangeType()==ChangeType.NEW).
//                map(ps -> ps.getPropertySource()).collect(Collectors.toList());
    }

    /**
     * Get the property sources to be updated.
     * @return the recorded changes, never null.
     */
    public Collection<PropertySource> getUpdatedPropertySources(){
        List<PropertySource> result = new ArrayList<>();
        for (PropertySourceChange pc : this.changedPropertySources) {
            if (pc.getChangeType() == ChangeType.UPDATED) {
                result.add(pc.getResource());
            }
        }
        return result;
//        return getPropertySourceChanges().stream().filter(pc -> pc.getChangeType()==ChangeType.UPDATED).
//                map(ps -> ps.getPropertySource()).collect(Collectors.toList());
    }

    /**
     * Checks if the given propertySource is affected (added, changed or removed).
     * @param propertySource the propertySource, not null.
     * @return true, if the given propertySource ia affected.
     */
    public boolean isAffected(PropertySource propertySource) {
        for (PropertySourceChange ps : this.changedPropertySources) {
            if (ps.getResource() == propertySource ||
                    ps.getResource().getName().equals(propertySource.getName())) {
                return true;
            }
        }
        return false;
//        return this.changedPropertySources.stream().filter(ps ->  ps.getPropertySource()==propertySource ||
//                ps.getPropertySource().getName().equals(propertySource.getName())).findAny().isPresent();
    }

    /**
     * CHecks if the current change set does not contain any changes.
     * @return tru, if the change set is empty.
     */
    public boolean isEmpty(){
        return this.changedPropertySources.isEmpty();
    }


    @Override
    public String toString() {
        return "ConfigurationContextChange{" +
                "changedPropertySources=" + changedPropertySources +
                ", version='" + version + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
