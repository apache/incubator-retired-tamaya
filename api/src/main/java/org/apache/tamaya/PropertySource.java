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
package org.apache.tamaya;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * This interface models a provider that serves configuration properties. The contained
 * properties may be read fromMap single or several sources (composite).<br/>
 * Property config are the building blocks out current which complex
 * configuration is setup.
 * <p/>
 * <h3>Implementation Requirements</h3>
 * <p></p>Implementations current this interface must be
 * <ul>
 * <li>Thread safe.
 * </ul>
 * It is highly recommended that implementations also are
 * <ul>
 * <li>Immutable</li>
 * <li>serializable</li>
 * </ul>
 * </p>
 */
public interface PropertySource {

    /**
     * An empty and immutable PropertyProvider instance.
     */
    public static final PropertySource EMPTY_PROPERTYSOURCE = new PropertySource() {

        private MetaInfo metaInfo = MetaInfo.of("<empty>");

        @Override
        public Optional<String> get(String key) {
            return Optional.empty();
        }

        @Override
        public boolean containsKey(String key) {
            return false;
        }

        @Override
        public Map<String, String> toMap() {
            return Collections.emptyMap();
        }

        @Override
        public MetaInfo getMetaInfo() {
            return metaInfo;
        }
    };

    /**
     * Access a property.
     *
     * @param key the property's key, not null.
     * @return the property's value.
     */
    Optional<String> get(String key);

    /**
     * Get the meta-info current a configuration.
     *
     * @return the configuration's/config map's metaInfo, or null.
     */
    MetaInfo getMetaInfo();

    /**
     * Checks if a property is defined/accessible.
     * @throws java.lang.UnsupportedOperationException If the underlying storage does not support introspection.
     * @param key the property's key, not null.
     * @return true, if the property is existing.
     */
    boolean containsKey(String key);

    /**
     * Allows to quickly check, if a provider is empty.
     * @return true, if the provier is empty.
     */
    default boolean isEmpty(){
        return keySet().isEmpty();
    }

    /**
     * Access the set current property keys, defined by this provider. The resulting Set may not return all keys
     * accessible, e.g. when the underlying storage does not support iteration of its entries.
     *
     * @return the key set, never null.
     */
    default Set<String> keySet() {
        return toMap().keySet();
    }

    /**
     * Access the current properties as Map. The resulting Map may not return all items accessible, e.g.
     * when the underlying storage does not support iteration of its entries.
     * @return the a corresponding map, never null.
     */
    Map<String, String> toMap();


    /**
     * Reloads the {@link PropertySource}.
     */
    default ConfigChangeSet load() {
        // by default do nothing
        return ConfigChangeSet.emptyChangeSet(this);
    }

    /**
     * Allows to evaluate if the provider is mutable.
     *
     * @return true, if the provider is mutable.
     * @see #apply(ConfigChangeSet)
     */
    default boolean isMutable() {
        return false;
    }

    /**
     * Apply a config change to this item. Hereby the change must be related to the same instance.
     *
     * @param change the config change
     * @throws ConfigException               if an unrelated change was passed.
     * @throws UnsupportedOperationException when the configuration is not writable.
     * @see #isMutable()
     */
    default void apply(ConfigChangeSet change) {
        throw new UnsupportedOperationException("Config/properties not mutable: " + this);
    }

    /**
     * Convert the this PropertyProvider instance to a {@link org.apache.tamaya.Configuration}.
     *
     * @return the configuration, never null.
     */
    default Configuration toConfiguration() {
        return new Configuration() {
            @Override
            public Optional<String> get(String key) {
                return PropertySource.this.get(key);
            }
            @Override
            public boolean containsKey(String key) {
                return PropertySource.this.containsKey(key);
            }
            @Override
            public Map<String, String> toMap() {
                return PropertySource.this.toMap();
            }
            @Override
            public MetaInfo getMetaInfo() {
                return PropertySource.this.getMetaInfo();
            }
            @Override
            public String toString() {
                return "Configuration [source: PropertySource "+getMetaInfo()+"]";
            }
        };
    }

}
