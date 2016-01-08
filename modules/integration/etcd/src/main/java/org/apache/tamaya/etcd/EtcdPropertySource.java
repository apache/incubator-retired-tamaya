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
package org.apache.tamaya.etcd;

import org.apache.tamaya.spi.PropertySource;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Propertysource that is reading configuration from a configured etcd endpoint. Setting
 * {@code etcd.prefix} as system property maps the etcd based onfiguration
 * to this prefix namespace. Etcd servers are configured as {@code etcd.server.urls} system or environment property.
 */
public class EtcdPropertySource implements PropertySource{
    private static final Logger LOG = Logger.getLogger(EtcdPropertySource.class.getName());
    private List<EtcdAccessor> etcdBackends = new ArrayList<>();
    private String prefix = System.getProperty("tamaya.etcd.prefix", "");

    public EtcdPropertySource(){
        int timeout = 2;
        String val = System.getProperty("tamaya.etcd.timeout");
        if(val == null){
            val = System.getenv("tamaya.etcd.timeout");
        }
        if(val!=null){
            timeout = Integer.parseInt(val);
        }
        String serverURLs = System.getProperty("tamaya.etcd.server.urls");
        if(serverURLs==null){
            serverURLs = System.getenv("tamaya.etcd.server.urls");
        }
        if(serverURLs==null){
            serverURLs = "http://127.0.0.1:4001";
        }
        for(String url:serverURLs.split("\\,")) {
            try{
                etcdBackends.add(new EtcdAccessor(url.trim(), timeout));
                LOG.info("Using etcd endoint: " + url);
            } catch(Exception e){
                LOG.log(Level.SEVERE, "Error initializing etcd accessor for URL: " + url, e);
            }
        }
    }

    @Override
    public int getOrdinal() {
        String configuredOrdinal = get(TAMAYA_ORDINAL);
        if(configuredOrdinal!=null){
            try{
                return Integer.parseInt(configuredOrdinal);
            } catch(Exception e){
                Logger.getLogger(getClass().getName()).log(Level.WARNING,
                        "Configured Ordinal is not an int number: " + configuredOrdinal, e);
            }
        }
        return getDefaultOrdinal();
    }

    /**
     * Returns the  default ordinal used, when no ordinal is set, or the ordinal was not parseable to an int value.
     * @return the  default ordinal used, by default 0.
     */
    public int getDefaultOrdinal(){
        return 100;
    }

    @Override
    public String getName() {
        return "etcd";
    }

    @Override
    public String get(String key) {
        // check prefix, if key does not start with it, it is not part of our name space
        // if so, the prefix part must be removed, so etcd can resolve without it
        if(!key.startsWith(prefix)){
            return null;
        } else{
            key = key.substring(prefix.length());
        }
        Map<String,String> props = null;
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
        for(EtcdAccessor accessor:etcdBackends){
            try{
                props = accessor.get(key);
                if(!props.containsKey("_ERROR")) {
                    // No repfix mapping necessary here, since we only access/return the value...
                    return props.get(key);
                } else{
                    LOG.log(Level.FINE, "etcd error on " + accessor.getUrl() + ": " + props.get("_ERROR"));
                }
            } catch(Exception e){
                LOG.log(Level.FINE, "etcd access failed on " + accessor.getUrl() + ", trying next...", e);
            }
        }
        return null;
    }

    @Override
    public Map<String, String> getProperties() {
        if(etcdBackends.isEmpty()){
            for(EtcdAccessor accessor:etcdBackends){
                try{
                    Map<String, String> props = accessor.getProperties("");
                    if(!props.containsKey("_ERROR")) {
                        return mapPrefix(props);
                    } else{
                        LOG.log(Level.FINE, "etcd error on " + accessor.getUrl() + ": " + props.get("_ERROR"));
                    }
                } catch(Exception e){
                    LOG.log(Level.FINE, "etcd access failed on " + accessor.getUrl() + ", trying next...", e);
                }
            }
        }
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
        return true;
    }
}
