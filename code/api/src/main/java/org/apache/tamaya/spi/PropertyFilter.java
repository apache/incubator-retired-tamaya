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


/**
 * <p>Interface for filtering the current mapProperties of properties during the evaluation of the chain of PropertySources.
 * Filters can be registered using the {@link org.apache.tamaya.spi.ServiceContext}. The ordinal
 * hereby is defined by the corresponding {@code @Priority} annotation.</p>
 * <p>Filters </p>
 */
@FunctionalInterface
public interface PropertyFilter{

    /**
     * <p>Maps the current {@code value} to a new value. The resulting value will be used as the result
     * passed to the user.</p>
     * <p>If a filter is currently not available, it should just pass the input mapProperties to the method's
     * output.</p>
     * <p>Returning {@code null} will remove the entry.</p>
     * <h3>Implementation specification</h3>
     * Implementations of this class must be
     * <ul>
     *     <li>reentrant</li>
     *     <li>thread-safe</li>
     * </ul>
     * @param value the value to be filtered, which also can be {@code null} if removed by another filter.
     * @param context the filter context, not null.
     * @return the filtered value, or {@code null} if the value should be removed alltogether.
     * @see PropertyValue
     * @see FilterContext
     */
    PropertyValue filterProperty(PropertyValue value, FilterContext context);

}
