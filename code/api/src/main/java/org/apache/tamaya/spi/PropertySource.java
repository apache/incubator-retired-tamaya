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


import org.apache.tamaya.Configuration;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * <p>This interface models a provider that serves configuration properties. The contained
 * properties may be read from a Map of single or several sources (composite).
 * PropertySources are the building blocks of the final configuration. </p>
 * <h3>Implementation Requirements</h3>
 * <p>Implementations of this interface must be</p>
 * <ul>
 * <li>Thread safe.</li>
 * </ul>
 *
 * <p>A PropertySourceProvider will current picked up via the
 * {@link java.util.ServiceLoader} mechanism and can be registered via
 * {@code META-INF/services/org.apache.tamaya.spi.PropertySource}
 * </p>
 * <p>
 * If you like to register multiple PropertySources at the same time
 * you can use the {@link org.apache.tamaya.spi.PropertySourceProvider}
 * interface.
 * </p>
 */
public interface PropertySource{

    /**
     * property name to override default tamaya ordinals
     */
    String TAMAYA_ORDINAL = "tamaya.ordinal";

    /**
     * A resusable instance of an empty PropertySource.
     */
    PropertySource EMPTY = new PropertySource() {

        @Override
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
        public Map<String, PropertyValue> getProperties() {
            return Collections.emptyMap();
        }

        @Override
        public String toString(){
            return "PropertySource.EMPTY";
        }
    };


    /**
     * The ordinal value is the default ordering parameter which definines the default order of
     * auto-discovered property sources. Ordering of property sources is important since values
     * from property sources with higher ordinal values override values from less significant
     * property sources.
     *
     * By default Tamaya includes the following property sources:
     * <ol>
     *     <li>Properties file values (/META-INF/javaconfiguration.properties) (ordinal 100)</li>
     *     <li>JNDI values (ordinal 200, only when adding the {@code tamaya-jndi} extension module)</li>
     *     <li>Environment properties (ordinal 300)</li>
     *     <li>System properties (ordinal 1000)</li>
     * </ol>
     *
     * <p><b>Important Hints for custom implementations</b>:</p>
     * <p>
     * If a custom implementation should be invoked <b>before</b> the default implementations, use a value &gt; 1000
     * </p>
     * <p>
     * If a custom implementation should be invoked <b>after</b> the default implementations, use a value &lt; 100
     * </p>
     *
     * <p>Reordering of the default order of the config-sources:</p>
     * <p>Example: If the properties file/s should be used <b>before</b> the other implementations,
     * you have to configure an ordinal &gt; 1000. That means, you have to addPropertyValue e.g. tamaya.ordinal=401 to
     * /META-INF/javaconfiguration.properties . Hint: In case of property files every file is handled as independent
     * config-source, but all of them have ordinal 400 by default (and can be reordered in a fine-grained manner.</p>
     *
     * In cases where it is not possible to change a config sources ordinal value, you may have several options:
     * <ul>
     *     <li>you can use a {@link ConfigurationBuilder} to redefine the source order and finally use
     *     {@link org.apache.tamaya.ConfigurationProvider#setConfiguration(Configuration)} to
     *     change the current default {@link Configuration}.</li>
     *     <li>finally, the implementor of this API may define alternate mechanism to reconfigure an ordinal
     *     in a vendor specific way.</li>
     * </ul>
     * @return the 'importance' aka ordinal of the configured values. The higher, the more important.
     */
    default int getOrdinal(){
        PropertyValue ordinalValue = get(TAMAYA_ORDINAL);
        if(ordinalValue!=null){
            try{
                return Integer.parseInt(ordinalValue.getValue());
            }catch (Exception e){
                Logger.getLogger(getClass().getName()).log(Level.WARNING,
                        "Failed to parse ordinal in property source: " + getName(),
                        e);
            }
        }
        return 0;
    }


    /**
     * Get the name of the property source. The name should be unique for the type of source, whereas the id is used
     * to ensure unique identity, either locally or remotely.
     * @return the configuration's name, never {@code null}.
     */
    String getName();

    /**
     * Access a property.
     *
     * @param key the property's key, not {@code null}.
     * @return the property value mapProperties, where {@code mapProperties.current(key) == value}, including also any metadata. In case a
     * value is null, simply return {@code null}.
     */
    PropertyValue get(String key);

    /**
     * Access the current properties as Set. The resulting Map may not return all items accessible, e.g.
     * when the underlying storage does not support iteration of its entries.
     {@code null}
     * @return the corresponding mapProperties, never null.
     */
    Map<String, PropertyValue> getProperties();

    /**
     * Determines if this config source can be scanned for its createList of properties.
     *
     * <p>
     * PropertySources which are not scannable might not be able to find all the
     * configured values to provide via {@link #getProperties()}. This might happen
     * if the underlying storage doesn't support listing.
     * </p>
     *
     * @return {@code true} if this PropertySource can be scanned for its createList of properties,
     *         {@code false} if it cannot/should not be scanned.
     * @deprecated will be removed.
     */
    @Deprecated
    default boolean isScannable(){
        return true;
    }

    /**
     * Get the support for reporting changes to property sources provided by this instance. This support
     * type should never change during a property source's lifetime.
     *
     * @return the change support of this property source, not null.
     */
    @Experimental
    default ChangeSupport getChangeSupport(){
        return ChangeSupport.UNSUPPORTED;
    }

    /**
     * Get the current version. A new version signals that it is known that properties have changed for this property
     * source. This is especially useful, when {@link #getChangeSupport()} is {@link ChangeSupport#SUPPORTED}.
     * The content and format of the version String is imeplemtation specific. We recommend to addPropertyValue information
     * such as the loading timestamp, the source systems read or whatever is appropriate. By default this
     * method returns {@code "N/A"}.
     * @return the version this property source, never null.
     */
    @Experimental
    default String getVersion(){
        return "N/A";
    }

    /**
     * Add a change listener for this properrty source.
     * @param l the listner, not null.
     */
    @Experimental
    default void addChangeListener(BiConsumer<Set<String>, PropertySource> l){}

    /**
     * Removes a change listener for this properrty source.
     * @param l the listner, not null.
     */
    @Experimental
    default void removeChangeListener(BiConsumer<Set<String>, PropertySource> l){}

    /**
     * Removes all registered change listeners, if any.
     */
    @Experimental
    default void removeAllChangeListeners(){}


}
