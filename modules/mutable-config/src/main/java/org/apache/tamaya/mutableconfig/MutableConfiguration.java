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
package org.apache.tamaya.mutableconfig;

import org.apache.tamaya.Configuration;
import org.apache.tamaya.mutableconfig.spi.ConfigChangeRequest;

import java.util.Collection;
import java.util.Map;


/**
 * This interface extends the Configuration interface hereby adding methods to change configuration entries.
 * Hereby not all configuration entries are necessarily mutable, since some entries may be read from non
 * mutable areas of configuration. Of course, it is always possible to add a mutable shadow layer on top of all
 * property sources to persist/control any changes applied. The exact management and storage persistence algorithm
 * should be transparent.
 *
 * As a consequence clients should first check, using the corresponding methods, if entries can be added/updated or
 * removed.
 *
 * This class should only used in a single threaded context, though all methods inherited from {@link Configuration}
 * must be thread-safe. Methods handling configuration changes are expected to be used in a single threaded environment
 * only. For multi-threaded us create a new instance of {@link MutableConfiguration} for each thread.
 */
public interface MutableConfiguration extends Configuration {

    /**
     * Storesd the changes. After a commit the change is not editable anymore. All changes applied will be written to
     * the corresponding configuration backend.
     *
     * NOTE that changes applied must not necessarily be visible in the current {@link Configuration} instance,
     * since visibility of changes also depends on the ordinals set on the {@link org.apache.tamaya.spi.PropertySource}s
     * configured.
     * @throws org.apache.tamaya.ConfigException if the request already has been committed or cancelled, or the commit fails.
     */
    void store();

    /**
     * Access the current configuration change context, built up on all the change context of the participating
     * {@link org.apache.tamaya.mutableconfig.spi.MutablePropertySource} instances.
     * @return the colleted changes as one single config change for the current transaction, or null, if no transaction
     * is active.
     */
    ConfigChangeRequest getConfigChangeRequest();

    /**
     * Access the active {@link ChangePropagationPolicy}.This policy controls how configuration changes are written/published
     * to the known {@link org.apache.tamaya.mutableconfig.spi.MutablePropertySource} instances of a {@link Configuration}.
     * @return he active {@link ChangePropagationPolicy}, never null.
     */
    ChangePropagationPolicy getChangePropagationPolicy();

    /**
     * Sets a property.
     *
     * @param key   the property's key, not null.
     * @param value the property's value, not null.
     * @return the former property value, or null.
     * @throws org.apache.tamaya.ConfigException if the key/value cannot be added, or the request is read-only.
     */
    MutableConfiguration put(String key, String value);

    /**
     * Puts all given configuration entries. This method should check that all given properties are
     * basically removable, as defined by #isWritable. If any of the passed keys is not writable during this initial
     * check, the operation should not perform any configuration changes and throw a
     * {@link org.apache.tamaya.ConfigException}. If errors occur afterwards, when the properties are effectively
     * written back to the backends, the errors should be collected and returned as part of the ConfigException
     * payload. Nevertheless the operation should in that case remove all entries as far as possible and abort the
     * writing operation.
     *
     * @param properties the properties tobe written, not null.
     * @return the config change request
     * @throws org.apache.tamaya.ConfigException if any of the given properties could not be written, or the request
     * is read-only.
     */
    MutableConfiguration putAll(Map<String, String> properties);

    /**
     * Removes all given configuration entries. This method should check that all given properties are
     * basically removable, as defined by #isRemovable. If any of the passed keys is not removable during this initial
     * check, the operation should not perform any configuration changes and throw a
     * {@link org.apache.tamaya.ConfigException}. If errors
     * occur afterwards, when the properties are effectively written back to the backends, the errors should be
     * collected and returned as part of the ConfigException payload. Nevertheless the operation should in that case
     * remove all entries as far as possible and abort the writing operation.
     *
     * @param keys the property's keys to be removedProperties, not null.
     * @return the config change request
     * @throws org.apache.tamaya.ConfigException if any of the given keys could not be removedProperties, or the
     * request is read-only.
     */
    MutableConfiguration remove(Collection<String> keys);

    /**
     * Removes all given configuration entries. This method should check that all given properties are
     * basically removable, as defined by #isRemovable. If any of the passed keys is not removable during this initial
     * check, the operation should not perform any configuration changes and throw a {@link org.apache.tamaya.ConfigException}. If errors
     * occur afterwards, when the properties are effectively written back to the backends, the errors should be
     * collected and returned as part of the ConfigException payload. Nevertheless the operation should in that case
     * remove all entries as far as possible and abort the writing operation.
     *
     * @param keys the property's keys to be removedProperties, not null.
     * @return the config change request
     * @throws org.apache.tamaya.ConfigException if any of the given keys could not be removedProperties, or the request is read-only.
     */
    MutableConfiguration remove(String... keys);

}
