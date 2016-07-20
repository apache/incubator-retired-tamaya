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
package org.apache.tamaya.adapter.spi;

import org.apache.tamaya.ConfigException;
import org.apache.tamaya.Configuration;

import java.util.Map;

/**
 * <p>A adapter that transforms/adapts a {@link org.apache.tamaya.Configuration} to implement
 * another interface. Adapters are ordered internally by their annotated {@link javax.annotation.Priority}
 * value (highest first). Returning a non null value for the adapted target type ends traversal of
 * possible adapters and returns immedeately the result.
 *
 * Normally adapters should not be cached. Caching is within the responsibility of the caller logic.
 * <h3>Implementation Requirements</h3>
 * Implementations of this interface must be
 * <ul>
 *  <li>Thread safe</li>
 *  <li>Immutable</li>
 * </ul>
 */
public interface ConfigurationAdapter {

    /**
     * Adapts a configuration. If this factory is not capable of providing the required
     * target type, it is safe to return {@code null}.
     *
     * @param configuration the configuration to be adapted, not null.
     * @param targetType the target type, not null.
     * @return the adapter, or null.
     */
    <T> T adapt(Configuration configuration, Class<T> targetType);

}
