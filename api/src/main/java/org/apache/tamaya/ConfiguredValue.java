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

import org.apache.tamaya.annotation.LoadPolicy;

import java.beans.PropertyChangeEvent;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * A accessor for a single configured value. This can be used to support values that may be reinjected, reconfigured.
 * <h3>Implementation Requirements</h3>
 * Instances of this class must be
 * <ul>
 *     <li>Serializable</li>
 *     <li>Immutable</li>
 *     <li>Thread safe</li>
 * </ul>
 */
public interface ConfiguredValue<T> {

    /**
     * Access the {@link org.apache.tamaya.annotation.LoadPolicy} used for updating this value.
     * @return the load policy, never null.
     */
    LoadPolicy getLoadPolicy();

    /**
     * get the UTC timestamp in ms of the last access to a value, using get().
     * @return the UTC timestamp of the last access
     */
    long getLastAccess();

    /**
     * get the UTC timestamp in ms of the last update to the value,.
     * @return the UTC timestamp of the last update
     */
    long getLastUpdate();

    /**
     * Access if this instance has been updated since the given UTC timestamp in ms.
     * @param timestamp
     * @return true, if his instance has been updated since the given UTC timestamp in ms.
     */
    boolean isUpdatedSince(long timestamp);

    /**
     * Access if this instance has been accessed since the given UTC timestamp in ms.
     * @param timestamp
     * @return true, if his instance has been accessed since the given UTC timestamp in ms.
     */
    boolean isAccessedSince(long timestamp);

    /**
     * Add a listener to be called, when this value is changed.
     * @param l the listner, not null
     */
    void addListener(Consumer<PropertyChangeEvent> l);

    /**
     * Removes a listener to be called, when this value is changed.
     * @param l the listner to be removed, not null
     */
    void removeListener(Consumer<PropertyChangeEvent> l);

    /**
     * Get some descriptive meta info on the current value.
     * @return the meta info, not null.
     */
    String getMetaInfo();

    /**
     * Evaluate if the item value has been updated since the last access.
     * @return true, if item value has been updated since the last access.
     */
    default boolean isUpdated(){
        return isUpdatedSince(getLastAccess());
    }

    /**
     * If a value is present in this {@code ConfiguredValue}, returns the value,
     * otherwise throws {@code ConfigException}.
     *
     * @return the non-null value held by this {@code Optional}
     * @throws org.apache.tamaya.ConfigException if there is no value present
     *
     * @see ConfiguredValue#isPresent()
     */
    T get();

    /**
     * If a value is present in this {@code ConfiguredValue}, returns the value,
     * otherwise throws {@code ConfigException}.
     *
     * @return the non-null value held by this {@code Optional}
     * @throws org.apache.tamaya.ConfigException if there is no value present
     *
     * @see ConfiguredValue#isPresent()
     */
    default T updateAndGet(){
        update();
        return get();
    }

    /**
     * Reevaluates the current value based on the instance's settings from the underlying configurations
     * and applies the new value to its internal state. On change any registered listeners will be triggered.
     */
    void update();

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
    void ifPresent(Consumer<? super T> consumer);

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
    ConfiguredValue<T> filter(Predicate<? super T> predicate);

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
    <U> ConfiguredValue<U> map(Function<? super T, ? extends U> mapper);

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
    <U> ConfiguredValue<U> flatMap(Function<? super T, ConfiguredValue<U>> mapper);

    /**
     * Return the value if present, otherwise return {@code other}.
     *
     * @param other the value to be returned if there is no value present, may
     * be null
     * @return the value, if present, otherwise {@code other}
     */
    T orElse(T other);

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
    T orElseGet(Supplier<? extends T> other);

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
    <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X;

    /**
     * Converts this value to an {@link java.util.Optional} instance.
     * @return an {@link java.util.Optional} instance, never null.
     */
    default Optional<T> toOptional(){
        if(isPresent()){
            return Optional.of(get());
        }
        return Optional.empty();
    }

}
