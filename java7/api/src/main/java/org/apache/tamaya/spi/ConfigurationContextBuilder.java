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

import java.util.Collection;

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
 * {@link org.apache.tamaya.ConfigurationProvider#setConfigurationContext(org.apache.tamaya.spi.ConfigurationContext)}. Since this method can
 * throw an UnsupportedOperationException, you should check before if changing the current ConfigurationContext
 * programmatically is supported by calling
 * {@link org.apache.tamaya.ConfigurationProvider#isConfigurationContextSettable()}.
 */
public interface ConfigurationContextBuilder {

    /**
     * Init this builder instance with the given {@link ConfigurationContext} instance. This
     * method will replace any existing data in the current builder with the data contained in the given
     * {@link ConfigurationContext}.
     *
     * @param context the {@link ConfigurationContext} instance to be used, not null.
     * @return this builder, for chaining, never null.
     */
    ConfigurationContextBuilder setContext(ConfigurationContext context);

    /**
     * This method can be used for programmatically adding {@link PropertySource}s.
     * It is not needed for normal 'usage' by end users, but only for Extension Developers!
     *
     * @param propertySourcesToAdd the PropertySources to add
     * @return this builder, for chaining, never null.
     */
    ConfigurationContextBuilder addPropertySources(PropertySource... propertySourcesToAdd);

    /**
     * This method can be used for programmatically adding {@link PropertySource}s.
     * It is not needed for normal 'usage' by end users, but only for Extension Developers!
     *
     * @param propertySourcesToAdd the PropertySources to add
     * @return this builder, for chaining, never null.
     */
    ConfigurationContextBuilder addPropertySources(Collection<PropertySource> propertySourcesToAdd);

    /**
     * This method can be used for programmatically adding {@link PropertySource}s.
     * It is not needed for normal 'usage' by end users, but only for Extension Developers!
     *
     * @param propertySourcesNames the PropertySource names of the sources to remove
     * @return this builder, for chaining, never null.
     */
    ConfigurationContextBuilder removePropertySources(String... propertySourcesNames);

    /**
     * This method can be used for programmatically adding {@link PropertySource}s.
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
    ConfigurationContextBuilder addPropertyFilters(PropertyFilter... filters);

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
    ConfigurationContextBuilder removePropertyFilters(PropertyFilter... filters);

    /**
     * Removes the given PropertyFilter instances.
     *
     * @param filters the filters to remove
     * @return this builder, for chaining, never null.
     */
    ConfigurationContextBuilder removePropertyFilters(Collection<PropertyFilter> filters);

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
     * Sets the {@link PropertyValueCombinationPolicy} used to evaluate the final
     * property values.
     *
     * @param policy the {@link PropertyValueCombinationPolicy} used, not null
     * @return this builder, for chaining, never null.
     */
    ConfigurationContextBuilder setPropertyValueCombinationPolicy(PropertyValueCombinationPolicy policy);

    /**
     * Builds a {@link ConfigurationContext} based on the data set.
     */
    ConfigurationContext build();

}
