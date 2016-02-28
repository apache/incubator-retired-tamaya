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
package org.apache.tamaya.mutableconfig;

/**
 * Policy that can be passed when creating a {@link MutableConfigurationQuery} to define how existing values from
 * the base configuration should be handled. The corresponding behaviour is immedeately activen, it does not
 * require a {@code commit()}. Nevertheless cleaning up all changes will reverse any changes and also related
 * effects.
 */
public enum ValueVisibilityPolicy {
    /**
     * Entries from the base configuration are hidden by the entries edited. This gives you the best control on your
     * changes applied, but probably will not match the behaviour of your default configuration, since the effective
     * ordinals of your PropertySources may determine other overriding behaviour.
     */
    CHANGES,

    /**
     * Entries added are also added to the overall configuration for read access before committed, but any existing
     * values are never overridden.
     */
    CONFIG,

}
