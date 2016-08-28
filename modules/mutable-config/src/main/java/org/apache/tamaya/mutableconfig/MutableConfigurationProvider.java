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
package org.apache.tamaya.mutableconfig;

import org.apache.tamaya.ConfigException;
import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;
import org.apache.tamaya.mutableconfig.spi.ConfigChangeRequest;
import org.apache.tamaya.mutableconfig.spi.MutableConfigurationProviderSpi;
import org.apache.tamaya.mutableconfig.spi.MutablePropertySource;
import org.apache.tamaya.spi.PropertySource;
import org.apache.tamaya.spi.ServiceContextManager;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;


/**
 * Accessor for creating {@link MutableConfiguration} instances to change configuration and commit changes.
 */
public final class MutableConfigurationProvider {

    private static final Logger LOG = Logger.getLogger(MutableConfigurationProvider.class.getName());
    /**
     * URIs used by this query instance to identify the backends to use for write operations.
     */
    private static MutableConfigurationProviderSpi mutableConfigurationProviderSpi = loadSpi();

    /**
     * SPI loader method.
     * @throws ConfigException if loading fails.
     * @return the SPI, never null.
     */
    private static MutableConfigurationProviderSpi loadSpi() {
        try{
            return ServiceContextManager.getServiceContext().getService(
                    MutableConfigurationProviderSpi.class)  ;
        } catch(Exception e){
            throw new ConfigException("Failed to initialize MutableConfigurationProviderSpi - " +
                    "mutable configuration support.");
        }
    }


    /** Singleton constructor. */
    private MutableConfigurationProvider(){}

    /**
     * Creates a new {@link MutableConfiguration} for the given default configuration, using all
     * {@link MutablePropertySource} instances found in its context and {@code autoCommit = false}.
     *
     * @return a new MutableConfiguration instance
     */
    public static MutableConfiguration createMutableConfiguration(){
        return mutableConfigurationProviderSpi.createMutableConfiguration(
                ConfigurationProvider.getConfiguration(), getApplyMostSignificantOnlyChangePolicy());
    }

    /**
     * Creates a new {@link MutableConfiguration} for the given default configuration, using all
     * {@link MutablePropertySource} instances found in its context and {@code autoCommit = false}.
     * @param changePropgationPolicy policy that defines how a change is written back and which property
     *                               sources are finally eligible for a write operation.
     * @return a new MutableConfiguration instance, with the given change policy active.
     */
    public static MutableConfiguration createMutableConfiguration(ChangePropagationPolicy changePropgationPolicy){
        return mutableConfigurationProviderSpi.createMutableConfiguration(
                ConfigurationProvider.getConfiguration(), changePropgationPolicy);
    }


    /**
     * Creates a new {@link MutableConfiguration} for the given configuration, using all
     * {@link MutablePropertySource} instances found in its context and {@code MOST_SIGNIFICANT_ONLY_POLICY}
     * configuration writing policy.
     *
     * @param configuration the configuration to use to write the changes/config.
     * @return a new MutableConfiguration instance
     */
    public static MutableConfiguration createMutableConfiguration(Configuration configuration){
        return createMutableConfiguration(configuration, MOST_SIGNIFICANT_ONLY_POLICY);
    }

    /**
     * Creates a new {@link MutableConfiguration} for the given configuration, using all
     * {@link MutablePropertySource} instances found in its context and {@code ALL_POLICY}
     * configuration writing policy.
     *
     * @param configuration the configuration to use to write the changes/config.
     * @param changePropagationPolicy the configuration writing policy.
     * @return a new MutableConfiguration instance
     */
    public static MutableConfiguration createMutableConfiguration(Configuration configuration, ChangePropagationPolicy changePropagationPolicy){
        return mutableConfigurationProviderSpi.createMutableConfiguration(configuration, changePropagationPolicy);
    }

    /**
     * This propagation policy writes through all changes to all mutable property sources, where applicable.
     * This is also the default policy.
     * @return default all policy.
     */
    public static ChangePropagationPolicy getApplyAllChangePolicy(){
        return ALL_POLICY;
    }

    /**
     * This propagation policy writes changes only once to the most significant property source, where a change is
     * applicable.
     * @return a corresponding {@link ChangePropagationPolicy} implementation, never null.
     */
    public static ChangePropagationPolicy getApplyMostSignificantOnlyChangePolicy(){
        return MOST_SIGNIFICANT_ONLY_POLICY;
    }

    /**
     * This propagation policy writes changes only once to the most significant property source, where a change is
     * applicable.
     * @param propertySourceNames the names of the mutable property sources to be considered for writing any changes to.
     * @return a corresponding {@link ChangePropagationPolicy} implementation, never null.
     */
    public static ChangePropagationPolicy getApplySelectiveChangePolicy(String... propertySourceNames){
        return new SelectiveChangeApplyPolicy(propertySourceNames);
    }

    /**
     * This propagation policy writes changes only once to the most significant property source, where a change is
     * applicable.
     * @return a corresponding {@link ChangePropagationPolicy} implementation, never null.
     */
    public static ChangePropagationPolicy getApplyNonePolicy(){
        return NONE_POLICY;
    }

    /**
     * This propagation policy writes through all changes to all mutable property sources, where applicable.
     */
    private static final ChangePropagationPolicy ALL_POLICY = new ChangePropagationPolicy() {
        @Override
        public void applyChange(ConfigChangeRequest change, Collection<PropertySource> propertySources) {
            for(PropertySource propertySource: propertySources){
                if(propertySource instanceof MutablePropertySource){
                    MutablePropertySource target = (MutablePropertySource)propertySource;
                    try{
                        target.applyChange(change);
                    }catch(ConfigException e){
                        LOG.warning("Failed to store changes '"+change+"' not applicable to "+target.getName()
                        +"("+target.getClass().getName()+").");
                    }
                }
            }
        }

    };

    /**
     * This propagation policy writes changes only once to the most significant property source, where a change is
     * applicable.
     */
    private static final ChangePropagationPolicy MOST_SIGNIFICANT_ONLY_POLICY = new ChangePropagationPolicy() {
        @Override
        public void applyChange(ConfigChangeRequest change, Collection<PropertySource> propertySources) {
            for(PropertySource propertySource: propertySources){
                if(propertySource instanceof MutablePropertySource){
                    MutablePropertySource target = (MutablePropertySource)propertySource;
                    try{
                        target.applyChange(change);
                    }catch(ConfigException e){
                        LOG.warning("Failed to store changes '"+change+"' not applicable to "+target.getName()
                                +"("+target.getClass().getName()+").");
                    }
                    break;
                }
            }
        }

    };

    /**
     * This propagation policy writes changes only once to the most significant property source, where a change is
     * applicable.
     */
    private static final ChangePropagationPolicy NONE_POLICY = new ChangePropagationPolicy() {
        @Override
        public void applyChange(ConfigChangeRequest change, Collection<PropertySource> propertySources) {
            LOG.warning("Cannot store changes '"+change+"': prohibited by change policy (read-only).");
        }
    };

    /**
     * This propagation policy writes through all changes to all mutable property sources, where applicable.
     */
    private static final class SelectiveChangeApplyPolicy implements ChangePropagationPolicy {

        private Set<String> propertySourceNames = new HashSet<>();

        SelectiveChangeApplyPolicy(String... propertySourceNames){
            this.propertySourceNames.addAll(Arrays.asList(propertySourceNames));
        }

        @Override
        public void applyChange(ConfigChangeRequest change, Collection<PropertySource> propertySources) {
            for(PropertySource propertySource: propertySources){
                if(propertySource instanceof MutablePropertySource){
                    if(this.propertySourceNames.contains(propertySource.getName())) {
                        MutablePropertySource target = (MutablePropertySource) propertySource;
                        try{
                            target.applyChange(change);
                        }catch(ConfigException e){
                            LOG.warning("Failed to store changes '"+change+"' not applicable to "+target.getName()
                                    +"("+target.getClass().getName()+").");
                        }
                        break;
                    }
                }
            }
        }
    };


}
