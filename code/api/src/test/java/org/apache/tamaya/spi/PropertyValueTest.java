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

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

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
    public void builder() throws Exception {
        PropertyValueBuilder b = PropertyValue.builder("a", "b");
        assertThat(b).isNotNull();
        assertThat("a").isEqualTo(b.key);
        assertThat("b").isEqualTo(b.source);
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
        assertThat(pv.getMeta()).hasSize(2);
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
    }

    @Test
    public void testGetMetaEntries2() throws Exception {
        PropertyValue pv = PropertyValue.of("k", "v", null);
        assertThat(pv.getMeta()).isNotNull().isEmpty();
    }

    @Test
    public void testMap() throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("a", "b");
        map.put("b", "c");
        Map<String, PropertyValue> result = PropertyValue.map(map, "source");
        assertThat(result).isNotNull().hasSize(map.size());
       for(PropertyValue pv:result.values()){
           assertThat("source").isEqualTo(pv.getMetaEntry("source"));
       }
        assertThat("b").isEqualTo(map.get("a"));
        assertThat("c").isEqualTo(map.get("b"));
    }

    @Test
    public void testMap_WithMeta() throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("a", "b");
        map.put("b", "c");
        Map<String, String> meta = new HashMap<>();
        meta.put("m1", "m1v");
        meta.put("m2", "m2v");
        Map<String, PropertyValue> result = PropertyValue.map(map, "source", meta);
        assertThat(result).isNotNull().hasSize(map.size());
        for(PropertyValue pv:result.values()){
            assertThat("source").isEqualTo(pv.getMetaEntry("source"));
            assertThat("m1v").isEqualTo(pv.getMeta("m1"));
            assertThat("m2v").isEqualTo(pv.getMeta("m2"));
        }
        assertThat("b").isEqualTo(map.get("a"));
        assertThat("c").isEqualTo(map.get("b"));
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
        assertThat(pv.getKey()).isEqualTo("k");
    }

    @Test
    public void testGetValue() throws Exception {
        PropertyValue pv = PropertyValue.of("k", "v", "testGetValue");
        assertThat(pv.getValue()).isEqualTo("v");
    }

    @Test
    public void testGetSource() throws Exception {
        PropertyValue pv = PropertyValue.of("k", "v", "testGetSource");
        assertThat(pv.getSource()).isEqualTo("testGetSource");
        pv = PropertyValue.of("k", "v", "testGetSource");
        assertThat(pv.getSource()).isEqualTo("testGetSource");
    }

    @Test
    public void testGetMetaEntry() throws Exception {
        PropertyValue pv = PropertyValue.createObject("k")
                .setMeta("k", "v2");
        assertThat(pv.getKey()).isEqualTo("k");
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
        assertThat(PropertyValue.createValue("", null)).isNotNull();
        assertThat(PropertyValue.createObject()).isNotNull();
        assertThat(PropertyValue.createObject("")).isNotNull();
        assertThat(PropertyValue.createList()).isNotNull();
        assertThat(PropertyValue.createList("")).isNotNull();
    }

    @Test
    public void valueOf() {
        PropertyValue foo = PropertyValue.createValue("foo", "bar");
        assertThat(foo).isNotNull();
        assertThat("foo").isEqualTo(foo.getKey());
        assertThat("bar").isEqualTo(foo.getValue());
    }

    @Test
    public void arrayOf() {
        ListValue foo = PropertyValue.createList("foo");
        assertThat(foo).isNotNull();
        assertThat("foo").isEqualTo(foo.getKey());
    }

    @Test
    public void objectOf() {
        ObjectValue root = PropertyValue.createObject("bar");
        assertThat(root.getSize() == 0).isTrue();
        assertThat(root.setValue("foo", null)).isNotNull();
        assertThat(root.getSize()==0).isFalse();
        assertThat(root.getValue("foo")).isNotNull();
        assertThat(root.getValue("foo").getValue()).isNull();
        assertThat(root.setValue("foo", "bar")).isNotNull();
        assertThat(root.getValue("foo").getValue()).isEqualTo("bar");
        assertThat(root.getSize()==1).isTrue();
    }

    @Test
    public void addMeta() {
        PropertyValue root = PropertyValue.createObject();
        root.setMeta("a", Integer.valueOf(3));
        assertThat(Integer.valueOf(3).toString()).isEqualTo(root.getMeta("a"));
    }

    @Test
    public void getKey() {
        PropertyValue root = PropertyValue.createObject("a");
        assertThat("a").isEqualTo(root.getKey());
    }

    @Test
    public void getQualifiedKey() {
        ObjectValue root = PropertyValue.createObject("a");
        assertThat("a").isEqualTo(root.getQualifiedKey());
        ObjectValue child = PropertyValue.createObject("b");
        ObjectValue n = root.set(child);
        assertThat("a.b").isEqualTo(child.getQualifiedKey());
        PropertyValue added = child.setValue("c", null);
        assertThat("a.b.c").isEqualTo(added.getQualifiedKey());
    }

    @Test
    public void isLeaf() {
        PropertyValue n = PropertyValue.createValue("", "");
        assertThat(n.isLeaf()).isTrue();
        n = PropertyValue.createList("");
        assertThat(n.isLeaf()).isFalse();
    }

    @Test
    public void isImmutable() {
        PropertyValue n = PropertyValue.createValue("", "");
        assertThat(n.isImmutable()).isFalse();
        n.immutable();
        assertThat(n.isImmutable()).isTrue();
        assertThat(n.mutable().isImmutable()).isFalse();
    }

    @Test
    public void isRoot() {
        PropertyValue n = PropertyValue.createValue("", "");
        assertThat(n.isRoot()).isTrue();
        n = PropertyValue.createValue("", "").setParent(n);
        assertThat(n.isRoot()).isFalse();
    }

    @Test(expected=IllegalStateException.class)
    public void checkImmutableChangeThrowsExceotion() {
        PropertyValue n = PropertyValue.createValue("", "");
        n.immutable();
        n.setValue("jhgjg");
    }

    @Test
    public void checkMutable() {
        PropertyValue n = PropertyValue.createValue("", "");
        n.immutable();
        n = n.mutable();
        n.setValue("jhgjg");
        assertThat("jhgjg").isEqualTo(n.getValue());
    }

    @Test
    public void getParent() {
        ObjectValue n = PropertyValue.createObject("");
        assertThat(n.getParent()).isNull();
        PropertyValue val = n.setObject("b");
        assertThat(n.getValue("b")).isNotNull();
        assertThat(val).isEqualTo(n.getValue("b"));
        assertThat(n.getValue("b").getParent()).isNotNull();
    }

    @Test
    public void size(){
        PropertyValue n = PropertyValue.createValue("key", "");
        assertThat(0).isEqualTo(n.getSize());
        assertThat(n.iterator().hasNext()).isFalse();
    }

    @Test
    public void setValue() {
        PropertyValue n = PropertyValue.createValue("key", "");
        assertThat("").isEqualTo(n.getValue());
        n.setValue("jhgjg");
        assertThat("jhgjg").isEqualTo(n.getValue());
    }

    @Test
    public void setKey() {
        PropertyValue n = PropertyValue.createValue("key", "");
        assertThat("key").isEqualTo(n.getKey());
        n.setKey("jhgjg");
        assertThat("jhgjg").isEqualTo(n.getKey());
    }

    @Test
    public void toBuilder() {
        PropertyValue n = PropertyValue.createValue("key", "");
        assertThat(n.toBuilder()).isNotNull();
    }

    @Test
    public void toPropertyValue() {
        PropertyValue n = PropertyValue.createValue("key", "");
        assertThat(n == n.toPropertyValue()).isTrue();
    }

    @Test
    public void toObjectValue() {
        PropertyValue n = PropertyValue.createValue("key", "");
        assertThat(n.toObjectValue()).isNotNull();
    }

    @Test
    public void toListValue() {
        PropertyValue n = PropertyValue.createValue("key", "");
        assertThat(n.toListValue()).isNotNull();
    }

//    @Test
//    public void getChildren_Filtered() {
//        PropertyValue n = PropertyValue.createObject();
//        n.setField("a");
//        n.setField("b");
//        n.setField("c");
//        n.setField("c");
//        List<PropertyValue> nodes = n.getValues("a");
//        assertNotNull(nodes);
//        assertEquals(1, nodes.size());
//        assertEquals("a", nodes.getValue(0).getKey());
//
//        nodes = n.getValues("c");
//        assertEquals(2, nodes.size());
//        assertEquals("c", nodes.getValue(0).getKey());
//        assertEquals("c", nodes.getValue(1).getKey());
//    }
//
//    @Test
//    public void getValues() {
//        PropertyValue n = PropertyValue.createObject();
//        n.setField("a");
//        n.setField("b");
//        n.setField("c");
//        n.setField("c");
//        List<PropertyValue> nodes = n.getValues();
//        assertNotNull(nodes);
//        assertEquals(4, nodes.size());
//        assertEquals("a", nodes.getValue(0).getKey());
//        assertEquals("b", nodes.getValue(1).getKey());
//        assertEquals("c", nodes.getValue(2).getKey());
//        assertEquals("c", nodes.getValue(3).getKey());
//    }

    @Test
    public void asMap() {
        ObjectValue n = PropertyValue.createObject("");
        n.setValue("a", "aVal");
        n.setObject("b").setObject("b2").setValue("b3", "b3Val");
        ListValue array = n.setList("c");
        array.addValue("cVal1");
        array.addValue("cVal2");
        Map<String,String> map = n.toMap();
        System.out.println(map);
        assertThat(map).hasSize(4);
        assertThat("aVal").isEqualTo(map.get("a"));
        assertThat("b3Val").isEqualTo(map.get("b.b2.b3"));
        assertThat("cVal1").isEqualTo(map.get("c[0]"));
        assertThat("cVal2").isEqualTo(map.get("c[1]"));
    }

    @Test
    public void asString() {
        ObjectValue n = PropertyValue.createObject();
        n.setValue("a", "aVal");
        n.setValue("b.b2.b3", "b3Val");
        n.setValue("c", "cVal2");
        assertThat("a = aVal\n" +
                "b.b2.b3 = b3Val\n" +
                "c = cVal2\n").isEqualTo(n.asString());
    }

    @Test
    public void equals() {
        assertThat(PropertyValue.createObject()).isEqualTo(PropertyValue.createObject());
        assertThat(PropertyValue.createObject("a")).isNotEqualTo(PropertyValue.createObject());
        assertThat(PropertyValue.createObject("")).isEqualTo(PropertyValue.createObject());
        PropertyValue root = PropertyValue.createObject();
        assertThat(root).isEqualTo(root);
    }

    @Test
    public void testToString() {
        ObjectValue n = PropertyValue.createObject("");
        n.setValue("a", "aVal");
        n.setValue("b.b2.b3", "b3Val");
        n.setValue("c", "cVal1");
        assertThat("PropertyValue[MAP]{'', size='3'}").isEqualTo(n.toString());
    }

}
