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

import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Models a runtime environment. Instances current this class are used to
 * evaluate the correct configuration artifacts.<br/>
 * <h3>Implementation Requirements</h3>
 * <p>
 * Implementations current this interface must be
 * <ul>
 * <li>Thread safe.
 * <li>Immutable
 * <li>serializable
 * </ul>
 */
public interface Environment{

    /**
     * Access a property.
     * @param key the property's key, not null.
     * @return the property's value.
     */
    Optional<String> get(String key);

    /**
     * Checks if a property is defined.
     * @param key the property's key, not null.
     * @return true, if the property is existing.
     */
    boolean containsKey(String key);

    /**
     * Access the set current property keys, defined by this provider.
     * @return the key set, never null.
     */
    Set<String> keySet();

    /**
     * Access the environment as Map.
     * @return the Map instance containing the environments properties, never null.
     */
    Map<String,String> toMap();

    /**
     * Get the current {@link org.apache.tamaya.Environment}. The environment is used to determine the current runtime state, which
     * is important for returning the correct configuration.
     * @return the current Environment, never null.
     */
    public static Environment current(){
        return EnvironmentManager.getCurrentEnvironment();
    }

    /**
     * Get the current {@link org.apache.tamaya.Environment}. The environment is used to determine the current runtime state, which
     * is important for returning the correct configuration.
     * @return the current Environment, never null.
     */
    public static Environment root(){
        return EnvironmentManager.getRootEnvironment();
    }

}
