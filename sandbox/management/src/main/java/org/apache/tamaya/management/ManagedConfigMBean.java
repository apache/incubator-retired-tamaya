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
package org.apache.tamaya.management;

import java.util.Map;
import java.util.Set;

/**
 * Managed bean interface for accessing environment data.
 */
public interface ManagedConfigMBean {

    /**
     * Get a general description of the configuration (context) in place, in JSON format:
     * <pre>
     *     ConfigurationContext[gqContextClassName] {
     *         version = 2345-34334-2333-3434,
     *         config {
     *             key = "value",
     *             key2 = "value2"
     *             ...
     *         },
     *         filters = [...],
     *         converters{...},
     *         property-sources{...}
     *     }
     * </pre>
     *
     * @return a JSON formatted meta-information.
     */
    String getConfigurationInfo();


    /**
     * Accesses a configuration current a given type as Map.
     *
     * @return the current configuration map.
     * @throws org.apache.tamaya.ConfigException If the configuration is not available.
     */
    Map<String, String> getConfiguration();

    /**
     * Accesses a configuration values for current a given config area as Map.
     * @param area the target area key, not null.
     * @param recursive if set to false only direct child keys of the given area are returned.
     * @return the key/values found, including the recursive child values.
     * @throws org.apache.tamaya.ConfigException If the configuration is not yet loaded.
     */
    Map<String, String> getConfigurationArea(String area, boolean recursive);

    /**
     * Access the defined areas for a given configuration.
     * @return the areas defined (only returning the areas that contain properties).
     * @throws org.apache.tamaya.ConfigException If the configuration is not yet loaded
     */
    Set<String> getAreas();

    /**
     * Access the transitive areas for the current configuration.
     * @return the transitive areas defined.
     * @throws org.apache.tamaya.ConfigException If the configuration is not yet loaded
     */
    Set<String> getTransitiveAreas();

    /**
     * Allows to determine if an area is existing.
     * @param area the target area key, not null.
     * @return true, if such an area exists (the area may be empty).
     */
    boolean isAreaExisting(String area);

    /**
     * Allows to determine if an area is empty.
     * @param area the target area key, not null.
     * @return true, if such an area exists and is not empty.
     */
    default boolean isAreaEmpty(String area){
        return getConfigurationArea(area, true).isEmpty();
    }

}