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
        PropertyValue val = new PropertyValue(  "k", "v");
        lv.addPropertyValue(val);
        PropertyValue val2 = new PropertyValue(  "k2", "v2");
        lv.addPropertyValue(val2);
        assertThat(0).isEqualTo(lv.getIndex(val));
        assertThat(1).isEqualTo(lv.getIndex(val2));
        PropertyValue val3 = new PropertyValue(  "k3", "v");
        assertThat(-1).isEqualTo(lv.getIndex(val3));
    }

    @Test
    public void getLists() {
        ListValue lv = PropertyValue.createList();
        PropertyValue val = PropertyValue.createList("k");
        lv.addPropertyValue(val);
        PropertyValue val2 = PropertyValue.createObject("k2");
        lv.addPropertyValue(val2);
        assertThat(lv.getLists()).isNotEmpty().hasSize(1);
    }

    @Test
    public void getObjects() {
        ListValue lv = PropertyValue.createList();
        PropertyValue val = PropertyValue.createObject("k");
        lv.addPropertyValue(val);
        PropertyValue val2 = PropertyValue.createObject("k2");
        lv.addPropertyValue(val2);
        assertThat(lv.getObjects("[0]")).isNotEmpty().hasSize(1);
        assertThat(lv.getObjects("[1]")).isNotEmpty().hasSize(1);
        assertThat(lv.getObjects("foo")).isEmpty();
    }

    @Test
    public void getStringValue() {
        ListValue lv = PropertyValue.createList();
        PropertyValue val = PropertyValue.createObject("k").setValue("k1", "v1");
        lv.addPropertyValue(val);
        assertThat(lv.getValue(0)).isNotNull();
        assertThat(lv.getPropertyValue(0).toObjectValue().getValue("k1")).isEqualTo("v1");
    }

    @Test
    public void getSize() {
        ListValue lv = PropertyValue.createList();
        assertThat(0).isEqualTo(lv.getSize());
        PropertyValue val = new PropertyValue(  "k", "v");
        lv.addPropertyValue(val);
        assertThat(1).isEqualTo(lv.getSize());
        PropertyValue val2 = new PropertyValue(  "k", "v");
        lv.addPropertyValue(val2);
        assertThat(2).isEqualTo(lv.getSize());
    }

    @Test
    public void getList() {
        ListValue lv = PropertyValue.createList();
        PropertyValue val = new PropertyValue(  "k", "v");
        lv.addPropertyValue(val);
        PropertyValue val2 = new PropertyValue(  "k", "v");
        lv.addPropertyValue(val2);
        assertThat(lv.getSize()).isEqualTo(2);
        assertThat(val).isEqualTo(lv.getPropertyValue(0));
        assertThat(val2).isEqualTo(lv.getPropertyValue(1));
        lv.addPropertyValue(val2);
        assertThat(lv.getSize()).isEqualTo(2);
        assertThat(val).isEqualTo(lv.getPropertyValue(0));
        assertThat(val2).isEqualTo(lv.getPropertyValue(1));
    }

    @Test
    public void iterator() {
        ListValue lv = PropertyValue.createList();
        PropertyValue val = new PropertyValue(  "k", "v");
        PropertyValue val2 = new PropertyValue(  "k", "v");
        lv.addPropertyValue(val);
        lv.addPropertyValue(val2);
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
        PropertyValue val = new PropertyValue(  "k", "v");
        lv.addPropertyValue(val);
        lv.addPropertyValue(val);
        lv.addPropertyValue(val);
        assertThat(1).isEqualTo(lv.getSize());
        assertThat(val).isEqualTo(lv.getPropertyValue(0));

    }

    @Test
    public void addValue_Value() {
        ListValue lv = PropertyValue.createList();
        lv.addValue("v");
        assertThat(1).isEqualTo(lv.getSize());
        assertThat("v").isEqualTo(lv.getPropertyValue(0).getValue());
        lv.addValue("v2");
        assertThat(2).isEqualTo(lv.getSize());
        assertThat("v2").isEqualTo(lv.getPropertyValue(1).getValue());
        lv.addValue("v");
        assertThat(3).isEqualTo(lv.getSize());
        assertThat("v").isEqualTo(lv.getPropertyValue(2).getValue());
    }

    @Test
    public void addValue_KeyValue() {
        ListValue lv = PropertyValue.createList();
        lv.addValue("v");
        assertThat(1).isEqualTo(lv.getSize());
        assertThat("v").isEqualTo(lv.getPropertyValue(0).getValue());
        assertThat("[0]").isEqualTo(lv.getPropertyValue(0).getKey());
        lv.addValue("v2");
        assertThat(2).isEqualTo(lv.getSize());
        assertThat("v2").isEqualTo(lv.getPropertyValue(1).getValue());
        assertThat("[1]").isEqualTo(lv.getPropertyValue(1).getKey());
        lv.addValue("v");
        assertThat(3).isEqualTo(lv.getSize());
        assertThat("v").isEqualTo(lv.getPropertyValue(2).getValue());
        assertThat("[2]").isEqualTo(lv.getPropertyValue(2).getKey());
    }

    @Test
    public void addValues() {
        ListValue lv = PropertyValue.createList();
        lv.addValues("v", "v1", "v");
        assertThat(3).isEqualTo(lv.getSize());
        assertThat("v").isEqualTo(lv.getPropertyValue(0).getValue());
        assertThat("v1").isEqualTo(lv.getPropertyValue(1).getValue());
        assertThat("v").isEqualTo(lv.getPropertyValue(2).getValue());
    }

    @Test
    public void addObject() {
        ListValue lv = PropertyValue.createList();
        lv.addObject();
        assertThat(1).isEqualTo(lv.getSize());
        ObjectValue ov = (ObjectValue)lv.getPropertyValue(0);
    }

    @Test
    public void addList() {
        ListValue lv = PropertyValue.createList();
        lv.addList();
        assertThat(1).isEqualTo(lv.getSize());
        ListValue ov = (ListValue)lv.getPropertyValue(0);
        assertThat("[0]").isEqualTo(ov.getKey());
    }

    @Test
    public void getValues() {
        ListValue lv = PropertyValue.createList();
        lv.addList();
        lv.addObject();
        assertThat(lv.getPropertyValues("")).isNotNull().hasSize(0);
        assertThat(lv.getPropertyValues("[0]")).hasSize(1);
        assertThat(lv.getPropertyValues("[1]")).hasSize(1);
    }

    @Test
    public void toObjectValue() {
        ListValue lv = PropertyValue.createList("foo");
        lv.addList().addValue("a");
        ObjectValue ov = lv.toObjectValue();
        assertThat(ov).isNotNull();
        assertThat(ov.getKey()).isEqualTo(lv.getKey());
        assertThat(ov.getValue("[0]")).isNotNull();
        assertThat(ov.getValue("[0]")).isEqualTo("List: [a]");
    }

    @Test
    public void toListValue() {
        ListValue lv = PropertyValue.createList("foo");
        lv.addList();
        ListValue lv2 = lv.toListValue();
        assertThat(lv == lv2).isTrue();
    }

    @Test
    public void mutable() {
        ListValue lv = PropertyValue.createList("foo");
        lv.addList();
        assertThat(lv.isImmutable()).isFalse();
        ListValue lv2 = lv.mutable();
        assertThat(lv2.isImmutable()).isFalse();
        assertThat(lv == lv2).isTrue();
    }

    @Test
    public void deepClone() {
        ListValue lv1 = PropertyValue.createList("foo");
        lv1.addList();
        ListValue lv2 = lv1.deepClone();
        assertThat(lv1.getMeta()!=lv2.getMeta()).isTrue();
        assertThat(lv1.equals(lv2)).isTrue();
        assertThat(lv1.iterator().next()!=lv2.iterator().next()).isTrue();
    }

    @Test
    public void equals() {
        ListValue lv1 = PropertyValue.createList("foo");
        lv1.addList();
        ListValue lv2 = PropertyValue.createList("foo");
        lv2.addList();
        assertThat(lv1.equals(lv2)).isTrue();
    }

    @Test
    public void testHashCode() {
        ListValue lv1 = PropertyValue.createList("foo");
        lv1.addList();
        ListValue lv2 = PropertyValue.createList("foo");
        lv2.addList();
        assertThat(lv1.hashCode() == lv2.hashCode()).isTrue();
    }

    @Test
    public void testToString() {
        ListValue lv1 = PropertyValue.createList("foo");
        String toString = lv1.toString();
        assertThat(toString).isNotNull();
        lv1.addList();
        toString = lv1.toString();
        assertThat(toString).isNotNull();
        lv1.addObject();
        toString = lv1.toString();
        assertThat(toString).isNotNull();
        lv1.addValue("valueKey");
        toString = lv1.toString();
        assertThat(toString).isNotNull();
        assertThat("List{, size='3', values=[List{, size='0', values=[]}, Object{size='0', values='{}}, " +
                "valueKey]}").isEqualTo(toString);
    }
}
