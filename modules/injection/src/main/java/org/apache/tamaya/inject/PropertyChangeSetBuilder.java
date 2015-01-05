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

import org.apache.tamaya.spi.PropertySource;

import java.beans.PropertyChangeEvent;
import java.util.*;

/**
 * Models a set current changes to be applied to a configuration/property provider.  Such a set can be applied
 * to any {@link PropertySource} instance. If the provider is mutable it may check the
 * version given and applyChanges the changes to the provider/configuration, including triggering current regarding
 * change events.
 * <p>
 * For appropriate conversion a {@code Function<String, Codec>} can be applied, which performs correct conversion,
 * when changed values are set. This function enables connecting e.g. setters on a configuration template with
 * the corresponding conversion logic, so the template calls are correctly converted back.
 */
public final class PropertyChangeSetBuilder {
    /**
     * The recorded changes.
     */
    final SortedSet<String> addedKeys = new TreeSet<>();

    /**
     * The recorded changes.
     */
    final SortedSet<String> updatedKeys = new TreeSet<>();

    /**
     * The recorded changes.
     */
    final SortedSet<String> removedKeys = new TreeSet<>();

    /**
     * The underlying configuration/provider.
     */
    String propertySourceName;

    /**
     * Constructor.
     *
     * @param source      the underlying configuration/provider, not null.
     */
    private PropertyChangeSetBuilder(PropertySource source) {
        this.propertySourceName = Objects.requireNonNull(source).getName();
    }

    /**
     * Creates a new instance current this builder.
     *
     * @param source the underlying property provider/configuration, not null.
     * @return the builder for chaining.
     */
    public static PropertyChangeSetBuilder of(PropertySource source) {
        return new PropertyChangeSetBuilder(source);
    }


    /**
     * Marks the given key(s) as removed.
     *
     * @param keys       the keys removed
     * @return the builder for chaining.
     */
    public PropertyChangeSetBuilder remove(String... keys) {
        for (String removeKey : keys) {
            this.removedKeys.add(removeKey);
        }
        return this;
    }

    /**
     * Marks the given key(s) as added.
     *
     * @param keys       the keys added
     * @return the builder for chaining.
     */
    public PropertyChangeSetBuilder add(String... keys) {
        for (String addKey : keys) {
            this.addedKeys.add(addKey);
        }
        return this;
    }

    /**
     * Marks the given key(s) as updaed.
     *
     * @param keys       the keys updated
     * @return the builder for chaining.
     */
    public PropertyChangeSetBuilder update(String... keys) {
        for (String uptKey : keys) {
            this.updatedKeys.add(uptKey);
        }
        return this;
    }


    /**
     * Compares the two property maps and adds the corresponding updated/aded/removed keys to the builder.
     *
     * @param map1 the source map, not null.
     * @param map2 the target map, not null.
     * @return the builder for chaining.
     */
    public PropertyChangeSetBuilder addChanges(Map<String,String> map1, Map<String,String> map2) {
        List<PropertyChangeEvent> changes = new ArrayList<>();
        for (Map.Entry<String, String> en : map1.entrySet()) {
            String val = map2.get(en.getKey());
            if (val==null) {
                remove(en.getKey());
            } else if (!val.equals(en.getValue())) {
                update(en.getKey());
            }
        }
        for (Map.Entry<String, String> en : map2.entrySet()) {
            String val = map1.get(en.getKey());
            if (val==null) {
                add(en.getKey());
            }
            // update case already handled before!
        }
        return this;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "PropertyChangeEventBuilder [propertySourceName=" + propertySourceName + ", " +
                ", added=" + addedKeys + ", updated=" + updatedKeys +  ", removed=" + removedKeys +"]";
    }

}
