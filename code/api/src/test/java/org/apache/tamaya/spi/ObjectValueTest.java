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

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ObjectValue}.
 */
public class ObjectValueTest {

    @Test
    public void getCreation() {
        ObjectValue ov = PropertyValue.createObject();
        assertThat(ov).isNotNull();
        ov = PropertyValue.createObject("k");
        assertThat(ov).isNotNull();
        assertThat("k").isEqualTo(ov.getKey());
    }

    @Test
    public void getValueType() {
        assertThat(PropertyValue.ValueType.ARRAY).isEqualTo(PropertyValue.createList().getValueType());
    }

    @Test
    public void getIndex() {
        ObjectValue ov = PropertyValue.createObject();
        PropertyValue val = new PropertyValue("k", "v");
        ov.setPropertyValue(val);
        PropertyValue val2 = new PropertyValue("k2", "v2");
        ov.setPropertyValue(val2);
        assertThat(val).isEqualTo(ov.getPropertyValue(val.getKey()));
        assertThat(val2).isEqualTo(ov.getPropertyValue(val2.getKey()));
        assertThat(ov.getPropertyValue("foo")).isNull();
    }

    @Test
    public void getKeys() {
        ObjectValue ov = PropertyValue.createObject();
        PropertyValue val = new PropertyValue("k", "v");
        ov.setPropertyValue(val);
        PropertyValue val2 = new PropertyValue("k2", "v2");
        ov.setPropertyValue(val2);
        assertThat(ov.getKeys()).contains("k", "k2");
    }

    @Test
    public void getSize() {
        ObjectValue ov = PropertyValue.createObject();
        assertThat(0).isEqualTo(ov.getSize());
        PropertyValue val = new PropertyValue("k", "v");
        ov.setPropertyValue(val);
        assertThat(1).isEqualTo(ov.getSize());
        PropertyValue val2 = new PropertyValue("k2", "v");
        ov.setPropertyValue(val2);
        assertThat(2).isEqualTo(ov.getSize());
        PropertyValue val3 = new PropertyValue("k2", "v");
        ov.setPropertyValue(val3);
        assertThat(2).isEqualTo(ov.getSize());
    }

    @Test
    public void getValue() {
        ObjectValue ov = PropertyValue.createObject();
        PropertyValue val = new PropertyValue("k1", "v");
        ov.setPropertyValue(val);
        PropertyValue val2 = new PropertyValue("k2", "v");
        ov.setPropertyValue(val2);
        assertThat(ov.getValues()).isNotNull().hasSize(2);
        assertThat(val).isEqualTo(ov.getPropertyValue("k1"));
        assertThat(val2).isEqualTo(ov.getPropertyValue("k2"));
        ov.setPropertyValue(val2);
        assertThat(ov.getValues()).isNotNull().hasSize(2);
        assertThat(val).isEqualTo(ov.getPropertyValue("k1"));
        assertThat(val2).isEqualTo(ov.getPropertyValue("k2"));
    }

    @Test
    public void setValueWithCompositeKeys_KeyValue() {
        ObjectValue ov = PropertyValue.createObject();
        ov.setValueWithCompositeKey("a.b", "val");
        assertThat(ov.toLocalMap()).containsKeys("b");
    }

    @Test
    public void setValueWithCompositeKeys_Map() {
        ObjectValue ov = PropertyValue.createObject();
        Map<String,String> data = new HashMap<>();
        data.put("a.b", "val");
        ov.setValueWithCompositeKey(data);
        assertThat(ov.toLocalMap()).containsKeys("b");
    }

    @Test
    public void getStringValue() {
        ObjectValue ov = PropertyValue.createObject();
        PropertyValue val = new PropertyValue("k1", "v");
        ov.setPropertyValue(val);
        assertThat(ov.getValue("k1")).isEqualTo("v");
        assertThat(ov.getValue("foo")).isNull();
    }

    @Test
    public void getOrSetValue() {
        ObjectValue ov = PropertyValue.createObject();
        PropertyValue val = new PropertyValue("k1", "v");
        ov.setPropertyValue(val);
        PropertyValue val2 = ov.getOrSetValue("k2",
                () -> new PropertyValue("foo", "bar"));
        PropertyValue pv = ov.getOrSetValue("foo",  () -> new PropertyValue("foo", "bar"));
        assertThat(pv).isNotNull();
        assertThat(ov.getValues()).hasSize(3);
        assertThat(val).isEqualTo(ov.getPropertyValue("k1"));
        assertThat(val2).isEqualTo(ov.getPropertyValue("k2"));
        assertThat(pv).isEqualTo(ov.getPropertyValue("foo"));
    }

    @Test
    public void setValues_Map() {
        ObjectValue ov = PropertyValue.createObject();
        Map map = new HashMap<>();
        map.put("k1", "v");
        map.put("k2.k3", "v2");
        map.put("foo", "bar");
        ov.setValues(map);
        PropertyValue pv = ov.getPropertyValue("foo");
        assertThat(pv).isNotNull();
        assertThat("foo").isEqualTo(pv.getKey());
        assertThat("bar").isEqualTo(pv.getValue());
    }

    @Test
    public void iterator() {
        ObjectValue ov = PropertyValue.createObject();
        PropertyValue val = new PropertyValue("k1", "v");
        PropertyValue val2 = new PropertyValue("k2", "v");
        ov.setPropertyValue(val);
        ov.setPropertyValue(val2);
        Iterator iter = ov.iterator();
        assertThat(iter).isNotNull();
        assertThat(iter.hasNext()).isTrue();
        assertThat(val).isEqualTo(iter.next());
        assertThat(iter.hasNext()).isTrue();
        assertThat(val2).isEqualTo(iter.next());
        assertThat(iter.hasNext()).isFalse();
    }

    @Test
    public void set() {
        ObjectValue ov = PropertyValue.createObject();
        PropertyValue val = new PropertyValue("k", "v");
        ov.setPropertyValue(val);
        ov.setPropertyValue(val);
        ov.setPropertyValue(val);
        assertThat(1).isEqualTo(ov.getSize());
        assertThat(val).isEqualTo(ov.getPropertyValue("k"));

    }

    @Test
    public void getSet_Value() {
        ObjectValue ov = PropertyValue.createObject();
        assertThat(0).isEqualTo(ov.getSize());
        assertThat(ov.getValue()).isNotNull();
    }

    @Test
    public void setValue_KeyValue() {
        ObjectValue ov = PropertyValue.createObject();
        ov.setValue("k", "v");
        assertThat(1).isEqualTo(ov.getSize());
        assertThat("v").isEqualTo(ov.getValue("k"));
        assertThat("k").isEqualTo(ov.getPropertyValue("k").getKey());
        ov.setValue("k2", "v2");
        assertThat(2).isEqualTo(ov.getSize());
        assertThat("v2").isEqualTo(ov.getValue("k2"));
        assertThat("k2").isEqualTo(ov.getPropertyValue("k2").getKey());
        ov.setValue("k", "v");
        assertThat(2).isEqualTo(ov.getSize());
        assertThat("v").isEqualTo(ov.getPropertyValue("k").getValue());
        assertThat("k").isEqualTo(ov.getPropertyValue("k").getKey());
    }

    @Test
    public void setValue_WithCompositeKey_Single() {
        ObjectValue ov = PropertyValue.createObject();
        ov.setValueWithCompositeKey("k1.k2.k3", "v");
        assertThat(1).isEqualTo(ov.getSize());
        ObjectValue treeNode = (ObjectValue)ov.getPropertyValue("k1");
        assertThat(treeNode).isNotNull();
        treeNode = (ObjectValue)treeNode.getPropertyValue("k2");
        assertThat(treeNode).isNotNull();
        PropertyValue finalValue = treeNode.getPropertyValue("k3");
        assertThat(finalValue).isNotNull();
        assertThat("v").isEqualTo(finalValue.getValue());
    }


    @Test
    public void setObject() {
        ObjectValue ov = PropertyValue.createObject();
        ov.addObject("k");
        assertThat(1).isEqualTo(ov.getSize());
        ObjectValue ov2 = (ObjectValue)ov.getPropertyValue("k");
        assertThat(ov2).isNotNull();
        assertThat("k").isEqualTo(ov2.getKey());
    }

    @Test
    public void setList() {
        ObjectValue ov = PropertyValue.createObject();
        ov.addList("k");
        assertThat(1).isEqualTo(ov.getSize());
        ListValue lv = (ListValue)ov.getPropertyValue("k");
        assertThat("k").isEqualTo(lv.getKey());
    }

    @Test
    public void getValue_WithName() {
        ObjectValue ov = PropertyValue.createObject();
        ov.addList("k1");
        ov.addList("k2");
        ov.addObject("k3");
        ov.setValue("k4", "v");
        Collection<PropertyValue> values = ov.getValues();
        assertThat(values).isNotNull().hasSize(4);
    }

    @Test
    public void getValues() {
        ObjectValue ov = PropertyValue.createObject();
        ov.addList("k1");
        ov.addList("k2");
        ov.addObject("k3");
        ov.setValue("k4", "v");
        Collection<PropertyValue> values = ov.getValues();
        assertThat(values).isNotNull().hasSize(4);
    }

    @Test
    public void toListValue() {
        ObjectValue ov = PropertyValue.createObject("foo");
        ov.addList("list").addValue("a");
        ListValue lv = ov.toListValue();
        assertThat(lv).isNotNull();
        assertThat(lv.getKey()).isEqualTo(ov.getKey());
        assertThat(lv.getPropertyValue(0)).isNotNull();
        assertThat(ov.getValue("list")).isEqualTo(lv.getPropertyValue(0).getValue());
    }

    @Test
    public void toObjectValue() {
        ObjectValue ov = PropertyValue.createObject("foo");
        ov.addList("list");
        ObjectValue ov2 = ov.toObjectValue();
        assertThat(ov == ov2).isTrue();
    }

    @Test
    public void mutable() {
        ObjectValue ov = PropertyValue.createObject("foo");
        ov.addList("list");
        assertThat(ov.isImmutable()).isFalse();
        ObjectValue ov2 = ov.mutable();
        assertThat(ov2.isImmutable()).isFalse();
        assertThat(ov == ov2).isTrue();
    }

    @Test
    public void testSetValues_WithSource() throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("a", "b");
        map.put("b", "c");
        ObjectValue ov = PropertyValue.createObject();
        ov.setValues(map, "source", true);
        assertThat(ov.getSize()).isEqualTo(map.size());
        for(PropertyValue pv:ov){
            assertThat("source").isEqualTo(pv.getMeta("source"));
        }
        assertThat("b").isEqualTo(ov.getValue("a"));
        assertThat("c").isEqualTo(ov.getValue("b"));
    }

    @Test
    public void testSetValues() {
        Map<String, String> map = new HashMap<>();
        map.put("a", "b");
        map.put("b", "c");
        ObjectValue ov = PropertyValue.createObject();
        ov.setValues(map);
        assertThat(ov.getSize()).isEqualTo(map.size());
        assertThat(ov.getSize()).isEqualTo(map.size());
        assertThat("b").isEqualTo(ov.getValue("a"));
        assertThat("c").isEqualTo(ov.getValue("b"));
    }

    @Test
    public void deepClone() {
        ObjectValue ov1 = PropertyValue.createObject("foo");
        ov1.addList("list");
        ObjectValue ov2 = ov1.deepClone();
        assertThat(ov1.getValues()!=ov2.getValues()).isTrue();
        assertThat(ov1.getMeta()!=ov2.getMeta()).isTrue();
        assertThat(ov1.equals(ov2)).isTrue();
        assertThat(ov1.iterator().next()!=ov2.iterator().next()).isTrue();
    }

    @Test
    public void equals() {
        ObjectValue ov1 = PropertyValue.createObject("foo");
        ov1.addList("list");
        ObjectValue ov2 = PropertyValue.createObject("foo");
        ov2.addList("list");
        assertThat(ov1.equals(ov2)).isTrue();
    }

    @Test
    public void testHashCode() {
        ObjectValue ov1 = PropertyValue.createObject("foo");
        ov1.addList("list");
        ObjectValue ov2 = PropertyValue.createObject("foo");
        ov2.addList("list");
        assertThat(ov1.hashCode() == ov2.hashCode()).isTrue();
    }

    @Test
    public void testToString() {
        ObjectValue ov1 = PropertyValue.createObject("foo");
        String toString = ov1.toString();
        assertThat(toString).isNotNull();
        ov1.addList("list");
        toString = ov1.toString();
        assertThat(toString).isNotNull();
        ov1.addObject("object");
        toString = ov1.toString();
        assertThat(toString).isNotNull();
        ov1.setValue("valueKey", "value");
        toString = ov1.toString();
        assertThat(toString).isNotNull();
        assertThat("Object{size='3', values='{valueKey=value}}").isEqualTo(toString);
    }
}
