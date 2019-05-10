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
     */
    ObjectValue( String key){
        super(key);
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
     * Access the current present field names/keys.
     * @return the keys present, never null.
     */
    public Set<String> getKeys() {
        return Collections.unmodifiableSet(this.fields.keySet());
    }

    /**
     * Get a single child getValue by name.
     * @param name the child's name, not null.
     * @return the child found, or null.
     * @throws IllegalArgumentException if multiple getPropertyValues with the given name are existing (ambigous).
     */
    public PropertyValue getPropertyValue(String name){
        return this.fields.get(name);
    }

    /**
     * Get the node's createValue.
     * @return the createValue, or null.
     */
    public String getValue() {
        return "Map: " + this.fields;
    }

    @Override
    public PropertyValue setValue(String value) {
        throw new UnsupportedOperationException("Cannot set value on object value.");
    }

    /**
     * Get a String value with the given key, if possible. If a node is present, but no value is setPropertyValue, then the
     * node's {@code toString()} method is used as a result.
     * @param key the key, not null.
     * @return the value found, or null.
     */
    public String getValue(String key) {
        String result = null;
        PropertyValue value = getPropertyValue(key);
        if(value!=null){
            result = value.getValue();
            if(result==null){
                result = value.toString();
            }
        }
        return result;
    }

    /**
     * Get a single child getValue with the given name, creates it if not existing.
     * @param name the child's name, not null.
     * @param valueSupplier the supplier to create a new instance, if no value is present, not null.
     * @param <T> the target type.
     * @return the child found or created, never null.
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

    /**
     * Applies a mapProperties of {@code Map<String,String>} to this instance as values.
     * @param config the String based mapProperties, not {@code null}.
     * @return the corresponding createValue based mapProperties.
     */
    public ObjectValue setValues(Map<String, String> config) {
        return setValues(config, null, true);
    }

    /**
     * Applies a mapProperties of {@code Map<String,String>} to this instance as values.
     * @param config the String based mapProperties, not {@code null}.
     * @param source the source name, optional.
     * @param overwriteExisting if true, existing values will be overridden.
     * @return the corresponding createValue based mapProperties.
     */
    public ObjectValue setValues(Map<String, String> config, String source, boolean overwriteExisting) {
        checkImmutable();
        Map<String, PropertyValue> result = PropertyValue.mapProperties(config, source);
        for(Map.Entry<String,String> en:config.entrySet()){
            PropertyValue value = new PropertyValue( en.getKey(), en.getValue());
            value.setParent(this);
            if(source!=null) {
                value.setMeta("source", source);
            }
            if(overwriteExisting){
                this.fields.put(en.getKey(), value);
            }else{
                this.fields.putIfAbsent(en.getKey(), value);
            }
        }
        return this;
    }

    @Override
    public ObjectValue toObjectValue(){
        return this;
    }

    @Override
    public ListValue toListValue(){
        ListValue array = new ListValue(getKey());
        array.setParent(getParent());
        array.setMeta(getMeta());
        array.setVersion(getVersion());
        for(PropertyValue val:fields.values()){
            array.addPropertyValue(val.deepClone());
        }
        return array;
    }


    @Override
    public Iterator<PropertyValue> iterator() {
        return Collections.unmodifiableCollection(this.fields.values()).iterator();
    }


    /**
     * Adds another existing node, hereby setting the corresponding parent node.
     * @param value the value, not null
     * @return the value added, not null.
     * @throws IllegalStateException if the instance is immutable.
     * @see #isImmutable()
     */
    protected ObjectValue setPropertyValue(PropertyValue value) {
        checkImmutable();
        value.setParent(this);
        this.fields.put(value.getKey(), value);
        return this;
    }

    /**
     * Sets the given key, value pair.
     * @param k the key, not null.
     * @param v the value, not null.
     * @return the value added, not null.
     */
    public ObjectValue setValue(String k, String v) {
        return setPropertyValue(new PropertyValue(k,v));
    }

    /**
     * Sets the given list value.
     * @param key the key, not null.
     * @return the value added, not null.
     */
    public ListValue addList(String key) {
        ListValue lv = PropertyValue.createList(key);
        setPropertyValue(lv);
        return lv;
    }

    /**
     * Sets the given object vaöue.
     * @param key the key, not null.
     * @return the value added, not null.
     */
    public ObjectValue addObject(String key) {
        ObjectValue ov = PropertyValue.createObject(key);
        setPropertyValue(ov);
        return ov;
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
                return node.setPropertyValue(new PropertyValue(token, value));
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
     * Convert the value tree to a property map.
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
     * Convert the value tree to a local property map.
     * @return the corresponding local map, not null.
     */
    @Override
    public Map<String,String> toLocalMap(){
        Map<String, String> map = new TreeMap<>();
        for (PropertyValue n : fields.values()) {
            switch(n.getValueType()){
                case VALUE:
                    map.put(n.getKey(), n.getValue());
                    break;
                default:
                    for(PropertyValue val:n) {
                        Map<String,String> valueMap = val.toLocalMap();
                        map.putAll(valueMap);
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
        ObjectValue newProp = new ObjectValue(getKey());
        newProp.setParent(getParent());
        newProp.setMeta(getMeta());
        fields.values().forEach(c -> newProp.setPropertyValue(c.deepClone().mutable()));
        newProp.setVersion(getVersion());
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
        return "Object{" +
                "size='" + getSize() + '\'' +
                ", values='" + toLocalMap() +
                (getMeta().isEmpty()?"":", metaData=" + getMeta()) +
                '}';
    }

    /**
     * Merges multiple values into one single node.
     * @param values the values to merge, not null.
     * @return the merged instance, or null.
     */
    public static ObjectValue from(Collection<PropertyValue> values) {
        if(values.size()==1){
            return values.iterator().next().toObjectValue();
        }
        ObjectValue merged = PropertyValue.createObject();
        for(PropertyValue val:values){
            merged.setPropertyValue(val);
        }
        return merged;
    }

}
