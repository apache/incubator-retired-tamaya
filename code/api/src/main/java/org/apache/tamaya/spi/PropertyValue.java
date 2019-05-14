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

    private static final long serialVersionUID = 2L;
    private static final int EMPTY = 0;
    private static final String SOURCE = "source";
    /** The requested key. */
    private String key;
    /** The createValue. */
    private String value;
    /** The parent value, null if it's a root value. */
    private PropertyValue parent;
    /** The createValue version, used for determining config changes. */
    private AtomicInteger version = new AtomicInteger();
    /** Flag to mark a createValue as immutable. */
    private boolean immutable;
    /** Additional metadata provided by the provider. */
    private final Map<String,String> metaEntries = new HashMap<>();

    /**
     * Enum of the different supported value types.
     */
    public enum ValueType{
        /** A multi valued property value, which contains named child properties. */
        MAP,
        /** A multi valued property value, which contains unnamed child properties. */
        ARRAY,
        /** A simple value property. */
        VALUE
    }


    /**
     * Creates a new (invisible) root, which is a node with an empty name.
     * @return a new empty root, never null.
     */
    public static ObjectValue createObject(){
        return new ObjectValue("");
    }

    /**
     * Creates a new (invisible) root, which is a node with an empty name.
     * @return a new empty root, never null.
     */
    public static ListValue createList(){
        return new ListValue("");
    }

    /**
     * Creates a new createValue of type {@link ValueType#VALUE}.
     * @param key the key, not {@code null}.
     * @param value the createValue, not null.
     * @return a new createValue instance.
     */
    public static PropertyValue createValue(String key, String value){
        return new PropertyValue(key, value);
    }

    /**
     * Creates a new createValue of type {@link ValueType#ARRAY}.
     * @param key the key, not {@code null}.
     * @return a new createValue instance.
     */
    public static ListValue createList(String key){
        return new ListValue(key);
    }

    /**
     * Creates a new createValue of type {@link ValueType#MAP}.
     * @param key the key, not {@code null}.
     * @return a new createValue instance.
     */
    public static ObjectValue createObject(String key){
        return new ObjectValue(key);
    }

    /**
     * Maps a mapProperties of {@code Map<String,String>} to a {@code Map<String,PropertyValue>}.
     * @param config the String based mapProperties, not {@code null}.
     * @param source the source name, not {@code null}.
     * @return the corresponding createValue based mapProperties.
     */
    public static Map<String, PropertyValue> mapProperties(Map<String, String> config, String source) {
        return mapProperties(config, source, null);
    }

    /**
     * Maps a mapProperties of {@code Map<String,String>} to a {@code Map<String,PropertyValue>}.
     *
     * @param config the String based mapProperties, not {@code null}.
     * @param source the source name, not {@code null}.
     * @param metaData additional metadata, not {@code null}.
     * @return the corresponding createValue based mapProperties.
     */
    public static Map<String, PropertyValue> mapProperties(Map<String, String> config, String source,
                                                           Map<String,String> metaData) {
        return mapProperties(config, source, metaData, null);
    }

    /**
     * Maps a mapProperties of {@code Map<String,String>} to a {@code Map<String,PropertyValue>}.
     *
     * @param config the String based mapProperties, not {@code null}.
     * @param source the source name, not {@code null}.
     * @param metaData additional metadata, not {@code null}.
     * @param prefix the prefix to add (optional).
     * @return the corresponding createValue based mapProperties.
     */
    public static Map<String, PropertyValue> mapProperties(Map<String, String> config, String source,
                                                           Map<String,String> metaData, String prefix) {
        Objects.requireNonNull(config, "Config must be given.");

        Map<String, PropertyValue> result = new HashMap<>(config.size());

        for(Map.Entry<String,String> en:config.entrySet()){
            PropertyValue pv = createValue(en.getKey(), en.getValue());
            if(metaData!=null) {
                pv.setMeta(metaData);
            }
            if(source!=null){
                pv.setMeta(SOURCE, source);
            }
            if(prefix==null) {
                result.put(en.getKey(), pv);
            }else{
                result.put(prefix + en.getKey(), pv.setKey(prefix=en.getKey()));
            }
        }
        return result;
    }

    /**
     * Creates a new instance
     * @param key the key, not {@code null}.
     */
    public PropertyValue(String key){
        this(key, null);
    }

    /**
     * Creates a new instance
     * @param key the key, not {@code null}.
     * @param value the initial text createValue.
     */
    protected PropertyValue(String key, String value){
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
    public ValueType getValueType() {
        return ValueType.VALUE;
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
     * Get the source.
     * @return the source, or null.
     * @deprecated Use {@code getMeta("source")}.
     */
    @Deprecated
    public String getSource() {
        return this.metaEntries.get(SOURCE);
    }


    /**
     * Sets the createValue.
     * @param value the createValue
     * @return this value for chaining.
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
     * Get a qualified name of a value in property format using '.' as separator, e.g.
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
     * Get the value's parent.
     * @return the parent, or null.
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
     * Checks if the value is a root value.
     * @return true, if the current value is a root value.
     */
    public final boolean isRoot() {
        return parent == null;
    }

    /**
     * Checks if the value is a leaf value (has no values).
     * @return true, if the current value is a leaf value.
     */
    public final boolean isLeaf(){
        return getValueType()==ValueType.VALUE;
    }

    /**
     * Creates a full configuration map for this key, createValue pair and all its getMeta context data. This map
     * is also used for subsequent processing, like createValue filtering.
     * @return the property createValue entry map.
     */
    public final Map<String, String> getMeta() {
        return Collections.unmodifiableMap(metaEntries);
    }

    /**
     * Access the given key from this createValue. Valid keys are the key or any getMeta-context key.
     * @param key the key, not {@code null}.
     * @return the createValue found, or {@code null}.
     * @deprecated Use {@link #getMeta(String)} instead of.
     */
    @Deprecated
    public String getMetaEntry(String key) {
        return this.metaEntries.get(Objects.requireNonNull(key));
    }

    /**
     * Access the given key from this createValue. Valid keys are the key or any getMeta-context key.
     * @param key the key, not {@code null}.
     * @param <T> the target type.
     * @return the createValue found, or {@code null}.
     */
    public final <T> T getMeta(String key) {
        return (T)this.metaEntries.get(Objects.requireNonNull(key));
    }

    /**
     * Get the createValue's number of elements.
     * @return the getNumChilds of this multi createValue.
     */
    public int getSize() {
        return EMPTY;
    }

    @Override
    public Iterator<PropertyValue> iterator() {
        return Collections.emptyIterator();
    }


    /**
     * Replaces/sets the context data.
     * @param metaEntries the context data to be applied, not {@code null}.
     * @return the builder for chaining.
     * @throws IllegalStateException if the instance is immutable.
     * @see #isImmutable()
     */
    public final PropertyValue setMeta(Map<String, String> metaEntries) {
        checkImmutable();
        if(!Objects.equals(this.metaEntries, metaEntries)) {
            this.metaEntries.clear();
            this.metaEntries.putAll(metaEntries);
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
        Objects.requireNonNull(value, "Meta value must be given.");
        if(!Objects.equals(this.metaEntries.get(key), value.toString())) {
            this.metaEntries.put(key, value.toString());
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
        if(this.metaEntries.containsKey(key)) {
            this.metaEntries.remove(key);
            version.incrementAndGet();
        }
        return this;
    }


    /**
     * Convert the value tree to a property map.
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
     * Convert the value tree to a property mapProperties using local keys.
     * @return the corresponding property mapProperties, not null.
     */
    public Map<String,String> toLocalMap(){
        Map<String, String> map = new TreeMap<>();
        if(value!=null) {
            map.put(getKey(), value);
        }
        return map;
    }


    /**
     * Create a String representation of the tree.
     * @return the corresponding String representation, not null.
     */
    @Override
    public String toString() {
        return String.valueOf(value);
    }

    /**
     * Convert an instance to a Object PropertyValue.
     * @return the list value, never null.
     */
    public ObjectValue toObjectValue(){
        ObjectValue ov = new ObjectValue(getKey());
        ov.setParent(getParent());
        ov.setValue("value", value);
        return ov;
    }

    /**
     * Convert an instance to a List PropertyValue.
     * @return the list value, never null.
     */
    public ListValue toListValue(){
        ListValue lv = new ListValue(getKey());
        lv.setParent(getParent());
        lv.addValue(value);
        return lv;
    }


    /**
     * Creates a deep clone of this intance.
     * @return a clone, never null.
     */
    protected PropertyValue deepClone() {
        PropertyValue newProp = new PropertyValue(getKey(), this.value);
        newProp.setParent(getParent());
        newProp.setMeta(getMeta());
        newProp.setVersion(getVersion());
        newProp.setValue(getValue());
        return newProp;
    }

    /**
     * @throws IllegalStateException if the instance is immutable.
     */
    protected final void checkImmutable(){
        if(immutable){
            throw new IllegalStateException("Instance is immutable.");
        }
    }

    /**
     * Called to mark a change on this instance.
     * @return the new version.
     */
    protected final int incrementVersion(){
        checkImmutable();
        return version.incrementAndGet();
    }

    /**
     * Sets the new version, used iternally when cloning.
     * @param version the new version.
     */
    protected final void setVersion(int version) {
        this.version.set(version);
    }

    /**
     * Changes the entry's key, mapping also corresponding context entries.
     *
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
     * Sets the new parent, used iternally when converting between value types.
     * @param parent the parent value.
     * @return the simple value, never null.
     */
    protected PropertyValue setParent(PropertyValue parent){
        this.parent = parent;
        return this;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PropertyValue)) {
            return false;
        }
        PropertyValue dataNode = (PropertyValue) o;
        return Objects.equals(getKey(), dataNode.getKey()) &&
                Objects.equals(value, dataNode.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getParent(), getKey(), value, getMeta());
    }

}
