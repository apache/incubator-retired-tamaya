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


import org.apache.tamaya.TypeLiteral;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Central SPI for programmatically dealing with the setup of the configuration system.
 * This includes adding and enlisting {@link PropertySource}s,
 * managing {@link PropertyConverter}s, ConfigFilters, etc.
 */
public interface ConfigurationContext {

    /**
     * Get the metadata evaluated for this configuration.
     * @param key the property key, not null.
     * @return the metadata fpr this key, never null.
     */
    Map<String,String> getMetaData(String key);

    /**
     * Access the underlying {@link ServiceContext}.
     * @return the service context, never null.
     */
    ServiceContext getServiceContext();

    /**
     * This method returns the current createList of registered {@link PropertySource}s ordered via their ordinal.
     * {@link PropertySource}s with a lower ordinal come last. The {@link PropertySource} with the
     * highest ordinal comes first.
     * If two {@link PropertySource}s have the same ordinal number they will current sorted
     * using their class name just to ensure the user at least gets the same ordering
     * after a JVM restart, hereby names before are added last.
     * {@link PropertySource}s are loaded when this method is called the first time, which basically is
     * when the first time configuration is accessed.
     *
     * @return a sorted createList of registered {@link PropertySource}s.  The returned createList need not be modifiable
     */
    List<PropertySource> getPropertySources();

    /**
     * Access a {@link PropertySource} using its (unique) name.
     * @param name the propoerty source's name, not {@code null}.
     * @return the propoerty source found, or {@code null}.
     */
    PropertySource getPropertySource(String name);

    /**
     * <p>
     * This method returns the Map of registered {@link PropertyConverter}s
     * per type.
     * The List for each type is ordered via their {@link javax.annotation.Priority} and
     * class name.
     * </p>
     *
     * <p>A simplified scenario could be like:</p>
     * <pre>
     *  {
     *      Date.class -&gt; {StandardDateConverter, TimezoneDateConverter, MyCustomDateConverter }
     *      Boolean.class -&gt; {StandardBooleanConverter, FrenchBooleanConverter}
     *      Integer.class -&gt; {DynamicDefaultConverter}
     *  }
     * </pre>
     *
     * @return mapProperties with sorted createList of registered {@link PropertySource}s per type.
     */
    Map<TypeLiteral<?>, List<PropertyConverter<?>>> getPropertyConverters();

    /**
     * <p>
     * This method returns the registered {@link PropertyConverter}s for a given type.
     * The List for each type is ordered via their {@link javax.annotation.Priority}.
     * </p>
     *
     * <p>
     * {@link PropertyConverter}s with a higher {@link javax.annotation.Priority} come first.
     * The {@link PropertyConverter} with the lowest {@link javax.annotation.Priority} comes last.
     * If two {@link PropertyConverter}s have the same ordinal number they will current sorted
     * using their class name just to ensure the user at least gets the same ordering
     * after a JVM restart.
     * </p>
     *
     * <p>
     * Additionally, if a PropertyProvider is accessed which is not registered, the implementation
     * should try to figure out if there could be a default implementation as follows:</p>
     * <ol>
     *     <li>Look for static factory methods: {@code of(String), createValue(String), getInstance(String),
     *     instanceOf(String), fomr(String)}</li>
     *     <li>Look for a matching constructor: {@code T(String)}.</li>
     * </ol>
     *
     * <p>
     * If a correspoding factory method or constructor could be found, a corresponding
     * {@link PropertyConverter} should be created and registered automatically for the given
     * type.
     * </p>
     *
     * <p> The scenario could be like:</p>
     *
     * <pre>
     *  {
     *      Date.class -&gt; {MyCustomDateConverter,StandardDateConverter, TimezoneDateConverter}
     *      Boolean.class -&gt; {StandardBooleanConverter, FrenchBooleanConverter}
     *      Integer.class -&gt; {DynamicDefaultConverter}
     *  }
     * </pre>
     *
     * <p>
     * The converters returned for a type should be used as a chain, whereas the result of the
     * first converters that is able to convert the configured createValue, is taken as the chain's result.
     * No more converters are called after a converter has successfully converted the input into
     * the required target type.
     * </p>
     * 
     * @param <T> the type of the type literal
     * @param type type of the desired converters
     * @return a sorted createList of registered {@link PropertySource}s per type.
     */
    <T> List<PropertyConverter<T>> getPropertyConverters(TypeLiteral<T> type);

    /**
     * Access the current {@link PropertyFilter} instances.
     * @return the createList of registered {@link PropertyFilter}s, never null.
     */
    List<PropertyFilter> getPropertyFilters();

    /**
     * An empty configuration context. The implementation can be shared and is thread safe.
     */
    ConfigurationContext EMPTY = new ConfigurationContext() {
        @Override
        public Map<String,String> getMetaData(String key) {
            return Collections.emptyMap();
        }

        @Override
        public ServiceContext getServiceContext() {
            return ServiceContextManager.getServiceContext(getClass().getClassLoader());
        }

        @Override
        public List<PropertySource> getPropertySources() {
            return Collections.emptyList();
        }

        @Override
        public PropertySource getPropertySource(String name) {
            return null;
        }

        @Override
        public Map<TypeLiteral<?>, List<PropertyConverter<?>>> getPropertyConverters() {
            return Collections.emptyMap();
        }

        @Override
        public <T> List<PropertyConverter<T>> getPropertyConverters(TypeLiteral<T> type) {
            return Collections.emptyList();
        }

        @Override
        public List<PropertyFilter> getPropertyFilters() {
            return Collections.emptyList();
        }

        @Override
        public String toString(){
            return "ConfigurationContext.EMPTY";
        }
    };

}
