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
package org.apache.tamaya.inject.api;


import java.io.Serializable;

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
 * <li>Serializable, when also the item stored is serializable</li>
 * <li>Thread safe</li>
 * </ul>
 *
 * @param <T> The type of the value.
 */
public abstract class BaseDynamicValue<T> implements DynamicValue<T>, Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Performs a commit, if necessary, and returns the current value.
     *
     * @return the non-null value held by this {@code DynamicValue}
     * @throws org.apache.tamaya.ConfigException if there is no value present
     * @see DynamicValue#isPresent()
     */
    public T commitAndGet() {
        commit();
        return get();
    }

    /**
     * Return {@code true} if there is a value present, otherwise {@code false}.
     *
     * @return {@code true} if there is a value present, otherwise {@code false}
     */
    public boolean isPresent() {
        return get() != null;
    }


    /**
     * Return the value if present, otherwise return {@code other}.
     *
     * @param other the value to be returned if there is no value present, may
     *              be null
     * @return the value, if present, otherwise {@code other}
     */
    public T orElse(T other) {
        T value = get();
        if (value == null) {
            return other;
        }
        return value;
    }

    /**
     * Return the value if present, otherwise invoke {@code other} and return
     * the result of that invocation.
     *
     * @param other a {@code ConfiguredItemSupplier} whose result is returned if no value
     *              is present
     * @return the value if present otherwise the result of {@code other.get()}
     * @throws NullPointerException if value is not present and {@code other} is
     *                              null
     */
    public T orElseGet(ConfiguredItemSupplier<? extends T> other) {
        T value = get();
        if (value == null) {
            return other.get();
        }
        return value;
    }

    /**
     * Return the contained value, if present, otherwise throw an exception
     * to be created by the provided supplier.
     * <p>
     * NOTE A method reference to the exception constructor with an empty
     * argument list can be used as the supplier. For example,
     * {@code IllegalStateException::new}
     *
     * @param <X>               Type of the exception to be thrown
     * @param exceptionSupplier The supplier which will return the exception to
     *                          be thrown
     * @return the present value
     * @throws X                    if there is no value present
     * @throws NullPointerException if no value is present and
     *                              {@code exceptionSupplier} is null
     */
    public <X extends Throwable> T orElseThrow(ConfiguredItemSupplier<? extends X> exceptionSupplier) throws X {
        T value = get();
        if (value == null) {
            throw exceptionSupplier.get();
        }
        return value;
    }

}
