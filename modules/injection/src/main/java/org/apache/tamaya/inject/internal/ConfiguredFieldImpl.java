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

import org.apache.tamaya.ConfigException;
import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;
import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.inject.api.DynamicValue;
import org.apache.tamaya.inject.api.InjectionUtils;
import org.apache.tamaya.inject.spi.ConfiguredField;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.Collection;
import java.util.Objects;

/**
 * Small class that contains and manages all information anc access to a configured field and a concrete instance current
 * it (referenced by a weak reference). It also implements all aspects current keys filtering, converting any applying the
 * final keys by reflection.
 */
public class ConfiguredFieldImpl implements ConfiguredField{
    /**
     * The configured field instance.
     */
    protected Field annotatedField;

    /**
     * Models a configured field and provides mechanisms for injection.
     *
     * @param field the field instance.
     */
    public ConfiguredFieldImpl(Field field) {
        Objects.requireNonNull(field);
        this.annotatedField = field;
    }


    /**
     * Evaluate the initial keys fromMap the configuration and applyChanges it to the field.
     *
     * @param target the target instance.
     * @throws ConfigException if evaluation or conversion failed.
     */
    public void configure(Object target, Configuration config) throws ConfigException {
        if (this.annotatedField.getType() == DynamicValue.class) {
            applyDynamicValue(target);
        } else {
            applyValue(target, config, false);
        }
    }


    /**
     * This method instantiates and assigns a dynamic value.
     *
     * @param target the target instance, not null.
     * @throws ConfigException if the configuration required could not be resolved or converted.
     */
    private void applyDynamicValue(Object target) throws ConfigException {
        Objects.requireNonNull(target);
        try {
            AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
                @Override
                public Object run() throws Exception {
                    annotatedField.setAccessible(true);
                    return annotatedField;
                }
            });
            annotatedField.set(target,
                    DefaultDynamicValue.of(annotatedField, ConfigurationProvider.getConfiguration()));
        } catch (Exception e) {
            throw new ConfigException("Failed to annotation configured field: " + this.annotatedField.getDeclaringClass()
                    .getName() + '.' + annotatedField.getName(), e);
        }
    }

    /**
     * This method applies a configuration to the field.
     *
     * @param target      the target instance, not null.
     * @param resolve     set to true, if expression resolution should be applied on the keys passed.
     * @throws ConfigException if the configuration required could not be resolved or converted.
     */
    private void applyValue(Object target, Configuration config, boolean resolve) throws ConfigException {
        Objects.requireNonNull(target);
        try {
            String[] retKey = new String[1];
            String configValue = InjectionHelper.getConfigValue(this.annotatedField, retKey, config);
            // Next step perform expression resolution, if any
            String evaluatedValue = resolve && configValue != null
                    ? InjectionHelper.evaluateValue(configValue)
                    : configValue;

            // Check for adapter/filter
            Object value = InjectionHelper.adaptValue(this.annotatedField,
                    TypeLiteral.of(this.annotatedField.getType()), retKey[0], evaluatedValue);
            AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
                @Override
                public Object run() throws Exception {
                    annotatedField.setAccessible(true);
                    return annotatedField;
                }
            });
            annotatedField.set(target, value);
        } catch (Exception e) {
            throw new ConfigException("Failed to evaluate annotated field: " + this.annotatedField.getDeclaringClass()
                    .getName() + '.' + annotatedField.getName(), e);
        }
    }

    /**
     * Get the field's type.
     * @return the field's type, not null.
     */
    @Override
    public Class<?> getType(){
        return this.annotatedField.getType();
    }

    /**
     * Access the applyable configuration keys for this field.
     * @return the configuration keys, never null.
     */
    @Override
    public Collection<String> getConfiguredKeys(){
        return InjectionUtils.getKeys(this.annotatedField);
    }

    @Override
    public String toString() {
        return "ConfiguredField[" + getSignature() + ']';
    }

    @Override
    public String getName() {
        return annotatedField.getName();
    }

    @Override
    public String getSignature() {
        return getName()+':'+getType().getName();
    }

    @Override
    public Field getAnnotatedField() {
        return annotatedField;
    }
}
