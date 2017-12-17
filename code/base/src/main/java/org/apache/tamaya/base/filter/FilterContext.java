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
package org.apache.tamaya.base.filter;

import org.apache.tamaya.spi.ConfigValue;
import org.apache.tamaya.spi.Filter;

import javax.config.Config;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A filter context containing all the required values for implementing filtering.
 *
 * @see Filter
 */
public final class FilterContext {
    /** The key. */
    private final ConfigValue property;

    private Map<String,String> configEntries = new HashMap<>();

    /** The current context. */
    private final Config config;

    private boolean singlePropertyScoped;

    private static ThreadLocal<FilterContext> INSTANCE = new ThreadLocal<>();

    public static FilterContext getContext(){
        return INSTANCE.get();
    }

    public static void setContext(FilterContext context){
        INSTANCE.set(Objects.requireNonNull(context));
    }

    /**
     * Creates a new FilterContext, for filtering of a multi value access
     * using {@link Config#getPropertyNames()} .
     *
     * @param value the value under evaluation, not {@code null}.
     * @param configEntries the raw configuration data available in the
     *                      current evaluation context, not {@code null}.
     * @param config the current config, not {@code null}.
     */
    public FilterContext(ConfigValue value, Map<String,String> configEntries, Config config) {
        Objects.requireNonNull(value, "Value must not be null.");
        Objects.requireNonNull(configEntries, "Initial configuration entries must be not null.");
        Objects.requireNonNull(config, "config must be not null.");

        this.singlePropertyScoped = false;
        this.property = Objects.requireNonNull(value);
        this.configEntries.putAll(configEntries);
        this.config = config;
    }

    /**
     * Creates a new FilterContext, for filtering of a single value access
     * using {@link Config#getPropertyNames()}.
     * @param value the value under evaluation, not {@code null}.
     * @param config the current config, not {@code null}.
     */
    public FilterContext(ConfigValue value, Config config) {
        this.config = config;
        this.property = Objects.requireNonNull(value, "Value must not be null.");
        this.singlePropertyScoped = true;
    }

    /**
     * Get the current context.
     * @return the current context, not {@code null}.
     */
    public Config getConfig(){
        return config;
    }

    /**
     * Get the property value under evaluation. This information is very useful to evaluate additional metadata needed to determine/
     * control further aspects of the conversion.
     *
     * @return the key. This may be null in case where a default value has to be converted and no unique underlying
     * key/value configuration is present.
     */
    public ConfigValue getProperty() {
        return property;
    }

    /**
     * Method that determines if filtering is done for a single property accessed, or as part of call to
     * {@code getProperties()}.
     * @return true, if its scoped to a single property accessed.
     */
    public boolean isSinglePropertyScoped(){
        return singlePropertyScoped;
    }

    /**
     * This map contains the following keys:
     * <ul>
     * <li>the original value <b>before</b> any filters were applied on it.</li>
     * <li>all values starting with an {@code _<key>.}, for example {@code a.value}
     * may have a map set with {@code a.value} (oringinal value), {@code _a.value.origin,
     * _a.value.type, etc}. The exact contents is determine by the {@link javax.config.spi.ConfigSource}s
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
    public Map<String, String> getConfigEntries() {
        return configEntries;
    }

    @Override
    public String toString() {
        return "FilterContext{property='" + property + "', configEntries=" + configEntries.keySet() + '}';
    }

}
