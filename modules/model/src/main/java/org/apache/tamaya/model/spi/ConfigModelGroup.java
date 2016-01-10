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
package org.apache.tamaya.model.spi;

import org.apache.tamaya.Configuration;
import org.apache.tamaya.model.ConfigModel;
import org.apache.tamaya.model.ModelType;
import org.apache.tamaya.model.ValidationResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Default configuration Model for a configuration area.
 */
public class ConfigModelGroup implements ConfigModel {

    private final String name;
    private boolean required;
    private final String provider;
    private List<ConfigModel> childConfigModels = new ArrayList<>();

    public ConfigModelGroup(String name, String provider, ConfigModel... configModels){
        this(name, provider, Arrays.asList(configModels));
    }

    public ConfigModelGroup(String name, String provider, Collection<ConfigModel> configModels){
        this.name = Objects.requireNonNull(name);
        this.provider = provider;
        this.childConfigModels.addAll(configModels);
        this.childConfigModels = Collections.unmodifiableList(childConfigModels);
        for(ConfigModel val: configModels) {
            if(val.isRequired()){
                this.required = true;
                break;
            }
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getProvider() {
        return provider;
    }

    @Override
    public boolean isRequired() {
        return required;
    }

    @Override
    public ModelType getType() {
        return ModelType.Group;
    }

    @Override
    public String getDescription() {
        if(childConfigModels.isEmpty()){
            return null;
        }
        StringBuilder b = new StringBuilder();
        for(ConfigModel val: childConfigModels){
            b.append("  >> ").append(val);
        }
        return b.toString();
    }

    public Collection<ConfigModel> getValidations(){
        return childConfigModels;
    }

    @Override
    public Collection<ValidationResult> validate(Configuration config) {
        List<ValidationResult> result = new ArrayList<>(1);
        for(ConfigModel child: childConfigModels){
            result.addAll(child.validate(config));
        }
        return result;
    }

    @Override
    public String toString(){
        return String.valueOf(getType()) + ", size: " + childConfigModels.size() + ": " + getDescription();
    }

}
