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
 * Policy that determines how the final value of a configuration entry is evaluated. An instances of this
 * interface can be registered to get control how multiple PropertySources are combined. This is useful in cases
 * where the default overriding policy as implemented in {@link #DEFAULT_OVERRIDING_COLLECTOR} is not matching
 * the need of the current application, e.g. then entries containing multiple values should be combined to new
 * values instead of overridden.
 */
public interface PropertyValueCombinationPolicy {

    /**
     * Default overriding collector, where each existing entry ({@code current} is overridden by a subsequent non-null
     * entry evaluated by {@code propertySource.get(key)}.
     */
    PropertyValueCombinationPolicy DEFAULT_OVERRIDING_COLLECTOR = new PropertyValueCombinationPolicy(){

        @Override
        public String collect(String currentValue, String key, PropertySource propertySource) {
            String value = propertySource.get(key);
            return value!=null?value:currentValue;
        }

    };

    /**
     * Method that is called for each value evaluated by a PropertySource for the given key. This method is called
     * either when a single key is accessed, e.g. by calling {@code org.apache.tamaya.Configuration.getXXX}, but also
     * when the full configuration property map is accessed by calling
     * {@link org.apache.tamaya.Configuration#getProperties()}.
     *
     * @param currentValue the current value, including null.
     *                     The collector should either combine the existing value with value from {@code currentValue}
     *                     or replace the value in {@code currentValue} with {@code valueRead}, hereby returning the
     *                     result to be used as new {@code currentValue}.
     * @param key The current key to be evaluated.
     * @param propertySource The PropertySource that may return an value for the given key. The PropertySource given
     *                       may be evaluated for additional meta-data, how the given values are to be combined.
     *                       Note that the value returned by a PropertySource can be null. In that case
     *                       {@code currentValue} should be returned in almost all cases.
     * @return the value to be used for future evaluation.
     */
    String collect(String currentValue, String key, PropertySource propertySource);

}
