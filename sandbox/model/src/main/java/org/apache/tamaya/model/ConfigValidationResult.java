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

import java.util.Objects;

/**
 * Models a partial configuration validation result.
 */
public class ConfigValidationResult {
    /**
     * the config section.
     */
    private ConfigSection section;
    /**
     * the config parameter (may be null).
     */
    private ConfigParameter parameter;
    /**
     * The validation result.
     */
    private ValidationResult result;
    /**
     * The validation message.
     */
    private String message;

    /**
     * Constructor.
     *
     * @param section   the section, not null.
     * @param parameter the corresponding parameter, or null.
     * @param result    the validation result, not null.
     * @param message   the detail message.
     */
    public ConfigValidationResult(ConfigSection section, ConfigParameter parameter, ValidationResult result, String message) {
        this.message = message;
        this.parameter = parameter;
        this.section = Objects.requireNonNull(section);
        this.result = Objects.requireNonNull(result);
    }

    /**
     * Constructor.
     *
     * @param section the section, not null.
     * @param result  the validation result, not null.
     * @param message the detail message.
     */
    public ConfigValidationResult(ConfigSection section, ValidationResult result, String message) {
        this.message = message;
        this.section = Objects.requireNonNull(section);
        this.result = Objects.requireNonNull(result);
    }

    /**
     * Get the validated section.
     *
     * @return the section, never null.
     */
    public ConfigSection getSection() {
        return section;
    }

    /**
     * Get the validated parameter.
     *
     * @return the parameter, or null.
     */
    public ConfigParameter getParameter() {
        return parameter;
    }

    /**
     * Get the validation result.
     *
     * @return the result, never null.
     */
    public ValidationResult getResult() {
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
        return "ConfigValidationResult{" +
                "section=" + section +
                ", parameter=" + parameter +
                ", result=" + result +
                ", message='" + message + '\'' +
                '}';
    }
}
