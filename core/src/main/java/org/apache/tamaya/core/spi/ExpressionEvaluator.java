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
 * This interfaces provides a model for expression evaluation. This enables transparently plugin expression languages
 * as needed. In a Java EE context full fledged EL may be used, whereas in ME only simple replacement mechanisms
 * are better suited to the runtime requirements.
 */
@FunctionalInterface
public interface ExpressionEvaluator {
    /**
     * Evaluates the given expression.
     * @param expression the expression to be evaluated, not null.
     * @param configurations the configurations to be used for evaluating the values for injection into {@code instance}.
     *                       If no items are passed, the default configuration is used.
     * @return the evaluated expression.
     */
    String evaluate(String expression, Configuration... configurations);
}
