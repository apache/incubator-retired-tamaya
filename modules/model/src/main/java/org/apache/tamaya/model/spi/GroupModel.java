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
import org.apache.tamaya.model.ModelTarget;
import org.apache.tamaya.model.Validation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Default configuration Model for a configuration area.
 */
public class GroupModel implements ConfigModel {

    private final String owner;
    private final String name;
    private boolean required;
    private List<ConfigModel> childModels = new ArrayList<>();

    public GroupModel(String owner, String name, ConfigModel... configModels){
        this(owner, name, Arrays.asList(configModels));
    }

    public GroupModel(String owner, String name, Collection<ConfigModel> configModels){
        this.owner = Objects.requireNonNull(owner);
        this.name = Objects.requireNonNull(name);
        this.childModels.addAll(configModels);
        this.childModels = Collections.unmodifiableList(childModels);
        for(ConfigModel val: configModels) {
            if(val.isRequired()){
                this.required = true;
                break;
            }
        }
    }

    @Override
    public String getOwner() {
        return owner;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isRequired() {
        return required;
    }

    @Override
    public ModelTarget getType() {
        return ModelTarget.Group;
    }

    @Override
    public String getDescription() {
        if(childModels.isEmpty()){
            return null;
        }
        StringBuilder b = new StringBuilder();
        for(ConfigModel val: childModels){
            b.append("  >> ").append(val);
        }
        return b.toString();
    }

    public Collection<ConfigModel> getValidations(){
        return childModels;
    }

    @Override
    public Collection<Validation> validate(Configuration config) {
        List<Validation> result = new ArrayList<>(1);
        for(ConfigModel child: childModels){
            result.addAll(child.validate(config));
        }
        return result;
    }

    @Override
    public String toString(){
        return String.valueOf(getType()) + ", size: " + childModels.size() + ": " + getDescription();
    }

}
