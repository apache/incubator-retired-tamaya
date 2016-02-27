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
package org.apache.tamaya.collections.internal;

import org.apache.tamaya.ConfigurationProvider;
import org.apache.tamaya.spi.PropertySource;
import org.apache.tamaya.spi.PropertyValue;
import org.apache.tamaya.spi.PropertyValueCombinationPolicy;

import javax.annotation.Priority;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * PropertyValueCombinationPolicy that allows to configure a PropertyValueCombinationPolicy for each key
 * individually, by adding a configured entry of the form
 * {@code _key.combination-policy=collect|override|fqPolicyClassName}.
 */
@Priority(100)
public class AdaptiveCombinationPolicy implements PropertyValueCombinationPolicy {
    /** Logger. */
    private static final Logger LOG = Logger.getLogger(AdaptiveCombinationPolicy.class.getName());

    /**
     * Collecting combination policy using (optional) {@code item-separator} parameter for determining the sparator
     * to combine multiple config entries found.
     */
    private static final PropertyValueCombinationPolicy COLLECTING_POLICY = new PropertyValueCombinationPolicy(){
        @Override
        public Map<String, String> collect(Map<String, String> currentValue, String key, PropertySource propertySource) {
            // check for default collection combination policies for lists, sets, maps etc.
            final String SEPARATOR = ConfigurationProvider.getConfiguration().getOrDefault('_' + key+".item-separator", ",");
            PropertyValue newValue = propertySource.get(key);
            if(newValue!=null){
                Map<String,String> newMapValue = new HashMap<>();
                if(currentValue!=null){
                    newMapValue.putAll(currentValue);
                }
                String oldVal = newMapValue.get(key);
                newMapValue.putAll(newValue.getConfigEntries());
                if(oldVal!=null){
                    newMapValue.put(key,oldVal + SEPARATOR + newValue.getValue());
                }
                return newMapValue;
            }else{
                if(currentValue!=null){
                    return currentValue;
                }
                return Collections.emptyMap();
            }
        }
    };

    /** Cache for loaded custom combination policies. */
    private Map<Class, PropertyValueCombinationPolicy> configuredPolicies = new ConcurrentHashMap<>();

    @Override
    public Map<String,String> collect(Map<String,String> currentValue, String key, PropertySource propertySource){
        if(key.startsWith("_")){
            PropertyValue newValue = propertySource.get(key);
            if(newValue!=null){
                return newValue.getConfigEntries();
            }
            return currentValue;
        }
        String adaptiveCombinationPolicyClass  = ConfigurationProvider.getConfiguration().getOrDefault(
                '_' + key+".combination-policy", "override");
        PropertyValueCombinationPolicy combinationPolicy = null;
        switch(adaptiveCombinationPolicyClass){
            case "collect":
            case "COLLECT":
                if(LOG.isLoggable(Level.FINEST)){
                    LOG.finest("Using collecting combination policy for key: " + key + "");
                }
                combinationPolicy = COLLECTING_POLICY;
                break;
            case "override":
            case "OVERRIDE":
                if(LOG.isLoggable(Level.FINEST)){
                    LOG.finest("Using default (overriding) combination policy for key: " + key + "");
                }
                combinationPolicy = PropertyValueCombinationPolicy.DEFAULT_OVERRIDING_COLLECTOR;
                break;
            default:
                try{
                    Class<PropertyValueCombinationPolicy> clazz = (Class<PropertyValueCombinationPolicy>)
                            Class.forName(adaptiveCombinationPolicyClass);
                    combinationPolicy = configuredPolicies.get(clazz);
                    if(combinationPolicy==null){
                        combinationPolicy = clazz.newInstance();
                        configuredPolicies.put(clazz, combinationPolicy);
                    }
                    if(LOG.isLoggable(Level.FINEST)){
                        LOG.finest("Using custom combination policy "+adaptiveCombinationPolicyClass+" for " +
                                "key: " + key + "");
                    }
                }
                catch(Exception e){
                    LOG.log(Level.SEVERE, "Error loading configured PropertyValueCombinationPolicy for " +
                            "key: " + key + ", using default (overriding) policy.", e);
                    combinationPolicy = PropertyValueCombinationPolicy.DEFAULT_OVERRIDING_COLLECTOR;
                }
        }
        return combinationPolicy.collect(currentValue, key, propertySource);
    }
}
