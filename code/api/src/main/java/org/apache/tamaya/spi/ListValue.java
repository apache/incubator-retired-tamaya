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

import java.util.*;

/**
 * Class modelling the result of a request for a property createValue. A property createValue is basically identified by its key.
 * There might be reasons, where one want to further analyze, which PropertySources provided a createValue and which not, so
 * it is possible to createObject a PropertyValue with a null createValue.
 *
 *  A PropertyValue represents an abstract data point in a configuration structure read. PropertyValues actually
 *  represent a tree, with additional functionality for representing data lists/arrays using indexed children
 *  names. This allows to support a full mapping of common document based configuration formats, such as JSON, YAML,
 *  XML and more.
 */
public final class ListValue extends PropertyValue{

    private static final long serialVersionUID = 1L;

    /** List of child properties. */
    private List<PropertyValue> list = new ArrayList<>();

    /**
     * Creates a new instance
     * @param key the key, not {@code null}.
     */
    ListValue(String key){
        super(key);
    }

    /**
     * Get the item's current createValue type.
     * @return the createValue type, never null.
     */
    public ValueType getValueType() {
        return ValueType.ARRAY;
    }

    /**
     * Get the index of the given member value.
     * @param member the member, not null.
     * @return the index, or -1.
     */
    public int getIndex(PropertyValue member) {
        return this.list.indexOf(member);
    }

    /**
     * Get the createValue's number of elements.
     * @return the getNumChilds of this multi createValue.
     */
    @Override
    public int getSize() {
        return this.list.size();
    }

    @Override
    public Iterator<PropertyValue> iterator() {
        return Collections.unmodifiableList(this.list).iterator();
    }

    /**
     * Adds a createValue to the array.
     * @param value the createValue, not null
     * @return this instance, for chaining.
     * @throws IllegalStateException if the instance is immutable.
     * @see #isImmutable()
     */
    public ListValue addPropertyValue(PropertyValue value) {
        checkImmutable();
        if(!this.list.stream().filter(p -> p==value).findAny().isPresent()){
            value.setKey(generateListKey());
            value.setParent(this);
            this.list.add(value);
        }
        return this;
    }

    /**
     * Adds an anonymous text value to the array.
     * @param value the child's value, not null.
     * @return the created value, not null.
     * @throws IllegalStateException if the instance is immutable.
     * @see #isImmutable()
     */
    public ListValue addValue(String value) {
        return addPropertyValue(new PropertyValue(generateListKey(), value));
    }

    /**
     * Adds text values to the array.
     * @param values the child's values, not null.
     * @return the created values, not null.
     * @throws IllegalStateException if the instance is immutable.
     * @see #isImmutable()
     */
    public ListValue addValues(String... values) {
        List<PropertyValue> result = new ArrayList<>();
        for(String val:values) {
            result.add(addPropertyValue(new PropertyValue(generateListKey(), val)));
        }
        return this;
    }

    /**
     * Adds an anonymous child createObject to the array.
     * @return the created createObject, not null.
     * @throws IllegalStateException if the instance is immutable.
     * @see #isImmutable()
     */
    public ObjectValue addObject() {
        ObjectValue ov = new ObjectValue(generateListKey());
        addPropertyValue(ov);
        return ov;
    }

    /**
     * Adds an anonymous array.
     * @return this instance, for chaining.
     * @throws IllegalStateException if the instance is immutable.
     * @see #isImmutable()
     */
    public ListValue addList() {
        ListValue lv = new ListValue(generateListKey());
        addPropertyValue(lv);
        return lv;
    }

    /**
     * Get a String value with the given key, if possible. If a node is present, but no value is setPropertyValue, then the
     * node's {@code toString()} method is used as a result.
     * @param index the target index.
     * @return the value found, or null.
     */
    public String getValue(int index) {
        PropertyValue value = getPropertyValue(index);
        if(value!=null){
            if(value.getValueType()==ValueType.VALUE) {
                return value.getValue();
            }
            return value.toString();
        }
        return null;
    }

    /**
     * Get the n-th element of the children.
     * @param n the index.
     * @return the element found
     * @throws NoSuchElementException if no such element exists.
     */
    public PropertyValue getPropertyValue(int n) {
        return this.list.get(n);
    }


    /**
     * Get the array elements, filtered by the given predicate.
     * @param name the name of the objects, null selects all.
     * @return this values matching, never null.
     */
    public List<ObjectValue> getObjects(String name) {
        List<ObjectValue> result = new ArrayList<>();
        this.list.forEach(el -> {
            if (el instanceof ObjectValue && name.equals(el.getKey())) {
                result.add((ObjectValue) el);
            }
        });
        return result;
    }

    /**
     * Get the array elements, filtered by the given name.
     * @param name the name of the objects, null selects all.
     * @return this values matching, never null.
     */
    public List<ListValue> getLists(String name) {
        List<ListValue> result = new ArrayList<>();
        this.list.forEach(el -> {
                if (el instanceof ListValue && name.equals(el.getKey())) {
                    result.add((ListValue) el);
                }
            });
        return result;
    }

    /**
     * Get all array elements of type {@link ListValue}.
     * @return this values matching, never null.
     */
    public List<ListValue> getLists() {
        List<ListValue> result = new ArrayList<>();
            this.list.forEach(el -> {
                if (el instanceof ListValue) {
                    result.add((ListValue) el);
                }
            });
        return result;
    }

    /**
     * Get the text elements, filtered by the given name.
     * @param name the name of the objects, null selects all.
     * @return this values matching, never null.
     */
    public List<PropertyValue> getPropertyValues(String name) {
        List<PropertyValue> result = new ArrayList<>();
        if (name == null) {
            result.addAll(this.list);
        } else {
            this.list.forEach(el -> {
                if (name.equals(el.getKey())) {
                    result.add(el);
                }
            });
        }
        return result;
    }

    @Override
    public ObjectValue toObjectValue(){
        ObjectValue object = new ObjectValue(getKey());
        object.setParent(getParent());
        object.setMeta(getMeta());
        object.setVersion(getVersion());
        int index = 0;
        for(PropertyValue val: list){
            object.setPropertyValue(val.deepClone().setKey(val.getKey()));
            index++;
        }
        return object;
    }

    @Override
    public ListValue toListValue(){
        return this;
    }

    /**
     * Get the node's createValue.
     * @return the createValue, or null.
     */
    public String getValue() {
        return "List: " + this.list;
    }

    @Override
    public PropertyValue setValue(String value) {
        throw new UnsupportedOperationException("Cannot set value on list value.");
    }

    /**
     * Clones this instance and all it's children, marking as mutable value.
     * @return the new value clone.
     */
    @Override
    public ListValue mutable(){
       return (ListValue)super.mutable();
    }

    @Override
    protected ListValue deepClone(){
        ListValue newProp = new ListValue(getKey());
        newProp.setParent(getParent());
        newProp.setMeta(getMeta());
        list.forEach(c -> newProp.addPropertyValue(c.deepClone().mutable()));
        newProp.setVersion(getVersion());
        return newProp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ListValue)) {
            return false;
        }
        ListValue dataNode = (ListValue) o;
        return Objects.equals(getKey(), dataNode.getKey()) &&
                Objects.equals(getValue(), dataNode.getValue()) &&
                Objects.equals(list, dataNode.list) &&
                Objects.equals(getMeta(), dataNode.getMeta());
    }

    private String generateListKey(){
        return "["+this.list.size()+"]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKey(), list, getValue(), getMeta());
    }


    @Override
    public String toString() {
        return "List{" +
                ", size='" + getSize() + '\'' +
                ", values=" + list +
                (getMeta().isEmpty()?"":", metaData=" + getMeta()) +
                '}';
    }

    /**
     * Merges multiple values into one single node.
     * @param values the values to merge, not null.
     * @return the merged instance, or null.
     */
    public static ListValue from(Collection<PropertyValue> values) {
        ListValue merged = PropertyValue.createList();
        for(PropertyValue val:values){
            merged.addPropertyValue(val);
        }
        return merged;
    }

}
