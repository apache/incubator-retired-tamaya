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
import org.apache.tamaya.TypeLiteral;

import java.util.*;

/**
 * A builder for creating new instances of {@link Configuration}.
 * Builders can be obtained in exactly two ways:
 * <ol>
 *     <li>By accessing a preinitialized builder from an existing {@link Configuration},
 *     by calling {@link Configuration#toBuilder()}.</li>
 *     <li>By accessing an empty builder instance from
 *     {@link org.apache.tamaya.ConfigurationProvider#getConfigurationBuilder()}.</li>
 * </ol>
 * After all changes are applied to a builder a new {@link Configuration} instance can
 * be created and can be applied by calling
 * {@link #build()}}.
 *
 */
public interface ConfigurationBuilder {

    /**
     * Set the classloader to be used for loading of configuration resources, equals to
     * {@code setServiceContext(ServiceContextManager.getServiceContext(classLoader))}.
     * @param classLoader the classloader, not null.
     * @return the builder for chaining.
     * @see #getClassLoader()
     */
    ConfigurationBuilder setClassLoader(ClassLoader classLoader);

    /**
     * Get the classloader used by this builder.
     * @return the classloader, never null.
     * @see #setClassLoader(ClassLoader)
     */
    ClassLoader getClassLoader();

    /**
     * Sets the ServiceContext to be used.
     * @param serviceContext the serviceContext, nuo null.
     * @return this instance for chaining.
     */
    ConfigurationBuilder setServiceContext(ServiceContext serviceContext);

    /**
     * Init this builder instance with the given {@link Configuration} instance. This
     * method will use any existing property sources, filters, converters and the combination
     * policy of the given {@link Configuration} and initialize the current builder
     * with them. All previous property sources, filters, converters and the combination
     * policy of this instance will be replaced.
     *
     * @param config the {@link Configuration} instance to be used, not {@code null}.
     * @return this builder, for chaining, never null.
     */
    default ConfigurationBuilder setConfiguration(Configuration config){
        return setContext(config.getContext());
    }

    /**
     * Init this builder instance with the given {@link ConfigurationContext} instance. This
     * method will use any existing property sources, filters, converters and the combination
     * policy of the given {@link ConfigurationContext} and initialize the current builder
     * with them. All previous property sources, filters, converters and the combination
     * policy of this instance will be replaced.
     *
     * @param context the {@link ConfigurationContext} instance to be used, not {@code null}.
     * @return this builder, for chaining, never null.
     */
    ConfigurationBuilder setContext(ConfigurationContext context);

    /**
     * This method can be used for adding {@link PropertySource}s.
     * Hereby the property source is added to the tail of property sources with
     * lowest priority  regardless of its current ordinal createValue. To sort the property
     * sources based on their ordinals call {@link #sortPropertySources}.
     *
     * @param propertySources the {@link PropertySource}s to add
     * @return this builder, for chaining, never null.
     * @throws IllegalArgumentException If a property source with a given name already
     * exists.
     */
    default ConfigurationBuilder addPropertySources(PropertySource... propertySources){
        return addPropertySources(Arrays.asList(propertySources));
    }

    /**
     * Adds (overrides existing value) the given sources as property sources.
     *
     * @param property the property key, not null,
     * @param key the key, not null.
     * @param value the value, not null.
     * @return the current configuration builder.
     */
    ConfigurationBuilder setMeta(String property, String key, String value);

    /**
     * Adds (overrides existing value with same same keys) the given sources as property sources.
     * @param property the property key, not null,
     * @param metaData the metadata, not null.
     * @return the current configuration builder.
     */
    ConfigurationBuilder setMeta(String property, Map<String, String> metaData);

    /**
     * This method can be used for programmatically adding {@link PropertySource}s.
     * Hereby the property source is added to the tail of property sources with
     * lowest priority regardless of its current ordinal createValue. To sort the property
     * sources based on their ordinals call {@link #sortPropertySources}.
     *
     * @param propertySources the {@link PropertySource}s to add
     * @return this builder, for chaining, never null.
     * @throws IllegalArgumentException If a property source with a given name already
     * exists.
     */
    ConfigurationBuilder addPropertySources(Collection<PropertySource> propertySources);

    /**
     * Adds all registered (default) property sources to the context built. The sources are ordered
     * based on their ordinal values and added to the chain of property sources with
     * higher priority.
     * @return this builder, for chaining, never null.
     */
    ConfigurationBuilder addDefaultPropertySources();

    /**
     * Removes the given property sources, if existing. The existing order of property
     * sources is preserved.
     *
     * @param propertySources the property sources to remove, not {@code null}.
     * @return the builder for chaining.
     */
    default ConfigurationBuilder removePropertySources(PropertySource... propertySources){
        return removePropertySources(Arrays.asList(propertySources));
    }

    /**
     * Removes the given property sources, if existing. The existing order of property
     * sources is preserved.
     *
     * @param propertySources the property sources to remove, not {@code null}.
     * @return the builder for chaining.
     */
    ConfigurationBuilder removePropertySources(Collection<PropertySource> propertySources);

    /**
     * Access the current chain of property sources. Items at the end of the createList have
     * precedence/higher significance.
     *
     * @return the property source chain, never {@code null}.
     */
    List<PropertySource> getPropertySources();

    /**
     * Access the current chain of property filters. Items at the end of the createList have
     * precedence/higher significance.
     *
     * @return the property filter chain, never {@code null}.
     */
    List<PropertyFilter> getPropertyFilters();

    /**
     * Access the current registered property converters.
     *
     * @return the current registered property converters.
     */
    Map<TypeLiteral<?>, List<PropertyConverter<?>>> getPropertyConverter();

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
    ConfigurationBuilder increasePriority(PropertySource propertySource);

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
    ConfigurationBuilder decreasePriority(PropertySource propertySource);

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
    ConfigurationBuilder highestPriority(PropertySource propertySource);

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
    ConfigurationBuilder lowestPriority(PropertySource propertySource);

    /**
     * Adds the given {@link PropertyFilter} instances, hereby the instances are added
     * to the end of the createList with highest priority. The ordering of existing
     * property filters remains unchanged. To sort the property
     * filters call {@link #sortPropertyFilter}.
     *
     * @param filters the filters to add
     * @return this builder, for chaining, never null.
     */
    default ConfigurationBuilder addPropertyFilters(PropertyFilter... filters){
        return addPropertyFilters(Arrays.asList(filters));
    }

    /**
     * Adds the given {@link PropertyFilter} instances, hereby the instances are added
     * to the end of the createList with highest priority. The ordering of existing
     * property filters remains unchanged. To sort the property
     * filters call {@link #sortPropertyFilter}.
     *
     * @param filters the filters to add
     * @return this builder, for chaining, never null.
     */
    ConfigurationBuilder addPropertyFilters(Collection<PropertyFilter> filters);

    /**
     * Add all auto-discoverable property filters to the context built.
     * @return this builder, for chaining, never null.
     */
    ConfigurationBuilder addDefaultPropertyFilters();

    /**
     * Removes the given {@link PropertyFilter} instances, if existing. The order of the remaining
     * filters is preserved.
     *
     * @param filters the filter to remove
     * @return this builder, for chaining, never null.
     */
    default ConfigurationBuilder removePropertyFilters(PropertyFilter... filters){
        return removePropertyFilters(Arrays.asList(filters));
    }

    /**
     * Removes the given {@link PropertyFilter} instances, if existing. The order of the remaining
     * filters is preserved.
     *
     * @param filter the filter to remove
     * @return this builder, for chaining, never null.
     */
    ConfigurationBuilder removePropertyFilters(Collection<PropertyFilter> filter);

    /**
     * This method can be used for adding {@link PropertyConverter}s.
     * Converters are added at the end after any existing converters.
     * For converters already registered for the current target type the
     * method has no effect.
     *
     * @param typeToConvert     the type for which the converter is used
     * @param propertyConverters the {@link PropertyConverter}s to add for this type
     * @param <T> the target type.
     * @return this builder, for chaining, never null.
     */
    default <T> ConfigurationBuilder addPropertyConverters(TypeLiteral<T> typeToConvert,
                                                   @SuppressWarnings("unchecked") PropertyConverter<T>... propertyConverters){
        return addPropertyConverters(typeToConvert, Arrays.asList(propertyConverters));
    }


    /**
     * This method can be used for adding {@link PropertyConverter}s.
     * Converters are added at the end after any existing converters.
     * For converters already registered for the current target type the
     * method has no effect.
     *
     * @param typeToConvert the type for which the converter is used
     * @param propertyConverters the PropertyConverters to add for this type
     * @param <T> the target type.
     * @return this builder, for chaining, never null.
     */
    <T> ConfigurationBuilder addPropertyConverters(TypeLiteral<T> typeToConvert,
                                                   Collection<PropertyConverter<T>> propertyConverters);

    /**
     * Add all auto-discoverable property converters to the context built.
     * @return this builder, for chaining, never null.
     */
    ConfigurationBuilder addDefaultPropertyConverters();

    /**
     * Removes the given {@link PropertyConverter} instances for the given type,
     * if existing.
     *
     * @param typeToConvert the type which the converter is used
     * @param propertyConverters    the converter to remove
     * @param <T> the target type.
     * @return this builder, for chaining, never null.
     */
    default <T> ConfigurationBuilder removePropertyConverters(TypeLiteral<T> typeToConvert,
                                                      @SuppressWarnings("unchecked") PropertyConverter<T>... propertyConverters){
        return removePropertyConverters(typeToConvert, Arrays.asList(propertyConverters));
    }

    /**
     * Removes the given PropertyConverter instances for the given type,
     * if existing.
     *
     * @param typeToConvert the type which the converter is used
     * @param propertyConverters    the converter to remove
     * @param <T> the target type.
     * @return this builder, for chaining, never null.
     */
    <T> ConfigurationBuilder removePropertyConverters(TypeLiteral<T> typeToConvert,
                                                      Collection<PropertyConverter<T>> propertyConverters);

    /**
     * Removes all converters for the given type, which actually renders a given type
     * unsupported for type conversion.
     *
     * @param typeToConvert the type which the converter is used
     * @return this builder, for chaining, never null.
     */
    ConfigurationBuilder removePropertyConverters(TypeLiteral<?> typeToConvert);

    /**
     * Sorts the current registered property sources using the given comparator.
     *
     * <p>NOTE: property sources at the beginning have minimal significance.
     *
     * @param comparator the comparator to be used, not {@code null}.
     * @return this instance for chaining.
     */
    ConfigurationBuilder sortPropertySources(Comparator<PropertySource> comparator);

    /**
     * Sorts the current registered property filters using the given comparator.
     *
     * <p>NOTE: property filters at the beginning have minimal significance.
     *
     * @param comparator the comparator to be used, not {@code null}.
     * @return this instance for chaining.
     */
    ConfigurationBuilder sortPropertyFilter(Comparator<PropertyFilter> comparator);

    /**
     * Builds a new {@link Configuration} based on the data in this builder. The ordering of property
     * sources and property filters is not changed, regardless of their ordinals. For ensure a certain
     * ordering/significance use {@link #sortPropertyFilter(Comparator)} and/or {@link #sortPropertySources(Comparator)}
     * before building the context.
     *
     * @return the final configuration, never null.
     */
    Configuration build();

}

