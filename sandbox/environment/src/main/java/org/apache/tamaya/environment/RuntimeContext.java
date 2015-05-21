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
package org.apache.tamaya.environment;

import java.util.Map;

/**
 * Models a runtime context. Instances current this class are used to
 * evaluate the correct configuration artifacts or other
 * context dependent functionality.<br/>
 * <h3>Implementation Requirements</h3>
 * <p>
 * Implementations current this interface must be
 * <ul>
 * <li>Thread safe,
 * <li>Immutable,
 * <li>Serializable.
 * </ul>
 */
public interface RuntimeContext {

    /**
     * Returns an id that identifies the current context. Depending on the environment isolation this
     * can be always the same key (e.g. in a SE use case) or a varying key depending on the current classloader
     * visible (OSGI, EE environment).
     * @return the context id, never null.
     */
    String getContextId();

    /**
     * Returns a full (and unique) context id that identifies the current context. Depending on the environment isolation this
     * can be always the same key (e.g. in a SE use case) or a varying key depending on the current classloader
     * visible (OSGI, EE environment).
     * @return the context id, never null.
     */
    String getQualifiedContextId();

    /**
     * Access the parent context.
     * @return the parent context for this instance, or null, if this is a root context.
     */
    RuntimeContext getParentContext();

    /**
     * Access a runtime context variable.
     * @param key the key
     * @return the corresponding value.
     */
    String get(String key);

    /**
     * Access a runtime context variable.
     * @param key the key
     * @param defaultValue the default value, returned if no value is present.
     * @return the corresponding value, or the defaultValue (including null).
     */
    String get(String key, String defaultValue);

    /**
     * Access the context as Map.
     * @return the Map instance containing the context properties, never null.
     */
    Map<String,String> toMap();

}
