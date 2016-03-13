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

import org.apache.tamaya.mutableconfig.spi.MutablePropertySource;
import org.apache.tamaya.spisupport.BasePropertySource;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Base class for implementing a {@link MutablePropertySource}.
 */
public abstract class AbstractMutablePropertySource extends BasePropertySource
        implements MutablePropertySource {

    /**
     * Map with the curren transactions, identified by transactionId.
     */
    protected final Map<UUID, TransactionContext> transactions = new ConcurrentHashMap<>();

    /**
     * Constructor udsing zero' as default ordinal.
     */
    protected AbstractMutablePropertySource(){
        super();
    }

    /**
     * Constructor allow to pass the default ordinal to be used.
     * @param defaultOrdinal the default ordinal.
     */
    protected AbstractMutablePropertySource(int defaultOrdinal){
        super(defaultOrdinal);
    }

    /**
     * Get a list with property keys removed within the given transaction.
     * @param transactionId the transaction id, not null.
     * @return the removed property keys, never null.
     */
    protected final Set<String> getRemovedProperties(UUID transactionId) {
        TransactionContext ctx = this.transactions.get(transactionId);
        if(ctx!=null) {
            return ctx.getRemovedProperties();
        }
        return Collections.emptySet();
    }

    /**
     * Get a list with property keys added within the given transaction.
     * @param transactionId the transaction id, not null.
     * @return the added property keys, never null.
     */
    protected final Map<String,String> getAddedProperties(UUID transactionId) {
        TransactionContext ctx = this.transactions.get(transactionId);
        if(ctx!=null) {
            return ctx.getAddedProperties();
        }
        return Collections.emptyMap();
    }

    @Override
    public boolean isWritable(String keyExpression) {
        return true;
    }

    @Override
    public boolean isRemovable(String keyExpression) {
        return true;
    }

    @Override
    public final MutablePropertySource put(UUID transactionId, String key, String value) {
        TransactionContext ctx = this.transactions.get(transactionId);
        if(ctx==null) {
            throw new IllegalStateException("No such transaction: " + transactionId);
        }
        ctx.put(key, value);
        return this;
    }

    @Override
    public final MutablePropertySource putAll(UUID transactionId, Map<String, String> properties) {
        TransactionContext ctx = this.transactions.get(transactionId);
        if(ctx==null) {
            throw new IllegalStateException("No such transaction: " + transactionId);
        }
        ctx.putAll(properties);
        return this;
    }

    @Override
    public final MutablePropertySource remove(UUID transactionId, String... keys) {
        TransactionContext ctx = this.transactions.get(transactionId);
        if(ctx==null) {
            throw new IllegalStateException("No such transaction: " + transactionId);
        }
        ctx.removeAll(Arrays.asList(keys));
        return this;
    }

    @Override
    public final MutablePropertySource remove(UUID transactionId, Collection<String> keys) {
        TransactionContext ctx = this.transactions.get(transactionId);
        if(ctx==null) {
            throw new IllegalStateException("No such transaction: " + transactionId);
        }
        ctx.removeAll(keys);
        return this;
    }

    @Override
    public final void startTransaction(UUID transactionId) {
        TransactionContext ctx = this.transactions.get(transactionId);
        if(ctx==null) {
            this.transactions.put(transactionId, new TransactionContext(transactionId));
        }
    }

    @Override
    public final void commitTransaction(UUID transactionId) {
        TransactionContext ctx = this.transactions.remove(transactionId);
        if(ctx==null) {
            throw new IllegalStateException("No such transaction: " + transactionId);
        }
        commitInternal(ctx);
    }


    /**
     * Commit of the changes to the current property source. This is the last chance to get changes written back to the
     * property source. On return the transactional context will be removed.
     */
    protected abstract void commitInternal(TransactionContext context);

    @Override
    public final void rollbackTransaction(UUID transactionId) {
        this.transactions.remove(transactionId);
    }
}
