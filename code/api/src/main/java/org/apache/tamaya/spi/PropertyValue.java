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
import java.util.stream.Collectors;

/**
 * Class modelling the result of a request for a property value. A property value is basically identified by its key.
 * There might be reasons, where one want to further analyze, which PropertySources provided a value and which not, so
 * it is possible to create a PropertyValue with a null value.
 *
 *  A PropertyValue represents an abstract data point in a configuration structure read. PropertyValues actually
 *  represent a tree, with additional functionality for representing data lists/arrays using indexed children
 *  names. This allows to support a full mapping of common document based configuration formats, such as JSON, YAML,
 *  XML and more.
 */
public final class PropertyValue implements Serializable, Iterable<PropertyValue>{

    private static final long serialVersionUID = 1L;
    /** The requested key. */
    private String key;
    /** The value. */
    private String value;
    /** Additional metadata provided by the provider. */
    private final transient Map<String,Object> metaData = new HashMap<>();
    /** List of child properties. */
    private final List<PropertyValue> children = new ArrayList<>();
    /** The getParent getChild, null if it's a root getChild. */
    private PropertyValue parent;
    /** The getChild's getIndex, if the getChild is participating in a list structure. */
    private int index = -1;
    /** The value version, used for determining config changes. */
    private AtomicInteger version = new AtomicInteger();
    /** Helper structure used for indexing new list getChildren. */
    private Map<String, AtomicInteger> indices = new HashMap<>();
    /** Flag to mark a value as immutable. */
    private boolean immutable;



    /**
     * Creates a new builder instance.
     * @param key the key, not {@code null}.
     * @param source the source, typically the name of the {@link PropertySource}
     *               providing the value, not {@code null}.
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
     * Creates a new builder instance.
     * @param key the key, not {@code null}.
     * @param source the source.
     * @return a new builder instance.
     * @deprecated Use {@link #create(String)}
     */
    @Deprecated
    public static PropertyValue of(String key, String source){
        Objects.requireNonNull(key, "Key must be given.");

        return new PropertyValue(null, key).setMeta("source", source);
    }

    /**
     * Creates a new builder instance.
     * @param key the key, not {@code null}.
     * @param value the property value, not {@code null}.
     * @param source the source, typically the name of the {@link PropertySource}
     *               providing the value, not {@code null}.
     * @return a new builder instance.
     */
    @Deprecated
    public static PropertyValue of(String key, String value, String source) {
        Objects.requireNonNull(key, "Key must be given.");
        if(source!=null) {
            return new PropertyValue(null, key).setValue(value).setMeta("source", source);
        }
        return new PropertyValue(null, key).setValue(value);
    }

    /**
     * Creates a new builder instance.
     * @param key the key, not {@code null}.
     * @param value the new value.
     * @return a new builder instance.
     */
    public static PropertyValue create(String key, String value){
        Objects.requireNonNull(key, "Key must be given.");

        return new PropertyValue(null, key).setValue(value);
    }


    /**
     * Creates a new (invisible) root getChild, which is a getChild with an empty name.
     * @return a new empty root getChild, never null.
     */
    public static PropertyValue create(){
        return new PropertyValue(null, "");
    }

    /**
     * Creates a new named root getChild.
     * @param name the name, not null.
     * @return a new named root getChild, never null.
     */
    public static PropertyValue create(String name){
        return new PropertyValue(null, name);
    }



    /**
      * Maps a map of {@code Map<String,String>} to a {@code Map<String,PropertyValue>}.
      * @param config the String based map, not {@code null}.
      * @param source the source name, not {@code null}.
      * @return the corresponding value based map.
      */
    public static Map<String, PropertyValue> map(Map<String, String> config, String source) {
        Map<String, PropertyValue> result = new HashMap<>(config.size());
        for(Map.Entry<String,String> en:config.entrySet()){
            result.put(en.getKey(), PropertyValue.of(en.getKey(), en.getValue(), source));
        }
        return result;
    }

    /**
     * Maps a map of {@code Map<String,String>} to a {@code Map<String,PropertyValue>}.
     *
     * @param config the String based map, not {@code null}.
     * @param source the source name, not {@code null}.
     * @param metaData additional metadata, not {@code null}.
     * @return the corresponding value based map.
     */
    public static Map<String, PropertyValue> map(Map<String, String> config, String source,
                                                 Map<String,String> metaData) {
        Objects.requireNonNull(config, "Config must be given.");
        Objects.requireNonNull(metaData, "Meta data must be given.");

        Map<String, PropertyValue> result = new HashMap<>(config.size());

        for(Map.Entry<String,String> en:config.entrySet()){
            PropertyValue pv = PropertyValue.create(en.getKey(), en.getValue())
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
     */
    private PropertyValue(PropertyValue parent, String key){
        this.parent = parent;
        this.key = Objects.requireNonNull(key, "Key is required.");
    }

    /**
     * Checks if the instance is immutable.
     * @return true, if the instance is immutable.
     */
    public boolean isImmutable(){
        return immutable;
    }

    /**
     * Sets this instance and also all its direct an indirect children to immutable. Any further changes will throw
     * an {@link IllegalStateException}.
     * @return this instance for chaining.
     */
    public PropertyValue setImmutable(){
        this.immutable = true;
        children.forEach(PropertyValue::setImmutable);
        return this;
    }

    /**
     * The requested key.
     * @return the, key never {@code null}.
     */
    public String getKey() {
        return key;
    }

    /**
     * Get a qualified name of a getChild in property format using '.' as separator, e.g.
     * {@code a.b.c} or {@code a.b.c[0]} for indexed entries. Entries hereby also can have multiple
     * levels of indexing, e.g. {@code a[1].b.c[14].d} is a valid option.
     *
     * The qualified key is defined by {@link #getQualifiedKey()} of it's parent concatenated with the key
     * of this node. If there is no parent, or the parent's qualified key is empty only {@link #getKey()}
     * is returned. Additionally if the current values is an indeyed value the key is extended by the
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
        if(!parentName.isEmpty()){
            parentName+=".";
        }
        if(isIndexed()){
            return parentName+key+"["+index+"]";
        }
        return parentName+key;
    }

    /**
     * The value.
     * @return the value, in case a value is null it is valid to return {#code null} as result for
     * {@link PropertySource#get(String)}.
     */
    public String getValue() {
        return this.value;
    }

    /**
     * Get the getChild's getParent.
     * @return the getParent, or null.
     */
    public PropertyValue getParent(){
        return parent;
    }

    /**
     * Get the values version, the version is updated with each change written.
     * @return the version.
     */
    public int getVersion(){
        return version.get();
    }

    /**
     * Get a getChild's getIndex.
     * @return the getIndex, or -1, if the getChild does not participate in an array.
     */
    public int getIndex(){
        return index;
    }

    /**
     * Get the source.
     * @return the source, or null.
     * @deprecated Use {@code getMeta("source")}.
     */
    @Deprecated
    public String getSource() {
        return (String)this.metaData.get("source");
    }


    /**
     * Checks if the getChild is a root getChild.
     * @return true, if the current getChild is a root getChild.
     */
    public boolean isRoot() {
        return parent == null;
    }

    /**
     * Checks if the getChild is a leaf getChild (has no getChildren).
     * @return true, if the current getChild is a leaf getChild.
     */
    public boolean isLeaf(){
        return children.isEmpty();
    }

    /**
     * Allows to check if a getChild is indexed.
     * @return true, if the getChild participates in an array.
     */
    public boolean isIndexed(){
        if(parent==null){
            return false;
        }
        return index>=0;
    }

    /**
     * Creates a full configuration map for this key, value pair and all its getMeta context data. This map
     * is also used for subsequent processing, like value filtering.
     * @return the property value entry map.
     */
    public Map<String, Object> getMeta() {
        return Collections.unmodifiableMap(metaData);
    }

    /**
     * Access the given key from this value. Valid keys are the key or any getMeta-context key.
     * @param key the key, not {@code null}.
     * @return the value found, or {@code null}.
     * @deprecated Use {@link #getMeta(String)} instead of.
     */
    @Deprecated
    public String getMetaEntry(String key) {
        return (String)this.metaData.get(Objects.requireNonNull(key));
    }

    /**
     * Access the given key from this value. Valid keys are the key or any getMeta-context key.
     * @param key the key, not {@code null}.
     * @param <T> the target type.
     * @return the value found, or {@code null}.
     */
    public <T> T getMeta(String key) {
        return (T)this.metaData.get(Objects.requireNonNull(key));
    }

    /**
     * Access the given metadata.
     * @param type the type, not {@code null}.
     * @param <T> the target type.
     * @return the value found, or {@code null}.
     */
    public <T> T getMeta(Class<T> type) {
        return (T)this.metaData.get(type.getName());
    }

    /**
     * Get a single child getChild by name.
     * @param name the child's name, not null.
     * @return the child found, or null.
     * @throws IllegalArgumentException if multiple getChildren with the given name are existing (ambigous).
     */
    public PropertyValue getChild(String name){
        List<PropertyValue> nodes = this.getChildren(name);
        if(nodes.isEmpty()){
            return null;
        }
        if(nodes.size()>1){
            throw new IllegalArgumentException("Multiple getChildren existing: " + name);
        }
        return nodes.get(0);
    }

    /**
     * Get a sub value.
     * @param index the target getIndex.
     * @return the value found.
     * @throws java.util.NoSuchElementException if no such element is existing.
     */
    public PropertyValue getChild(int index) {
        return this.children.get(index);
    }

    /**
     * Get a single child getChild with the given name, creates it if not existing.
     * @param name the child's name, not null.
     * @return the child found or created, never null.
     * @throws IllegalArgumentException if multiple getChildren with the given name are existing (ambigous).
     * @throws IllegalStateException if the instance is immutable.
     * @see #isImmutable()
     */
    public PropertyValue getOrCreateChild(String name){
        List<PropertyValue> nodes = this.getChildren(name);
        if(nodes.isEmpty()){
            checkImmutable();
            PropertyValue n = new PropertyValue(this, name);
            this.children.add(n);
            version.incrementAndGet();
            return n;
        }
        if(nodes.size()>1){
            throw new IllegalArgumentException("Multiple getChildren existing: " + name);
        }
        return nodes.get(0);
    }

    /**
     * Get's the n-th getChild of an indexed getChild setCurrent.
     * @param name the child's name, not null.
     * @param index the target getChild getIndex.
     * @return the getChild found, or null.
     */
    public PropertyValue getChildWithIndex(String name, int index){
        List<PropertyValue> nodes = this.getChildren(name);
        if(nodes.isEmpty() || nodes.size()<=index){
            return null;
        }
        return nodes.get(index);
    }

    /**
     * Get all child getChildren with a given name.
     * @param name the target name, not null.
     * @return the list of matching getChildren, could be none, one or multiple in case of arrays.
     */
    public List<PropertyValue> getChildren(String name){
        return children.stream().filter(n -> n.key.equals(name)).collect(Collectors.toList());
    }

    /**
     * Get the value's number of elements.
     * @return the getNumChilds of this multi value.
     */
    public int getNumChilds() {
        return this.children.size();
    }

    /**
     * The value.
     * @return the value, in case a value is null it is valid to return {#code null} as result for
     * {@link PropertySource#get(String)}.
     */
    public List<PropertyValue> getChildren() {
        return Collections.unmodifiableList(this.children);
    }

    @Override
    public Iterator<PropertyValue> iterator() {
        return Collections.unmodifiableList(this.children).iterator();
    }

    /**
     * Adds a new non-indexed child getChild.
     * @param name the child's name, not null.
     * @return the new child, never null.
     * @throws IllegalStateException if the instance is immutable.
     * @see #isImmutable()
     */
    public PropertyValue createChild(String name){
        return createChild(name, false);
    }

    /**
     * Adds a new non-indexed child.
     * @param name the child's name, not null.
     * @param value the child's value, not null.
     * @return the new child, not null.
     * @throws IllegalStateException if the instance is immutable.
     * @see #isImmutable()
     */
    public PropertyValue createChild(String name, String value){
        return createChild(name, false).setValue(value);
    }

    /**
     * Adds another existing node, hereby setting the corresponding parent node.
     * @param value the value, not null
     * @return this instance, for chaining.
     * @throws IllegalStateException if the instance is immutable.
     * @see #isImmutable()
     */
    public PropertyValue addChild(PropertyValue value) {
        checkImmutable();
        value.parent = this;
        this.children.add(value);
        return this;
    }

    /**
     * Adds a new child getChild.
     * @param name the child's name, not null.
     * @param indexed if true, the getChild will be participate in an array of the given name.
     * @return the new getChild, not null.
     * @throws IllegalStateException if the instance is immutable.
     * @see #isImmutable()
     */
    public PropertyValue createChild(String name, boolean indexed){
        checkImmutable();
        PropertyValue n = new PropertyValue(this, name);
        this.children.add(n);
        version.incrementAndGet();
        if(indexed) {
            AtomicInteger index = indices.computeIfAbsent(name, s -> new AtomicInteger(0));
            n.index = index.getAndIncrement();
        }else{
            List<PropertyValue> nodes = this.getChildren(name);
            if(nodes.size()>1){
                AtomicInteger index = indices.get(name);
                if(index!=null){
                    n.index = index.getAndIncrement();
                }else{
                    index = new AtomicInteger(0);
                    indices.put(name, index);
                    for(PropertyValue node:nodes){
                        node.index = index.getAndIncrement();
                    }
                }
            }
        }
        return n;
    }

    /**
     * Adds a new child getChild, where the getChild is given in '.'-separated property notation,
     * e.g. {@code a.b.c}.
     * @param key the property key, e.g. {@code a.b.c}
     * @param value the property value
     * @return the new leaf-getChild created.
     * @throws IllegalStateException if the instance is immutable.
     * @see #isImmutable()
     */
    public PropertyValue addProperty(String key, String value) {
        checkImmutable();
        PropertyValue node = this;
        StringTokenizer tokenizer = new StringTokenizer(key, "\\.", false);
        while(tokenizer.hasMoreTokens()){
            String token = tokenizer.nextToken().trim();
            node = node.getOrCreateChild(token);
            if(!tokenizer.hasMoreTokens()){
                // Its the last or single token
                node.setValue(value);
            }
        }
        return node;
    }

    /**
     * Adds multiple child getChildren, where the getChildren are defined in '.'-separated property notation,
     *      * e.g. {@code a.b.c}.
     * @param props the properties
     * @return the collection of added leaf-child getChildren.
     * @throws IllegalStateException if the instance is immutable.
     * @see #isImmutable()
     */
    public Collection<PropertyValue> addProperties(Map<String,String> props) {
        checkImmutable();
        List<PropertyValue> result = new ArrayList<>();
        props.entrySet().forEach(en -> result.add(addProperty(en.getKey(), en.getValue())));
        return result;
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
     * Sets the value.
     * @param value the value
     * @return this getChild for chaining.
     * @throws IllegalStateException if the instance is immutable.
     * @see #isImmutable()
     */
    public PropertyValue setValue(String value) {
        checkImmutable();
        if(!Objects.equals(this.value, value)) {
            this.value = value;
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
    public PropertyValue setMeta(Map<String, Object> metaEntries) {
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
     * @param value the context value, not {@code null} (will be converted to String).
     * @return the builder for chaining.
     * @throws IllegalStateException if the instance is immutable.
     * @see #isImmutable()
     */
    public PropertyValue setMeta(String key, Object value) {
        checkImmutable();
        Objects.requireNonNull(key, "Meta key must be given.");
        Objects.requireNonNull(value, "Meta value must be given.");
        if(!Objects.equals(this.metaData.get(key), value)) {
            this.metaData.put(key, value);
            version.incrementAndGet();
        }
        return this;
    }

    /**
     * Add an additional context data information.
     * @param type the context data type, used as key, not {@code null}.
     * @param value the context value, not {@code null}.
     * @param <T> the target type.
     * @return the builder for chaining.
     * @throws IllegalStateException if the instance is immutable.
     * @see #isImmutable()
     */
    public <T> PropertyValue setMeta(Class<T> type, T value) {
        checkImmutable();
        Objects.requireNonNull(type, "Meta key must be given.");
        Objects.requireNonNull(value, "Meta value must be given.");
        if(!Objects.equals(this.metaData.get(type.toString()), value)) {
            this.metaData.put(type.toString(), value);
            version.incrementAndGet();
        }
        return this;
    }

    /**
     * Add an additional context data information, using the data's class name as key.
     * @param value the context value, not {@code null}.
     * @param <T> the target type.
     * @return the builder for chaining.
     * @throws IllegalStateException if the instance is immutable.
     * @see #isImmutable()
     */
    public <T> PropertyValue setMeta(T value) {
        checkImmutable();
        Objects.requireNonNull(value, "Meta value must be given.");
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
    public PropertyValue removeMeta(String key) {
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
    public PropertyValue removeMeta(Class type) {
        checkImmutable();
        Objects.requireNonNull(key, "Key must be given.");
        if(this.metaData.containsKey(type.getName())) {
            this.metaData.remove(type.getName());
            version.incrementAndGet();
        }
        return this;
    }

    /**
     * Convert the getChild tree to a property map.
     * @return the corresponding property map, not null.
     */
    public Map<String,String> asMap(){
        Map<String, String> map = new TreeMap<>();
        if(isLeaf()){
            map.put(getQualifiedKey(), value);
        }
        for(PropertyValue n: children){
            map.putAll(n.asMap());
        }
        return map;
    }

    /**
     * Create a String representation of the tree.
     * @return the corresponding String representation, not null.
     */
    public String asString() {
        Map<String, String> map = asMap();
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

    /**
     * Clones this instance and all it's children, marking as mutable value.
     * @return the new value clone.
     */
    public PropertyValue mutable(){
        if(!immutable){
            return this;
        }
        PropertyValue newProp = new PropertyValue(this.parent, key);
        newProp.setValue(this.value);
        newProp.setMeta(metaData);
        children.forEach(c -> newProp.children.add(c.mutable()));
        newProp.version = new AtomicInteger(version.intValue());
        return newProp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PropertyValue)) return false;
        PropertyValue dataNode = (PropertyValue) o;
        return Objects.equals(parent, dataNode.parent) &&
                Objects.equals(key, dataNode.key) &&
                Objects.equals(value, dataNode.value) &&
                Objects.equals(metaData, dataNode.metaData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parent, key, value, metaData);
    }


    @Override
    public String toString() {
        return "PropertyValue{" +
                '\'' +getQualifiedKey() + '\'' +
                (value!=null?", value='" + value + '\'':"") +
                ", children='" + children.size() + '\'' +
                (metaData.isEmpty()?"":", metaData=" + metaData) +
                '}';
    }

    private void checkImmutable(){
        if(immutable){
            throw new IllegalStateException("Instance is immutable.");
        }
    }

}
