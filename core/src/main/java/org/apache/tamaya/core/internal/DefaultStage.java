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

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Simple default implementation of a stage.
 * Created by Anatole on 12.11.2014.
 */
final class DefaultStage implements Stage, Serializable{
    /** The stage's name. */
    private final String name;
    /** The stage's properites. */
    private final Map<String,String> properties;

    /**
     * Creates a new stage.
     * @param name the name, not null.
     * @param properties the properties, not null.
     */
    DefaultStage(String name, Map<String,String> properties){
        this.name = Objects.requireNonNull(name);
        Map<String,String> tempProps = new HashMap<>(properties);
        tempProps = Collections.unmodifiableMap(properties);
        this.properties = tempProps;
    }

    /**
     * Get the stage's name.
     * @return the stage's name, never null.
     */
    @Override
    public String getName(){
        return name;
    }

    /**
     * Get all the stage's (unmodifiable) properties.
     * @return the stage's (unmodifiable) properties, never null.
     */
    @Override
    public Map<String,String> getProperties(){
        return properties;
    }

}
