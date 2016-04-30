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

/**
 * Enum type describing the different validation results supported.
 */
public enum ValidationResult {
    /**
     * The validated item is valid
     */
    VALID,
    /**
     * The validated item is deprecated.
     */
    DEPRECATED,
    /**
     * The validated item is correct, but the value is worth a warning.
     */
    WARNING,
    /**
     * The given section or parameter is not a defined/validated item. It may be still valid, but typically,
     * when validation is fully implemented, such a parameter or section should be removed.
     */
    UNDEFINED,
    /**
     * A required parameter or section is missing.
     */
    MISSING,
    /**
     * The validated item has an invalid value.
     */
    ERROR;

    /**
     * Method to quickly evaluate if the current state is an error state.
     *
     * @return true, if the state is not ERROR or MISSING.
     */
    boolean isError() {
        return this.ordinal() == MISSING.ordinal() || this.ordinal() == ERROR.ordinal();
    }
}
