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
package org.apache.tamaya.etcd.internal;

import org.apache.tamaya.etcd.EtcdAccessor;
import org.apache.tamaya.etcd.EtcdBackends;
import org.apache.tamaya.mutableconfig.spi.AbstractMutableConfigurationBackendSpiSpi;

import java.net.URI;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Change Request implementation based on etcd services. Etcd also supports a ttl to set values only for a defined
 * number of seconds {@code ttl}. This is also supported by this component by adding ttl as a key parameter, e.g.
 * {@code changeRequest.set("myTimedKey?ttl=30", "myValue");} will set a key {@code myTimedKey} valid only for
 * 30 seconds.
 */
class EtcdMutableConfigurationBackend extends AbstractMutableConfigurationBackendSpiSpi {

    private static final Logger LOG = Logger.getLogger(EtcdMutableConfigurationBackend.class.getName());

    EtcdMutableConfigurationBackend(URI uri){
        super(uri);
    }

    @Override
    public boolean isExisting(String keyExpression) {
        for(EtcdAccessor accessor: EtcdBackends.getEtcdBackends()){
            try{
                Map<String,String> props = accessor.get(keyExpression);
                if(!props.containsKey("_ERROR")) {
                    // No repfix mapping necessary here, since we only access/return the value...
                    return props.get(keyExpression)!=null;
                }
            } catch(Exception e){
                LOG.log(Level.FINE, "etcd access failed on " + accessor.getUrl() + ", trying next...", e);
            }
        }
        return false;
    }


    @Override
    protected void commitInternal() {
        for(EtcdAccessor accessor: EtcdBackends.getEtcdBackends()){
            try{
                for(String k: getRemovedProperties()){
                    Map<String,String> res = accessor.delete(k);
                    if(res.get("_ERROR")!=null){
                        LOG.info("Failed to remove key from etcd: " + k);
                    }
                }
                for(Map.Entry<String,String> en:getAddedProperties().entrySet()){
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
            } catch(Exception e){
                LOG.log(Level.FINE, "etcd access failed on " + accessor.getUrl() + ", trying next...", e);
            }
        }
    }

}
