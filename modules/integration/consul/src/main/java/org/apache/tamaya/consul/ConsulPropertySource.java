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
package org.apache.tamaya.consul;

import com.google.common.base.Optional;
import com.google.common.net.HostAndPort;
import com.orbitz.consul.Consul;
import com.orbitz.consul.KeyValueClient;
import com.orbitz.consul.model.kv.Value;
import org.apache.tamaya.mutableconfig.propertysources.AbstractMutablePropertySource;
import org.apache.tamaya.mutableconfig.propertysources.TransactionContext;
import org.apache.tamaya.spi.PropertyValue;
import org.apache.tamaya.spi.PropertyValueBuilder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Propertysource that is reading configuration from a configured consul endpoint. Setting
 * {@code consul.prefix} as system property maps the consul based onfiguration
 * to this prefix namespace. Consul servers are configured as {@code consul.urls} system or environment property.
 */
public class ConsulPropertySource extends AbstractMutablePropertySource {
    private static final Logger LOG = Logger.getLogger(ConsulPropertySource.class.getName());

    private String prefix = System.getProperty("tamaya.consul.prefix", "");


    @Override
    public int getOrdinal() {
        PropertyValue configuredOrdinal = get(TAMAYA_ORDINAL);
        if(configuredOrdinal!=null){
            try{
                return Integer.parseInt(configuredOrdinal.getValue());
            } catch(Exception e){
                Logger.getLogger(getClass().getName()).log(Level.WARNING,
                        "Configured Ordinal is not an int number: " + configuredOrdinal, e);
            }
        }
        return getDefaultOrdinal();
    }

    /**
     * Returns the  default ordinal used, when no ordinal is set, or the ordinal was not parseable to an int value.
     * @return the  default ordinal used, by default 1000.
     */
    public int getDefaultOrdinal(){
        return 1000;
    }

    @Override
    public String getName() {
        return "consul";
    }

    @Override
    public PropertyValue get(String key) {
        // check prefix, if key does not start with it, it is not part of our name space
        // if so, the prefix part must be removedProperties, so etcd can resolve without it
        if(!key.startsWith(prefix)){
            return null;
        } else{
            key = key.substring(prefix.length());
        }
        String reqKey = key;
        if(key.startsWith("_")){
            reqKey = key.substring(1);
            if(reqKey.endsWith(".createdIndex")){
                reqKey = reqKey.substring(0,reqKey.length()-".createdIndex".length());
            } else if(reqKey.endsWith(".modifiedIndex")){
                reqKey = reqKey.substring(0,reqKey.length()-".modifiedIndex".length());
            } else if(reqKey.endsWith(".ttl")){
                reqKey = reqKey.substring(0,reqKey.length()-".ttl".length());
            } else if(reqKey.endsWith(".expiration")){
                reqKey = reqKey.substring(0,reqKey.length()-".expiration".length());
            } else if(reqKey.endsWith(".source")){
                reqKey = reqKey.substring(0,reqKey.length()-".source".length());
            }
        }
        for(HostAndPort hostAndPort: ConsulBackends.getConsulBackends()){
            try{
                Consul consul = Consul.builder().withHostAndPort(hostAndPort).build();
                KeyValueClient kvClient = consul.keyValueClient();
                Optional<Value> valueOpt = kvClient.getValue(reqKey);
                if(!valueOpt.isPresent()) {
                    LOG.log(Level.FINE, "key not found in consul: " + reqKey);
                }else{
                    // No repfix mapping necessary here, since we only access/return the value...
                    Value value = valueOpt.get();
                    Map<String,String> props = new HashMap<>();
                    props.put(reqKey+".createIndex", String.valueOf(value.getCreateIndex()));
                    props.put(reqKey+".modifyIndex", String.valueOf(value.getModifyIndex()));
                    props.put(reqKey+".lockIndex", String.valueOf(value.getLockIndex()));
                    props.put(reqKey+".flags", String.valueOf(value.getFlags()));
                    return new PropertyValueBuilder(key, value.getValue().get(), getName()).setContextData(props).build();
                }
            } catch(Exception e){
                LOG.log(Level.FINE, "etcd access failed on " + hostAndPort + ", trying next...", e);
            }
        }
        return null;
    }

    @Override
    public Map<String, String> getProperties() {
//        for(HostAndPort hostAndPort: ConsulBackends.getConsulBackends()){
//            try{
//                Consul consul = Consul.builder().withHostAndPort(hostAndPort).build();
//                KeyValueClient kvClient = consul.keyValueClient();
//                Optional<Value> valueOpt = kvClient.getValue(reqKey);
//                try{
//                    Map<String, String> props = kvClient.getProperties("");
//                    if(!props.containsKey("_ERROR")) {
//                        return mapPrefix(props);
//                    } else{
//                        LOG.log(Level.FINE, "consul error on " + hostAndPort + ": " + props.get("_ERROR"));
//                    }
//                } catch(Exception e){
//                    LOG.log(Level.FINE, "consul access failed on " + hostAndPort + ", trying next...", e);
//                }
//            } catch(Exception e){
//                LOG.log(Level.FINE, "etcd access failed on " + hostAndPort + ", trying next...", e);
//            }
//        }
        return Collections.emptyMap();
    }

    private Map<String, String> mapPrefix(Map<String, String> props) {
        if(prefix.isEmpty()){
            return props;
        }
        Map<String,String> map = new HashMap<>();
        for(Map.Entry<String,String> entry:props.entrySet()){
            if(entry.getKey().startsWith("_")){
                map.put("_" + prefix + entry.getKey().substring(1), entry.getValue());
            } else{
                map.put(prefix+ entry.getKey(), entry.getValue());
            }
        }
        return map;
    }

    @Override
    public boolean isScannable() {
        return false;
    }

    @Override
    protected void commitInternal(TransactionContext context) {
        for(HostAndPort hostAndPort: ConsulBackends.getConsulBackends()){
            try{
                Consul consul = Consul.builder().withHostAndPort(hostAndPort).build();
                KeyValueClient kvClient = consul.keyValueClient();

                for(String k: context.getRemovedProperties()){
                    try{
                        kvClient.deleteKey(k);
                    } catch(Exception e){
                        LOG.info("Failed to remove key from consul: " + k);
                    }
                }
                for(Map.Entry<String,String> en:context.getAddedProperties().entrySet()){
                    String key = en.getKey();
                    try{
                        kvClient.putValue(key,en.getValue());
                    }catch(Exception e) {
                        LOG.info("Failed to add key to consul: " + en.getKey() + "=" + en.getValue());
                    }
                }
                // success: stop here
                break;
            } catch(Exception e){
                LOG.log(Level.FINE, "consul access failed on " + hostAndPort + ", trying next...", e);
            }
        }
    }
}
