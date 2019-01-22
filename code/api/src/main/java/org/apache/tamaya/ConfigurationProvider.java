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

import org.apache.tamaya.spi.ConfigurationBuilder;
import org.apache.tamaya.spi.ConfigurationContext;
import org.apache.tamaya.spi.ConfigurationProviderSpi;
import org.apache.tamaya.spi.ServiceContextManager;

import java.util.logging.Logger;

/**
 * Static access to the {@link Configuration} of the whole application.
 * @deprecated Use static methods of {@link Configuration}
 */
@Deprecated
public final class ConfigurationProvider {

    private static final Logger LOG = Logger.getLogger(ConfigurationProvider.class.getName());

    private static ConfigurationProviderSpi spi() {
        ConfigurationProviderSpi spi = ServiceContextManager.getServiceContext()
                .getService(ConfigurationProviderSpi.class);
        if(spi==null){
            throw new IllegalStateException("ConfigurationProviderSpi not available.");
        }
        LOG.finest("TAMAYA Delegate    : " + spi.getClass().getName());
        LOG.info("TAMAYA Configuration : " + spi.getConfiguration(Thread.currentThread().getContextClassLoader()));
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
        return spi().getConfiguration(Thread.currentThread().getContextClassLoader());
    }

    /**
     * Access the current configuration.
     * @param classLoader the target classloader, not null.
     * @return the corresponding Configuration instance, never {@code null}.
     */
    public static Configuration getConfiguration(ClassLoader classLoader) {
        return spi().getConfiguration(classLoader);
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
     * This method allows replacement of the current default {@link org.apache.tamaya.Configuration} with a new
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
        setConfiguration(config, Thread.currentThread().getContextClassLoader());
    }

    /**
     * This method allows replacement of the current default {@link org.apache.tamaya.Configuration} with a new
     * instance. It is the responsibility of the ConfigurationProvider to trigger
     * corresponding update events for the current {@link org.apache.tamaya.Configuration}, so observing
     * listeners can do whatever is appropriate to react to any given configuration change.
     *
     * @param config the new Configuration to be applied, not {@code null}
     * @param classLoader the target classloader, not null.
     * @throws java.lang.UnsupportedOperationException if the current provider is read-only and
     *                                                 does not support
     *                                                 applying a new Configuration.
     */
    public static void setConfiguration(Configuration config, ClassLoader classLoader) {
        LOG.info("TAMAYA Applying new Configuration: " + config);
        spi().setConfiguration(config, classLoader);
    }

    /**
     * Create a new {@link ConfigurationBuilder} instance. This method creates
     * a new builder instance that is not related to any concrete {@link org.apache.tamaya.Configuration}.
     * You can use {@link Configuration#setCurrent(Configuration, ClassLoader)} to change the
     * current configuration.
     *
     * @return a new, empty {@link ConfigurationBuilder}, never null.
     * @see Configuration#setCurrent(Configuration)
     * @see Configuration#setCurrent(Configuration, ClassLoader)
     * @see org.apache.tamaya.spi.ConfigurationContext
     */
    public static ConfigurationBuilder getConfigurationBuilder() {
        return spi().getConfigurationBuilder();
    }

}
