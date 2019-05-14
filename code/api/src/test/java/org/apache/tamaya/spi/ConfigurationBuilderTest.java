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
import org.apache.tamaya.InvocationRecorder;
import org.apache.tamaya.TypeLiteral;
import org.junit.Test;
import java.util.*;

import static org.mockito.Mockito.mock;

/**
 * Tests the abstract functionality of {@link ConfigurationBuilder}.
 */
public class ConfigurationBuilderTest {

    private TestConfigurationBuilder builderUnderTest = new TestConfigurationBuilder();


    @Test
    public void addPropertySources_EllipseOp(){

        PropertySource testPropertySource1 = mock(PropertySource.class);
        PropertySource testPropertySource2 = mock(PropertySource.class);
        builderUnderTest.addPropertySources(testPropertySource1, testPropertySource2);
        builderUnderTest.recorder.assertInvocation("addPropertySources", Arrays.asList(testPropertySource1, testPropertySource2));
    }

    @Test
    public void removePropertySources_EllipseOp(){
        PropertySource testPropertySource1 = mock(PropertySource.class);
        PropertySource testPropertySource2 = mock(PropertySource.class);
        builderUnderTest.removePropertySources(testPropertySource1, testPropertySource2);
        builderUnderTest.recorder.assertInvocation("removePropertySources", Arrays.asList(testPropertySource1, testPropertySource2));
    }

    @Test
    public void addPropertyFilters_EllipseOp(){
        PropertyFilter testFilter1 = mock(PropertyFilter.class);
        PropertyFilter testFilter2 = mock(PropertyFilter.class);
        builderUnderTest.addPropertyFilters(testFilter1, testFilter2);
        builderUnderTest.recorder.assertInvocation("addPropertyFilters", Arrays.asList(testFilter1, testFilter2));
    }

    @Test
    public void removePropertyFilters_EllipseOp(){
        PropertyFilter testFilter1 = mock(PropertyFilter.class);
        PropertyFilter testFilter2 = mock(PropertyFilter.class);
        builderUnderTest.removePropertyFilters(testFilter1, testFilter2);
        builderUnderTest.recorder.assertInvocation("removePropertyFilters", Arrays.asList(testFilter1, testFilter2));
    }

    @Test
    public void addPropertyConverters_EllipseOp(){
        PropertyConverter testConverter1 = mock(PropertyConverter.class);
        PropertyConverter testConverter2 = mock(PropertyConverter.class);
        builderUnderTest.addPropertyConverters(TypeLiteral.of(String.class),
                testConverter1, testConverter2);
        builderUnderTest.recorder.assertInvocation("addPropertyConverters", TypeLiteral.of(String.class),
                Arrays.asList(testConverter1, testConverter2));
    }

    @Test
    public void removePropertyConverters_EllipseOp(){
        PropertyConverter testConverter1 = mock(PropertyConverter.class);
        PropertyConverter testConverter2 = mock(PropertyConverter.class);
        builderUnderTest.removePropertyConverters(TypeLiteral.of(String.class),
                testConverter1, testConverter2);
        builderUnderTest.recorder.assertInvocation("removePropertyConverters", TypeLiteral.of(String.class),
                Arrays.asList(testConverter1, testConverter2));
    }

    @Test
    public void setConfiguration_NoClassloader(){
        Configuration config = mock(Configuration.class);
        builderUnderTest.setConfiguration(config);
        builderUnderTest.recorder.assertInvocation("setContext", config.getContext());
    }

    private class TestConfigurationBuilder implements ConfigurationBuilder {

        public InvocationRecorder recorder = new InvocationRecorder();

        @Override
        public ConfigurationBuilder setClassLoader(ClassLoader classLoader) {
            return null;
        }

        @Override
        public ClassLoader getClassLoader() {
            return null;
        }

        @Override
        public ConfigurationBuilder setServiceContext(ServiceContext serviceContext) {
            return null;
        }

        @Override
        public ConfigurationBuilder setContext(ConfigurationContext context) {
            recorder.recordMethodCall(context);
            return null;
        }

        @Override
        public ConfigurationBuilder setMeta(String property, String key, String value) {
            return null;
        }

        @Override
        public ConfigurationBuilder setMeta(String property, Map<String, String> metaData) {
            return null;
        }

        @Override
        public ConfigurationBuilder addPropertySources(Collection<PropertySource> propertySources) {
            recorder.recordMethodCall(propertySources);
            return null;
        }

        @Override
        public ConfigurationBuilder addDefaultPropertySources() {
            return null;
        }

        @Override
        public ConfigurationBuilder removePropertySources(Collection<PropertySource> propertySources) {
            recorder.recordMethodCall(propertySources);
            return null;
        }

        @Override
        public List<PropertySource> getPropertySources() {
            return null;
        }

        @Override
        public List<PropertyFilter> getPropertyFilters() {
            return null;
        }

        @Override
        public Map<TypeLiteral<?>, List<PropertyConverter<?>>> getPropertyConverter() {
            return null;
        }

        @Override
        public ConfigurationBuilder increasePriority(PropertySource propertySource) {
            return null;
        }

        @Override
        public ConfigurationBuilder decreasePriority(PropertySource propertySource) {
            return null;
        }

        @Override
        public ConfigurationBuilder highestPriority(PropertySource propertySource) {
            return null;
        }

        @Override
        public ConfigurationBuilder lowestPriority(PropertySource propertySource) {
            return null;
        }

        @Override
        public ConfigurationBuilder addPropertyFilters(Collection<PropertyFilter> filters) {
            recorder.recordMethodCall(filters);
            return null;
        }

        @Override
        public ConfigurationBuilder addDefaultPropertyFilters() {
            return null;
        }

        @Override
        public ConfigurationBuilder removePropertyFilters(Collection<PropertyFilter> filter) {
            recorder.recordMethodCall(filter);
            return null;
        }

        @Override
        public <T> ConfigurationBuilder addPropertyConverters(TypeLiteral<T> typeToConvert, Collection<PropertyConverter<T>> propertyConverters) {
            recorder.recordMethodCall(typeToConvert, propertyConverters);
            return null;
        }

        @Override
        public ConfigurationBuilder addDefaultPropertyConverters() {
            return null;
        }

        @Override
        public <T> ConfigurationBuilder removePropertyConverters(TypeLiteral<T> typeToConvert, Collection<PropertyConverter<T>> propertyConverters) {
            recorder.recordMethodCall(typeToConvert, propertyConverters);
            return null;
        }

        @Override
        public ConfigurationBuilder removePropertyConverters(TypeLiteral<?> typeToConvert) {
            return null;
        }

        @Override
        public ConfigurationBuilder sortPropertySources(Comparator<PropertySource> comparator) {
            return null;
        }

        @Override
        public ConfigurationBuilder sortPropertyFilter(Comparator<PropertyFilter> comparator) {
            return null;
        }

        @Override
        public ConfigurationBuilder sortPropertyConverter(Comparator<PropertyConverter> comparator) {
            return null;
        }

        @Override
        public Configuration build() {
            return null;
        }
    }
}