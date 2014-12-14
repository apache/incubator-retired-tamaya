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

import org.apache.tamaya.spi.ServiceContext;
import org.apache.tamaya.spi.ConfigurationManagerSingletonSpi;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Singleton accessor for accessing {@link Configuration} instances and
 * proxied configuration templates.
 */
final class ConfigurationManager{

    /**
     * Private singleton constructor.
     */
    private ConfigurationManager(){
    }

    /**
     * Allows to check if a configuration with a given name is defined.
     *
     * @param name the configuration's name, not null, not empty.
     * @return true, if such a configuration is defined.
     */
    public static boolean isConfigurationDefined(String name){
        return ServiceContext.getInstance().getSingleton(ConfigurationManagerSingletonSpi.class).isConfigurationDefined(name);
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
    public static <T> T getConfiguration(String name, Class<T> template){
        return ServiceContext.getInstance().getSingleton(ConfigurationManagerSingletonSpi.class).getConfiguration(name, template);
    }


    /**
     * Access a configuration by name.
     *
     * @param name the configuration's name, not null, not empty.
     * @return the corresponding Configuration instance, never null.
     * @throws ConfigException if no such configuration is defined.
     */
    public static Configuration getConfiguration(String name){
        return ServiceContext.getInstance().getSingleton(ConfigurationManagerSingletonSpi.class).getConfiguration(name);
    }

    /**
     * Access a configuration.
     *
     * @return the corresponding Configuration instance, never null.
     * @throws ConfigException if no such configuration is defined.
     */
    public static Configuration getConfiguration(){
        return ServiceContext.getInstance().getSingleton(ConfigurationManagerSingletonSpi.class).getConfiguration();
    }

    /**
     * Access a typed configuration, based on the default configuration.
     *
     * @param type the annotated configuration type (could be an interface or
     *             a non abstract class), not null.
     * @return the corresponding typed Configuration instance, never null.
     * @throws ConfigException if the configuration could not be resolved.
     */
    public static <T> T getConfiguration(Class<T> type){
        return ServiceContext.getInstance().getSingleton(ConfigurationManagerSingletonSpi.class).getConfiguration(type);
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
        ServiceContext.getInstance().getSingleton(ConfigurationManagerSingletonSpi.class).configure(instance);
    }

    /**
     * Evaluate the current expression based on the current configuration valid.
     *
     * @param expression the expression, not null.
     * @return the evaluated config expression.
     */
    public static String evaluateValue(String expression){
        return evaluateValue(getConfiguration(), expression);
    }

    /**
     * Evaluate the current expression based on the current configuration valid.
     *
     * @param config     The configuration to be used for evluating, not null.
     * @param expression the expression, not null.
     * @return the evaluated config expression.
     */
    public static String evaluateValue(Configuration config, String expression){
        return ServiceContext.getInstance().getSingleton(ConfigurationManagerSingletonSpi.class).evaluateValue(config, expression);
    }

    /**
     * Add a ConfigChangeSet listener to the given configuration instance.
     * @param predicate the event filtering predicate, or null, for selecting all changes.
     * @param l the listener, not null.
     */
    public static void addChangeListener(Predicate<PropertySource> predicate, Consumer<ConfigChangeSet> l) {
        ServiceContext.getInstance().getSingleton(ConfigurationManagerSingletonSpi.class).addChangeListener(predicate, Objects.requireNonNull(l));
    }

    /**
     * Removes a ConfigChangeSet listener from the given configuration instance.
     * @param predicate the event filtering predicate, or null, for selecting all changes.
     * @param l the listener, not null.
     */
    public static void removeChangeListener(Predicate<PropertySource> predicate, Consumer<ConfigChangeSet> l) {
        ServiceContext.getInstance().getSingleton(ConfigurationManagerSingletonSpi.class).removeChangeListener(predicate, Objects.requireNonNull(l));
    }

    /**
     * Method to publish changes on a {@link org.apache.tamaya.Configuration} to all interested parties.
     * Basically this method gives an abstraction on the effective event bus design fo listeners. In a CDI context
     * the CDI enterprise event bus should be used internally to do the work, whereas in a SE only environment
     * a more puristic approach would be useful.
     * @param configChange the change to be published, not null.
     */
    public static void publishChange(ConfigChangeSet configChange) {
        ServiceContext.getInstance().getSingleton(ConfigurationManagerSingletonSpi.class).publishChange(Objects.requireNonNull(configChange));
    }
}
