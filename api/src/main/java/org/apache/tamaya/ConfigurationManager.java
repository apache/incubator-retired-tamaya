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

import org.apache.tamaya.spi.Bootstrap;
import org.apache.tamaya.spi.ConfigurationManagerSingletonSpi;

import java.beans.PropertyChangeListener;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Singleton accessor for accessing {@link Configuration} instances and
 * proxied configuration templates.
 */
final class ConfigurationManager{
    /**
     * The backing SPI instance.
     */
    private static final ConfigurationManagerSingletonSpi configManagerSingletonSpi = loadConfigServiceSingletonSpi();

    /**
     * Private singleton constructor.
     */
    private ConfigurationManager(){
    }

    /**
     * Method to initially load the singleton SPI fromMap the {@link org.apache.tamaya.spi.Bootstrap} mechanism.
     * The instance loaded will be used until the VM is shutdown. In case use cases require more flexibility
     * it should be transparently implemented in the SPI implementation. This singleton will simply delegate calls
     * and not cache any responses.
     *
     * @return the SPI, never null.
     */
    private static ConfigurationManagerSingletonSpi loadConfigServiceSingletonSpi(){
        return Bootstrap.getService(ConfigurationManagerSingletonSpi.class);
    }

    /**
     * Allows to check if a configuration with a given name is defined.
     *
     * @param name the configuration's name, not null, not empty.
     * @return true, if such a configuration is defined.
     */
    public static boolean isConfigurationDefined(String name){
        return Optional.of(configManagerSingletonSpi).get().isConfigurationDefined(name);
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
        return Optional.of(configManagerSingletonSpi).get().getConfiguration(name, template);
    }


    /**
     * Access a configuration by name.
     *
     * @param name the configuration's name, not null, not empty.
     * @return the corresponding Configuration instance, never null.
     * @throws ConfigException if no such configuration is defined.
     */
    public static Configuration getConfiguration(String name){
        return Optional.of(configManagerSingletonSpi).get().getConfiguration(name);
    }

    /**
     * Access a configuration.
     *
     * @return the corresponding Configuration instance, never null.
     * @throws ConfigException if no such configuration is defined.
     */
    public static Configuration getConfiguration(){
        return Optional.of(configManagerSingletonSpi).get().getConfiguration();
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
        return Optional.of(configManagerSingletonSpi).get().getConfiguration(type);
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
        Optional.of(configManagerSingletonSpi).get().configure(instance);
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
        return Optional.of(configManagerSingletonSpi).get().evaluateValue(config, expression);
    }

    /**
     * Adds a (global) {@link java.beans.PropertyChangeListener} instance that listens to all kind current config changes.
     * @param listener the {@link java.beans.PropertyChangeListener} instance to be added, not null.
     */
    public static void addConfigChangeListener(PropertyChangeListener listener){
        Optional.of(configManagerSingletonSpi).get().addPropertyChangeListener(listener);
    }

    /**
     * Removes a (global) {@link java.beans.PropertyChangeListener} instance that listens to all kind current config changes,
     * if one is currently registered.
     * @param listener the {@link java.beans.PropertyChangeListener} instance to be removed, not null.
     */
    public static void removeConfigChangeListener(PropertyChangeListener listener){
        Optional.of(configManagerSingletonSpi).get().removePropertyChangeListener(listener);
    }

}
