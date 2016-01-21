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

import java.net.URI;
import java.util.Collection;
import java.util.Map;


/**
 * This interface extends the Configuration interface hereby adding methods to change configuration entries.
 * Hereby not all configuration entries are necessarily mutable, since some entries may be read from non
 * mutable areas of configuration. Of course, it is always possible to add a mutable shadow layer on top of all
 * configuration to enable whatever changes applied. The exact management and storage persistence algorithm should be
 * transparent.<br>
 * As a consequence clients should first check, using the corresponding methods, if entries are to edited or removed
 * actually are eligible for change/creation or removal.
 */
public interface ConfigChangeRequest {

    /**
     * Get the unique id of this change request (UUID).
     *
     * @return the unique id of this change request (UUID).
     */
    String getRequestID();

    /**
     * Identifies the configuration backends that participate in this request instance and which are
     * also responsible for writing back the changes applied.
     *
     * @return the backend URI, never null.
     */
    Collection<URI> getBackendURIs();

    /**
     * Checks if a configuration key is writable (or it can be added).
     *
     * @param keyExpression the key to be checked for write access (including creation), not null. Here this could also
     *                      be a regular expression, such "as a.b.c.*".
     * @return the boolean
     */
    boolean isWritable(String keyExpression);

    /**
     * Checks if a configuration key is removable. This also implies that it is writable, but there might be writable
     * keys that cannot be removed.
     *
     * @param keyExpression the keyExpression the key to be cheched for write access (including creation), not null.
     *                      Here this could also
     *                      be a regulat expression, such "as a.b.c.*".
     * @return the boolean
     */
    boolean isRemovable(String keyExpression);

    /**
     * Checks if any keys of the given type already exist in the backend. <b>NOTE:</b> there may be backends that
     * are not able to support lookups with regular expressions. In most such cases you should pass the keys to
     * lookup explicitly.
     *
     * @param keyExpression the key to be checked for write access (including creation), not null. Here this could
     *                      also be a regular expression, such "as a.b.c.*".
     * @return true, if there is any key found matching the expression.
     */
    boolean exists(String keyExpression);

    /**
     * Sets a property.
     *
     * @param key   the property's key, not null.
     * @param value the property's value, not null.
     * @return the former property value, or null.
     * @throws org.apache.tamaya.ConfigException if the key/value cannot be added, or the request is read-only.
     */
    ConfigChangeRequest put(String key, String value);

    /**
     * Puts all given configuration entries. This method should check that all given properties are
     * basically removable, as defined by #isWritable. If any of the passed keys is not writable during this initial
     * check, the operation should not perform any configuration changes and throw a {@link org.apache.tamaya.ConfigException}. If errors
     * occur afterwards, when the properties are effectively written back to the backends, the errors should be
     * collected and returned as part of the ConfigException payload. Nevertheless the operation should in that case
     * remove all entries as far as possible and abort the writing operation.
     *
     * @param properties the properties tobe written, not null.
     * @return the config change request
     * @throws org.apache.tamaya.ConfigException if any of the given properties could not be written, or the request is read-only.
     */
    ConfigChangeRequest putAll(Map<String, String> properties);

    /**
     * Removes all given configuration entries. This method should check that all given properties are
     * basically removable, as defined by #isRemovable. If any of the passed keys is not removable during this initial
     * check, the operation should not perform any configuration changes and throw a {@link org.apache.tamaya.ConfigException}. If errors
     * occur afterwards, when the properties are effectively written back to the backends, the errors should be
     * collected and returned as part of the ConfigException payload. Nevertheless the operation should in that case
     * remove all entries as far as possible and abort the writing operation.
     *
     * @param keys the property's keys to be removed, not null.
     * @return the config change request
     * @throws org.apache.tamaya.ConfigException if any of the given keys could not be removed, or the request is read-only.
     */
    ConfigChangeRequest remove(Collection<String> keys);

    /**
     * Removes all given configuration entries. This method should check that all given properties are
     * basically removable, as defined by #isRemovable. If any of the passed keys is not removable during this initial
     * check, the operation should not perform any configuration changes and throw a {@link org.apache.tamaya.ConfigException}. If errors
     * occur afterwards, when the properties are effectively written back to the backends, the errors should be
     * collected and returned as part of the ConfigException payload. Nevertheless the operation should in that case
     * remove all entries as far as possible and abort the writing operation.
     *
     * @param keys the property's keys to be removed, not null.
     * @return the config change request
     * @throws org.apache.tamaya.ConfigException if any of the given keys could not be removed, or the request is read-only.
     */
    ConfigChangeRequest remove(String... keys);

    /**
     * Commits the request. After a commit the change is not editable anymore. All changes applied will be written to
     * the corresponding configuration backend.
     * " @throws ConfigException if the request already has been committed or cancelled, or the commit fails.
     */
    void commit();

    /**
     * Cancel the given request, leaving everything unchanged. Cancelled requests are read-only and can not be used
     * for preparing/submitting any configuration changes.
     *
     * @throws org.apache.tamaya.ConfigException if the request already has been committed or cancelled.
     */
    void cancel();

    /**
     * Operation to check, if the current request is closed (either commited or cancelled). Closed requests are
     * read-only and can not be used for preparing/submitting any configuration changes.
     *
     * @return true, if this instance is closed.
     */
    boolean isClosed();

    /**
     * Method to set the request id to be used.
     * @param requestId the id to  be used, not null
     * @throws IllegalStateException if the request is already closed.
     */
    void setRequestId(String requestId);
}
