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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Singleton that reads and stores the current etcd setup, especially the possible URLs to be used.
 */
public final class EtcdBackends {

    private static final Logger LOG = Logger.getLogger(EtcdBackends.class.getName());
    private static List<EtcdAccessor> etcdBackends = new ArrayList<>();

    static{
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

    private EtcdBackends(){}

    public static List<EtcdAccessor> getEtcdBackends(){
        return etcdBackends;
    }
}
