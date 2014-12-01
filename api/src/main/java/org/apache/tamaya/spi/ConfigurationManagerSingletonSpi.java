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
package org.apache.tamaya.spi;

import org.apache.tamaya.Configuration;

import java.beans.PropertyChangeListener;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Manager for {@link org.apache.tamaya.Configuration} instances. Implementations must register an instance
 * using the {@link Bootstrap} mechanism in place (by default this is based on the {@link java.util.ServiceLoader}.
 * The {@link org.apache.tamaya.ConfigurationManager} Singleton in the API delegates its corresponding calls to the
 * instance returned by the current bootstrap service in place.
 *
 * @see org.apache.tamaya.ConfigurationManager
 * @see Bootstrap
 */
public interface ConfigurationManagerSingletonSpi{

    /**
     * Allows to check if a configuration with a given name is defined.
     * @param name the configuration's name, not null, not empty.
     * @return true, if such a configuration is defined.
     */
    boolean isConfigurationDefined(String name);

    /**
     * Access a configuration by name.
     * @param name the configuration's name, not null, not empty.
     * @return the corresponding Configuration instance, never null.
     * @throws org.apache.tamaya.ConfigException if no such configuration is defined.
     */
    default Configuration getConfiguration(String name){
        return getConfiguration(name, Configuration.class);
    }

    /**
     * Access the default configuration.
     * @return the corresponding Configuration instance, never null.
     * @throws org.apache.tamaya.ConfigException if no such configuration is defined.
     */
    default Configuration getConfiguration(){
        return getConfiguration("default", Configuration.class);
    }

    /**
     * Configures an instance, by resolving and injecting the configuration
     * entries.
     *
     * @param instance the instance with configuration annotations, not null.
     * @throws org.apache.tamaya.ConfigException if any required configuration could not be resolved/injected.
     */
    void configure(Object instance);

    /**
     * Access a configuration by name.
     *
     * @param name the configuration's name, not null, not empty.
     *             @param template the annotated configuration's
     *                             template interface, not null.
     * @return the corresponding Configuration instance, never null.
     * @throws org.apache.tamaya.ConfigException if no such configuration is defined.
     */
    <T> T getConfiguration(String name, Class<T> template);

    /**
     * Access a typed configuration.
     *
     * @param type the annotated configuration type (could be an interface or
     *             a non abstract class), not null.
     * @return the corresponding typed Configuration instance, never null.
     * @throws org.apache.tamaya.ConfigException if the configuration could not be resolved.
     */
    default <T> T getConfiguration(Class<T> type){
        return getConfiguration("default", type);
    }

    /**
     * Evaluate the current expression based on the current configuration valid.
     * @param config     The configuration to be used for evaluating, using EL, not null.
     * @param expression the expression, not null.
     * @return the evaluated config expression.
     */
    String evaluateValue(Configuration config, String expression);

    /**
     * Adds a (global) {@link java.beans.PropertyChangeListener} instance that listens to all kind of config changes.
     * @param listener the {@link java.beans.PropertyChangeListener} instance to be added, not null.
     */
    void addPropertyChangeListener(PropertyChangeListener listener);

    /**
     * Removes a (global) {@link java.beans.PropertyChangeListener} instance that listens to all kind of config changes,
     * if one is currently registered.
     * @param listener the {@link java.beans.PropertyChangeListener} instance to be removed, not null.
     */
    void removePropertyChangeListener(PropertyChangeListener listener);

}
