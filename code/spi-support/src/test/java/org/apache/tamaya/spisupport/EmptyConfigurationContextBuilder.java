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
package org.apache.tamaya.spisupport;

import com.sun.org.apache.bcel.internal.generic.INSTANCEOF;
import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.spi.*;

import java.util.*;

public class EmptyConfigurationContextBuilder implements ConfigurationContextBuilder{

    private static final ConfigurationContextBuilder INSTANCE = new EmptyConfigurationContextBuilder();

    @Override
    public ConfigurationContextBuilder setContext(ConfigurationContext context) {
        return this;
    }

    @Override
    public ConfigurationContextBuilder addPropertySources(PropertySource... propertySources) {
        return this;
    }

    @Override
    public ConfigurationContextBuilder addPropertySources(Collection<PropertySource> propertySources) {
        return this;
    }

    @Override
    public ConfigurationContextBuilder addDefaultPropertySources() {
        return this;
    }

    @Override
    public ConfigurationContextBuilder removePropertySources(PropertySource... propertySources) {
        return this;
    }

    @Override
    public ConfigurationContextBuilder removePropertySources(Collection<PropertySource> propertySources) {
        return this;
    }

    @Override
    public List<PropertySource> getPropertySources() {
        return Collections.emptyList();
    }

    @Override
    public List<PropertyFilter> getPropertyFilters() {
        return Collections.emptyList();
    }

    @Override
    public Map<TypeLiteral<?>, Collection<PropertyConverter<?>>> getPropertyConverter() {
        return Collections.emptyMap();
    }

    @Override
    public ConfigurationContextBuilder increasePriority(PropertySource propertySource) {
        return this;
    }

    @Override
    public ConfigurationContextBuilder decreasePriority(PropertySource propertySource) {
        return this;
    }

    @Override
    public ConfigurationContextBuilder highestPriority(PropertySource propertySource) {
        return this;
    }

    @Override
    public ConfigurationContextBuilder lowestPriority(PropertySource propertySource) {
        return null;
    }

    @Override
    public ConfigurationContextBuilder addPropertyFilters(PropertyFilter... filters) {
        return this;
    }

    @Override
    public ConfigurationContextBuilder addPropertyFilters(Collection<PropertyFilter> filters) {
        return this;
    }

    @Override
    public ConfigurationContextBuilder addDefaultPropertyFilters() {
        return null;
    }

    @Override
    public ConfigurationContextBuilder removePropertyFilters(PropertyFilter... filters) {
        return this;
    }

    @Override
    public ConfigurationContextBuilder removePropertyFilters(Collection<PropertyFilter> filters) {
        return this;
    }

    @Override
    public <T> ConfigurationContextBuilder addPropertyConverters(TypeLiteral<T> typeToConvert, PropertyConverter<T>... propertyConverters) {
        return null;
    }

    @Override
    public <T> ConfigurationContextBuilder addPropertyConverters(TypeLiteral<T> typeToConvert, Collection<PropertyConverter<T>> propertyConverters) {
        return this;
    }

    @Override
    public ConfigurationContextBuilder addDefaultPropertyConverters() {
        return this;
    }

    @Override
    public <T> ConfigurationContextBuilder removePropertyConverters(TypeLiteral<T> typeToConvert, PropertyConverter<T>... propertyConverters) {
        return this;
    }

    @Override
    public <T> ConfigurationContextBuilder removePropertyConverters(TypeLiteral<T> typeToConvert, Collection<PropertyConverter<T>> propertyConverters) {
        return this;
    }

    @Override
    public ConfigurationContextBuilder removePropertyConverters(TypeLiteral<?> typeToConvert) {
        return this;
    }

    @Override
    public ConfigurationContextBuilder sortPropertySources(Comparator<PropertySource> comparator) {
        return this;
    }

    @Override
    public ConfigurationContextBuilder sortPropertyFilter(Comparator<PropertyFilter> comparator) {
        return this;
    }

    @Override
    public ConfigurationContextBuilder setPropertyValueCombinationPolicy(PropertyValueCombinationPolicy policy) {
        return this;
    }

    @Override
    public ConfigurationContext build() {
        return EmptyConfigurationContext.instance();
    }

    public static ConfigurationContextBuilder instance() {
        return INSTANCE;
    }
}
