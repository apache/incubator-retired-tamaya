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
import org.apache.tamaya.Configuration;
import org.apache.tamaya.TypeLiteral;
import org.junit.Test;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ConversionContext}, created by atsticks on 20.08.16.
 */
public class ConversionContextTest {

    @Test
    public void getSetKey() throws Exception {
        ConversionContext ctx = new ConversionContext.Builder("getKey", TypeLiteral.of(String.class)).build();
        assertThat(ctx.getKey()).isEqualTo("getKey");
        ctx = new ConversionContext.Builder("getKey", TypeLiteral.of(String.class)).setKey("setKey").build();
        assertThat(ctx.getKey()).isEqualTo("setKey");
    }

    @Test
    public void getSetTargetType() throws Exception {
        ConversionContext ctx = new ConversionContext.Builder("getTargetType", TypeLiteral.of(String.class)).build();
        assertThat(ctx.getTargetType()).isEqualTo(TypeLiteral.of(String.class));
        ctx = new ConversionContext.Builder("setTargetType", TypeLiteral.of(String.class)).setTargetType(TypeLiteral.of(Integer.class)).build();
        assertThat(ctx.getTargetType()).isEqualTo(TypeLiteral.of(Integer.class));
    }

    @Test
    public void getSetAnnotatedElement() throws Exception {
        ConversionContext ctx = new ConversionContext.Builder("getAnnotatedElement", TypeLiteral.of(List.class)).build();
        assertThat(ctx.getAnnotatedElement()).isNull();
        ctx = new ConversionContext.Builder(TypeLiteral.of(List.class)).setAnnotatedElement(MY_ANNOTATED_ELEMENT).build();
        assertThat(ctx.getAnnotatedElement()).isEqualTo(MY_ANNOTATED_ELEMENT);
    }

    @Test
    public void testConfiguration() throws Exception {
        Configuration config = Configuration.EMPTY;
        ConversionContext ctx = new ConversionContext.Builder("testConfiguration", TypeLiteral.of(List.class))
                .setConfiguration(config).build();
        assertThat(ctx.getConfiguration()).isEqualTo(config);
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
        assertThat(ctx.getSupportedFormats().containsAll(readable)).isTrue();
        assertThat(ctx.getSupportedFormats().indexOf(readable.get(0))
                < ctx.getSupportedFormats().indexOf(readable.get(1))).isTrue();

        ctx = new ConversionContext.Builder(TypeLiteral.of(List.class)).build();
        assertThat(ctx.getSupportedFormats()).isEmpty();
        ctx.addSupportedFormats(MyConverter.class, writeable.get(0), writeable.get(1));
        assertThat(ctx.getSupportedFormats().containsAll(readable)).isTrue();
        assertThat(ctx.getSupportedFormats().indexOf(readable.get(0))
                < ctx.getSupportedFormats().indexOf(readable.get(1))).isTrue();
    }

    @Test
    public void testToString() throws Exception {
        ConversionContext ctx = new ConversionContext.Builder("toString", TypeLiteral.of(List.class))
                .addSupportedFormats(MyConverter.class, "0.0.0.0/nnn", "x.x.x.x/yyy").build();
        assertThat(ctx.toString()).isEqualTo("ConversionContext{configuration=null, key='toString', "
                + "targetType=TypeLiteral{type=interface java.util.List}, "
                + "annotatedElement=null, supportedFormats=[0.0.0.0/nnn (MyConverter), x.x.x.x/yyy (MyConverter)]}");
    }

    @Test
    public void testGetSetValues_Ellipse(){
        ConversionContext ctx = new ConversionContext.Builder("toString", TypeLiteral.of(List.class))
                .addSupportedFormats(MyConverter.class, "0.0.0.0/nnn", "x.x.x.x/yyy")
                .setValues(new PropertyValue("test", "value")).build();
        assertThat(ctx.getValues()).isNotNull().hasSize(1);
        assertThat("value").isEqualTo(ctx.getValues().get(0).getValue());
        assertThat("test").isEqualTo(ctx.getValues().get(0).getKey());
    }

    @Test
    public void testGetSetValues_List(){
        ConversionContext ctx = new ConversionContext.Builder("toString", TypeLiteral.of(List.class))
                .addSupportedFormats(MyConverter.class, "0.0.0.0/nnn", "x.x.x.x/yyy")
                .setValues(Collections.singletonList(new PropertyValue("test", "value"))).build();
        assertThat(ctx.getValues()).isNotNull().hasSize(1);
        assertThat("value").isEqualTo(ctx.getValues().get(0).getValue());
        assertThat("test").isEqualTo(ctx.getValues().get(0).getKey());
    }

    @Test
    public void testGetConfigurationContext(){
        ConversionContext ctx = new ConversionContext.Builder("toString", TypeLiteral.of(List.class))
                .addSupportedFormats(MyConverter.class, "0.0.0.0/nnn", "x.x.x.x/yyy")
                .setValues(PropertyValue.createObject().setValue("test", "value")).build();
        assertThat(ctx.getConfigurationContext()).isNotNull();
    }

    @Test
    public void testGetMeta(){
        ConversionContext ctx = new ConversionContext.Builder("test", TypeLiteral.of(List.class))
                .addSupportedFormats(MyConverter.class, "0.0.0.0/nnn", "x.x.x.x/yyy")
                .setValues(new PropertyValue("test", "value")
                .setMeta("meta1", "val1").setMeta("meta2", "val2")).build();
        assertThat(ctx.getMeta()).isNotNull().isNotEmpty().hasSize(2);
    }

    @Test
    public void testBuilderToString() {
        ConversionContext.Builder b = new ConversionContext.Builder("toString", TypeLiteral.of(List.class))
                .addSupportedFormats(MyConverter.class, "0.0.0.0/nnn", "x.x.x.x/yyy");
        assertThat(b.toString()).isNotNull().contains("targetType=TypeLiteral{type=interface java.util.List}",
                "supportedFormats=[0.0.0.0/nnn (MyConverter), x.x.x.x/yyy (MyConverter)]", "annotatedElement", "key='toString'", "Builder");
    }

    private static final AnnotatedElement MY_ANNOTATED_ELEMENT = new AnnotatedElement() {
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

}
