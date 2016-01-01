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
package org.apache.tamaya.resource;

import org.apache.tamaya.ConfigException;
import org.apache.tamaya.spi.ServiceContextManager;

/**
 * Singleton Accessor for accessing the current {@link org.apache.tamaya.resource.ResourceResolver} instance.
 */
public final class ConfigResources {

    /**
     * Singleton constructor.
     */
    private ConfigResources(){}

    /**
     * <p>Access the current ResourceResolver.</p>
     *
     * @throws ConfigException if no ResourceResolver is available (should not happen).
     *
     * @return the current ResourceResolver instance, never null.
     */
    public static ResourceResolver getResourceResolver() throws ConfigException {
        ResourceResolver resolver = ServiceContextManager.getServiceContext().getService(ResourceResolver.class);
        if (resolver == null) {
            throw new ConfigException("ResourceResolver not available.");
        }
        return resolver;
    }
}
