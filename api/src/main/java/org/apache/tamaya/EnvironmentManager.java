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

import org.apache.tamaya.spi.Bootstrap;
import org.apache.tamaya.spi.EnvironmentManagerSingletonSpi;

import java.util.List;
import java.util.Optional;
import java.util.Set;


/**
 * Singleton accessor class for the current environment.
 */
final class EnvironmentManager{

    private static final EnvironmentManagerSingletonSpi environmentManagerSingletonSpi = loadContextProviderSpi();

    /**
     * Private singleton constructor.
     */
    private EnvironmentManager(){}

    /**
     * Method to load the environment SPI during initial load.
     * @return the EnvironmentProviderSpi SPI, never null.
     */
    private static EnvironmentManagerSingletonSpi loadContextProviderSpi(){
        return Bootstrap.getService(EnvironmentManagerSingletonSpi.class);
    }

    /**
     * Get the current {@link Environment}. The environment is used to determine the current runtime state, which
     * is important for returning the correct configuration.
     * @return the current Environment, never null.
     */
    public static Environment getEnvironment(){
        return Optional.ofNullable(environmentManagerSingletonSpi).orElseThrow(
                () -> new IllegalStateException("No SPI loaded.")
        ).getEnvironment();
    }

    /**
     * Get the current root (startup/machine/VM) {@link Environment}.
     * @return the current root Environment, never null.
     */
    public static Environment getRootEnvironment(){
        return Optional.ofNullable(environmentManagerSingletonSpi).orElseThrow(
                () -> new IllegalStateException("No SPI loaded.")
        ).getRootEnvironment();
    }

    /**
     * Evaluate the overall chain current possible environments.
     * @return the hierarchy chain current possible Environments.
     */
    public static List<String> getEnvironmentTypeOrder(){
        return Optional.ofNullable(environmentManagerSingletonSpi).orElseThrow(
                () -> new IllegalStateException("No SPI loaded.")
        ).getEnvironmentTypeOrder();
    }

    /**
     * Evaluate the current type chain current environments.
     * @return the current type chain current Environments.
     */
    public static List<String> getEnvironmentHierarchy(){
        return Optional.ofNullable(environmentManagerSingletonSpi).orElseThrow(
                () -> new IllegalStateException("No SPI loaded.")
        ).getEnvironmentHierarchy();
    }

    /**
     * Get the current environment current the given environment type.
     * @param environmentType the target type.
     * @return the corresponding environment
     * @throws IllegalArgumentException if not such type is present or active.
     */
    public static Optional<Environment> getEnvironment(String environmentType){
        return Optional.ofNullable(environmentManagerSingletonSpi).orElseThrow(
                () -> new IllegalStateException("No SPI loaded.")
        ).getEnvironment(environmentType);
    }

    /**
     * Get a environment current the given environment type and context.
     * @param environmentType the target type, not null.
     * @param contextId the target context, not null.
     * @return the corresponding environment, if available.
     */
    public static Optional<Environment> getEnvironment(String environmentType, String contextId){
        return Optional.ofNullable(environmentManagerSingletonSpi).orElseThrow(
                () -> new IllegalStateException("No SPI loaded.")
        ).getEnvironment(environmentType, contextId);
    }

    /**
     * Get the currently known environment contexts current a given environment type.
     * @param environmentType the target environment type.
     * @return the corresponding environment contexts known, never null.
     */
    public static Set<String> getEnvironmentContexts(String environmentType){
        return Optional.ofNullable(environmentManagerSingletonSpi).orElseThrow(
                () -> new IllegalStateException("No SPI loaded.")
        ).getEnvironmentContexts(environmentType);
    }

    /**
     * Allows to check, if the czurrent environment type is one current the current active environment types.
     * @param environmentType the environment type to be queried.
     * @return true, if the czurrent environment type is one current the current active environment types.
     */
    public static boolean isEnvironmentActive(String environmentType){
        return Optional.ofNullable(environmentManagerSingletonSpi).orElseThrow(
                () -> new IllegalStateException("No SPI loaded.")
        ).isEnvironmentActive(environmentType);
    }

}
