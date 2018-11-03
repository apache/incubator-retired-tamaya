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

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

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
public class PropertyValue implements Serializable, Iterable<PropertyValue>{

    private static final long serialVersionUID = 1L;
    /** The type of node. */
    private ValueType valueType;
    /** The requested key. */
    private String key;
    /** The createValue. */
    private String value;
    /** The getParent getField, null if it's a root getField. */
    private PropertyValue parent;
    /** The createValue version, used for determining config changes. */
    private AtomicInteger version = new AtomicInteger();
    /** Flag to mark a createValue as immutable. */
    private boolean immutable;
    /** Additional metadata provided by the provider. */
    private final transient Map<String,Object> metaData = new HashMap<>();

    /**
     * Enum of the different supported value types.
     */
    public enum ValueType{
        /** A multi valued property value, which contains named child properties. */
        OBJECT,
        /** A multi valued property value, which contains unnamed child properties. */
        ARRAY,
        /** A simple value property. */
        VALUE
    }


    /**
     * Creates a new builder instance.
     * @param key the key, not {@code null}.
     * @param source the source, typically the name of the {@link PropertySource}
     *               providing the createValue, not {@code null}.
     * @return a new builder instance.
     * @deprecated Will be removed, use {@link PropertyValue} directly.
     */
    @Deprecated
    public static PropertyValueBuilder builder(String key, String source){
        Objects.requireNonNull(key, "Key must be given.");
        Objects.requireNonNull(source, "Source must be given");

        return new PropertyValueBuilder(key, source);
    }

    /**
     * Creates a new (invisible) root, which is a node with an empty name.
     * @return a new empty root, never null.
     */
    public static ObjectValue createObject(){
        return new ObjectValue(null, "");
    }

    /**
     * Creates a new (invisible) root, which is a node with an empty name.
     * @return a new empty root, never null.
     */
    public static ListValue createList(){
        return new ListValue(null, "");
    }

    /**
     * Creates a new createValue of type {@link ValueType#VALUE}.
     * @param key the key, not {@code null}.
     * @param value the createValue, not null.
     * @return a new createValue instance.
     */
    public static PropertyValue createValue(String key, String value){
        return new PropertyValue(null, key, ValueType.VALUE, value);
    }

    /**
     * Creates a new createValue of type {@link ValueType#ARRAY}.
     * @param key the key, not {@code null}.
     * @return a new createValue instance.
     */
    public static ListValue createList(String key){
        return new ListValue(null, key);
    }

    /**
     * Creates a new createValue of type {@link ValueType#OBJECT}.
     * @param key the key, not {@code null}.
     * @return a new createValue instance.
     */
    public static ObjectValue createObject(String key){
        return new ObjectValue(null, key);
    }

    /**
     * Creates a new builder instance.
     * @param key the key, not {@code null}.
     * @param value the property createValue, not {@code null}.
     * @param source the source, typically the name of the {@link PropertySource}
     *               providing the createValue, not {@code null}.
     * @return a new builder instance.
     */
    @Deprecated
    public static PropertyValue of(String key, String value, String source) {
        Objects.requireNonNull(key);
        if(source!=null) {
            return new PropertyValue(null, key, ValueType.VALUE, value).setMeta("source", source);
        }
        return new PropertyValue(null, key, ValueType.VALUE, value);
    }

    /**
      * Maps a map of {@code Map<String,String>} to a {@code Map<String,PropertyValue>}.
      * @param config the String based map, not {@code null}.
      * @param source the source name, not {@code null}.
      * @return the corresponding createValue based map.
      */
    public static Map<String, PropertyValue> map(Map<String, String> config, String source) {
        Map<String, PropertyValue> result = new HashMap<>(config.size());
        for(Map.Entry<String,String> en:config.entrySet()){
            result.put(en.getKey(), createValue(en.getKey(), en.getValue()).setMeta("source", source));
        }
        return result;
    }

    /**
     * Maps a map of {@code Map<String,String>} to a {@code Map<String,PropertyValue>}.
     *
     * @param config the String based map, not {@code null}.
     * @param source the source name, not {@code null}.
     * @param metaData additional metadata, not {@code null}.
     * @return the corresponding createValue based map.
     */
    public static Map<String, PropertyValue> map(Map<String, String> config, String source,
                                                 Map<String,String> metaData) {
        Objects.requireNonNull(config, "Config must be given.");
        Objects.requireNonNull(metaData, "Meta data must be given.");

        Map<String, PropertyValue> result = new HashMap<>(config.size());

        for(Map.Entry<String,String> en:config.entrySet()){
            PropertyValue pv = createValue(en.getKey(), en.getValue())
                    .setMeta(metaData);
            if(source!=null){
                pv.setMeta("source", source);
            }
            result.put(en.getKey(), pv);
        }
        return result;
    }

    /**
     * Creates a new instance
     * @param key the key, not {@code null}.
     * @param parent the parent.
     * @param valueType the createValue type, not null.
     */
    protected PropertyValue(PropertyValue parent, String key, ValueType valueType){
        this(parent, key, valueType, null);
    }

    /**
     * Creates a new instance
     * @param key the key, not {@code null}.
     * @param parent the parent.
     * @param valueType the createValue type, not null.
     * @param value the initial text createValue.
     */
    protected PropertyValue(PropertyValue parent, String key, ValueType valueType, String value){
        this.parent = parent;
        this.valueType = Objects.requireNonNull(valueType, "ValueType is required.");
        this.key = Objects.requireNonNull(key);
        this.value = value;
    }

    /**
     * Checks if the instance is immutable.
     * @return true, if the instance is immutable.
     */
    public final boolean isImmutable(){
        return immutable;
    }

    /**
     * Sets this instance and also all its direct an indirect children to immutable. Any further changes will throw
     * an {@link IllegalStateException}.
     * @return this instance for chaining.
     */
    public PropertyValue immutable(){
        this.immutable = true;
        return this;
    }

    /**
     * Clones this instance and all it's children, marking as mutable createValue.
     * @return the new createValue clone.
     */
    public PropertyValue mutable(){
        if(!immutable){
            return this;
        }
        return deepClone();
    }

    /**
     * Get the item's current createValue type.
     * @return the createValue type, never null.
     */
    public final ValueType getValueType() {
        return valueType;
    }

    /**
     * The requested key.
     * @return the, key never {@code null}.
     */
    public final String getKey() {
        return key;
    }

    /**
     * Get the node's createValue.
     * @return the createValue, or null.
     */
    public String getValue() {
        return this.value;
    }

    /**
     * Sets the createValue.
     * @param value the createValue
     * @return this getField for chaining.
     * @throws IllegalStateException if the instance is immutable.
     * @see #isImmutable()
     */
    public PropertyValue setValue(String value) {
        checkImmutable();
        if(!Objects.equals(this.value, value)) {
            this.value = value;
            incrementVersion();
        }
        return this;
    }

    /**
     * Get a qualified name of a getField in property format using '.' as separator, e.g.
     * {@code a.b.c} or {@code a.b.c[0]} for indexed entries. Entries hereby also can have multiple
     * levels of indexing, e.g. {@code a[1].b.c[14].d} is a valid option.
     *
     * The qualified key is defined by {@link #getQualifiedKey()} of it's parent concatenated with the key
     * of this node. If there is no parent, or the parent's qualified key is empty only {@link #getKey()}
     * is returned. Additionally if the current values is an indeyed createValue the key is extended by the
     * index in brackets, e.g. {@code [0], [1], ...}. All the subsequent keys are valid qualified keys:
     * <pre>
     *     a
     *     a.b
     *     a[0].b
     *     [0]
     *     a.b[4].c.d[0].[1].any
     * </pre>
     *
     * @return the qualified key, never null..
     */
    public String getQualifiedKey(){
        if(parent==null){
            return key;
        }
        String parentName =  parent.getQualifiedKey();
        if(parent instanceof ListValue){
            return parentName+"["+((ListValue)parent).getIndex(this)+"]";
        }else{
            if(!parentName.isEmpty()){
                parentName+=".";
            }
            return parentName+key;
        }
    }

    /**
     * Get the getField's getParent.
     * @return the getParent, or null.
     */
    public final PropertyValue getParent(){
        return parent;
    }

    /**
     * Get the values version, the version is updated with each change written.
     * @return the version.
     */
    public final int getVersion(){
        return version.get();
    }

    /**
     * Get the source.
     * @return the source, or null.
     * @deprecated Use {@code getMeta("source")}.
     */
    @Deprecated
    public final String getSource() {
        return (String)this.metaData.get("source");
    }


    /**
     * Checks if the getField is a root getField.
     * @return true, if the current getField is a root getField.
     */
    public final boolean isRoot() {
        return parent == null;
    }

    /**
     * Checks if the getField is a leaf getField (has no getList).
     * @return true, if the current getField is a leaf getField.
     */
    public final boolean isLeaf(){
        return getValueType()==ValueType.VALUE;
    }

    /**
     * Creates a full configuration map for this key, createValue pair and all its getMeta context data. This map
     * is also used for subsequent processing, like createValue filtering.
     * @return the property createValue entry map.
     */
    public final Map<String, Object> getMeta() {
        return Collections.unmodifiableMap(metaData);
    }

    /**
     * Access the given key from this createValue. Valid keys are the key or any getMeta-context key.
     * @param key the key, not {@code null}.
     * @return the createValue found, or {@code null}.
     * @deprecated Use {@link #getMeta(String)} instead of.
     */
    @Deprecated
    public final String getMetaEntry(String key) {
        return (String)this.metaData.get(Objects.requireNonNull(key));
    }

    /**
     * Access the given key from this createValue. Valid keys are the key or any getMeta-context key.
     * @param key the key, not {@code null}.
     * @param <T> the target type.
     * @return the createValue found, or {@code null}.
     */
    public final <T> T getMeta(String key) {
        return (T)this.metaData.get(Objects.requireNonNull(key));
    }

    /**
     * Access the given metadata.
     * @param type the type, not {@code null}.
     * @param <T> the target type.
     * @return the createValue found, or {@code null}.
     */
    public final <T> T getMeta(Class<T> type) {
        return (T)this.metaData.get(type.getName());
    }


    /**
     * Get the createValue's number of elements.
     * @return the getNumChilds of this multi createValue.
     */
    public int getSize() {
        return 0;
    }

    @Override
    public Iterator<PropertyValue> iterator() {
        return Collections.emptyIterator();
    }


    /**
     * Changes the entry's key, mapping also corresponding context entries.
     * @param key the new key, not {@code null}.
     * @return the builder for chaining.
     * @throws IllegalStateException if the instance is immutable.
     * @see #isImmutable()
     */
    public PropertyValue setKey(String key) {
        checkImmutable();
        if(!Objects.equals(this.key, key)) {
            this.key = Objects.requireNonNull(key);
            version.incrementAndGet();
        }
        return this;
    }


    /**
     * Replaces/sets the context data.
     * @param metaEntries the context data to be applied, not {@code null}.
     * @return the builder for chaining.
     * @throws IllegalStateException if the instance is immutable.
     * @see #isImmutable()
     */
    public final PropertyValue setMeta(Map<String, Object> metaEntries) {
        checkImmutable();
        if(!Objects.equals(this.metaData, metaEntries)) {
            this.metaData.clear();
            this.metaData.putAll(metaEntries);
            version.incrementAndGet();
        }
        return this;
    }

    /**
     * Add an additional context data information.
     * @param key the context data key, not {@code null}.
     * @param value the context createValue, not {@code null} (will be converted to String).
     * @return the builder for chaining.
     * @throws IllegalStateException if the instance is immutable.
     * @see #isImmutable()
     */
    public final PropertyValue setMeta(String key, Object value) {
        checkImmutable();
        Objects.requireNonNull(key, "Meta key must be given.");
        Objects.requireNonNull(value, "Meta createValue must be given.");
        if(!Objects.equals(this.metaData.get(key), value)) {
            this.metaData.put(key, value);
            version.incrementAndGet();
        }
        return this;
    }

    /**
     * Add an additional context data information.
     * @param type the context data type, used as key, not {@code null}.
     * @param value the context createValue, not {@code null}.
     * @param <T> the target type.
     * @return the builder for chaining.
     * @throws IllegalStateException if the instance is immutable.
     * @see #isImmutable()
     */
    public final <T> PropertyValue setMeta(Class<T> type, T value) {
        checkImmutable();
        Objects.requireNonNull(type, "Meta key must be given.");
        Objects.requireNonNull(value, "Meta createValue must be given.");
        if(!Objects.equals(this.metaData.get(type.toString()), value)) {
            this.metaData.put(type.toString(), value);
            version.incrementAndGet();
        }
        return this;
    }

    /**
     * Add an additional context data information, using the data's class name as key.
     * @param value the context createValue, not {@code null}.
     * @param <T> the target type.
     * @return the builder for chaining.
     * @throws IllegalStateException if the instance is immutable.
     * @see #isImmutable()
     */
    public final <T> PropertyValue setMeta(T value) {
        checkImmutable();
        Objects.requireNonNull(value, "Meta createValue must be given.");
        if(!Objects.equals(this.metaData.get(value.getClass().toString()), value)) {
            this.metaData.put(value.getClass().toString(), value);
            version.incrementAndGet();
        }
        return this;
    }

    /**
     * Removes a getMeta entry.
     * @param key the entry's key, not {@code null}.
     * @return the builder for chaining.
     * @throws IllegalStateException if the instance is immutable.
     * @see #isImmutable()
     */
    public final PropertyValue removeMeta(String key) {
        checkImmutable();
        Objects.requireNonNull(key, "Key must be given.");
        if(this.metaData.containsKey(key)) {
            this.metaData.remove(key);
            version.incrementAndGet();
        }
        return this;
    }

    /**
     * Removes a getMeta entry.
     * @param type the entry's type, not {@code null}.
     * @return the builder for chaining.
     * @throws IllegalStateException if the instance is immutable.
     * @see #isImmutable()
     */
    public final PropertyValue removeMeta(Class type) {
        checkImmutable();
        Objects.requireNonNull(key, "Key must be given.");
        if(this.metaData.containsKey(type.getName())) {
            this.metaData.remove(type.getName());
            version.incrementAndGet();
        }
        return this;
    }

    /**
     * Convert the getField tree to a property map.
     * @return the corresponding property map, not null.
     */
    public Map<String,String> toMap(){
        Map<String, String> map = new TreeMap<>();
        if(value!=null) {
            map.put(getQualifiedKey(), value);
        }
        return map;
    }

    /**
     * Create a String representation of the tree.
     * @return the corresponding String representation, not null.
     */
    public String asString() {
        Map<String, String> map = toMap();
        StringBuilder b = new StringBuilder();
        map.entrySet().forEach(en -> b.append(en.getKey()).append(" = ").append(en.getValue()).append('\n'));
        if(b.length()==0){
            return "<nodata>";
        }
        return b.toString();
    }

    /**
     * Creates a new builder instance based on this item.
     * @return a new builder, never null.
     * @deprecated Use {@link PropertyValue} directly.
     */
    @Deprecated
    public PropertyValueBuilder toBuilder() {
        return new PropertyValueBuilder(this.getKey(), this.getValue())
                .setMeta(this.metaData);
    }

    public PropertyValue toPropertyValue(){
        return this;
    }

    public ObjectValue toObjectValue(){
        ObjectValue ov = new ObjectValue(getParent(),getKey());
        ov.setField("createValue", value);
        return ov;
    }

    public ListValue toListValue(){
        ListValue lv = new ListValue(getParent(),getKey());
        lv.addValue("createValue", value);
        return lv;
    }


    protected PropertyValue deepClone() {
        PropertyValue newProp = new PropertyValue(getParent(), getKey(), ValueType.VALUE, this.value);
        newProp.setMeta(getMeta());
        newProp.setVersion(getVersion());
        return newProp;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PropertyValue)) return false;
        PropertyValue dataNode = (PropertyValue) o;
        return getParent() == dataNode.getParent() &&
                Objects.equals(getKey(), dataNode.getKey()) &&
                Objects.equals(value, dataNode.value) &&
                Objects.equals(getMeta(), dataNode.getMeta());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getParent(), getKey(), value, getMeta());
    }


    @Override
    public String toString() {
        return "PropertyValue{" +
                '\'' +getQualifiedKey() + '\'' +
                (value!=null?", createValue='" + value + '\'':"") +
                (getMeta().isEmpty()?"":", metaData=" + getMeta()) +
                '}';
    }

    protected final void checkImmutable(){
        if(immutable){
            throw new IllegalStateException("Instance is immutable.");
        }
    }

    protected final int incrementVersion(){
        checkImmutable();
        return version.incrementAndGet();
    }

    protected final void setVersion(int version) {
        this.version.set(version);
    }

    protected final void setParent(PropertyValue parent){
        this.parent = parent;
    }


}
