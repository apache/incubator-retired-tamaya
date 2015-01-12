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
package org.apache.tamaya.inject;

import java.io.Serializable;
import java.util.*;

/**
 * Event that contains a set of changed keys of a {@link org.apache.tamaya.spi.PropertySource}. Any values must
 * be obtained by calling the {@link org.apache.tamaya.Configuration} due to security reasons. This event can be used
 * by the {@link org.apache.tamaya.spi.PropertySource} implementation to cleanup caches as needed.
 * This class is immutable and thread-safe. To create instances use
 * {@link PropertyChangeSetBuilder}.
 */
public final class PropertyChangeSet implements Serializable{
    /** The serialVersionUID. */
    private static final long serialVersionUID = 1L;
    /** The base property source name. */
    private String propertySourceName;
    /** The timestamp of the change. */
    private long timestamp = System.currentTimeMillis();
    /**
     * The recorded keys added.
     */
    final SortedSet<String> addedKeys = new TreeSet<>();

    /**
     * The recorded keys updated.
     */
    final SortedSet<String> updatedKeys = new TreeSet<>();

    /**
     * The recorded keys removed.
     */
    final SortedSet<String> removedKeys = new TreeSet<>();


    /**
     * Constructor used by {@link PropertyChangeSetBuilder}.
     * @param builder The builder instance, not null.
     */
    PropertyChangeSet(PropertyChangeSetBuilder builder) {
        this.propertySourceName = Objects.requireNonNull(builder.propertySourceName);
        this.addedKeys.addAll(builder.addedKeys);
        this.removedKeys.addAll(builder.removedKeys);
        this.updatedKeys.addAll(builder.updatedKeys);
    }

    /**
     * Get the underlying property provider/configuration.
     * @return the underlying property provider/configuration, never null.
     */
    public String getPropertySourceName(){
        return this.propertySourceName;
    }

    /**
     * Get the timestamp of this changeset. This allows to track, if a ChangeSet was already applied.
     * @return the timestamp
     */
    public long getTimestamp(){
        return timestamp;
    }

    /**
     * Get the keys added.
     * @return the added keys, never null.
     */
    public Collection<String> getKeysAdded(){
        return Collections.unmodifiableCollection(this.addedKeys);
    }

    /**
     * Get the keys removed.
     * @return the removed keys, never null.
     */
    public Collection<String> getKeysRemoved(){
        return Collections.unmodifiableCollection(this.removedKeys);
    }

    /**
     * Get the updated keys.
     * @return the updated keys, never null.
     */
    public Collection<String> getKeysUpdated(){
        return Collections.unmodifiableCollection(this.addedKeys);
    }


    /**
     * CHecks if the current change set does not contain any changes.
     * @return tru, if the change set is empty.
     */
    public boolean isEmpty(){
        return this.addedKeys.isEmpty() && this.updatedKeys.isEmpty() && this.removedKeys.isEmpty();
    }


    @Override
    public String toString() {
        return "ConfigChangeSet{" +
                "propertySourceName=" + propertySourceName +
                ", timestamp=" + timestamp +
                ", addedKeys=" + addedKeys +
                ", updatedKeys=" + updatedKeys +
                ", removedKeys=" + removedKeys +
                '}';
    }
}
