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
package org.apache.tamaya.staged.spi;

import org.apache.tamaya.spi.PropertySource;

import java.util.List;
import java.util.Map;

/**
 * Resolver to resolve/map DSL related source expressions into PropertySources
 * loadable by a ConfigurationContext. Hereby the ordering of loaded property sources must be
 * honored if possible by implicitly adapting/Overriding the default ordinal for the sources
 * added.
 */
public interface DSLPropertySourceProvider {

    /**
     * Resolve the given expression (without the key part).
     * @param sourceExpression the source expression, not null.
     * @param defaultPropertySources the default property sources that can be used as defined by the functionality by
     *                               a resolver.
     * @return the list of loaded Property sources, never null.
     */
    List<PropertySource> resolve(String sourceExpression,
                                 Map<String, PropertySource> defaultPropertySources);

    /**
     * Get the resolver key, which identifiesan expression to be resolved by a resolver instance.
     * As an example {@code "named:"} is the key for an expression {@code "named:sys-properties"}.
     * The method {@link #resolve(String, Map)} will onyl receive the secoind part of the expression.
     * @return identifying key.
     */
    String getKey();
}
