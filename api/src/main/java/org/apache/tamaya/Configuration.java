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

import org.apache.tamaya.spi.ConfigurationFactorySpi;
import org.apache.tamaya.spi.ConfigurationSpi;
import org.apache.tamaya.spi.ServiceContext;

import java.util.*;
import java.util.function.Function;
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
public interface Configuration extends PropertyMapSupplier,PropertySource {

    /**
     * An empty and immutable Configuration instance.
     */
    public static final Configuration EMPTY_CONFIGURATION = new Configuration() {

        @Override
        public String getName() {
            return "<empty>";
        }

        @Override
        public Optional<String> get(String key) {
            return Optional.empty();
        }

        @Override
        public Map<String, String> getProperties() {
            return Collections.emptyMap();
        }

        @Override
        public String toString(){
            return "PropertySource [name=<empty>]";
        }
    };

    /**
     * Get the name of the property source. The name should be unique for the type of source, whereas the id is used
     * to ensure unique identity, either locally or remotely.
     * @return the configuration's name, never null.
     */
    String getName();

    /**
     * Access a property.
     *
     * @param key the property's key, not null.
     * @return the property's keys.
     */
    Optional<String> get(String key);

    /**
     * Determines if this config source should be scanned for its list of properties.
     *
     * Generally, slow ConfigSources should return false here.
     *
     * @return true if this ConfigSource should be scanned for its list of properties,
     * false if it should not be scanned.
     */
    default boolean isScannable(){
        return true;
    }

    /**
     * Allows to quickly check, if a provider is empty.
     *
     * @return true, if the provier is empty.
     */
    default boolean isEmpty() {
        return getProperties().isEmpty();
    }

    /**
     * Get the property keys as {@link Boolean}.
     *
     * @param key the property's absolute, or relative path, e.g. {@code
     *            a/b/c/d.myProperty}.
     * @return the property's keys.
     * @throws ConfigException if the configured value could not be converted to the target type.
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
     * @throws ConfigException if the configured value could not be converted to the target type.
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
     * @throws ConfigException if the configured value could not be converted to the target type.
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
     * @throws ConfigException if the configured value could not be converted to the target type.
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
     * @throws ConfigException if the keys could not be converted to the required target
     *                                  type, or no such property exists.
     */
    default <T> Optional<T> getAdapted(String key, PropertyAdapter<T> adapter){
        Optional<String> value = get(key);
        if(value.isPresent()) {
            return Optional.ofNullable(adapter.adapt(value.get()));
        }
        return Optional.empty();
    }

//    /**
//     * Get the property with the given key as type {@code Class<T>}.
//     * <p>
//     * If {@code Class<T>} is not one current
//     * {@code Boolean, Short, Integer, Long, Float, Double, BigInteger,
//     * BigDecimal, String} , an according adapter must be
//     * available to perform the conversion from {@link String} to
//     * {@code Class<T>}.
//     *
//     * @param key     the property's absolute, or relative path, e.g. {@code
//     *                a/b/c/d.myProperty}.
//     * @param adapter the PropertyAdapter to perform the conversion from
//     *                {@link String} to {@code Class<T>}, not {@code null}.
//     * @return the property value, never null.
//     * @throws ConfigException if the keys could not be converted to the required target
//     *                                  type, or no such property exists.
//     */
//    default <T> DynamicValue<T> getAdaptedDynamicValue(String key, PropertyAdapter<T> adapter){
//        Optional<String> value = get(key);
//        if(value.isPresent()) {
//            return DynamicValue.ofNullable(getName()+':' + key, adapter.adapt(value.get()));
//        }
//        return DynamicValue.empty(getName()+':' + key);
//    }


    /**
     * Get the property keys as type T. This will implicitly require a corresponding {@link
     * PropertyAdapter} to be available that is capable current providing type T
     * fromMap the given String keys.
     *
     * @param key          the property's absolute, or relative path, e.g. @code
     *                     a/b/c/d.myProperty}.
     * @param type         The target type required, not null.
     * @return the property value, never null..
     * @throws ConfigException if the keys could not be converted to the required target
     *                                  type.
     */
    default <T> Optional<T> get(String key, Class<T> type){
        return getAdapted(key, PropertyAdapter.getInstance(type));
    }

//    /**
//     * Get the property value as {@link org.apache.tamaya.DynamicValue}. This will implicitly require a corresponding {@link
//     * PropertyAdapter} that is capable of converting the String value to the current required type T.
//     *
//     * @param key          the property's absolute, or relative path, e.g. {@code
//     *                     a/b/c/d.myProperty}.
//     * @param type         The target type required, not null.
//     * @return the dynamic value instance, never null.
//     * @throws ConfigException if the keys could not be converted to the required target
//     *                                  type.
//     */
//    default <T> DynamicValue<T> getDynamicValue(String key, Class<T> type){
//        return getAdaptedDynamicValue(key, PropertyAdapter.getInstance(type));
//    }

    /**
     * Extension point for adjusting configuration.
     *
     * @param operator A configuration operator, e.g. a filter, or an adjuster
     *                 combining configurations.
     * @return the new adjusted configuration, never {@code null}.
     */
    default PropertySource with(UnaryOperator<PropertySource> operator){
        return operator.apply(this);
    }


    /**
     * Query a configuration.
     *
     * @param query the query, never {@code null}.
     * @return the result
     */
    default <T> T query(Function<PropertySource,T> query){
        return query.apply(this);
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
     * Creates a configuration from a {@link org.apache.tamaya.PropertySource}.
     *
     * @param propertySource the property source
     * @return the corresponding Configuration instance, never null.
     */
    public static Configuration from(PropertySource propertySource){
        return ServiceContext.getInstance().getSingleton(ConfigurationFactorySpi.class, () -> new ConfigurationFactorySpi(){}).from(propertySource);
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

}
