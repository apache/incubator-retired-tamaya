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

import org.apache.tamaya.mutableconfig.ConfigChangeRequest;
import org.apache.tamaya.mutableconfig.spi.ConfigChangeManagerSpi;

import java.net.URI;

/**
 * Created by atsticks on 15.01.16.
 */
public class MutableConfigSupport implements ConfigChangeManagerSpi{

    private URI backendURI;

    public MutableConfigSupport(){
        backendURI = URI.create("config:etcd");
    }

    @Override
    public ConfigChangeRequest createChangeRequest(URI uri) {
        if(backendURI.equals(uri)) {
            return new EtcdConfigChangeRequest(backendURI);
        }
        return null;
    }
}
