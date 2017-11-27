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
package org.apache.tamaya;

import org.apache.tamaya.spi.*;

import java.util.logging.Logger;

/**
 * Static access to the {@link Configuration} of the whole application.
 */
public final class ConfigurationProvider {

    private static final Logger LOG = Logger.getLogger(ConfigurationProvider.class.getName());

    private static ConfigurationProviderSpi spi() {
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
     * @return the corresponding Configuration instance, never {@code null}.
     */
    public static Configuration getConfiguration() {
        return spi().getConfiguration();
    }

    /**
     * Creates a new configuration instance based on the given context.
     *
     * @param context the configuration context, not {@code null}.
     * @return a new Configuration instance, never {@code null}.
     */
    public static Configuration createConfiguration(ConfigurationContext context) {
        return spi().createConfiguration(context);
    }

    /**
     * Get access to the current ConfigurationContext.
     *
     * @return the current ConfigurationContext, never null.
     * @deprecated Use {@link Configuration#getContext()} instead.
     */
    @Deprecated
    public static ConfigurationContext getConfigurationContext() {
        return spi().getConfigurationContext();
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
     * @deprecated Use #setConfiguration(Configuration) instead of.
     */
    @Deprecated
    public static void setConfigurationContext(ConfigurationContext context) {
        spi().setConfigurationContext(context);
    }

    /**
     * This method allows to replace the current default {@link org.apache.tamaya.Configuration} with a new
     * instance. It is the responsibility of the ConfigurationProvider to trigger
     * corresponding update events for the current {@link org.apache.tamaya.Configuration}, so observing
     * listeners can do whatever is appropriate to react to any given configuration change.
     *
     * @param config the new Configuration to be applied, not {@code null}
     * @throws java.lang.UnsupportedOperationException if the current provider is read-only and
     *                                                 does not support
     *                                                 applying a new Configuration.
     */
    public static void setConfiguration(Configuration config) {
        LOG.info("TAMAYA Applying new Configuration: " + config);
        spi().setConfiguration(config);
    }

    /**
     * Create a new {@link ConfigurationBuilder} instance. This method creates
     * a new builder instance that is not related to any concrete {@link org.apache.tamaya.spi.ConfigurationContext}.
     * You can use {@link #setConfigurationContext(org.apache.tamaya.spi.ConfigurationContext)} to change the
     * current configuration context.
     *
     * @return a new, empty {@link ConfigurationBuilder}, never null.
     * @see #setConfigurationContext(org.apache.tamaya.spi.ConfigurationContext)
     * @see org.apache.tamaya.spi.ConfigurationContext
     * @deprecated Will be removed.
     */
    @Deprecated
    public static ConfigurationContextBuilder getConfigurationContextBuilder() {
        return spi().getConfigurationContextBuilder();
    }

    /**
     * Create a new {@link ConfigurationBuilder} instance. This method creates
     * a new builder instance that is not related to any concrete {@link org.apache.tamaya.spi.ConfigurationContext}.
     * You can use {@link #setConfigurationContext(org.apache.tamaya.spi.ConfigurationContext)} to change the
     * current configuration context.
     *
     * @return a new, empty {@link ConfigurationBuilder}, never null.
     * @see #setConfigurationContext(org.apache.tamaya.spi.ConfigurationContext)
     * @see org.apache.tamaya.spi.ConfigurationContext
     */
    public static ConfigurationBuilder getConfigurationBuilder() {
        return spi().getConfigurationBuilder();
    }

}
