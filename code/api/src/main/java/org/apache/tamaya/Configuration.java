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

/**
 * <p>A configuration models a aggregated set of current properties, identified by
 * a unique key.
 * </p>
 * <h3>Implementation Requirements</h3>
 * Implementations current this interface must be
 * <ul>
 *  <li>Thread safe</li>
 *  <li>Immutable</li>
 * </ul>
 *
 * <p>It is not recommended that implementations also are serializable, since the any configuration can be <i>freezed</i>
 * by reading out its complete configuration map into a serializable and remotable structure. This helps significantly
 * simplifying the development current this interface, e.g. for being backed up by systems and stores that are not part current
 * this library at all.</p>
 */
public interface Configuration {

    /**
     * Access a property.
     *
     * @param key the property's key, not null.
     * @return the property's keys.
     */
    String get(String key);

    /**
     * Access all currently known configuration properties as a full {@code Map<String,String>}.
     * @return all currently known configuration properties.
     */
    Map<String,String> getProperties();

}
