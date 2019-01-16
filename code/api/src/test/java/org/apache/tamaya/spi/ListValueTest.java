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

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ListValue}.
 */
public class ListValueTest {

    @Test
    public void getCreation() {
        ListValue lv = PropertyValue.createList();
        assertThat(lv).isNotNull();
        lv = PropertyValue.createList("k");
        assertThat(lv).isNotNull();
        assertThat("k").isEqualTo(lv.getKey());
    }

    @Test
    public void getValueType() {
        assertThat(PropertyValue.ValueType.ARRAY).isEqualTo(PropertyValue.createList().getValueType());
    }

    @Test
    public void getIndex() {
        ListValue lv = PropertyValue.createList();
        PropertyValue val = PropertyValue.createValue("k", "v");
        lv.add(val);
        PropertyValue val2 = PropertyValue.createValue("k2", "v2");
        lv.add(val2);
        assertThat(0).isEqualTo(lv.getIndex(val));
        assertThat(1).isEqualTo(lv.getIndex(val2));
        PropertyValue val3 = PropertyValue.createValue("k3", "v");
        assertThat(-1).isEqualTo(lv.getIndex(val3));
    }

    @Test
    public void getSize() {
        ListValue lv = PropertyValue.createList();
        assertThat(0).isEqualTo(lv.getSize());
        PropertyValue val = PropertyValue.createValue("k", "v");
        lv.add(val);
        assertThat(1).isEqualTo(lv.getSize());
        PropertyValue val2 = PropertyValue.createValue("k", "v");
        lv.add(val2);
        assertThat(2).isEqualTo(lv.getSize());
    }

    @Test
    public void getList() {
        ListValue lv = PropertyValue.createList();
        PropertyValue val = PropertyValue.createValue("k", "v");
        lv.add(val);
        PropertyValue val2 = PropertyValue.createValue("k", "v");
        lv.add(val2);
        assertThat(lv.getValues()).isNotNull();
        assertThat(2).isEqualTo(lv.getValues().size());
        assertThat(val).isEqualTo(lv.getValues().get(0));
        assertThat(val2).isEqualTo(lv.getValues().get(1));
        lv.add(val2);
        assertThat(lv.getValues()).isNotNull();
        assertThat(2).isEqualTo(lv.getValues().size());
        assertThat(val).isEqualTo(lv.getValues().get(0));
        assertThat(val2).isEqualTo(lv.getValues().get(1));
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
        assertThat(result).isNotNull();
        assertThat(1).isEqualTo(result.size());
        assertThat(val).isEqualTo(result.get(0));
    }

    @Test
    public void iterator() {
        ListValue lv = PropertyValue.createList();
        PropertyValue val = PropertyValue.createValue("k", "v");
        PropertyValue val2 = PropertyValue.createValue("k", "v");
        lv.add(val);
        lv.add(val2);
        Iterator iter = lv.iterator();
        assertThat(iter).isNotNull();
        assertThat(iter.hasNext()).isTrue();
        assertThat(val).isEqualTo(iter.next());
        assertThat(iter.hasNext()).isTrue();
        assertThat(val2).isEqualTo(iter.next());
        assertThat(iter.hasNext()).isFalse();
    }

    @Test
    public void add() {
        ListValue lv = PropertyValue.createList();
        PropertyValue val = PropertyValue.createValue("k", "v");
        lv.add(val);
        lv.add(val);
        lv.add(val);
        assertThat(1).isEqualTo(lv.getSize());
        assertThat(val).isEqualTo(lv.get(0));

    }

    @Test
    public void addValue_Value() {
        ListValue lv = PropertyValue.createList();
        lv.addValue("v");
        assertThat(1).isEqualTo(lv.getSize());
        assertThat("v").isEqualTo(lv.get(0).getValue());
        lv.addValue("v2");
        assertThat(2).isEqualTo(lv.getSize());
        assertThat("v2").isEqualTo(lv.get(1).getValue());
        lv.addValue("v");
        assertThat(3).isEqualTo(lv.getSize());
        assertThat("v").isEqualTo(lv.get(2).getValue());
    }

    @Test
    public void addValue_KeyValue() {
        ListValue lv = PropertyValue.createList();
        lv.addValue("k", "v");
        assertThat(1).isEqualTo(lv.getSize());
        assertThat("v").isEqualTo(lv.get(0).getValue());
        assertThat("k").isEqualTo(lv.get(0).getKey());
        lv.addValue("k2", "v2");
        assertThat(2).isEqualTo(lv.getSize());
        assertThat("v2").isEqualTo(lv.get(1).getValue());
        assertThat("k2").isEqualTo(lv.get(1).getKey());
        lv.addValue("k", "v");
        assertThat(3).isEqualTo(lv.getSize());
        assertThat("v").isEqualTo(lv.get(2).getValue());
        assertThat("k").isEqualTo(lv.get(2).getKey());
    }

    @Test
    public void addValues() {
        ListValue lv = PropertyValue.createList();
        lv.addValues("v", "v1", "v");
        assertThat(3).isEqualTo(lv.getSize());
        assertThat("v").isEqualTo(lv.get(0).getValue());
        assertThat("v1").isEqualTo(lv.get(1).getValue());
        assertThat("v").isEqualTo(lv.get(2).getValue());
    }

    @Test
    public void addObject() {
        ListValue lv = PropertyValue.createList();
        lv.addObject();
        assertThat(1).isEqualTo(lv.getSize());
        ObjectValue ov = (ObjectValue)lv.get(0);
    }

    @Test
    public void addObject_Key() {
        ListValue lv = PropertyValue.createList();
        lv.addObject("key");
        assertThat(1).isEqualTo(lv.getSize());
        ObjectValue ov = (ObjectValue)lv.get(0);
        assertThat("key").isEqualTo(ov.getKey());
    }

    @Test
    public void addList() {
        ListValue lv = PropertyValue.createList();
        lv.addList();
        assertThat(1).isEqualTo(lv.getSize());
        ListValue ov = (ListValue)lv.get(0);
        assertThat("").isEqualTo(ov.getKey());
    }

    @Test
    public void addList_Key() {
        ListValue lv = PropertyValue.createList();
        lv.addList("key");
        assertThat(1).isEqualTo(lv.getSize());
        ListValue ov = (ListValue)lv.get(0);
        assertThat("key").isEqualTo(ov.getKey());
    }

    @Test
    public void getValues() {
        ListValue lv = PropertyValue.createList();
        lv.addList("list");
        lv.addObject("object");
        assertThat(lv.getValues("")).isNotNull();
        assertThat(0).isEqualTo(lv.getValues("").size());
        assertThat(1).isEqualTo(lv.getValues("list").size());
        assertThat(1).isEqualTo(lv.getValues("object").size());
    }

    @Test
    public void toPropertyValue() {
        ListValue lv = PropertyValue.createList("foo");
        lv.addList("list");
        PropertyValue pv = lv.toPropertyValue();
        assertThat(pv).isNotNull();
        assertThat(pv.getKey()).isEqualTo(lv.getKey());
    }

    @Test
    public void toObjectValue() {
        ListValue lv = PropertyValue.createList("foo");
        lv.addList("list").setValue("a");
        ObjectValue ov = lv.toObjectValue();
        assertThat(ov).isNotNull();
        assertThat(ov.getKey()).isEqualTo(lv.getKey());
        assertThat(ov.getValue("list[0]")).isNotNull();
        assertThat(lv.getLists("list").get(0).getValue()).isEqualTo(ov.getValue("list[0]").getValue());
    }

    @Test
    public void toListValue() {
        ListValue lv = PropertyValue.createList("foo");
        lv.addList("list");
        ListValue lv2 = lv.toListValue();
        assertThat(lv == lv2).isTrue();
    }

    @Test
    public void mutable() {
        ListValue lv = PropertyValue.createList("foo");
        lv.addList("list");
        assertThat(lv.isImmutable()).isFalse();
        ListValue lv2 = lv.mutable();
        assertThat(lv2.isImmutable()).isFalse();
        assertThat(lv == lv2).isTrue();
    }

    @Test
    public void deepClone() {
        ListValue lv1 = PropertyValue.createList("foo");
        lv1.addList("list");
        ListValue lv2 = lv1.deepClone();
        assertThat(lv1.getValues()!=lv2.getValues()).isTrue();
        assertThat(lv1.getMeta()!=lv2.getMeta()).isTrue();
        assertThat(lv1.equals(lv2)).isTrue();
        assertThat(lv1.iterator().next()!=lv2.iterator().next()).isTrue();
    }

    @Test
    public void equals() {
        ListValue lv1 = PropertyValue.createList("foo");
        lv1.addList("list");
        ListValue lv2 = PropertyValue.createList("foo");
        lv2.addList("list");
        assertThat(lv1.equals(lv2)).isTrue();
    }

    @Test
    public void testHashCode() {
        ListValue lv1 = PropertyValue.createList("foo");
        lv1.addList("list");
        ListValue lv2 = PropertyValue.createList("foo");
        lv2.addList("list");
        assertThat(lv1.hashCode() == lv2.hashCode()).isTrue();
    }

    @Test
    public void testToString() {
        ListValue lv1 = PropertyValue.createList("foo");
        String toString = lv1.toString();
        assertThat(toString).isNotNull();
        lv1.addList("list");
        toString = lv1.toString();
        assertThat(toString).isNotNull();
        lv1.addObject("object");
        toString = lv1.toString();
        assertThat(toString).isNotNull();
        lv1.addValue("valueKey");
        toString = lv1.toString();
        assertThat(toString).isNotNull();
        assertThat("PropertyValue[ARRAY]{'foo', size='3'}").isEqualTo(toString);
    }
}
