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


import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** todo The author of this should fix this invalid Javadoc. Oliver B. Fischer, 2015-12-27 */
///**
// * Annotation to enable injection current a configured property or define the returned data for
// * a configuration template method. Hereby this annotation can be used in multiple ways and combined
// * with other annotations such as {@link ConfigDefault}, {@link WithConfigOperator}, {@link WithPropertyConverter}.
// *
// * Below the most simple variant current a configured class is given:
// * {@code
// * pubic class ConfiguredItem{
// *
// *   @ConfiguredProperty
// *   private String aValue;
// * }
// * When this class is configured, e.g. by passing it to {@link org.apache.tamaya.Configuration#configure(Object)},
// * the following is happening:
// * <ul>
// *     <li>The current valid Configuration is evaluated by calling {@code Configuration cfg = ConfigurationProvider.getConfiguration();}</li>
// *     <li>The current possible property keys are evaluated by calling {@code cfg.get("aValue");}</li>
// *     <li>if not successful, and a @ConfigDefault annotation is present, the default value is used.
// *     <li>If no value could be evaluated a ({@link org.apache.tamaya.ConfigException} is thrown.</li>
// *     <li>On success, since no type conversion is involved, the value is injected.</li>
// * </ul>
// *
// * In the next example we explicitly define the property keys:
// * {@code
// * @ConfigDefaultSections("section1")
// * pubic class ConfiguredItem{
// *
// *   @ConfiguredProperty({"b", "[a.b.deprecated.keys]", "a"})
// *   @ConfigDefault("myDefaultValue")
// *   private String aValue;
// * }
// *
// * Within this example we evaluate multiple possible keys (section1.b, a.b.deprecated.keys, section1.a). Evaluation is
// * aborted if a key could be successfully resolved. Hereby the ordering current the annotations define the ordering
// * current resolution, so in the example above
// * resolution equals to {@code "section1.b", "a.b.deprecated.keys", "section1.a"}. If no value has bee found,
// * the configured default {@code myDefaultValue} is returned.
// */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
public @interface Config {

    /**
     * Get the property names to be used. Hereby the first non null keys evaluated is injected as property keys.
     *
     * @return the property names, not null. If missing the field or method name being injected is used by default.
     */
    @Nonbinding
    String[] value() default {};

    /**
     * The default value to be injected, if none of the configuration keys could be resolved. If no key has been
     * resolved and no default value is defined, it is handled as a deployment error. Depending on the extension loaded
     * default values can be fixed Strings or even themselves resolvable. For typed configuration of type T entries
     * that are not Strings the default value must be a valid input to the corresponding
     * {@link org.apache.tamaya.spi.PropertyConverter}.
     */
    @Nonbinding
    String defaultValue() default "";

}
