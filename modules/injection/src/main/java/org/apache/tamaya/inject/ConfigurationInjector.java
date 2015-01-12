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
package org.apache.tamaya.inject;

import org.apache.tamaya.spi.ServiceContext;


/**
 * Accessor interface for injection of configuration and configuration templates.
 */
public interface ConfigurationInjector {

    /**
     * Extract the configuration annotation config and registers it per class, for later reuse.
     * @param type the type to be configured.
     * @return the configured type registered.
     */
    void registerType(Class<?> type);

    /**
     * Configured the current instance and reigsterd necessary listener to forward config change events as
     * defined by the current annotations in place.
     * @param instance the instance to be configured
     */
    void configure(Object instance);

    /**
     * Get the current injector instance.
     * @return the current injector, not null.
     */
    public static ConfigurationInjector getInstance(){
        return ServiceContext.getInstance().getService(ConfigurationInjector.class).get();
    }

}
