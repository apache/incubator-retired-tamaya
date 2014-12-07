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
package org.apache.tamaya.spi;

import org.apache.tamaya.PropertyAdapter;
import org.apache.tamaya.annotation.WithPropertyAdapter;

/**
 * SPI that is used by the {@link org.apache.tamaya.PropertyAdapters} singleton as delegation instance.
 */
public interface PropertyAdaptersSingletonSpi{

    /**
     * Registers a new PropertyAdapter for the given target type, hereby replacing any existing adapter for
     * this type.
     * @param targetType The target class, not null.
     * @param adapter The adapter, not null.
     * @param <T> The target type
     * @return any adapter replaced with the new adapter, or null.
     */
    <T> PropertyAdapter<T> register(Class<T> targetType, PropertyAdapter<T> adapter);

    /**
     * Get an adapter converting to the given target type.
     * @param targetType the target type class
     * @return true, if the given target type is supported.
     */
    default <T> PropertyAdapter<T> getAdapter(Class<T> targetType){
        return getAdapter(targetType, null);
    }

    /**
     * Get an adapter converting to the given target type.
     * @param targetType the target type class
     * @param <T> the target type
     * @return the corresponding adapter, never null.
     * @throws org.apache.tamaya.ConfigException if the target type is not supported.
     */
    <T> PropertyAdapter<T> getAdapter(Class<T> targetType, WithPropertyAdapter annotation);

    /**
     * Checks if the given target type is supported, i.e. a adapter is registered and accessible.
     * @param targetType the target type class
     * @return true, if the given target type is supported.
     */
    boolean isTargetTypeSupported(Class<?> targetType);
}
