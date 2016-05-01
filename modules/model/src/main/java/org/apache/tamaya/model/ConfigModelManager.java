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
import org.apache.tamaya.model.spi.ModelProviderSpi;
import org.apache.tamaya.spi.ServiceContextManager;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Validator accessor to validate the current configuration.
 */
public final class ConfigModelManager {

    /** The logger used. */
    private static final Logger LOG = Logger.getLogger(ConfigModelManager.class.getName());

    /**
     * Singleton constructor.
     */
    private ConfigModelManager() {
    }

    /**
     * Access the usage statistics for the recorded uses of configuration.
     */
    public static String getConfigInfoText(){
        StringBuilder b = new StringBuilder();
        List<ConfigModel> models = new ArrayList<>(getModels());
        Collections.sort(models, new Comparator<ConfigModel>() {
            @Override
            public int compare(ConfigModel k1, ConfigModel k2) {
                return k2.getName().compareTo(k2.getName());
            }
        });
        for(ConfigModel model:models){
            b.append(model.getName()).append('(').append(model.getType())
                    .append("):\n  ").append(
            model.getDescription()).append("mandatory=").append(model.isRequired());
            b.append('\n');
        }
        return b.toString();
    }

    /**
     * Get the validations defined.
     *
     * @return the sections defined, never null.
     */
    public static Collection<ConfigModel> getModels() {
        List<ConfigModel> result = new ArrayList<>();
        for (ModelProviderSpi model : ServiceContextManager.getServiceContext().getServices(ModelProviderSpi.class)) {
            result.addAll(model.getConfigModels());
        }
        return result;
    }


    /**
     * Find the validations by matching the validation's name against the given model type.
     * 
     * @param name the name to use, not null.
     * @param modelType classname of the target model type.  
     * @param <T> type of the model to filter for.
     * @return the sections defined, never null.
     */
    public static <T extends ConfigModel> T getModel(String name, Class<T> modelType) {
        for (ModelProviderSpi model : ServiceContextManager.getServiceContext().getServices(ModelProviderSpi.class)) {
            for(ConfigModel configModel : model.getConfigModels()) {
                if(configModel.getName().equals(name) && configModel.getClass().equals(modelType)) {
                    return modelType.cast(configModel);
                }
            }
        }
        return null;
    }

    /**
     * Find the validations by checking the validation's name using the given regular expression.
     * @param namePattern the regular expression to use, not null.
     * @param targets the target types only to be returned (optional).
     * @return the sections defined, never null.
     */
    public static Collection<ConfigModel> findModels(String namePattern, ModelTarget... targets) {
        List<ConfigModel> result = new ArrayList<>();
        for (ModelProviderSpi model : ServiceContextManager.getServiceContext().getServices(ModelProviderSpi.class)) {
            for(ConfigModel configModel : model.getConfigModels()) {
                if(configModel.getName().matches(namePattern)) {
                    if(targets.length>0){
                        for(ModelTarget tgt:targets){
                            if(configModel.getType().equals(tgt)){
                                result.add(configModel);
                                break;
                            }
                        }
                    }else {
                        result.add(configModel);
                    }
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
    public static Collection<Validation> validate() {
        return validate(false);
    }

    /**
     * Validates the current configuration.
     * @param showUndefined show any unknown parameters.
     * @return the validation results, never null.
     */
    public static Collection<Validation> validate(boolean showUndefined) {
        return validate(ConfigurationProvider.getConfiguration(), showUndefined);
    }

    /**
     * Validates the given configuration.
     *
     * @param config the configuration to be validated against, not null.
     * @return the validation results, never null.
     */
    public static Collection<Validation> validate(Configuration config) {
        return validate(config, false);
    }

    /**
     * Validates the given configuration.
     *
     * @param config the configuration to be validated against, not null.
     * @param showUndefined allows filtering for undefined configuration elements.
     * @return the validation results, never null.
     */
    public static Collection<Validation> validate(Configuration config, boolean showUndefined) {
        List<Validation> result = new ArrayList<>();
        for (ConfigModel defConf : getModels()) {
            result.addAll(defConf.validate(config));
        }
        if(showUndefined){
            Map<String,String> map = new HashMap<>(config.getProperties());
            Set<String> areas = extractTransitiveAreas(map.keySet());
            for (ConfigModel defConf : getModels()) {
                if(ModelTarget.Section.equals(defConf.getType())){
                    for (Iterator<String> iter = areas.iterator();iter.hasNext();){
                        String area = iter.next();
                        if(area.matches(defConf.getName())){
                            iter.remove();
                        }
                    }
                }
                if(ModelTarget.Parameter.equals(defConf.getType())){
                    map.remove(defConf.getName());
                }
            }
            outer:for(Map.Entry<String,String> entry:map.entrySet()){
                for (ConfigModel defConf : getModels()) {
                    if(ModelTarget.Section.equals(defConf.getType())){
                        if(defConf.getName().endsWith(".*") && entry.getKey().matches(defConf.getName())){
                            // Ignore parameters that are part of transitive section.
                            continue outer;
                        }
                    }
                }
                result.add(Validation.ofUndefined(entry.getKey(), ModelTarget.Parameter));
            }
            for(String area:areas){
                result.add(Validation.ofUndefined(area, ModelTarget.Section));
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
     * 
     * @param context allows to specify an additional MBean context, maybe {@code null}. 
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
