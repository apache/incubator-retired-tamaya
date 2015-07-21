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
public abstract class BaseDynamicValue<T> implements DynamicValue<T> {


}
