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
package org.apache.tamaya.model;

import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;
import org.apache.tamaya.model.spi.ConfigDocumentationMBean;
import org.apache.tamaya.model.spi.ValidationProviderSpi;
import org.apache.tamaya.spi.ServiceContextManager;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Validator accessor to validate the current configuration.
 */
public final class ConfigValidator {

    /** The logger used. */
    private final static Logger LOG = Logger.getLogger(ConfigValidator.class.getName());

    /**
     * Singleton constructor.
     */
    private ConfigValidator() {
    }

    /**
     * Get the validations defined.
     *
     * @return the sections defined, never null.
     */
    public static Collection<Validation> getValidations() {
        List<Validation> result = new ArrayList<>();
        for (ValidationProviderSpi model : ServiceContextManager.getServiceContext().getServices(ValidationProviderSpi.class)) {
            result.addAll(model.getValidations());
        }
        return result;
    }

    /**
     * Find the validations by checking the validation's name using the given regular expression.
     * @param type the target ValidationType, not null.
     * @param namePattern the regular expression to use, not null.
     * @return the sections defined, never null.
     */
    public static Collection<Validation> findValidations(ValidationType type, String namePattern) {
        List<Validation> result = new ArrayList<>();
        for (ValidationProviderSpi model : ServiceContextManager.getServiceContext().getServices(ValidationProviderSpi.class)) {
            for(Validation validation: model.getValidations()) {
                if(validation.getName().matches(namePattern) && validation.getType()==type) {
                    result.add(validation);
                }
            }
        }
        return result;
    }

    /**
     * Find the validations by checking the validation's name using the given regular expression.
     * @param name the name to use, not null.
     * @return the sections defined, never null.
     */
    public static <T extends Validation> T getValidation(String name, Class<T> validationType) {
        for (ValidationProviderSpi model : ServiceContextManager.getServiceContext().getServices(ValidationProviderSpi.class)) {
            for(Validation validation: model.getValidations()) {
                if(validation.getName().equals(name) && validation.getClass().equals(validationType)) {
                    return (T)validation;
                }
            }
        }
        return null;
    }

    /**
     * Find the validations by checking the validation's name using the given regular expression.
     * @param namePattern the regular expression to use, not null.
     * @return the sections defined, never null.
     */
    public static Collection<Validation> findValidations(String namePattern) {
        List<Validation> result = new ArrayList<>();
        for (ValidationProviderSpi model : ServiceContextManager.getServiceContext().getServices(ValidationProviderSpi.class)) {
            for(Validation validation: model.getValidations()) {
                if(validation.getName().matches(namePattern)) {
                    result.add(validation);
                }
            }
        }
        return result;
    }

    /**
     * Validates the current configuration.
     *
     * @return the validation results, never null.
     */
    public static Collection<ValidationResult> validate() {
        return validate(false);
    }

    /**
     * Validates the current configuration.
     * @param showUndefined show any unknown parameters.
     * @return the validation results, never null.
     */
    public static Collection<ValidationResult> validate(boolean showUndefined) {
        return validate(ConfigurationProvider.getConfiguration(), showUndefined);
    }

    /**
     * Validates the given configuration.
     *
     * @param config the configuration to be validated against, not null.
     * @return the validation results, never null.
     */
    public static Collection<ValidationResult> validate(Configuration config) {
        return validate(config, false);
    }

    /**
     * Validates the given configuration.
     *
     * @param config the configuration to be validated against, not null.
     * @return the validation results, never null.
     */
    public static Collection<ValidationResult> validate(Configuration config, boolean showUndefined) {
        List<ValidationResult> result = new ArrayList<>();
        for (Validation defConf : getValidations()) {
            result.addAll(defConf.validate(config));
        }
        if(showUndefined){
            Map<String,String> map = new HashMap<>(config.getProperties());
            Set<String> areas = extractTransitiveAreas(map.keySet());
            for (Validation defConf : getValidations()) {
                if(ValidationType.Section.equals(defConf.getType())){
                    for (Iterator<String> iter = areas.iterator();iter.hasNext();){
                        String area = iter.next();
                        if(area.matches(defConf.getName())){
                            iter.remove();
                        }
                    }
                }
                if(ValidationType.Parameter.equals(defConf.getType())){
                    map.remove(defConf.getName());
                }
            }
            outer:for(Map.Entry<String,String> entry:map.entrySet()){
                for (Validation defConf : getValidations()) {
                    if(ValidationType.Section.equals(defConf.getType())){
                        if(defConf.getName().endsWith(".*") && entry.getKey().matches(defConf.getName())){
                            // Ignore parameters that are part of transitive section.
                            continue outer;
                        }
                    }
                }
                result.add(ValidationResult.ofUndefined(entry.getKey(), ValidationType.Parameter, null));
            }
            for(String area:areas){
                result.add(ValidationResult.ofUndefined(area, ValidationType.Section, null));
            }
        }
        return result;
    }

    private static java.util.Set<java.lang.String> extractTransitiveAreas(Set<String> keys) {
        Set<String> transitiveClosure = new HashSet<>();
        for(String key:keys){
            int index = key.lastIndexOf('.');
            while(index>0){
                String areaKey = key.substring(0,index);
                transitiveClosure.add(areaKey);
                index = areaKey.lastIndexOf('.');
            }
        }
        return transitiveClosure;
    }


    /**
     * Registers the {@link ConfigDocumentationMBean} mbean for accessing config documentation into the local platform
     * mbean server.
     */
    public static void registerMBean() {
        registerMBean(null);
    }

    /**
     * Registers the {@link ConfigDocumentationMBean} mbean for accessing config documentation into the local platform
     * mbean server.
     */
    public static void registerMBean(String context) {
        try{
            ConfigDocumentationMBean configMbean = ServiceContextManager.getServiceContext()
                    .getService(ConfigDocumentationMBean.class);
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            ObjectName on = context==null?new ObjectName("org.apache.tamaya.model:type=ConfigDocumentationMBean"):
                    new ObjectName("org.apache.tamaya.model:type=ConfigDocumentationMBean,context="+context);
            try{
                mbs.getMBeanInfo(on);
                LOG.warning("Cannot register mbean " + on + ": already existing.");
            } catch(InstanceNotFoundException e) {
                LOG.info("Registering mbean " + on + "...");
                mbs.registerMBean(configMbean, on);
            }
        } catch(Exception e){
            LOG.log(Level.WARNING,
                    "Failed to register ConfigDocumentationMBean.", e);
        }
    }

}
