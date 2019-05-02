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
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Class modelling the result of a request for a property value. A property value is basically identified by its key.
 * There might be reasons, where one want to further analyze, which PropertySources provided a value and which not, so
 * it is possible to createObject a PropertyValue with a null value.
 *
 *  A PropertyValue represents an abstract data point in a configuration structure read. PropertyValues actually
 *  represent a tree, with additional functionality for representing data lists/arrays using indexed children
 *  names. This allows to support a full mapping of common document based configuration formats, such as JSON, YAML,
 *  XML and more.
 */
public final class ObjectValue extends PropertyValue{

    private static final long serialVersionUID = 1L;

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
     * Get the item's current value type.
     * @return the value type, never null.
     */
    @Override
    public ValueType getValueType() {
        return ValueType.MAP;
    }

    /**
     * Get the fields of this instance.
     * @return the current fields, never null.
     */
    public Collection<PropertyValue> getValues(){
        return Collections.unmodifiableCollection(this.fields.values());
    }

    /**
     * Get the fields of this instance, filtered with the given predicate.
     * @param predicate the predicate, not null.
     * @return the current fields, never null.
     */
    public Collection<PropertyValue> getValues(Predicate<PropertyValue> predicate){
        return Collections.unmodifiableCollection(this.fields.values().stream()
        .filter(predicate).collect(Collectors.toList()));
    }

    /**
     * Get a single child getValue by name.
     * @param name the child's name, not null.
     * @return the child found, or null.
     * @throws IllegalArgumentException if multiple getValues with the given name are existing (ambigous).
     */
    public PropertyValue getValue(String name){
        return this.fields.get(name);
    }

    /**
     * Get a single child getValue with the given name, creates it if not existing.
     * @param name the child's name, not null.
     * @param valueSupplier the supplier to create a new instance, if no value is present, not null.
     * @param <T> the target type.
     * @return the child found or created, never null.
     * @throws IllegalArgumentException if multiple getValues with the given name are existing (ambigous).
     * @throws IllegalStateException if the instance is immutable.
     * @see #isImmutable()
     */
    public <T extends PropertyValue> T getOrSetValue(String name, Supplier<T> valueSupplier){
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
     * Get the value's number of elements.
     * @return the getNumChilds of this multi value.
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
     * Adds text values to the createObject.
     * @param values the child's values, not null.
     * @return the created values, not null.
     * @throws IllegalStateException if the instance is immutable.
     * @see #isImmutable()
     */
    public Collection<PropertyValue> setValues(Map<String, String> values) {
        checkImmutable();
        List<PropertyValue> result = new ArrayList<>();
        for(Map.Entry<String, String> en:values.entrySet()) {
            PropertyValue val = setValue(en.getKey(), en.getValue());
            result.add(val);
        }
        return result;
    }

    /**
     * Adds another existing node, hereby setting the corresponding parent node.
     * @param value the value, not null
     * @param <T> the value type.
     * @return the value added, not null.
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
     * Sets the given key, value pair.
     * @param k the key, not null.
     * @param v the value, not null.
     * @return the value added, not null.
     */
    public PropertyValue setValue(String k, String v) {
        return set(PropertyValue.createValue(k,v));
    }

    /**
     * Sets the given list value.
     * @param key the key, not null.
     * @return the value added, not null.
     */
    public ListValue setList(String key) {
        return set(PropertyValue.createList(key));
    }

    /**
     * Sets the given object vaöue.
     * @param key the key, not null.
     * @return the value added, not null.
     */
    public ObjectValue setObject(String key) {
        return set(PropertyValue.createObject(key));
    }

    /**
     * Adds a new child getValue, where the getValue is given in '.'-separated property notation,
     * e.g. {@code a.b.c}.
     * @param key the property key, e.g. {@code a.b.c}
     * @param value the property value
     * @return the new leaf-getValue created.
     * @throws IllegalStateException if the instance is immutable.
     * @see #isImmutable()
     */
    public PropertyValue setValueWithCompositeKey(String key, String value) {
        checkImmutable();
        ObjectValue node = this;
        StringTokenizer tokenizer = new StringTokenizer(key, "\\.", false);
        while(tokenizer.hasMoreTokens()){
            String token = tokenizer.nextToken().trim();
            if(tokenizer.hasMoreTokens()) {
                node = node.getOrSetValue(token, () -> PropertyValue.createObject(token));
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
     * @return the value instances created.
     * @throws IllegalStateException if the instance is immutable.
     * @see #isImmutable()
     */
    public Collection<PropertyValue> setValueWithCompositeKey(Map<String,String> values) {
        checkImmutable();
        List<PropertyValue> result = new ArrayList<>();
        for(Map.Entry<String,String> en:values.entrySet()){
            result.add(setValueWithCompositeKey(en.getKey(), en.getValue()));
        }
        return result;
    }

    /**
     * Convert the getValue tree to a property map.
     * @return the corresponding property map, not null.
     */
    @Override
    public Map<String,String> toMap(){
        Map<String, String> map = new TreeMap<>();
        for (PropertyValue n : fields.values()) {
            if (ValueType.VALUE.equals(n.getValueType())) {
                map.put(n.getQualifiedKey(), n.getValue());
            } else {
                for(PropertyValue val:n) {
                    map.putAll(val.toMap());
                }
            }
        }
        return map;
    }


    /**
     * Clones this instance and all it's children, marking as mutable value.
     * @return the new value clone.
     */
    @Override
    public ObjectValue mutable(){
       return (ObjectValue)super.mutable();
    }

    @Override
    protected ObjectValue deepClone(){
        ObjectValue newProp = new ObjectValue(getParent(), getKey());
        newProp.setMeta(getMeta());
        fields.values().forEach(c -> newProp.set(c.deepClone().mutable()));
        newProp.setVersion(getVersion());
        newProp.setValue(getValue());
        return newProp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ObjectValue)) {
            return false;
        }
        ObjectValue dataNode = (ObjectValue) o;
        return Objects.equals(getKey(), dataNode.getKey()) &&
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
                (getValue()!=null?", value='" + getValue() + '\'':"") +
                ", size='" + getSize() + '\'' +
                (getMeta().isEmpty()?"":", metaData=" + getMeta()) +
                '}';
    }

}
