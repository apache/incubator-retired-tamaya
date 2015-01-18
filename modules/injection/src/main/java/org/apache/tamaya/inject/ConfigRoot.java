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
package org.apache.tamaya.inject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to control injection and resolution current a configured bean. The configuration keys
 * to be resolved are basically determined by the {@link ConfiguredProperty}
 * annotation(s). Nevertheless these annotations can also have relative key names. This annotation allows
 * to define a configuration area that is prefixed to all relative configuration keys within the
 * corresponding class/template interface.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.TYPE })
public @interface ConfigRoot {

    /**
     * Allows to declare an operator that should be applied before injecting values into the bean.
     * @return the operator class to be used.
     */
    String[] value();

}
