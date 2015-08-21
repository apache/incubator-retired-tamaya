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
import org.apache.tamaya.model.ValidationType;

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
    /** Optional regular expression for validating the value. */
    private String regEx;
    /** The target type into which the value must be convertible. */
    private Class type;

    /**
     * Internal constructor.
     * @param builder the builder, not null.
     */
    protected ParameterValidation(Builder builder) {
        super(builder.name, builder.required, builder.description, builder.provider);
        this.regEx = builder.regEx;
        this.type = builder.type;
    }

    @Override
    public ValidationType getType() {
        return ValidationType.Parameter;
    }

    /**
     * Get the required parameter type.
     *
     * @return the type.
     */
    public Class getParameterType() {
        return type;
    }

    @Override
    public Collection<ValidationResult> validate(Configuration config) {
        List<ValidationResult> result = new ArrayList<>(1);
        String configValue = config.get(getName());
        if (configValue == null && isRequired()) {
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
        if (isRequired()) {
            b.append(", required: " + isRequired());
        }
        if (regEx != null) {
            b.append(", expression: " + regEx);
        }
        return b.toString();
    }

    /**
     * Creates a new Builder instance.
     * @param name the fully qualified parameter name.
     * @return
     */
    public static Builder builder(String name) {
        return new Builder(name);
    }

    /**
     * Creates a new Validation
     * @param name the fully qualified parameter name.
     * @param required the required flag.
     * @param expression an optional regular expression to validate a value.
     * @return the new Validation instance.
     */
    public static Validation of(String name, boolean required, String expression) {
        return new Builder(name).setRequired(required).setExpression(expression).build();
    }

    /**
     * Creates a new Validation
     * @param name the fully qualified parameter name.
     * @param required the required flag.
     * @return the new Validation instance.
     */
    public static Validation of(String name, boolean required) {
        return new Builder(name).setRequired(required).build();
    }

    /**
     * Creates a new Validation. The parameter will be defined as optional.
     * @param name the fully qualified parameter name.
     * @return the new Validation instance.
     */
    public static Validation of(String name) {
        return new Builder(name).setRequired(false).build();
    }


    /**
     * A new Builder for creating ParameterValidation instances.
     */
    public static class Builder {
        /** The parameter's target type. */
        private Class type;
        /** The fully qualified parameter name. */
        private String name;
        /** The optional provider. */
        private String provider;
        /** The optional validation expression. */
        private String regEx;
        /** The optional description. */
        private String description;
        /** The required flag. */
        private boolean required;

        /**
         * Creates a new Builder.
         * @param name the fully qualified parameter name, not null.
         */
        public Builder(String name) {
            this.name = Objects.requireNonNull(name);
        }

        /**
         * Sets the target type.
         * @param type the type, not null.
         * @return the Builder for chaining
         */
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

        /**
         * Sets the required flag.
         * @param required the required flag.
         * @return the Builder for chaining
         */
        public Builder setRequired(boolean required) {
            this.required = required;
            return this;
        }

        /**
         * Sets the optional description
         * @param description the description
         * @return the Builder for chaining
         */
        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        /**
         * Sets the optional validation expression
         * @param expression the validation expression
         * @return the Builder for chaining
         */
        public Builder setExpression(String expression) {
            this.regEx = expression;
            return this;
        }

        /**
         * Sets the fully qualified parameter name.
         * @param name the fully qualified parameter name, not null.
         * @return the Builder for chaining
         */
        public Builder setName(String name) {
            this.name = Objects.requireNonNull(name);
            return this;
        }

        /**
         * Set the provider.
         * @param provider the provider.
         * @return the Builder for chaining
         */
        public Builder setProvider(String provider) {
            this.provider = provider;
            return this;
        }

        /**
         * Creates a new Validation with the given parameters.
         * @return a new Validation , never null.
         */
        public Validation build() {
            return new ParameterValidation(this);
        }
    }
}
