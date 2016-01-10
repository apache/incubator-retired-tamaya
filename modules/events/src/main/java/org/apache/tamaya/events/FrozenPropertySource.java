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

import org.apache.tamaya.spi.PropertySource;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * PropertySource implementation that stores all current values of a given (possibly dynamic, contextual and non server
 * capable instance) and is fully serializable. Note that hereby only the scannable key/value pairs are considered.
 */
public final class FrozenPropertySource implements PropertySource, Serializable {
    private static final long serialVersionUID = -6373137316556444171L;
    /**
     * The ordinal.
     */
    private final int ordinal;
    /**
     * The properties read.
     */
    private Map<String, String> properties = new HashMap<>();
    /**
     * The PropertySource's name.
     */
    private final String name;

    /**
     * Constructor.
     *
     * @param propertySource The base PropertySource.
     */
    private FrozenPropertySource(PropertySource propertySource) {
        this.properties.putAll(propertySource.getProperties());
        this.properties.put("[meta]frozenAt", String.valueOf(System.currentTimeMillis()));
        this.properties = Collections.unmodifiableMap(this.properties);
        this.ordinal = propertySource.getOrdinal();
        this.name = propertySource.getName();
    }

    /**
     * Creates a new FrozenPropertySource instance based on a PropertySource given.
     *
     * @param propertySource the property source to be frozen, not null.
     * @return the frozen property source.
     */
    public static FrozenPropertySource of(PropertySource propertySource) {
        if (propertySource instanceof FrozenPropertySource) {
            return (FrozenPropertySource) propertySource;
        }
        return new FrozenPropertySource(propertySource);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getOrdinal() {
        return this.ordinal;
    }

    @Override
    public String get(String key) {
        return this.properties.get(key);
    }

    @Override
    public Map<String, String> getProperties() {
        return properties;
    }

    @Override
    public boolean isScannable() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FrozenPropertySource)) {
            return false;
        }
        FrozenPropertySource that = (FrozenPropertySource) o;
        return ordinal == that.ordinal && properties.equals(that.properties);
    }

    @Override
    public int hashCode() {
        int result = ordinal;
        result = 31 * result + properties.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "FrozenPropertySource{" +
                "name=" + name +
                ", ordinal=" + ordinal +
                ", properties=" + properties +
                '}';
    }
}
