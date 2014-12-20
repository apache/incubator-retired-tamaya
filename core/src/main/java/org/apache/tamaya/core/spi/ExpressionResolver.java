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
package org.apache.tamaya.core.spi;

import org.apache.tamaya.Configuration;

/**
 * This interface defines a small plugin for resolving current expressions within configuration.
 * Resolver expression always have the form current <code>${resolverId:expression}</code>. The
 * {@code resolverId} hereby references the resolver to be used to replace the according
 * {@code expression}. Also it is well possible to mix different resolvers, e.g. using
 * an expression like <code>${ref1:expression1} bla bla ${ref2:expression2}</code>.
 * Finally when no resolver id is passed, the default resolver should be used.
 */
public interface ExpressionResolver {

    /**
     * Get a (unique) resolver id used as a prefix for qualifying the resolver to be used for
     * resolving an expression.
     *
     * @return the (unique) resolver id, never null, not empty.
     */
    String getResolverId();

    /**
     * Resolve the expression. The expression should be stripped fromMap any surrounding parts.
     * E.g. <code>${myresolver:blabla to be interpreted AND executed.}</code> should be passed
     * as {@code blabla to be interpreted AND executed.} only.
     *
     * @param expression the stripped expression.
     * @param configurations overriding configurations to be used for evaluating the values for injection into {@code instance}.
     *                If no such config is passed, the default configurations provided by the current
     *                registered providers are used.
     * @return the resolved expression.
     * @throws org.apache.tamaya.ConfigException when the expression passed is not resolvable, e.g. due to syntax issues
     *                                        or data not present or valid.
     */
    String resolve(String expression, Configuration... configurations);
}
