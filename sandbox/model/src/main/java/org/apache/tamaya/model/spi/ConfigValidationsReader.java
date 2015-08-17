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
package org.apache.tamaya.model.spi;

import org.apache.tamaya.model.Validation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class to read metamodel information from properties. Hereby these properties can be part of a
 * configuration (containing other entriees as well) or be dedicated model definition properties read
 * from any kind of source.
 */
public final class ConfigValidationsReader {

    /** The logger used. */
    private static final Logger LOGGER =  Logger.getLogger(ConfigValidationsReader.class.getName());

    /** The default model entries selector. */
    private static final String DEFAULT_META_INFO_SELECTOR = "{model}";
    /** parameter to change the selector to be used for filtering out the target values to be used. */
    private static final String META_INFO_SELECTOR_PARAM = "org.apache.tamaya.model.integrated.selector";

    /**
     * Loads validations as configured in the given properties.
     * @param props the properties to be read
     * @param defaultProviderName the default provider name used if no explicit provider name is configured.
     * @return a collection of config validations.
     */
    public static Collection<Validation> loadValidations(Properties props,
                                                         String defaultProviderName) {
        Map<String,String> map = new HashMap<>();
        for(Map.Entry<Object,Object> en: props.entrySet()){
            map.put(en.getKey().toString(), props.getProperty(en.getKey().toString()));
        }
        return loadValidations(map, defaultProviderName);
    }

    /**
     * Loads validations as configured in the given properties.
     * @param props the properties to be read
     * @param defaultProviderName the default provider name used if no explicit provider name is configured.
     * @return a collection of config validations.
     */
    public static Collection<Validation> loadValidations(Map<String,String> props,
                                                         String defaultProviderName) {
        String selector = props.get(META_INFO_SELECTOR_PARAM);
        if(selector==null){
            selector = DEFAULT_META_INFO_SELECTOR;
        }
        return loadValidations(props, selector, defaultProviderName);
    }

    /**
     * Loads validations as configured in the given properties.
     * @param props the properties to be read
     * @param selector the selector (default is {model}), that identifies the model entries.
     * @param defaultProviderName the default provider name used if no explicit provider name is configured.
     * @return a collection of config validations.
     */
    public static Collection<Validation> loadValidations(Map<String,String> props, String selector,
                                                         String defaultProviderName) {
        List<Validation> result = new ArrayList<>();
        String provider = props.get(selector + ".__provider");
        if (provider == null) {
            provider = defaultProviderName;
        }
        Set<String> itemKeys = new HashSet<>();
        for (Object key : props.keySet()) {
            if (key.toString().endsWith(".class")) {
                itemKeys.add(key.toString().substring(0, key.toString().length() - ".class".length()));
            }
        }
        for (String baseKey : itemKeys) {
            String clazz = props.get(baseKey + ".class");
            String type = props.get(baseKey + ".type");
            if (type == null) {
                type = String.class.getName();
            }
            String description = props.get(baseKey + ".description");
            String regEx = props.get(baseKey + ".expression");
            String validations = props.get(baseKey + ".validations");
            String requiredVal = props.get(baseKey + ".required");
            if ("Parameter".equalsIgnoreCase(clazz)) {
                result.add(createParameterValidation(baseKey.substring(selector.length() + 1), description, type,
                        requiredVal, regEx, validations));
            } else if ("Section".equalsIgnoreCase(clazz)) {
                result.add(createSectionValidation(baseKey.substring(selector.length() + 1), description, requiredVal,
                        validations));
            }
        }
        return result;
    }

    /**
     * Creates a parameter validation.
     * @param paramName the param name, not null.
     * @param description the optional description
     * @param type the param type, default is String.
     * @param reqVal the required value, default is 'false'.
     * @param regEx an optional regular expression to be checked for this param
     * @param validations the optional custom validations to be performed.
     * @return the new validation for this parameter.
     */
    private static Validation createParameterValidation(String paramName, String description, String type, String reqVal,
                                                       String regEx, String validations) {
        boolean required = "true".equalsIgnoreCase(reqVal);
        ParameterValidation.Builder builder = ParameterValidation.builder(paramName).setRequired(required)
                .setDescription(description).setExpression(regEx).setType(type);
        if (validations != null) {
            try {
                // TODO defined validator API
//                builder.addValidations(validations);
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Failed to load validations for " + paramName, e);
            }
        }
       return builder.build();
    }

    /**
     * Creates a section validation.
     * @param sectionName the section's name, not null.
     * @param description the optional description
     * @param reqVal the required value, default is 'false'.
     * @param validations the optional custom validations to be performed.
     * @return the new validation for this section.
     */
    private static Validation createSectionValidation(String sectionName, String description, String reqVal,
                                                     String validations) {
        boolean required = "true".equalsIgnoreCase(reqVal);
        AreaValidation.Builder builder = AreaValidation.builder(sectionName).setRequired(required)
                .setDescription(description);
        if (validations != null) {
            try {
                // TODO defined validator API
//                builder.addValidations(validations);
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Failed to load validations for " + sectionName, e);
            }
        }
        return builder.build();
    }
}
