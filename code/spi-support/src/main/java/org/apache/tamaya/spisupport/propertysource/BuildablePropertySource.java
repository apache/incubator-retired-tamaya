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
package org.apache.tamaya.spisupport.propertysource;

import org.apache.tamaya.spi.ChangeSupport;
import org.apache.tamaya.spi.PropertySource;
import org.apache.tamaya.spi.PropertyValue;

import java.util.*;

/**
 * A Buildable property source.
 */
public class BuildablePropertySource implements PropertySource{

    private int ordinal;
    private String name = "PropertySource-"+UUID.randomUUID().toString();
    private Map<String,PropertyValue> properties = new HashMap<>();

    @Override
    public int getOrdinal() {
        return ordinal;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public PropertyValue get(String key) {
        return properties.get(key);
    }

    @Override
    public Map<String, PropertyValue> getProperties() {
        return Collections.unmodifiableMap(properties);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BuildablePropertySource that = (BuildablePropertySource) o;

        return name.equals(that.name);
    }

    @Override
    public ChangeSupport getChangeSupport(){
        return ChangeSupport.IMMUTABLE;
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
        private String source = "<on-the-fly-build>";
        private String name = "PropertySource-"+ UUID.randomUUID().toString();
        private Map<String,PropertyValue> properties = new HashMap<>();

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
         * @param value the createValue
         * @return the builder
         */
        public Builder withSimpleProperty(String key, String value) {
            return withProperties(PropertyValue.of(key, value, this.source));
        }

        /**
         * With simple property builder.
         *
         * @param key    the key
         * @param value  the createValue
         * @param source the source
         * @return the builder
         */
        public Builder withSimpleProperty(String key, String value, String source) {
            return withProperties(PropertyValue.of(key, value, source));
        }

        /**
         * With properties builder.
         *
         * @param values the values
         * @return the builder
         */
        public Builder withProperties(PropertyValue... values) {
            for(PropertyValue val:values){
                this.properties.put(val.getKey(), val);
            }
            return this;
        }

        /**
         * With properties builder.
         *
         * @param properties the properties
         * @return the builder
         */
        public Builder withProperties(Map<String, PropertyValue> properties) {
            this.properties =  Objects.requireNonNull(properties);
            return this;
        }

        /**
         * With properties builder.
         *
         * @param properties the properties
         * @param source     the source
         * @return the builder
         */
        public Builder withProperties(Map<String, String> properties, String source) {
            this.properties.putAll(PropertyValue.map(properties, source));
            return this;
        }

        /**
         * With simple properties builder.
         *
         * @param properties the properties
         * @return the builder
         */
        public Builder withSimpleProperties(Map<String, String> properties) {
            this.properties.putAll(PropertyValue.map(properties, this.source));
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
        public BuildablePropertySource build() {
            BuildablePropertySource buildablePropertySource = new BuildablePropertySource();
            buildablePropertySource.name = this.name;
            buildablePropertySource.properties = this.properties;
            buildablePropertySource.ordinal = this.ordinal;
            return buildablePropertySource;
        }
    }
}
