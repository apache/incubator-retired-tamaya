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
package org.apache.tamaya.core.internal;

import org.apache.tamaya.Stage;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Builder for creating new stages. Instances of this class are not thread safe.
 * Created by Anatole on 12.11.2014.
 */
public final class StageBuilder {

    private String name;
    private Map<String,String> properties = new HashMap<>();

    /**
     * Constructor.
     * @param name the required stage's name, not null.
     */
    private StageBuilder(String name){
        this.name = Objects.requireNonNull(name);
    }

    /**
     * Creates a new builder instance.
     * @param name the stage's name, not null.
     * @return the new builder, never null.
     */
    public static StageBuilder create(String name){
        return new StageBuilder(name);
    }

    /**
     * Adds all the given properties to the stage, existing properties, will be replaced.
     * @param properties the properties, not null.
     * @return the builder for chaining.
     */
    public StageBuilder setProperties(Map<String,String> properties){
        this.properties.putAll(properties);
        return this;
    }

    /**
     * Sets a property for the stage, any existing property value will be overriden.
     * @param key the property key, not null.
     * @param value the property value, not null.
     * @return the builder for chaining.
     */
    public StageBuilder setProperty(String key, String value){
        this.properties.put(key, value);
        return this;
    }

    /**
     * Creates a new Stage instance.
     * @return a new Stage, never null.
     */
    public Stage build(){
        return new DefaultStage(name, properties);
    }
}
