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
     * Get access to the current {@link ConfigurationContext}.
     *
     * @return the current {@link ConfigurationContext}, never null.
     */
    ConfigurationContext getConfigurationContext();

}
