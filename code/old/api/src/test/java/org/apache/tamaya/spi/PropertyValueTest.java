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

import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

@SuppressWarnings("unchecked")
public class PropertyValueTest {

    @Test(expected = NullPointerException.class)
    public void mapThreeParameterVariantRequiresNonNullValueForConfigParameter() {
        PropertyValue.map(null, "a", Collections.EMPTY_MAP);
    }

    @Test(expected = NullPointerException.class)
    public void mapThreeParameterVariantRequiresNonNullValueForSource() {
        PropertyValue.map(Collections.EMPTY_MAP, null, Collections.EMPTY_MAP);
    }

    @Test(expected = NullPointerException.class)
    public void mapThreeParameterVariantRequiresNonNullValueForMetaData() {
        PropertyValue.map(Collections.EMPTY_MAP, "s", null);
    }

    @Test(expected = NullPointerException.class)
    public void ofDoesNotAcceptNullAsKey() throws Exception {
        PropertyValue.of(null, "b", "source");
    }

    @Test(expected = NullPointerException.class)
    public void ofDoesNotAcceptNullAsSource() throws Exception {
        PropertyValue.of("a", "b", null);
    }

    @Test
    public void testOf(){
        assertNotNull(PropertyValue.of("k", "v", "testGetKey"));
    }

    @Test(expected = NullPointerException.class)
    public void getMetaEntryRequiresNonNullValueForKey() {
        PropertyValue.of("a", "b", "s").getMetaEntry(null);
    }

    @Test(expected = NullPointerException.class)
    public void testSetMetaEntriesRequiresNonNullParameter() {
        new PropertyValueBuilder("a").setMetaEntries(null);
    }

    @Test
    public void testHashCode(){
        assertEquals(PropertyValue.of("k", "v", "testGetKey").hashCode(),
                PropertyValue.of("k", "v", "testGetKey").hashCode());
        assertNotSame(PropertyValue.of("k", "v", "testGetKey").hashCode(),
                PropertyValue.of("k1", "v", "testGetKey").hashCode());
        assertNotSame(PropertyValue.of("k", "v", "testGetKey").hashCode(),
                PropertyValue.of("k", "v1", "testGetKey").hashCode());
        assertNotSame(PropertyValue.of("k", "v", "1").hashCode(),
                PropertyValue.of("k", "v", "2").hashCode());
    }

    @Test
    public void testEquals(){
        assertEquals(PropertyValue.of("k", "v", "testEquals"),
                PropertyValue.of("k", "v", "testEquals"));
        assertNotSame(PropertyValue.of("k2", "v", "testEquals"),
                PropertyValue.of("k", "v", "testEquals"));
        assertNotSame(PropertyValue.of("k", "v", "testEquals"),
                PropertyValue.of("k", "v2", "testEquals"));
        assertNotSame(PropertyValue.of("k", "v", "testEquals"),
                PropertyValue.of("k", "v", "testEquals2"));
    }

    @Test
    public void testBuilder(){
        assertNotNull(PropertyValue.builder("k", "testGetKey"));
        assertEquals(PropertyValue.of("k", "v", "testEquals"),
                PropertyValue.builder("k", "testEquals").setValue("v").build());
    }

    @Test
    public void testToBuilder(){
        assertNotNull(PropertyValue.of("k", "v", "testGetKey").toBuilder());
        // round-trip
        PropertyValue val = PropertyValue.of("k", "v", "testGetKey");
        assertEquals(val,
                val.toBuilder().build());
    }

    @Test
    public void testGetKey() throws Exception {
        PropertyValue pv = PropertyValue.of("k", "v", "testGetKey");
        assertEquals("k", pv.getKey());
    }

    @Test
    public void testGetValue() throws Exception {
        PropertyValue pv = PropertyValue.of("k", "v", "testGetValue");
        assertEquals("v", pv.getValue());
    }

    @Test
    public void testGetSource() throws Exception {
        PropertyValue pv = PropertyValue.of("k", "v", "testGetSource");
        assertEquals("testGetSource", pv.getSource());
        pv = PropertyValue.of("k", "v", "testGetSource");
        assertEquals("testGetSource", pv.getSource());
    }

    @Test
    public void testGetMetaEntry() throws Exception {
        PropertyValue pv = PropertyValue.builder("k", "testGetMetaEntry").setValue("v")
                .addMetaEntry("k", "v2").build();
        assertEquals("v", pv.getValue());
        assertEquals("k", pv.getKey());
        assertEquals("v2", pv.getMetaEntry("k"));
        assertEquals("testGetMetaEntry", pv.getSource());
    }

    @Test
    public void testGetMetaEntries() throws Exception {
        PropertyValue pv = PropertyValue.of("k", "v", "testGetMetaEntries");
        assertNotNull(pv.getMetaEntries());
        assertTrue(pv.getMetaEntries().isEmpty());
    }

    @Test
    public void testMap() throws Exception {
        Map<String,String> map = new HashMap<>();
        map.put("a", "1");
        map.put("b", "2");
        Map<String,PropertyValue> result = PropertyValue.map(map, "source1");
        assertNotNull(result);
        assertEquals(map.size(), result.size());

        for (Map.Entry<String,String>en:map.entrySet()) {
            PropertyValue val = result.get(en.getKey());
            assertNotNull(val);
            assertEquals(val.getKey(), en.getKey());
            assertEquals(val.getValue(), en.getValue());
            assertEquals(val.getSource(), "source1");
            assertTrue(val.getMetaEntries().isEmpty());
        }
    }

    @Test
    public void testMapWithMetadata() throws Exception {
        Map<String,String> map = new HashMap<>();
        map.put("a", "1");
        map.put("b", "2");
        Map<String,String> meta = new HashMap<>();
        map.put("m1", "n1");
        map.put("m2", "n2");
        Map<String,PropertyValue> result = PropertyValue.map(map, "source1", meta);
        assertNotNull(result);
        assertEquals(map.size(), result.size());
        for(Map.Entry<String,String>en:map.entrySet()){
            PropertyValue val = result.get(en.getKey());
            assertNotNull(val);
            assertEquals(val.getKey(), en.getKey());
            assertEquals(val.getValue(), en.getValue());
            assertEquals(val.getSource(), "source1");
            assertEquals(val.getMetaEntries(), meta);
        }
    }

    @Test(expected = NullPointerException.class)
    public void testInstantiateNoKey1() throws Exception {
        PropertyValue.builder(null, "testGetKey").setValue("v").build();
    }

    @Test(expected = NullPointerException.class)
    public void testInstantiateNoKey2() throws Exception {
        PropertyValue.of(null, "v", "testGetKey");
    }

    @Test
    public void testInstantiateNoValue1() throws Exception {
        PropertyValue.builder("k", "testGetKey").build();
    }

    @Test
    public void testInstantiateNoValue2() throws Exception {
        PropertyValue.of("k", null, "testGetKey");
    }

    @Test(expected = NullPointerException.class)
    public void testInstantiateNoSource1() throws Exception {
        PropertyValue.builder("k", null).setValue("v").build();
    }

    @Test(expected = NullPointerException.class)
    public void testInstantiateNoSource2() throws Exception {
        PropertyValue.of("k", "v", null);
    }

    @Test(expected = NullPointerException.class)
    public void testGetMetaEntry_Null() throws Exception {
        PropertyValue.of("k", "v", "src").getMetaEntry(null);
    }

    @Test(expected = NullPointerException.class)
    public void builderMethodThreeParameterVariantRequiresNonNullValueAsKey() {
        PropertyValue.builder(null, "b", "s");
    }

    @Test(expected = NullPointerException.class)
    public void builderMethodThreeParameterVariantRequiresNonNullValueAsSource() {
        PropertyValue.builder("A", "b", null);
    }

    @Test(expected = NullPointerException.class)
    public void builderMethodThreeParameterVariantRequiresNonNullValueAsValue() {
        PropertyValue.builder("A", null, "s");
    }


    @Test(expected = NullPointerException.class)
    public void builderMethodTwoParameterVariantRequiresNonNullValueAsSource() {
        PropertyValue.builder(null, "a");
    }

    @Test(expected = NullPointerException.class)
    public void builderMethodTwoParameterVariantRequiresNonNullValueAsValue() {
        PropertyValue.builder("A", null);
    }

}