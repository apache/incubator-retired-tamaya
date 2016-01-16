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
package org.apache.tamaya.mutableconfig;

import org.apache.tamaya.ConfigException;
import org.apache.tamaya.mutableconfig.spi.ConfigChangeManagerSpi;
import org.apache.tamaya.spi.ServiceContextManager;

import java.net.URI;
import java.util.Objects;

/**
 * Accessor for creating {@link ConfigChangeRequest} instances to change and commite configuration.
 */
public final class ConfigChangeManager {

    /** Singleton constructor. */
    private ConfigChangeManager(){}

    /**
     * Creates a new change request for the given configurationSource
     * @return a new ChangeRequest
     * @throws org.apache.tamaya.ConfigException if the given configurationSource cannot be edited.
     */
    public static ConfigChangeRequest createChangeRequest(URI configurationSource){
        Objects.requireNonNull(configurationSource);
        for(ConfigChangeManagerSpi spi:ServiceContextManager.getServiceContext()
                .getServices(ConfigChangeManagerSpi.class)){
            ConfigChangeRequest req = spi.createChangeRequest(configurationSource);
            if(req!=null){
                return req;
            }
        }
        throw new ConfigException("Not an editable configuration source: " + configurationSource);
    }

}
