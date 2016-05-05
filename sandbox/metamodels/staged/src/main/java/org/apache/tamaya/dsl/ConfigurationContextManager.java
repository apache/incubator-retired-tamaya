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
package org.apache.tamaya.dsl;

import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;
import org.apache.tamaya.functions.ConfigurationFunctions;
import org.apache.tamaya.spi.*;
import org.apache.tamaya.staged.spi.DSLSourceResolver;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Created by atsticks on 05.05.16.
 */
public class ConfigurationContextManager {

    private static final Logger LOGGER = Logger.getLogger(EnvConfig.class.getName());

    private ConfigurationContext configurationContext;
    private Set<String> formats = new HashSet<>();
    private Set<String> suffixes = new HashSet<>();
    private Map<String,DSLSourceResolver> dslResolvers = new HashMap<>();


    public ConfigurationContextManager(){
        loadDSLSourceResolvers();
        Configuration metaConfig = EnvConfig.getMetaConfiguration();
        Configuration profilesConfig = metaConfig.with(
                ConfigurationFunctions.section("TAMAYA.PROFILES", true));
        Configuration metaProfile = profilesConfig.with(
                ConfigurationFunctions.section("<COMMON>", true));
        String[] values = metaProfile.getOrDefault("formats","yaml, properties, xml-properties").split(",");
        for(String fmt:values){
            fmt = fmt.trim();
            if(fmt.isEmpty()){
                continue;
            }
            this.formats.add(fmt);
        }
        values = metaProfile.getOrDefault("suffixes", "yml, yaml, properties, xml").split(",");
        for(String sfx:values){
            sfx = sfx.trim();
            if(sfx.isEmpty()){
                continue;
            }
            this.suffixes.add(sfx);
        }
        ConfigurationContextBuilder builder = ConfigurationProvider.getConfigurationContextBuilder();
        Map<String, PropertySource> loadedPropertySources = loadDefaultPropertySources();
        int defaultOrdinal = loadSources(builder, "<COMMON>", metaProfile, loadedPropertySources, 0) + 20;
        // Load current profiles
        for(String profile:ProfileManager.getInstance().getActiveProfiles()){
            metaProfile = profilesConfig.with(
                    ConfigurationFunctions.section(profile, true));
            defaultOrdinal = loadSources(builder, profile, metaProfile, loadedPropertySources, defaultOrdinal) + 20;
        }
//         formats:  yaml, properties, xml-properties
//    - SUFFIX:   yaml, yml, properties, xml
//    - sources:
//      - "named:env-properties"   # provider name, or class name
//      - "named:main-args"
//      - "named:sys-properties"
//      - "resource:classpath:META-INF/config/**/*.SUFFIX"

}

    /**
     * Loads all default registered property sources and providers.
     * @return the default property sources available on the system.
     */
    private Map<String, PropertySource> loadDefaultPropertySources() {
        Map<String, PropertySource> loadedPropertySources = new HashMap<>();
        for(PropertySource ps: ServiceContextManager.getServiceContext().getServices(PropertySource.class)){
            loadedPropertySources.put(ps.getName(), ps);
        }
        for(PropertySourceProvider prov: ServiceContextManager.getServiceContext().getServices(PropertySourceProvider.class)){
            for(PropertySource ps: prov.getPropertySources()){
                loadedPropertySources.put(ps.getName(), ps);
            }
        }
        return loadedPropertySources;
    }

    private int loadSources(ConfigurationContextBuilder builder, String profileId, Configuration metaProfile, Map<String,
            PropertySource> defaultPropertySources, int defaultOrdinal) {
        String[] values;
        values = metaProfile.getOrDefault("sources","<default>").split(",");
        if(values.length==1 && "<default>".equals(values[0])){
            // load default property sources and providers from config as default.
            // additional providers may be added depending on the active profile...
            LOGGER.info("Using default configuration setup for "+profileId);
            builder.addPropertySources(defaultPropertySources.values());
        }else {
            int newMaxOrdinal = defaultOrdinal;
            LOGGER.info("Loading DSL based "+profileId+" configuration context...");
            int count = 0;
            for (String source : values) {
                source = source.trim();
                if (source.isEmpty()) {
                    continue;
                }
                String sourceKey = getSourceKey(source);
                LOGGER.info("Loading "+profileId+" configuration source: " + source);
                // evaluate DSLSourceResolver and resolve PropertySources, register thm into context
                // apply newMaxOrdinal...
                DSLSourceResolver resolver = dslResolvers.get(sourceKey);
                if(resolver==null){
                    LOGGER.warning("DSL error: unresolvable source expression: "+ source);
                    continue;
                }
                List<PropertySource> sources = resolver.resolve(source.substring(sourceKey.length()), defaultPropertySources, defaultOrdinal);
                count += sources.size();
                for(PropertySource ps:sources){
                    if(ps.getOrdinal()>newMaxOrdinal){
                        newMaxOrdinal = ps.getOrdinal();
                    }
                }
                builder.addPropertySources(sources);
            }
            LOGGER.info("Loaded "+count+" DSL based "+profileId+" configuration contexts.");
            return newMaxOrdinal;
        }
        return defaultOrdinal;
    }

    private String getSourceKey(String source) {
        int index = source.indexOf(':');
        if(index>0){
            return source.substring(0,index);
        }
        return source;
    }

    private void loadDSLSourceResolvers() {
        // Load the ConfigurationDSLSourceResolvers on the system
        for(DSLSourceResolver res:ServiceContextManager.getServiceContext().getServices(DSLSourceResolver.class)){
            this.dslResolvers.put(res.getKey(), res);
        }
    }

}
