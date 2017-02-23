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
package org.apache.tamaya.spi;


import java.util.Collections;
import java.util.Map;


/**
 * <p>This interface models a provider that serves configuration properties. The contained
 * properties may be read fromMap single or several sources (composite).
 * PropertySources are the building blocks of the final configuration. </p>
 * <h3>Implementation Requirements</h3>
 * <p>Implementations of this interface must be</p>
 * <ul>
 * <li>Thread safe.</li>
 * </ul>
 *
 * <p>A PropertySourceProvider will get picked up via the
 * {@link java.util.ServiceLoader} mechanism and can be registered via
 * META-INF/services/org.apache.tamaya.spi.PropertySource
 * </p>
 * <p>
 * If you like to register multiple PropertySources at the same time
 * you can use the {@link org.apache.tamaya.spi.PropertySourceProvider}
 * interface.
 * </p>
 */
public interface PropertySource {

    /**
     * property name to override default tamaya ordinals
     */
    String TAMAYA_ORDINAL = "tamaya.ordinal";

    /**
     * A resusable instance of an empty PropertySource.
     */
    PropertySource EMPTY = new PropertySource() {

        public int getOrdinal() {
            return Integer.MIN_VALUE;
        }

        @Override
        public String getName() {
            return "<empty>";
        }

        @Override
        public PropertyValue get(String key) {
            return null;
        }

        @Override
        public Map<String, String> getProperties() {
            return Collections.emptyMap();
        }

        @Override
        public boolean isScannable() {
            return false;
        }

        @Override
        public String toString(){
            return "PropertySource.EMPTY";
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
     * @return the property value map, where {@code map.get(key) == value}, including also any metadata. In case a
     * value is null, simply return {@code null}.
     */
    PropertyValue get(String key);

    /**
     * Access the current properties as Map. The resulting Map may not return all items accessible, e.g.
     * when the underlying storage does not support iteration of its entries.
     *
     * @return the a corresponding map, never null.
     */
    Map<String,String> getProperties();

    /**
     * Determines if this config source can be scanned for its list of properties.
     *
     * <p>
     * PropertySources which are not scannable might not be able to find all the
     * configured values to provide via {@link #getProperties()}. This can e.g. happen
     * if the underlying storage doesn't support listing.
     * </p>
     *
     * @return {@code true} if this PropertySource can be scanned for its list of properties,
     *         {@code false} if it should not be scanned.
     */
    boolean isScannable();

}
