/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tamaya.spi;

import org.apache.tamaya.Configuration;

/**
 * SPI that must be implemented to provide the component that manages all {@link org.apache.tamaya.Configuration}
 * instances in a system. In SE this may be a true singleton containing exact one {@link org.apache.tamaya.Configuration}
 * instance, whereas in Java EE and other more complex environments instances may be returned depending the current
 * runtime context.
 */
public interface ConfigurationProviderSpi {

    /**
     * Access the current {@link org.apache.tamaya.Configuration}.
     *
     * @return the current {@link org.apache.tamaya.Configuration} instance, never null.
     */
    Configuration getConfiguration();

    /**
     * Create a {@link Configuration} instance using the given context. The configuration
     * created hereby must respect the artifacts provided by its context (property sources,
     * filters, converters, policies etc), including their ordering and significance.
     * @param context the context to be used, not {@code null}.
     * @return the corresponding configuration instance.
     */
    Configuration createConfiguration(ConfigurationContext context);

    /**
     * Creates a new {@link org.apache.tamaya.spi.ConfigurationContextBuilder} instance.
     *
     * @return a new {@link org.apache.tamaya.spi.ConfigurationContextBuilder}, never null.
     */
    ConfigurationContextBuilder getConfigurationContextBuilder();

    /**
     * This method allows to replace the current {@link org.apache.tamaya.Configuration} with a new
     * instance. This can be used to update the configuration with a new one, e.g. because some of the
     * data has changed and must be updated. It is the responsibility of the ConfigurationProvider to trigger
     * corresponding update events for the current {@link org.apache.tamaya.Configuration}.
     *
     * @param config the new Configuration to be applied.
     * @throws java.lang.UnsupportedOperationException if the current provider is read-only.
     */
    void setConfiguration(Configuration config);

    /**
     * Method that allows to determine if a new {@link org.apache.tamaya.Configuration} can be applied
     * programmatically.
     *
     * @return true, if {@link #setConfiguration(org.apache.tamaya.Configuration)} is supported
     * by the current implementation.
     * @see #setConfiguration(org.apache.tamaya.Configuration)
     */
    boolean isConfigurationSettable();

    /**
     * Get access to the current {@link ConfigurationContext}.
     *
     * @return the current {@link ConfigurationContext}, never null.
     * @deprecated Will be removed in favour of {@link Configuration#getContext()}.
     */
    @Deprecated
    default ConfigurationContext getConfigurationContext(){
        return getConfiguration().getContext();
    }

    /**
     * This method allows to replace the current {@link org.apache.tamaya.spi.ConfigurationContext} with a new
     * instance. This can be used to update the context with a new one, e.g. because some of the configuration
     * data has changed and must be updated. It is the responsibility of the ConfigurationProvider to trigger
     * corresponding update event for the current {@link org.apache.tamaya.spi.ConfigurationContext} or
     * {@link org.apache.tamaya.Configuration}.
     *
     * @param context the new ConfigurationContext to be applied.
     * @throws java.lang.UnsupportedOperationException if the current provider is read-only.
     * @deprecated use {@link #setConfiguration(Configuration)}
     */
    @Deprecated
    default void setConfigurationContext(ConfigurationContext context){
        setConfiguration(createConfiguration(context));
    }

    /**
     * Method that allows to determine if a new {@link org.apache.tamaya.spi.ConfigurationContext} can be applied
     * programmatically.
     *
     * @return true, if {@link #setConfigurationContext(org.apache.tamaya.spi.ConfigurationContext)} is supported
     * by the current implementation.
     * @see #setConfigurationContext(org.apache.tamaya.spi.ConfigurationContext)
     * @deprecated use {@link #isConfigurationSettable()}
     */
    @Deprecated
    default boolean isConfigurationContextSettable(){
        return isConfigurationSettable();
    }


}
