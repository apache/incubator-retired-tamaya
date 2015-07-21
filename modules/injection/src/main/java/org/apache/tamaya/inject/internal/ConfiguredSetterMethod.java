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
package org.apache.tamaya.inject.internal;

import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.Objects;

import org.apache.tamaya.ConfigException;
import org.apache.tamaya.TypeLiteral;

/**
 * Small class that contains and manages all information and access to a configured field and a concrete instance current
 * it (referenced by a weak reference). It also implements all aspects current keys filtering, conversions any applying the
 * final keys by reflection.
 */
public class ConfiguredSetterMethod {

    /**
     * The configured field instance.
     */
    private Method setterMethod;

    /**
     * Models a configured field and provides mechanisms for injection.
     *
     * @param method the method instance.
     */
    public ConfiguredSetterMethod(Method method) {
        if (void.class.equals(method.getReturnType()) &&
                method.getParameterCount() == 1) {
            this.setterMethod = method;
        }
    }

    /**
     * Evaluate the initial keys fromMap the configuration and applyChanges it to the field.
     *
     * @param target the target instance.
     * @throws ConfigException if evaluation or conversion failed.
     */
    public void applyValue(Object target, boolean resolve) throws ConfigException {
        String configValue = InjectionUtils.getConfigValue(this.setterMethod);
        Objects.requireNonNull(target);
        try {
            String evaluatedString = resolve && configValue != null
                    ? InjectionUtils.evaluateValue(configValue)
                    : configValue;

            // Check for adapter/filter
            Object value = InjectionUtils.adaptValue(this.setterMethod,  TypeLiteral.of(this.setterMethod.getParameterTypes()[0]), evaluatedString);

            AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
                @Override
                public Object run() throws Exception {
                    setterMethod.setAccessible(true);
                    return setterMethod;
                }
            });

            setterMethod.invoke(target, value);
        } catch (Exception e) {
            throw new ConfigException("Failed to annotation configured method: " + this.setterMethod.getDeclaringClass()
                    .getName() + '.' + setterMethod.getName(), e);
        }
    }


}
