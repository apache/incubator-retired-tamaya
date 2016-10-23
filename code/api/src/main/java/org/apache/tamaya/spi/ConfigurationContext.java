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

import java.util.List;
import java.util.Map;

/**
 * Central SPI for programmatically dealing with the setup of the configuration system.
 * This includes adding and enlisting {@link org.apache.tamaya.spi.PropertySource}s,
 * managing {@link PropertyConverter}s, ConfigFilters, etc.
 */
public interface ConfigurationContext {

    /**
     * This method can be used for programmatically adding {@link PropertySource}s.
     * It is not needed for normal 'usage' by end users, but only for Extension Developers!
     *
     * @param propertySources the PropertySources to add
     * @deprecated Use {@link ConfigurationContextBuilder} to create a new {@link ConfigurationContext}.
     * @see #toBuilder()
     */
    @Deprecated
    void addPropertySources(PropertySource... propertySources);

    /**
     * This method returns the current list of registered PropertySources ordered via their ordinal.
     * PropertySources with a lower ordinal come last. The PropertySource with the
     * highest ordinal comes first.
     * If two PropertySources have the same ordinal number they will get sorted
     * using their class name just to ensure the user at least gets the same ordering
     * after a JVM restart, hereby names before are added last.
     * PropertySources are loaded when this method is called the first time, which basically is
     * when the first time configuration is accessed.
     *
     * @return a sorted list of registered PropertySources.  The returned list need not be modifiable
     */
    List<PropertySource> getPropertySources();

    /**
     * Access a {@link PropertySource} using its (unique) name.
     * @param name the propoerty source's name, not null.
     * @return the propoerty source found, or null.
     */
    PropertySource getPropertySource(String name);

    /**
     * This method can be used for programmatically adding {@link PropertyConverter}s.
     * It is not needed for normal 'usage' by end users, but only for Extension Developers!
     *
     * @param <T> the type of the type literal
     * @param type the type which the converter is for
     * @param propertyConverter the PropertyConverters to add for this type
     * @deprecated Use {@link ConfigurationContextBuilder} to create a new {@link ConfigurationContext}.
     * @see #toBuilder()
     */
    @Deprecated
    <T> void addPropertyConverter(TypeLiteral<T> type, PropertyConverter<T> propertyConverter);


    /**
     * <p>
     * This method returns the Map of registered PropertyConverters
     * per type.
     * The List for each type is ordered via their {@link javax.annotation.Priority} and
     * cladd name.
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
     * @return map with sorted list of registered PropertySources per type.
     */
    Map<TypeLiteral<?>, List<PropertyConverter<?>>> getPropertyConverters();

    /**
     * <p>
     * This method returns the registered PropertyConverters for a given type.
     * The List for each type is ordered via their {@link javax.annotation.Priority}.
     * </p>
     *
     * <p>
     * PropertyConverters with a higher Priority come first. The PropertyConverter with the
     * lowest Priority comes last.
     * If two PropertyConverter have the same ordinal number they will get sorted
     * using their class name just to ensure the user at least gets the same ordering
     * after a JVM restart.
     * </p>
     *
     * <p>
     * Additionally if a PropertyProvider is accessed, which is not registered the implementation
     * should try to figure out, if there could be a default implementation as follows:</p>
     * <ol>
     *     <li>Look for static factory methods: {@code of(String), valueOf(String), getInstance(String),
     *     instanceOf(String), fomr(String)}</li>
     *     <li>Look for a matching constructor: {@code T(String)}.</li>
     * </ol>
     *
     * <p>
     * If a correspoding factory method or constructor could be found, a corresponding
     * PropertyConverter should be created and registered automatically for the given
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
     * first converter that is able to convert the configured value, is taken as the chain's result.
     * No more converters are called after a converter has successfully converted the input into
     * the required target type.
     * </p>
     * 
     * @param <T> the type of the type literal
     * @param type type of the desired converter
     * @return a sorted list of registered PropertySources per type.
     */
    <T> List<PropertyConverter<T>> getPropertyConverters(TypeLiteral<T> type);

    /**
     * Access the current PropertyFilter instances.
     * @return the list of registered PropertyFilters, never null.
     */
    List<PropertyFilter> getPropertyFilters();

    /**
     * Access the {@link org.apache.tamaya.spi.PropertyValueCombinationPolicy} used to evaluate the final
     * property values.
     * @return the {@link org.apache.tamaya.spi.PropertyValueCombinationPolicy} used, never null.
     */
    PropertyValueCombinationPolicy getPropertyValueCombinationPolicy();

    /**
     * Creates a {@link ConfigurationContextBuilder} preinitialized with the data from this instance.
     * @return a new builder instance, never null.
     */
    ConfigurationContextBuilder toBuilder();

}
