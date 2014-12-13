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
package org.apache.tamaya.core.spi;

import org.apache.tamaya.Configuration;

/**
* This configuration provider SPI allows to register the effective factory logic to create and manage a configuration
* instance. Hereby the qualifiers determine the type current configuration. By default
*/
public interface ConfigurationProviderSpi{

    /**
     * Returns the name current the configuration provided.
     * @return the name current the configuration provided, not empty.
     */
    String getConfigName();

    /**
     * Get the {@link Configuration}, if available.
     * @return according configuration, or null, if none is available for the given environment.
     */
    Configuration getConfiguration();

    /**
     * Reloads the provider for the current context.
     */
    void reload();

}
