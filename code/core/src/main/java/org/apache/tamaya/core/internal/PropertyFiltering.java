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
package org.apache.tamaya.core.internal;

import org.apache.tamaya.spi.ConfigurationContext;
import org.apache.tamaya.spi.FilterContext;
import org.apache.tamaya.spi.PropertyFilter;
import org.apache.tamaya.spi.PropertySource;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of the Configuration API. This class uses the current {@link ConfigurationContext} to evaluate the
 * chain of {@link PropertySource} and {@link PropertyFilter}
 * instance to evaluate the current Configuration.
 */
public final class PropertyFiltering{
    /**
     * The logger.
     */
    private static final Logger LOG = Logger.getLogger(PropertyFiltering.class.getName());

    /**
     * The maximal number of filter cycles performed before aborting.
     */
    private static final int MAX_FILTER_LOOPS = 10;

    /**
     * Private singleton constructor.
     */
    private PropertyFiltering(){}

    public static String applyFilter(String key, Map<String,String> configData, ConfigurationContext configurationContext) {
        // Apply filters to values, prevent values filtered to null!
        String result = configData.get(key);
        for (int i = 0; i < MAX_FILTER_LOOPS; i++) {
            boolean changed = false;
            // Apply filters to values, prevent values filtered to null!
            FilterContext filterContext = new FilterContext(key, configData, true);
            for (PropertyFilter filter : configurationContext.getPropertyFilters()) {
                String newValue = filter.filterProperty(result, filterContext);
                if (newValue != null && !newValue.equals(result)) {
                    changed = true;
                    if (LOG.isLoggable(Level.FINEST)) {
                        LOG.finest("Filter - " + key + ": " + result + " -> " + newValue + " by " + filter);
                    }
                } else if (result != null && !result.equals(newValue)) {
                    changed = true;
                    if (LOG.isLoggable(Level.FINEST)) {
                        LOG.finest("Filter - " + key + ": " + result + " -> " + newValue + " by " + filter);
                    }
                }
                result = newValue;
            }
            if (!changed) {
                LOG.finest("Finishing filter loop, no changes detected.");
                break;
            } else {
                if (i == (MAX_FILTER_LOOPS - 1)) {
                    if (LOG.isLoggable(Level.WARNING)) {
                        LOG.warning("Maximal filter loop count reached, aborting filter evaluation after cycles: " + i);
                    }
                } else {
                    LOG.finest("Repeating filter loop, changes detected.");
                }
            }
        }
        return result;
    }

    public static Map<String, String> applyFilters(Map<String, String> inputMap, ConfigurationContext configurationContext) {
        Map<String, String> resultMap = new HashMap<>(inputMap);
        // Apply filters to values, prevent values filtered to null!
        Map<String, String> metaData = filterMetadata(inputMap);
        for (int i = 0; i < MAX_FILTER_LOOPS; i++) {
            AtomicInteger changes = new AtomicInteger();
            for (Map.Entry<String, String> entry : inputMap.entrySet()) {
                FilterContext filterContext = new FilterContext(entry.getKey(), inputMap, false);
                for (PropertyFilter filter : configurationContext.getPropertyFilters()) {
                    final String k = entry.getKey();
                    final String v = entry.getValue();
                    String newValue = filter.filterProperty(v, filterContext);
                    if (newValue != null && !newValue.equals(v)) {
                        changes.incrementAndGet();
                        LOG.finest("Filter - " + k + ": " + v + " -> " + newValue + " by " + filter);
                    } else if (v != null && !v.equals(newValue)) {
                        changes.incrementAndGet();
                        LOG.finest("Filter - " + k + ": " + v + " -> " + newValue + " by " + filter);
                    }
                    // Remove null values
                    if (null != newValue) {
                        resultMap.put(k, newValue);
                    }
                    else{
                        resultMap.remove(k);
                    }
                }
            }
            if (changes.get() == 0) {
                LOG.finest("Finishing filter loop, no changes detected.");
                break;
            } else {
                if (i == (MAX_FILTER_LOOPS - 1)) {
                    if (LOG.isLoggable(Level.WARNING)) {
                        LOG.warning("Maximal filter loop count reached, aborting filter evaluation after cycles: " + i);
                    }
                } else {
                    LOG.finest("Repeating filter loop, changes detected: " + changes.get());
                }
                changes.set(0);
            }
        }
        return resultMap;
    }

    private static Map<String, String> filterMetadata(Map<String, String> inputMap) {
        Map<String,String> result = new HashMap<>();
        for(Map.Entry<String,String> en:inputMap.entrySet()){
            if(en.getKey().startsWith("_")){
                result.put(en.getKey(), en.getValue());
            }
        }
        return Collections.unmodifiableMap(result);
    }

}
