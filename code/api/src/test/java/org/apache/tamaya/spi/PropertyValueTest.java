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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("unchecked")
public class PropertyValueTest {

    @Test
    public void from(){

        ObjectValue val = ObjectValue.from(Arrays.asList());
        assertThat(val).isNotNull();
        val = ObjectValue.from(Arrays.asList(
                PropertyValue.createObject("o1").setValue("key", "value"),
                PropertyValue.createObject("o2").setValue("key2", "value2"))
        );
        assertThat(val).isNotNull();
        assertThat(val.getValue("o1")).isNotNull();
        assertThat(val.getValue("o2")).isNotNull();
        assertThat(val.getPropertyValue("o1").toObjectValue().getValue("key")).isEqualTo("value");
        assertThat(val.getPropertyValue("o2").toObjectValue().getValue("key2")).isEqualTo("value2");
        val = ObjectValue.from(Arrays.asList(
                PropertyValue.createList("o1").addValue("value"),
                new PropertyValue( "o2", "value2"))
        );
        assertThat(val).isNotNull();
        assertThat(val.getValue("o1")).isNotNull();
        assertThat(val.getValue("o2")).isNotNull();
        assertThat(val.getPropertyValue("o1").toListValue().getValue(0)).isEqualTo("value");
        assertThat(val.getPropertyValue("o2").getValue()).isEqualTo("value2");
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
        PropertyValue pv = new PropertyValue( "k", "v");
        assertThat(pv.getMeta()).isNotNull().isEmpty();
    }

    @Test
    public void testHashCode(){
        assertThat(new PropertyValue( "k", "v").hashCode()).isEqualTo(new PropertyValue( "k", "v").hashCode());
        assertThat(new PropertyValue( "k", "v").hashCode()).isNotSameAs(new PropertyValue( "k1", "v").hashCode());
        assertThat(new PropertyValue( "k", "v").hashCode()).isNotSameAs(new PropertyValue( "k", "v1").hashCode());
        assertThat(new PropertyValue( "k", "v").hashCode()).isNotSameAs(new PropertyValue( "k", "v").hashCode());
    }

    @Test
    public void testEquals(){
        assertThat(new PropertyValue( "k", "v")).isEqualTo(new PropertyValue( "k", "v"));
        assertThat(new PropertyValue( "k2", "v")).isNotSameAs(new PropertyValue( "k", "v"));
        assertThat(new PropertyValue( "k", "v")).isNotSameAs(new PropertyValue( "k", "v2"));
        assertThat(new PropertyValue( "k", "v")).isNotSameAs(new PropertyValue( "k", "v"));
    }
        
    @Test
    public void testGetKey() throws Exception {
        PropertyValue pv = new PropertyValue( "k", "v");
        assertThat(pv.getKey()).isEqualTo("k");
    }

    @Test
    public void testGetValue() throws Exception {
        PropertyValue pv = new PropertyValue("k", "v");
        assertThat(pv.getValue()).isEqualTo("v");
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
        new PropertyValue( null, "v");
    }

    @Test
    public void testInstantiateNoValue2() throws Exception {
        new PropertyValue("k", null);
    }

    @Test
    public void testInstantiateNoSource2() throws Exception {
        new PropertyValue("k", "v");
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
        assertThat(new PropertyValue("", null)).isNotNull();
        assertThat(PropertyValue.createObject()).isNotNull();
        assertThat(PropertyValue.createObject("")).isNotNull();
        assertThat(PropertyValue.createList()).isNotNull();
        assertThat(PropertyValue.createList("")).isNotNull();
    }

    @Test
    public void valueOf() {
        PropertyValue foo = new PropertyValue("foo", "bar");
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
        assertThat(root.getPropertyValue("foo")).isNotNull();
        assertThat(root.getValue("foo")).isEqualTo("null");
        assertThat(root.setValue("foo", "bar")).isNotNull();
        assertThat(root.getValue("foo")).isEqualTo("bar");
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
        ObjectValue n = root.setPropertyValue(child);
        assertThat("a.b").isEqualTo(child.getQualifiedKey());
        child.setValue("c", null);
        PropertyValue val = child.getPropertyValue("c");
        assertThat("a.b.c").isEqualTo(val.getQualifiedKey());
    }

    @Test
    public void isLeaf() {
        PropertyValue n = new PropertyValue("", "");
        assertThat(n.isLeaf()).isTrue();
        n = PropertyValue.createList("");
        assertThat(n.isLeaf()).isFalse();
    }

    @Test
    public void isImmutable() {
        PropertyValue n = new PropertyValue("", "");
        assertThat(n.isImmutable()).isFalse();
        n.immutable();
        assertThat(n.isImmutable()).isTrue();
        assertThat(n.mutable().isImmutable()).isFalse();
    }

    @Test
    public void isRoot() {
        PropertyValue n = new PropertyValue("", "");
        assertThat(n.isRoot()).isTrue();
        n = new PropertyValue("", "").setParent(n);
        assertThat(n.isRoot()).isFalse();
    }

    @Test(expected=IllegalStateException.class)
    public void checkImmutableChangeThrowsExceotion() {
        PropertyValue n = new PropertyValue("", "");
        n.immutable();
        n.setValue("jhgjg");
    }

    @Test
    public void checkMutable() {
        PropertyValue n = new PropertyValue("", "");
        n.immutable();
        n = n.mutable();
        n.setValue("jhgjg");
        assertThat("jhgjg").isEqualTo(n.getValue());
    }

    @Test
    public void getParent() {
        ObjectValue n = PropertyValue.createObject("");
        assertThat(n.getParent()).isNull();
        PropertyValue val = n.addObject("b");
        assertThat(n.getValue("b")).isNotNull();
        assertThat(val).isEqualTo(n.getPropertyValue("b"));
        assertThat(n.getPropertyValue("b").getParent()).isNotNull();
    }

    @Test
    public void size(){
        PropertyValue n = new PropertyValue("key", "");
        assertThat(0).isEqualTo(n.getSize());
        assertThat(n.iterator().hasNext()).isFalse();
    }

    @Test
    public void setValue() {
        PropertyValue n = new PropertyValue("key", "");
        assertThat("").isEqualTo(n.getValue());
        n.setValue("jhgjg");
        assertThat("jhgjg").isEqualTo(n.getValue());
    }

    @Test
    public void setKey() {
        PropertyValue n = new PropertyValue("key", "");
        assertThat("key").isEqualTo(n.getKey());
        n.setKey("jhgjg");
        assertThat("jhgjg").isEqualTo(n.getKey());
    }

    @Test
    public void toObjectValue() {
        PropertyValue n = new PropertyValue("key", "");
        assertThat(n.toObjectValue()).isNotNull();
    }

    @Test
    public void toListValue() {
        PropertyValue n = new PropertyValue("key", "");
        assertThat(n.toListValue()).isNotNull();
    }

//    @Test
//    public void getChildren_Filtered() {
//        PropertyValue n = PropertyValue.createObject();
//        n.setField("a");
//        n.setField("b");
//        n.setField("c");
//        n.setField("c");
//        List<PropertyValue> nodes = n.getPropertyValues("a");
//        assertNotNull(nodes);
//        assertEquals(1, nodes.size());
//        assertEquals("a", nodes.getValue(0).getKey());
//
//        nodes = n.getPropertyValues("c");
//        assertEquals(2, nodes.size());
//        assertEquals("c", nodes.getValue(0).getKey());
//        assertEquals("c", nodes.getValue(1).getKey());
//    }
//
//    @Test
//    public void getPropertyValues() {
//        PropertyValue n = PropertyValue.createObject();
//        n.setField("a");
//        n.setField("b");
//        n.setField("c");
//        n.setField("c");
//        List<PropertyValue> nodes = n.getPropertyValues();
//        assertNotNull(nodes);
//        assertEquals(4, nodes.size());
//        assertEquals("a", nodes.getValue(0).getKey());
//        assertEquals("b", nodes.getValue(1).getKey());
//        assertEquals("c", nodes.getValue(2).getKey());
//        assertEquals("c", nodes.getValue(3).getKey());
//    }

    @Test
    public void toMap() {
        ObjectValue n = PropertyValue.createObject("");
        n.setValue("a", "aVal");
        n.addObject("b").addObject("b2").setValue("b3", "b3Val");
        ListValue array = n.addList("c");
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
    public void toLocalMap() {
        ObjectValue n = PropertyValue.createObject("");
        n.setValue("a", "aVal");
        n.addObject("b").addObject("b2").setValue("b3", "b3Val");
        ListValue array = n.addList("c");
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
        assertThat("Object{size='3', values='{a=aVal, b.b2.b3=b3Val, c=cVal2}}").isEqualTo(n.toString());
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
        assertThat("Object{size='3', values='{a=aVal, b.b2.b3=b3Val, c=cVal1}}").isEqualTo(n.toString());
    }

}
