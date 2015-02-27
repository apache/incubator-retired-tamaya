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

import org.apache.tamaya.PropertyConverter;
import org.apache.tamaya.TypeLiteral;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;

/**
 * A builder for creating new or adapting instances of {@link org.apache.tamaya.spi.ConfigurationContext}.
 * Builders can be obtained in exactly two ways:
 * <ol>
 *     <li>By accessing a preinitialized builder from an existing {@link org.apache.tamaya.spi.ConfigurationContext},
 *     by calling {@link ConfigurationContext#toBuilder()}.</li>
 *     <li>By accessing an empty builder instance from
 *     {@link org.apache.tamaya.ConfigurationProvider#getConfigurationContextBuilder()}.</li>
 * </ol>
 * After all changes are applied to a builder a new {@link org.apache.tamaya.spi.ConfigurationContext} instance can
 * be created and can be applied by calling
 * {@link org.apache.tamaya.ConfigurationProvider#setConfigurationContext(ConfigurationContext)}. Since this method can
 * throw an UnsupportedOperationException, you should check before if changing the current ConfigurationContext
 * programmatically is supported by calling
 * {@link org.apache.tamaya.ConfigurationProvider#isConfigurationContextSettable()}.
 */
public interface ConfigurationContextBuilder {

    /**
     * Init this builder instance with the given {@link org.apache.tamaya.spi.ConfigurationContext} instance. This
     * method will replace any existing data in the current builder with the data contained in the given
     * {@link org.apache.tamaya.spi.ConfigurationContext}.
     *
     * @param context the {@link org.apache.tamaya.spi.ConfigurationContext} instance to be used, not null.
     * @return this builder, for chaining, never null.
     */
    ConfigurationContextBuilder setContext(ConfigurationContext context);

    /**
     * This method can be used for programmatically adding {@link org.apache.tamaya.spi.PropertySource}s.
     * It is not needed for normal 'usage' by end users, but only for Extension Developers!
     *
     * @param propertySourcesToAdd the PropertySources to add
     * @return this builder, for chaining, never null.
     */
    default ConfigurationContextBuilder addPropertySources(PropertySource... propertySourcesToAdd) {
        return addPropertySources(Arrays.asList(propertySourcesToAdd));
    }


    /**
     * This method can be used for programmatically adding {@link org.apache.tamaya.spi.PropertySource}s.
     * It is not needed for normal 'usage' by end users, but only for Extension Developers!
     *
     * @param propertySourcesToAdd the PropertySources to add
     * @return this builder, for chaining, never null.
     */
    ConfigurationContextBuilder addPropertySources(Collection<PropertySource> propertySourcesToAdd);

    /**
     * This method can be used for programmatically adding {@link org.apache.tamaya.spi.PropertySource}s.
     * It is not needed for normal 'usage' by end users, but only for Extension Developers!
     *
     * @param propertySourcesNames the PropertySource names of the sources to remove
     * @return this builder, for chaining, never null.
     */
    default ConfigurationContextBuilder removePropertySources(String... propertySourcesNames) {
        return removePropertySources(Arrays.asList(propertySourcesNames));
    }

    /**
     * This method can be used for programmatically adding {@link org.apache.tamaya.spi.PropertySource}s.
     * It is not needed for normal 'usage' by end users, but only for Extension Developers!
     *
     * @param propertySourcesNames the PropertySource names of the sources to remove
     * @return this builder, for chaining, never null.
     */
    ConfigurationContextBuilder removePropertySources(Collection<String> propertySourcesNames);

    /**
     * Adds the given PropertyFilter instances.
     *
     * @param filters the filters to add
     * @return this builder, for chaining, never null.
     */
    default ConfigurationContextBuilder addPropertyFilters(PropertyFilter... filters) {
        return addPropertyFilters(Arrays.asList(filters));
    }

    /**
     * Adds the given PropertyFilter instances.
     *
     * @param filters the filters to add
     * @return this builder, for chaining, never null.
     */
    ConfigurationContextBuilder addPropertyFilters(Collection<PropertyFilter> filters);

    /**
     * Removes the given PropertyFilter instances.
     *
     * @param filters the filters to remove
     * @return this builder, for chaining, never null.
     */
    default ConfigurationContextBuilder removePropertyFilters(PropertyFilter... filters) {
        return removePropertyFilters(Arrays.asList(filters));
    }

    /**
     * Removes the given PropertyFilter instances.
     *
     * @param selector the selector query, not null.
     * @return this builder, for chaining, never null.
     */
    ConfigurationContextBuilder removePropertyFilters(Predicate<PropertyFilter> selector);

    /**
     * Removes the given PropertyFilter instances.
     *
     * @param filters the filters to remove
     * @return this builder, for chaining, never null.
     */
    ConfigurationContextBuilder removePropertyFilters(Collection<PropertyFilter> filters);

    /**
     * Remove the property sources selected by the given selector predicate.
     *
     * @param selector the selector query, not null.
     * @return this builder, for chaining, never null.
     */
    ConfigurationContextBuilder removePropertySources(Predicate<PropertySource> selector);

    /**
     * This method can be used for programmatically adding {@link org.apache.tamaya.PropertyConverter}s.
     * It is not needed for normal 'usage' by end users, but only for Extension Developers!
     *
     * @param typeToConvert     the type which the converter is for
     * @param propertyConverter the PropertyConverters to add for this type
     * @return this builder, for chaining, never null.
     */
    <T> ConfigurationContextBuilder addPropertyConverter(TypeLiteral<T> typeToConvert,
                                                         PropertyConverter<T> propertyConverter);

    /**
     * Removes the given PropertyConverter instances.
     *
     * @param typeToConvert the type which the converter is for
     * @param converters    the converters to remove
     * @return this builder, for chaining, never null.
     */
    ConfigurationContextBuilder removePropertyConverters(TypeLiteral<?> typeToConvert,
                                                         PropertyConverter<?>... converters);

    /**
     * Removes the given PropertyConverter instances.
     *
     * @param typeToConvert the type which the converter is for
     * @param converters    the converters to remove
     * @return this builder, for chaining, never null.
     */
    ConfigurationContextBuilder removePropertyConverters(TypeLiteral<?> typeToConvert,
                                                         Collection<PropertyConverter<?>> converters);

    /**
     * Sets the {@link org.apache.tamaya.spi.PropertyValueCombinationPolicy} used to evaluate the final
     * property values.
     *
     * @param policy the {@link org.apache.tamaya.spi.PropertyValueCombinationPolicy} used, not null
     * @return this builder, for chaining, never null.
     */
    ConfigurationContextBuilder setPropertyValueCombinationPolicy(PropertyValueCombinationPolicy policy);

    /**
     * Builds a {@link org.apache.tamaya.spi.ConfigurationContext} based on the data set.
     */
    ConfigurationContext build();

}
