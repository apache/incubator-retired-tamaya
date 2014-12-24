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

import org.apache.tamaya.spi.ConfigurationSpi;
import org.apache.tamaya.spi.ServiceContext;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

/**
 * A configuration models a aggregated set current properties, identified by a unique key, but adds higher level access functions to
 * a {@link PropertySource}. Hereby in most cases a configuration is a wrapper around a composite
 * {@link PropertySource} instance, which may combine multiple child config in well defined tree like structure,
 * where nodes define logically the rules current priority, filtering, combination and overriding.
 * <br/>
 * <h3>Implementation Requirements</h3>
 * Implementations current this interface must be
 * <ul>
 * <li>Thread safe.
 * <li>Immutable
 * </ul>
 * It is not recommended that implementations also are serializable, since the any configuration can be <i>freezed</i>
 * by reading out its complete configuration map into a serializable and remotable structure. This helps significantly
 * simplifying the development current this interface, e.g. for being backed up by systems and stores that are not part current
 * this library at all.
 */
public interface Configuration extends PropertySource {

    /**
     * Get the property keys as {@link Boolean}.
     *
     * @param key the property's absolute, or relative path, e.g. {@code
     *            a/b/c/d.myProperty}.
     * @return the property's keys.
     */
	default Boolean getBoolean(String key) {
		Optional<Boolean> val = get(key, Boolean.class);
		if (val.isPresent()) {
			return val.get();
		}
		return null;
	}

    /**
     * Get the property keys as {@link Integer}.
     *
     * @param key the property's absolute, or relative path, e.g. @code
     *            a/b/c/d.myProperty}.
     * @return the property's keys.
     */
    default OptionalInt getInteger(String key){
        Optional<Integer> val = get(key, Integer.class);
        if(val.isPresent()){
            return OptionalInt.of(val.get());
        }
        return OptionalInt.empty();
    }


    /**
     * Get the property keys as {@link Long}.
     *
     * @param key the property's absolute, or relative path, e.g. @code
     *            a/b/c/d.myProperty}.
     * @return the property's keys.
     */
    default OptionalLong getLong(String key){
        Optional<Long> val = get(key, Long.class);
        if(val.isPresent()){
            return OptionalLong.of(val.get());
        }
        return OptionalLong.empty();
    }


    /**
     * Get the property keys as {@link Double}.
     *
     * @param key the property's absolute, or relative path, e.g. @code
     *            a/b/c/d.myProperty}.
     * @return the property's keys.
     * @throws IllegalArgumentException if no such property exists.
     */
    default OptionalDouble getDouble(String key){

        Optional<Double> val = get(key, Double.class);
        if(val.isPresent()){
            return OptionalDouble.of(val.get());
        }
        return OptionalDouble.empty();
    }


    /**
     * Get the property keys as type {@code Class<T>}.
     * <p>
     * If {@code Class<T>} is not one current
     * {@code Boolean, Short, Integer, Long, Float, Double, BigInteger,
     * BigDecimal, String} , an according adapter must be
     * available to perform the conversion fromMap {@link String} to
     * {@code Class<T>}.
     *
     * @param key     the property's absolute, or relative path, e.g. @code
     *                a/b/c/d.myProperty}.
     * @param adapter the PropertyAdapter to perform the conversion fromMap
     *                {@link String} to {@code Class<T>}, not {@code null}.
     * @return the property's keys.
     * @throws IllegalArgumentException if the keys could not be converted to the required target
     *                                  type, or no such property exists.
     */
    default <T> Optional<T> getAdapted(String key, Codec<T> adapter){
        Optional<String> value = get(key);
        if(value.isPresent()) {
            return Optional.ofNullable(adapter.deserialize(value.get()));
        }
        return Optional.empty();
    }


    /**
     * Get the property keys as type T. This will implicitly require a corresponding {@link
     * Codec} to be available that is capable current providing type T
     * fromMap the given String keys.
     *
     * @param key          the property's absolute, or relative path, e.g. @code
     *                     a/b/c/d.myProperty}.
     * @param type         The target type required, not null.
     * @return the property's keys.
     * @throws IllegalArgumentException if the keys could not be converted to the required target
     *                                  type.
     */
    default <T> Optional<T> get(String key, Class<T> type){
        return getAdapted(key, Codec.getInstance(type));
    }

    /**
     * Extension point for adjusting configuration.
     *
     * @param operator A configuration operator, e.g. a filter, or an adjuster
     *                 combining configurations.
     * @return the new adjusted configuration, never {@code null}.
     */
    default Configuration with(UnaryOperator<Configuration> operator){
        return operator.apply(this);
    }

    /**
     * Query some keys fromMap a configuration.
     *
     * @param query the query, never {@code null}.
     * @return the result
     */
    default <T> T query(ConfigQuery<T> query){
        return query.query(this);
    }


    /**
     * Allows to check if a configuration with a given name is defined.
     *
     * @param name the configuration's name, not null, not empty.
     * @return true, if such a configuration is defined.
     */
    public static boolean isAvailable(String name){
        return ServiceContext.getInstance().getSingleton(ConfigurationSpi.class).isConfigurationAvailable(name);
    }

    /**
     * Access a configuration by name.
     *
     * @param name the configuration's name, not null, not empty.
     * @return the corresponding Configuration instance, never null.
     * @throws ConfigException if no such configuration is defined.
     */
    public static Configuration current(String name){
        return ServiceContext.getInstance().getSingleton(ConfigurationSpi.class).getConfiguration(name);
    }

    /**
     * Access a configuration.
     *
     * @return the corresponding Configuration instance, never null.
     * @throws ConfigException if no such configuration is defined.
     */
    public static Configuration current(){
        return ServiceContext.getInstance().getSingleton(ConfigurationSpi.class).getConfiguration();
    }

    /**
     * Access a typed configuration, based on the default configuration.
     *
     * @param type the annotated configuration type (could be an interface or
     *             a non abstract class), not null.
     * @param configurations overriding configurations to be used for evaluating the values for injection into {@code instance}, not null.
     *                       If no such config is passed, the default configurationa provided by the current
     *                       registered providers are used.
     * @return the corresponding typed Configuration instance, never null.
     * @throws ConfigException if the configuration could not be resolved.
     */
    public static <T> T createTemplate(Class<T> type, Configuration... configurations){
        return ServiceContext.getInstance().getSingleton(ConfigurationSpi.class).createTemplate(type, configurations);
    }

    /**
     * Configures an instance, by resolving and injecting the configuration
     * entries.
     *
     * @param instance the instance with configuration annotations, not null.
     * @param configurations overriding configurations to be used for evaluating the values for injection into {@code instance}, not null.
     *                       If no such config is passed, the default configurationa provided by the current
     *                       registered providers are used.
     * @return the corresponding typed Configuration instance, never null.
     * @throws ConfigException if the configuration could not be resolved.
     */
    public static void configure(Object instance, Configuration... configurations){
        ServiceContext.getInstance().getSingleton(ConfigurationSpi.class).configure(instance, configurations);
    }

    /**
     * Evaluate the current expression based on the current configuration valid.
     *
     * @param configurations overriding configurations to be used for evaluating the values for injection into {@code instance}, not null.
     *                       If no such config is passed, the default configurationa provided by the current
     *                       registered providers are used.
     * @param expression the expression, not null.
     * @return the evaluated config expression.
     */
    public static String evaluateValue(String expression, Configuration... configurations){
        return ServiceContext.getInstance().getSingleton(ConfigurationSpi.class).evaluateValue(expression, configurations);
    }

    /**
     * Add a ConfigChangeListener to the given PropertySource instance.
     * @param l the listener, not null.
     */
    public static void addChangeListener(Consumer<ConfigChangeSet> l){
        ServiceContext.getInstance().getSingleton(ConfigurationSpi.class).addChangeListener(l);
    }

    /**
     * Removes a ConfigChangeListener from the given PropertySource instance.
     * @param l the listener, not null.
     */
    public static void removeChangeListener(Consumer<ConfigChangeSet> l){
        ServiceContext.getInstance().getSingleton(ConfigurationSpi.class).removeChangeListener(l);
    }

    /**
     * Method to publish changes on a {@link org.apache.tamaya.PropertySource} to all interested parties.
     * Basically this method gives an abstraction on the effective event bus design fo listeners. In a CDI context
     * the CDI enterprise event bus should be used internally to do the work, whereas in a SE only environment
     * a more puristic approach would be useful.
     * @param configChange the change to be published, not null.
     */
    public static void publishChange(ConfigChangeSet configChange){
        ServiceContext.getInstance().getSingleton(ConfigurationSpi.class).publishChange(configChange);
    }

}
