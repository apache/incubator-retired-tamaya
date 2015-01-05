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
package org.apache.tamaya.resolver.internal;

import org.apache.tamaya.resolver.spi.ExpressionEvaluator;
import org.apache.tamaya.spi.PropertyFilter;
import org.apache.tamaya.spi.ServiceContext;

import javax.annotation.Priority;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * Default expression evaluator that manages several instances of {@link org.apache.tamaya.resolver.spi.ExpressionResolver}.
 * Each resolver is identified by a resolver id. Each expression passed has the form resolverId:resolverExpression, which
 * has the advantage that different resolvers can be active in parallel.
 */
@Priority(10000)
public class ExpressionResolutionFilter implements PropertyFilter {

    private static final Logger LOG = Logger.getLogger(ExpressionResolutionFilter.class.getName());

    private ExpressionEvaluator evaluator = ServiceContext.getInstance().getService(ExpressionEvaluator.class).get();

    /**
     * Resolves an expression in the form current <code>${resolverId:expression}</code> or
     * <code>${<prefix>expression}</code>. The expression can be
     * part current any type current literal text. Also multiple expressions with mixed matching resolvers are
     * supported.
     * All control characters (${}\) can be escaped using '\'.<br>
     * So all the following are valid expressions:
     * <ul>
     * <li><code>${expression}</code></li>
     * <li><code>bla bla ${expression}</code></li>
     * <li><code>${expression} bla bla</code></li>
     * <li><code>bla bla ${expression} bla bla</code></li>
     * <li><code>${expression}${resolverId2:expression2}</code></li>
     * <li><code>foo ${expression}${resolverId2:expression2}</code></li>
     * <li><code>foo ${expression} bar ${resolverId2:expression2}</code></li>
     * <li><code>${expression}foo${resolverId2:expression2}bar</code></li>
     * <li><code>foor${expression}bar${resolverId2:expression2}more</code></li>
     * <li><code>\${expression}foo${resolverId2:expression2}bar</code> (first expression is escaped).</li>
     * </ul>
     * Given {@code resolverId:} is a valid prefix targeting a {@link java.beans.Expression} explicitly, also the
     * following expressions are valid:
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
     * @param key the key to be filtered
     * @param valueToBeFiltered value to be analyzed for expressions
     * @return the resolved value, or the input in case where no expression was detected.
     */
    @Override
    public String filterProperty(String key, String valueToBeFiltered, Function<String,String> propertyValueProvider){
        return evaluator.filterProperty(key, valueToBeFiltered, propertyValueProvider);
    }


}
