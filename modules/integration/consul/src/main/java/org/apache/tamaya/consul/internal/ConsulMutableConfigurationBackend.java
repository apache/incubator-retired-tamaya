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
package org.apache.tamaya.consul.internal;

import com.google.common.net.HostAndPort;
import com.orbitz.consul.Consul;
import com.orbitz.consul.KeyValueClient;
import org.apache.tamaya.consul.ConsulBackends;
import org.apache.tamaya.consul.ConsulPropertySource;
import org.apache.tamaya.mutableconfig.spi.AbstractMutableConfigurationBackendSpi;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Change Request implementation based on consul services.
 */
class ConsulMutableConfigurationBackend extends AbstractMutableConfigurationBackendSpi {

    private static final Logger LOG = Logger.getLogger(ConsulMutableConfigurationBackend.class.getName());

    ConsulMutableConfigurationBackend(URI uri){
        super(uri, new ConsulPropertySource());
    }

    @Override
    public boolean isExisting(String keyExpression) {
        for(HostAndPort hostAndPort: ConsulBackends.getConsulBackends()){
            try{
                Consul consul = Consul.builder().withHostAndPort(hostAndPort).build();
                KeyValueClient kvClient = consul.keyValueClient();
                List<String> keys = kvClient.getKeys(keyExpression);
                return !keys.isEmpty();
            } catch(Exception e){
                LOG.log(Level.FINE, "consul access failed on " + hostAndPort + ", trying next...", e);
            }
        }
        return false;
    }


    @Override
    protected void commitInternal() {
        for(HostAndPort hostAndPort: ConsulBackends.getConsulBackends()){
            try{
                Consul consul = Consul.builder().withHostAndPort(hostAndPort).build();
                KeyValueClient kvClient = consul.keyValueClient();

                for(String k: getRemovedProperties()){
                    try{
                        kvClient.deleteKey(k);
                    } catch(Exception e){
                        LOG.info("Failed to remove key from consul: " + k);
                    }
                }
                for(Map.Entry<String,String> en:getAddedProperties().entrySet()){
                    String key = en.getKey();
                    try{
                        kvClient.putValue(key,en.getValue());
                    }catch(Exception e) {
                        LOG.info("Failed to add key to consul: " + en.getKey() + "=" + en.getValue());
                    }
                }
            } catch(Exception e){
                LOG.log(Level.FINE, "consul access failed on " + hostAndPort + ", trying next...", e);
            }
        }
    }

}
