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

import static org.assertj.core.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class PropertyValueBuilderTest {

    /*
     * Tests for PropertyValueBuilder(String)
     */

    @Test(expected = NullPointerException.class)
    public void constructorWithSingleParameterRequiresNonNullValue() {
        new PropertyValue(null);
    }

    /*
     * Tests for PropertyValueBuilder(String, String)
     */

    @Test(expected = NullPointerException.class)
    public void constructorWithTwoParametersRequiresNonNullValueForKey() {
        new PropertyValueBuilder(null, "s");
    }

    @Test(expected = NullPointerException.class)
    public void constructorWithTwoParametersRequiresNonNullValueForSource() {
        new PropertyValueBuilder("a", null);
    }

    /*
     * Tests for PropertyValueBuilder(String, String, String)
     */

    @Test(expected = NullPointerException.class)
    public void constructorWithThreeParametersRequiresNonNullValueForSource() {
        new PropertyValueBuilder("a", "b", null);
    }

    @Test(expected = NullPointerException.class)
    public void constructorWithThreeParametersRequiresNonNullValueForKey() {
        new PropertyValueBuilder(null, "b", "s");
    }

    /*
     * Tests for addMetaEntry(String, Object)
     */

    @Test(expected = NullPointerException.class)
    public void addMetaEntryRequiresNonNullParameterForKey() {
        new PropertyValueBuilder("a", "b", "c").addMetaEntry(null, "a");
    }

    @Test(expected = NullPointerException.class)
    public void addMetaEntryRequiresNonNullParameterForValue() {
        new PropertyValueBuilder("a", "b", "c").addMetaEntry("a", null);
    }

    @Test(expected = NullPointerException.class)
    public void setKeyRequiresNonNullParameterForKey() {
        new PropertyValueBuilder("a", "b", "s").setKey(null);
    }

    @Test(expected = NullPointerException.class)
    public void setKeyRequiresNonNullParameterForValue() {
        new PropertyValueBuilder("a", "b", "s").setValue(null);
    }
    
    /*
     * Tests für addMetaEntries(Map)
     */

    @Test(expected = NullPointerException.class)
    public void addMetaEntriesRequiresNonNullParameter() {
        new PropertyValueBuilder("a", "b", "s").addMetaEntries(null);
    }

    @Test
    public void testCreate1(){
        new PropertyValueBuilder("k");
    }

    @Test(expected = NullPointerException.class)
    public void testCreate1_Null(){
        new PropertyValueBuilder(null);
    }

    @Test
    public void testCreate2(){
        new PropertyValueBuilder("k", "source");
    }

    @Test(expected = NullPointerException.class)
    public void testCreate2_Null(){
        new PropertyValueBuilder("k", null);
    }

    @Test
    public void testKey() throws Exception {
        PropertyValueBuilder b = new PropertyValueBuilder("k", "testKey").setValue("v");
        PropertyValue val = b.build();
        assertThat("k").isEqualTo(val.getKey());
        assertThat("v").isEqualTo(val.getValue());
        assertThat(val.getMetaEntries().get("k")).isNull();
    }
    
    @Test
    public void testSetKey() {
        PropertyValueBuilder b = new PropertyValueBuilder("k", "testSetKey").setKey("key");
        PropertyValue val = b.build();
        assertThat("key").isEqualTo(val.getKey());
    }

    @Test
    public void testSource() throws Exception {
        PropertyValueBuilder b = new PropertyValueBuilder("k", "testSource").setValue("v");
        PropertyValue val = b.build();
        assertThat("testSource").isEqualTo(val.getSource());
        
        PropertyValueBuilder b2 = b.setSource("differentSource");
        val = b2.build();
        assertThat("differentSource").isEqualTo(val.getSource());
    }

    @Test(expected=NullPointerException.class)
    public void testKeyNullValue() throws Exception {
        new PropertyValueBuilder(null, "testKeyNullValue");
    }

    @Test
    public void testSetMetaEntries() throws Exception {
        Map<String,String> meta = new HashMap<>();
        meta.put("1","2");
        meta.put("a", "b");
        PropertyValue pv = PropertyValue.builder("k", "testGetKey")
                .setValue("v")
                .addMetaEntry("k", "v2")
                .setMetaEntries(meta).build();
        assertThat(pv.getValue()).isEqualTo("v");
        assertThat(pv.getKey()).isEqualTo("k");
        assertThat(pv.getMetaEntry("k")).isNull();
        assertThat(pv.getSource()).isEqualTo("testGetKey");
        assertThat(pv.getMetaEntries()).hasSize(2);
        assertThat(pv.getMetaEntry("1")).isEqualTo("2");
        assertThat(pv.getMetaEntry("a")).isEqualTo("b");
    }

    @Test
    public void testGetKey() throws Exception {
        PropertyValue pv = PropertyValue.builder("k", "testGetKey").setValue("v").build();
        assertThat(pv.getKey()).isEqualTo("k");
    }

    @Test
    public void testGetValue1() throws Exception {
        PropertyValue pv = PropertyValue.of("k", "v", "testGetValue");
        assertThat(pv.getValue()).isEqualTo("v");
    }

    @Test
    public void testGetValue2() throws Exception {
        PropertyValue pv = PropertyValue.builder("k", "testGetValue").setValue("v").build();
        assertThat(pv.getValue()).isEqualTo("v");
    }

    @Test(expected = NullPointerException.class)
    public void removeMetaEntryRequiresNonNullParameter() {
        new PropertyValueBuilder("y").removeMetaEntry(null);
    }

    @Test
    public void testRemoveMetaEntry() throws Exception {
        PropertyValue pv = PropertyValue.builder("k", "testGetKey")
                .setValue("v")
                .addMetaEntry("k", "v2")
                .addMetaEntry("k2", "v22")
                .removeMetaEntry("k").build();
        assertThat(pv.getMetaEntry("k2")).isEqualTo("v22");
        assertThat(pv.getMetaEntry("k")).isNull();
    }

    @Test(expected=NullPointerException.class)
    public void testSourceNullValue() throws Exception {
        new PropertyValueBuilder("k", null);
    }

    @Test
    public void testGetMetaEntries() throws Exception {
        Map<String,String> meta = new HashMap<>();
        meta.put("1","2");
        meta.put("a", "b");
        PropertyValueBuilder b = PropertyValue.builder("k", "testGetKey")
                .setValue("v")
                .setMetaEntries(meta);
        PropertyValue pv = b.build();
        assertThat(b.getMetaEntries()).isEqualTo(meta);
        assertThat(pv.getMetaEntries()).isEqualTo(meta);
    }

    @Test
    public void testSetContextData() throws Exception {
        PropertyValueBuilder b = new PropertyValueBuilder("k", "testSetContextData").setValue("v");
        Map<String,String> context = new HashMap<>();
        context.put("source", "testSetContextData");
        context.put("ts", String.valueOf(System.currentTimeMillis()));
        context.put("y", "yValue");
        b.setMetaEntries(new HashMap<String, String>());
        b.setMetaEntries(context);
        context.remove("y");
        b.setMetaEntries(context);
        PropertyValue contextData = b.build();
        assertThat(context.size()).isEqualTo(contextData.getMetaEntries().size());
        assertThat("testSetContextData").isEqualTo(contextData.getMetaEntry("source"));
        assertThat(contextData.getMetaEntry("ts")).isNotNull();
        assertThat(contextData.getMetaEntry("y")).isNull();
    }

    @Test
    public void testAddContextData() throws Exception {
        PropertyValueBuilder b = new PropertyValueBuilder("k", "testAddContextData").setValue("v");
        b.addMetaEntry("ts", System.currentTimeMillis());
        b.addMetaEntry("y", "yValue");
        b.addMetaEntry("y", "y2");
        PropertyValue contextData = b.build();
        assertThat(2).isEqualTo(contextData.getMetaEntries().size());
        assertThat(contextData.getMetaEntry("ts")).isNotNull();
        assertThat("y2").isEqualTo(contextData.getMetaEntry("y"));
    }
    
    @Test
    public void testMapKey() {
        PropertyValueBuilder b = new PropertyValueBuilder("key", "testMapKey")
                .setValue("value")
                .addMetaEntry("_keyAndThenSome", "mappedvalue")
                .addMetaEntry("somethingelse", "othervalue")
                .mapKey("mappedkey");
        PropertyValue pv = b.build();     
        assertThat(pv.getKey()).isEqualTo("mappedkey");
        assertThat(pv.getValue()).isEqualTo("value");
        assertThat(pv.getMetaEntries()).hasSize(2);
        assertThat(pv.getMetaEntry("_mappedkey.AndThenSome")).isEqualTo("mappedvalue");
        assertThat(pv.getMetaEntry("somethingelse")).isEqualTo("othervalue");
    }
    
    @Test
    public void testToString(){
        PropertyValueBuilder b = new PropertyValueBuilder("k")
                .setValue("v")
                .addMetaEntry("metak", "metav");
        assertThat(b.toString()).isEqualTo("PropertyValueBuilder{key='k'value='v', metaEntries={metak=metav}}");
    }

}