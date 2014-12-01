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
import org.apache.tamaya.annot.*;
import org.apache.tamaya.core.internal.Utils;

import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Small class that contains and manages all information anc access to a configured field and a concrete instance of
 * it (referenced by a weak reference). It also implements all aspects of value filtering, conversiong any applying the
 * final value by reflection.
 * Created by Anatole on 01.10.2014.
 */
@SuppressWarnings("UnusedDeclaration")
public class ConfiguredMethod {

    private static final Logger LOG = Logger.getLogger(ConfiguredMethod.class.getName());

    /**
     * The configured field instance.
     */
    private Method annotatedMethod;

    /**
     * Models a configured field and provides mechanisms for injection.
     *
     * @param method the method instance.
     */
    public ConfiguredMethod(Method method) {
        this.annotatedMethod = Objects.requireNonNull(method);
    }


    /**
     * Internally evaluated the current valid configuration value based on the given annotations present.
     *
     * @return the value to be returned, or null.
     */
    private String getConfigValue() {
        DefaultAreas areasAnnot = this.annotatedMethod.getDeclaringClass().getAnnotation(DefaultAreas.class);
        DefaultValue defaultAnnot = this.annotatedMethod.getAnnotation(DefaultValue.class);
        Collection<ConfiguredProperty> configuredProperties =
                Utils.getAnnotations(this.annotatedMethod, ConfiguredProperty.class, ConfiguredProperties.class);
        List<String> keys = evaluateKeys(areasAnnot, configuredProperties);
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
     * Evaluates all absolute configuration key based on the annotations found on a class.
     *
     * @param areasAnnot          the (optional) annotation definining areas to be looked up.
     * @param propertyAnnotations the annotation on field/method level that may defined the
     *                            exact key to be looked up (in absolute or relative form).
     * @return the list of keys in order how they should be processed/looked up.
     */
    private List<String> evaluateKeys(DefaultAreas areasAnnot, Collection<ConfiguredProperty> propertyAnnotations) {
        List<String> keys =
                Objects.requireNonNull(propertyAnnotations).stream()
                        .filter(p -> !p.value().isEmpty())
                        .map(ConfiguredProperty::value).collect(Collectors.toList());
        if (keys.isEmpty()) //noinspection UnusedAssignment
            keys.add(annotatedMethod.getName());
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
        DefaultAreas areasAnnot = this.annotatedMethod.getDeclaringClass().getAnnotation(DefaultAreas.class);
        Collection<ConfiguredProperty> configuredProperties =
                Utils.getAnnotations(this.annotatedMethod, ConfiguredProperty.class, ConfiguredProperties.class);
        List<String> keys = evaluateKeys(areasAnnot, configuredProperties);
        return keys.contains(key);
    }

    /**
     * This method evaluates the {@link org.apache.tamaya.Configuration} that currently is valid for the given target field/method.
     *
     * @return the {@link org.apache.tamaya.Configuration} instance to be used, never null.
     */
    public Configuration getConfiguration() {
        WithConfig name = annotatedMethod.getAnnotation(WithConfig.class);
        if (name != null) {
            return Configuration.of(name.value());
        }
        return Configuration.of();
    }

    /**
     * This method reapplies a changed configuration value to the field.
     *
     * @throws org.apache.tamaya.ConfigException if the configuration required could not be resolved or converted.
     */
    public Object getValue(Object[] args) throws ConfigException {
        // TODO do something with additional args?
        String configValue = getConfigValue();
        try {
            // Check for adapter/filter
            WithPropertyAdapter adapterAnnot = this.annotatedMethod.getAnnotation(WithPropertyAdapter.class);
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
                // TODO optionally return null...
                LOG.info("No config value found for " +
                        this.annotatedMethod.getDeclaringClass().getName() + '#' +
                        this.annotatedMethod.getName());
                return null;
            } else {
                Class<?> baseType = annotatedMethod.getReturnType();
                if (String.class.equals(baseType) || baseType.isAssignableFrom(configValue.getClass())) {
                    return configValue;
                } else {
                    PropertyAdapter<?> adapter = PropertyAdapters.getAdapter(baseType);
                    return adapter.adapt(configValue);
                }
            }
        } catch (Exception e) {
            throw new ConfigException("Failed to inject configured field: " + this.annotatedMethod.getDeclaringClass()
                    .getName() + '.' + annotatedMethod.getName(), e);
        }
    }

}
