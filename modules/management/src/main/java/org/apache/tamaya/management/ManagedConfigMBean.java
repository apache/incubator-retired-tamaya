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
     * Configuration: {
     *   "class": "org.apache.tamaya.core.internal.DefaultConfiguration",
     *   "timestamp": 1440426409388,
     *   "data": {
     *     "ALLUSERSPROFILE": "C:\ProgramData",
     *     "APPDATA": "C:\Users\Anatole\AppData\Roaming",
     *     "COMPUTERNAME": "DEVBOX-WIN",
     *     "ComSpec": "C:\Windows\system32\cmd.exe",
     *     "CommonProgramFiles": "C:\Program Files\Common Files",
     *     "CommonProgramFiles(x86)": "C:\Program Files (x86)\Common Files",
     *     "CommonProgramW6432": "C:\Program Files\Common Files",
     *     "FP_NO_HOST_CHECK": "NO",
     *     "HOMEDRIVE": "C:",
     *     // ...
     *   }
     * }
     * </pre>
     *
     * @return a JSON formatted meta-information.
     */
    String getJsonConfigurationInfo();

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
    String getXmlConfigurationInfo();

    /**
     * Accesses a configuration current a given type as Map.
     *
     * @return the current configuration map.
     * @throws org.apache.tamaya.ConfigException If the configuration is not available.
     */
    Map<String, String> getConfiguration();

    /**
     * Accesses a configuration values for current a given config section as Map.
     * @param area the target section key, not null.
     * @param recursive if set to false only direct child keys of the given section are returned.
     * @return the key/values found, including the recursive child values.
     * @throws org.apache.tamaya.ConfigException If the configuration is not yet loaded.
     */
    Map<String, String> getSection(String area, boolean recursive);

    /**
     * Access the defined sections for a given configuration.
     * @return the sections defined (only returning the sections that contain properties).
     * @throws org.apache.tamaya.ConfigException If the configuration is not yet loaded
     */
    Set<String> getSections();

    /**
     * Access the transitive sections for the current configuration.
     * @return the transitive sections defined.
     * @throws org.apache.tamaya.ConfigException If the configuration is not yet loaded
     */
    Set<String> getTransitiveSections();

    /**
     * Allows to determine if an section is existing.
     * @param area the target section key, not null.
     * @return true, if such an section exists (the section may be empty).
     */
    boolean isAreaExisting(String area);

    /**
     * Allows to determine if an section is empty.
     * @param area the target section key, not null.
     * @return true, if such an section exists and is not empty.
     */
    boolean isAreaEmpty(String area);

}