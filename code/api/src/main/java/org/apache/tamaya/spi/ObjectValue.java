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
import java.util.function.Supplier;
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
public final class ObjectValue extends PropertyValue{

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = Logger.getLogger(ObjectValue.class.getName());

    /** List of child properties. */
    private Map<String, PropertyValue> fields = new HashMap<>();


    /**
     * Creates a new instance
     * @param key the key, not {@code null}.
     * @param parent the parent.
     */
    ObjectValue(PropertyValue parent, String key){
        super(parent, key);
    }

    /**
     * Get the item's current createValue type.
     * @return the createValue type, never null.
     */
    public ValueType getValueType() {
        return ValueType.MAP;
    }

    /**
     * Get the fields of this instance.
     * @return the current fields, never null.
     */
    public Map<String, PropertyValue> getFields(){
        return Collections.unmodifiableMap(this.fields);
    }

    /**
     * Get a single child getField by name.
     * @param name the child's name, not null.
     * @return the child found, or null.
     * @throws IllegalArgumentException if multiple getList with the given name are existing (ambigous).
     */
    public PropertyValue getField(String name){
        return this.fields.get(name);
    }

    /**
     * Get a single child getField with the given name, creates it if not existing.
     * @param name the child's name, not null.
     * @param valueSupplier the supplier to create a new instance, if no value is present, not null.
     * @param <T> the target type.
     * @return the child found or created, never null.
     * @throws IllegalArgumentException if multiple getList with the given name are existing (ambigous).
     * @throws IllegalStateException if the instance is immutable.
     * @see #isImmutable()
     */
    public <T extends PropertyValue> T getOrSetField(String name, Supplier<T> valueSupplier){
        T field = (T)this.fields.get(name);
        if(field==null){
            checkImmutable();
            field = valueSupplier.get();
            this.fields.put(name, field);
            incrementVersion();
        }
        return field;
    }

    /**
     * Get the createValue's number of elements.
     * @return the getNumChilds of this multi createValue.
     */
    @Override
    public int getSize() {
        return this.fields.size();
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
        return this;
    }

    @Override
    public ListValue toListValue(){
        ListValue array = new ListValue(getParent(), getKey());
        array.setValue(getValue());
        array.setMeta(getMeta());
        array.setVersion(getVersion());
        int index = 0;
        for(PropertyValue val:fields.values()){
            array.add(val.deepClone());
        }
        return array;
    }


    @Override
    public Iterator<PropertyValue> iterator() {
        return Collections.unmodifiableCollection(this.fields.values()).iterator();
    }

    /**
     * Adds a new nvalue child.
     * @param name the child's name, not null.
     * @param value the value
     * @return the createValue added, not null.
     * @throws IllegalStateException if the instance is immutable.
     * @see #isImmutable()
     */
    public PropertyValue setField(String name, String value){
        return set(new PropertyValue(this, name, value));
    }

    /**
     * Adds text values to the createObject.
     * @param values the child's values, not null.
     * @return the created values, not null.
     * @throws IllegalStateException if the instance is immutable.
     * @see #isImmutable()
     */
    public Collection<PropertyValue> setFields(Map<String, String> values) {
        checkImmutable();
        List<PropertyValue> result = new ArrayList<>();
        for(Map.Entry<String, String> en:values.entrySet()) {
            result.add(new PropertyValue(this, en.getKey(), en.getValue()));
        }
        return result;
    }

    /**
     * Adds a new non-indexed child getField.
     * @param name the child's name, not null.
     * @return the createValue added, not null.
     * @throws IllegalStateException if the instance is immutable.
     * @see #isImmutable()
     */
    public ListValue setFieldList(String name){
        return set(new ListValue(this, name));
    }

    /**
     * Adds a new non-indexed child getField.
     * @param name the child's name, not null.
     * @return the createValue added, not null.
     * @throws IllegalStateException if the instance is immutable.
     * @see #isImmutable()
     */
    public ObjectValue setFieldObject(String name){
        return set(new ObjectValue(this, name));
    }


    /**
     * Adds another existing node, hereby setting the corresponding parent node.
     * @param value the createValue, not null
     * @param <T> the value type.
     * @return the createValue added, not null.
     * @throws IllegalStateException if the instance is immutable.
     * @see #isImmutable()
     */
    public <T extends PropertyValue> T set(T value) {
        checkImmutable();
        value.setParent(this);
        this.fields.put(value.getKey(), value);
        return value;
    }

    /**
     * Adds a new child getField, where the getField is given in '.'-separated property notation,
     * e.g. {@code a.b.c}.
     * @param key the property key, e.g. {@code a.b.c}
     * @param value the property createValue
     * @return the new leaf-getField created.
     * @throws IllegalStateException if the instance is immutable.
     * @see #isImmutable()
     */
    public PropertyValue setFieldWithCompositeKey(String key, String value) {
        checkImmutable();
        ObjectValue node = this;
        StringTokenizer tokenizer = new StringTokenizer(key, "\\.", false);
        while(tokenizer.hasMoreTokens()){
            String token = tokenizer.nextToken().trim();
            if(tokenizer.hasMoreTokens()) {
                node = node.getOrSetField(token, () -> PropertyValue.createObject(token));
            }else{
                return node.set(PropertyValue.createValue(token, value));
            }
        }
        return null;
    }

    /**
     * Adds multiple values, where the keys are given in '.'-separated property notation,
     * e.g. {@code a.b.c}.
     * @param values the values, not null.
     * @return the createValue instances created.
     * @throws IllegalStateException if the instance is immutable.
     * @see #isImmutable()
     */
    public Collection<PropertyValue> setFielsWithCompositeKey(Map<String,String> values) {
        checkImmutable();
        List<PropertyValue> result = new ArrayList<>();
        for(Map.Entry<String,String> en:values.entrySet()){
            result.add(setFieldWithCompositeKey(en.getKey(), en.getValue()));
        }
        return result;
    }

    /**
     * Convert the getField tree to a property map.
     * @return the corresponding property map, not null.
     */
    @Override
    public Map<String,String> toMap(){
        Map<String, String> map = new TreeMap<>();
        for (PropertyValue n : fields.values()) {
            switch(n.getValueType()){
                case VALUE:
                    map.put(n.getQualifiedKey(), n.getValue());
                    break;
                default:
                    for(PropertyValue val:n) {
                        map.putAll(val.toMap());
                    }
            }
        }
        return map;
    }


    /**
     * Clones this instance and all it's children, marking as mutable createValue.
     * @return the new createValue clone.
     */
    @Override
    public ObjectValue mutable(){
       return (ObjectValue)super.mutable();
    }

    @Override
    protected ObjectValue deepClone(){
        ObjectValue newProp = new ObjectValue(getParent(), getKey());
        newProp.setMeta(getMeta());
        fields.values().forEach(c -> newProp.set(c.mutable()));
        newProp.setVersion(getVersion());
        return newProp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ObjectValue)) return false;
        ObjectValue dataNode = (ObjectValue) o;
        return getParent() == dataNode.getParent() &&
                Objects.equals(getKey(), dataNode.getKey()) &&
                Objects.equals(fields, dataNode.fields) &&
                Objects.equals(getMeta(), dataNode.getMeta());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getParent(), getKey(), fields, getMeta());
    }


    @Override
    public String toString() {
        return "PropertyValue[MAP]{" +
                '\'' +getQualifiedKey() + '\'' +
                (getValue()!=null?", createValue='" + getValue() + '\'':"") +
                ", size='" + getSize() + '\'' +
                (getMeta().isEmpty()?"":", metaData=" + getMeta()) +
                '}';
    }

}
