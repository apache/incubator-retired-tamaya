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


import org.apache.tamaya.ConfigException;
import org.apache.tamaya.core.properties.AggregationPolicy;

import java.util.Map;
import java.util.Set;

/**
 * Managed bean interface for accessing environment data.
 */
public interface ManagedConfigMBean {
    /**
     * Get the names of the configuration's defined.
     *
     * @return the names of the configuration's defined.
     */
    public Set<String> getConfigurationNames();

    /**
     * Get a general configuration info descriptor in JSON format for a configuration
     * type in the following form:
     * <pre>
     *     tbd
     * </pre>
     *
     * @param configName the configuration name, not null.
     * @return a JSON formatted meta-information.
     */
    public String getConfigurationInfo(String configName);

    /**
     * Allows to determine if a configuration of a given type is available (accessible) in the
     * given environment context.
     *
     * @param configName the configuration name, not null.
     * @param envType        the environment context, not null.
     * @param envContext        the environment context, not null.
     * @return true, if such a configuration is accessible.
     */
    public boolean isConfigurationAvailable(String configName, String envType, String envContext);

    /**
     * Allows to determine if a configuration of a given type is loaded in the
     * given environment context.
     *
     * @param configName the configuration name, not null.
     * @param envType        the environment context, not null.
     * @param envContext        the environment context, not null.
     * @return true, if such a configuration is accessible.
     */
    public boolean isConfigurationLoaded(String configName, String envType, String envContext);

    /**
     * Accesses a configuration of a given type as Map.
     *
     * @param configName the configuration name, not null.
     * @param envType        the environment context, not null.
     * @param envContext        the environment context, not null.
     * @return true, if such a configuration is accessible.
     * @throws org.apache.tamaya.ConfigException If the configuration is not yet loaded.
     */
    public Map<String, String> getConfiguration(String configName, String envType, String envContext)
            throws ConfigException;

    /**
     * Accesses a configuration values for of a given config area as Map.
     * @param area the target area key, not null.
     * @param configName the configuration name, not null.
     * @param envType        the environment context, not null.
     * @param envContext        the environment context, not null.
     * @return the key/values found, including the recursive child values.
     * @throws org.apache.tamaya.ConfigException If the configuration is not yet loaded.
     */
    public Map<String, String> getRecursiveConfigValues(String area, String configName, String envType, String envContext)
            throws ConfigException;

    /**
     * Accesses a configuration values for of a given config area as Map.
     * @param area the target area key, not null.
     * @param configName the configuration name, not null.
     * @param envType        the environment context, not null.
     * @param envContext        the environment context, not null.
     * @return the key/values found, not transitive.
     * @throws org.apache.tamaya.ConfigException If the configuration is not yet loaded.
     */
    public Map<String, String> getConfigValues(String area, String configName, String envType, String envContext)
            throws ConfigException;

    /**
     * Updates a configuration of a given type.
     *
     * @param configName        the configuration name, not null.
     * @param envType        the environment context, not null.
     * @param envContext        the environment context, not null.
     * @param values            the values to be changed.
     * @param aggregationPolicy the aggregation Policy to be used.
     * @return the configuration after the changesd have been applied.
     * @throws org.apache.tamaya.ConfigException If the configuration is not yet loaded, or not
     *                                           mutable.
     */
    public Map<String, String> updateConfiguration(String configName, String envType, String envContext, Map<String, String> values, AggregationPolicy aggregationPolicy)
            throws ConfigException;

    /**
     * Access a JSON formatted info on a configuration loaded in the form as
     * <pre>
     *     tbd
     * </pre>
     * @param configName        the configuration name, not null.
     * @param envType        the environment context, not null.
     * @param envContext        the environment context, not null.
     * @return the JSON formatted info, never null.
     * @throws org.apache.tamaya.ConfigException If the configuration is not yet loaded
     */
    public String getConfigurationInfo(String configName, String envType, String envContext);

    /**
     * Access the defined areas for a given configuration.
     * @param configName        the configuration name, not null.
     * @param envContext        the environment context, not null.
     * @return the areas defined (only returning the areas that contain properties).
     * @throws org.apache.tamaya.ConfigException If the configuration is not yet loaded
     */
    public Set<String> getAreas(String configName, String envType, String envContext);

    /**
     * Access the transitive areas for a given configuration.
     * @param configName        the configuration name, not null.
     * @param envType        the environment context, not null.
     * @param envContext        the environment context, not null.
     * @return the transitive areas defined.
     * @throws org.apache.tamaya.ConfigException If the configuration is not yet loaded
     */
    public Set<String> getTransitiveAreas(String configName, String envType, String envContext);

    /**
     * Allows to determine if an area is existing.
     * @param area the target area key, not null.
     * @param configName        the configuration name, not null.
     * @param envType        the environment context, not null.
     * @param envContext        the environment context, not null.
     * @return true, if such an area exists (the area may be empty).
     */
    public boolean isAreaExisting(String area, String configName, String envType, String envContext);

    /**
     * Allows to determine if an area is empty.
     * @param area the target area key, not null.
     * @param configName        the configuration name, not null.
     * @param envType        the environment context, not null.
     * @param envContext        the environment context, not null.
     * @return true, if such an area exists and is not empty.
     */
    public boolean isAreaEmpty(String area, String configName, String envType, String envContext);

}