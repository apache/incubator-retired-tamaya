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
package org.apache.tamaya.annot;

import java.lang.annotation.*;

/**
 * Annotation to enable injection of a configured property.
 */
@Repeatable(ConfiguredProperties.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.FIELD, ElementType.METHOD })
public @interface ConfiguredProperty {

    /**
     * Get the property names to be used. Hereby the first non null value evaluated is injected as property value.
     *
     * @return the property names, not null. If missing the field or method name being injected is used by default.
     */
    String value() default "";

}
