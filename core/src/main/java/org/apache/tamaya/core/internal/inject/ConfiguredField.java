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

import org.apache.tamaya.Codec;
import org.apache.tamaya.ConfigException;
import org.apache.tamaya.Configuration;
import org.apache.tamaya.annotation.*;
import org.apache.tamaya.core.internal.Utils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Small class that contains and manages all information anc access to a configured field and a concrete instance current
 * it (referenced by a weak reference). It also implements all aspects current keys filtering, converting any applying the
 * final keys by reflection.
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
     * Evaluate the initial keys fromMap the configuration and applyChanges it to the field.
     *
     * @param target the target instance.
     * @param configurations Configuration instances that replace configuration served by services. This allows
     *                       more easily testing and adaption.
     * @throws ConfigException if evaluation or conversion failed.
     */
    public void applyInitialValue(Object target, Configuration... configurations) throws ConfigException {
        String configValue = InjectionUtils.getConfigValue(this.annotatedField, configurations);
        applyValue(target, configValue, false, configurations);
    }


    /**
     * This method reapplies a changed configuration keys to the field.
     *
     * @param target      the target instance, not null.
     * @param configValue the new keys to be applied, null will trigger the evaluation current the configured default keys.
     * @param resolve     set to true, if expression resolution should be applied on the keys passed.
     * @throws ConfigException if the configuration required could not be resolved or converted.
     */
    public void applyValue(Object target, String configValue, boolean resolve, Configuration... configurations) throws ConfigException {
        Objects.requireNonNull(target);
        try {
            if (resolve && configValue != null) {
                // net step perform exression resolution, if any
                configValue = Configuration.evaluateValue(configValue, configurations);
            }
            // Check for adapter/filter
            Object value = InjectionUtils.adaptValue(this.annotatedField, this.annotatedField.getType(), configValue);
            annotatedField.setAccessible(true);
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
    public boolean matchesKey(String configName, String key) {
        Collection<ConfiguredProperty> configuredProperties = Utils.getAnnotations(this.annotatedField, ConfiguredProperty.class,
                ConfiguredProperties.class );
        for(ConfiguredProperty prop: configuredProperties){
            String currentName = prop.config().trim();
            if(currentName.isEmpty()){
                if(!"default".equals(configName)){
                    continue;
                }
            }
            else if(!currentName.equals(configName)){
                continue;
            }
            DefaultAreas areasAnnot = this.annotatedField.getDeclaringClass().getAnnotation(DefaultAreas.class);
            List<String> keys = InjectionUtils.evaluateKeys(this.annotatedField, areasAnnot, prop);
            if( keys.contains(key)){
                return true;
            }
        }
        return false;
    }

}
