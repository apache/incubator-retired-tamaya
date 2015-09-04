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
package org.apache.tamaya.examples.remote.server;

import org.apache.tamaya.ConfigOperator;
import org.apache.tamaya.functions.ConfigurationFunctions;
import org.apache.tamaya.server.spi.ScopeProvider;


/**
 * Implementation of a {@link ScopeProvider} that registers a {@link ConfigOperator} that
 * filters out and combines a configuration subsection identified by
 * {@code client.default, client.<CLIENTID>}.
 */
public class ClientScopeProvider implements ScopeProvider{

    /**
     * Access the unique scope name.
     * @return the unique scope name.
     */
    public String getScopeType(){
            return "CLIENT";
    }

    @Override
    public ConfigOperator getScope(String scopeId) {
        return c ->
                ConfigurationFunctions.combine("Scoped Config CLIENT="+scopeId,
                        c.with(ConfigurationFunctions.sectionRecursive(true, "client.default")),
                        c.with(ConfigurationFunctions.sectionRecursive(true, "client." + scopeId))
                );
    }
}
