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
 * SPI to implemented by the component responsible for usage tracking of
 * configuration.
 */
public interface UsageTrackerSpi {

    /**
     * Get the list of package, which are not evaluated for tracking configuration access and usage statistics.
     * @return the set of ignored package names.
     */
    Set<String> getIgnoredPackages();

    /**
     * Get the recorded usage references of configuration.
     * @return the recorded usge references, never null.
     */
    Collection<Usage> getUsages();

    /**
     * Track the access of {@code ConfigurationProvider#getConfiguration()} for
     * usage statistics.
     */
    void trackConfigurationAccess();

    /**
     * Track the access of {@code Configuration#getProperties()} for
     * usage statistics.
     */
    void trackAllPropertiesAccess();

    /**
     * Track the access of {@code Configuration#get(String)} for
     * usage statistics.
     */
    void trackSingleKeyAccess(String key, String value);

    /**
     * Access the usage statistics for the recorded uses of configuration.
     */
    String getUsageInfo();

}
