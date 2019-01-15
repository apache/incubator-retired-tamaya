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
package org.apache.tamaya.spisupport;

import org.apache.tamaya.spi.ConfigurationContext;
import org.apache.tamaya.spi.PropertySource;
import org.apache.tamaya.spi.PropertyValue;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Component SPI which encapsulates the evaluation of a single or full <b>raw</b> createValue
 * for a {@link ConfigurationContext}.
 */
public interface ConfigValueEvaluator {

    /**
     * Evaluates single createValue using a {@link ConfigurationContext}.
     * @param key the config key, not null.
     * @param context the context, not null.
     * @return the createValue, or null.
     */
    default PropertyValue evaluateRawValue(String key, ConfigurationContext context){
        List<PropertyValue> values = evaluateAllValues(key, context);
        if(values.isEmpty()){
            return null;
        }
        return values.get(0);
    }

    /**
     * Evaluates all values using a {@link ConfigurationContext}.
     * @param key the config key, not null.
     * @param context the context, not null.
     * @return the createValue, or null.
     */
    default List<PropertyValue> evaluateAllValues(String key, ConfigurationContext context){
        List<PropertyValue> result = new ArrayList<>();
        for(PropertySource ps:context.getPropertySources()){
            try{
                PropertyValue val = ps.get(key);
                if(val!=null){
                    result.add(val);
                }
            }catch(Exception e){
                Logger.getLogger(getClass().getName())
                        .log(Level.WARNING, "Failed to access '"+key+"' from PropertySource: " + ps.getName(), e);
            }
        }
        // Ensure returning values found in order of precedence.
        Collections.reverse(result);
        return result;
    }

    /**
     * Evaluates all property values from a {@link ConfigurationContext}.
     * @param context the context, not null.
     * @return the createValue, or null.
     */
    default Map<String, PropertyValue> evaluateRawValues(ConfigurationContext context){
        Map<String, PropertyValue> result = new HashMap<>();
        List<PropertySource> propertySources = context.getPropertySources();
        Collections.reverse(propertySources);
        for(PropertySource ps:propertySources){
            try{
                Map<String,PropertyValue> val = ps.getProperties();
                if(val!=null){
                    result.putAll(val);
                }
            }catch(Exception e){
                Logger.getLogger(getClass().getName())
                        .log(Level.WARNING, "Failed to access properties from PropertySource: " + ps.getName(), e);
            }
        }
        return result;
    }

}
