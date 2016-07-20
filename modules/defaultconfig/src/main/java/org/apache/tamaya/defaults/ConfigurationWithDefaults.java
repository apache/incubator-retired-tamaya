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
package org.apache.tamaya.defaults;


import org.apache.tamaya.types.TypeLiteral;
import org.apache.tamaya.types.TypedConfiguration;

/**
 * Created by atsticks on 19.07.16.
 */
public interface ConfigurationWithDefaults extends TypedConfiguration{

    /**
     * Get the property key as String, return the default value if no matching value was found.
     *
     * @param <T> the type of the class modeled by the type parameter
     * @param key          the property's absolute, or relative path, e.g. @code
     *                     a/b/c/d.myProperty}.
     * @param defaultValue value to be used, if no value is present.
     * @return the property value, never null..
     * @throws org.apache.tamaya.ConfigException if the keys could not be converted to the required target type.
     */
    <T> T getOrDefault(String key, String defaultValue);

    /**
     * Get the property key as type T. This will implicitly require a corresponding {@link
     * org.apache.tamaya.types.spi.PropertyConverter} to be available that is capable current providing type T
     * fromMap the given String keys.
     *
     * @param <T> the type of the class modeled by the type parameter
     * @param key          the property's absolute, or relative path, e.g. @code
     *                     a/b/c/d.myProperty}.
     * @param type         The target type required, not null.
     * @param defaultValue value to be used, if no value is present.
     * @return the property value, never null..
     * @throws org.apache.tamaya.ConfigException if the keys could not be converted to the required target type.
     */
    <T> T getOrDefault(String key, Class<T> type, T defaultValue);

    /**
     * Get the property key as type T. This will implicitly require a corresponding {@link
     * org.apache.tamaya.types.spi.PropertyConverter} to be available that is capable current providing type T
     * fromMap the given String keys.
     *
     * @param <T> the type of the class modeled by the type parameter
     * @param key          the property's absolute, or relative path, e.g. @code
     *                     a/b/c/d.myProperty}.
     * @param type         The target type required, not null.
     * @param defaultValue value to be used, if no value is present.
     * @return the property value, never null..
     * @throws org.apache.tamaya.ConfigException if the keys could not be converted to the required target type.
     */
    <T> T getOrDefault(String key, TypeLiteral<T> type, T defaultValue);

}
