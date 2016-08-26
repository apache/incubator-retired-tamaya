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
package org.apache.tamaya.mutableconfig;

import org.apache.tamaya.mutableconfig.spi.ConfigChangeRequest;
import org.apache.tamaya.spi.PropertySource;

import java.util.Collection;

/**
 * Policy that defines how changes are applied to the available
 * {@link org.apache.tamaya.mutableconfig.spi.MutablePropertySource} instances, e.g.
 * <ul>
 *     <li><b>ALL: </b>Changes are propagated to all {@link org.apache.tamaya.mutableconfig.spi.MutablePropertySource}
 *     instances in order of significance. This means that a key added, updated or removed in each instance, if the key
 *     is writable/removable.</li>
 *     <li><b>SIGNIFICANT_ONLY: </b>A change (creation, update) is only applied, if
 * <ol>
 *     <li>the value is not provided by a more significant read-only property source.</li>
 *     <li>there is no more significant writable property source, which supports writing a g iven key.</li>
 * </ol>
 * In other words a added or updated value is written exactly once to the most significant
 * writable property source, which accepts a given key. Otherwise the change is discarded.</li>
 * <li><b>NONE: </b>Do not apply any changes.</li>
 * </ul>
 */
public interface ChangePropagationPolicy {

    /**
     * Method being called when a multiple key/value pairs are added or updated.
     * @param propertySources the property sources, including readable property sources of the current configuration,
     *                        never null.
     * @param configChange the configuration change, not null.
     */
    void applyChange(ConfigChangeRequest configChange, Collection<PropertySource> propertySources);

}
