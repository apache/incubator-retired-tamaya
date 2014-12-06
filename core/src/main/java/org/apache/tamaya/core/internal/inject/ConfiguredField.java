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
package org.apache.tamaya.core.internal.inject;

import org.apache.tamaya.ConfigException;
import org.apache.tamaya.Configuration;
import org.apache.tamaya.PropertyAdapter;
import org.apache.tamaya.PropertyAdapters;
import org.apache.tamaya.mapping.*;
import org.apache.tamaya.core.internal.Utils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Small class that contains and manages all information anc access to a configured field and a concrete instance current
 * it (referenced by a weak reference). It also implements all aspects current value filtering, converting any applying the
 * final value by reflection.
 */
@SuppressWarnings("UnusedDeclaration")
public class ConfiguredField {

    private Logger LOG = Logger.getLogger(ConfiguredField.class.getName());

    /**
     * The configured field instance.
     */
    private Field annotatedField;

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
     * Evaluate the initial value fromMap the configuration and apply it to the field.
     *
     * @param target the target instance.
     * @throws ConfigException if evaluation or conversion failed.
     */
    public void applyInitialValue(Object target) throws ConfigException {
        Collection<ConfiguredProperty> configuredProperties = Utils.getAnnotations(
                annotatedField, ConfiguredProperty.class, ConfiguredProperties.class);
        DefaultAreas areasAnnot = this.annotatedField.getDeclaringClass().getAnnotation(DefaultAreas.class);
        WithLoadPolicy loadPolicy = Utils.getAnnotation(WithLoadPolicy.class, this.annotatedField, this.annotatedField.getDeclaringClass());
        DefaultValue defaultValue = this.annotatedField.getAnnotation(DefaultValue.class);
        String configValue = getConfigValue(loadPolicy, areasAnnot, configuredProperties, defaultValue);
        applyValue(target, configValue, false);
    }

    /**
     * Internally evaluated the current vaslid configuration value based on the given annotations present.
     *
     * @param loadPolicyAnnot The load policy, determining any explicit listeners to be informed.
     * @param areasAnnot      Any default areas to be looked up.
     * @param propertiesAnnot The configured property keys (qualified or relative).
     * @param defaultAnnot    any configured default value.
     * @return the value to be applied, or null.
     */
    private String getConfigValue(WithLoadPolicy loadPolicyAnnot, DefaultAreas areasAnnot, Collection<ConfiguredProperty> propertiesAnnot, DefaultValue defaultAnnot) {
        List<String> keys = evaluateKeys(areasAnnot, propertiesAnnot);
        annotatedField.setAccessible(true);
        Configuration config = getConfiguration();
        String configValue = null;
        for (String key : keys) {
            if (config.containsKey(key)) {
                configValue = config.get(key).orElse(null);
            }
            if (configValue != null) {
                break;
            }
        }
        if (configValue == null && defaultAnnot != null) {
            configValue = defaultAnnot.value();
        }
        if (configValue != null) {
            // net step perform expression resolution, if any
            return Configuration.evaluateValue(configValue);
        }
        return null;
    }

    /**
     * This method reapplies a changed configuration value to the field.
     *
     * @param target      the target instance, not null.
     * @param configValue the new value to be applied, null will trigger the evaluation current the configured default value.
     * @param resolve     set to true, if expression resolution should be applied on the value passed.
     * @throws ConfigException if the configuration required could not be resolved or converted.
     */
    public void applyValue(Object target, String configValue, boolean resolve) throws ConfigException {
        Objects.requireNonNull(target);
        try {
            if (resolve && configValue != null) {
                // net step perform exression resolution, if any
                configValue = Configuration.evaluateValue(configValue);
            }
            // Check for adapter/filter
            WithPropertyAdapter adapterAnnot = this.annotatedField.getAnnotation(WithPropertyAdapter.class);
            Class<? extends PropertyAdapter> propertyAdapterType;
            if (adapterAnnot != null) {
                propertyAdapterType = adapterAnnot.value();
                if (!propertyAdapterType.equals(PropertyAdapter.class)) {
                    // TODO cache here...
                    PropertyAdapter<String> filter = propertyAdapterType.newInstance();
                    configValue = filter.adapt(configValue);
                }
            }
            if (configValue == null) {
                // TODO Check for optional injection!
                // annotatedField.set(target, null);
                LOG.info("No config found for " +
                        this.annotatedField.getDeclaringClass().getName() + '#' +
                        this.annotatedField.getName());
            } else {
                Class baseType = annotatedField.getType();
                if (String.class.equals(baseType) || baseType.isAssignableFrom(configValue.getClass())) {
                    annotatedField.set(target, configValue);
                } else {
                    PropertyAdapter<?> adapter = PropertyAdapters.getAdapter(baseType);
                    annotatedField.set(target, adapter.adapt(configValue));
                }
            }
        } catch (Exception e) {
            throw new ConfigException("Failed to mapping configured field: " + this.annotatedField.getDeclaringClass()
                    .getName() + '.' + annotatedField.getName(), e);
        }
    }

    /**
     * Evaluates all absolute configuration key based on the annotations found on a class.
     *
     * @param areasAnnot          the (optional) annotation definining areas to be looked up.
     * @param propertyAnnotations the annotation on field/method level that may defined the
     *                            exact key to be looked up (in absolute or relative form).
     * @return the list current keys in order how they should be processed/looked up.
     */
    private List<String> evaluateKeys(DefaultAreas areasAnnot,Collection<ConfiguredProperty> propertyAnnotations) {
        Objects.requireNonNull(propertyAnnotations);
        List<String> keys = propertyAnnotations.stream().map(ConfiguredProperty::value).filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
        if (keys.isEmpty()) //noinspection UnusedAssignment
            keys.add(annotatedField.getName());
        ListIterator<String> iterator = keys.listIterator();
        while (iterator.hasNext()) {
            String next = iterator.next();
            if (next.startsWith("[") && next.endsWith("]")) {
                // absolute key, strip away brackets, take key as is
                iterator.set(next.substring(1, next.length() - 1));
            } else {
                if (areasAnnot != null) {
                    // Remove original entry, since it will be replaced with prefixed entries
                    iterator.remove();
                    // Add prefixed entries, including absolute (root) entry for "" area value.
                    for (String area : areasAnnot.value()) {
                        iterator.add(area.isEmpty() ? next : area + '.' + next);
                    }
                }
            }
        }
        return keys;
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
        DefaultAreas areasAnnot = this.annotatedField.getDeclaringClass().getAnnotation(DefaultAreas.class);
        Collection<ConfiguredProperty> configuredProperties = Utils.getAnnotations(this.annotatedField, ConfiguredProperty.class,
                ConfiguredProperties.class );
        List<String> keys = evaluateKeys(areasAnnot, configuredProperties);
        return keys.contains(key);
    }

    /**
     * This method evaluates the {@link Configuration} that currently is valid for the given target field/method.
     *
     * @return the {@link Configuration} instance to be used, never null.
     */
    public Configuration getConfiguration() {
        WithConfig name = annotatedField.getAnnotation(WithConfig.class);
        if(name!=null) {
            return Configuration.current(name.value());
        }
        return Configuration.current();
    }


}
