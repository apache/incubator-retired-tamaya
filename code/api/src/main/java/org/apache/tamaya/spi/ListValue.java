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
import java.util.function.Predicate;
import java.util.logging.Logger;

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
     * @param parent the parent.
     */
    ListValue(PropertyValue parent, String key){
        super(parent, key);
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

    /**
     * The createValue.
     * @return the createValue, in case a createValue is null it is valid to return {#code null} as result for
     * {@link PropertySource#get(String)}.
     */
    public List<PropertyValue> getList() {
        return Collections.unmodifiableList(this.list);
    }

    @Override
    public Iterator<PropertyValue> iterator() {
        return Collections.unmodifiableList(this.list).iterator();
    }

    /**
     * Adds a createValue to the array.
     * @param value the createValue, not null
     * @param <T> the instance type.
     * @return this instance, for chaining.
     * @throws IllegalStateException if the instance is immutable.
     * @see #isImmutable()
     */
    public <T extends PropertyValue> T add(T value) {
        checkImmutable();
        value.setParent(this);
        this.list.add(value);
        return value;
    }

    /**
     * Adds an named text value to the array.
     * @param key the child's key, not null.
     * @param value the child's value, not null.
     * @return the created value, not null.
     * @throws IllegalStateException if the instance is immutable.
     * @see #isImmutable()
     */
    public PropertyValue addValue(String key, String value) {
        return add(new PropertyValue(this,key, value));
    }

    /**
     * Adds an anonymous text value to the array.
     * @param value the child's value, not null.
     * @return the created value, not null.
     * @throws IllegalStateException if the instance is immutable.
     * @see #isImmutable()
     */
    public PropertyValue addValue(String value) {
        return add(new PropertyValue(this,"", value));
    }

    /**
     * Adds text values to the array.
     * @param values the child's values, not null.
     * @return the created values, not null.
     * @throws IllegalStateException if the instance is immutable.
     * @see #isImmutable()
     */
    public List<PropertyValue> addValues(String... values) {
        List<PropertyValue> result = new ArrayList<>();
        for(String val:values) {
            result.add(add(new PropertyValue(this, "", val)));
        }
        return result;
    }

    /**
     * Adds an anonymous child createObject to the array.
     * @return the created createObject, not null.
     * @throws IllegalStateException if the instance is immutable.
     * @see #isImmutable()
     */
    public ObjectValue addObject() {
        return add(new ObjectValue(this, ""));
    }

    /**
     * Adds a child createObject to the array.
     * @param name the child's name, not null.
     * @return the created createObject, not null.
     * @throws IllegalStateException if the instance is immutable.
     * @see #isImmutable()
     */
    public ObjectValue addObject(String name) {
        return add(new ObjectValue(this,name));
    }

    /**
     * Adds an anonymous array.
     * @return this instance, for chaining.
     * @throws IllegalStateException if the instance is immutable.
     * @see #isImmutable()
     */
    public ListValue addList() {
        return add(new ListValue(this, ""));
    }

    /**
     * Adds a named array.
     * @param name the child's name, not null.
     * @return this instance, for chaining.
     * @throws IllegalStateException if the instance is immutable.
     * @see #isImmutable()
     */
    public ListValue addList(String name) {
        return add(new ListValue(this,name));
    }

    /**
     * Get the array elements, filtered by the given predicate.
     * @param filter the filter predicate, null selects all elements.
     * @return this values matching, never null.
     */
    public List<PropertyValue> getList(Predicate<PropertyValue> filter) {
        List<PropertyValue> result = new ArrayList<>();
        if(filter==null){
            result.addAll(this.list);
        }else {
            this.list.forEach(el -> {
                if (filter.test(el)) result.add(el);
            });
        }
        return result;
    }

    /**
     * Get the array elements, filtered by the given predicate.
     * @param name the name of the objects, null selects all.
     * @return this values matching, never null.
     */
    public List<ObjectValue> getObjects(String name) {
        List<ObjectValue> result = new ArrayList<>();
        if (name == null) {
            this.list.forEach(el -> {
                if (el instanceof ObjectValue) {
                    result.add((ObjectValue) el);
                }
            });
        } else {
            this.list.forEach(el -> {
                if (el instanceof ObjectValue && name.equals(el.getKey())) {
                    result.add((ObjectValue) el);
                }
            });
        }
        return result;
    }

    /**
     * Get the array elements, filtered by the given name.
     * @param name the name of the objects, null selects all.
     * @return this values matching, never null.
     */
    public List<ListValue> getLists(String name) {
        List<ListValue> result = new ArrayList<>();
        if (name == null) {
            this.list.forEach(el -> {
                if (el instanceof ListValue) {
                    result.add((ListValue) el);
                }
            });
        } else {
            this.list.forEach(el -> {
                if (el instanceof ListValue && name.equals(el.getKey())) {
                    result.add((ListValue) el);
                }
            });
        }
        return result;
    }

    /**
     * Get the text elements, filtered by the given name.
     * @param name the name of the objects, null selects all.
     * @return this values matching, never null.
     */
    public List<PropertyValue> getValues(String name) {
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
    public PropertyValue toPropertyValue(){
        PropertyValue value = new PropertyValue(getParent(), getKey(), getValue());
        value.setMeta(getMeta());
        value.setVersion(getVersion());
        return value;
    }

    @Override
    public ObjectValue toObjectValue(){
        ObjectValue object = new ObjectValue(getParent(), getKey());
        object.setMeta(getMeta());
        object.setVersion(getVersion());
        int index = 0;
        for(PropertyValue val: list){
            object.set(val.deepClone().setKey("["+index+"]"));
            index++;
        }
        return object;
    }

    @Override
    public ListValue toListValue(){
        return this;
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
        ListValue newProp = new ListValue(getParent(), getKey());
        newProp.setMeta(getMeta());
        list.forEach(c -> newProp.add(c.mutable()));
        newProp.setVersion(getVersion());
        return newProp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ListValue)) return false;
        ListValue dataNode = (ListValue) o;
        return getParent() == dataNode.getParent() &&
                Objects.equals(getKey(), dataNode.getKey()) &&
                Objects.equals(getValue(), dataNode.getValue()) &&
                Objects.equals(list, dataNode.list) &&
                Objects.equals(getMeta(), dataNode.getMeta());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getParent(), getKey(), list, getValue(), getMeta());
    }


    @Override
    public String toString() {
        return "PropertyValue[ARRAY]{" +
                '\'' +getQualifiedKey() + '\'' +
                (getValue()!=null?", value='" + getValue() + '\'':"") +
                ", size='" + getSize() + '\'' +
                (getMeta().isEmpty()?"":", metaData=" + getMeta()) +
                '}';
    }

}
