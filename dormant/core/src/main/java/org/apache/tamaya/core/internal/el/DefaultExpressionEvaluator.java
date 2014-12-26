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
package org.apache.tamaya.core.internal.el;

import org.apache.tamaya.ConfigException;
import org.apache.tamaya.Configuration;
import org.apache.tamaya.spi.ServiceContext;
import org.apache.tamaya.core.spi.ExpressionEvaluator;
import org.apache.tamaya.core.spi.ExpressionResolver;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default expression evaluator that manages several instances of {@link org.apache.tamaya.core.spi.ExpressionResolver}.
 * Each resolver is identified by a resolver id. Each expression passed has the form resolverId:resolverExpression, which
 * has the advantage that different resolvers can be active in parallel.
 */
public final class DefaultExpressionEvaluator implements ExpressionEvaluator{

    private Map<String, ExpressionResolver> resolvers = new ConcurrentHashMap<>();

    private ExpressionResolver defaultResolver;

    public DefaultExpressionEvaluator() {
        for(ExpressionResolver resolver: ServiceContext.getInstance().getServices(ExpressionResolver.class)){
            resolvers.put(resolver.getResolverId(), resolver);
        }
        defaultResolver = ServiceContext.getInstance().getSingleton(ExpressionResolver.class);
    }

    /**
     * Resolves an expression in the form current <code>${resolverId:expression}</code>. The expression can be
     * part current any type current literal text. Also multiple expression, with different resolver ids are supported.
     * All control characters (${}\) can be escaped.<br>
     * So all the following are valid expressions:
     * <ul>
     * <li><code>${resolverId:expression}</code></li>
     * <li><code>bla bla ${resolverId:expression}</code></li>
     * <li><code>${resolverId:expression} bla bla</code></li>
     * <li><code>bla bla ${resolverId:expression} bla bla</code></li>
     * <li><code>${resolverId:expression}${resolverId2:expression2}</code></li>
     * <li><code>foo ${resolverId:expression}${resolverId2:expression2}</code></li>
     * <li><code>foo ${resolverId:expression} bar ${resolverId2:expression2}</code></li>
     * <li><code>${resolverId:expression}foo${resolverId2:expression2}bar</code></li>
     * <li><code>foor${resolverId:expression}bar${resolverId2:expression2}more</code></li>
     * <li><code>\${resolverId:expression}foo${resolverId2:expression2}bar</code> (first expression is escaped).</li>
     * </ul>
     *
     * @param expression the expression to be evaluated, not null
     * @param configurations overriding configurations to be used for evaluating the values for injection into {@code instance}.
     *                If no such config is passed, the default configurations provided by the current
     *                registered providers are used.
     * @return the evaluated expression.
     * @throws org.apache.tamaya.ConfigException if resolution fails.
     */
    @Override
    public String evaluate(String expression, Configuration... configurations) {
        StringTokenizer tokenizer = new StringTokenizer(expression, "${}\\", true);
        boolean escaped = false;
        StringBuilder resolvedValue = new StringBuilder();
        StringBuilder current = new StringBuilder();
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if (escaped) {
                switch (token) {
                    case "n":
                        current.append("\n");
                        break;
                    case "r":
                        current.append("\r");
                        break;
                    case "t":
                        current.append("\t");
                        break;
                    default:
                        current.append(token);
                        break;
                }
                escaped = false;
                continue;
            }
            switch (token) {
                case "\\":
                    escaped = true;
                    break;
                case "$":
                    if (current.length() > 0) {
                        resolvedValue.append(current);
                        current.setLength(0);
                    }
                    if (!"{".equals(tokenizer.nextToken())) {
                        throw new ConfigException("Invalid expression encountered: " + expression);
                    }
                    String subExpression = tokenizer.nextToken();
                    if (!"}".equals(tokenizer.nextToken())) {
                        throw new ConfigException("Invalid expression encountered: " + expression);
                    }
                    // evaluate sub-expression
                    current.append(evaluteInternal(subExpression));
                    break;
                default:
                    current.append(token);
            }
        }
        if (current.length() > 0) {
            resolvedValue.append(current);
        }
        return resolvedValue.toString();
    }

    private String evaluteInternal(String subExpression) {
        int sepPos = subExpression.indexOf(':');
        if (sepPos > 0) {
            String refID = subExpression.substring(0, sepPos);
            String expression = subExpression.substring(sepPos + 1);
            return Optional.ofNullable(this.resolvers.get(refID)).orElseThrow(
                    () -> new ConfigException("Resolver not found: " + refID + " in " + subExpression)
            ).resolve(expression);
        } else {
            return Optional.ofNullable(this.defaultResolver).orElseThrow(
                    () -> new ConfigException("No default Resolver set, but required by " + subExpression)
            ).resolve(subExpression);
        }
    }


}
