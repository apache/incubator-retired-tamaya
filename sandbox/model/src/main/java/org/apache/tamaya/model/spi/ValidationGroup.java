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
import org.apache.tamaya.model.Validation;
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
public class ValidationGroup implements Validation {

    private String name;
    private List<Validation> childValidations = new ArrayList<>();

    public ValidationGroup(String name, Validation... validations){
        this(name, Arrays.asList(validations));
    }

    public ValidationGroup(Collection<Validation> validations){
        this("", validations);
    }

    public ValidationGroup(Validation... validations){
        this("", Arrays.asList(validations));
    }

    protected ValidationGroup(String name, Collection<Validation> validations) {
        this.name = Objects.requireNonNull(name);
        this.childValidations.addAll(validations);
        this.childValidations = Collections.unmodifiableList(childValidations);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getType() {
        return "Group";
    }

    @Override
    public String getDescription() {
        StringBuilder b = new StringBuilder();
        for(Validation val:childValidations){
            b.append("  >> " + val);
        }
        return b.toString();
    }

    public Collection<Validation> getValidations(){
        return childValidations;
    }

    @Override
    public Collection<ValidationResult> validate(Configuration config) {
        List<ValidationResult> result = new ArrayList<>(1);
        for(Validation child: childValidations){
            result.addAll(child.validate(config));
        }
        return result;
    }

    @Override
    public String toString(){
        StringBuilder b = new StringBuilder();
        b.append(getType()).append(", size: ").append(childValidations.size()).append(": ").append(getDescription());
        return b.toString();
    }

}
