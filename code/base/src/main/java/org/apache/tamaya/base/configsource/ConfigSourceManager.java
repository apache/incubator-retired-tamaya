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
package org.apache.tamaya.base.configsource;

import org.apache.tamaya.base.FormatUtils;
import org.apache.tamaya.spi.*;

import javax.config.Config;
import javax.config.spi.ConfigSource;
import javax.config.spi.ConfigSourceProvider;
import java.util.*;
import java.util.logging.Logger;

/**
 * Implementation of the Configuration API. This class uses the current {@link Config} to evaluate the
 * chain of {@link javax.config.spi.ConfigSource} and {@link Filter}
 * instance to evaluate the current Configuration.
 */
public class ConfigSourceManager {
    /**
     * The logger.
     */
    private static final Logger LOG = Logger.getLogger(ConfigSourceManager.class.getName());
    /**
     * The current list of loaded {@link ConfigSource} instances.
     */
    private final List<ConfigSource> configSources = new ArrayList<>();

    /**
     * The overriding policy used when combining PropertySources registered to evalute the final configuration
     * values.
     */
    ConfigValueCombinationPolicy configValueCombinationPolicy;

    private ClassLoader classloader = ServiceContext.defaultClassLoader();

    /**
     * Create a new filter manager.
     */
    public ConfigSourceManager(){
        configValueCombinationPolicy = ServiceContextManager.getServiceContext().getService(
                ConfigValueCombinationPolicy.class);
        if(configValueCombinationPolicy == null){
            configValueCombinationPolicy = ConfigValueCombinationPolicy.DEFAULT_OVERRIDING_COLLECTOR;
        }
        LOG.info("Using PropertyValueCombinationPolicy: " + configValueCombinationPolicy);
    }

    /**
     * Create a new filter manager.
     * @param configSources the config sources to be used, not null.
     */
    public ConfigSourceManager(List<ConfigSource> configSources){
        this.configSources.addAll(configSources);
        LOG.info("Registered " + configSources.size() + " config sources: " + configSources);
    }

    /**
     * Get the classloader used for instance creation.
     * @return the classloader, never null.
     */
    public ClassLoader getClassloader(){
        return classloader;
    }

    /**
     * Sets the classloader to use for loading of instances.
     * @param ClassLoader the classloader, not null.
     * @return this instance for chaining.
     */
    public ConfigSourceManager setClassloader(ClassLoader ClassLoader){
        this.classloader = Objects.requireNonNull(classloader);
        return this;
    }

    /**
     * The current unmodifiable list of loaded {@link ConfigSource} instances.
     */
    public List<ConfigSource> getSources(){
        return Collections.unmodifiableList(configSources);
    }

    /**
     * Registers a new ConfigSource instance.
     *
     * @param configSources  the config source, not {@code null}.
     */
    public ConfigSourceManager addSources(Collection<ConfigSource> configSources) {
        Objects.requireNonNull(configSources);
        for(ConfigSource configSource:configSources) {
            if (!this.configSources.contains(configSource)) {
                this.configSources.add(configSource);
            }
        }
        return this;
    }

    public ConfigSourceManager addDiscoveredSources() {
        List<ConfigSource> propertySources = new ArrayList<>();
        addCoreSources(propertySources);
        for(ConfigSource ps: ServiceContextManager.getServiceContext().getServices(ConfigSource.class)) {
            if(!propertySources.contains(ps)){
                propertySources.add(ps);
            }
        }
        for(ConfigSourceProvider provider:
                ServiceContextManager.getServiceContext().getServices(ConfigSourceProvider.class)){
            provider.getConfigSources(classloader).forEach(propertySources::add);
        }
        Collections.sort(propertySources, ConfigSourceComparator.getInstance());
        return addSources(propertySources);
    }

    protected ConfigSourceManager addCoreSources(List<ConfigSource> propertySources) {
        for(ConfigSource ps: new ConfigSource[]{
                new EnvironmentConfigSource(),
                new JavaConfigurationConfigSource(),
                new CLIConfigSource(),
                new SystemConfigSource()
        }){
            if(!propertySources.contains(ps)){
                propertySources.add(ps);
            }
        }
        return this;
    }

    /**
     * Registers a new ConfigSource instance.
     *
     * @param configSources  the config source, not {@code null}.
     */
    public ConfigSourceManager addSources(ConfigSource... configSources) {
        return addSources(Arrays.asList(configSources));
    }


    /**
     * Access a {@link ConfigSource} using its (unique) name.
     * @param name the propoerty source's name, not {@code null}.
     * @return the propoerty source found, or {@code null}.
     */
    public ConfigSource getSource(String name) {
        for(ConfigSource ps: getSources()){
            if(name.equals(ps.getName())){
                return ps;
            }
        }
        return null;
    }

    /**
     * Removes the given config sources, if existing. The existing order of config
     * sources is preserved.
     *
     * @param configSources the config sources to remove, not {@code null}.
     * @return the builder for chaining.
     */
    public final ConfigSourceManager removeSources(ConfigSource... configSources) {
        return removeSources(Arrays.asList(configSources));
    }
    /**
     * Removes the given config sources, if existing. The existing order of config
     * sources is preserved.
     *
     * @param configSources the config sources to remove, not {@code null}.
     * @return the builder for chaining.
     */
    public ConfigSourceManager removeSources(Collection<ConfigSource> configSources) {
        this.configSources.removeAll(configSources);
        return this;
    }

    /**
     * Increases the priority of the given property source, by moving it towards the end
     * of the chain of property sources. If the property source given is already at the end
     * this method has no effect. This operation does not change any ordinal values.
     *
     * @param configSource the config source to be incresed regarding its significance.
     * @return the builder for chaining.
     * @throws IllegalArgumentException If no such property source exists in the current
     * chain.
     */
    public ConfigSourceManager increasePriority(ConfigSource configSource){
        int index = configSources.indexOf(configSource);
        if(index<0){
            throw new IllegalArgumentException("No such ConfigSource: " + configSource);
        }
        if(index<(configSources.size()-1)){
            configSources.remove(configSource);
            configSources.add(index+1, configSource);
        }
        return this;
    }

    /**
     * Decreases the priority of the given property source, by moving it towards the start
     * of the chain of property sources. If the property source given is already the first
     * this method has no effect. This operation does not change any ordinal values.
     *
     * @param configSource the config source to be decresed regarding its significance.
     * @return the builder for chaining.
     * @throws IllegalArgumentException If no such property source exists in the current
     * chain.
     */
    public ConfigSourceManager decreasePriority(ConfigSource configSource){
        int index = configSources.indexOf(configSource);
        if(index<0){
            throw new IllegalArgumentException("No such ConfigSource: " + configSource);
        }
        if(index>0){
            configSources.remove(configSource);
            configSources.add(index-1, configSource);
        }
        return this;
    }

    /**
     * Increases the priority of the given property source to be maximal, by moving it to
     * the tail of the of property source chain. If the property source given is
     * already the last item this method has no effect. This operation does not change
     * any ordinal values.
     *
     * @param configSource the config source to be maximized regarding its significance.
     * @return the builder for chaining.
     * @throws IllegalArgumentException If no such property source exists in the current
     * chain.
     */
    public ConfigSourceManager highestPriority(ConfigSource configSource){
        int index = configSources.indexOf(configSource);
        if(index<0){
            throw new IllegalArgumentException("No such ConfigSource: " + configSource);
        }
        if(index<(configSources.size()-1)){
            configSources.remove(configSource);
            configSources.add(configSource);
        }
        return this;
    }

    /**
     * Decreases the priority of the given property source to be minimal, by moving it to
     * the start of the chain of property source chain. If the property source given is
     * already the first item this method has no effect. This operation does not change
     * any ordinal values.
     *
     * @param configSource the config source to be minimized regarding its significance.
     * @return the builder for chaining.
     * @throws IllegalArgumentException If no such property source exists in the current
     * chain.
     */
    public ConfigSourceManager lowestPriority(ConfigSource configSource){
        int index = configSources.indexOf(configSource);
        if(index<0){
            throw new IllegalArgumentException("No such PropertySource: " + configSource);
        }
        if(index>0){
            configSources.remove(configSource);
            configSources.add(0, configSource);
        }
        return this;
    }

    public ConfigSourceManager sortSources(Comparator<ConfigSource> comparator) {
        Collections.sort(configSources, comparator);
        return this;
    }

    /**
     * Access the {@link ConfigValueCombinationPolicy} used to evaluate the final
     * property values.
     * @return the {@link ConfigValueCombinationPolicy} used, never null.
     */
    public ConfigValueCombinationPolicy getConfigValueCombinationPolicy(){
        return configValueCombinationPolicy;
    }

    /**
     * Removes all contained items.
     * @return this instance for chaining.
     */
    public ConfigSourceManager clear() {
        this.configSources.clear();
        this.configValueCombinationPolicy = ConfigValueCombinationPolicy.DEFAULT_OVERRIDING_POLICY;
        return this;
    }

    /**
     * Evaluates the raw value using the context's PropertyValueCombinationPolicy.
     * @param key the key, not null.
     * @return the value, before filtering is applied.
     */
    public String evaluteRawValue(String key) {
        List<ConfigSource> configSources = getSources();
        String unfilteredValue = null;
        ConfigValueCombinationPolicy combinationPolicy = getConfigValueCombinationPolicy();
        for (ConfigSource propertySource : configSources) {
            unfilteredValue = combinationPolicy.collect(unfilteredValue, key, propertySource);
        }
        return unfilteredValue;
    }

    public Map<String, String> evaluateRawValues() {
        List<ConfigSource> configSources = getSources();
        ConfigValueCombinationPolicy combinationPolicy = getConfigValueCombinationPolicy();
        Map<String, String> result = new HashMap<>();
        for (ConfigSource propertySource : configSources) {
            for (Map.Entry<String,String> propEntry: propertySource.getProperties().entrySet()) {
                String unfilteredValue = result.get(propEntry.getKey());
                unfilteredValue = combinationPolicy.
                        collect(unfilteredValue, propEntry.getKey(), propertySource);
                if(unfilteredValue!=null){
                    result.put(propEntry.getKey(), unfilteredValue);
                }
            }
        }
        return result;
    }


    @Override
    public String toString() {
        StringBuilder b = new StringBuilder("Config Sources\n");
             b.append("--------------\n");
        if(configSources.isEmpty()){
            b.append("  No config sources loaded.\n\n");
        }else {
            b.append("  CLASS                         NAME                                                                  ORDINAL SCANNABLE SIZE    STATE     ERROR\n\n");
            for (ConfigSource ps : configSources) {
                b.append("  ");
                FormatUtils.appendFormatted(b, ps.getClass().getSimpleName(), 30);
                FormatUtils.appendFormatted(b, ps.getName(), 70);
                FormatUtils.appendFormatted(b, String.valueOf(ConfigSourceComparator.getOrdinal(ps)), 8);
                String state = ps.getValue("_state");
                if(state==null){
                    FormatUtils.appendFormatted(b, "OK", 10);
                }else {
                    FormatUtils.appendFormatted(b, state, 10);
                    if("ERROR".equals(state)){
                        String val = ps.getValue("_exception");
                        if(val!=null) {
                            FormatUtils.appendFormatted(b, val, 30);
                        }
                    }
                }
                b.append('\n');
            }
            b.append("\n");
        }
        b.append("\n");
        b.append("  ConfigValueCombinationPolicy: " + configValueCombinationPolicy.getClass().getName()).append('\n');
        return b.toString();
    }

    public void setConfigValueCombinationPolicy(ConfigValueCombinationPolicy configValueCombinationPolicy) {
        this.configValueCombinationPolicy = Objects.requireNonNull(configValueCombinationPolicy);
    }

}
