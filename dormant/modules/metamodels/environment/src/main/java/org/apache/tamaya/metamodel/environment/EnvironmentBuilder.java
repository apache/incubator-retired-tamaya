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
package org.apache.tamaya.metamodel.environment;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
* Builder to create new {@link Environment instances.}
*/
public final class EnvironmentBuilder{

    /** The property name for the stage property. */
    public static final String STAGE_PROP = "stage";

    /** THe environment data. */
    Map<String,String> contextData = new HashMap<>();

    /**
     * Constructor.
     */
    private EnvironmentBuilder() {
    }

    /**
     * Creates a new buildr instance.
     * @return the new builder instance.
     */
    public static final EnvironmentBuilder of() {
        return new EnvironmentBuilder();
    }

    /**
     * Sets a new environment property.
     * @param key the key, not null.
     * @param value the keys, not null.
     * @return the builder for chaining
     */
    public EnvironmentBuilder set(String key, String value){
        this.contextData.put(key, value);
        return this;
    }

    /**
     * Sets new environment properties.
     * @param values the key/values, not null.
     * @return the builder for chaining
     */
    public EnvironmentBuilder setAll(Map<String,String> values){
        this.contextData.putAll(values);
        return this;
    }

    /**
     * Sets the stage using the default stage key.
     * @param stage The stage, not null.
     * @return the builder for chaining.
     */
    public EnvironmentBuilder setStage(String stage){
        this.contextData.put(STAGE_PROP, Objects.requireNonNull(stage));
        return this;
    }

    /**
     * Access a property
     * @param key the key, not null.
     * @return the builder for chaining.
     */
    public String getProperty(String key) {
        return this.contextData.get(key);
    }

    /**
     * Builds a new Environment.
     * @return a new Environment, never null.
     */
    public Environment build() {
        return new BuildableEnvironment(this);
    }
}
