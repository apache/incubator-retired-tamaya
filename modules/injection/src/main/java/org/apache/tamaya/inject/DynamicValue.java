/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tamaya;

import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Logger;

/**
 * A accessor for a single configured value. This can be used to support values that may change during runtime, reconfigured or
 * final. Hereby external code (could be Tamaya configuration listners or client code), can set a new value. Depending on the
 * {@link org.apache.tamaya.DynamicValue.UpdatePolicy} the new value is immedeately active or it requires an active commit
 * by client code. Similarly an instance also can ignore all later changes to the value.
 * <h3>Implementation Details</h3>
 * This class is
 * <ul>
 *     <li>Serializable, when also the item stored is serializable</li>
 *     <li>Thread safe</li>
 * </ul>
 */
public final class DynamicValue<T> implements Serializable{

    /**
     * Policy to control how new values are applied to this instance.
     */
    enum UpdatePolicy{
        /** New values are applied immedately and registered listeners are informed about the change. */
        IMMEDIATE,
        /** New values or not applied, but stored in the newValue property. Explcit call to #commit
         of #commitAndGet are required to accept the change and inform the listeners about the change.
         */
        EXPLCIT,
        /**
         * New values are always immedately discarded.
         */
        NEVER,
        /**
         * Changes are logged before the are discarded.
         */
        LOG_AND_DISCARD
    }


    /** The property name of the entry. */
    private String propertyName;
    /**
     * Policy that defines how new values are applied, be default it is applied initially once, but never updated anymore.
     */
    private UpdatePolicy updatePolicy = UpdatePolicy.NEVER;
    /** The current value, never null. */
    private transient Optional<T> value;
    /** The new value, or null. */
    private transient Optional<T> newValue;
    /** List of listeners that listen for changes. */
    private transient WeakList<Consumer<PropertyChangeEvent>> listeners;

    /**
     * Returns an empty {@code Optional} instance.  No value is present for this
     * Optional.
     *
     * @apiNote Though it may be tempting to do so, avoid testing if an object
     * is empty by comparing with {@code ==} against instances returned by
     * {@code Option.empty()}. There is no guarantee that it is a singleton.
     * Instead, use {@link #isPresent()}.
     *
     * @param <T> Type of the non-existent value
     * @return an empty {@code Optional}
     */
    public static <T> DynamicValue<T> empty(String propertyName) {
        DynamicValue v = new DynamicValue<T>(propertyName, null);
        return v;
    }

    /**
     * Constructor.
     * @param propertyName the name of the value in the format {@code <configName>:<propertyName>}.</config>
     * @param item the initial value.
     */
    private DynamicValue(String propertyName, Optional<T> item){
        this.propertyName = Objects.requireNonNull(propertyName);
        this.value = item;
    }

    /**
     * Creates a new instance.
     * @param propertyName the name of the value in the format {@code <configName>:<propertyName>}.</config>
     * @param value the initial value, not null.
     * @param <T> the type
     * @return a new instance, never null
     */
    public static <T> DynamicValue<T> of(String propertyName, T value){
        return new DynamicValue(propertyName, Optional.of(value));
    }

    /**
     * Creates a new instance.
     * @param propertyName the name of the value in the format {@code <configName>:<propertyName>}.</config>
     * @param value the initial value
     * @param <T> the target type.
     * @return a new instance, never null
     */
    public static <T> DynamicValue<T> ofNullable(String propertyName, T value){
        return value == null ? empty(propertyName) : of(propertyName, value);
    }

    /**
     * Performs a commit, if necessary and returns the current value.
     * otherwise throws {@code ConfigException}.
     *
     * @return the non-null value held by this {@code Optional}
     * @throws org.apache.tamaya.ConfigException if there is no value present
     *
     * @see DynamicValue#isPresent()
     */
    public T commitAndGet(){
        commit();
        return get();
    }

    /**
     * Commits a new value that has not been committed yet, make it the new value of the instance. On change any registered listeners will be triggered.
     */
    public void commit(){
        synchronized (value){
            if(newValue!=null){
                PropertyChangeEvent evt = new PropertyChangeEvent(this, propertyName, value.orElse(null), newValue.orElse(null));
                value = newValue;
                newValue = null;
                for(Consumer<PropertyChangeEvent> consumer: listeners.get()){
                    consumer.accept(evt);
                }
            }
        }
    }

    /**
     * Discards a new value that was published. No listeners will be informed.
     */
    public void discard(){
        newValue = null;
    }



    /**
     * Access the {@link UpdatePolicy} used for updating this value.
     * @return the update policy, never null.
     */
    public UpdatePolicy getUpdatePolicy() {
        return updatePolicy;
    }

    /**
     * Add a listener to be called as weak reference, when this value has been changed.
     * @param l the listner, not null
     */
    public void addListener(Consumer<PropertyChangeEvent> l) {
        if(listeners==null){
            listeners = new WeakList<>();
        }
        listeners.add(l);
    }

    /**
     * Removes a listener to be called, when this value has been changed.
     * @param l the listner to be removed, not null
     */
    public void removeListener(Consumer<PropertyChangeEvent> l) {
        if(listeners!=null){
            listeners.remove(l);
        }
    }

    /**
     * If a value is present in this {@code ConfiguredValue}, returns the value,
     * otherwise throws {@code ConfigException}.
     *
     * @return the non-null value held by this {@code Optional}
     * @throws org.apache.tamaya.ConfigException if there is no value present
     *
     * @see DynamicValue#isPresent()
     */
    public T get() {
        return value.get();
    }

    /**
     * Method to apply a new value. Depending on the {@link  org.apache.tamaya.DynamicValue.UpdatePolicy}
     * the value is immediately or deferred visible (or it may even be ignored completely).
     * @param newValue the new value, may also be null.
     */
    public void setNewValue(T newValue){
        switch(this.updatePolicy){
            case IMMEDIATE:
                this.newValue = Optional.ofNullable(newValue);
                commit();
                break;
            case EXPLCIT:
                this.newValue = Optional.ofNullable(newValue);
                break;
            case LOG_AND_DISCARD:
                Logger.getLogger(getClass().getName()).info("Discard change on " + this + ", newValue="+newValue);
                this.newValue = null;
                break;
            case NEVER:
                this.newValue = null;
                break;
        }

    }

    /**
     * Sets a new {@link org.apache.tamaya.DynamicValue.UpdatePolicy}.
     * @param updatePolicy the new policy, not null.
     */
    public void setUpdatePolicy(UpdatePolicy updatePolicy){
        this.updatePolicy = Objects.requireNonNull(updatePolicy);
    }

    /**
     * Access a new value that has not yet been committed.
     * @return the uncommitted new value, or null.
     */
    public T getNewValue(){
        Optional<T> nv = newValue;
        if(nv!=null){
            return nv.orElse(null);
        }
        return null;
    }

    /**
     * Return {@code true} if there is a value present, otherwise {@code false}.
     *
     * @return {@code true} if there is a value present, otherwise {@code false}
     */
    public boolean isPresent() {
        return value.isPresent();
    }

    /**
     * If a value is present, invoke the specified consumer with the value,
     * otherwise do nothing.
     *
     * @param consumer block to be executed if a value is present
     * @throws NullPointerException if value is present and {@code consumer} is
     * null
     */
    public void ifPresent(Consumer<? super T> consumer) {
        value.ifPresent(consumer);
    }

    /**
     * If a value is present, and the value matches the given predicate,
     * return an {@code Optional} describing the value, otherwise return an
     * empty {@code Optional}.
     *
     * @param predicate a predicate to apply to the value, if present
     * @return an {@code Optional} describing the value of this {@code Optional}
     * if a value is present and the value matches the given predicate,
     * otherwise an empty {@code Optional}
     * @throws NullPointerException if the predicate is null
     */
    public DynamicValue<T> filter(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate);
        if (!isPresent())
            return this;
        else
            return predicate.test(value.get()) ? this : empty(propertyName);
    }

    /**
     * If a value is present, apply the provided mapping function to it,
     * and if the result is non-null, return an {@code Optional} describing the
     * result.  Otherwise return an empty {@code Optional}.
     *
     * @apiNote This method supports post-processing on optional values, without
     * the need to explicitly check for a return status.  For example, the
     * following code traverses a stream of file names, selects one that has
     * not yet been processed, and then opens that file, returning an
     * {@code Optional<FileInputStream>}:
     *
     * <pre>{@code
     *     Optional<FileInputStream> fis =
     *         names.stream().filter(name -> !isProcessedYet(name))
     *                       .findFirst()
     *                       .map(name -> new FileInputStream(name));
     * }</pre>
     *
     * Here, {@code findFirst} returns an {@code Optional<String>}, and then
     * {@code map} returns an {@code Optional<FileInputStream>} for the desired
     * file if one exists.
     *
     * @param <U> The type of the result of the mapping function
     * @param mapper a mapping function to apply to the value, if present
     * @return an {@code Optional} describing the result of applying a mapping
     * function to the value of this {@code Optional}, if a value is present,
     * otherwise an empty {@code Optional}
     * @throws NullPointerException if the mapping function is null
     */
    public <U> DynamicValue<U> map(Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        if (!isPresent())
            return empty(propertyName);
        else {
            return DynamicValue.ofNullable(propertyName, mapper.apply(value.get()));
        }
    }

    /**
     * If a value is present, apply the provided {@code Optional}-bearing
     * mapping function to it, return that result, otherwise return an empty
     * {@code Optional}.  This method is similar to {@link #map(Function)},
     * but the provided mapper is one whose result is already an {@code Optional},
     * and if invoked, {@code flatMap} does not wrap it with an additional
     * {@code Optional}.
     *
     * @param <U> The type parameter to the {@code Optional} returned by
     * @param mapper a mapping function to apply to the value, if present
     *           the mapping function
     * @return the result of applying an {@code Optional}-bearing mapping
     * function to the value of this {@code Optional}, if a value is present,
     * otherwise an empty {@code Optional}
     * @throws NullPointerException if the mapping function is null or returns
     * a null result
     */
    public <U> DynamicValue<U> flatMap(Function<? super T, DynamicValue<U>> mapper) {
        Objects.requireNonNull(mapper);
        if (!isPresent())
            return empty(propertyName);
        else {
            return Objects.requireNonNull(mapper.apply(value.get()));
        }
    }

    /**
     * Return the value if present, otherwise return {@code other}.
     *
     * @param other the value to be returned if there is no value present, may
     * be null
     * @return the value, if present, otherwise {@code other}
     */
    public T orElse(T other) {
        return value.orElse(other);
    }

    /**
     * Return the value if present, otherwise invoke {@code other} and return
     * the result of that invocation.
     *
     * @param other a {@code Supplier} whose result is returned if no value
     * is present
     * @return the value if present otherwise the result of {@code other.get()}
     * @throws NullPointerException if value is not present and {@code other} is
     * null
     */
    public T orElseGet(Supplier<? extends T> other) {
        return value.orElseGet(other);
    }

    /**
     * Return the contained value, if present, otherwise throw an exception
     * to be created by the provided supplier.
     *
     * @apiNote A method reference to the exception constructor with an empty
     * argument list can be used as the supplier. For example,
     * {@code IllegalStateException::new}
     *
     * @param <X> Type of the exception to be thrown
     * @param exceptionSupplier The supplier which will return the exception to
     * be thrown
     * @return the present value
     * @throws X if there is no value present
     * @throws NullPointerException if no value is present and
     * {@code exceptionSupplier} is null
     */
    public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        return value.orElseThrow(exceptionSupplier);
    }

    /**
     * Converts the instance to an {@link java.util.Optional} instance.
     * @return the corresponding Optional value.
     */
    public Optional<T> toOptional(){
        return value;
    }

    /**
     * Serialization implementation that strips away the non serializable Optional part.
     * @param oos the output stream
     * @throws IOException if serialization fails.
     */
    private void writeObject(ObjectOutputStream oos)throws IOException {
        oos.writeObject(updatePolicy);
        if(isPresent()) {
            oos.writeObject(this.value.get());
        }
        else{
            oos.writeObject(null);
        }
    }

    /**
     * Reads an instance from the input stream.
     * @param ois the object input stream
     * @throws IOException if deserialization fails.
     * @throws ClassNotFoundException
     */
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        this.updatePolicy = (UpdatePolicy)ois.readObject();
        if(isPresent()) {
            this.value = Optional.of((T) ois.readObject());
        }
        newValue = null;
    }


    /**
     * Simple helper that allows keeping the listeners registered as weak references, hereby avoiding any
     * memory leaks.
     * @param <T> the type
     */
    private class WeakList<T>{
        List<WeakReference<T>> refs = new LinkedList<>();

        /**
         * Adds a new instance.
         * @param t the new instance, not null.
         */
        void add(T t){
            refs.add(new WeakReference(t));
        }

        /**
         * Removes a instance.
         * @param t the instance to be removed.
         */
        void remove(T t){
            synchronized (refs){
                for(Iterator<WeakReference<T>> iterator = refs.iterator();iterator.hasNext();){
                    WeakReference<T> ref = iterator.next();
                    T instance = ref.get();
                    if(instance==null || instance == t){
                        iterator.remove();
                        break;
                    }
                }
            }
        }


        /**
         * Access a list (copy) of the current instances that were not discarded by the GC.
         * @return the list of accessible items.
         */
        public List<T> get() {
            synchronized (refs) {
                List<T> res = new ArrayList<>();
                for (Iterator<WeakReference<T>> iterator = refs.iterator(); iterator.hasNext(); ) {
                    WeakReference<T> ref = iterator.next();
                    T instance = ref.get();
                    if(instance==null){
                        iterator.remove();
                    }
                    else{
                        res.add(instance);
                    }
                }
                return res;
            }
        }
    }

}
