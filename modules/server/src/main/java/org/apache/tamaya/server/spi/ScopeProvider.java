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
package org.apache.tamaya.server.spi;

import org.apache.tamaya.ConfigOperator;

/**
 * Simple registrable provider class to register scopes for the server extension.
 */
public interface ScopeProvider {

    /**
     * Access the unique scope name.
     * @return the unique scope name.
     */
    String getScopeType();

    /**
     * Return the scope operator that implements the scope for the given scope id.
     * @param scopeId target scope id.
     * @return the scope operator, never null.
     */
    ConfigOperator getScope(String scopeId);
}
