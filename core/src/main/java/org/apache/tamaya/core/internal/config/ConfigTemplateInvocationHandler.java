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
package org.apache.tamaya.core.internal.config;

import org.apache.tamaya.Configuration;
import org.apache.tamaya.core.internal.inject.ConfiguredType;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * Created by Anatole on 17.10.2014.
 */
class ConfigTemplateInvocationHandler implements InvocationHandler {

    private Configuration config;
    private ConfiguredType type;

    public ConfigTemplateInvocationHandler(Class<?> type, Configuration config) {
        this.config = Objects.requireNonNull(config);
        this.type = new ConfiguredType(Objects.requireNonNull(type));
        if(!type.isInterface()){
            throw new IllegalArgumentException("Can only proxy interfaces as configuration templates.");
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if("toString".equals(method.getName())){
            return "Configured Proxy -> " + this.type.getType().getName();
        }
        return this.type.getConfiguredValue(method, args);
    }
}
