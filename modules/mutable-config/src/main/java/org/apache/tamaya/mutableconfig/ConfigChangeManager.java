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
import org.apache.tamaya.mutableconfig.spi.ConfigChangeManagerSpi;
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
import java.util.UUID;


/**
 * Accessor for creating {@link ConfigChangeRequest} instances to change and commit configuration.
 */
public final class ConfigChangeManager {

    /** Singleton constructor. */
    private ConfigChangeManager(){}

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
    public static ConfigChangeRequest createChangeRequest(String... configurationTargets){
        try {
            URI[] uris = new URI[configurationTargets.length];
            for (int i = 0; i < configurationTargets.length; i++) {
                uris[i] = new URI(configurationTargets[i]);
            }
            return createChangeRequest(uris);
        } catch(URISyntaxException e){
            throw new ConfigException("Invalid URIs enocuntered in " + Arrays.toString(configurationTargets));
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
    public static ConfigChangeRequest createChangeRequest(URI... configurationTargets){
        if(Objects.requireNonNull(configurationTargets).length==0){
            throw new IllegalArgumentException("At least one target URI is required.");
        }
        List<ConfigChangeRequest> targets = new ArrayList<>();
        for(ConfigChangeManagerSpi spi:ServiceContextManager.getServiceContext()
                .getServices(ConfigChangeManagerSpi.class)){
            for(URI target:configurationTargets) {
                ConfigChangeRequest req = spi.createChangeRequest(target);
                if (req != null) {
                    targets.add(req);
                }
            }
        }
        if(targets.isEmpty()) {
            throw new ConfigException("Not an editable configuration target for: " +
                    Arrays.toString(configurationTargets));
        }
        if(targets.size()==1){
            return targets.get(0);
        }
        return new CompoundConfigChangeRequest(targets);
    }


    /**
     * Compound request that contains internally multiple change requests. Changes are committed to all members.
     */
    private static final class CompoundConfigChangeRequest implements ConfigChangeRequest{

        private final List<ConfigChangeRequest> targets;
        private final List<URI> backendURIs = new ArrayList<>();
        private String requestId = UUID.randomUUID().toString();

        CompoundConfigChangeRequest(List<ConfigChangeRequest> targets){
            this.targets = targets;
            for(ConfigChangeRequest req:targets){
                req.setRequestId(requestId);
                backendURIs.addAll(req.getBackendURIs());
            }
        }

        @Override
        public String getRequestID() {
            return requestId;
        }

        @Override
        public List<URI> getBackendURIs() {
            return Collections.unmodifiableList(backendURIs);
        }

        @Override
        public boolean isWritable(String keyExpression) {
            for(ConfigChangeRequest req:targets){
                if(req.isWritable(keyExpression)){
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean isRemovable(String keyExpression) {
            for(ConfigChangeRequest req:targets){
                if(req.isRemovable(keyExpression)){
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean exists(String keyExpression) {
            for(ConfigChangeRequest req:targets){
                if(req.exists(keyExpression)){
                    return true;
                }
            }
            return false;
        }

        @Override
        public ConfigChangeRequest put(String key, String value) {
            for(ConfigChangeRequest req:targets){
                if(req.isWritable(key)){
                    req.put(key, value);
                }
            }
            return this;
        }

        @Override
        public ConfigChangeRequest putAll(Map<String, String> properties) {
            for(ConfigChangeRequest req:targets){
                for(Map.Entry<String,String> en:properties.entrySet()) {
                    if (req.isWritable(en.getKey())) {
                        req.put(en.getKey(), en.getValue());
                    }
                }
            }
            return this;
        }

        @Override
        public ConfigChangeRequest remove(String... keys) {
            for(ConfigChangeRequest req:targets){
                for(String key:keys){
                    if (req.isRemovable(key)) {
                        req.remove(key);
                    }
                }
            }
            return this;
        }

        @Override
        public ConfigChangeRequest remove(Collection<String> keys) {
            for(ConfigChangeRequest req:targets){
                for(String key:keys){
                    if (req.isRemovable(key)) {
                        req.remove(key);
                    }
                }
            }
            return this;
        }

        @Override
        public void commit() {
            for(ConfigChangeRequest req:targets){
                req.commit();
            }
        }

        @Override
        public void cancel() {
            for(ConfigChangeRequest req:targets){
                req.cancel();
            }
        }

        @Override
        public boolean isClosed() {
            for(ConfigChangeRequest req:targets){
                if(req.isClosed()){
                    return true;
                }
            }
            return false;
        }

        @Override
        public void setRequestId(String requestId) {
            if(isClosed()){
                throw new IllegalStateException("Cannot set requestId, already closed.");
            }
            this.requestId = Objects.requireNonNull(requestId);
        }
    }

}
