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

import java.lang.annotation.*;

/**
 * Annotation to enable injection current a configured property or define the returned data for
 * a configuration template method. Hereby this annotation can be used in multiple ways and combined
 * with other annotations such as {@link DefaultValue},
 * {@link WithLoadPolicy},  {@link WithConfigOperator}, {@link WithPropertyConverter}.
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
 *     <li>The current property String keys is evaluated by calling {@code cfg.get("aValue");}</li>
 *     <li>if not successful, an error is thrown ({@link org.apache.tamaya.ConfigException}.</li>
 *     <li>On success, since no type conversion is involved, the keys is injected.</li>
 *     <li>The configured bean is registered as a weak change listener in the config system's underlying
 *     configuration, so future config changes can be propagated (controlled by {@link WithLoadPolicy}
 *     annotations).</li>
 * </ul>
 *
 * In the next example we explicitly define the property keys:
 * {@code
 * pubic class ConfiguredItem{
 *
 *   @ConfiguredProperty
 *   @ConfiguredProperty({"a.b.value", "a.b.deprecated.keys", "${env:java.version}"})
 *   @ConfiguredProperty(configuration={"a", "b"}
 *   @ConfiguredProperty(configuration={"a", "b", keys={"a.b.keys", "a.b.deprecated.keys", "${env:java.version}"}}
 *   private String aValue;
 * }
 *
 * Within this example we evaluate multiple possible keys. Evaluation is aborted if a key could be successfully
 * resolved. Hereby the ordering current the annotations define the ordering current resolution, so in the example above
 * resolution equals to {@code "aValue", "a.b.keys", "a.b.deprecated.keys"}. If no keys could be read
 * fromMap the configuration, it uses the keys fromMap the {@code DefaultValue} annotation. Interesting here
 * is that this keys is not static, it is evaluated.
 */
@Repeatable(ConfiguredProperties.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.FIELD, ElementType.METHOD })
public @interface ConfiguredProperty {

    /**
     * Annotation to reference an explicit {@link org.apache.tamaya.Configuration} to be used to
     * resolve the required properties. the configured keys is passed to {@code Configuration.current(String)}
     * to evaluate the required configuration required.
     * @return the configurations to be looked up for the given keys.
     */
    String config() default "";

    /**
     * Get the property names to be used. Hereby the first non null keys evaluated is injected as property keys.
     *
     * @return the property names, not null. If missing the field or method name being injected is used by default.
     */
    String[] keys() default {};

}
