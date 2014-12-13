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
package org.apache.tamaya.core.env;

import org.apache.tamaya.Environment;
import org.apache.tamaya.Stage;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
* Created by Anatole on 06.09.2014.
*/
public final class EnvironmentBuilder{

    Map<String,String> contextData = new HashMap<>();
    Environment parent;
    String id;
    String type;
    Stage stage = Stage.DEVELOPMENT;

    private EnvironmentBuilder(String id, String type) {
        Objects.requireNonNull(id);
        this.id = id;
        this.type = type;
    }

    public static final EnvironmentBuilder of(String id, String type) {
        return new EnvironmentBuilder(id, type);
    }

    public EnvironmentBuilder setParent(Environment parent){
        this.parent = parent;
        return this;
    }

    public EnvironmentBuilder set(String key, String value){
        this.contextData.put(key, value);
        return this;
    }

    public EnvironmentBuilder setAll(Map<String,String> values){
        this.contextData.putAll(values);
        return this;
    }

    public EnvironmentBuilder setStage(Stage stage){
        Objects.requireNonNull(stage);
        this.stage = stage;
        return this;
    }

    public String getProperty(String key) {
        return this.contextData.get(key);
    }

    public Environment build() {
        return new BuildableEnvironment(this);
    }
}
