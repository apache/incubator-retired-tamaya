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
package org.apache.tamaya.inject;

import java.beans.PropertyChangeEvent;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A accessor for a single configured value. This can be used to support values that may change during runtime,
 * reconfigured or final. Hereby external code (could be Tamaya configuration listners or client code), can set a
 * new value. Depending on the {@link UpdatePolicy} the new value is immedeately active or it requires an active commit
 * by client code. Similarly an instance also can ignore all later changes to the value.<br/>
 * Types of this interface can be used as injection targets in injected beans or as template resiult on configuration
 * templates.
 * <h3>Implementation Specification</h3>
 * Implementation of this interface must be
 * <ul>
 *     <li>Serializable, when also the item stored is serializable</li>
 *     <li>Thread safe</li>
 * </ul>
 * @param <T> The type of the value.
 */
public interface DynamicValue<T> {

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

    /**
     * Performs a commit, if necessary, and returns the current value.
     *
     * @return the non-null value held by this {@code DynamicValue}
     * @throws org.apache.tamaya.ConfigException if there is no value present
     *
     * @see DynamicValue#isPresent()
     */
    default T commitAndGet(){
        commit();
        return get();
    }

    /**
     * Commits a new value that has not been committed yet, make it the new value of the instance. On change any
     * registered listeners will be triggered.
     */
    void commit();

    /**
     * Discards a new value that was published. No listeners will be informed.
     */
    void discard();

    /**
     * Access the {@link UpdatePolicy} used for updating this value.
     * @return the update policy, never null.
     */
    UpdatePolicy getUpdatePolicy();

    /**
     * Add a listener to be called as weak reference, when this value has been changed.
     * @param l the listener, not null
     */
    void addListener(Consumer<PropertyChangeEvent> l);

    /**
     * Removes a listener to be called, when this value has been changed.
     * @param l the listner to be removed, not null
     */
    void removeListener(Consumer<PropertyChangeEvent> l);

    /**
     * If a value is present in this {@code DynamicValue}, returns the value,
     * otherwise throws {@code ConfigException}.
     *
     * @return the non-null value held by this {@code Optional}
     * @throws org.apache.tamaya.ConfigException if there is no value present
     *
     * @see DynamicValue#isPresent()
     */
    T get();

    /**
     * Method to check for and apply a new value. Depending on the {@link  UpdatePolicy}
     * the value is immediately or deferred visible (or it may even be ignored completely).
     * @return true, if a new value has been detected. The value may not be visible depending on the current
     * {@link org.apache.tamaya.inject.DynamicValue.UpdatePolicy} in place.
     */
    boolean updateValue();

    /**
     * Evaluates the current value dynamically from the underlying configuration.
     * @return the current actual value, or null.
     */
    T evaluateValue();

    /**
     * Sets a new {@link UpdatePolicy}.
     * @param updatePolicy the new policy, not null.
     */
    void setUpdatePolicy(UpdatePolicy updatePolicy);

    /**
     * Access a new value that has not yet been committed.
     * @return the uncommitted new value, or null.
     */
    T getNewValue();

    /**
     * Return {@code true} if there is a value present, otherwise {@code false}.
     *
     * @return {@code true} if there is a value present, otherwise {@code false}
     */
    boolean isPresent();

    /**
     * If a value is present, invoke the specified consumer with the value,
     * otherwise do nothing.
     *
     * @param consumer block to be executed if a value is present
     * @throws NullPointerException if value is present and {@code consumer} is
     * null
     */
    default void ifPresent(Consumer<? super T> consumer){
        if(isPresent()){
            consumer.accept(get());
        }
    }

    /**
     * Return the value if present, otherwise return {@code other}.
     *
     * @param other the value to be returned if there is no value present, may
     * be null
     * @return the value, if present, otherwise {@code other}
     */
   default T orElse(T other){
       if(isPresent()){
           return get();
       }
       return other;
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
    default T orElseGet(Supplier<? extends T> other){
        if(isPresent()){
            return get();
        }
        return other.get();
    }

    /**
     * Return the contained value, if present, otherwise throw an exception
     * to be created by the provided supplier.
     *
     * NOTE A method reference to the exception constructor with an empty
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
    default <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X{
        if(isPresent()){
            return  get();
        }
        throw exceptionSupplier.get();
    }

    /**
     * Converts the instance to an {@link java.util.Optional} instance.
     * @return the corresponding Optional value.
     */
    default Optional<T> toOptional(){
        if(isPresent()){
            return Optional.of(get());
        }
        return Optional.empty();
    }

}
