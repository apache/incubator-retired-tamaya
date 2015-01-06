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
package org.apache.tamaya.resolver.spi;

import java.util.function.Function;

/**
 * This interfaces provides a model for expression evaluation. This enables transparently plugin expression languages
 * as needed. In a Java EE context full fledged EL may be used, whereas in ME only simple replacement mechanisms
 * are better suited to the runtime requirements.
 */
public interface ExpressionResolver {

    /**
     * Get the unique resolver prefix. This allows to address a resolver explicitly, in case of conflicts. By
     * default all registered resolvers are called in order as defined by the {@link javax.annotation.Priority}
     * annotation.
     *
     * @return the prefix that identifies this resolver instance, e.g. 'config:'.
     */
    public String getResolverPrefix();

    /**
     * Evaluates the given expression.
     *
     * @param expression       the expression to be evaluated, not null. If a resolver was addressed explicitly,
     *                         the prefix is removed prior to calling this method.
     * @return the evaluated expression, or null, if the evaluator is not able to resolve the expression.
     */
    String evaluate(String expression);
}
