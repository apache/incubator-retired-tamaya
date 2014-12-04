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

import com.sun.javafx.scene.control.behavior.OptionalBoolean;

import java.beans.PropertyChangeListener;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * A configuration models a aggregated set current properties, identified by a unique key, but adds higher level access functions to
 * a {@link PropertyProvider}. Hereby in most cases a configuration is a wrapper around a composite
 * {@link PropertyProvider} instance, which may combine multiple child config in well defined tree like structure,
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
public interface Configuration extends PropertyProvider{

    /**
     * Get the property value as {@link Boolean}.
     *
     * @param key the property's absolute, or relative path, e.g. {@code
     *            a/b/c/d.myProperty}.
     * @return the property's value.
     */
    default OptionalBoolean getBoolean(String key){
        Optional<Boolean> val = get(key, Boolean.class);
        if(val.isPresent()){
            if(val.get()){
                return OptionalBoolean.TRUE;
            }
            return OptionalBoolean.FALSE;
        }
        return OptionalBoolean.ANY;
    }

    /**
     * Get the property value as {@link Integer}.
     *
     * @param key the property's absolute, or relative path, e.g. @code
     *            a/b/c/d.myProperty}.
     * @return the property's value.
     */
    default OptionalInt getInteger(String key){
        Optional<Integer> val = get(key, Integer.class);
        if(val.isPresent()){
            return OptionalInt.of(val.get());
        }
        return OptionalInt.empty();
    }


    /**
     * Get the property value as {@link Long}.
     *
     * @param key the property's absolute, or relative path, e.g. @code
     *            a/b/c/d.myProperty}.
     * @return the property's value.
     */
    default OptionalLong getLong(String key){
        Optional<Long> val = get(key, Long.class);
        if(val.isPresent()){
            return OptionalLong.of(val.get());
        }
        return OptionalLong.empty();
    }


    /**
     * Get the property value as {@link Double}.
     *
     * @param key the property's absolute, or relative path, e.g. @code
     *            a/b/c/d.myProperty}.
     * @return the property's value.
     * @throws IllegalArgumentException if no such property exists.
     */
    default OptionalDouble getDouble(String key){

        Optional<Double> val = get(key, Double.class);
        if(val.isPresent()){
            return OptionalDouble.empty().of(val.get());
        }
        return OptionalDouble.empty();
    }


    /**
     * Get the property value as type {@code Class<T>}.
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
     * @return the property's value.
     * @throws IllegalArgumentException if the value could not be converted to the required target
     *                                  type, or no such property exists.
     */
    default <T> Optional<T> getAdapted(String key, PropertyAdapter<T> adapter){
        Optional<String> value = get(key);
        if(value.isPresent()) {
            return Optional.ofNullable(adapter.adapt(value.get()));
        }
        return Optional.empty();
    }


    /**
     * Get the property value as type T. This will implicitly require a corresponding {@link
     * PropertyAdapter} to be available that is capable current providing type T
     * fromMap the given String value.
     *
     * @param key          the property's absolute, or relative path, e.g. @code
     *                     a/b/c/d.myProperty}.
     * @param type         The target type required, not null.
     * @return the property's value.
     * @throws IllegalArgumentException if the value could not be converted to the required target
     *                                  type.
     */
    default <T> Optional<T> get(String key, Class<T> type){
        return getAdapted(key, PropertyAdapters.getAdapter(type));
    }

    /**
     * Return a set with all fully qualifies area names.
     *
     * @return s set with all areas, never {@code null}.
     */
    default Set<String> getAreas(){
        final Set<String> areas = new HashSet<>();
        this.keySet().forEach(s -> {
            int index = s.lastIndexOf('.');
            if(index > 0){
                areas.add(s.substring(0, index));
            }
            else{
                areas.add("<root>");
            }
        });
        return areas;
    }

    /**
     * Return a set with all fully qualified area names, containing the transitive closure also including all
     * subarea names, regardless if properties are accessible or not.
     *
     * @return s set with all transitive areas, never {@code null}.
     */
    default Set<String> getTransitiveAreas(){
        final Set<String> transitiveAreas = new HashSet<>();
        getAreas().forEach(s -> {
            int index = s.lastIndexOf('.');
            if (index < 0) {
                transitiveAreas.add("<root>");
            } else {
                while (index > 0) {
                    s = s.substring(0, index);
                    transitiveAreas.add(s);
                    index = s.lastIndexOf('.');
                }
            }
        });
        return transitiveAreas;
    }

    /**
     * Return a set with all fully qualified area names, containing only the
     * areas that match the predicate and have properties attached
     *
     * @param predicate A predicate to deternine, which areas should be returned, not {@code null}.
     * @return s set with all areas, never {@code null}.
     */
    default Set<String> getAreas(final Predicate<String> predicate){
        return getAreas().stream().filter(predicate).collect(Collectors.toCollection(TreeSet::new));
    }

    /**
     * Return a set with all fully qualified area names, containing the transitive closure also including all
     * subarea names, regardless if properties are accessible or not.
     *
     * @param predicate A predicate to deternine, which areas should be returned, not {@code null}.
     * @return s set with all transitive areas, never {@code null}.
     */
    default Set<String> getTransitiveAreas(Predicate<String> predicate){
        return getTransitiveAreas().stream().filter(predicate).collect(Collectors.toCollection(TreeSet::new));
    }

    /**
     * Allows to evaluate if an area exists.
     *
     * @param areaKey the configuration area (sub)path.
     * @return {@code true}, if such a node exists.
     */
    default boolean containsArea(String areaKey){
        return getAreas().contains(areaKey);
    }

    /**
     * Extension point for adjusting configuration.
     *
     * @param operator A configuration operator, e.g. a filter, or an adjuster
     *                 combining configurations.
     * @return the new adjusted configuration, never {@code null}.
     */
    default Configuration with(ConfigOperator operator){
        return operator.operate(this);
    }

    /**
     * Query some value fromMap a configuration.
     *
     * @param query the query, never {@code null}.
     * @return the result
     */
    default <T> T query(ConfigQuery<T> query){
        return query.query(this);
    }

    /**
     * Field that allows property config to be versioned, meaning that each change on a provider requires this value
     * to be incremented by one. This can be easily used to implement versioning (and optimistic locking)
     * in distributed (remote) usage scenarios.
     * @return the version current the current instance, or 'N/A'.
     */
    default String getVersion(){return "N/A";}

    /**
     * Add a ConfigChangeListener to this configuration instance.
     * @param l the listener, not null.
     */
    default void addPropertyChangeListener(PropertyChangeListener l){
        throw new UnsupportedOperationException("Change listeners not supported by default.");
    }

    /**
     * Removes a ConfigChangeListener to this configuration instance.
     * @param l the listener, not null.
     */
    default void removePropertyChangeListener(PropertyChangeListener l){
        throw new UnsupportedOperationException("Change listeners not supported by default.");
    }

    /**
     * Allows to check if a configuration with a given name is defined.
     *
     * @param name the configuration's name, not null, not empty.
     * @return true, if such a configuration is defined.
     */
    public static boolean isDefined(String name){
        return ConfigurationManager.isConfigurationDefined(name);
    }

    /**
     * Access a configuration by name.
     *
     * @param name the configuration's name, not null, not empty.
     *             @param template the annotated configuration's
     *                             template interface, not null.
     * @return the corresponding Configuration instance, never null.
     * @throws ConfigException if no such configuration is defined.
     */
    public static <T> T current(String name, Class<T> template){
        return ConfigurationManager.getConfiguration(name, template);
    }


    /**
     * Access a configuration by name.
     *
     * @param name the configuration's name, not null, not empty.
     * @return the corresponding Configuration instance, never null.
     * @throws ConfigException if no such configuration is defined.
     */
    public static Configuration current(String name){
        return ConfigurationManager.getConfiguration(name);
    }

    /**
     * Access a configuration.
     *
     * @return the corresponding Configuration instance, never null.
     * @throws ConfigException if no such configuration is defined.
     */
    public static Configuration current(){
        return ConfigurationManager.getConfiguration();
    }

    /**
     * Access a typed configuration, based on the default configuration.
     *
     * @param type the annotated configuration type (could be an interface or
     *             a non abstract class), not null.
     * @return the corresponding typed Configuration instance, never null.
     * @throws ConfigException if the configuration could not be resolved.
     */
    public static <T> T current(Class<T> type){
        return ConfigurationManager.getConfiguration(type);
    }

    /**
     * Configures an instance, by resolving and injecting the configuration
     * entries.
     *
     * @param instance the instance with configuration annotations, not null.
     * @return the corresponding typed Configuration instance, never null.
     * @throws ConfigException if the configuration could not be resolved.
     */
    public static void configure(Object instance){
        ConfigurationManager.configure(instance);
    }

    /**
     * Evaluate the current expression based on the current configuration valid.
     *
     * @param expression the expression, not null.
     * @return the evaluated config expression.
     */
    public static String evaluateValue(String expression){
        return ConfigurationManager.evaluateValue(expression);
    }

    /**
     * Evaluate the current expression based on the current configuration valid.
     *
     * @param config     The configuration to be used for evluating, not null.
     * @param expression the expression, not null.
     * @return the evaluated config expression.
     */
    public static String evaluateValue(Configuration config, String expression){
        return ConfigurationManager.evaluateValue(config, expression);
    }

}
