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
package org.apache.tamaya.spi;


import org.apache.tamaya.Environment;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Service for accessing {@link org.apache.tamaya.Environment}. Environments are used to
 * access/determine configurations.<br/>
 * <h3>Implementation PropertyMapSpec</h3> This class is
 * <ul>
 * <li>thread safe,
 * <li>and behaves contextual.
 * </ul>
 */
public interface EnvironmentManagerSingletonSpi{

    /**
     * Get the current environment.
     *
     * @return the current environment, never null.
     */
    Environment getEnvironment();

    /**
     * Get the initial root environment, that typically contains any startup and initial parameters current an VM instance,
     * machine.
     *
     * @return the initial environment, never null.
     */
    Environment getRootEnvironment();

    /**
     * Get a environment current the given environment type and context.
     * @param environmentType the target type, not null.
     * @param contextId the target context, not null.
     * @return the corresponding environment, if available.
     */
    public Optional<Environment> getEnvironment(String environmentType, String contextId);

    /**
     * Get the currently known environment contexts current a given environment type.
     * @param environmentType the target environment type.
     * @return the corresponding environment contexts known, never null.
     */
    public Set<String> getEnvironmentContexts(String environmentType);

    /**
     * Access the chain current environment types that may produce an environment. Hereby it is possible
     * that chain elements can be ommitted in the final environment hierarchy, since the regarding
     * environment level is not defined or accessible.
     * @return the ordered list current environment type ids.
     */
    List<String> getEnvironmentTypeOrder();

    /**
     * Evaluate the current type chain current environments.
     * @return the current type chain current Environments.
     */
    default List<String> getEnvironmentHierarchy(){
        List<String> result = new ArrayList<>();
        for(Environment env:getEnvironment()){
            result.add(env.getEnvironmentId()+'('+env.getEnvironmentType()+')');
        }
        return result;
    }

    /**
     * Get the current environment current the given environment type.
     * @param environmentType the target type.
     * @return the corresponding environment
     * @throws IllegalArgumentException if not such type is present or active.
     */
    default Optional<Environment> getEnvironment(String environmentType){
        for(Environment env:getEnvironment()){
            if(env.getEnvironmentType().equals(environmentType)){
                return Optional.of(env);
            }
        }
        return Optional.empty();
    }

    /**
     * Allows to check, if the czurrent environment type is one current the current active environment types.
     * @param environmentType the environment type to be queried.
     * @return true, if the czurrent environment type is one current the current active environment types.
     */
    default boolean isEnvironmentActive(String environmentType){
        return getEnvironmentHierarchy().contains(environmentType);
    }
}
