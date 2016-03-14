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

import org.apache.tamaya.mutableconfig.propertysources.AbstractMutablePropertySource;
import org.apache.tamaya.mutableconfig.propertysources.ConfigChangeContext;
import org.apache.tamaya.spi.PropertyValue;
import org.apache.tamaya.spi.PropertyValueBuilder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Propertysource that is reading configuration from a configured etcd endpoint. Setting
 * {@code etcd.prefix} as system property maps the etcd based onfiguration
 * to this prefix namespace. Etcd servers are configured as {@code etcd.server.urls} system or environment property.
 * ETcd can be disabled by setting {@code tamaya.etcdprops.disable} either as env or system property.
 */
public class EtcdPropertySource extends AbstractMutablePropertySource{
    private static final Logger LOG = Logger.getLogger(EtcdPropertySource.class.getName());

    private String prefix = System.getProperty("tamaya.etcd.prefix", "");

    private final boolean disabled = evaluateDisabled();

    private boolean evaluateDisabled() {
        String value = System.getProperty("tamaya.etcdprops.disable");
        if(value==null){
            value = System.getenv("tamaya.etcdprops.disable");
        }
        if(value==null){
            return false;
        }
        return value.isEmpty() || Boolean.parseBoolean(value);
    }

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
     * @return the  default ordinal used, by default 0.
     */
    public int getDefaultOrdinal(){
        return 1000;
    }

    @Override
    public String getName() {
        return "etcd";
    }

    @Override
    public PropertyValue get(String key) {
        if(disabled){
            return null;
        }
        // check prefix, if key does not start with it, it is not part of our name space
        // if so, the prefix part must be removedProperties, so etcd can resolve without it
        if(!key.startsWith(prefix)){
            return null;
        } else{
            key = key.substring(prefix.length());
        }
        Map<String,String> props;
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
        for(EtcdAccessor accessor: EtcdBackends.getEtcdBackends()){
            try{
                props = accessor.get(reqKey);
                if(!props.containsKey("_ERROR")) {
                    // No repfix mapping necessary here, since we only access/return the value...
                    return new PropertyValueBuilder(key, props.get(reqKey), getName()).setContextData(props).build();
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
        if(disabled){
            return Collections.emptyMap();
        }
        if(!EtcdBackends.getEtcdBackends().isEmpty()){
            for(EtcdAccessor accessor: EtcdBackends.getEtcdBackends()){
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

    @Override
    protected void commitInternal(ConfigChangeContext context) {
        for(EtcdAccessor accessor: EtcdBackends.getEtcdBackends()){
            try{
                for(String k: context.getRemovedProperties()){
                    Map<String,String> res = accessor.delete(k);
                    if(res.get("_ERROR")!=null){
                        LOG.info("Failed to remove key from etcd: " + k);
                    }
                }
                for(Map.Entry<String,String> en:context.getAddedProperties().entrySet()){
                    String key = en.getKey();
                    Integer ttl = null;
                    int index = en.getKey().indexOf('?');
                    if(index>0){
                        key = en.getKey().substring(0, index);
                        String rawQuery = en.getKey().substring(index+1);
                        String[] queries = rawQuery.split("&");
                        for(String query:queries){
                            if(query.contains("ttl")){
                                int qIdx = query.indexOf('=');
                                ttl = qIdx>0?Integer.parseInt(query.substring(qIdx+1).trim()):null;
                            }
                        }
                    }
                    Map<String,String> res = accessor.set(key, en.getValue(), ttl);
                    if(res.get("_ERROR")!=null){
                        LOG.info("Failed to add key to etcd: " + en.getKey()  + "=" + en.getValue());
                    }
                }
                // success, stop here
                break;
            } catch(Exception e){
                LOG.log(Level.FINE, "etcd access failed on " + accessor.getUrl() + ", trying next...", e);
            }
        }
    }
}
