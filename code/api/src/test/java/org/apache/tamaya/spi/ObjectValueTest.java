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
        PropertyValue val = PropertyValue.createValue("k", "v");
        ov.set(val);
        PropertyValue val2 = PropertyValue.createValue("k2", "v2");
        ov.set(val2);
        assertThat(val).isEqualTo(ov.getValue(val.getKey()));
        assertThat(val2).isEqualTo(ov.getValue(val2.getKey()));
        assertThat(ov.getValue("foo")).isNull();
    }

    @Test
    public void getSize() {
        ObjectValue ov = PropertyValue.createObject();
        assertThat(0).isEqualTo(ov.getSize());
        PropertyValue val = PropertyValue.createValue("k", "v");
        ov.set(val);
        assertThat(1).isEqualTo(ov.getSize());
        PropertyValue val2 = PropertyValue.createValue("k2", "v");
        ov.set(val2);
        assertThat(2).isEqualTo(ov.getSize());
        PropertyValue val3 = PropertyValue.createValue("k2", "v");
        ov.set(val3);
        assertThat(2).isEqualTo(ov.getSize());
    }

    @Test
    public void getValue() {
        ObjectValue ov = PropertyValue.createObject();
        PropertyValue val = PropertyValue.createValue("k1", "v");
        ov.set(val);
        PropertyValue val2 = PropertyValue.createValue("k2", "v");
        ov.set(val2);
        assertThat(ov.getValues()).isNotNull().hasSize(2);
        assertThat(val).isEqualTo(ov.getValue("k1"));
        assertThat(val2).isEqualTo(ov.getValue("k2"));
        ov.set(val2);
        assertThat(ov.getValues()).isNotNull().hasSize(2);
        assertThat(val).isEqualTo(ov.getValue("k1"));
        assertThat(val2).isEqualTo(ov.getValue("k2"));
    }

    @Test
    public void getOrSetValue() {
        ObjectValue ov = PropertyValue.createObject();
        PropertyValue val = PropertyValue.createValue("k1", "v");
        ov.set(val);
        PropertyValue val2 = ov.getOrSetValue("k2",
                () -> PropertyValue.createValue("foo", "bar"));
        PropertyValue pv = ov.getOrSetValue("foo",  () -> PropertyValue.createValue("foo", "bar"));
        assertThat(pv).isNotNull();
        assertThat(ov.getValues()).hasSize(3);
        assertThat(val).isEqualTo(ov.getValue("k1"));
        assertThat(val2).isEqualTo(ov.getValue("k2"));
        assertThat(pv).isEqualTo(ov.getValue("foo"));
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
        assertThat(pv).isNotNull();
        assertThat("foo").isEqualTo(pv.getKey());
        assertThat("bar").isEqualTo(pv.getValue());
    }

    @Test
    public void iterator() {
        ObjectValue ov = PropertyValue.createObject();
        PropertyValue val = PropertyValue.createValue("k1", "v");
        PropertyValue val2 = PropertyValue.createValue("k2", "v");
        ov.set(val);
        ov.set(val2);
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
        PropertyValue val = PropertyValue.createValue("k", "v");
        ov.set(val);
        ov.set(val);
        ov.set(val);
        assertThat(1).isEqualTo(ov.getSize());
        assertThat(val).isEqualTo(ov.getValue("k"));

    }

    @Test
    public void getSet_Value() {
        ObjectValue ov = PropertyValue.createObject();
        ov.setValue("v");
        assertThat(0).isEqualTo(ov.getSize());
        assertThat("v").isEqualTo(ov.getValue());
    }

    @Test
    public void setValue_KeyValue() {
        ObjectValue ov = PropertyValue.createObject();
        ov.setValue("k", "v");
        assertThat(1).isEqualTo(ov.getSize());
        assertThat("v").isEqualTo(ov.getValue("k").getValue());
        assertThat("k").isEqualTo(ov.getValue("k").getKey());
        ov.setValue("k2", "v2");
        assertThat(2).isEqualTo(ov.getSize());
        assertThat("v2").isEqualTo(ov.getValue("k2").getValue());
        assertThat("k2").isEqualTo(ov.getValue("k2").getKey());
        ov.setValue("k", "v");
        assertThat(2).isEqualTo(ov.getSize());
        assertThat("v").isEqualTo(ov.getValue("k").getValue());
        assertThat("k").isEqualTo(ov.getValue("k").getKey());
    }

    @Test
    public void setValue_WithCompositeKey_Single() {
        ObjectValue ov = PropertyValue.createObject();
        ov.setValueWithCompositeKey("k1.k2.k3", "v");
        assertThat(1).isEqualTo(ov.getSize());
        ObjectValue treeNode = (ObjectValue)ov.getValue("k1");
        assertThat(treeNode).isNotNull();
        treeNode = (ObjectValue)treeNode.getValue("k2");
        assertThat(treeNode).isNotNull();
        PropertyValue finalValue = treeNode.getValue("k3");
        assertThat(finalValue).isNotNull();
        assertThat("v").isEqualTo(finalValue.getValue());
    }


    @Test
    public void setObject() {
        ObjectValue ov = PropertyValue.createObject();
        ov.setObject("k");
        assertThat(1).isEqualTo(ov.getSize());
        ObjectValue ov2 = (ObjectValue)ov.getValue("k");
        assertThat(ov2).isNotNull();
        assertThat("k").isEqualTo(ov2.getKey());
    }

    @Test
    public void setList() {
        ObjectValue ov = PropertyValue.createObject();
        ov.setList("k");
        assertThat(1).isEqualTo(ov.getSize());
        ListValue lv = (ListValue)ov.getValue("k");
        assertThat("k").isEqualTo(lv.getKey());
    }

    @Test
    public void getValue_WithName() {
        ObjectValue ov = PropertyValue.createObject();
        ov.setList("k1");
        ov.setList("k2");
        ov.setObject("k3");
        ov.setValue("k4", "v");
        Collection<PropertyValue> values = ov.getValues();
        assertThat(values).isNotNull().hasSize(4);
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
        assertThat(values).isNotNull().hasSize(1);
        assertThat("k1").isEqualTo(values.iterator().next().getKey());
    }

    @Test
    public void getValues() {
        ObjectValue ov = PropertyValue.createObject();
        ov.setList("k1");
        ov.setList("k2");
        ov.setObject("k3");
        ov.setValue("k4", "v");
        Collection<PropertyValue> values = ov.getValues();
        assertThat(values).isNotNull().hasSize(4);
    }

    @Test
    public void toPropertyValue() {
        ObjectValue ov = PropertyValue.createObject("foo");
        ov.setList("list");
        PropertyValue pv = ov.toPropertyValue();
        assertThat(pv).isNotNull();
        assertThat(pv.getKey()).isEqualTo(ov.getKey());
    }

    @Test
    public void toListValue() {
        ObjectValue ov = PropertyValue.createObject("foo");
        ov.setList("list").setValue("a");
        ListValue lv = ov.toListValue();
        assertThat(lv).isNotNull();
        assertThat(lv.getKey()).isEqualTo(ov.getKey());
        assertThat(lv.get(0)).isNotNull();
        assertThat(ov.getValue("list").getValue()).isEqualTo(lv.get(0).getValue());
    }

    @Test
    public void toObjectValue() {
        ObjectValue ov = PropertyValue.createObject("foo");
        ov.setList("list");
        ObjectValue ov2 = ov.toObjectValue();
        assertThat(ov == ov2).isTrue();
    }

    @Test
    public void mutable() {
        ObjectValue ov = PropertyValue.createObject("foo");
        ov.setList("list");
        assertThat(ov.isImmutable()).isFalse();
        ObjectValue ov2 = ov.mutable();
        assertThat(ov2.isImmutable()).isFalse();
        assertThat(ov == ov2).isTrue();
    }

    @Test
    public void deepClone() {
        ObjectValue ov1 = PropertyValue.createObject("foo");
        ov1.setList("list");
        ObjectValue ov2 = ov1.deepClone();
        assertThat(ov1.getValues()!=ov2.getValues()).isTrue();
        assertThat(ov1.getMeta()!=ov2.getMeta()).isTrue();
        assertThat(ov1.equals(ov2)).isTrue();
        assertThat(ov1.iterator().next()!=ov2.iterator().next()).isTrue();
    }

    @Test
    public void equals() {
        ObjectValue ov1 = PropertyValue.createObject("foo");
        ov1.setList("list");
        ObjectValue ov2 = PropertyValue.createObject("foo");
        ov2.setList("list");
        assertThat(ov1.equals(ov2)).isTrue();
    }

    @Test
    public void testHashCode() {
        ObjectValue ov1 = PropertyValue.createObject("foo");
        ov1.setList("list");
        ObjectValue ov2 = PropertyValue.createObject("foo");
        ov2.setList("list");
        assertThat(ov1.hashCode() == ov2.hashCode()).isTrue();
    }

    @Test
    public void testToString() {
        ObjectValue ov1 = PropertyValue.createObject("foo");
        String toString = ov1.toString();
        assertThat(toString).isNotNull();
        ov1.setList("list");
        toString = ov1.toString();
        assertThat(toString).isNotNull();
        ov1.setObject("object");
        toString = ov1.toString();
        assertThat(toString).isNotNull();
        ov1.setValue("valueKey", "value");
        toString = ov1.toString();
        assertThat(toString).isNotNull();
        assertThat("PropertyValue[MAP]{'foo', size='3'}").isEqualTo(toString);
    }
}
