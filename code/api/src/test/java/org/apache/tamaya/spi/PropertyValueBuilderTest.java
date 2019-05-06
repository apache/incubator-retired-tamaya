///*
// * Licensed to the Apache Software Foundation (ASF) under one
// * or more contributor license agreements.  See the NOTICE file
// * distributed with this work for additional information
// * regarding copyright ownership.  The ASF licenses this file
// * to you under the Apache License, Version 2.0 (the
// * "License"); you may not use this file except in compliance
// * with the License.  You may obtain a copy of the License at
// *
// *   http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing,
// * software distributed under the License is distributed on an
// * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// * KIND, either express or implied.  See the License for the
// * specific language governing permissions and limitations
// * under the License.
// */
//package org.apache.tamaya.spi;
//
//import static org.assertj.core.api.Assertions.*;
//import static org.junit.Assert.assertEquals;
//
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.Map;
//
//import org.assertj.core.api.Assertions;
//import org.junit.Test;
//
//public class PropertyValueBuilderTest {
//
//    /*
//     * Tests for PropertyValueBuilder(String, String)
//     */
//
//    @Test(expected = NullPointerException.class)
//    public void constructorWithSingleParameterRequiresNonNullValue_String() {
//        new PropertyValueBuilder((String)null, (String)null);
//    }
//
//    /*
//     * Tests for PropertyValueBuilder(PropertyValue)
//     */
//
//    @Test(expected = NullPointerException.class)
//    public void constructorWithSingleParameterRequiresNonNullValue_PropertyValue() {
//        new PropertyValueBuilder((PropertyValue) null);
//    }
//
//    @Test
//    public void constructorWithSingleParameter_PropertyValue() {
//        assertEquals(PropertyValue.of("k", "v", "s"),
//                new PropertyValueBuilder(PropertyValue.of("k", "v", "s")).build());
//    }
//
//    /*
//     * Tests for setMeta(String, Object)
//     */
//
//
//    /*
//     * Tests für withMetaEntries(Map)
//     */
//
//    @Test(expected = NullPointerException.class)
//    public void addMetaEntriesRequiresNonNullParameter() {
//        new PropertyValueBuilder("a", "v").withMetaEntries(null);
//    }
//
//    @Test
//    public void testCreate1(){
//        new PropertyValueBuilder("k", "v");
//    }
//
//
//    @Test
//    public void testKey() throws Exception {
//        PropertyValueBuilder b = new PropertyValueBuilder("k", "v");
//        PropertyValue val = b.build();
//        Assertions.assertThat("k").isEqualTo(val.getKey());
//        Assertions.assertThat("v").isEqualTo(val.getValue());
//        Assertions.assertThat(val.getMeta().current("k")).isNull();
//    }
//
//    @Test
//    public void testSource() throws Exception {
//        PropertyValueBuilder b = new PropertyValueBuilder("k", "v").setSource("testSource");
//        PropertyValue val = b.build();
//        Assertions.assertThat("testSource").isEqualTo(val.getSource());
//
//        PropertyValueBuilder b2 = b.setSource("differentSource");
//        val = b2.build();
//        Assertions.assertThat("differentSource").isEqualTo(val.getSource());
//    }
//
//    @Test
//    public void testSetMetaEntries() throws Exception {
//        Map<String,String> meta = new HashMap<>();
//        meta.put("1","2");
//        meta.put("a", "b");
//        PropertyValue pv = new PropertyValueBuilder("k", "v")
//                .setMeta("k", "v2")
//                .setMeta(meta).build();
//        Assertions.assertThat(pv.getValue()).isEqualTo("v");
//        Assertions.assertThat(pv.getKey()).isEqualTo("k");
//        assertThat(pv.getMeta().current("k")).isNull();
//        Assertions.assertThat(pv.getMeta()).hasSize(2);
//        assertThat(pv.getMeta().current("1")).isEqualTo("2");
//        assertThat(pv.getMeta().current("a")).isEqualTo("b");
//    }
//
//    @Test
//    public void testGetValue1() throws Exception {
//        PropertyValue pv = PropertyValue.of("k", "v", "testGetValue");
//        Assertions.assertThat(pv.getValue()).isEqualTo("v");
//    }
//
//    @Test(expected = NullPointerException.class)
//    public void removeMetaEntryRequiresNonNullParameter() {
//        new PropertyValueBuilder("y", "v").removeMetaEntry(null);
//    }
//
//    @Test
//    public void testRemoveMetaEntry() throws Exception {
//        PropertyValue pv = new PropertyValueBuilder("k", "v")
//                .setSource("testGetKey")
//                .setMeta("k", "v2")
//                .setMeta("k2", "v22")
//                .removeMetaEntry("k").build();
//        assertThat(pv.getMeta().current("k2")).isEqualTo("v22");
//        assertThat(pv.getMeta().current("k")).isNull();
//    }
//
//    @Test
//    public void testGetMetaEntries() throws Exception {
//        Map<String,String> meta = new HashMap<>();
//        meta.put("1","2");
//        meta.put("a", "b");
//        PropertyValueBuilder b = new PropertyValueBuilder("k", "v")
//                .setSource("testGetKey")
//                .setMeta(meta);
//        PropertyValue pv = b.build();
//        assertThat(b.getMeta()).isEqualTo(meta);
//        Assertions.assertThat(pv.getMeta()).isEqualTo(meta);
//    }
//
//    @Test
//    public void testSetContextData() throws Exception {
//        PropertyValueBuilder b = new PropertyValueBuilder("k", "v").setSource("testSetContextData");
//        Map<String,String> context = new HashMap<>();
//        context.put("source", "testSetContextData");
//        context.put("ts", String.value(System.currentTimeMillis()));
//        context.put("y", "yValue");
//        b.setMeta(new HashMap<String, String>());
//        b.setMeta(context);
//        context.remove("y");
//        b.setMeta(context);
//        PropertyValue contextData = b.build();
//        Assertions.assertThat(context.size()).isEqualTo(contextData.getMeta().size());
//        Assertions.assertThat("testSetContextData").isEqualTo(contextData.getMeta("source"));
//        assertThat(contextData.getMeta("ts")).isNotNull();
//        assertThat(contextData.getMeta("y")).isNull();
//    }
//
//    @Test
//    public void testAddContextData() throws Exception {
//        PropertyValueBuilder b = new PropertyValueBuilder("k", "v");
//        b.setMeta("ts", System.currentTimeMillis());
//        b.setMeta("y", "yValue");
//        b.setMeta("y", "y2");
//        PropertyValue contextData = b.build();
//        Assertions.assertThat(2).isEqualTo(contextData.getMeta().size());
//        assertThat(contextData.getMeta("ts")).isNotNull();
//        Assertions.assertThat("y2").isEqualTo(contextData.getMeta("y"));
//    }
//
//    @Test
//    public void testMapKey() {
//        PropertyValueBuilder b = new PropertyValueBuilder("key", "value")
//                .setMeta("_keyAndThenSome", "mappedvalue")
//                .setMeta("somethingelse", "othervalue")
//                .mapKey("mappedkey");
//        PropertyValue pv = b.build();
//        Assertions.assertThat(pv.getKey()).isEqualTo("mappedkey");
//        Assertions.assertThat(pv.getValue()).isEqualTo("value");
//        Assertions.assertThat(pv.getMeta()).hasSize(2);
//        assertThat(pv.getMeta("_mappedkey.AndThenSome")).isEqualTo("mappedvalue");
//        assertThat(pv.getMeta("somethingelse")).isEqualTo("othervalue");
//    }
//
//    @Test
//    public void testToString(){
//        PropertyValueBuilder b = new PropertyValueBuilder("k", "v")
//                .setMeta("metak", "metav");
//        System.out.println(b.toString());
//        assertThat(b.toString()).isEqualTo("PropertyValueBuilder{key='k'value='v'listValue='[]', metaEntries={metak=metav}}");
//    }
//
//    @Test(expected = NullPointerException.class)
//    public void mapThreeParameterVariantRequiresNonNullValueForConfigParameter() {
//        PropertyValueBuilder.mapProperties(null, "a", Collections.EMPTY_MAP);
//    }
//
//    @Test
//    public void mapThreeParameterVariantRequiresNonNullValueForSource() {
//        PropertyValueBuilder.mapProperties(Collections.EMPTY_MAP, null, Collections.EMPTY_MAP);
//    }
//
//    @Test(expected = NullPointerException.class)
//    public void mapThreeParameterVariantRequiresNonNullValueForMetaData() {
//        PropertyValueBuilder.mapProperties(Collections.EMPTY_MAP, "s", null);
//    }
//
//}