/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tamaya.spi;

import org.junit.Test;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Tests for {@link ObjectValue}.
 */
public class ObjectValueTest {

    @Test
    public void getCreation() {
        ObjectValue ov = PropertyValue.createObject();
        assertNotNull(ov);
        ov = PropertyValue.createObject("k");
        assertNotNull(ov);
        assertEquals("k", ov.getKey());
    }

    @Test
    public void getValueType() {
        assertEquals(PropertyValue.ValueType.ARRAY, PropertyValue.createList().getValueType());
    }

    @Test
    public void getIndex() {
        ObjectValue ov = PropertyValue.createObject();
        PropertyValue val = PropertyValue.createValue("k", "v");
        ov.set(val);
        PropertyValue val2 = PropertyValue.createValue("k2", "v2");
        ov.set(val2);
        assertEquals(val, ov.getValue(val.getKey()));
        assertEquals(val2, ov.getValue(val2.getKey()));
        assertNull(ov.getValue("foo"));
    }

    @Test
    public void getSize() {
        ObjectValue ov = PropertyValue.createObject();
        assertEquals(0, ov.getSize());
        PropertyValue val = PropertyValue.createValue("k", "v");
        ov.set(val);
        assertEquals(1, ov.getSize());
        PropertyValue val2 = PropertyValue.createValue("k2", "v");
        ov.set(val2);
        assertEquals(2, ov.getSize());
        PropertyValue val3 = PropertyValue.createValue("k2", "v");
        ov.set(val3);
        assertEquals(2, ov.getSize());
    }

    @Test
    public void getValue() {
        ObjectValue ov = PropertyValue.createObject();
        PropertyValue val = PropertyValue.createValue("k1", "v");
        ov.set(val);
        PropertyValue val2 = PropertyValue.createValue("k2", "v");
        ov.set(val2);
        assertNotNull(ov.getValues());
        assertEquals(2, ov.getValues().size());
        assertEquals(val, ov.getValue("k1"));
        assertEquals(val2, ov.getValue("k2"));
        ov.set(val2);
        assertNotNull(ov.getValues());
        assertEquals(2, ov.getValues().size());
        assertEquals(val, ov.getValue("k1"));
        assertEquals(val2, ov.getValue("k2"));
    }

    @Test
    public void getOrSetValue() {
        ObjectValue ov = PropertyValue.createObject();
        PropertyValue val = PropertyValue.createValue("k1", "v");
        ov.set(val);
        PropertyValue val2 = ov.getOrSetValue("k2",
                () -> PropertyValue.createValue("foo", "bar"));
        PropertyValue pv = ov.getOrSetValue("foo",  () -> PropertyValue.createValue("foo", "bar"));
        assertNotNull(pv);
        assertEquals(3, ov.getValues().size());
        assertEquals(val, ov.getValue("k1"));
        assertEquals(val2, ov.getValue("k2"));
        assertEquals(pv, ov.getValue("foo"));
    }

    @Test
    public void setValues_Map() {
        ObjectValue ov = PropertyValue.createObject();
        Map map = new HashMap<>();
        map.put("k1", "v");
        map.put("k2.k3", "v2");
        map.put("foo", "bar");
        ov.setValues(map);
        PropertyValue pv = ov.getValue("foo");
        assertNotNull(pv);
        assertEquals("foo", pv.getKey());
        assertEquals("bar", pv.getValue());
    }

    @Test
    public void iterator() {
        ObjectValue ov = PropertyValue.createObject();
        PropertyValue val = PropertyValue.createValue("k1", "v");
        PropertyValue val2 = PropertyValue.createValue("k2", "v");
        ov.set(val);
        ov.set(val2);
        Iterator iter = ov.iterator();
        assertNotNull(iter);
        assertTrue(iter.hasNext());
        assertEquals(val, iter.next());
        assertTrue(iter.hasNext());
        assertEquals(val2, iter.next());
        assertFalse(iter.hasNext());
    }

    @Test
    public void set() {
        ObjectValue ov = PropertyValue.createObject();
        PropertyValue val = PropertyValue.createValue("k", "v");
        ov.set(val);
        ov.set(val);
        ov.set(val);
        assertEquals(1, ov.getSize());
        assertEquals(val, ov.getValue("k"));

    }

    @Test
    public void getSet_Value() {
        ObjectValue ov = PropertyValue.createObject();
        ov.setValue("v");
        assertEquals(0, ov.getSize());
        assertEquals("v", ov.getValue());
    }

    @Test
    public void setValue_KeyValue() {
        ObjectValue ov = PropertyValue.createObject();
        ov.setValue("k", "v");
        assertEquals(1, ov.getSize());
        assertEquals("v", ov.getValue("k").getValue());
        assertEquals("k", ov.getValue("k").getKey());
        ov.setValue("k2", "v2");
        assertEquals(2, ov.getSize());
        assertEquals("v2", ov.getValue("k2").getValue());
        assertEquals("k2", ov.getValue("k2").getKey());
        ov.setValue("k", "v");
        assertEquals(2, ov.getSize());
        assertEquals("v", ov.getValue("k").getValue());
        assertEquals("k", ov.getValue("k").getKey());
    }

    @Test
    public void setValue_WithCompositeKey_Single() {
        ObjectValue ov = PropertyValue.createObject();
        ov.setValueWithCompositeKey("k1.k2.k3", "v");
        assertEquals(1, ov.getSize());
        ObjectValue treeNode = (ObjectValue)ov.getValue("k1");
        assertNotNull(treeNode);
        treeNode = (ObjectValue)treeNode.getValue("k2");
        assertNotNull(treeNode);
        PropertyValue finalValue = treeNode.getValue("k3");
        assertNotNull(finalValue);
        assertEquals("v", finalValue.getValue());
    }


    @Test
    public void setObject() {
        ObjectValue ov = PropertyValue.createObject();
        ov.setObject("k");
        assertEquals(1, ov.getSize());
        ObjectValue ov2 = (ObjectValue)ov.getValue("k");
        assertNotNull(ov2);
        assertEquals("k", ov2.getKey());
    }

    @Test
    public void setList() {
        ObjectValue ov = PropertyValue.createObject();
        ov.setList("k");
        assertEquals(1, ov.getSize());
        ListValue lv = (ListValue)ov.getValue("k");
        assertEquals("k", lv.getKey());
    }

    @Test
    public void getValue_WithName() {
        ObjectValue ov = PropertyValue.createObject();
        ov.setList("k1");
        ov.setList("k2");
        ov.setObject("k3");
        ov.setValue("k4", "v");
        Collection<PropertyValue> values = ov.getValues();
        assertNotNull(values);
        assertEquals(4, values.size());
    }

    @Test
    public void getValues_WithPredicate() {
        ObjectValue ov = PropertyValue.createObject();
        ov.setList("k1");
        ov.setList("k2");
        ov.setObject("k3");
        ov.setValue("k4", "v");
        Collection<PropertyValue> values = ov.getValues(
                pv -> "k1".equals(pv.getKey())
        );
        assertNotNull(values);
        assertEquals(1, values.size());
        assertEquals("k1", values.iterator().next().getKey());
    }

    @Test
    public void getValues() {
        ObjectValue ov = PropertyValue.createObject();
        ov.setList("k1");
        ov.setList("k2");
        ov.setObject("k3");
        ov.setValue("k4", "v");
        Collection<PropertyValue> values = ov.getValues();
        assertNotNull(values);
        assertEquals(4, values.size());
    }

    @Test
    public void toPropertyValue() {
        ObjectValue ov = PropertyValue.createObject("foo");
        ov.setList("list");
        PropertyValue pv = ov.toPropertyValue();
        assertNotNull(pv);
        assertEquals(pv.getKey(), ov.getKey());
    }

    @Test
    public void toListValue() {
        ObjectValue ov = PropertyValue.createObject("foo");
        ov.setList("list").setValue("a");
        ListValue lv = ov.toListValue();
        assertNotNull(lv);
        assertEquals(lv.getKey(), ov.getKey());
        assertNotNull(lv.get(0));
        assertEquals(ov.getValue("list").getValue(), lv.get(0).getValue());
    }

    @Test
    public void toObjectValue() {
        ObjectValue ov = PropertyValue.createObject("foo");
        ov.setList("list");
        ObjectValue ov2 = ov.toObjectValue();
        assertTrue(ov == ov2);
    }

    @Test
    public void mutable() {
        ObjectValue ov = PropertyValue.createObject("foo");
        ov.setList("list");
        assertFalse(ov.isImmutable());
        ObjectValue ov2 = ov.mutable();
        assertFalse(ov2.isImmutable());
        assertTrue(ov == ov2);
    }

    @Test
    public void deepClone() {
        ObjectValue ov1 = PropertyValue.createObject("foo");
        ov1.setList("list");
        ObjectValue ov2 = ov1.deepClone();
        assertTrue(ov1.getValues()!=ov2.getValues());
        assertTrue(ov1.getMeta()!=ov2.getMeta());
        assertTrue(ov1.equals(ov2));
        assertTrue(ov1.iterator().next()!=ov2.iterator().next());
    }

    @Test
    public void equals() {
        ObjectValue ov1 = PropertyValue.createObject("foo");
        ov1.setList("list");
        ObjectValue ov2 = PropertyValue.createObject("foo");
        ov2.setList("list");
        assertTrue(ov1.equals(ov2));
    }

    @Test
    public void testHashCode() {
        ObjectValue ov1 = PropertyValue.createObject("foo");
        ov1.setList("list");
        ObjectValue ov2 = PropertyValue.createObject("foo");
        ov2.setList("list");
        assertTrue(ov1.hashCode() == ov2.hashCode());
    }

    @Test
    public void testToString() {
        ObjectValue ov1 = PropertyValue.createObject("foo");
        String toString = ov1.toString();
        assertNotNull(toString);
        ov1.setList("list");
        toString = ov1.toString();
        assertNotNull(toString);
        ov1.setObject("object");
        toString = ov1.toString();
        assertNotNull(toString);
        ov1.setValue("valueKey", "value");
        toString = ov1.toString();
        assertNotNull(toString);
        assertEquals("PropertyValue[MAP]{'foo', size='3'}", toString);
    }
}