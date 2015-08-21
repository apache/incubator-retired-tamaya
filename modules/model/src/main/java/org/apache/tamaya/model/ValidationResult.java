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
package org.apache.tamaya.model;

import org.apache.tamaya.Configuration;
import org.apache.tamaya.model.spi.AbstractValidation;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * Models a partial configuration validation result.
 */
public final class ValidationResult {
    /**
     * the config section.
     */
    private Validation validation;
    /**
     * The validation result.
     */
    private ValidationState result;
    /**
     * The validation message.
     */
    private String message;

    /**
     * Creates a new ValidationResult.
     *
     * @param validation the validation item, not null.
     */
    public static ValidationResult ofValid(Validation validation) {
        return new ValidationResult(validation, ValidationState.VALID, null);
    }

    /**
     * Creates a new ValidationResult.
     *
     * @param validation the validation item, not null.
     */
    public static ValidationResult ofMissing(Validation validation) {
        return new ValidationResult(validation, ValidationState.MISSING, null);
    }


    /**
     * Creates a new ValidationResult.
     *
     * @param validation the validation item, not null.
     *                   @param message Additional message to be shown (optional).
     */
    public static ValidationResult ofMissing(Validation validation, String message) {
        return new ValidationResult(validation, ValidationState.MISSING, message);
    }

    /**
     * Creates a new ValidationResult.
     *
     * @param validation the validation item, not null.
     */
    public static ValidationResult ofError(Validation validation, String error) {
        return new ValidationResult(validation, ValidationState.ERROR, error);
    }

    /**
     * Creates a new ValidationResult.
     *
     * @param validation the validation item, not null.
     */
    public static ValidationResult ofWarning(Validation validation, String warning) {
        return new ValidationResult(validation, ValidationState.WARNING, warning);
    }

    /**
     * Creates a new ValidationResult.
     *
     * @param validation the validation item, not null.
     */
    public static ValidationResult ofDeprecated(Validation validation, String alternateUsage) {
        return new ValidationResult(validation, ValidationState.DEPRECATED, alternateUsage != null ? "Use instead: " + alternateUsage : null);
    }

    /**
     * Creates a new ValidationResult.
     *
     * @param validation the validation item, not null.
     */
    public static ValidationResult ofDeprecated(Validation validation) {
        return new ValidationResult(validation, ValidationState.DEPRECATED, null);
    }

    /**
     * Creates a new ValidationResult.
     *
     * @param key the name/key
     * @return a corresponding validation item
     */
    public static ValidationResult ofUndefined(final String key, final ValidationType type, String provider) {
        return new ValidationResult(new AbstractValidation(key, false, "Undefined key: " + key, provider) {

            @Override
            public ValidationType getType() {
                return type;
            }

            @Override
            public Collection<ValidationResult> validate(Configuration config) {
                return Collections.emptySet();
            }
        }, ValidationState.UNDEFINED, null);
    }


    /**
     * Constructor.
     *
     * @param validation the validation item, not null.
     * @param result     the validation result, not null.
     * @param message    the detail message.
     */
    public static ValidationResult of(Validation validation, ValidationState result, String message) {
        return new ValidationResult(validation, result, message);
    }


    /**
     * Constructor.
     *
     * @param validation the validation item, not null.
     * @param result     the validation result, not null.
     * @param message    the detail message.
     */
    private ValidationResult(Validation validation, ValidationState result, String message) {
        this.message = message;
        this.validation = Objects.requireNonNull(validation);
        this.result = Objects.requireNonNull(result);
    }

    /**
     * Get the validation section.
     *
     * @return the section, never null.
     */
    public Validation getValidation() {
        return validation;
    }

    /**
     * Get the validation result.
     *
     * @return the result, never null.
     */
    public ValidationState getResult() {
        return result;
    }

    /**
     * Get the detail message.
     *
     * @return the detail message, or null.
     */
    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        if (message != null) {
            return result + ": " + validation.getName() + " (" + validation.getType() + ") -> " + message + '\n';
        }
        return result + ": " + validation.getName() + " (" + validation.getType() + ")";
    }
}
