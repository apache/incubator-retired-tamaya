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

import com.google.common.net.HostAndPort;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Singleton that reads and stores the current consul setup, especially the possible host:ports to be used.
 */
public final class ConsulBackends {

    private static final Logger LOG = Logger.getLogger(ConsulBackends.class.getName());
    private static List<HostAndPort> consulBackends = new ArrayList<>();

    static{
        String serverURLs = System.getProperty("tamaya.consul.urls");
        if(serverURLs==null){
            serverURLs = System.getenv("tamaya.consul.urls");
        }
        if(serverURLs==null){
            serverURLs = "127.0.0.1:8300";
        }
        for(String url:serverURLs.split("\\,")) {
            try{
                consulBackends.add(HostAndPort.fromString(url.trim()));
                LOG.info("Using consul endoint: " + url);
            } catch(Exception e){
                LOG.log(Level.SEVERE, "Error initializing consul accessor for URL: " + url, e);
            }
        }
    }

    private ConsulBackends(){}

    public static List<HostAndPort> getConsulBackends(){
        return consulBackends;
    }
}
