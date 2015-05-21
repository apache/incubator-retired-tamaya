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
package org.apache.tamaya.environment.spi;


import org.apache.tamaya.environment.RuntimeContext;
import org.apache.tamaya.environment.RuntimeContextBuilder;


/**
 * SPI component for evaluating the current runtime context. All registered providers hereby are
 * organized by default depending on their (optional) {@code @Priority} annotation's value. (the
 * effective ordering depends on the current {@link org.apache.tamaya.spi.ServiceContext} implementation
 * active).
 */
public interface ContextProviderSpi {

    public static final String ENVIRONMENT_TYPE = "environment-type";

    /**
     * If a data providers identifies a new runtime context level, it should build a new
     * {@link org.apache.tamaya.environment.RuntimeContext} with all the related data to be added to this
     * context, otherwise it should simply return null.
     *
     * @param contextBuilder the current context builder.
     * @return the new current context for the current runtime state, or null.
     */
    void setupContext(RuntimeContextBuilder contextBuilder);
}
