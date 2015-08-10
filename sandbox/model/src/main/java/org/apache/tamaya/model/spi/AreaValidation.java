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
import org.apache.tamaya.model.ValidationResult;
import org.apache.tamaya.model.Validation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Default configuration Model for a configuration area.
 */
public class AreaValidation extends ValidationGroup {

    private boolean required = false;

    public static Builder builder(String name){
        return new Builder(name);
    }

    public static Validation of(String name, boolean required){
        return new Builder(name).setRequired(required).build();
    }

    public static Validation of(String name, boolean required, Validation... validations){
        return new Builder(name).setRequired(required).addValidations(validations).build();
    }

    protected AreaValidation(Builder builder) {
        super(builder.name, builder.childValidations);
        this.required = builder.required;
    }

    @Override
    public String getType(){
        return "Area";
    }

    @Override
    public Collection<ValidationResult> validate(Configuration config) {
        Map<String,String> map = config.getProperties();
        String lookupKey = getName() + '.';
        boolean present = false;
        for(String key:map.keySet()){
            if(key.startsWith(lookupKey)){
                present = true;
                break;
            }
        }
        List<ValidationResult> result = new ArrayList<>(1);
        if(required && !present) {
            result.add(ValidationResult.ofMissing(this));
        }
        result.addAll(super.validate(config));
        return result;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append(getType()).append(": " + getName());
        if(required) {
            b.append(", required: " + required);
        }
        for(Validation val:getValidations()){
             b.append(", ").append(val.toString());
        }
        return b.toString();
    }


    public static class Builder{
        private String name;
        private String description;
        private boolean required;
        private List<Validation> childValidations = new ArrayList<>();

        public Builder(String areaName){
            this.name = Objects.requireNonNull(areaName);
        }

        public Builder addValidations(Validation... validations){
            this.childValidations.addAll(Arrays.asList(validations));
            return this;
        }

        public Builder addValidations(Collection<Validation> validations){
            this.childValidations.addAll(validations);
            return this;
        }

        public Builder addParameter(ParameterValidation parameterConfig){
            this.childValidations.add(parameterConfig);
            return this;
        }

        public Builder setRequired(boolean required){
            this.required = required;
            return this;
        }

        public Builder setDescription(String description){
            this.description = description;
            return this;
        }

        public Builder setName(String name){
            this.name = Objects.requireNonNull(name);
            return this;
        }

        public Validation build(){
            return new AreaValidation(this);
        }
    }
}
