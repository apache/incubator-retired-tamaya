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

import java.util.*;

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

        @Override
        public String getName() {
            return "<empty>";
        }

        @Override
        public Optional<String> get(String key) {
            return Optional.empty();
        }

        @Override
        public Map<String, String> getProperties() {
            return Collections.emptyMap();
        }

        @Override
        public String toString(){
            return "PropertySource [name=<empty>]";
        }
    };

    /**
     * Get the name of the property source. The name should be unique for the type of source, whereas the id is used
     * to ensure unique identity, either locally or remotely.
     * @return the configuration's name, never null.
     */
    String getName();

    /**
     * Access a property.
     *
     * @param key the property's key, not null.
     * @return the property's keys.
     */
    Optional<String> get(String key);

    /**
     * Access the current properties as Map. The resulting Map may not return all items accessible, e.g.
     * when the underlying storage does not support iteration of its entries.
     *
     * @return the a corresponding map, never null.
     */
    Map<String, String> getProperties();

    /**
     * Determines if this config source should be scanned for its list of properties.
     *
     * Generally, slow ConfigSources should return false here.
     *
     * @return true if this ConfigSource should be scanned for its list of properties,
     * false if it should not be scanned.
     */
    default boolean isScannable(){
        return true;
    }

    /**
     * Allows to quickly check, if a provider is empty.
     *
     * @return true, if the provier is empty.
     */
    default boolean isEmpty() {
        return getProperties().isEmpty();
    }

    /**
     * Convert the this PropertyProvider instance to a {@link org.apache.tamaya.Configuration}.
     *
     * @return the configuration, never null.
     */
    default Configuration toConfiguration() {
        return new Configuration() {
            @Override
            public String getName() {
                return PropertySource.this.getName();
            }

            @Override
            public boolean isScannable() {
                return PropertySource.this.isScannable();
            }

            @Override
            public boolean isEmpty() {
                return PropertySource.this.isEmpty();
            }

            @Override
            public Optional<String> get(String key) {
                return PropertySource.this.get(key);
            }

            @Override
            public Map<String, String> getProperties() {
                return PropertySource.this.getProperties();
            }

            @Override
            public String toString() {
                return "Configuration [name: " + getName() + "]";
            }
        };
    }

}
