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

import org.apache.tamaya.spi.ConfigurationBuilder;
import org.apache.tamaya.spi.ConfigurationContext;
import org.apache.tamaya.spi.ConfigurationProviderSpi;
import org.apache.tamaya.spi.ServiceContextManager;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.UnaryOperator;


/**
 * <p>A configuration models an aggregated set of current properties, identified by
 * a unique key, but adds higher level access functions to
 * a {@link org.apache.tamaya.spi.PropertySource}. Hereby in most
 * cases a configuration is a wrapper around a composite
 * {@link org.apache.tamaya.spi.PropertySource} instance, which may combine
 * multiple child configurations in a well defined tree like structure,
 * where nodes define logically the rules' current priority, filtering,
 * combination and overriding.
 * </p>
 * <h3>Implementation Requirements</h3>
 * Implementations of this interface must be
 * <ul>
 *  <li>Thread safe</li>
 *  <li>Immutable</li>
 * </ul>
 *
 * <p>It is not recommended that implementations also are serializable, since the any configuration can be <i>frozen</i>
 * by reading out its complete configuration map into a serializable and remotable structure. This helps significantly
 * by simplifying the development of this interface, e.g. for being backed up by systems and stores that are not part of
 * this library at all.</p>
 */
public interface Configuration {

    /**
     * Access a property.
     *
     * @param key the property's key, not {@code null}.
     * @return the property's createValue.
     */
    default String get(String key){
        return get(key, TypeLiteral.of(String.class));
    }

    /**
     * Access a property.
     *
     * @param key the property's key, not {@code null}.
     * @param defaultValue createValue to be returned, if no createValue is present, not {@code null}
     * @return the property's keys.
     */
    default String getOrDefault(String key, String defaultValue){
        return getOrDefault(key, TypeLiteral.of(String.class), defaultValue);
    }

    /**
     * Access a String property, using an an {@link Optional} instance.
     *
     * @param key the property's key, not {@code null}.
     * @return the property's keys.
     */
    default Optional<String> getOptional(String key){
        return Optional.ofNullable(getOrDefault(key, String.class, null));
    }

    /**
     * Access a property, using an an {@link Optional} instance.
     *
     * @param key the property's key, not {@code null}.
     * @param type the target type, not null.
     * @param <T> the type of the class modeled by the type parameter
     * @return the property's keys.
     */
    default <T> Optional<T> getOptional(String key, Class<T> type){
        return Optional.ofNullable(getOrDefault(key, TypeLiteral.of(type), null));
    }

    /**
     * Access a property, using an an {@link Optional} instance.
     *
     * @param key the property's key, not {@code null}.
     * @param type the target type, not null.
     * @param <T> the type of the class modeled by the type parameter
     * @return the property's keys.
     */
    default <T> Optional<T> getOptional(String key, TypeLiteral<T> type){
        return Optional.ofNullable(getOrDefault(key, type, null));
    }

    /**
     * Gets the property keys as type T. This will implicitly require a corresponding {@link
     * org.apache.tamaya.spi.PropertyConverter} to be available that is capable of providing type T
     * fromMap for the given String keys.
     *
     * @param <T> the type of the class modeled by the type parameter
     * @param key          the property's absolute, or relative path, e.g. {@code
     *                     a/b/c/d.myProperty}, not  {@code null}.
     * @param type         The target type required, not  {@code null}.
     * @param defaultValue createValue to be used, if no createValue is present, not {@code null}
     * @return the property createValue, never {@code null}.
     * @throws ConfigException if the keys could not be converted to the required target type.
     */
    default <T> T getOrDefault(String key, Class<T> type, T defaultValue){
        return getOrDefault(key, TypeLiteral.of(type), defaultValue);
    }

    /**
     * Gets the property keys as type T. This will implicitly require a corresponding {@link
     * org.apache.tamaya.spi.PropertyConverter} to be available that is capable of providing type T
     * fromMap for the given String keys.
     *
     * @param <T> the type of the class modeled by the type parameter
     * @param key          the property's absolute, or relative path, e.g. @code
     *                     a/b/c/d.myProperty}.
     * @param type         The target type required, not {@code null}.
     * @return the property createValue, never {@code null}.
     * @throws ConfigException if the keys could not be converted to the required target type.
     */
    default <T> T get(String key, Class<T> type){
        return get(key, TypeLiteral.of(type));
    }

    /**
     * Get the property keys as type T. This will implicitly require a corresponding {@link
     * org.apache.tamaya.spi.PropertyConverter} to be available that is capable of providing type T
     * literals for the given key.
     *
     * @param <T> the type of the type literal
     * @param key          the property's absolute, or relative path, e.g. @code
     *                     a/b/c/d.myProperty}, not {@code null}.
     * @param type         The target type required, not {@code null}.
     * @return the property createValue, never {@code null}.
     * @throws ConfigException if the keys could not be converted to the required target type.
     */
    <T> T get(String key, TypeLiteral<T> type);

    /**
     * Get the property keys as type T. This will implicitly require a corresponding {@link
     * org.apache.tamaya.spi.PropertyConverter} to be available that is capable of providing type T
     * literals for the given key.
     *
     * @param <T> the type of the type literal
     * @param key          the property's absolute, or relative path, e.g.
     *                     {@code a/b/c/d.myProperty}, not {@code null}.
     * @param type         The target type required, not {@code null}.
     * @param defaultValue default createValue to be used, if no createValue is present.
     * @return the property createValue, never null.
     * @throws ConfigException if the keys could not be converted to the required target type.
     */
    <T> T getOrDefault(String key, TypeLiteral<T> type, T defaultValue);

    /**
     * Access all currently known configuration properties as a full {@code Map<String,String>}.
     * Be aware that entries from non scannable parts of the registered {@link org.apache.tamaya.spi.PropertySource}
     * instances may not be contained in the result, but nevertheless be accessible by calling one of the
     * {@code current(...)} methods.
     * @return all currently known configuration properties.
     */
    Map<String,String> getProperties();

    /**
     * Extension point for adjusting configuration.
     *
     * @param operator A configuration operator, e.g. a filter, or an adjuster
     *                 combining configurations, never  {@code null}.
     * @return the new adjusted configuration returned by the {@code operator}, never {@code null}.
     * @deprecated use {@link #map(UnaryOperator)}
     */
    @Deprecated
    default Configuration with(ConfigOperator operator){
        Objects.requireNonNull(operator, "Operator must be given.");
        return operator.operate(this);
    }

    /**
     * Extension point for adjusting configuration.
     *
     * @param operator A configuration operator, e.g. a filter, or an adjuster
     *                 combining configurations, never  {@code null}.
     * @return the new adjusted configuration returned by the {@code operator}, never {@code null}.
     */
    default Configuration map(UnaryOperator<Configuration> operator){
        Objects.requireNonNull(operator, "Operator must be given.");
        return operator.apply(this);
    }

    /**
     * Query a configuration.
     *
     * @param <T> the type of the configuration.
     * @param query the query, not {@code null}.
     * @return the result returned by the {@code query}.
     * @deprecated Use {@link #adapt(Function)}
     */
    @Deprecated
    default <T> T query(ConfigQuery<T> query){
        Objects.requireNonNull(query, "Query must be given.");
        return query.query(this);
    }

    /**
     * Query a configuration.
     *
     * @param <T> the type of the configuration.
     * @param query the query, not {@code null}.
     * @return the result returned by the {@code query}.
     */
    default <T> T adapt(Function<Configuration, T> query){
        Objects.requireNonNull(query, "Adapter must be given.");
        return query.apply(this);
    }

    /**
     * Access a configuration's context.
     * @return the configuration context, never null.
     */
    ConfigurationContext getContext();

    /**
     * Create a new builder using this instance as its base.
     * @return a new builder, never null.
     */
    default ConfigurationBuilder toBuilder() {
        return getContext().getServiceContext()
                .getService(ConfigurationProviderSpi.class).getConfigurationBuilder().setConfiguration(this);
    }

    /**
     * This method allows replacement of the current default {@link org.apache.tamaya.Configuration} with a new
     * instance. It is the responsibility of the ConfigurationProvider to trigger
     * corresponding update events for the current {@link org.apache.tamaya.Configuration}, so observing
     * listeners can do whatever is appropriate to react to any given configuration change.
     *
     * @param config the new Configuration to be applied, not {@code null}
     * @throws java.lang.UnsupportedOperationException if the current provider is read-only and
     *                                                 does not support
     *                                                 applying a new Configuration.
     */
    static void setCurrent(Configuration config) {
        ServiceContextManager.getServiceContext()
                .getService(ConfigurationProviderSpi.class).setConfiguration(config, Thread.currentThread().getContextClassLoader());
    }

    /**
     * This method allows replacement of the current default {@link org.apache.tamaya.Configuration} with a new
     * instance. It is the responsibility of the ConfigurationProvider to trigger
     * corresponding update events for the current {@link org.apache.tamaya.Configuration}, so observing
     * listeners can do whatever is appropriate to react to any given configuration change.
     *
     * @param config the new Configuration to be applied, not {@code null}
     * @param classLoader the target classloader, not null.
     * @throws java.lang.UnsupportedOperationException if the current provider is read-only and
     *                                                 does not support
     *                                                 applying a new Configuration.
     */
    static void setCurrent(Configuration config, ClassLoader classLoader) {
        ServiceContextManager.getServiceContext(classLoader)
                .getService(ConfigurationProviderSpi.class).setConfiguration(config, classLoader);
    }

    /**
     * Access the configuration instance for the current thread's context classloader.
     * @return the configuration instance, never null.
     */
    static Configuration current(){
        return ServiceContextManager.getServiceContext()
                .getService(ConfigurationProviderSpi.class).getConfiguration(Thread.currentThread().getContextClassLoader());
    }

    /**
     * Accesses the configuration for a given classloader.
     * @param classloader the classloader, not null.
     * @return the configuration instance, never null.
     */
    static Configuration current(ClassLoader classloader){
        return ServiceContextManager.getServiceContext(classloader)
                .getService(ConfigurationProviderSpi.class).getConfiguration(classloader);
    }

    /**
     * Access a new configuration builder initialized with the current thread's context classloader.
     * @return the builder, never null.
     */
    static ConfigurationBuilder createConfigurationBuilder(){
        return ServiceContextManager.getServiceContext()
                .getService(ConfigurationProviderSpi.class).getConfigurationBuilder();
    }


    /**
     * Immutable and reusable, thread-safe implementation of an empty propertySource.
     */
    Configuration EMPTY = new Configuration() {

        @Override
        public String get(String key) {
            return null;
        }

        @Override
        public String getOrDefault(String key, String defaultValue) {
            return defaultValue;
        }

        @Override
        public <T> T getOrDefault(String key, Class<T> type, T defaultValue) {
            return defaultValue;
        }

        @Override
        public <T> T get(String key, Class<T> type) {
            return null;
        }

        @Override
        public <T> T get(String key, TypeLiteral<T> type) {
            return null;
        }

        @Override
        public <T> T getOrDefault(String key, TypeLiteral<T> type, T defaultValue) {
            return defaultValue;
        }

        @Override
        public Map<String, String> getProperties() {
            return Collections.emptyMap();
        }

        @Override
        public ConfigurationContext getContext() {
            return ConfigurationContext.EMPTY;
        }

        @Override
        public String toString(){
            return "Configuration<empty>";
        }
    };

}
