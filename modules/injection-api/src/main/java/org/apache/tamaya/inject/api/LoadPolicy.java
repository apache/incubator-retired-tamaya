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

/**
 * Available policies that describe how changes affecting configured values are published/reinjected
 * for a {@link DynamicValue}.
 * The policy also affects the cases were any configured listeners/listener methods are called for
 * propagation current configuration changes.
 */
public enum LoadPolicy {
    /**
     * The configuration keys is evaluated once, when the owning component is loaded/configured, but never updated later.
     */
    INITIAL,
    /**
     * The configuration keys is evaluated exactly once on its first access/use lazily, but never updated later.
     * @see DynamicValue#get()
     * @see DynamicValue#commitAndGet()
     */
    LAZY,
    /**
     * The configuration value is evaluated evertime it is accessed.
     */
    ALWAYS
}
