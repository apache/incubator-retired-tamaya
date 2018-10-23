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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.spi.*;

/**
 *
 * @author William.Lieurance 2018.02.18
 */
public class MockedConfigurationContext implements ConfigurationContext {

    ServiceContext serviceContext = ServiceContextManager.getServiceContext(getClass().getClassLoader());
    PropertyConverterManager pcm = new PropertyConverterManager(serviceContext,false);
    List<PropertySource> pss = new ArrayList<>();

    public MockedConfigurationContext() {
        pcm.register(TypeLiteral.of(Integer.class), new IntegerTestConverter());
        pss.add(new MockedPropertySource());
    }

    @Override
    public ServiceContext getServiceContext() {
        return serviceContext;
    }

    @Override
    public void addPropertySources(PropertySource... propertySources) {
        throw new RuntimeException("addPropertySources should be never called in this test");
    }

    @Override
    public List<PropertySource> getPropertySources() {
        return pss;
    }

    @Override
    public PropertySource getPropertySource(String name) {
        for (PropertySource ps : pss) {
            if (ps.getName().equals(name)) {
                return ps;
            }
        }
        return null;
    }

    @Override
    public <T> void addPropertyConverter(TypeLiteral<T> type, PropertyConverter<T> propertyConverter) {
        pcm.register(type, propertyConverter);
    }

    @Override
    public Map<TypeLiteral<?>, List<PropertyConverter<?>>> getPropertyConverters() {
        return pcm.getPropertyConverters();
    }

    @Override
    public <T> List<PropertyConverter<T>> getPropertyConverters(TypeLiteral<T> type) {
        return pcm.getPropertyConverters(type);
    }

    @Override
    public List<PropertyFilter> getPropertyFilters() {
        return Arrays.asList(new MockedPropertyFilter());
    }

    @Override
    public PropertyValueCombinationPolicy getPropertyValueCombinationPolicy() {
        return PropertyValueCombinationPolicy.DEFAULT_OVERRIDING_POLICY;
    }

    @Override
    public ConfigurationContextBuilder toBuilder() {
        throw new RuntimeException("toBuilder should be never called in this test");
    }
}
