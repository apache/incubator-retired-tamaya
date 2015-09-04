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
package org.apache.tamaya.functions;

import org.apache.tamaya.Configuration;
import org.apache.tamaya.spi.PropertySource;

import java.util.Map;
import java.util.Objects;

/**
 * PropertySource that wraps a Configuration instance.
 */
final class ConfigWrappingPropertySource implements PropertySource {
    /** The property source name. */
    private String name;
    /** The ordinal. */
    private int ordinal;
    /** The wrapped config. */
    private Configuration config;

    /**
     * Constructor.
     * @param name the property source name, not null.
     * @param ordinal ths ordinal
     * @param config the wrapped config, not null.
     */
    public ConfigWrappingPropertySource(String name, int ordinal, Configuration config){
        this.name = Objects.requireNonNull(name);
        this.ordinal = ordinal;
        this.config = Objects.requireNonNull(config);
    }

    @Override
    public int getOrdinal() {
        return ordinal;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String get(String key) {
        return config.get(key);
    }

    @Override
    public Map<String, String> getProperties() {
        return config.getProperties();
    }

    @Override
    public boolean isScannable() {
        return true;
    }

    @Override
    public String toString(){
        return "ConfigWrappingPropertySource(name="+name+", ordinal="+ordinal+", config="+config+")";
    }
}
