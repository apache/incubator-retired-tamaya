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
package org.apache.tamaya.inject.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to define when values for {@link DynamicValue}s are evaluated/updated and when registered listeners
 * are informed on changes identified..
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
public @interface ConfigEvaluation {

    /**
     * The {@link LoadPolicy} used for a {@link DynamicValue} determining how often a configuration value is loaded,
     * default is LoadPolicy#ALWAYS.
     */
    LoadPolicy load() default LoadPolicy.ALWAYS;

    /**
     * The {@link UpdatePolicy} used for a {@link DynamicValue} determining, what happens if a value change was
     * triggered, default is UpdatePolicy#IMMEDEATE.
     */
    UpdatePolicy update() default UpdatePolicy.IMMEDEATE;

}
