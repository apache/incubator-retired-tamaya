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

import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;
import org.apache.tamaya.mutableconfig.spi.ConfigurationChangeProviderSpi;
import org.apache.tamaya.spi.ServiceContextManager;

/**
 * Accessor for creating {@link ConfigChangeRequest} instances to change and commite configuration.
 */
public final class ConfigChangeProvider {

    /**
     * Creates a new change request for the given Configuration.
     * @param config the configuration.
     * @return a new ChangeRequest
     * @throws UnsupportedOperationException if no change providers are registered.
     */
    public static ConfigChangeRequest createChangeRequest(Configuration config){
        return ServiceContextManager.getServiceContext().getService(ConfigurationChangeProviderSpi.class)
                .createChangeRequest(config);
    }

    /**
     * Creates a new change request for the current Configuration.
     * @return a new ChangeRequest
     */
    public static ConfigChangeRequest createChangeRequest(){
        return createChangeRequest(ConfigurationProvider.getConfiguration());
    }

}
