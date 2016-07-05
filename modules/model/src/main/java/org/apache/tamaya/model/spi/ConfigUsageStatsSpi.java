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
package org.apache.tamaya.model.spi;

import org.apache.tamaya.model.Usage;

import java.util.Collection;
import java.util.Set;

/**
 * SPI to be implemented by the component responsible for usage tracking of
 * configuration.
 */
public interface ConfigUsageStatsSpi {

    /**
     * Enables/disables usage tracking.
     * @param enable set to true to enable usage tracking.
     */
    void enableUsageTracking(boolean enable);

    /**
     * Allows to check if usage tracking is enabled (should be disbled by default).
     * @return true, if usage tracking is enabled.
     */
    boolean isUsageTrackingEnabled();

    /**
     * Get the list of packages, which are not evaluated for tracking configuration access and usage statistics.
     * @return the set of ignored package names.
     */
    Set<String> getIgnoredPackages();

    /**
     * Adds the given packageNames to the list of packages to be ignored when collecting usage data.
     * @param packageName the package names to be added, not null.
     */
    void addIgnoredUsagePackages(String... packageName);

    /**
     * Access the usage statistics for a given key. If usage stats collection is not
     * activated (default), this method returns null.
     * @param key the fully qualified configuration key, not null.
     * @return the stats collected, or null.
     */
    Usage getUsage(String key);

    /**
     * Access the usage statistics for accessing {@link org.apache.tamaya.Configuration#getProperties()}.
     * If usage stats collection is not activated (default), this method returns null.
     * @return the stats collected, or null.
     */
    Usage getUsageAllProperties();

    /**
     * Get the recorded usage references of configuration.
     * @return the recorded usge references, never null.
     */
    Collection<Usage> getUsages();

    /**
     * Track the access of {@code Configuration#getProperties()} for
     * usage statistics.
     */
    void trackAllPropertiesAccess();

    /**
     * Track the access of {@code Configuration#get(String)} for
     * usage statistics.
     * @param key key to track for
     * @param value value to track for
     */
    void trackSingleKeyAccess(String key, String value);

    /**
     * Access the usage statistics for the recorded uses of configuration.
     * @return usage statistics info
     */
    String getUsageInfo();

    /**
     * Clears all collected usage statistics.
     */
    void clearUsageStats();
}
