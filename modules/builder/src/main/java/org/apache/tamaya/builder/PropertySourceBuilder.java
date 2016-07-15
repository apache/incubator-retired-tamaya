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
package org.apache.tamaya.builder;

import org.apache.tamaya.builder.spi.PropertySource;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Simple builder for building a {@link PropertySource}.
 */
public final class PropertySourceBuilder {
    /** The ordinal to be used. */
    private int ordinal;
    /** The name to be used. */
    private final String name;
    /** The properties. */
    private final Map<String,String> properties = new HashMap<>();

    /** private constructor. */
    private PropertySourceBuilder(String name){
        this.name = Objects.requireNonNull(name);
    }

    /**
     * Gets a new instance of a builder.
     * @param name The name of the property source, not null.
     * @return a new instance.
     */
    public static PropertySourceBuilder of(String name){
        return new PropertySourceBuilder(name);
    }

    /**
     * Gets a new instance of a builder.
     * @param name The name of the property source, not null.
     * @return a new instance.
     */
    public static PropertySourceBuilder from(String name){
        return new PropertySourceBuilder(name);
    }

    /**
     * Sets a new property key/value.
     * @param key the property key, not null.
     * @param value the property value, not null.
     * @return the bulder for chaining.
     */
    public PropertySourceBuilder put(String key, String value){
        this.properties.put(key, value);
        return this;
    }

    /**
     * Put all the given key, values.
     * @param values the new key/values, not null.
     * @return the bulder for chaining.
     */
    public PropertySourceBuilder putAll(Map<String, String> values){
        this.properties.putAll(values);
        return this;
    }

    /**
     * Sets the ordinal to be used explicitly (instead evaluating it using {@code tamaya.ordinal}.
     * @param ordinal the explicit ordinal to be used.
     * @return the bulder for chaining.
     */
    public PropertySourceBuilder withOrdinal(int ordinal){
        this.ordinal = ordinal;
        return this;
    }

    /**
     * Puts all values from the given property source.
     * @param propertySource the property source, not null.
     * @return the bulder for chaining.
     */
    public PropertySourceBuilder putAll(PropertySource propertySource){
        this.properties.putAll(propertySource.getProperties());
        return this;
    }

    /**
     * Creates a new immutable {@link PropertySource} instance.
     * @return a new immutable {@link PropertySource} instance, never null.
     */
    public PropertySource build(){
        return new SimplePropertySource(name, properties);
    }

    @Override
    public String toString() {
        return "PropertySourceBuilder{" +
                "ordinal=" + ordinal +
                ", name='" + name + '\'' +
                ", properties=" + properties +
                '}';
    }
}
