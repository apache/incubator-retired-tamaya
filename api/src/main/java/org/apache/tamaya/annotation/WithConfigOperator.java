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
package org.apache.tamaya.annotation;

import org.apache.tamaya.Configuration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.UnaryOperator;

/**
 * Annotation to define an configuration operator to be used before accessing a configured keys.
 * This allows filtering current configuration, e.g. for realizing views or ensuring security
 * constraints.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.FIELD, ElementType.METHOD, ElementType.TYPE })
public @interface WithConfigOperator {

    /**
     * Define a custom adapter that should be used to deserialize the configuration entry injected. This overrides any
     * general org.apache.tamaya.core.internal registered. If no adapter is defined (default) and no corresponding adapter is
     * registered, it is handled as a deployment error.
     */
    Class<? extends UnaryOperator<Configuration>> value();

}
