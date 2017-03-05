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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A filter context containing all the required values for implementing filtering.
 *
 * @see PropertyFilter
 */
public class FilterContext {
    /** The key. */
    private final String key;
    @Experimental
    private Map<String, PropertyValue> configEntries = new HashMap();
    @Experimental
    private boolean singlePropertyScoped;


    /**
     * Creates a new FilterContext.
     * @param key the key under evaluation, not null.
     * @param configEntries the raw configuration data available in the current evaluation context, not null.
     */
    public FilterContext(String key, Map<String,PropertyValue> configEntries) {
        this.singlePropertyScoped = false;
        this.key = Objects.requireNonNull(key);
        this.configEntries.putAll(configEntries);
        this.configEntries = Collections.unmodifiableMap(this.configEntries);
    }

    public FilterContext(String key, PropertyValue value) {
        this.singlePropertyScoped = true;
        this.key = Objects.requireNonNull(key);
        if(value!=null) {
            this.configEntries.put(value.getKey(), value);
        }
        this.configEntries = Collections.unmodifiableMap(this.configEntries);
    }

    /**
     * Get the key accessed. This information is very useful to evaluate additional metadata needed to determine/
     * control further aspects of the conversion.
     *
     * @return the key. This may be null in case where a default value has to be converted and no unique underlying
     * key/value configuration is present.
     */
    public String getKey() {
        return key;
    }

    /**
     * Method that determines if filtering is done for a single property accessed, or as part of call to
     * {@code getProperties()}.
     * @return true, if its scoped to a single property accessed.
     */
    @Experimental
    public boolean isSinglePropertyScoped(){
        return singlePropertyScoped;
    }

    /**
     * This map contains the following keys:
     * <ul>
     * <li>the original value <b>before</b> any filters were applied on it.</li>
     * <li>all values starting with an {@code _<key>.}, for example {@code a.value}
     * may have a map set with {@code a.value} (oringinal value), {@code _a.value.origin,
     * _a.value.type, etc}. The exact contents is determine by the {@link PropertySource}s
     * active.</li>
     * </ul>
     * Also important to know is that this map given contains all the evaluated raw entries, regardless
     * of the filters that are later applied. This ensures that met-information required by one filter is
     * not hidden by another filter, because of an invalid filter ordering. In other words filters may remove
     * key/value pairs, e.g. fir security reasons, by returning {@code null}, but the values in the raw map
     * passed as input to the filter process will not be affected by any such removal (but the final properties
     * returned are affected, of course).
     * 
     * Finally, when a single property is accessed, e.g. by calling {@code Configuration.get(String)}.
     *
     * @return the configuration instance, or null.
     */
    @Experimental
    public Map<String, PropertyValue> getConfigEntries() {
        return configEntries;
    }

    @Override
    public String toString() {
        return "FilterContext{key='" + key + "', configEntries=" + configEntries.keySet() + '}';
    }

}
