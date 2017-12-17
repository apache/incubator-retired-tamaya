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

import org.apache.tamaya.base.DefaultConfigBuilder;

import javax.config.spi.ConfigBuilder;
import javax.config.spi.ConfigSource;
import javax.config.spi.Converter;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * A builder for creating new or adapting instances of {@link javax.config.Config}.
 * Builders can be obtained in exactly two ways:
 * <ol>
 *     <li>By accessing an empty builder instance from
 *     {@link javax.config.spi.ConfigProviderResolver#getBuilder()}.</li>
 * </ol>
 */
public interface TamayaConfigBuilder extends ConfigBuilder, ConfigContextSupplier{

    /**
     * Create a new empty configuration builder.
     * @return
     */
    static TamayaConfigBuilder create() {
        return new DefaultConfigBuilder();
    }

    static TamayaConfigBuilder create(ConfigContextSupplier contextSupplier){
        return new DefaultConfigBuilder(contextSupplier.getConfigContext());
    }

    static TamayaConfigBuilder from(ConfigBuilder configBuilder){
        if(configBuilder instanceof TamayaConfigBuilder) {
            return (TamayaConfigBuilder) configBuilder;
        }else if(configBuilder instanceof ConfigContextSupplier){
            return create((ConfigContextSupplier)configBuilder);
        }
        throw new IllegalArgumentException("Builder must implement ConfigContextSupplier.");
    }

    /**
     * This method can be used for programmatically adding {@link ConfigSource}s.
     * Hereby the property source is added to the tail of property sources with
     * lowest priority regardless of its current ordinal value. To sort the property
     * sources based on their ordinals call {@link #sortSources}.
     *
     * @param propertySources the PropertySources to add
     * @return this builder, for chaining, never null.
     * @throws IllegalArgumentException If a property source with a given name already
     * exists.
     */
    TamayaConfigBuilder withSources(Collection<ConfigSource> propertySources);

    /**
     * Removes the given property sources, if existing. The existing order of property
     * sources is preserved.
     *
     * @param propertySources the property sources to remove, not {@code null}.
     * @return the builder for chaining.
     */
    TamayaConfigBuilder removeSources(ConfigSource... propertySources);

    /**
     * Removes the given property sources, if existing. The existing order of property
     * sources is preserved.
     *
     * @param propertySources the property sources to remove, not {@code null}.
     * @return the builder for chaining.
     */
    TamayaConfigBuilder removeSources(Collection<ConfigSource> propertySources);

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
    TamayaConfigBuilder increasePriority(ConfigSource propertySource);

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
    TamayaConfigBuilder decreasePriority(ConfigSource propertySource);

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
    TamayaConfigBuilder highestPriority(ConfigSource propertySource);

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
    TamayaConfigBuilder lowestPriority(ConfigSource propertySource);

    /**
     * Access the current chain of property sources. Items at the end of the list have
     * precedence/more significance.
     *
     * @return the property source chain, never {@code null}.
     */
    List<ConfigSource> getSources();

    /**
     * Sorts the current registered property sources using the given comparator.
     *
     * NOTE: property sources at the beginning have minimal significance.
     *
     * @param comparator the comparator to be used, not {@code null}.
     * @return this instance for chaining.
     */
    TamayaConfigBuilder sortSources(Comparator<ConfigSource> comparator);

    /**
     * Adds the given PropertyFilter instances, hereby the instances are added
     * to the end of the list with highest priority. The ordering of existing
     * property filters remains unchanged. To sort the property
     * filters call {@link #sortFilter}.
     *
     * @param filters the filters to add
     * @return this builder, for chaining, never null.
     */
    TamayaConfigBuilder withFilters(Filter... filters);

    /**
     * Adds the given PropertyFilter instances, hereby the instances are added
     * to the end of the list with highest priority. The ordering of existing
     * property filters remains unchanged. To sort the property
     * filters call {@link #sortFilter}.
     *
     * @param filters the filters to add
     * @return this builder, for chaining, never null.
     */
    TamayaConfigBuilder withFilters(Collection<Filter> filters);

    /**
     * Add all registered (default) property filters to the context built.
     * @return this builder, for chaining, never null.
     */
    TamayaConfigBuilder addDiscoveredFilters();

    /**
     * Removes the given PropertyFilter instances, if existing. The order of the remaining
     * filters is preserved.
     *
     * @param filters the filter to remove
     * @return this builder, for chaining, never null.
     */
    TamayaConfigBuilder removeFilters(Filter... filters);

    /**
     * Removes the given PropertyFilter instances, if existing. The order of the remaining
     * filters is preserved.
     *
     * @param filters the filter to remove
     * @return this builder, for chaining, never null.
     */
    TamayaConfigBuilder removeFilters(Collection<Filter> filters);

    /**
     * Access the current chain of property filters. Items at the end of the list have
     * precedence/more significance.
     *
     * @return the property source chain, never {@code null}.
     */
    List<Filter> getFilters();

    /**
     * This method can be used for adding {@link Converter}s.
     * Converters are added at the end after any existing converters.
     * For converters already registered for the current target type the
     * method has no effect.
     *
     * @param converters the converters to add for this type
     * @return this builder, for chaining, never null.
     */
    TamayaConfigBuilder withConverters(Collection<Converter<?>> converters);

    /**
     * This method can be used for adding {@link Converter}s.
     * Converters are added at the end after any existing converters.
     * For converters already registered for the current target type the
     * method has no effect.
     *
     * @param typeToConvert     the type for which the converters is for
     * @param converters the converters to add for this type
     * @param <T> the target type.
     * @return this builder, for chaining, never null.
     */
    <T> TamayaConfigBuilder withConverters(Class<T> typeToConvert, Converter<T>... converters);

    /**
     * This method can be used for adding {@link Converter}s.
     * Converters are added at the end after any existing converters.
     * For converters already registered for the current target type the
     * method has no effect.
     *
     * @param typeToConvert     the type for which the converters is for
     * @param converters the converters to add for this type
     * @param <T> the target type.
     * @return this builder, for chaining, never null.
     */
    <T> TamayaConfigBuilder withConverters(Class<T> typeToConvert, Collection<Converter<T>> converters);


    /**
     * Removes the given PropertyConverter instances for the given type,
     * if existing.
     *
     * @param typeToConvert the type which the converters is for
     * @param converters    the converters to remove
     * @param <T> the target type.
     * @return this builder, for chaining, never null.
     */
    <T> TamayaConfigBuilder removeConverters(Class<T> typeToConvert, Converter<T>... converters);

    /**
     * Removes the given PropertyConverter instances for the given type,
     * if existing.
     *
     * @param typeToConvert the type which the converters is for
     * @param converters    the converters to remove
     * @param <T> the target type.
     * @return this builder, for chaining, never null.
     */
    <T> TamayaConfigBuilder removeConverters(Class<T> typeToConvert, Collection<Converter<T>> converters);

    /**
     * Removes all converters for the given type, which actually renders a given type
     * unsupported for type conversion.
     *
     * @param typeToConvert the type which the converters is for
     * @return this builder, for chaining, never null.
     */
    <T> TamayaConfigBuilder removeConverters(Class<T> typeToConvert);

    /**
     * Access the current registered property converters.
     *
     * @return the current registered property converters.
     */
    Map<Type, List<Converter>> getConverter();

    /**
     * Sets the {@link ConfigValueCombinationPolicy} used to evaluate the final
     * property values.
     *
     * @param policy the {@link ConfigValueCombinationPolicy} used, not {@code null}.
     * @return this builder, for chaining, never null.
     */
    TamayaConfigBuilder withPropertyValueCombinationPolicy(ConfigValueCombinationPolicy policy);

    /**
     * Sorts the current registered property filters using the given comparator.
     *
     * NOTE: property filters at the beginning have minimal significance.
     *
     * @param comparator the comparator to be used, not {@code null}.
     * @return this instance for chaining.
     */
    TamayaConfigBuilder sortFilter(Comparator<Filter> comparator);

    @Override
    TamayaConfigBuilder addDefaultSources();

    @Override
    TamayaConfigBuilder addDiscoveredSources();

    @Override
    TamayaConfigBuilder addDiscoveredConverters();

    @Override
    TamayaConfigBuilder forClassLoader(ClassLoader loader);

    @Override
    TamayaConfigBuilder withSources(ConfigSource... sources);

    @Override
    TamayaConfigBuilder withConverters(Converter<?>... converters);

    @Override
    <T> TamayaConfigBuilder withConverter(Class<T> type, Converter<T> converter);

}

