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

import java.lang.reflect.Field;
import java.util.Collection;

/**
 * Abstract model of an field used to inject configuration.
 */
public interface ConfiguredField {

    /**
     * Get the field's type.
     * @return the field type, never null.
     */
    Class<?> getType();

    /**
     * Get a list of all target keys for the given field. The first resolvable key normally determines the
     * configuration value injected.
     * @return a list of evaluated keys.
     */
    Collection<String> getConfiguredKeys();

    /**
     * Get the field's name.
     * @return the name, not null.
     */
    String getName();

    /**
     * Get the field's full signature.
     * @return the signature, not null.
     */
    String getSignature();

    /**
     * Get the annotated field.
     * @return the field, not null.
     */
    Field getAnnotatedField();

    /**
     * Actually calls the annotated method on the instance, hereby passing the configuration values evaluated.
     * @param instance the instance, not null.
     * @param config the configuration, not null.
     */
    void configure(Object instance, Configuration config);
}
