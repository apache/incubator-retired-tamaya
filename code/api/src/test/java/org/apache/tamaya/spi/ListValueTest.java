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

import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link ListValue}.
 */
public class ListValueTest {

    @Test
    public void getCreation() {
        ListValue lv = PropertyValue.createList();
        assertNotNull(lv);
        lv = PropertyValue.createList("k");
        assertNotNull(lv);
        assertEquals("k", lv.getKey());
    }

    @Test
    public void getValueType() {
        assertEquals(PropertyValue.ValueType.ARRAY, PropertyValue.createList().getValueType());
    }

    @Test
    public void getIndex() {
        ListValue lv = PropertyValue.createList();
        PropertyValue val = PropertyValue.createValue("k", "v");
        lv.add(val);
        PropertyValue val2 = PropertyValue.createValue("k2", "v2");
        lv.add(val2);
        assertEquals(0, lv.getIndex(val));
        assertEquals(1, lv.getIndex(val2));
        PropertyValue val3 = PropertyValue.createValue("k3", "v");
        assertEquals(-1, lv.getIndex(val3));
    }

    @Test
    public void getSize() {
        ListValue lv = PropertyValue.createList();
        assertEquals(0, lv.getSize());
        PropertyValue val = PropertyValue.createValue("k", "v");
        lv.add(val);
        assertEquals(1, lv.getSize());
        PropertyValue val2 = PropertyValue.createValue("k", "v");
        lv.add(val2);
        assertEquals(2, lv.getSize());
    }

    @Test
    public void getList() {
        ListValue lv = PropertyValue.createList();
        PropertyValue val = PropertyValue.createValue("k", "v");
        lv.add(val);
        PropertyValue val2 = PropertyValue.createValue("k", "v");
        lv.add(val2);
        assertNotNull(lv.getValues());
        assertEquals(2, lv.getValues().size());
        assertEquals(val, lv.getValues().get(0));
        assertEquals(val2, lv.getValues().get(1));
        lv.add(val2);
        assertNotNull(lv.getValues());
        assertEquals(2, lv.getValues().size());
        assertEquals(val, lv.getValues().get(0));
        assertEquals(val2, lv.getValues().get(1));
    }

    @Test
    public void getList_WithPredicate() {
        ListValue lv = PropertyValue.createList();
        PropertyValue val = PropertyValue.createValue("k1", "v");
        lv.add(val);
        PropertyValue val2 = PropertyValue.createValue("k2", "v");
        lv.add(val2);
        List<PropertyValue> result = lv.getValues(
                pv -> "k1".equals(pv.getKey())
        );
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(val, result.get(0));
    }

    @Test
    public void iterator() {
        ListValue lv = PropertyValue.createList();
        PropertyValue val = PropertyValue.createValue("k", "v");
        PropertyValue val2 = PropertyValue.createValue("k", "v");
        lv.add(val);
        lv.add(val2);
        Iterator iter = lv.iterator();
        assertNotNull(iter);
        assertTrue(iter.hasNext());
        assertEquals(val, iter.next());
        assertTrue(iter.hasNext());
        assertEquals(val2, iter.next());
        assertFalse(iter.hasNext());
    }

    @Test
    public void add() {
        ListValue lv = PropertyValue.createList();
        PropertyValue val = PropertyValue.createValue("k", "v");
        lv.add(val);
        lv.add(val);
        lv.add(val);
        assertEquals(1, lv.getSize());
        assertEquals(val, lv.get(0));

    }

    @Test
    public void addValue_Value() {
        ListValue lv = PropertyValue.createList();
        lv.addValue("v");
        assertEquals(1, lv.getSize());
        assertEquals("v", lv.get(0).getValue());
        lv.addValue("v2");
        assertEquals(2, lv.getSize());
        assertEquals("v2", lv.get(1).getValue());
        lv.addValue("v");
        assertEquals(3, lv.getSize());
        assertEquals("v", lv.get(2).getValue());
    }

    @Test
    public void addValue_KeyValue() {
        ListValue lv = PropertyValue.createList();
        lv.addValue("k", "v");
        assertEquals(1, lv.getSize());
        assertEquals("v", lv.get(0).getValue());
        assertEquals("k", lv.get(0).getKey());
        lv.addValue("k2", "v2");
        assertEquals(2, lv.getSize());
        assertEquals("v2", lv.get(1).getValue());
        assertEquals("k2", lv.get(1).getKey());
        lv.addValue("k", "v");
        assertEquals(3, lv.getSize());
        assertEquals("v", lv.get(2).getValue());
        assertEquals("k", lv.get(2).getKey());
    }

    @Test
    public void addValues() {
        ListValue lv = PropertyValue.createList();
        lv.addValues("v", "v1", "v");
        assertEquals(3, lv.getSize());
        assertEquals("v", lv.get(0).getValue());
        assertEquals("v1", lv.get(1).getValue());
        assertEquals("v", lv.get(2).getValue());
    }

    @Test
    public void addObject() {
        ListValue lv = PropertyValue.createList();
        lv.addObject();
        assertEquals(1, lv.getSize());
        ObjectValue ov = (ObjectValue)lv.get(0);
    }

    @Test
    public void addObject_Key() {
        ListValue lv = PropertyValue.createList();
        lv.addObject("key");
        assertEquals(1, lv.getSize());
        ObjectValue ov = (ObjectValue)lv.get(0);
        assertEquals("key", ov.getKey());
    }

    @Test
    public void addList() {
        ListValue lv = PropertyValue.createList();
        lv.addList();
        assertEquals(1, lv.getSize());
        ListValue ov = (ListValue)lv.get(0);
        assertEquals("", ov.getKey());
    }

    @Test
    public void addList_Key() {
        ListValue lv = PropertyValue.createList();
        lv.addList("key");
        assertEquals(1, lv.getSize());
        ListValue ov = (ListValue)lv.get(0);
        assertEquals("key", ov.getKey());
    }

    @Test
    public void getValues() {
        ListValue lv = PropertyValue.createList();
        lv.addList("list");
        lv.addObject("object");
        assertNotNull(lv.getValues(""));
        assertEquals(0, lv.getValues("").size());
        assertEquals(1, lv.getValues("list").size());
        assertEquals(1, lv.getValues("object").size());
    }

    @Test
    public void toPropertyValue() {
        ListValue lv = PropertyValue.createList("foo");
        lv.addList("list");
        PropertyValue pv = lv.toPropertyValue();
        assertNotNull(pv);
        assertEquals(pv.getKey(), lv.getKey());
    }

    @Test
    public void toObjectValue() {
        ListValue lv = PropertyValue.createList("foo");
        lv.addList("list").setValue("a");
        ObjectValue ov = lv.toObjectValue();
        assertNotNull(ov);
        assertEquals(ov.getKey(), lv.getKey());
        assertNotNull(ov.getValue("list[0]"));
        assertEquals(lv.getLists("list").get(0).getValue(), ov.getValue("list[0]").getValue());
    }

    @Test
    public void toListValue() {
        ListValue lv = PropertyValue.createList("foo");
        lv.addList("list");
        ListValue lv2 = lv.toListValue();
        assertTrue(lv == lv2);
    }

    @Test
    public void mutable() {
        ListValue lv = PropertyValue.createList("foo");
        lv.addList("list");
        assertFalse(lv.isImmutable());
        ListValue lv2 = lv.mutable();
        assertFalse(lv2.isImmutable());
        assertTrue(lv == lv2);
    }

    @Test
    public void deepClone() {
        ListValue lv1 = PropertyValue.createList("foo");
        lv1.addList("list");
        ListValue lv2 = lv1.deepClone();
        assertTrue(lv1.getValues()!=lv2.getValues());
        assertTrue(lv1.getMeta()!=lv2.getMeta());
        assertTrue(lv1.equals(lv2));
        assertTrue(lv1.iterator().next()!=lv2.iterator().next());
    }

    @Test
    public void equals() {
        ListValue lv1 = PropertyValue.createList("foo");
        lv1.addList("list");
        ListValue lv2 = PropertyValue.createList("foo");
        lv2.addList("list");
        assertTrue(lv1.equals(lv2));
    }

    @Test
    public void testHashCode() {
        ListValue lv1 = PropertyValue.createList("foo");
        lv1.addList("list");
        ListValue lv2 = PropertyValue.createList("foo");
        lv2.addList("list");
        assertTrue(lv1.hashCode() == lv2.hashCode());
    }

    @Test
    public void testToString() {
        ListValue lv1 = PropertyValue.createList("foo");
        String toString = lv1.toString();
        assertNotNull(toString);
        lv1.addList("list");
        toString = lv1.toString();
        assertNotNull(toString);
        lv1.addObject("object");
        toString = lv1.toString();
        assertNotNull(toString);
        lv1.addValue("valueKey");
        toString = lv1.toString();
        assertNotNull(toString);
        assertEquals("PropertyValue[ARRAY]{'foo', size='3'}", toString);
    }
}