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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.tamaya.model.ConfigModel;

/**
 * Utility class to read metamodel information from properties. Hereby these properties can be part of a
 * configuration (containing other entriees as well) or be dedicated model definition properties read
 * from any kind of source.
 */
public final class ConfigModelReader {

    /** The default model entries selector. */
    private static final String DEFAULT_META_INFO_SELECTOR = ".model";
    /** parameter to change the selector to be used for filtering out the target values to be used. */
    private static final String META_INFO_SELECTOR_PARAM = "org.apache.tamaya.model.integrated.selector";

    /**
     * Utility class only.
     */
    private ConfigModelReader(){}


    /**
     * Loads validations as configured in the given properties.
     * @param props the properties to be read
     * @return a collection of config validations.
     */
    public static Collection<ConfigModel> loadValidations(Map<String,String> props) {
        List<ConfigModel> result = new ArrayList<>();
        Set<String> itemKeys = new HashSet<>();
        for (Object key : props.keySet()) {
            if (key.toString().startsWith("_") &&
                    key.toString().endsWith(DEFAULT_META_INFO_SELECTOR + ".target")) {
                itemKeys.add(key.toString().substring(0, key.toString().length() - ".model.target".length()));
            }
        }
        for (String baseKey : itemKeys) {
            String target = props.get(baseKey + ".model.target");
            String type = props.get(baseKey + ".model.type");
            if (type == null) {
                type = String.class.getName();
            }
            String value = props.get(baseKey + ".model.transitive");
            boolean transitive = false;
            if(value!=null) {
                transitive = Boolean.parseBoolean(value);
            }
            String description = props.get(baseKey + ".model.description");
            String regEx = props.get(baseKey + ".model.expression");
            String validations = props.get(baseKey + ".model.validations");
            String requiredVal = props.get(baseKey + ".model.required");
            String targetKey = baseKey.substring(1);
            if ("Parameter".equalsIgnoreCase(target)) {
                result.add(createParameterValidation(targetKey,
                        description, type, requiredVal, regEx, validations));
            } else if ("Section".equalsIgnoreCase(target)) {
                if(transitive){
                    result.add(createSectionValidation(targetKey+".*",
                            description, requiredVal, validations));
                } else {
                    result.add(createSectionValidation(targetKey,
                            description, requiredVal, validations));
                }
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
    private static ConfigModel createParameterValidation(String paramName, String description, String type, String reqVal,
                                                         String regEx, String validations) {
        boolean required = "true".equalsIgnoreCase(reqVal);
        ParameterModel.Builder builder = ParameterModel.builder(paramName).setRequired(required)
                .setDescription(description).setExpression(regEx).setType(type);
//        if (validations != null) {
//            try {
//                // TODO define validator API
////                builder.addValidations(loadValidations(validations));
//            } catch (Exception e) {
//                LOGGER.log(Level.WARNING, "Failed to load validations for " + paramName, e);
//            }
//        }
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
    private static ConfigModel createSectionValidation(String sectionName, String description, String reqVal,
                                                       String validations) {
        boolean required = "true".equalsIgnoreCase(reqVal);
        SectionModel.Builder builder = SectionModel.builder(sectionName).setRequired(required)
                .setDescription(description);
//        if (validations != null) {
//            try {
//                // TODO define validator API
////                builder.addValidations(loadValidations(valiadtions));
//            } catch (Exception e) {
//                LOGGER.log(Level.WARNING, "Failed to load validations for " + sectionName, e);
//            }
//        }
        return builder.build();
    }
}
