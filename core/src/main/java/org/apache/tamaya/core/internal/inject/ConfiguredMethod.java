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

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Objects;

import org.apache.tamaya.ConfigException;
import org.apache.tamaya.Configuration;
import org.apache.tamaya.annotation.ConfiguredProperties;
import org.apache.tamaya.annotation.ConfiguredProperty;
import org.apache.tamaya.annotation.DefaultAreas;
import org.apache.tamaya.core.internal.Utils;

/**
 * Small class that contains and manages all information and access to a configured field and a concrete instance current
 * it (referenced by a weak reference). It also implements all aspects current keys filtering, conversions any applying the
 * final keys by reflection.
 */
public class ConfiguredMethod {

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
     * Evaluate the initial keys fromMap the configuration and applyChanges it to the field.
     *
     * @param target the target instance.
     * @param configurations Configuration instances that replace configuration served by services. This allows
     *                       more easily testing and adaption.
     * @throws ConfigException if evaluation or conversion failed.
     */
    public void applyInitialValue(Object target, Configuration... configurations) throws ConfigException {
        String configValue = InjectionUtils.getConfigValue(this.annotatedMethod, configurations);
        applyValue(target, configValue, false, configurations);
    }

    /**
     * This method reapplies a changed configuration keys to the field.
     *
     * @param target      the target instance, not null.
     * @param configValue the new keys to be applied, null will trigger the evaluation current the configured default keys.
     * @param resolve     set to true, if expression resolution should be applied on the keys passed.
     * @throws org.apache.tamaya.ConfigException if the configuration required could not be resolved or converted.
     */
    public void applyValue(Object target, String configValue, boolean resolve, Configuration... configurations) throws ConfigException {
        Objects.requireNonNull(target);
        try {
            if (resolve && configValue != null) {
                // net step perform exression resolution, if any
                configValue = Configuration.evaluateValue(configValue, configurations);
            }
            // Check for adapter/filter
            Object value = InjectionUtils.adaptValue(this.annotatedMethod, this.annotatedMethod.getParameterTypes()[0], configValue);
            annotatedMethod.setAccessible(true);
            annotatedMethod.invoke(target, value);
        } catch (Exception e) {
            throw new ConfigException("Failed to annotation configured method: " + this.annotatedMethod.getDeclaringClass()
                    .getName() + '.' + annotatedMethod.getName(), e);
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
        DefaultAreas areasAnnot = this.annotatedMethod.getDeclaringClass().getAnnotation(DefaultAreas.class);
        Collection<ConfiguredProperty> configuredProperties =
                Utils.getAnnotations(this.annotatedMethod, ConfiguredProperty.class, ConfiguredProperties.class);
        for(ConfiguredProperty prop: configuredProperties) {
            if (InjectionUtils.evaluateKeys(this.annotatedMethod, areasAnnot, prop).contains(key)) {
                return true;
            }
        }
        return false;
    }



}
