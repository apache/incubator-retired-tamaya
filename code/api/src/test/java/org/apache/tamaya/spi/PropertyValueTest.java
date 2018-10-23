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
import java.util.List;
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
        PropertyValue.of("a", "b", "s").getMeta((String)null);
    }

    @Test(expected = NullPointerException.class)
    public void getMetaEntryRequiresNonNullValueForKeyClass() {
        PropertyValue.of("a", "b", "s").getMeta((Class)null);
    }

    @Test(expected = NullPointerException.class)
    public void testSetMetaEntriesRequiresNonNullParameter() {
        PropertyValue.create().setMeta(null);
    }

    @Test
    public void testSetMetaEntries() throws Exception {
        Map<String,Object> meta = new HashMap<>();
        meta.put("1","2");
        meta.put("a", "b");
        PropertyValue pv = PropertyValue.create()
                .setMeta("k", "v2")
                .setMeta(meta);
        assertThat(pv.getMeta().get("k")).isNull();
        Assertions.assertThat(pv.getMeta()).hasSize(2);
        assertThat(pv.getMeta().get("1")).isEqualTo("2");
        assertThat(pv.getMeta().get("a")).isEqualTo("b");
    }

    @Test(expected = NullPointerException.class)
    public void removeMetaEntryRequiresNonNullParameter() {
        PropertyValue.create().removeMeta((String)null);
    }

    @Test(expected = NullPointerException.class)
    public void removeMetaEntryRequiresNonNullParameterClass() {
        PropertyValue.create().removeMeta((Class)null);
    }


    @Test
    public void testRemoveMetaEntry() throws Exception {
        PropertyValue pv = PropertyValue.create("k")
                .setMeta("k", "v2")
                .setMeta("k2", "v22")
                .removeMeta("k");
        assertThat(pv.getMeta().get("k2")).isEqualTo("v22");
        assertThat(pv.getMeta().get("k")).isNull();
    }

    @Test
    public void testRemoveMetaEntryClass() throws Exception {
        PropertyValue pv = PropertyValue.create("k")
                .setMeta(String.class, "v2")
                .setMeta("k2", "v22")
                .removeMeta(String.class);
        assertThat(pv.getMeta().get("k2")).isEqualTo("v22");
        assertThat(pv.getMeta(String.class)).isNull();
    }

    @Test
    public void testGetMetaEntries() throws Exception {
        Map<String,Object> meta = new HashMap<>();
        meta.put("1","2");
        meta.put("a", "b");
        PropertyValue pv = PropertyValue.create("k")
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
        PropertyValue pv = PropertyValue.create("k")
                .setValue("v")
                .setMeta("k", "v2");
        Assertions.assertThat(pv.getValue()).isEqualTo("v");
        Assertions.assertThat(pv.getKey()).isEqualTo("k");
        assertThat(pv.getMeta().get("k")).isEqualTo("v2");
        assertThat((String)pv.getMeta("k")).isEqualTo("v2");
    }


    @Test(expected = NullPointerException.class)
    public void testInstantiateNoKey1() throws Exception {
        PropertyValue.create((String)null);
    }

    @Test(expected = NullPointerException.class)
    public void testInstantiateNoKey2() throws Exception {
        PropertyValue.of(null, "v", "testGetKey");
    }

    @Test
    public void testInstantiateNoValue2() throws Exception {
        PropertyValue.of("k", null, "testGetKey");
    }

    @Test
    public void testInstantiateNoSource2() throws Exception {
        PropertyValue.of("k", "v", null);
    }

    @Test(expected = NullPointerException.class)
    public void addMetaEntryRequiresNonNullParameterForKey() {
        PropertyValue.create("k").setMeta((String)null, "a");
    }

    @Test(expected = NullPointerException.class)
    public void addMetaEntryRequiresNonNullParameterForKeyClass() {
        PropertyValue.create("k").setMeta((Class)null, "a");
    }

    @Test(expected = NullPointerException.class)
    public void addMetaEntryRequiresNonNullParameterForValue() {
        PropertyValue.create("k").setMeta("a", null);
    }

    @Test
    public void setValueRequiresNullParameterForValue() {
        PropertyValue.create("k").setValue(null);
    }

    @Test
    public void create() {
        assertNotNull(PropertyValue.create());
        assertTrue(PropertyValue.create().isRoot());
        PropertyValue n = PropertyValue.create().getOrCreateChild("child");
        assertFalse(n.isRoot());
    }

    @Test
    public void create_String() {
        PropertyValue foo = PropertyValue.create("foo");
        assertNotNull(foo);
        assertEquals("foo", foo.getKey());
        PropertyValue n = PropertyValue.create("bar").getOrCreateChild("child");
        assertFalse(n.isRoot());
    }

    @Test
    public void getOrCreateChild() {
        PropertyValue root = PropertyValue.create("bar");
        assertTrue(root.getChildren().isEmpty());
        assertNotNull(root.getOrCreateChild("foo"));
        assertFalse(root.getChildren().isEmpty());
        assertFalse(root.getChildren("foo").isEmpty());
        assertTrue(root.getOrCreateChild("foo") == root.getOrCreateChild("foo"));
        assertTrue(root.getChildren("bar").isEmpty());
        assertTrue(root.getOrCreateChild("bar") == root.getOrCreateChild("bar"));
        assertFalse(root.getChildren("bar").isEmpty());
    }

    @Test
    public void getChild() {
        PropertyValue root = PropertyValue.create("bar");
        assertNull(root.getChild("foo"));
        root.getOrCreateChild("foo");
        assertNotNull(root.getChild("foo"));
    }

    @Test
    public void createChild_Indexed() {
        PropertyValue root = PropertyValue.create("a");
        assertNotNull(root.createChild("a", true));
        List<PropertyValue> nodes = root.getChildren("a");
        assertEquals(1, nodes.size());
        assertTrue(root.createChild("a").isIndexed());
        nodes = root.getChildren("a");
        assertEquals(2, nodes.size());
        assertFalse(root.createChild("b").isIndexed());
        assertTrue(root.createChild("b").isIndexed());
    }

    @Test
    public void addMeta() {
        PropertyValue root = PropertyValue.create();
        assertNotNull(root.setMeta("a"));
        root.setMeta("a", Integer.valueOf(3));
        assertEquals(Integer.valueOf(3), root.getMeta("a"));
    }

    @Test
    public void getKey() {
        PropertyValue root = PropertyValue.create("a");
        assertEquals("a", root.getKey());
    }

    @Test
    public void getQualifiedKey() {
        PropertyValue root = PropertyValue.create("a");
        assertEquals("a", root.getQualifiedKey());
        PropertyValue n = root.createChild("b");
        assertEquals("a.b", n.getQualifiedKey());
        PropertyValue added = n.createChild("c");
        assertEquals("a.b.c", added.getQualifiedKey());
        added = n.createChild("c");
        assertEquals("a.b.c[1]", added.getQualifiedKey());
        assertEquals("a.b.c[0]", n.getChildWithIndex("c",0).getQualifiedKey());
    }

    @Test
    public void isIndexed() {
        PropertyValue n = PropertyValue.create();
        assertFalse(n.isIndexed());
        assertFalse(n.createChild("a").isIndexed());
        assertFalse(n.createChild("b").isIndexed());
        assertFalse(n.createChild("c").isIndexed());
        assertTrue(n.createChild("c").isIndexed());
    }

    @Test
    public void isLeaf() {
        PropertyValue n = PropertyValue.create();
        assertTrue(n.isLeaf());
        n.createChild("b");
        assertFalse(n.isLeaf());
    }

    @Test
    public void getParent() {
        PropertyValue n = PropertyValue.create();
        assertNull(n.getParent());
        n = n.createChild("b");
        assertNotNull(n.getParent());
    }

    @Test
    public void getChildren_Filtered() {
        PropertyValue n = PropertyValue.create();
        n.createChild("a");
        n.createChild("b");
        n.createChild("c");
        n.createChild("c");
        List<PropertyValue> nodes = n.getChildren("a");
        assertNotNull(nodes);
        assertEquals(1, nodes.size());
        assertEquals("a", nodes.get(0).getKey());

        nodes = n.getChildren("c");
        assertEquals(2, nodes.size());
        assertEquals("c", nodes.get(0).getKey());
        assertEquals("c", nodes.get(1).getKey());
    }

    @Test
    public void getChildren() {
        PropertyValue n = PropertyValue.create();
        n.createChild("a");
        n.createChild("b");
        n.createChild("c");
        n.createChild("c");
        List<PropertyValue> nodes = n.getChildren();
        assertNotNull(nodes);
        assertEquals(4, nodes.size());
        assertEquals("a", nodes.get(0).getKey());
        assertEquals("b", nodes.get(1).getKey());
        assertEquals("c", nodes.get(2).getKey());
        assertEquals("c", nodes.get(3).getKey());
    }

    @Test
    public void asMap() {
        PropertyValue n = PropertyValue.create();
        n.createChild("a", "aVal");
        n.createChild("b").createChild("b2").createChild("b3", "b3Val");
        n.createChild("c", "cVal1");
        n.createChild("c", "cVal2");
        Map<String,String> map = n.asMap();
        System.out.println(map);
        assertEquals(4, map.size());
        assertEquals("aVal", map.get("a"));
        assertEquals("b3Val", map.get("b.b2.b3"));
        assertEquals("cVal1", map.get("c[0]"));
        assertEquals("cVal2", map.get("c[1]"));
    }

    @Test
    public void asString() {
        PropertyValue n = PropertyValue.create();
        n.createChild("a", "aVal");
        n.createChild("b").createChild("b2").createChild("b3", "b3Val");
        n.createChild("c", "cVal1");
        n.createChild("c", "cVal2");
        assertEquals("a = aVal\n" +
                "b.b2.b3 = b3Val\n" +
                "c[0] = cVal1\n" +
                "c[1] = cVal2\n", n.asString());
    }

    @Test
    public void equals() {
        assertEquals(PropertyValue.create(), PropertyValue.create());
        assertNotEquals(PropertyValue.create("a"), PropertyValue.create());
        assertEquals(PropertyValue.create("b"), PropertyValue.create("b"));
        PropertyValue root = PropertyValue.create();
        assertEquals(root, root);
    }

    @Test
    public void testToString() {
        PropertyValue n = PropertyValue.create();
        n.createChild("a", "aVal");
        n.createChild("b").createChild("b2").createChild("b3", "b3Val");
        n.createChild("c", "cVal1");
        n.createChild("c", "cVal2");
        assertEquals("PropertyValue{'', children='4'}", n.toString());
    }

}