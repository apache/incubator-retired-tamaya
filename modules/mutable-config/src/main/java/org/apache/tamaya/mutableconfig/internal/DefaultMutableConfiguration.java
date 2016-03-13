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
package org.apache.tamaya.mutableconfig.internal;

import org.apache.tamaya.ConfigOperator;
import org.apache.tamaya.ConfigQuery;
import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;
import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.mutableconfig.ChangePropagationPolicy;
import org.apache.tamaya.mutableconfig.MutableConfiguration;
import org.apache.tamaya.mutableconfig.MutableConfigurationProvider;
import org.apache.tamaya.mutableconfig.spi.MutablePropertySource;
import org.apache.tamaya.spi.ConfigurationContext;
import org.apache.tamaya.spi.PropertySource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Logger;


/**
 * Default implementation of a {@link MutableConfiguration}.
 */
public class DefaultMutableConfiguration implements MutableConfiguration {
    private static final Logger LOG = Logger.getLogger(DefaultMutableConfiguration.class.getName());
    private final Configuration config;
    private ChangePropagationPolicy changePropagationPolicy =
            MutableConfigurationProvider.getApplyAllChangePolicy();
    private UUID transactionId;
    private boolean autoCommit = false;

    public DefaultMutableConfiguration(Configuration config){
        this.config = Objects.requireNonNull(config);
        this.autoCommit = false;
    }

    @Override
    public void setChangePropagationPolicy(ChangePropagationPolicy changePropagationPolicy){
        this.changePropagationPolicy = Objects.requireNonNull(changePropagationPolicy);
    }

    @Override
    public ChangePropagationPolicy getChangePropagationPolicy(){
        return changePropagationPolicy;
    }

    @Override
    public void setAutoCommit(boolean autoCommit) {
        if(transactionId!=null){
            throw new IllegalStateException("Cannot change autoCommit within a transaction, perform a " +
                    "commit or rollback first.");
        }
        this.autoCommit = autoCommit;
    }

    @Override
    public UUID getTransactionId() {
        return transactionId;
    }

    @Override
    public boolean getAutoCommit() {
        return autoCommit;
    }

    @Override
    public List<MutablePropertySource> getMutablePropertySources() {
        List<MutablePropertySource> result = new ArrayList<>();
        ConfigurationContext context = ConfigurationProvider.getConfigurationContext(this.config);
        for(PropertySource propertySource:context.getPropertySources()) {
            if(propertySource instanceof  MutablePropertySource){
                result.add((MutablePropertySource)propertySource);
            }
        }
        return result;
    }

    @Override
    public boolean isWritable(String keyExpression) {
        for(MutablePropertySource target:getMutablePropertySources()) {
            if( target.isWritable(keyExpression)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<MutablePropertySource> getPropertySourcesThatCanWrite(String keyExpression) {
        List<MutablePropertySource> result = new ArrayList<>();
        for(MutablePropertySource propertySource:getMutablePropertySources()) {
            if(propertySource.isWritable(keyExpression)){
                result.add(propertySource);
            }
        }
        return result;
    }

    @Override
        public boolean isRemovable(String keyExpression) {
            for(MutablePropertySource target:getMutablePropertySources()) {
                if( target.isRemovable(keyExpression)) {
                    return true;
                }
            }
            return false;
        }

    @Override
    public List<MutablePropertySource> getPropertySourcesThatCanRemove(String keyExpression) {
        List<MutablePropertySource> result = new ArrayList<>();
        for(MutablePropertySource propertySource:getMutablePropertySources()) {
            if(propertySource.isRemovable(keyExpression)){
                result.add(propertySource);
            }
        }
        return result;
    }

    @Override
    public boolean isExisting(String keyExpression) {
        for(MutablePropertySource target:getMutablePropertySources()) {
            if(target.get(keyExpression)!=null) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<MutablePropertySource> getPropertySourcesThatKnow(String keyExpression) {
        List<MutablePropertySource> result = new ArrayList<>();
        for(MutablePropertySource propertySource:getMutablePropertySources()) {
            if(propertySource.get(keyExpression)!=null){
                result.add(propertySource);
            }
        }
        return result;
    }

    @Override
    public MutableConfiguration put(String key, String value) {
        UUID taID = startTransaction();
        changePropagationPolicy.applyChange(getPropertySources(), taID, key, value);
        if(autoCommit){
            commitTransaction();
        }
        return this;
    }

    @Override
    public MutableConfiguration putAll(Map<String, String> properties) {
        UUID taID = startTransaction();
        changePropagationPolicy.applyChanges(getPropertySources(), taID, properties);
        if(autoCommit){
            commitTransaction();
        }
        return this;
    }

    @Override
    public MutableConfiguration remove(String... keys) {
        UUID taID = startTransaction();
        changePropagationPolicy.applyRemove(getPropertySources(), taID, keys);
        for(String key:keys){
            for(MutablePropertySource target:getMutablePropertySources()) {
                if (target.isRemovable(key)) {
                    target.remove(taID, key);
                }
            }
        }
        if(autoCommit){
            commitTransaction();
        }
        return this;
    }

    @Override
    public UUID startTransaction() {
        UUID taID = transactionId;
        if(taID!=null){
            return taID;
        }
        taID = UUID.randomUUID();
        transactionId = taID;
        try {
            for (MutablePropertySource target : getMutablePropertySources()) {
                target.startTransaction(taID);
            }
        }catch(Exception e){
            rollbackTransaction();
        }
        return taID;
    }

    @Override
    public void commitTransaction() {
        UUID taID = transactionId;
        if(taID==null){
            LOG.warning("No active transaction on this thread, ignoring commit.");
            return;
        }
        try {
            for (MutablePropertySource target : getMutablePropertySources()) {
                target.commitTransaction(taID);
            }
            this.transactionId = null;
        }catch(Exception e){
            rollbackTransaction();
        }
    }

    @Override
    public void rollbackTransaction() {
        UUID taID = transactionId;
        if(taID==null){
            LOG.warning("No active transaction on this thread, ignoring rollback.");
            return;
        }
        try {
            for (MutablePropertySource target : getMutablePropertySources()) {
                target.rollbackTransaction(taID);
            }
        }finally{
            this.transactionId = null;
        }
    }

    @Override
    public MutableConfiguration remove(Collection<String> keys) {
        UUID taID = startTransaction();
        for(String key:keys){
            for(MutablePropertySource target:getMutablePropertySources()) {
                if (target.isRemovable(key)) {
                    target.remove(taID, key);
                }
            }
        }
        if(autoCommit){
            commitTransaction();
        }
        return this;
    }

    @Override
    public String get(String key) {
        return this.config.get(key);
    }

    @Override
    public String getOrDefault(String key, String defaultValue) {
        return this.config.getOrDefault(key, defaultValue);
    }

    @Override
    public <T> T getOrDefault(String key, Class<T> type, T defaultValue) {
        return this.config.getOrDefault(key, type, defaultValue);
    }

    @Override
    public <T> T get(String key, Class<T> type) {
        return this.config.get(key, type);
    }

    @Override
    public <T> T get(String key, TypeLiteral<T> type) {
        return this.config.get(key, type);
    }

    @Override
    public <T> T getOrDefault(String key, TypeLiteral<T> type, T defaultValue) {
        return this.config.getOrDefault(key, type, defaultValue);
    }

        @Override
    public Map<String, String> getProperties() {
        return this.config.getProperties();
    }

    @Override
    public Configuration with(ConfigOperator operator) {
        return operator.operate(this);
    }

    @Override
    public <T> T query(ConfigQuery<T> query) {
        return query.query(this);
    }

    @Override
    public String toString() {
        return "DefaultMutableConfiguration{" +
                "config=" + config +
                ", autoCommit=" + autoCommit +
                '}';
    }

    private Collection<PropertySource> getPropertySources() {
        ConfigurationContext context = ConfigurationProvider.getConfigurationContext(this.config);
        return context.getPropertySources();
    }
}