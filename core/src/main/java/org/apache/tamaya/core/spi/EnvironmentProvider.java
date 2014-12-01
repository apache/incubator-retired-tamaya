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

import org.apache.tamaya.Environment;

import java.util.Set;

/**
 * SPI for components that define a concrete type of {@link org.apache.tamaya.Environment}.
 * The chain of environment providers determine the current {@link Environment} active
 * and its parent instances.
 * Created by Anatole on 14.10.2014.
 */
public interface EnvironmentProvider {

    /**
     * Get the environment type this provider is responsible for.
     * @return the environment.
     */
    String getEnvironmentType();

    /**
     * Evaluates if an environment is currently active.
     * @return
     */
    boolean isEnvironmentActive();

    /**
     * Access (or create) a new environment for the given context.
     * @param parentEnvironment the parent environment to b e set
     * @return the environment, or null.
     */
    Environment getEnvironment(Environment parentEnvironment);

    /**
     * Get all currently known environment contexts for this environment type.
     * @return all currently known environment contexts, never null. Environment
     * providers may prevent abritrary access to environment fromMap outside of the
     * regarding runtime context by just not including the context information
     * in this call's result.
     * @return all currently known environment contexts, never null.
     */
    Set<String> getEnvironmentContexts();
}
