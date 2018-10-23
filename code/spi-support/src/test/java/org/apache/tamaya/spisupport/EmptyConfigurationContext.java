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

import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.spi.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class EmptyConfigurationContext implements ConfigurationContext{

    private static final ConfigurationContext INSTANCE = new EmptyConfigurationContext();

    @Override
    public ServiceContext getServiceContext() {
        return ServiceContextManager.getServiceContext(getClass().getClassLoader());
    }

    @Override
    public void addPropertySources(PropertySource... propertySources) {
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
    public <T> void addPropertyConverter(TypeLiteral<T> type, PropertyConverter<T> propertyConverter) {
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
    public PropertyValueCombinationPolicy getPropertyValueCombinationPolicy() {
        return PropertyValueCombinationPolicy.DEFAULT_OVERRIDING_POLICY;
    }

    @Override
    public ConfigurationContextBuilder toBuilder() {
        return EmptyConfigurationContextBuilder.instance();
    }

    public static ConfigurationContext instance() {
        return INSTANCE;
    }
}
