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
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Default configuration Model for a configuration parameter.
 */
public class ParameterValidation extends AbstractValidation {

    private boolean required = false;
    private String regEx;
    private Class type;


    protected ParameterValidation(Builder builder) {
        super(builder.name, builder.description);
        this.required = builder.required;
        this.regEx = builder.regEx;
        this.type = builder.type;
    }

    @Override
    public String getType() {
        return "Parameter";
    }

    public Class getParameterType() {
        return type;
    }

    @Override
    public Collection<ValidationResult> validate(Configuration config) {
        List<ValidationResult> result = new ArrayList<>(1);
        String configValue = config.get(getName());
        if (configValue == null && required) {
            result.add(ValidationResult.ofMissing(this));
        }
        if (configValue != null && regEx != null) {
            if (!configValue.matches(regEx)) {
                result.add(ValidationResult.ofError(this, "Config value not matching expression: " + regEx + ", was " +
                        configValue));
            }
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append(getType()).append(": ").append(getName());
        if (required) {
            b.append(", required: " + required);
        }
        if (regEx != null) {
            b.append(", expression: " + regEx);
        }
        return b.toString();
    }

    public static Builder builder(String name) {
        return new Builder(name);
    }

    public static Validation of(String name, boolean required, String expression) {
        return new Builder(name).setRequired(required).setExpression(expression).build();
    }


    public static Validation of(String name, boolean required) {
        return new Builder(name).setRequired(required).build();
    }

    public static Validation of(String name) {
        return new Builder(name).setRequired(false).build();
    }


    public static class Builder {
        private Class type;
        private String name;
        private String regEx;
        private String description;
        private boolean required;

        public Builder(String name) {
            this.name = Objects.requireNonNull(name);
        }

        public Builder setType(String type) {
            try {
                this.type = Class.forName(type);
            } catch (ClassNotFoundException e) {
                try {
                    this.type = Class.forName("java.lang."+type);
                } catch (ClassNotFoundException e2) {
                    Logger.getLogger(getClass().getName()).log(Level.WARNING, "Failed to load parameter type: " + type, e2);
                }
            }
            return this;
        }

        public Builder setRequired(boolean required) {
            this.required = required;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setExpression(String regEx) {
            this.regEx = regEx;
            return this;
        }

        public Builder setName(String name) {
            this.name = Objects.requireNonNull(name);
            return this;
        }

        public Validation build() {
            return new ParameterValidation(this);
        }
    }
}
