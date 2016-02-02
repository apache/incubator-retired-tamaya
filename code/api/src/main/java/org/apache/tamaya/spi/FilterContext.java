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
 * A conversion context containing all the required values for implementing conversion. Use the included #Builder
 * for creating new instances of. This class is thread-safe to use. Adding supported formats is synchronized.
 *
 * @see PropertyConverter
 */
public class FilterContext {

    private final String key;
    @Experimental
    private Map<String, String> metaEntries = new HashMap();

    /**
     * Private constructor used from builder.
     *
     * @param builder the builder, not null.
     */
    protected FilterContext(Builder builder) {
        this.key = builder.key;
        this.metaEntries.putAll(builder.metaEntries);
    }

    /**
     * Get the key accessed. This information is very useful to evaluate additional metadata needed to determine/
     * control further aspects of the conversion.
     *
     * @return the key. This may be null in case where a default value has to be converted and no unique underlying
     * key/value configuration is present..
     */
    public String getKey() {
        return key;
    }

    /**
     * This map contains the following keys:
     * <ul>
     * <li>the original value <b>before</b> any filters were applied on it.</li>
     * <li>all values starting with an {@code _<key>.}. for example {@code a.value}</li>
     * may have a map set with {@code a.value} (oringinal value), {@code _a.value.origin,
     * _a.value.type, etc}. The exact contents is determine by the {@link PropertySource}s
     * active.
     * </ul>
     * Also important to know is that this map given contains all the evaluated raw entries, regardless
     * of the filters that are later applied. This ensures that met-information required by one filter is
     * not hidden by another filter, because of an invalid filter ordering. In other words filters may remove
     * key/value pairs, e.g. fir security reasons, by returning {@code null}, but the values in the raw map
     * passed as input to the filter process will not be affected by any such removal (but the final properties
     * returned are affected, of course).
     * <p/>
     * Finally, when a single property is accessed, e.g. by calling {@code Configuration.get(String)}.
     *
     * @return the configuration instance, or null.
     */
    @Experimental
    public Map<String, String> getMetaEntries() {
        return metaEntries;
    }

    /**
     * Property to check, if the entry filtered is actually accessed as single value, or as part of a full Map
     * access. For both scenarios filtering may be different.
     * @return true, if it is a directly accessed key.
     */
    @Experimental
    public boolean isSingleAccessedProperty(){
        return this.metaEntries.size()==1;
    }

    @Override
    public String toString() {
        return "FilterContext{" +
                "key='" + key + '\'' +
                ", metaEntries=" + metaEntries +
                '}';
    }

    /**
     * Builder to create new instances of {@link FilterContext}.
     */
    public static final class Builder {
        /**
         * The accessed key, or null.
         */
        private String key;
        private Map<String, String> metaEntries = new HashMap();

        /**
         * Creates a new Builder instance.
         *
         * @param key the requested key, may be null.
         */
        public Builder(String key, String value) {
            this(key, Collections.<String, String>emptyMap());
            metaEntries.put(key, value);
        }

        /**
         * Creates a new Builder instance.
         *
         * @param metaData the configuration, not null.
         * @param key      the requested key, may be null.
         */
        public Builder(String key, Map<String, String> metaData) {
            this.key = key;
            this.metaEntries.putAll(metaData);
        }

        /**
         * Sets the key.
         *
         * @param key the key, not null.
         * @return the builder instance, for chaining
         */
        public Builder setKey(String key) {
            this.key = Objects.requireNonNull(key);
            return this;
        }

        /**
         * Sets the configuration.
         *
         * @param metaProperties the meta configuration, not null
         * @return the builder instance, for chaining
         */
        public Builder setMetaProperties(Map<String, String> metaProperties) {
            this.metaEntries.putAll(Objects.requireNonNull(metaProperties));
            return this;
        }

        /**
         * Builds a new context instance.
         *
         * @return a new context, never null.
         */
        public FilterContext build() {
            return new FilterContext(this);
        }


        @Override
        public String toString() {
            return "Builder{" +
                    "key='" + key + '\'' +
                    ", metaEntries=" + metaEntries +
                    '}';
        }
    }
}
