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

import org.apache.tamaya.ConfigException;
import org.apache.tamaya.ConfigOperator;
import org.apache.tamaya.ConfigQuery;
import org.apache.tamaya.Configuration;
import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.mutableconfig.spi.AbstractMutableConfiguration;
import org.apache.tamaya.mutableconfig.spi.MutableConfigurationBackendSpi;
import org.apache.tamaya.mutableconfig.spi.MutableConfigurationBackendProviderSpi;
import org.apache.tamaya.spi.ServiceContextManager;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * Accessor for creating {@link MutableConfiguration} instances to change and commit configuration.
 */
public final class MutableConfigurationQuery implements ConfigQuery<MutableConfiguration> {

    /**
     * URIs used by this query instance to identify the backends to use for write operations.
     */
    private final List<MutableConfigurationBackendSpi> targets = new ArrayList<>();

    /** Singleton constructor. */
    private MutableConfigurationQuery(List<MutableConfigurationBackendSpi> targets){
        this.targets.addAll(targets);
    }

    @Override
    public MutableConfiguration query(Configuration config) {
        return new DefaultMutableConfiguration(config, targets);
    }

    /**
     * Creates a new change request for the given configurationSource
     *
     * @param configurationTargets the configuration targets (String to create URIs) to use to write the changes/config. By passing multiple
     *                             URIs you can write back changes into multiple configuration backends, e.g.
     *                             one for redistributing changes using multicast mechanism, a local property file
     *                             for failover as well as the shared etcd server.
     * @return a new ChangeRequest
     * @throws org.apache.tamaya.ConfigException if the given configurationSource cannot be edited.
     */
    public static MutableConfigurationQuery of(String... configurationTargets){
        try {
            URI[] uris = new URI[configurationTargets.length];
            for (int i = 0; i < configurationTargets.length; i++) {
                uris[i] = new URI(configurationTargets[i]);
            }
            return of(uris);
        } catch(URISyntaxException e){
            throw new ConfigException("Invalid URIs encountered in " + Arrays.toString(configurationTargets));
        }
    }

    /**
     * Creates a new change request for the given configurationSource
     *
     * @param configurationTargets the configuration targets to use to write the changes/config. By passing multiple
     *                             URIs you can write back changes into multiple configuration backends, e.g.
     *                             one for redistributing changes using multicast mechanism, a local property file
     *                             for failover as well as the shared etcd server.
     * @return a new ChangeRequest
     * @throws org.apache.tamaya.ConfigException if the given configurationSource cannot be edited.
     */
    public static MutableConfigurationQuery of(URI... configurationTargets){
        if(Objects.requireNonNull(configurationTargets).length==0){
            throw new IllegalArgumentException("At least one target URI is required.");
        }
        List<MutableConfigurationBackendSpi> targets = new ArrayList<>();
        for(MutableConfigurationBackendProviderSpi spi:ServiceContextManager.getServiceContext()
                .getServices(MutableConfigurationBackendProviderSpi.class)){
            for(URI target:configurationTargets) {
                MutableConfigurationBackendSpi req = spi.getBackend(target);
                if (req != null) {
                    targets.add(req);
                }
            }
        }
        if(targets.isEmpty()) {
            throw new ConfigException("Not an editable configuration target for: " +
                    Arrays.toString(configurationTargets));
        }
        return new MutableConfigurationQuery(targets);
    }


    /**
     * Compound request that contains internally multiple change requests. Changes are committed to all members.
     */
    private static final class DefaultMutableConfiguration extends AbstractMutableConfiguration
            implements MutableConfiguration {

        private final List<MutableConfigurationBackendSpi> targets;
        private final Configuration config;

        DefaultMutableConfiguration(Configuration config, List<MutableConfigurationBackendSpi> targets){
            this.targets = Objects.requireNonNull(targets);
            this.config = Objects.requireNonNull(config);
        }

        @Override
        public List<URI> getBackendURIs() {
            List<URI> result = new ArrayList<>(targets.size());
            for(MutableConfigurationBackendSpi backend: targets){
                result.add(backend.getBackendURI());
            }
            return Collections.unmodifiableList(result);
        }

        @Override
        public boolean isWritable(String keyExpression) {
            for(MutableConfigurationBackendSpi req:targets){
                if(req.isWritable(keyExpression)){
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean isRemovable(String keyExpression) {
            for(MutableConfigurationBackendSpi req:targets){
                if(req.isRemovable(keyExpression)){
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean isExisting(String keyExpression) {
            for(MutableConfigurationBackendSpi req:targets){
                if(req.isExisting(keyExpression)){
                    return true;
                }
            }
            return false;
        }

        @Override
        public MutableConfiguration put(String key, String value) {
            for(MutableConfigurationBackendSpi req:targets){
                if(req.isWritable(key)){
                    req.put(key, value);
                }
            }
            return super.put(key, value);
        }

        @Override
        public MutableConfiguration putAll(Map<String, String> properties) {
            for(MutableConfigurationBackendSpi req:targets){
                for(Map.Entry<String,String> en:properties.entrySet()) {
                    if (req.isWritable(en.getKey())) {
                        req.put(en.getKey(), en.getValue());
                    }
                }
            }
            return super.putAll(properties);
        }

        @Override
        public MutableConfiguration remove(String... keys) {
            for(MutableConfigurationBackendSpi req:targets){
                for(String key:keys){
                    if (req.isRemovable(key)) {
                        req.remove(key);
                    }
                }
            }
            return super.remove(keys);
        }

        @Override
        public MutableConfiguration remove(Collection<String> keys) {
            for(MutableConfigurationBackendSpi req:targets){
                for(String key:keys){
                    if (req.isRemovable(key)) {
                        req.remove(key);
                    }
                }
            }
            return super.remove(keys);
        }

        @Override
        protected void commitInternal() {
            for(MutableConfigurationBackendSpi req:targets){
                req.commit();
            }
        }

        @Override
        public String get(String key) {
            return config.get(key);
        }

        @Override
        public String getOrDefault(String key, String defaultValue) {
            return config.getOrDefault(key, defaultValue);
        }

        @Override
        public <T> T getOrDefault(String key, Class<T> type, T defaultValue) {
            return config.getOrDefault(key, type, defaultValue);
        }

        @Override
        public <T> T get(String key, Class<T> type) {
            return config.get(key, type);
        }

        @Override
        public <T> T get(String key, TypeLiteral<T> type) {
            return config.get(key, type);
        }

        @Override
        public <T> T getOrDefault(String key, TypeLiteral<T> type, T defaultValue) {
            return config.getOrDefault(key, type, defaultValue);
        }

        @Override
        public Map<String, String> getProperties() {
            return config.getProperties();
        }

        @Override
        public Configuration with(ConfigOperator operator) {
            return operator.operate(this);
        }

        @Override
        public <T> T query(ConfigQuery<T> query) {
            if(query instanceof MutableConfigurationQuery){
                throw new ConfigException("Cannot query a mutable configuration, already is one!");
            }
            return query.query(this);
        }

        @Override
        public String toString() {
            return "DefaultMutableConfiguration{" +
                    "config=" + config +
                    ", targets=" + targets +
                    '}';
        }
    }

}
