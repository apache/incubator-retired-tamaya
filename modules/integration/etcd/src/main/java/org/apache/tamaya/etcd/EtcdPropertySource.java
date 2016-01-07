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

import java.net.MalformedURLException;
import java.util.Collections;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Propertysource that is reading configuration from a configured etcd endpoint.
 */
public class EtcdPropertySource implements PropertySource{

    private EtcdAccessor etcdBackend;

    public EtcdPropertySource(){
        try {
            etcdBackend = new EtcdAccessor();
        } catch (MalformedURLException e) {
            e.printStackTrace();
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
        if(etcdBackend!=null) {
            return etcdBackend.get(key).get(key);
        }
        return null;
    }

    @Override
    public Map<String, String> getProperties() {
        if(etcdBackend!=null) {
            return etcdBackend.getProperties("");
        }
        return Collections.emptyMap();
    }

    @Override
    public boolean isScannable() {
        return true;
    }
}
