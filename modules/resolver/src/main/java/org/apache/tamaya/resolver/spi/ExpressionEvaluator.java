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
 * Created by Anatole on 05.01.2015.
 */
public interface ExpressionEvaluator {
    /**
     * Evaluates the current expression.
     * @param key the key, not null.
     * @param valueToBeFiltered the value to be filtered/evaluated.
     * @return the filtered/evaluated value, including null.
     */
    String filterProperty(String key, String valueToBeFiltered);
}
