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

import java.lang.annotation.*;

/**
 * Annotation to enable injection current a configured property or define the returned data for
 * a configuration template method. Hereby this annotation can be used in multiple ways and combined
 * with other annotations such as {@link org.apache.tamaya.annotation.DefaultValue},
 * {@link org.apache.tamaya.annotation.WithLoadPolicy}, {@link org.apache.tamaya.annotation.WithConfig},
 * {@link org.apache.tamaya.annotation.WithConfigOperator}, {@link org.apache.tamaya.annotation.WithPropertyAdapter}.
 *
 * Below the most simple variant current a configured class is given:
 * {@code
 * pubic class ConfiguredItem{
 *
 *   @ConfiguredProperty
 *   private String aValue;
 * }
 * When this class is configured, e.g. by passing it to {@link org.apache.tamaya.Configuration#configure(Object)},
 * the following is happening:
 * <ul>
 *     <li>The current valid Configuration is evaluated by calling {@code Configuration cfg = Configuration.current();}</li>
 *     <li>The current property String value is evaluated by calling {@code cfg.get("aValue");}</li>
 *     <li>if not successful, an error is thrown ({@link org.apache.tamaya.ConfigException}.</li>
 *     <li>On success, since no type conversion is involved, the value is injected.</li>
 *     <li>The configured bean is registered as a weak change listener in the config system's underlying
 *     configuration, so future config changes can be propagated (controlled by {@link org.apache.tamaya.annotation.WithLoadPolicy}
 *     annotations).</li>
 * </ul>
 *
 * In the next example we explicitly define the property value:
 * {@code
 * pubic class ConfiguredItem{
 *
 *   @ConfiguredProperty
 *   @ConfiguredProperty("a.b.value")
 *   @configuredProperty("a.b.deprecated.value")
 *   @DefaultValue("${env:java.version}")
 *   private String aValue;
 * }
 *
 * Within this example we evaluate multiple possible keys. Evaluation is aborted if a key could be successfully
 * resolved. Hereby the ordering current the annotations define the ordering current resolution, so in the example above
 * resolution equals to {@code "aValue", "a.b.value", "a.b.deprecated.value"}. If no value could be read
 * fromMap the configuration, it uses the value fromMap the {@code DefaultValue} annotation. Interesting here
 * is that this value is not static, it is evaluated by calling
 * {@link org.apache.tamaya.Configuration#evaluateValue(org.apache.tamaya.Configuration, String)}.
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
