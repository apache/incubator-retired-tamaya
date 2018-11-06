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

import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@SuppressWarnings("unchecked")
public class PropertyValueTest {

    @Test(expected = NullPointerException.class)
    public void ofDoesNotAcceptNullAsKey() throws Exception {
        PropertyValue.of(null, "b", "source");
    }

    @Test
    public void ofDoesAcceptNullAsSource() throws Exception {
        PropertyValue.of("a", "b", null);
    }

    @Test
    public void testOf(){
        assertThat(PropertyValue.of("k", "v", "testGetKey")).isNotNull();
    }

    @Test(expected = NullPointerException.class)
    public void getMetaEntryRequiresNonNullValueForKey() {
        PropertyValue.of("a", "b", "s").getMeta(null);
    }

    @Test(expected = NullPointerException.class)
    public void testSetMetaEntriesRequiresNonNullParameter() {
        PropertyValue.createObject().setMeta(null);
    }

    @Test
    public void testSetMetaEntries() throws Exception {
        Map<String,String> meta = new HashMap<>();
        meta.put("1","2");
        meta.put("a", "b");
        PropertyValue pv = PropertyValue.createObject()
                .setMeta("k", "v2")
                .setMeta(meta);
        assertThat(pv.getMeta().get("k")).isNull();
        Assertions.assertThat(pv.getMeta()).hasSize(2);
        assertThat(pv.getMeta().get("1")).isEqualTo("2");
        assertThat(pv.getMeta().get("a")).isEqualTo("b");
    }

    @Test(expected = NullPointerException.class)
    public void removeMetaEntryRequiresNonNullParameter() {
        PropertyValue.createObject().removeMeta(null);
    }


    @Test
    public void testRemoveMetaEntry() throws Exception {
        PropertyValue pv = PropertyValue.createObject("k")
                .setMeta("k", "v2")
                .setMeta("k2", "v22")
                .removeMeta("k");
        assertThat(pv.getMeta().get("k2")).isEqualTo("v22");
        assertThat(pv.getMeta().get("k")).isNull();
    }

    @Test
    public void testRemoveMetaEntryClass() throws Exception {
        PropertyValue pv = PropertyValue.createObject("k")
                .setMeta("k1", "v2")
                .setMeta("k2", "v22")
                .removeMeta("k1");
        assertThat(pv.getMeta().get("k2")).isEqualTo("v22");
        assertThat(pv.getMeta().get("k1")).isNull();
    }

    @Test
    public void testGetMetaEntries() throws Exception {
        Map<String,String> meta = new HashMap<>();
        meta.put("1","2");
        meta.put("a", "b");
        PropertyValue pv = PropertyValue.createObject("k")
                .setMeta(meta);
        assertThat(pv.getMeta()).isEqualTo(meta);
        Assertions.assertThat(pv.getMeta()).isEqualTo(meta);
    }

    @Test
    public void testGetMetaEntries2() throws Exception {
        PropertyValue pv = PropertyValue.of("k", "v", null);
        Assertions.assertThat(pv.getMeta()).isNotNull();
        Assertions.assertThat(pv.getMeta().isEmpty()).isTrue();
    }

    @Test
    public void testHashCode(){
        assertThat(PropertyValue.of("k", "v", "testGetKey").hashCode()).isEqualTo(PropertyValue.of("k", "v", "testGetKey").hashCode());
        assertThat(PropertyValue.of("k", "v", "testGetKey").hashCode()).isNotSameAs(PropertyValue.of("k1", "v", "testGetKey").hashCode());
        assertThat(PropertyValue.of("k", "v", "testGetKey").hashCode()).isNotSameAs(PropertyValue.of("k", "v1", "testGetKey").hashCode());
        assertThat(PropertyValue.of("k", "v", "1").hashCode()).isNotSameAs(PropertyValue.of("k", "v", "2").hashCode());
    }

    @Test
    public void testEquals(){
        assertThat(PropertyValue.of("k", "v", "testEquals")).isEqualTo(PropertyValue.of("k", "v", "testEquals"));
        assertThat(PropertyValue.of("k2", "v", "testEquals")).isNotSameAs(PropertyValue.of("k", "v", "testEquals"));
        assertThat(PropertyValue.of("k", "v", "testEquals")).isNotSameAs(PropertyValue.of("k", "v2", "testEquals"));
        assertThat(PropertyValue.of("k", "v", "testEquals")).isNotSameAs(PropertyValue.of("k", "v", "testEquals2"));
    }
        
    @Test
    public void testGetKey() throws Exception {
        PropertyValue pv = PropertyValue.of("k", "v", "testGetKey");
        Assertions.assertThat(pv.getKey()).isEqualTo("k");
    }

    @Test
    public void testGetValue() throws Exception {
        PropertyValue pv = PropertyValue.of("k", "v", "testGetValue");
        Assertions.assertThat(pv.getValue()).isEqualTo("v");
    }

    @Test
    public void testGetSource() throws Exception {
        PropertyValue pv = PropertyValue.of("k", "v", "testGetSource");
        Assertions.assertThat(pv.getSource()).isEqualTo("testGetSource");
        pv = PropertyValue.of("k", "v", "testGetSource");
        Assertions.assertThat(pv.getSource()).isEqualTo("testGetSource");
    }

    @Test
    public void testGetMetaEntry() throws Exception {
        PropertyValue pv = PropertyValue.createObject("k")
                .setMeta("k", "v2");
        Assertions.assertThat(pv.getKey()).isEqualTo("k");
        assertThat(pv.getMeta().get("k")).isEqualTo("v2");
    }


    @Test(expected = NullPointerException.class)
    public void testInstantiateNoKey1() throws Exception {
        PropertyValue.createObject((String)null);
    }

    @Test(expected = NullPointerException.class)
    public void testInstantiateNoKey2() throws Exception {
        PropertyValue.createValue(null, "v");
    }

    @Test
    public void testInstantiateNoValue2() throws Exception {
        PropertyValue.createValue("k", null);
    }

    @Test
    public void testInstantiateNoSource2() throws Exception {
        PropertyValue.createValue("k", "v");
    }

    @Test(expected = NullPointerException.class)
    public void addMetaEntryRequiresNonNullParameterForKey() {
        PropertyValue.createObject("k").setMeta(null, "a");
    }

    @Test(expected = NullPointerException.class)
    public void addMetaEntryRequiresNonNullParameterForValue() {
        PropertyValue.createObject("k").setMeta("a", null);
    }

    @Test
    public void newXXX() {
        assertNotNull(PropertyValue.createValue("", null));
        assertNotNull(PropertyValue.createObject());
        assertNotNull(PropertyValue.createObject(""));
        assertNotNull(PropertyValue.createList());
        assertNotNull(PropertyValue.createList(""));
    }

    @Test
    public void valueOf() {
        PropertyValue foo = PropertyValue.createValue("foo", "bar");
        assertNotNull(foo);
        assertEquals("foo", foo.getKey());
        assertEquals("bar", foo.getValue());
    }

    @Test
    public void arrayOf() {
        ListValue foo = PropertyValue.createList("foo");
        assertNotNull(foo);
        assertEquals("foo", foo.getKey());
    }

    @Test
    public void objectOf() {
        ObjectValue root = PropertyValue.createObject("bar");
        assertTrue(root.getSize() == 0);
        assertNotNull(root.setField("foo", null));
        assertFalse(root.getSize()==0);
        assertNotNull(root.getField("foo"));
        assertNull(root.getField("foo").getValue());
        assertNotNull(root.setField("foo", "bar"));
        assertEquals(root.getField("foo").getValue(), "bar");
        assertTrue(root.getSize()==1);
    }

    @Test
    public void addMeta() {
        PropertyValue root = PropertyValue.createObject();
        root.setMeta("a", Integer.valueOf(3));
        assertEquals(Integer.valueOf(3).toString(), root.getMeta("a"));
    }

    @Test
    public void getKey() {
        PropertyValue root = PropertyValue.createObject("a");
        assertEquals("a", root.getKey());
    }

    @Test
    public void getQualifiedKey() {
        ObjectValue root = PropertyValue.createObject("a");
        assertEquals("a", root.getQualifiedKey());
        ObjectValue child = PropertyValue.createObject("b");
        ObjectValue n = root.set(child);
        assertEquals("a.b", child.getQualifiedKey());
        PropertyValue added = child.setField("c", null);
        assertEquals("a.b.c", added.getQualifiedKey());
    }

    @Test
    public void isLeaf() {
        PropertyValue n = PropertyValue.createValue("", "");
        assertTrue(n.isLeaf());
        n = PropertyValue.createList("");
        assertFalse(n.isLeaf());
    }

    @Test
    public void getParent() {
        ObjectValue n = PropertyValue.createObject("");
        assertNull(n.getParent());
        n.setFieldObject("b");
        assertNotNull(n.getField("b"));
        assertNotNull(n.getField("b").getParent());
    }

//    @Test
//    public void getChildren_Filtered() {
//        PropertyValue n = PropertyValue.createObject();
//        n.setField("a");
//        n.setField("b");
//        n.setField("c");
//        n.setField("c");
//        List<PropertyValue> nodes = n.getList("a");
//        assertNotNull(nodes);
//        assertEquals(1, nodes.size());
//        assertEquals("a", nodes.getField(0).getKey());
//
//        nodes = n.getList("c");
//        assertEquals(2, nodes.size());
//        assertEquals("c", nodes.getField(0).getKey());
//        assertEquals("c", nodes.getField(1).getKey());
//    }
//
//    @Test
//    public void getList() {
//        PropertyValue n = PropertyValue.createObject();
//        n.setField("a");
//        n.setField("b");
//        n.setField("c");
//        n.setField("c");
//        List<PropertyValue> nodes = n.getList();
//        assertNotNull(nodes);
//        assertEquals(4, nodes.size());
//        assertEquals("a", nodes.getField(0).getKey());
//        assertEquals("b", nodes.getField(1).getKey());
//        assertEquals("c", nodes.getField(2).getKey());
//        assertEquals("c", nodes.getField(3).getKey());
//    }

    @Test
    public void asMap() {
        ObjectValue n = PropertyValue.createObject("");
        n.setField("a", "aVal");
        n.setFieldObject("b").setFieldObject("b2").setField("b3", "b3Val");
        ListValue array = n.setFieldList("c");
        array.addValue("cVal1");
        array.addValue("cVal2");
        Map<String,String> map = n.toMap();
        System.out.println(map);
        assertEquals(4, map.size());
        assertEquals("aVal", map.get("a"));
        assertEquals("b3Val", map.get("b.b2.b3"));
        assertEquals("cVal1", map.get("c[0]"));
        assertEquals("cVal2", map.get("c[1]"));
    }

    @Test
    public void asString() {
        ObjectValue n = PropertyValue.createObject();
        n.setField("a", "aVal");
        n.setField("b.b2.b3", "b3Val");
        n.setField("c", "cVal2");
        assertEquals("a = aVal\n" +
                "b.b2.b3 = b3Val\n" +
                "c = cVal2\n", n.asString());
    }

    @Test
    public void equals() {
        assertEquals(PropertyValue.createObject(), PropertyValue.createObject());
        assertNotEquals(PropertyValue.createObject("a"), PropertyValue.createObject());
        assertEquals(PropertyValue.createObject(""), PropertyValue.createObject());
        PropertyValue root = PropertyValue.createObject();
        assertEquals(root, root);
    }

    @Test
    public void testToString() {
        ObjectValue n = PropertyValue.createObject("");
        n.setField("a", "aVal");
        n.setField("b.b2.b3", "b3Val");
        n.setFieldList("c").addValue("cVal1");
        assertEquals("PropertyValue[OBJECT]{'', size='3'}", n.toString());
    }

}