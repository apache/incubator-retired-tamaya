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

import java.lang.reflect.Method;
import java.util.Collection;

/**
 * Abstract model of an method used to inject configuration.
 */
public interface ConfiguredMethod {

    /**
     * Get the key required to be evaluated.
     * @return the configured keys.
     */
    Collection<String> getConfiguredKeys();

    /**
     * Get the methods input parameter types.
     * @return the method param types, not null.
     */
    Class<?>[] getParameterTypes();

    /**
     * Get the underlying method reflection type.
     * @return the method element.
     */
    Method getAnnotatedMethod();

    /**
     * Get the method's name, e.g. {@code setName}.
     * @return the name, never null.
     */
    String getName();

    /**
     * Get the methods signature, e.g. {@code void setName(String)}.
     * @return he signature, never null.
     */
    String getSignature();

    /**
     * This method actually configures the given method on a instance of its parent type.
     * This evaluates the initial key closure and applies changes on the field.
     *
     * @param instance the target instance, not null.
     * @param config the configuration, not null.
     * @throws org.apache.tamaya.ConfigException if evaluation or conversion failed.
     */
    void configure(Object instance, Configuration config);
}
