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
import org.apache.tamaya.model.spi.AbstractModel;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * Models a partial configuration configModel result.
 */
public final class ValidationResult {
    /**
     * the config section.
     */
    private final ConfigModel configModel;
    /**
     * The configModel result.
     */
    private final ValidationState result;
    /**
     * The configModel message.
     */
    private final String message;

    /**
     * Creates a new ValidationResult.
     *
     * @param configModel the configModel item, not null.
     */
    public static ValidationResult ofValid(ConfigModel configModel) {
        return new ValidationResult(configModel, ValidationState.VALID, null);
    }

    /**
     * Creates a new ValidationResult.
     *
     * @param configModel the configModel item, not null.
     */
    public static ValidationResult ofMissing(ConfigModel configModel) {
        return new ValidationResult(configModel, ValidationState.MISSING, null);
    }


    /**
     * Creates a new ValidationResult.
     *
     * @param configModel the configModel item, not null.
     *                   @param message Additional message to be shown (optional).
     */
    public static ValidationResult ofMissing(ConfigModel configModel, String message) {
        return new ValidationResult(configModel, ValidationState.MISSING, message);
    }

    /**
     * Creates a new ValidationResult.
     *
     * @param configModel the configModel item, not null.
     */
    public static ValidationResult ofError(ConfigModel configModel, String error) {
        return new ValidationResult(configModel, ValidationState.ERROR, error);
    }

    /**
     * Creates a new ValidationResult.
     *
     * @param configModel the configModel item, not null.
     */
    public static ValidationResult ofWarning(ConfigModel configModel, String warning) {
        return new ValidationResult(configModel, ValidationState.WARNING, warning);
    }

    /**
     * Creates a new ValidationResult.
     *
     * @param configModel the configModel item, not null.
     */
    public static ValidationResult ofDeprecated(ConfigModel configModel, String alternateUsage) {
        return new ValidationResult(configModel, ValidationState.DEPRECATED, alternateUsage != null ? "Use instead: " + alternateUsage : null);
    }

    /**
     * Creates a new ValidationResult.
     *
     * @param configModel the configModel item, not null.
     */
    public static ValidationResult ofDeprecated(ConfigModel configModel) {
        return new ValidationResult(configModel, ValidationState.DEPRECATED, null);
    }

    /**
     * Creates a new ValidationResult.
     *
     * @param key the name/key
     * @return a corresponding configModel item
     */
    public static ValidationResult ofUndefined(final String key, final ModelType type, String provider) {
        return new ValidationResult(new AbstractModel(key, false, "Undefined key: " + key, provider) {

            @Override
            public ModelType getType() {
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
     * @param configModel the configModel item, not null.
     * @param result     the configModel result, not null.
     * @param message    the detail message.
     */
    public static ValidationResult of(ConfigModel configModel, ValidationState result, String message) {
        return new ValidationResult(configModel, result, message);
    }


    /**
     * Constructor.
     *
     * @param configModel the configModel item, not null.
     * @param result     the configModel result, not null.
     * @param message    the detail message.
     */
    private ValidationResult(ConfigModel configModel, ValidationState result, String message) {
        this.message = message;
        this.configModel = Objects.requireNonNull(configModel);
        this.result = Objects.requireNonNull(result);
    }

    /**
     * Get the configModel section.
     *
     * @return the section, never null.
     */
    public ConfigModel getConfigModel() {
        return configModel;
    }

    /**
     * Get the configModel result.
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
            return result + ": " + configModel.getName() + " (" + configModel.getType() + ") -> " + message + '\n';
        }
        return result + ": " + configModel.getName() + " (" + configModel.getType() + ")";
    }
}
