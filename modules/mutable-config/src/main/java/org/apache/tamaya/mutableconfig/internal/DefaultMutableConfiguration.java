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
import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.mutableconfig.ChangePropagationPolicy;
import org.apache.tamaya.mutableconfig.MutableConfiguration;
import org.apache.tamaya.mutableconfig.spi.ConfigChangeRequest;
import org.apache.tamaya.mutableconfig.spi.MutablePropertySource;
import org.apache.tamaya.spi.ConfigurationContext;
import org.apache.tamaya.spi.PropertySource;

import java.util.ArrayList;
import java.util.Arrays;
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
    private ConfigChangeRequest changeRequest = new ConfigChangeRequest(UUID.randomUUID().toString());
    private final Configuration config;
    private ChangePropagationPolicy changePropagationPolicy;

    public DefaultMutableConfiguration(Configuration config, ChangePropagationPolicy changePropagationPolicy){
        this.config = Objects.requireNonNull(config);
        this.changePropagationPolicy = Objects.requireNonNull(changePropagationPolicy);
    }

    @Override
    public ChangePropagationPolicy getChangePropagationPolicy(){
        return changePropagationPolicy;
    }

    @Override
    public ConfigChangeRequest getConfigChangeRequest(){
        return changeRequest;
    }

    protected List<MutablePropertySource> getMutablePropertySources() {
        List<MutablePropertySource> result = new ArrayList<>();
        for(PropertySource propertySource:this.config.getContext().getPropertySources()) {
            if(propertySource instanceof  MutablePropertySource){
                result.add((MutablePropertySource)propertySource);
            }
        }
        return result;
    }


    @Override
    public MutableConfiguration put(String key, String value) {
        changeRequest.put(key, value);
        return this;
    }

    @Override
    public MutableConfiguration putAll(Map<String, String> properties) {
        changeRequest.putAll(properties);
        return this;
    }

    @Override
    public MutableConfiguration remove(String... keys) {
        changeRequest.removeAll(Arrays.asList(keys));
        return this;
    }


    @Override
    public void store() {
        this.changePropagationPolicy.applyChange(changeRequest, config.getContext().getPropertySources());
    }

    @Override
    public MutableConfiguration remove(Collection<String> keys) {
        for(MutablePropertySource target:getMutablePropertySources()) {
            changeRequest.removeAll(keys);
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
    public ConfigurationContext getContext() {
        return config.getContext();
    }

    private Collection<PropertySource> getPropertySources() {
        return this.config.getContext().getPropertySources();
    }

    @Override
    public String toString() {
        return "DefaultMutableConfiguration{" +
                "config=" + config +
                '}';
    }

}