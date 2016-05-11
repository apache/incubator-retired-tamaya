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
import org.apache.tamaya.staged.spi.DSLPropertySourceProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Configuration class setting up the Tamaya runtime model.
 */
public final class TamayaConfigurator {

    private static final Logger LOGGER = Logger.getLogger(MetaConfiguration.class.getName());
    private static final Comparator<WrappedPropertySource> ORDINAL_COMPARATOR =
            new Comparator<WrappedPropertySource>(){
                @Override
                public int compare(WrappedPropertySource o1, WrappedPropertySource o2) {
                    return o1.getOrdinal() - o2.getOrdinal();
                }
            };

    private static final TamayaConfigurator INSTANCE = new TamayaConfigurator();

    private ConfigurationContext configurationContext;
    private Set<String> formats = new HashSet<>();
    private Set<String> suffixes = new HashSet<>();
    private Map<String,DSLPropertySourceProvider> dslResolvers = new HashMap<>();


    private TamayaConfigurator(){
        configure(null);
    }

    /**
     * Get the singleton instance.
     * @return the instance, never null.
     */
    public static TamayaConfigurator getInstance(){
        return INSTANCE;
    }

    /**
     * Configures the Tamaya runtime using the metamodel configuration found at the default
     * location.
     * @see MetaConfiguration
     */
    public void configure(){
        configure(null);
    }

    /**
     * Configures the Tamaya runtime using the given resource location expression and (optionally)
     * formats to be used for evaluating the metamodel configuration.
     * @param resourceExpression resource expression for resolving the location of the
     *                           meta configuration.
     * @param formats the format names to be used, optional, but not null.
     * @see MetaConfiguration
     */
    public void configure(String resourceExpression, String... formats){
        loadDSLSourceResolvers();
        Configuration metaConfig = MetaConfiguration.getConfiguration(resourceExpression, formats);
        Configuration profilesConfig = metaConfig.with(
                ConfigurationFunctions.section("TAMAYA.PROFILES.", true));
        Configuration metaProfile = profilesConfig.with(
                ConfigurationFunctions.section("<COMMON>.", true));
        System.out.println(metaProfile.getProperties().keySet());
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
            PropertySource> defaultPropertySources, int nextOrdinal) {
        String[] values;
        List<PropertySource> propertySourcesLoaded = new ArrayList<>();
        values = metaProfile.getOrDefault("sources","<default>").split(",");
        if(values.length==1 && "<default>".equals(values[0])){
            // load default property sources and providers from config as default.
            // additional providers may be added depending on the active profile...
            LOGGER.info("Using default configuration setup for "+profileId);
            nextOrdinal = addAndGetNextOrdinal(builder, defaultPropertySources.values(),
                    propertySourcesLoaded, nextOrdinal);
        }else {
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
                DSLPropertySourceProvider resolver = dslResolvers.get(sourceKey);
                if(resolver==null){
                    LOGGER.warning("DSL error: unresolvable source expression: "+ source);
                    continue;
                }
                List<PropertySource> sources = resolver.resolve(source.substring(sourceKey.length()),
                        defaultPropertySources);
                nextOrdinal = addAndGetNextOrdinal(builder, sources, propertySourcesLoaded, nextOrdinal);
            }
            LOGGER.info("Loaded "+count+" DSL based "+profileId+" configuration contexts.");
        }
        return nextOrdinal;
    }

    private int addAndGetNextOrdinal(ConfigurationContextBuilder builder, Collection<PropertySource> sourcesToAdd,
                                     List<PropertySource> allPropertySources, int nextOrdinal) {
        allPropertySources.addAll(wrapOrdinals(nextOrdinal, sourcesToAdd));
        nextOrdinal = Math.max(calculateHighestOrdinal(allPropertySources)+1, nextOrdinal+1);
        builder.addPropertySources(allPropertySources);
        return nextOrdinal;
    }

    private List<WrappedPropertySource> wrapOrdinals(int nextOrdinal, Collection<PropertySource> propertySources) {
        List<WrappedPropertySource> result = new ArrayList<>();
        for(PropertySource ps: propertySources){
            result.add(WrappedPropertySource.of(ps));
        }
        Collections.sort(result, ORDINAL_COMPARATOR);
        for(WrappedPropertySource ps: result){
            ps.setOrdinal(nextOrdinal++);
        }
        Collections.sort(result, ORDINAL_COMPARATOR);
        return result;
    }

    private int calculateHighestOrdinal(Collection<PropertySource> sources) {
        int maxOrdinal = 0;
        for (PropertySource ps : sources) {
            if (ps.getOrdinal() > maxOrdinal) {
                maxOrdinal = ps.getOrdinal();
            }
        }
        return maxOrdinal;
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
        for(DSLPropertySourceProvider res:
                ServiceContextManager.getServiceContext().getServices(
                        DSLPropertySourceProvider.class)){
            this.dslResolvers.put(res.getKey(), res);
        }
    }

}
