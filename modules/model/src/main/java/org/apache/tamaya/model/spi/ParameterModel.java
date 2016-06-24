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
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Default configuration Model for a configuration parameter.
 */
public class ParameterModel extends AbstractConfigModel {
    /** Optional regular expression for validating the value. */
    private final String regEx;
    /** The target type into which the value must be convertible. */
    private final Class<?> type;

    /**
     * Internal constructor.
     * @param builder the builder, not null.
     */
    protected ParameterModel(Builder builder) {
        super(builder.owner, builder.name, builder.required, builder.description);
        this.regEx = builder.regEx;
        this.type = builder.type;
    }

    @Override
    public ModelTarget getType() {
        return ModelTarget.Parameter;
    }

    /**
     * Get the required parameter type.
     *
     * @return the type.
     */
    public Class<?> getParameterType() {
        return type;
    }

    @Override
    public Collection<Validation> validate(Configuration config) {
        List<Validation> result = new ArrayList<>(1);
        String configValue = config.get(getName());
        if (configValue == null && isRequired()) {
            result.add(Validation.ofMissing(this));
        }
        if (configValue != null && regEx != null) {
            if (!configValue.matches(regEx)) {
                result.add(Validation.ofError(this, "Config value not matching expression: " + regEx + ", was " +
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
            b.append(", required: ").append(isRequired());
        }
        if (regEx != null) {
            b.append(", expression: ").append(regEx);
        }
        return b.toString();
    }

    /**
     * Creates a new Builder instance.
     * @param name the fully qualified parameter name.
     * @return a new builder, never null.
     */
    public static Builder builder(String owner, String name) {
        return new Builder(owner, name);
    }

    /**
     * Creates a new ConfigModel
     * @param name the fully qualified parameter name.
     * @param required the required flag.
     * @param expression an optional regular expression to validate a value.
     * @return the new ConfigModel instance.
     */
    public static ConfigModel of(String owner, String name, boolean required, String expression) {
        return new Builder(owner, name).setRequired(required).setExpression(expression).build();
    }

    /**
     * Creates a new ConfigModel
     * @param name the fully qualified parameter name.
     * @param required the required flag.
     * @return the new ConfigModel instance.
     */
    public static ConfigModel of(String owner, String name, boolean required) {
        return new Builder(owner, name).setRequired(required).build();
    }

    /**
     * Creates a new ConfigModel. The parameter will be defined as optional.
     * @param name the fully qualified parameter name.
     * @return the new ConfigModel instance.
     */
    public static ConfigModel of(String owner, String name) {
        return new Builder(owner, name).setRequired(false).build();
    }


    /**
     * A new Builder for creating ParameterModel instances.
     */
    public static class Builder {
        /** The parameter's target type. */
        private Class<?> type;
        /** The owner. */
        private String owner;
        /** The fully qualified parameter name. */
        private String name;
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
        public Builder(String owner, String name) {
            this.owner = Objects.requireNonNull(owner);
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
                    this.type = Class.forName("java.ui.lang."+type);
                } catch (ClassNotFoundException e2) {
                    Logger.getLogger(getClass().getName()).log(Level.INFO, "Failed to load parameter type: " + type, e2);
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
         * Sets the owner name.
         * @param owner the owner name, not null.
         * @return the Builder for chaining
         */
        public Builder setOwner(String owner) {
            this.owner = Objects.requireNonNull(owner);
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
         * Creates a new ConfigModel with the given parameters.
         * @return a new ConfigModel , never null.
         */
        public ConfigModel build() {
            return new ParameterModel(this);
        }
    }
}
