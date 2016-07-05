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
package org.apache.tamaya.model;

import org.apache.tamaya.model.spi.ConfigUsageStatsSpi;
import org.apache.tamaya.spi.ServiceContextManager;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Validator accessor to validate the current configuration.
 */
public final class ConfigUsageStats {

    /** The logger used. */
    private static final Logger LOG = Logger.getLogger(ConfigUsageStats.class.getName());
    private static final String NO_USAGE_TRACKER_SPI_COMPONENT_MESSAGE = "No UsageTrackerSpi component available.";

    /** The loaded usage tracking SPI. */
    private static ConfigUsageStatsSpi usageTracker = ServiceContextManager
            .getServiceContext().getService(ConfigUsageStatsSpi.class);

    /**
     * Singleton constructor.
     */
    private ConfigUsageStats() {
    }

    /**
     * Returns a set of package names that are to be ignored when collecting usage data.
     * @return the ignored package names, not null.
     */
    public static Set<String> getIgnoredUsagePackages(){
        return Objects.requireNonNull(usageTracker, NO_USAGE_TRACKER_SPI_COMPONENT_MESSAGE)
                .getIgnoredPackages();
    }

    /**
     * Adds the given packageNames to the list of packages to be ignored when collecting usage data.
     * @param packageName the package names to be added, not null.
     */
    public static void addIgnoredUsagePackages(String... packageName){
        Objects.requireNonNull(usageTracker, NO_USAGE_TRACKER_SPI_COMPONENT_MESSAGE)
                .addIgnoredUsagePackages(packageName);
    }

    /**
     * Enables/disables usage tracking.
     * @param enabled set to true to enable usage tracking.
     */
    public static void enableUsageTracking(boolean enabled){
        Objects.requireNonNull(usageTracker, NO_USAGE_TRACKER_SPI_COMPONENT_MESSAGE)
                .enableUsageTracking(enabled);
    }

    /**
     * Access the usage statistics for a given key. If usage stats collection is not
     * activated (default), this method returns null.
     * @param key the fully qualified configuration key, not null.
     * @return the stats collected, or null.
     */
    public static Usage getUsage(String key){
        return Objects.requireNonNull(usageTracker, NO_USAGE_TRACKER_SPI_COMPONENT_MESSAGE)
                .getUsage(key);
    }

    /**
     * Get the recorded usage references of configuration.
     * @return the recorded usge references, never null.
     */
    public static Collection<Usage> getUsages() {
        return Objects.requireNonNull(usageTracker, NO_USAGE_TRACKER_SPI_COMPONENT_MESSAGE).getUsages();
    }

    /**
     * Clears all collected usage statistics.
     */
    public static void clearUsageStats() {
        Objects.requireNonNull(usageTracker, NO_USAGE_TRACKER_SPI_COMPONENT_MESSAGE)
                .clearUsageStats();
    }

    /**
     * Access the usage statistics for accessing {@link org.apache.tamaya.Configuration#getProperties()}.
     * If usage stats collection is not activated (default), this method returns null.
     * @return the stats collected, or null.
     */
    public static Usage getUsageAllProperties(){
        return Objects.requireNonNull(usageTracker, NO_USAGE_TRACKER_SPI_COMPONENT_MESSAGE)
                .getUsageAllProperties();
    }

    /**
     * Allows to check if usage tracking is enabled (should be disbled by default).
     * @return true, if usage tracking is enabled.
     */
    public static boolean isUsageTrackingEnabled(){
        return Objects.requireNonNull(usageTracker, NO_USAGE_TRACKER_SPI_COMPONENT_MESSAGE)
                .isUsageTrackingEnabled();
    }

    /**
     * Access the usage statistics for the recorded uses of configuration.
     * @return usage info or default message.
     */
    public static String getUsageInfo(){
        return Objects.requireNonNull(usageTracker, NO_USAGE_TRACKER_SPI_COMPONENT_MESSAGE).getUsageInfo();
    }

}
