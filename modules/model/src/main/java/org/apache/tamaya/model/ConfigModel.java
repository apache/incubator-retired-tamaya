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

import java.util.Collection;

/**
 * Base structure describing a validated item, by default a parameter or a section.
 */
public interface ConfigModel {

    /**
     * Get the type of item that is modelled.
     * @return the modelled type, never null.
     */
    ModelTarget getType();

    /**
     * Get the item's name, it should minimally describe the validation. Examples are:
     * <pre>
     *     Sections: a.b.c
     *     Params: a.b.c.paramName
     *     Filter: a.b.c.FilterImplClass
     *     Dependency: mydepClassname
     *     CombinationPolicy: a.b.c.MyCombinationPolicyClass
     * </pre>
     * @return the item's name.
     */
    String getName();

    /**
     * Check if this validation is a required one.
     * @return true, if this validation is required.
     */
    boolean isRequired();

    /**
     * Get an description of the item, using the default locale. The description is basically optional
     * though it is higly recommended to provide a description, so the validation issues is well
     * resolvable.
     *
     * @return the description required, or null.
     */
    String getDescription();

    /**
     * Validates the item and all its children against the given configuration.
     *
     * @param config the configuration to be validated against, not null.
     * @return the validation result, or null, if not applicable.
     */
    Collection<Validation> validate(Configuration config);

}
