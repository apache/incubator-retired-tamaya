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
package org.apache.tamaya.mutableconfig.spi;

import org.apache.tamaya.mutableconfig.propertysources.ConfigChangeContext;
import org.apache.tamaya.spi.PropertySource;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;


/**
 * This interface models a writable backend for configuration data.
 *
 * As a consequence clients should first check, using the corresponding methods, if entries are to edited or removedProperties
 * actually are eligible for change/creation or removal.
 */
public interface MutablePropertySource extends PropertySource {

    /**
     * Checks if a configuration key is writable (or it can be added).
     *
     * @param key the key to be checked for write access (including creation), not null.
     * @return true if the key can be added or updated.
     */
    boolean isWritable(String key);

    /**
     * Checks if a configuration key is removable. This also implies that it is writable, but there might be writable
     * keys that cannot be removedProperties.
     *
     * @param key the key to be checked for removal, not null.
     * @return true, if the key can be removed.
     */
    boolean isRemovable(String key);

    /**
     * Sets a property.
     *
     * @param key   the property's key, not null.
     * @param value the property's value, not null.
     * @param transactionId the transactionId used to isolate the change, not null.
     * @return this instance for optional chaining of operations, nrvrt null.
     * @throws org.apache.tamaya.ConfigException if the key/value cannot be added, or the request is read-only.
     */
    MutablePropertySource put(String transactionId, String key, String value);


    /**
     * Puts all given configuration entries. This method should check that all given properties are
     * basically removable, as defined by #isWritable. If any of the passed keys is not writable during this initial
     * check, the operation should not perform any configuration changes and throw a {@link org.apache.tamaya.ConfigException}. If errors
     * occur afterwards, when the properties are effectively written back to the backends, the errors should be
     * collected and returned as part of the ConfigException payload. Nevertheless the operation should in that case
     * remove all entries as far as possible and abort the writing operation.
     *
     * @param transactionId the transactionId used to isolate the change, not null.
     * @param properties the properties tobe written, not null.
     * @return this instance for optional chaining of operations, nrvrt null.
     * @throws org.apache.tamaya.ConfigException if any of the given properties could not be written, or the request is read-only.
     */
    MutablePropertySource putAll(String transactionId, Map<String, String> properties);

    /**
     * Removes all given configuration entries. This method should check that all given properties are
     * basically removable, as defined by #isRemovable. If any of the passed keys is not removable during this initial
     * check, the operation should not perform any configuration changes and throw a {@link org.apache.tamaya.ConfigException}. If errors
     * occur afterwards, when the properties are effectively written back to the backends, the errors should be
     * collected and returned as part of the ConfigException payload. Nevertheless the operation should in that case
     * remove all entries as far as possible and abort the writing operation.
     *
     * @param transactionId the transactionId used to isolate the change, not null.
     * @param keys the property's keys to be removedProperties, not null.
     * @return this instance for optional chaining of operations, nrvrt null.
     * @throws org.apache.tamaya.ConfigException if any of the given keys could not be removedProperties, or the request is read-only.
     */
    MutablePropertySource remove(String transactionId, Collection<String> keys);

    /**
     * Removes all given configuration entries. This method should check that all given properties are
     * basically removable, as defined by #isRemovable. If any of the passed keys is not removable during this initial
     * check, the operation should not perform any configuration changes and throw a {@link org.apache.tamaya.ConfigException}. If errors
     * occur afterwards, when the properties are effectively written back to the backends, the errors should be
     * collected and returned as part of the ConfigException payload. Nevertheless the operation should in that case
     * remove all entries as far as possible and abort the writing operation.
     *
     * @param transactionId the transactionId used to isolate the change, not null.
     * @param keys the property's keys to be removedProperties, not null.
     * @return this instance for optional chaining of operations, nrvrt null.
     * @throws org.apache.tamaya.ConfigException if any of the given keys could not be removedProperties, or the request is read-only.
     */
    MutablePropertySource remove(String transactionId, String... keys);

    /**
     * Commits the request. After a commit the change is not editable anymore. All changes applied will be written to
     * the corresponding configuration backend.
     *
     * NOTE that changes applied must not necessarily be visible in the current {@link org.apache.tamaya.Configuration} instance,
     * since visibility of changes also depends on the ordinals set on the {@link org.apache.tamaya.spi.PropertySource}s
     * configured.
     * @throws org.apache.tamaya.ConfigException if the request already has been committed or cancelled, or the commit fails.
     * @param transactionId the transaction id, not null.
     */
    void commitTransaction(String transactionId);

    /**
     * Rollback any changes leaving everything unchanged. This will rollback all changes applied since the last commit.
     * @param transactionId the transaction id, not null.
     */
    void rollbackTransaction(String transactionId);

    /**
     * Start a new transaction context with the given isolation policy.
     * @param transactionId the transaction id, not null.
     */
    void startTransaction(String transactionId);

    /**
     * Get the transactional context for the given transaction ID.
     * @param transactionID the transaction ID, not null.
     * @return the transactional context, or null, if no such cointext is present.
     */
    ConfigChangeContext getConfigChangeContext(String transactionID);
}
