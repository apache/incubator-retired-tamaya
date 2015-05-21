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
package org.apache.tamaya.environment;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
* Builder to create new {@link RuntimeContext instances.}
*/
public final class RuntimeContextBuilder {

    /** The context id, never null or empty. */
    String contextId;

    /** THe environment data. */
    Map<String,String> contextData = new HashMap<>();

    /**
     * Constructor.
     */
    private RuntimeContextBuilder(String contextId) {
        this.contextId = Objects.requireNonNull(contextId);
    }

    /**
     * Creates a new buildr instance.
     * @return the new builder instance.
     */
    public static RuntimeContextBuilder of(String contextId) {
        return new RuntimeContextBuilder(contextId);
    }

    /**
     * Sets the environment contextId.
     * @param contextId the contextId, not null.
     * @return the builder for chaining
     */
    public RuntimeContextBuilder setContextId(String contextId){
        this.contextId = Objects.requireNonNull(contextId);
        return this;
    }

    /**
     * Sets a new environment property.
     * @param key the key, not null.
     * @param value the keys, not null.
     * @return the builder for chaining
     */
    public RuntimeContextBuilder set(String key, String value){
        this.contextData.put(key, value);
        return this;
    }

    /**
     * Sets new environment properties.
     * @param values the key/values, not null.
     * @return the builder for chaining
     */
    public RuntimeContextBuilder setAll(Map<String,String> values){
        this.contextData.putAll(values);
        return this;
    }

    /**
     * Builds a new Environment.
     * @return a new Environment, never null.
     */
    public RuntimeContext build() {
        return new BuildableRuntimeContext(this);
    }
}
