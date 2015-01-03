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
package org.apache.tamaya.core.properties;

import java.lang.Override;
import java.lang.String;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Models a {@link org.apache.tamaya.PropertySource} that can be build using a builder pattern.
 */
public class MapBasedPropertySource implements PropertySource {

    private static final long serialVersionUID = 7601389831472839249L;

    private static final Logger LOG = Logger.getLogger(MapBasedPropertySource.class.getName());

    private int ordinal;


    private String name;
    /**
     * The unit's entries.
     */
    private Map<String,String> entries = new HashMap<>();

    /**
     * Constructor.
     *
     * @param entries the config entries, not null.
     */
    public MapBasedPropertySource(int ordinal, String name, Map<String, String> entries){
        this.name = Objects.requireNonNull(name);
        this.ordinal = ordinal;
        this.entries.putAll(Objects.requireNonNull(entries, "entries required."));
        this.entries = Collections.unmodifiableMap(this.entries);
    }

    @Override
    public Map<String, String> getProperties() {
        return this.entries;
    }

    @Override
    public int getOrdinal(){
        return ordinal;
    }

    @Override
    public String getName(){
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MapBasedPropertySource that = (MapBasedPropertySource) o;

        if (!entries.equals(that.entries)) return false;
        if (!name.equals(that.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + entries.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "MapBasedPropertySource{" +
                "name=" + name +
                ", ordinal=" + ordinal +
                '}';
    }
}
