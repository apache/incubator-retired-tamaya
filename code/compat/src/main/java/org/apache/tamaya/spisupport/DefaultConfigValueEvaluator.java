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
import org.apache.tamaya.spi.PropertyFilter;
import org.apache.tamaya.spi.PropertySource;
import org.apache.tamaya.spi.PropertyValue;

import java.util.HashMap;
import java.util.Map;


/**
 * Implementation of the Configuration API. This class uses the current {@link ConfigurationContext} to evaluate the
 * chain of {@link PropertySource} and {@link PropertyFilter}
 * instance to evaluate the current Configuration.
 */
public class DefaultConfigValueEvaluator implements ConfigValueEvaluator{

    @Override
    public PropertyValue evaluteRawValue(String key, ConfigurationContext context) {
        PropertyValue unfilteredValue = null;
        for (PropertySource propertySource : context.getPropertySources()) {
            unfilteredValue = context.getPropertyValueCombinationPolicy().
                    collect(unfilteredValue, key, propertySource);
        }
        if(unfilteredValue==null || unfilteredValue.getValue()==null){
            return null;
        }
        return unfilteredValue;
    }

    @Override
    public Map<String, PropertyValue> evaluateRawValues(ConfigurationContext context) {
        Map<String, PropertyValue> result = new HashMap<>();
        for (PropertySource propertySource : context.getPropertySources()) {
            for (Map.Entry<String,PropertyValue> propEntry: propertySource.getProperties().entrySet()) {
                PropertyValue unfilteredValue = result.get(propEntry.getKey());
                unfilteredValue = context.getPropertyValueCombinationPolicy().
                        collect(unfilteredValue, propEntry.getKey(), propertySource);
                if(unfilteredValue!=null){
                    result.put(unfilteredValue.getKey(), unfilteredValue);
                }
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return "DefaultConfigEvaluator{}";
    }
}
