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


import java.util.Map;


/**
 * <p>This interface models a provider that serves configuration properties. The contained
 * properties may be read fromMap single or several sources (composite).
 * PropertySources are the building blocks of the final configuration. </p>
 * <h3>Implementation Requirements</h3>
 * <p>Implementations current this interface must be</p>
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
    static final String TAMAYA_ORDINAL = "tamaya.ordinal";


    /**
     * Lookup order:
     * TODO rethink whole default PropertySources and ordering:
     * TODO introduce default values or constants for ordinals
     * <ol>
     *     <li>System properties (ordinal 400)</li>
     *     <li>Environment properties (ordinal 300)</li>
     *     <li>JNDI values (ordinal 200)</li>
     *     <li>Properties file values (/META-INF/applicationConfiguration.properties) (ordinal 100)</li>
     * </ol>
     *
     * <p><b>Important Hints for custom implementations</b>:</p>
     * <p>
     * If a custom implementation should be invoked <b>before</b> the default implementations, use a value &gt; 400
     * </p>
     * <p>
     * If a custom implementation should be invoked <b>after</b> the default implementations, use a value &lt; 100
     * </p>
     *
     * <p>Reordering of the default order of the config-sources:</p>
     * <p>Example: If the properties file/s should be used <b>before</b> the other implementations,
     * you have to configure an ordinal &gt; 400. That means, you have to add e.g. deltaspike_ordinal=401 to
     * /META-INF/apache-deltaspike.properties . Hint: In case of property files every file is handled as independent
     * config-source, but all of them have ordinal 400 by default (and can be reordered in a fine-grained manner.</p>
     *
     * @return the 'importance' aka ordinal of the configured values. The higher, the more important.
     * //X TODO think about making this a default method which returns default priority
     */
    int getOrdinal();


    /**
     * Get the name of the property source. The name should be unique for the type of source, whereas the id is used
     * to ensure unique identity, either locally or remotely.
     * @return the configuration's name, never null.
     */
    String getName();

    /**
     * Access a property.
     *
     * //X TODO discuss if the key can be null
     * @param key the property's key, not null.
     * @return the property's keys.
     */
    String get(String key);

    /**
     * Access the current properties as Map. The resulting Map may not return all items accessible, e.g.
     * when the underlying storage does not support iteration of its entries.
     *
     * @return the a corresponding map, never null.
     * //X TODO or should we just do getPropertyKeys()? Think about security (key) vs easier merging (full map)?
     */
    Map<String,String> getProperties();

    /**
     * Determines if this config source could be scanned for its list of properties.
     *
     * <p>
     * PropertySources which are not scannable might not be able to find all the
     * configured values to provide via {@link #getProperties()}. This can e.g. happen
     * if the underlying storage doesn't support listing.
     * </p>
     *
     * @return {@code true} if this PropertySource could be scanned for its list of properties,
     *         {@code false} if it should not be scanned.
     */
    boolean isScannable();

}
