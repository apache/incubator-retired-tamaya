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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * PropertyValueCombinationPolicy that allows to configure a PropertyValueCombinationPolicy for each key
 * individually, by adding a configured entry of the form {@code key{combinationPolicy}=fqPolicyClassName}.
 */
@Priority(100)
public class AdaptiveCombinationPolicy implements PropertyValueCombinationPolicy {

    private static final Logger LOG = Logger.getLogger(AdaptiveCombinationPolicy.class.getName());

    private Map<Class, PropertyValueCombinationPolicy> configuredPolicies = new ConcurrentHashMap<>();

    @Override
    public Map<String,String> collect(Map<String,String> currentValue, String key, PropertySource propertySource){
        String adaptiveCombinationPolicyClass  = ConfigurationProvider.getConfiguration().get('_' + key+".combinationPolicy");
        if(adaptiveCombinationPolicyClass!=null){
            PropertyValueCombinationPolicy delegatePolicy = null;
            try{
                Class clazz = Class.forName(adaptiveCombinationPolicyClass);
                delegatePolicy = configuredPolicies.get(clazz);
                if(delegatePolicy==null){
                    delegatePolicy = PropertyValueCombinationPolicy.class.cast(clazz.newInstance());
                    configuredPolicies.put(clazz, delegatePolicy);
                }
                return delegatePolicy.collect(currentValue, key, propertySource);
            }
            catch(Exception e){
                LOG.log(Level.SEVERE, "Error loading configured PropertyValueCombinationPolicy for key: " + key, e);
            }
        }
        // check for default collection combination policies for lists, sets, maps etc.
        String collectionType = ConfigurationProvider.getConfiguration().get('_' + key+".collection-type");
        if(collectionType!=null) {
            if (collectionType.startsWith("java.util.")) {
                collectionType = collectionType.substring("java.util.".length());
            }
            switch(collectionType){
                case "List":
                case "ArrayList":
                case "LinkedList":
                case "Collection":
                case "Set":
                case "HashSet":
                case "TreeSet":
                case "SortedSet":
                case "Map":
                case "HashMap":
                case "ConcurrentHashMap":
                case "TreeMap":
                case "SortedMap":
                    PropertyValue newValue = propertySource.get(key);
                    if(newValue!=null){
                        Map<String,String> newMapValue = new HashMap<>(currentValue);
                        String oldVal = newMapValue.get(key);
                        if(oldVal!=null){
                            newMapValue.put(key,oldVal + ',' + newValue.getValue());
                        }
                        return newMapValue;
                    }else{
                        return newValue.getConfigEntries();
                    }
                default:
                    LOG.log(Level.SEVERE, "Unsupported collection-type for key: " + key + ": " + collectionType);
            }
        }
        PropertyValue newValue = propertySource.get(key);
        if(newValue!=null){
            return newValue.getConfigEntries();
        }
        return currentValue;
    }
}
