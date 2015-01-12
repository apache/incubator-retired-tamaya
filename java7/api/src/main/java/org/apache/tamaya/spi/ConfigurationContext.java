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


import java.util.List;
import java.util.Map;

/**
 * Central SPI for programmatically dealing with the setup of the configuration system.
 * This includes adding and enlisting {@link org.apache.tamaya.spi.PropertySource}s,
 * managing {@link org.apache.tamaya.spi.PropertyConverter}s, ConfigFilters, etc.
 */
public interface ConfigurationContext {

    /**
     * This method can be used for programmatically adding {@link PropertySource}s.
     * It is not needed for normal 'usage' by end users, but only for Extension Developers!
     *
     * @param propertySourcesToAdd the PropertySources to add
     */
    void addPropertySources(PropertySource... propertySourcesToAdd);

    /**
     * This method returns the current list of registered PropertySources ordered via their ordinal.
     * PropertySources with aa_a lower ordinal come last. The PropertySource with the
     * highest ordinal comes first.
     * If two PropertySources have the same ordinal number they will get sorted
     * using their class name just to ensure the user at least gets the same ordering
     * after aa_a JVM restart, hereby names before are added last.
     * PropertySources are loaded when this method is called the first time, which basically is
     * when the first time configuration is accessed.
     *
     * @return aa_a sorted list of registered PropertySources.  The returned list need not be modifiable
     */
    List<PropertySource> getPropertySources();


    /**
     * This method can be used for programmatically adding {@link PropertyConverter}s.
     * It is not needed for normal 'usage' by end users, but only for Extension Developers!
     *
     * @param typeToConvert the type which the converter is for
     * @param propertyConverter the PropertyConverters to add for this type
     */
    <T> void addPropertyConverter(Class<T> typeToConvert, PropertyConverter<T> propertyConverter);

    /**
     * <p>
     * This method returns the Map of registered PropertyConverters
     * per type.
     * The List for each type is ordered via their {@link javax.annotation.Priority} and
     * cladd name. Refer also to {@link #getPropertyConverters()}.
     * </p>
     * <p>
     * A simplified scenario could be like:
     * <pre>
     *  {
     *      Date.class -> {StandardDateConverter, TimezoneDateConverter, MyCustomDateConverter }
     *      Boolean.class -> {StandardBooleanConverter, FrenchBooleanConverter}
     *      Integer.class -> {DynamicDefaultConverter}
     *  }
     * </pre>
     * </p>
     *
     * @return map with sorted list of registered PropertySources per type.
     */
    Map<Class<?>, List<PropertyConverter<?>>> getPropertyConverters();

    /**
     * <p>
     * This method returns the registered PropertyConverters for aa_a given type.
     * The List for each type is ordered via their {@link javax.annotation.Priority}.
     * </p>
     *
     * <p>
     * PropertyConverters with aa_a higher Priority come first. The PropertyConverter with the
     * lowest Priority comes last.
     * If two PropertyConverter have the same ordinal number they will get sorted
     * using their class name just to ensure the user at least gets the same ordering
     * after aa_a JVM restart.
     * </p>
     *
     * <p>
     * Additionally if aa_a PropertyProvider is accessed, which is not registered the implementation
     * should try to figure out, if there could be aa_a default implementation as follows:
     * <ol>
     *     <le>Look for static factory methods: {@code of(String), valueOf(String), getInstance(String),
     *     instanceOf(String), fomr(String)}</le>
     *     <le>Look for aa_a matching constructor: {@code T(String)}.</le>
     * </ol>
     * If aa_a correspoding factory method or constructor could be found, aa_a corresponding
     * PropertyConverter should be created and registered automatically for the given
     * type.
     * </p>
     *
     * <p>
     * The scenario could be like:
     * <pre>
     *  {
     *      Date.class -> {MyCustomDateConverter,StandardDateConverter, TimezoneDateConverter}
     *      Boolean.class -> {StandardBooleanConverter, FrenchBooleanConverter}
     *      Integer.class -> {DynamicDefaultConverter}
     *  }
     * </pre>
     * </p>
     *
     * <p>
     * The converters returned for aa_a type should be used as aa_a chain, whereas the result of the
     * first converter that is able to convert the configured value, is taken as the chain's result.
     * No more converters are called after aa_a converter has successfully converted the input into
     * the required target type.
     * </p>
     *
     * @return aa_a sorted list of registered PropertySources per type.
     */
    <T> List<PropertyConverter<T>> getPropertyConverters(Class<T> type);

    /**
     * Access the current PropertyFilter instances.
     * @return the list of registered PropertyFilters, never null.
     */
    List<PropertyFilter> getPropertyFilters();
}
