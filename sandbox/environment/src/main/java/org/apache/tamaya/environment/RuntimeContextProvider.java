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
package org.apache.tamaya.environment;

import org.apache.tamaya.spi.ServiceContext;

import java.util.Map;

/**
 * Singleton accessor to the current {@link org.apache.tamaya.metamodel.environment.RuntimeContext}.
 */
public final class RuntimeContextProvider {

    private static final ContextSpi contextSpi = loadSpi();

    private static ContextSpi loadSpi(){
        return ServiceContext.getInstance().getSingleton(org.apache.tamaya.environment.spi.ContextSpi.class);
    }

    /**
     * Singleton constructor.
     */
    private RuntimeContextProvider(){}

    /**
     * Get the current {@link org.apache.tamaya.environment.RuntimeContextProvider}. The environment is used to determine the current runtime state, which
     * is important for returning the correct configuration.
     * @return the current Environment, never null.
     */
    public static RuntimeContext current(){
        return contextSpi.getCurrentContext();
    }

    /**
     * Get the current {@link org.apache.tamaya.environment.RuntimeContextProvider}. The environment is used to determine the current runtime state, which
     * is important for returning the correct configuration.
     * @return the current Environment, never null.
     */
    public static RuntimeContext root(){
        return contextSpi.getRootContext();
    }

}
