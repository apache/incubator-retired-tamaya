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
package org.apache.tamaya.events;

import org.apache.tamaya.Configuration;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * /**
 * Configuration implementation that stores all current values of a given (possibly dynamic, contextual and non remote
 * capable instance) and is fully serializable. Note that hereby only the scannable key/value pairs are considered.
 */
public final class FrozenConfiguration implements Configuration, Serializable {
    private static final long serialVersionUID = -6373137316556444171L;
    /**
     * The properties frozen.
     */
    private Map<String, String> properties = new HashMap<>();

    /**
     * Constructor.
     *
     * @param config The base configuration.
     */
    private FrozenConfiguration(Configuration config) {
        this.properties.putAll(config.getProperties());
        this.properties.put("[meta]frozenAt", String.valueOf(System.currentTimeMillis()));
        this.properties = Collections.unmodifiableMap(this.properties);
    }

    /**
     * Creates a new FrozenConfiguration instance based on a Configuration given.
     *
     * @param config the configuration to be frozen, not null.
     * @return the frozen Configuration.
     */
    public static FrozenConfiguration of(Configuration config) {
        if (config instanceof FrozenConfiguration) {
            return (FrozenConfiguration) config;
        }
        return new FrozenConfiguration(config);
    }

    @Override
    public Map<String, String> getProperties() {
        return properties;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FrozenConfiguration that = (FrozenConfiguration) o;
        if (!properties.equals(that.properties)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return properties.hashCode();
    }

    @Override
    public String toString() {
        return "FrozenConfiguration{" +
                "properties=" + properties +
                '}';
    }
}
