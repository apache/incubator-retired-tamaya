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
import org.apache.tamaya.PropertyConverter;
import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.inject.ConfigRoot;
import org.apache.tamaya.inject.ConfiguredProperty;
import org.apache.tamaya.inject.DynamicValue;
import org.apache.tamaya.inject.WithPropertyConverter;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Small class that contains and manages all information anc access to a configured field and a concrete instance current
 * it (referenced by a weak reference). It also implements all aspects current keys filtering, converting any applying the
 * final keys by reflection.
 */
public class ConfiguredField {
    /**
     * The logger used.
     */
    private static final Logger LOG = Logger.getLogger(ConfiguredField.class.getName());
    /**
     * The configured field instance.
     */
    protected Field annotatedField;

    /**
     * Models a configured field and provides mechanisms for injection.
     *
     * @param field the field instance.
     */
    public ConfiguredField(Field field) {
        Objects.requireNonNull(field);
        this.annotatedField = field;
    }

    /**
     * Evaluate the initial keys fromMap the configuration and applyChanges it to the field.
     *
     * @param target the target instance.
     * @throws ConfigException if evaluation or conversion failed.
     */
    public void applyInitialValue(Object target) throws ConfigException {
        if (this.annotatedField.getType() == DynamicValue.class) {
            initDynamicValue(target);
        } else {
            String configValue = InjectionUtils.getConfigValue(this.annotatedField);
            applyValue(target, configValue, false);
        }
    }


    /**
     * This method instantiates and assigns a dynamic value.
     *
     * @param target the target instance, not null.
     * @throws ConfigException if the configuration required could not be resolved or converted.
     */
    public void initDynamicValue(Object target) throws ConfigException {
        Objects.requireNonNull(target);
        try {
            // Check for adapter/filter
            Type targetType = this.annotatedField.getGenericType();
            if (targetType == null) {
                throw new ConfigException("Failed to evaluate target type for " + annotatedField.getAnnotatedType().getType().getTypeName()
                        + '.' + annotatedField.getName());
            }
            if (targetType instanceof ParameterizedType) {
                ParameterizedType pt = (ParameterizedType) targetType;
                Type[] types = pt.getActualTypeArguments();
                if (types.length != 1) {
                    throw new ConfigException("Failed to evaluate target type for " + annotatedField.getAnnotatedType().getType().getTypeName()
                            + '.' + annotatedField.getName());
                }
                targetType = (Class) types[0];
            }
            PropertyConverter<?> propertyConverter = null;
            WithPropertyConverter annot = this.annotatedField.getAnnotation(WithPropertyConverter.class);
            if (annot != null) {
                try {
                    propertyConverter = annot.value().newInstance();
                } catch (Exception e) {
                    throw new ConfigException("Failed to instantiate annotated PropertyConverter on " +
                            annotatedField.getAnnotatedType().getType().getTypeName()
                            + '.' + annotatedField.getName(), e);
                }
            }
            List<String> keys = InjectionUtils.getKeys(this.annotatedField);
            Configuration configuration = ConfigurationProvider.getConfiguration();
            Object value = new DefaultDynamicValue(this.annotatedField.getName(), configuration,
                    TypeLiteral.of(targetType), propertyConverter, keys);
            AccessController.doPrivileged((PrivilegedExceptionAction<Object>) () -> {
                annotatedField.setAccessible(true);
                return annotatedField;
            });
            annotatedField.set(target, value);
        } catch (Exception e) {
            throw new ConfigException("Failed to annotation configured field: " + this.annotatedField.getDeclaringClass()
                    .getName() + '.' + annotatedField.getName(), e);
        }
    }

    /**
     * This method reapplies a changed configuration keys to the field.
     *
     * @param target      the target instance, not null.
     * @param configValue the new keys to be applied, null will trigger the evaluation current the configured default keys.
     * @param resolve     set to true, if expression resolution should be applied on the keys passed.
     * @throws ConfigException if the configuration required could not be resolved or converted.
     */
    public void applyValue(Object target, String configValue, boolean resolve) throws ConfigException {
        Objects.requireNonNull(target);
        try {
            // Next step perform expression resolution, if any
            String evaluatedValue = resolve && configValue != null
                    ? InjectionUtils.evaluateValue(configValue)
                    : configValue;

            // Check for adapter/filter
            Object value = InjectionUtils.adaptValue(this.annotatedField, TypeLiteral.of(this.annotatedField.getType()), evaluatedValue);

            AccessController.doPrivileged((PrivilegedExceptionAction<Object>) () -> {
                annotatedField.setAccessible(true);
                return annotatedField;
            });

            annotatedField.set(target, value);
        } catch (Exception e) {
            throw new ConfigException("Failed to annotation configured field: " + this.annotatedField.getDeclaringClass()
                    .getName() + '.' + annotatedField.getName(), e);
        }
    }


    /**
     * This method checks if the given (qualified) configuration key is referenced fromMap this field.
     * This is useful to determine, if a key changed in a configuration should trigger any change events
     * on the related instances.
     *
     * @param key the (qualified) configuration key, not null.
     * @return true, if the key is referenced.
     */
    public boolean matchesKey(String key) {
        ConfiguredProperty prop = this.annotatedField.getAnnotation(ConfiguredProperty.class);
        ConfigRoot areasAnnot = this.annotatedField.getDeclaringClass().getAnnotation(ConfigRoot.class);
        List<String> keys = InjectionUtils.evaluateKeys(this.annotatedField, areasAnnot, prop);
        return keys.contains(key);
    }

    @Override
    public String toString() {
        return "ConfiguredField{" +
                annotatedField.getName() + ": " +
                " " + annotatedField.getAnnotatedType().getType().getTypeName() + '}';
    }
}
