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
import org.apache.tamaya.spi.ConfigurationContextBuilder;
import org.apache.tamaya.spi.ConfigurationProviderSpi;
import org.apache.tamaya.spi.ServiceContextManager;

/**
 * Static access to the {@link org.apache.tamaya.Configuration} for the very application.
 */
public final class ConfigurationProvider {

    private static final ConfigurationProviderSpi PROVIDER_SPI = ServiceContextManager.getServiceContext()
            .getService(ConfigurationProviderSpi.class).get();

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
    public static ConfigurationContext getConfigurationContext() {
        return PROVIDER_SPI.getConfigurationContext();
    }

    /**
     * This method allows to replace the current {@link org.apache.tamaya.spi.ConfigurationContext} with a new
     * instance. This can be used to update the context with a new one, e.g. because some of the configuration
     * data has changed and should be updated. It is the responsibility of the ConfigurationProvider to trigger
     * corresponding update events for the current {@link org.apache.tamaya.Configuration}, so observing
     * listeners can do whatever is appropriate to react to any given configuration changes.
     *
     * @param context the new ConfigurationContext to be applied.
     * @throws java.lang.UnsupportedOperationException if the current provider is read-only and does not support
     *                                                 applying a new ConfigurationContext.
     */
    public static void setConfigurationContext(ConfigurationContext context) {
        PROVIDER_SPI.setConfigurationContext(context);
    }

    /**
     * Method that allows to determine if a new {@link org.apache.tamaya.spi.ConfigurationContext} can be applied
     * programmatically.
     * @see #setConfigurationContext(org.apache.tamaya.spi.ConfigurationContext)
     * @return true, if {@link #setConfigurationContext(org.apache.tamaya.spi.ConfigurationContext)} is supported
     * by the current implementation.
     */
    public static boolean isConfigurationContextSettable() {
        return PROVIDER_SPI.isConfigurationContextSettable();
    }

    /**
     * Create a new {@link org.apache.tamaya.spi.ConfigurationContextBuilder} instance. This method creates
     * a new builder instance that is not related to any concrete {@link org.apache.tamaya.spi.ConfigurationContext}.
     * You can use {@link #setConfigurationContext(org.apache.tamaya.spi.ConfigurationContext)} to change the
     * current configuration context.
     *
     * @return a new, empty {@link org.apache.tamaya.spi.ConfigurationContextBuilder}, never null.
     * @see #setConfigurationContext(org.apache.tamaya.spi.ConfigurationContext)
     * @see org.apache.tamaya.spi.ConfigurationContext
     */
    public static ConfigurationContextBuilder getConfigurationContextBuilder() {
        return PROVIDER_SPI.getConfigurationContextBuilder();
    }

}
