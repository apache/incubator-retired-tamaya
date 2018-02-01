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

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import org.apache.tamaya.ConfigOperator;
import org.apache.tamaya.ConfigQuery;
import org.apache.tamaya.Configuration;
import org.apache.tamaya.TypeLiteral;
import org.junit.Test;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Tests for {@link ConversionContext}, created by atsticks on 20.08.16.
 */
public class ConversionContextTest {

    @Test
    public void getSetKey() throws Exception {
        ConversionContext ctx = new ConversionContext.Builder("getKey", TypeLiteral.of(String.class)).build();
        assertEquals("getKey", ctx.getKey());
        ctx = new ConversionContext.Builder("getKey", TypeLiteral.of(String.class)).setKey("setKey").build();
        assertEquals("setKey", ctx.getKey());
    }

    @Test
    public void getSetTargetType() throws Exception {
        ConversionContext ctx = new ConversionContext.Builder("getTargetType", TypeLiteral.of(String.class)).build();
        assertEquals(TypeLiteral.of(String.class), ctx.getTargetType());
        ctx = new ConversionContext.Builder("setTargetType", TypeLiteral.of(String.class)).setTargetType(TypeLiteral.of(Integer.class)).build();
        assertEquals(TypeLiteral.of(Integer.class), ctx.getTargetType());
    }

    @Test
    public void getSetAnnotatedElement() throws Exception {
        ConversionContext ctx = new ConversionContext.Builder("getAnnotatedElement", TypeLiteral.of(List.class)).build();
        assertNull(ctx.getAnnotatedElement());
        ctx = new ConversionContext.Builder(TypeLiteral.of(List.class)).setAnnotatedElement(MyAnnotatedElement).build();
        assertEquals(MyAnnotatedElement, ctx.getAnnotatedElement());
    }

    @Test
    public void testConfiguration() throws Exception {
        Configuration config = new MyConfiguration();
        ConversionContext ctx = new ConversionContext.Builder("testConfiguration", TypeLiteral.of(List.class))
                .setConfiguration(config).build();
        assertEquals(config, ctx.getConfiguration());
    }

    @Test
    public void testSupportedFormats() throws Exception {
        ArrayList<String> readable = new ArrayList<>(2);
        readable.add("0.0.0.0/nnn (MyConverter)");
        readable.add("x.x.x.x/yyy (MyConverter)");
        ArrayList<String> writeable = new ArrayList<>(2);
        writeable.add("0.0.0.0/nnn");
        writeable.add("x.x.x.x/yyy");

        ConversionContext ctx = new ConversionContext.Builder("getSupportedFormats", TypeLiteral.of(List.class))
                .addSupportedFormats(MyConverter.class, writeable.get(0), writeable.get(1)).build();
        assertTrue(ctx.getSupportedFormats().containsAll(readable));
        assertTrue(ctx.getSupportedFormats().indexOf(readable.get(0))
                < ctx.getSupportedFormats().indexOf(readable.get(1)));

        ctx = new ConversionContext.Builder(TypeLiteral.of(List.class)).build();
        assertTrue(ctx.getSupportedFormats().isEmpty());
        ctx.addSupportedFormats(MyConverter.class, writeable.get(0), writeable.get(1));
        assertTrue(ctx.getSupportedFormats().containsAll(readable));
        assertTrue(ctx.getSupportedFormats().indexOf(readable.get(0))
                < ctx.getSupportedFormats().indexOf(readable.get(1)));
    }

    @Test
    public void testToString() throws Exception {
        ConversionContext ctx = new ConversionContext.Builder("toString", TypeLiteral.of(List.class))
                .addSupportedFormats(MyConverter.class, "0.0.0.0/nnn", "x.x.x.x/yyy").build();
        assertEquals("ConversionContext{configuration=null, key='toString', targetType=TypeLiteral{type=interface java.util.List}, annotatedElement=null, supportedFormats=[0.0.0.0/nnn (MyConverter), x.x.x.x/yyy (MyConverter)]}", ctx.toString());
    }

    @Test
    public void testGetConfigurationContext() throws Exception {
        ConfigurationContext context = new MyConfigurationContext();
        ConversionContext ctx = new ConversionContext.Builder("getConfigurationContext", TypeLiteral.of(List.class))
                .setConfigurationContext(context).build();
        assertEquals(context, ctx.getConfigurationContext());
    }

    private static final AnnotatedElement MyAnnotatedElement = new AnnotatedElement() {
        @Override
        public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Annotation[] getAnnotations() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Annotation[] getDeclaredAnnotations() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    };

    private static final class MyConverter implements PropertyConverter<InetAddress> {

        @Override
        public InetAddress convert(String value, ConversionContext context) {
            return null;
        }
    }

    private static final class MyConfigurationContext implements ConfigurationContext {

        @Override
        public void addPropertySources(PropertySource... propertySources) {

        }

        @Override
        public List<PropertySource> getPropertySources() {
            return null;
        }

        @Override
        public PropertySource getPropertySource(String name) {
            return null;
        }

        @Override
        public <T> void addPropertyConverter(TypeLiteral<T> typeToConvert, PropertyConverter<T> propertyConverter) {

        }

        @Override
        public Map<TypeLiteral<?>, List<PropertyConverter<?>>> getPropertyConverters() {
            return null;
        }

        @Override
        public <T> List<PropertyConverter<T>> getPropertyConverters(TypeLiteral<T> type) {
            return null;
        }

        @Override
        public List<PropertyFilter> getPropertyFilters() {
            return null;
        }

        @Override
        public PropertyValueCombinationPolicy getPropertyValueCombinationPolicy() {
            return null;
        }

        @Override
        public ConfigurationContextBuilder toBuilder() {
            return null;
        }
    }

    private static final class MyConfiguration implements Configuration {

        @Override
        public String get(String key) {
            return null;
        }

        @Override
        public String getOrDefault(String key, String defaultValue) {
            return null;
        }

        @Override
        public <T> T getOrDefault(String key, Class<T> type, T defaultValue) {
            return null;
        }

        @Override
        public <T> T get(String key, Class<T> type) {
            return null;
        }

        @Override
        public <T> T get(String key, TypeLiteral<T> type) {
            return null;
        }

        @Override
        public <T> T getOrDefault(String key, TypeLiteral<T> type, T defaultValue) {
            return null;
        }

        @Override
        public Map<String, String> getProperties() {
            return null;
        }

        @Override
        public Configuration with(ConfigOperator operator) {
            return null;
        }

        @Override
        public <T> T query(ConfigQuery<T> query) {
            return null;
        }

        @Override
        public ConfigurationContext getContext() {
            return null;
        }
    }

}
