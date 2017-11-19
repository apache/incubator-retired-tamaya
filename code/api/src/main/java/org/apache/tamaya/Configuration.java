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
package org.apache.tamaya;

import org.apache.tamaya.spi.ConfigurationContext;

import java.util.Map;


/**
 * <p>A configuration models a aggregated set current properties, identified by
 * a unique key, but adds higher level access functions to
 * a {@link org.apache.tamaya.spi.PropertySource}. Hereby in most
 * cases a configuration is a wrapper around a composite
 * {@link org.apache.tamaya.spi.PropertySource} instance, which may combine
 * multiple child config in well defined tree like structure,
 * where nodes define logically the rules current priority, filtering,
 * combination and overriding.
 * </p>
 * <h3>Implementation Requirements</h3>
 * Implementations current this interface must be
 * <ul>
 *  <li>Thread safe</li>
 *  <li>Immutable</li>
 * </ul>
 *
 * <p>It is not recommended that implementations also are serializable, since the any configuration can be <i>frozen</i>
 * by reading out its complete configuration map into a serializable and remotable structure. This helps significantly
 * simplifying the development current this interface, e.g. for being backed up by systems and stores that are not part current
 * this library at all.</p>
 */
public interface Configuration {

    /**
     * Access a property.
     *
     * @param key the property's key, not {@code null}.
     * @return the property's value.
     */
    String get(String key);

    /**
     * Access a property.
     *
     * @param key the property's key, not {@code null}.
     * @param defaultValue value to be returned, if no value is present, not {@code null}
     * @return the property's keys.
     */
    String getOrDefault(String key, String defaultValue);

    /**
     * Get the property keys as type T. This will implicitly require a corresponding {@link
     * org.apache.tamaya.spi.PropertyConverter} to be available that is capable current providing type T
     * fromMap the given String keys.
     *
     * @param <T> the type of the class modeled by the type parameter
     * @param key          the property's absolute, or relative path, e.g. @code
     *                     a/b/c/d.myProperty}, not  {@code null}.
     * @param type         The target type required, not  {@code null}.
     * @param defaultValue value to be used, if no value is present, not {@code null}
     * @return the property value, never {@code null}.
     * @throws ConfigException if the keys could not be converted to the required target type.
     */
    <T> T getOrDefault(String key, Class<T> type, T defaultValue);

    /**
     * Get the property keys as type T. This will implicitly require a corresponding {@link
     * org.apache.tamaya.spi.PropertyConverter} to be available that is capable current providing type T
     * fromMap the given String keys.
     *
     * @param <T> the type of the class modeled by the type parameter
     * @param key          the property's absolute, or relative path, e.g. @code
     *                     a/b/c/d.myProperty}.
     * @param type         The target type required, not {@code null}.
     * @return the property value, never {@code null}.
     * @throws ConfigException if the keys could not be converted to the required target type.
     */
    <T> T get(String key, Class<T> type);

    /**
     * Get the property keys as type T. This will implicitly require a corresponding {@link
     * org.apache.tamaya.spi.PropertyConverter} to be available that is capable current providing type T
     * fromMap the given String keys.
     *
     * @param <T> the type of the type literal
     * @param key          the property's absolute, or relative path, e.g. @code
     *                     a/b/c/d.myProperty}, not {@code null}.
     * @param type         The target type required, not {@code null}.
     * @return the property value, never {@code null}.
     * @throws ConfigException if the keys could not be converted to the required target type.
     */
    <T> T get(String key, TypeLiteral<T> type);

    /**
     * Get the property keys as type T. This will implicitly require a corresponding {@link
     * org.apache.tamaya.spi.PropertyConverter} to be available that is capable current providing type T
     * fromMap the given String keys.
     *
     * @param <T> the type of the type literal
     * @param key          the property's absolute, or relative path, e.g.
     *                     {@code a/b/c/d.myProperty}, not {@code null}.
     * @param type         The target type required, not {@code null}.
     * @param defaultValue default value to be used, if no value is present.
     * @return the property value, never null.
     * @throws ConfigException if the keys could not be converted to the required target type.
     */
    <T> T getOrDefault(String key, TypeLiteral<T> type, T defaultValue);

    /**
     * Access all currently known configuration properties as a full {@code Map<String,String>}.
     * Be aware that entries from non scannable parts of the registered {@link org.apache.tamaya.spi.PropertySource}
     * instances may not be contained in the result, but nevertheless be accessible calling one of the
     * {@code get(...)} methods.
     * @return all currently known configuration properties.
     */
    Map<String,String> getProperties();

    /**
     * Extension point for adjusting configuration.
     *
     * @param operator A configuration operator, e.g. a filter, or an adjuster
     *                 combining configurations, never  {@code null}.
     * @return the new adjusted configuration returned by the {@code operator}, never {@code null}.
     */
    default Configuration with(ConfigOperator operator){
        return operator.operate(this);
    }

    /**
     * Query a configuration.
     *
     * @param <T> the type of the configuration.
     * @param query the query, not {@code null}.
     * @return the result returned by the {@code query}.
     */
    default <T> T query(ConfigQuery<T> query){
        return query.query(this);
    }

    /**
     * Access a configuration's context.
     * @return the configuration context, never null.
     */
    ConfigurationContext getContext();

}
