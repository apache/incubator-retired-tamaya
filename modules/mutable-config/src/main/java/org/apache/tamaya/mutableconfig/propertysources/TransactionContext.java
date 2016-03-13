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
package org.apache.tamaya.mutableconfig.propertysources;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Transactional context used for managing configuration changes within an {@link AbstractMutablePropertySource}.
 */
public final class TransactionContext{
    /**
     * The transaction id.
     */
    private UUID transactionId;
    /**
     * The starting point.
     */
    private long startedAt = System.currentTimeMillis();
    /**
     * The Properties.
     */
    private final Map<String,String> addedProperties = new HashMap<>();
    /**
     * The Removed.
     */
    private final Set<String> removedProperties = new HashSet<>();

    /**
     * Creates a new instance bound to the given transaction.
     * @param transactionID the transaction ID, not null.
     */
    public TransactionContext(UUID transactionID){
        this.transactionId = Objects.requireNonNull(transactionID);
    }

    /**
     * Get the corresppnding transaction ID of this instance.
     * @return the transaction ID, never null.
     */
    public UUID getTransactionID(){
        return transactionId;
    }

    /**
     * Timestamp in UTC millis, when this transaction (context) was created.
     * @return the timestamp in millis.
     */
    public long getStartedAt(){
        return startedAt;
    }

    /**
     * Get an unmodifiable key/value map of properties added or updated.
     * @return an unmodifiable key/value map of properties added or updated, never null.
     */
    public Map<String,String> getAddedProperties(){
        return Collections.unmodifiableMap(addedProperties);
    }

    /**
     * Get an unmodifiable key set of properties removed.
     * @return an unmodifiable key set of properties removed, never null.
     */
    public Set<String> getRemovedProperties(){
        return Collections.unmodifiableSet(removedProperties);
    }

    /**
     * Adds/updates a new key/value pair.
     * @param key the key, not null.
     * @param value the value, not null.
     */
    public void put(String key, String value) {
        this.addedProperties.put(key, value);
    }

    /**
     * Add/updated multiple key/values.
     * @param properties the keys and values to be added/updated, not null.
     */
    public void putAll(Map<String, String> properties) {
        this.addedProperties.putAll(properties);
    }

    /**
     * Remove all the given keys, ir present.
     * @param keys the keys to be removed, not null.
     */
    public void removeAll(Collection<String> keys) {
        this.removedProperties.addAll(keys);
    }

    /**
     * Allows easily to check if no additions/changes an no removals are present in the current transaction.
     * @return true, if not actions have to be committed.
     */
    public boolean isEmpty() {
        return this.addedProperties.isEmpty() && this.removedProperties.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TransactionContext)) return false;

        TransactionContext that = (TransactionContext) o;

        return transactionId.equals(that.transactionId);

    }

    @Override
    public int hashCode() {
        return transactionId.hashCode();
    }

    @Override
    public String toString() {
        return "TransactionContext{" +
                "addedProperties=" + addedProperties +
                ", transactionId=" + transactionId +
                ", startedAt=" + startedAt +
                ", removedProperties=" + removedProperties +
                '}';
    }

}