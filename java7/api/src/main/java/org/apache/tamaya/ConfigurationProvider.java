/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tamaya;

import org.apache.tamaya.spi.ConfigurationContext;
import org.apache.tamaya.spi.ConfigurationProviderSpi;
import org.apache.tamaya.spi.ServiceContextManager;

/**
 * Static access to the {@link Configuration} for the very application.
 */
public final class ConfigurationProvider {

    private static final ConfigurationProviderSpi PROVIDER_SPI = loadSpi();

    private static ConfigurationProviderSpi loadSpi() {
        ConfigurationProviderSpi spi = ServiceContextManager.getServiceContext()
                .getService(ConfigurationProviderSpi.class);
        if(spi==null){
            throw new IllegalStateException("ConfigurationProviderSpi not available.");
        }
        return spi;
    }

    private ConfigurationProvider() {
        // just to prevent initialisation
    }

    /**
     * Access the current configuration.
     *
     * @return the corresponding Configuration instance, never null.
     */
    public static Configuration getConfiguration() {
        return PROVIDER_SPI.getConfiguration();
    }

    /**
     * Get access to the current ConfigurationContext.
     *
     * @return the current ConfigurationContext, never null.
     */
    public static ConfigurationContext getConfigurationContext(){
        return PROVIDER_SPI.getConfigurationContext();
    }

}
