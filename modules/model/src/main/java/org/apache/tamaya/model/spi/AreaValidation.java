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
import org.apache.tamaya.model.ValidationType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Default configuration Model for a configuration section.
 */
public class AreaValidation extends ValidationGroup {

    /**
     * Creates a new builder.
     * @param name the section name.
     * @return a new builder instance.
     */
    public static Builder builder(String name){
        return new Builder(name);
    }

    /**
     * Creates a section validation for the given section.
     * @param name the fully qualified section name
     * @param required flag, if the section is required to be present.
     * @return the Validation instance
     */
    public static Validation of(String name, boolean required){
        return new Builder(name).setRequired(required).build();
    }

    /**
     * Creates a section validation for the given section.
     * @param name the fully qualified section name
     * @param required flag, if the section is required to be present.
     * @param validations additional validations
     * @return
     */
    public static Validation of(String name, boolean required, Validation... validations){
        return new Builder(name).setRequired(required).addValidations(validations).build();
    }

    /**
     * Internal constructor.
     * @param builder the builder, not null.
     */
    protected AreaValidation(Builder builder) {
        super(builder.name, builder.provider, builder.childValidations);
    }

    @Override
    public ValidationType getType(){
        return ValidationType.Section;
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
        if(isRequired() && !present) {
            result.add(ValidationResult.ofMissing(this));
        }
        result.addAll(super.validate(config));
        return result;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append(getType()).append(": " + getName());
        if(isRequired()) {
            b.append(", required: " + isRequired());
        }
        for(Validation val:getValidations()){
             b.append(", ").append(val.toString());
        }
        return b.toString();
    }

    /**
     * Builder for setting up a AreaValidation instance.
     */
    public static class Builder{
        /** The section name. */
        private String name;
        /** The optional provider. */
        private String provider;
        /** The optional description. */
        private String description;
        /** The required flag. */
        private boolean required;
        /** The (optional) custom validations.*/
        private List<Validation> childValidations = new ArrayList<>();

        /**
         * Creates a new Builder.
         * @param sectionName the section name, not null.
         */
        public Builder(String sectionName){
            this.name = Objects.requireNonNull(sectionName);
        }

        /**
         * Add validations.
         * @param validations the validations, not null.
         * @return the Builder for chaining.
         */
        public Builder addValidations(Validation... validations){
            this.childValidations.addAll(Arrays.asList(validations));
            return this;
        }

        /**
         * Add validations.
         * @param validations the validations, not null.
         * @return the Builder for chaining.
         */
        public Builder addValidations(Collection<Validation> validations){
            this.childValidations.addAll(validations);
            return this;
        }

        /**
         * Sets the required flag.
         * @param required zhe flag.
         * @return the Builder for chaining.
         */
        public Builder setRequired(boolean required){
            this.required = required;
            return this;
        }

        /**
         * Set the )optional) description.
         * @param description the description.
         * @return the Builder for chaining.
         */
        public Builder setDescription(String description){
            this.description = description;
            return this;
        }

        /**
         * Set the )optional) provider.
         * @param provider the provider.
         * @return the Builder for chaining.
         */
        public Builder setProvider(String provider){
            this.provider = provider;
            return this;
        }

        /**
         * Set the section name
         * @param name the section name, not null.
         * @return the Builder for chaining.
         */
        public Builder setName(String name){
            this.name = Objects.requireNonNull(name);
            return this;
        }

        /**
         * Build a new Validation instance.
         * @return the new Validation instance, not null.
         */
        public Validation build(){
            return new AreaValidation(this);
        }
    }
}
