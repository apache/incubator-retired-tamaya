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
package org.apache.tamaya.examples.remote.client;

import org.apache.tamaya.remote.BaseRemotePropertySource;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Property Resource, which reads configuration dat from the (local) configuration server. Normally also the config
 * server should be configured, e.g. using system or environment properties.
 */
public class RemotePropertySource extends BaseRemotePropertySource{
    /** Current remote property source default ordinal. */
    private static final int REMOTE_ORDINAL = 15000;

    @Override
    public int getDefaultOrdinal(){
        return REMOTE_ORDINAL;
    }

    @Override
    protected Collection<URL> getAccessURLs() {
        try {
            String configServerUrl = System.getenv("CONFIG_SERVER");
            if(configServerUrl==null){
                configServerUrl = System.getProperty("configServer");
            }
            if(configServerUrl==null){
                configServerUrl = "http://localhost:8888/config?scope=CLIENT&scopeId={clientId}&format=application/json";
            }
            System.out.println("Reading config from " + configServerUrl.replace("{clientId}", Client.getClientId()));
            return Arrays.asList(new URL(configServerUrl.replace("{clientId}", Client.getClientId())));
        } catch (MalformedURLException e) {
            Logger.getLogger(getClass().getName()).log(Level.WARNING, "Failed to configure remote config location,", e);
            return Collections.emptySet();
        }
    }

}
