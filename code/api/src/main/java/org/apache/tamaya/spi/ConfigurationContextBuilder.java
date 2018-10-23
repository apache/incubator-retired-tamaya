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

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * A builder for creating new or adapting instances of {@link ConfigurationContext}.
 * Builders can be obtained in exactly two ways:
 * <ol>
 *     <li>By accessing a preinitialized builder from an existing {@link ConfigurationContext},
 *     by calling {@link org.apache.tamaya.spi.ConfigurationContext#toBuilder()}.</li>
 *     <li>By accessing an empty builder instance from
 *     {@link org.apache.tamaya.ConfigurationProvider#getConfigurationContextBuilder()}.</li>
 * </ol>
 * After all changes are applied to a builder a new {@link ConfigurationContext} instance can
 * be created and can be applied by calling
 * {@link org.apache.tamaya.ConfigurationProvider#setConfigurationContext(org.apache.tamaya.spi.ConfigurationContext)}.
 * @deprecated Use {@link ConfigurationBuilder} instead.
 */
@Deprecated
public interface ConfigurationContextBuilder {

    /**
     * Set the classloader to be used for loading of configuration resources, equals to
     * {@code setServiceContext(ServiceContextManager.getServiceContext(classLoader))}.
     * @param classLoader the classloader, not null.
     * @return the builder for chaining.
     */
    ConfigurationContextBuilder setClassLoader(ClassLoader classLoader);

    /**
     * Sets the ServiceContext to be used.
     * @param serviceContext the serviceContext, nuo null.
     * @return this instance for chaining.
     */
    ConfigurationContextBuilder setServiceContext(ServiceContext serviceContext);

    /**
     * Init this builder instance with the given {@link ConfigurationContext} instance. This
     * method will use any existing property sources, filters, converters and the combination
     * policy of the given {@link ConfigurationContext} and initialize the current builder
     * with them.
     *
     * @param context the {@link ConfigurationContext} instance to be used, not {@code null}.
     * @return this builder, for chaining, never null.
     */
    ConfigurationContextBuilder setContext(ConfigurationContext context);

    /**
     * This method can be used for adding {@link PropertySource}s.
     * Hereby the property source is added to the tail of property sources with
     * lowest priority  regardless of its current ordinal value. To sort the property
     * sources based on their ordinals call {@link #sortPropertySources}.
     *
     * @param propertySources the PropertySources to add
     * @return this builder, for chaining, never null.
     * @throws IllegalArgumentException If a property source with a given name already
     * exists.
     */
    ConfigurationContextBuilder addPropertySources(PropertySource... propertySources);

    /**
     * This method can be used for programmatically adding {@link PropertySource}s.
     * Hereby the property source is added to the tail of property sources with
     * lowest priority regardless of its current ordinal value. To sort the property
     * sources based on their ordinals call {@link #sortPropertySources}.
     *
     * @param propertySources the PropertySources to add
     * @return this builder, for chaining, never null.
     * @throws IllegalArgumentException If a property source with a given name already
     * exists.
     */
    ConfigurationContextBuilder addPropertySources(Collection<PropertySource> propertySources);

    /**
     * Add all registered (default) property sources to the context built. The sources are ordered
     * based on their ordinal values and added to the chain of property sources with
     * higher priority.
     * @return this builder, for chaining, never null.
     */
    ConfigurationContextBuilder addDefaultPropertySources();

    /**
     * Removes the given property sources, if existing. The existing order of property
     * sources is preserved.
     *
     * @param propertySources the property sources to remove, not {@code null}.
     * @return the builder for chaining.
     */
    ConfigurationContextBuilder removePropertySources(PropertySource... propertySources);

    /**
     * Removes the given property sources, if existing. The existing order of property
     * sources is preserved.
     *
     * @param propertySources the property sources to remove, not {@code null}.
     * @return the builder for chaining.
     */
    ConfigurationContextBuilder removePropertySources(Collection<PropertySource> propertySources);

    /**
     * Access the current chain of property sources. Items at the end of the list have
     * precedence/more significance.
     *
     * @return the property source chain, never {@code null}.
     */
    List<PropertySource> getPropertySources();

    /**
     * Access the current chain of property filters. Items at the end of the list have
     * precedence/more significance.
     *
     * @return the property source chain, never {@code null}.
     */
    List<PropertyFilter> getPropertyFilters();

    /**
     * Access the current registered property converters.
     *
     * @return the current registered property converters.
     */
    Map<TypeLiteral<?>, Collection<PropertyConverter<?>>> getPropertyConverter();

    /**
     * Increases the priority of the given property source, by moving it towards the end
     * of the chain of property sources. If the property source given is already at the end
     * this method has no effect. This operation does not change any ordinal values.
     *
     * @param propertySource the property source to be incresed regarding its significance.
     * @return the builder for chaining.
     * @throws IllegalArgumentException If no such property source exists in the current
     * chain.
     */
    ConfigurationContextBuilder increasePriority(PropertySource propertySource);

    /**
     * Decreases the priority of the given property source, by moving it towards the start
     * of the chain of property sources. If the property source given is already the first
     * this method has no effect. This operation does not change any ordinal values.
     *
     * @param propertySource the property source to be decresed regarding its significance.
     * @return the builder for chaining.
     * @throws IllegalArgumentException If no such property source exists in the current
     * chain.
     */
    ConfigurationContextBuilder decreasePriority(PropertySource propertySource);

    /**
     * Increases the priority of the given property source to be maximal, by moving it to
     * the tail of the of property source chain. If the property source given is
     * already the last item this method has no effect. This operation does not change
     * any ordinal values.
     *
     * @param propertySource the property source to be maximized regarding its significance.
     * @return the builder for chaining.
     * @throws IllegalArgumentException If no such property source exists in the current
     * chain.
     */
    ConfigurationContextBuilder highestPriority(PropertySource propertySource);

    /**
     * Decreases the priority of the given property source to be minimal, by moving it to
     * the start of the chain of property source chain. If the property source given is
     * already the first item this method has no effect. This operation does not change
     * any ordinal values.
     *
     * @param propertySource the property source to be minimized regarding its significance.
     * @return the builder for chaining.
     * @throws IllegalArgumentException If no such property source exists in the current
     * chain.
     */
    ConfigurationContextBuilder lowestPriority(PropertySource propertySource);

    /**
     * Adds the given PropertyFilter instances, hereby the instances are added
     * to the end of the list with highest priority. The ordering of existing
     * property filters remains unchanged. To sort the property
     * filters call {@link #sortPropertyFilter}.
     *
     * @param filters the filters to add
     * @return this builder, for chaining, never null.
     */
    ConfigurationContextBuilder addPropertyFilters(PropertyFilter... filters);

    /**
     * Adds the given PropertyFilter instances, hereby the instances are added
     * to the end of the list with highest priority. The ordering of existing
     * property filters remains unchanged. To sort the property
     * filters call {@link #sortPropertyFilter}.
     *
     * @param filters the filters to add
     * @return this builder, for chaining, never null.
     */
    ConfigurationContextBuilder addPropertyFilters(Collection<PropertyFilter> filters);

    /**
     * Add all registered (default) property filters to the context built.
     * @return this builder, for chaining, never null.
     */
    ConfigurationContextBuilder addDefaultPropertyFilters();


    /**
     * Removes the given PropertyFilter instances, if existing. The order of the remaining
     * filters is preserved.
     *
     * @param filters the filter to remove
     * @return this builder, for chaining, never null.
     */
    ConfigurationContextBuilder removePropertyFilters(PropertyFilter... filters);

    /**
     * Removes the given PropertyFilter instances, if existing. The order of the remaining
     * filters is preserved.
     *
     * @param filters the filter to remove
     * @return this builder, for chaining, never null.
     */
    ConfigurationContextBuilder removePropertyFilters(Collection<PropertyFilter> filters);

    /**
     * This method can be used for adding {@link PropertyConverter}s.
     * Converters are added at the end after any existing converters.
     * For converters already registered for the current target type the
     * method has no effect.
     *
     * @param typeToConvert     the type for which the converters is for
     * @param propertyConverters the PropertyConverters to add for this type
     * @param <T> the target type.
     * @return this builder, for chaining, never null.
     */
    <T> ConfigurationContextBuilder addPropertyConverters(TypeLiteral<T> typeToConvert,
                                                          @SuppressWarnings("unchecked") PropertyConverter<T>... propertyConverters);

    /**
     * This method can be used for adding {@link PropertyConverter}s.
     * Converters are added at the end after any existing converters.
     * For converters already registered for the current target type the
     * method has no effect.
     *
     * @param typeToConvert     the type for which the converters is for
     * @param propertyConverters the PropertyConverters to add for this type
     * @param <T> the target type.
     * @return this builder, for chaining, never null.
     */
    <T> ConfigurationContextBuilder addPropertyConverters(TypeLiteral<T> typeToConvert,
                                                          Collection<PropertyConverter<T>> propertyConverters);

    /**
     * Add all registered (default) property converters to the context built.
     * @return this builder, for chaining, never null.
     */
    ConfigurationContextBuilder addDefaultPropertyConverters();

    /**
     * Removes the given PropertyConverter instances for the given type,
     * if existing.
     *
     * @param typeToConvert the type which the converters is for
     * @param propertyConverters    the converters to remove
     * @param <T> the target type.
     * @return this builder, for chaining, never null.
     */
    <T> ConfigurationContextBuilder removePropertyConverters(TypeLiteral<T> typeToConvert,
                                                             @SuppressWarnings("unchecked") PropertyConverter<T>... propertyConverters);

    /**
     * Removes the given PropertyConverter instances for the given type,
     * if existing.
     *
     * @param typeToConvert the type which the converters is for
     * @param propertyConverters    the converters to remove
     * @param <T> the target type.
     * @return this builder, for chaining, never null.
     */
    <T> ConfigurationContextBuilder removePropertyConverters(TypeLiteral<T> typeToConvert,
                                                             Collection<PropertyConverter<T>> propertyConverters);

    /**
     * Removes all converters for the given type, which actually renders a given type
     * unsupported for type conversion.
     *
     * @param typeToConvert the type which the converters is for
     * @return this builder, for chaining, never null.
     */
    ConfigurationContextBuilder removePropertyConverters(TypeLiteral<?> typeToConvert);

    /**
     * Sorts the current registered property sources using the given comparator.
     *
     * NOTE: property sources at the beginning have minimal significance.
     *
     * @param comparator the comparator to be used, not {@code null}.
     * @return this instance for chaining.
     */
    ConfigurationContextBuilder sortPropertySources(Comparator<PropertySource> comparator);

    /**
     * Sorts the current registered property filters using the given comparator.
     *
     * NOTE: property filters at the beginning have minimal significance.
     *
     * @param comparator the comparator to be used, not {@code null}.
     * @return this instance for chaining.
     */
    ConfigurationContextBuilder sortPropertyFilter(Comparator<PropertyFilter> comparator);

    /**
     * Sets the {@link PropertyValueCombinationPolicy} used to evaluate the final
     * property values.
     *
     * @param policy the {@link PropertyValueCombinationPolicy} used, not {@code null}.
     * @return this builder, for chaining, never null.
     */
    ConfigurationContextBuilder setPropertyValueCombinationPolicy(PropertyValueCombinationPolicy policy);

    /**
     * Access the serviceContext used by the builder.
     * @return the serviceContext, never null.
     */
    ServiceContext getServiceContext();

    /**
     * Builds a new {@link ConfigurationContext} based on the data in this builder. The ordering of property
     * sources and property filters is not changed, regardless of their ordinals. For ensure a certain
     * ordering/significance call {@link #sortPropertyFilter(Comparator)} and/or {@link #sortPropertySources(Comparator)}
     * before building the context.
     *
     * @return the final context to be used to create a configuration.
     * @see org.apache.tamaya.ConfigurationProvider#createConfiguration(ConfigurationContext)
     */
    ConfigurationContext build();

}

