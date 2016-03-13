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
import org.apache.tamaya.mutableconfig.spi.MutablePropertySource;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;


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
     * Starts a new transaction, if necessary, and returns the transaction id. New transaction are, similar to Java EE,
     * bound to the current thread. As a consequences all properties added , updated or removed must be managed by
     * a corresponding context, isolated by thread. The {@link MutablePropertySource} get the right transaction id
     * passed, when writing (committing) any changes applied.
     * @return the transaction id, not null.
     */
    UUID startTransaction();

    /**
     * Commits the request. After a commit the change is not editable anymore. All changes applied will be written to
     * the corresponding configuration backend.
     *
     * NOTE that changes applied must not necessarily be visible in the current {@link Configuration} instance,
     * since visibility of changes also depends on the ordinals set on the {@link org.apache.tamaya.spi.PropertySource}s
     * configured.
     * @throws org.apache.tamaya.ConfigException if the request already has been committed or cancelled, or the commit fails.
     */
    void commitTransaction();

    /**
     * Rollback any changes leaving everything unchanged. This will rollback all changes applied since the last commit.
     */
    void rollbackTransaction();

    /**
     * Get the current transaction id.
     * @return the current transaction id, or null, if no transaction is active.
     */
    UUID getTransactionId();

    /**
     * Get the current autoCommit policy. AutoCommit will commit the transaction after each change applied.
     * @return the current autoCommit policy, by default false.
     */
    boolean getAutoCommit();

    /**
     * Set the {@link ChangePropagationPolicy}.
     * @see #getChangePropagationPolicy()
     * @param changePropagationPolicy the policy, not null.
     */
    void setChangePropagationPolicy(ChangePropagationPolicy changePropagationPolicy);

    /**
     * Access the active {@link ChangePropagationPolicy}.This policy controls how configuration changes are written/published
     * to the known {@link MutablePropertySource} instances of a {@link Configuration}.
     * @return he active {@link ChangePropagationPolicy}, never null.
     */
    ChangePropagationPolicy getChangePropagationPolicy();

    /**
     * Set the autoCommit policy to be used for this configuration instance.
     * @param autoCommit the new autoCommit policy.
     * @throws IllegalStateException when there are uncommitted changes.
     */
    void setAutoCommit(boolean autoCommit);


    /**
     * Identifies the configuration backend that are targeted by this instance and which are
     * also responsible for writing back the changes applied.
     *
     * @return the property sources identified, in order of their occurrence/priority (most significant first).
     */
    List<MutablePropertySource> getMutablePropertySources();

    /**
     * Checks if a configuration key is writable (or it can be added).
     *
     * @param keyExpression the key to be checked for write access (including creation), not null. Here this could also
     *                      be a regular expression, such "as a.b.c.*".
     * @return the boolean
     */
    boolean isWritable(String keyExpression);

    /**
     * Identifies the configuration backends that supports writing the given key(s).
     * @param keyExpression the key to be checked for write access (including creation), not null. Here this could also
     *                      be a regular expression, such "as a.b.c.*".
     * @return @return the property sources identified, in order of their occurrence/priority (most significant first).
     */
    List<MutablePropertySource> getPropertySourcesThatCanWrite(String keyExpression);

    /**
     * Checks if a configuration key is removable. This also implies that it is writable, but there might be writable
     * keys that cannot be removedProperties.
     *
     * @param keyExpression the keyExpression the key to be checked for write access (including creation), not null.
     *                      Here this could also
     *                      be a regular expression, such "as a.b.c.*".
     * @return the boolean
     */
    boolean isRemovable(String keyExpression);

    /**
     * Identifies the configuration backend that know the given key(s) and support removing it/them.
     * @param keyExpression the key to be checked for write access (including creation), not null. Here this could also
     *                      be a regular expression, such "as a.b.c.*".
     * @return @return the property sources identified, in order of their occurrence/priority (most significant first).
     */
    List<MutablePropertySource> getPropertySourcesThatCanRemove(String keyExpression);

    /**
     * Checks if any keys of the given type already exist in the write backend. <b>NOTE:</b> there may be backends that
     * are not able to support lookups with regular expressions. In most such cases you should pass the keys to
     * lookup explicitly.
     *
     * @param keyExpression the key to be checked for write access (including creation), not null. Here this could
     *                      also be a regular expression, such "as a.b.c.*".
     * @return true, if there is any key found matching the expression.
     */
    boolean isExisting(String keyExpression);

    /**
     * Identifies the configuration backend that know the given key(s).
     * @param keyExpression the key to be checked for write access (including creation), not null. Here this could also
     *                      be a regular expression, such "as a.b.c.*".
     * @return @return the property sources identified, in order of their occurrence/priority (most significant first).
     */
    List<MutablePropertySource> getPropertySourcesThatKnow(String keyExpression);

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
