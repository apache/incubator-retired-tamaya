/*
 * Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.apache.tamaya.base.configsource;

import javax.config.spi.ConfigSource;
import java.util.*;

/**
 * A Buildable property source.
 */
public class BuildableConfigSource implements ConfigSource{

    private int ordinal;
    private String name = "PropertySource-"+UUID.randomUUID().toString();
    private Map<String,String> properties = new HashMap<>();

    @Override
    public int getOrdinal() {
        return ordinal;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getValue(String key) {
        return properties.get(key);
    }

    @Override
    public Map<String, String> getProperties() {
        return Collections.unmodifiableMap(properties);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BuildableConfigSource that = (BuildableConfigSource) o;

        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "BuildablePropertySource{" +
                "ordinal=" + ordinal +
                ", name='" + name + '\'' +
                ", properties=" + properties +
                '}';
    }

    /**
     * Builder builder.
     *
     * @return the builder
     */
    public static Builder builder() {
        return new Builder();
    }


    /**
     * The type Builder.
     */
    public static final class Builder {
        private int ordinal;
        private String source;
        private String name = "PropertySource-"+ UUID.randomUUID().toString();
        private Map<String,String> properties = new HashMap<>();

        private Builder() {
        }

        /**
         * With ordinal builder.
         *
         * @param ordinal the ordinal
         * @return the builder
         */
        public Builder withOrdinal(int ordinal) {
            this.ordinal = ordinal;
            return this;
        }

        /**
         * With source builder.
         *
         * @param source the source
         * @return the builder
         */
        public Builder withSource(String source) {
            this.source = Objects.requireNonNull(source);
            return this;
        }

        /**
         * With name builder.
         *
         * @param name the name
         * @return the builder
         */
        public Builder withName(String name) {
            this.name = Objects.requireNonNull(name);
            return this;
        }

        /**
         * With simple property builder.
         *
         * @param key   the key
         * @param value the value
         * @return the builder
         */
        public Builder withProperty(String key, String value) {
            return withProperty(key, value, this.source);
        }

        /**
         * With simple property builder.
         *
         * @param key    the key
         * @param value  the value
         * @param source the source
         * @return the builder
         */
        public Builder withProperty(String key, String value, String source) {
            this.properties.put(key, value);
            if(source!=null) {
                this.properties.put(key+"[meta]", "source="+source);
            }
            return this;
        }

        /**
         * With properties builder.
         *
         * @param properties the properties
         * @param source     the source
         * @return the builder
         */
        public Builder withProperty(Map<String, String> properties, String source) {
            properties.forEach((k,v) -> {
                withProperty(k, v, source);
            });
            return this;
        }

        /**
         * With simple properties builder.
         *
         * @param properties the properties
         * @return the builder
         */
        public Builder withProperties(Map<String, String> properties) {
            properties.forEach((k,v) -> {
                withProperty(k, v);
            });
            return this;
        }

        /**
         * But builder.
         *
         * @return the builder
         */
        public Builder but() {
            return builder().withOrdinal(ordinal).withName(name).withProperties(properties);
        }

        /**
         * Build buildable property source.
         *
         * @return the buildable property source
         */
        public BuildableConfigSource build() {
            BuildableConfigSource buildableConfigSource = new BuildableConfigSource();
            buildableConfigSource.name = this.name;
            buildableConfigSource.properties = this.properties;
            buildableConfigSource.ordinal = this.ordinal;
            return buildableConfigSource;
        }
    }
}
