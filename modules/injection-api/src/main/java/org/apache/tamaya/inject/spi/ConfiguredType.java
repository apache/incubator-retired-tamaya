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
package org.apache.tamaya.inject.spi;

import org.apache.tamaya.Configuration;

import java.util.Collection;

/**
 * Abstract model of an type used to inject configuration. This also includes instances passed programmatically.
 */
public interface ConfiguredType{

    /**
     * Get the type's class.
     * @return
     */
    Class getType();

    /**
     * Get the type's name.
     * @return the type's name.
     */
    String getName();

    /**
     * Get the registered configured fields.
     * @return the registered configured fields, never null.
     */
    public Collection<ConfiguredField> getConfiguredFields();

    /**
     * Get the registered configured methods.
     * @return the registered configured methods, never null.
     */
    Collection<ConfiguredMethod> getConfiguredMethods();

    /**
     * This method actually configures an instance using the given configuration data.
     *
     * @param instance The instance to be configured, not null.
     * @param config  the target config, not null.
     */
    void configure(Object instance, Configuration config);


}
