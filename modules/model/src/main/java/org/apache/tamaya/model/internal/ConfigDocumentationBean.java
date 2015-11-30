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
package org.apache.tamaya.model.internal;

import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;
import org.apache.tamaya.model.ConfigModel;
import org.apache.tamaya.model.ConfigModelManager;
import org.apache.tamaya.model.ModelType;
import org.apache.tamaya.model.ValidationResult;
import org.apache.tamaya.model.spi.ConfigDocumentationMBean;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MBean implementation of {@link ConfigDocumentationMBean}.
 */
public class ConfigDocumentationBean implements ConfigDocumentationMBean{

    private final JsonWriterFactory writerFactory;

    private static final Comparator<ValidationResult> COMPARATOR = new Comparator<ValidationResult>() {
        @Override
        public int compare(ValidationResult v1, ValidationResult v2) {
            int compare = VAL_COMPARATOR.compare(v1.getConfigModel(), v2.getConfigModel());
            if(compare==0){
                compare = v1.getResult().compareTo(v2.getResult());
            }
            if(compare==0){
                return v1.getMessage().compareTo(v2.getMessage());
            }
            return compare;
        }
    };
    private static final Comparator<ConfigModel> VAL_COMPARATOR = new Comparator<ConfigModel>() {
        @Override
        public int compare(ConfigModel v1, ConfigModel v2) {
            int compare = v1.getType().compareTo(v2.getType());
            if(compare==0){
                compare = v1.getName().compareTo(v2.getName());
            }
            return compare;
        }
    };

    private Configuration config;

    /**
     * Default constructor, using the current configuration being available.
     */
    public ConfigDocumentationBean(){
        this(null);
    }


    /**
     * Creates an mbean bound to the given configuration. This is useful, when multiple mbeans for each
     * context should be used, e.g. one mbean per ear, app deployment.
     * @param config the configuration to be used.
     */
    public ConfigDocumentationBean(Configuration config){
        this.config = config;
        Map<String, Object> writerProperties = new HashMap<>(1);
        writerProperties.put(JsonGenerator.PRETTY_PRINTING, true);
        writerFactory = Json.createWriterFactory(writerProperties);
    }

    /**
     * Access the configuration.
     * @return either the configuration bound to this bean, or the current configuration.
     */
    private Configuration getConfig(){
        return config!=null?config: ConfigurationProvider.getConfiguration();
    }

    @Override
    public String validate(boolean showUndefined) {
        List<ValidationResult> validations = new ArrayList<>(ConfigModelManager.validate(getConfig(), showUndefined));
        Collections.sort(validations, COMPARATOR);
        JsonArrayBuilder builder = Json.createArrayBuilder();
        for(ValidationResult val:validations){
            builder.add(toJsonObject(val));
        }
        return formatJson(builder.build());
    }



    @Override
    public String getConfigurationModel() {
        List<ConfigModel> configModels = new ArrayList<>(ConfigModelManager.getModels());
        Collections.sort(configModels, VAL_COMPARATOR);
        JsonArrayBuilder result = Json.createArrayBuilder();
        for(ConfigModel val: configModels){
            result.add(toJsonObject(val));
        }
        return formatJson(result.build());
    }

    @Override
    public String getConfigurationModel(ModelType type) {
        return findValidationModels(type, ".*");
    }

    @Override
    public String findConfigurationModels(String namePattern) {
        List<ConfigModel> configModels = new ArrayList<>(ConfigModelManager.findModels(namePattern));
        Collections.sort(configModels, VAL_COMPARATOR);
        JsonArrayBuilder result = Json.createArrayBuilder();
        for(ConfigModel val: configModels){
            result.add(toJsonObject(val));
        }
        return formatJson(result.build());
    }

    @Override
    public String findValidationModels(ModelType type, String namePattern) {
        List<ConfigModel> configModels = new ArrayList<>(ConfigModelManager.findModels(type, namePattern));
        Collections.sort(configModels, VAL_COMPARATOR);
        JsonArrayBuilder result = Json.createArrayBuilder();
        for(ConfigModel val: configModels){
            result.add(toJsonObject(val));
        }
        return formatJson(result.build());
    }

    @Override
    public String toString(){
        return "ConfigDocumentationBean, config: " + (this.config!=null?this.config.toString():"<current>");
    }


    private JsonObject toJsonObject(ConfigModel val) {
        JsonObjectBuilder valJson = Json.createObjectBuilder().add("type", val.getType().toString())
                .add("name", val.getName());
        if(val.getDescription()!=null) {
            valJson.add("description", val.getDescription());
        }
        if(val.isRequired()){
            valJson.add("required",true);
        }
        return valJson.build();
    }

    private JsonObject toJsonObject(ValidationResult val) {
        JsonObjectBuilder valJson = Json.createObjectBuilder().add("type", val.getConfigModel().getType().toString())
                .add("name", val.getConfigModel().getName());
        if(val.getConfigModel().isRequired()){
            valJson.add("required",true);
        }
        if(val.getConfigModel().getDescription() != null){
            valJson.add("description", val.getConfigModel().getDescription());
        }
        valJson.add("result", val.getResult().toString());
        if( val.getMessage() != null) {
            valJson.add("message", val.getMessage());
        }
        return valJson.build();
    }

    private String formatJson(JsonArray data) {
        StringWriter writer = new StringWriter();
        JsonWriter gen = writerFactory.createWriter(writer);
        gen.writeArray(data);
        return writer.toString();
    }
}
