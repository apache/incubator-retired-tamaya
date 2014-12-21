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

import org.apache.tamaya.ConfigChangeSet;
import org.apache.tamaya.Configuration;
import org.apache.tamaya.PropertySource;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Manager for {@link org.apache.tamaya.Configuration} instances. Implementations must register an instance
 * using the {@link ServiceContextManager} mechanism in place (by default this is based on the {@link java.util.ServiceLoader}.
 * The {@link org.apache.tamaya.ConfigurationManager} Singleton in the API delegates its corresponding calls to the
 * instance returned by the current bootstrap service in place.
 *
 * @see org.apache.tamaya.ConfigurationManager
 * @see ServiceContextManager
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
    Configuration getConfiguration(String name);

    /**
     * Access the default configuration.
     * @return the corresponding Configuration instance, never null.
     * @throws org.apache.tamaya.ConfigException if no such configuration is defined.
     */
    default Configuration getConfiguration(){
        return getConfiguration("default");
    }

    /**
     * Configures an instance, by resolving and injecting the configuration
     * entries.
     *
     * @param instance the instance with configuration annotations, not null.
     * @param configurations overriding configurations to be used for evaluating the values for injection into {@code instance}.
     *                If no such config is passed, the default configurationa provided by the current
     *                registered providers are used.
     * @throws org.apache.tamaya.ConfigException if any required configuration could not be resolved/injected.
     */
    void configure(Object instance, Configuration... configurations);

    /**
     * Access a configuration by name.
     *
     * @param configurations overriding configurations to be used for evaluating the values for injection into {@code instance}, not null.
     *                       If no such config is passed, the default configurationa provided by the current
     *                       registered providers are used.
     * @return the corresponding Configuration instance, never null.
     * @throws org.apache.tamaya.ConfigException if no such configuration is defined.
     */
    <T> T createTemplate(Class<T> template, Configuration... configurations);

    /**
     * Evaluate the current expression based on the current configuration valid.
     * @param configurations overriding configurations to be used for evaluating the values for injection into {@code instance}, not null.
     *                       If no such config is passed, the default configurationa provided by the current
     *                       registered providers are used.
     * @param expression the expression, not null.
     * @return the evaluated config expression.
     */
    String evaluateValue(String expression, Configuration... configurations);

    /**
     * Add a ConfigChangeSet listener to the given configuration instance.
     * @@param l the listener, not null.
     */
    void addChangeListener(Consumer<ConfigChangeSet> l);

    /**
     * Removes a ConfigChangeSet listener from the given configuration instance.
     * @param l the listener, not null.
     */
    void removeChangeListener(Consumer<ConfigChangeSet> l);

    /**
     * Method to publish changes on a {@link org.apache.tamaya.Configuration} to all interested parties.
     * Basically this method gives an abstraction on the effective event bus design fo listeners. In a CDI context
     * the CDI enterprise event bus should be used internally to do the work, whereas in a SE only environment
     * a more puristic approach would be useful.
     * @param configChangeSet the change to be published, not null.
     */
    void publishChange(ConfigChangeSet configChangeSet);
}
