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
import org.apache.tamaya.spi.FilterContext;
import org.apache.tamaya.spi.PropertyFilter;
import org.apache.tamaya.spi.PropertyValue;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of the Configuration API. This class uses the current {@link ConfigurationContext} to evaluate the
 * chain of {@link org.apache.tamaya.spi.PropertySource} and {@link PropertyFilter}
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

    /**
     * Filters a single value.
     * @param value the raw value, not {@code null}.
     * @param context the context
     * @return the filtered value, including {@code null}.
     */
    public static PropertyValue applyFilter(PropertyValue value, ConfigurationContext context) {
        FilterContext filterContext = new FilterContext(value, context);
        return filterValue(filterContext);
    }

    /**
     * Filters all properties.
     * @param rawProperties the unfiltered properties, not {@code null}.
     * @param context the context
     * @return the filtered value, inclusing null.
     */
    public static Map<String, PropertyValue> applyFilters(Map<String, PropertyValue> rawProperties, ConfigurationContext context) {
        Map<String, PropertyValue> result = new HashMap<>();
        // Apply filters to values, prevent values filtered to null!
        for (Map.Entry<String, PropertyValue> entry : rawProperties.entrySet()) {
            FilterContext filterContext = new FilterContext(entry.getValue(), rawProperties, context);
            PropertyValue filtered = filterValue(filterContext);
            if(filtered!=null){
                result.put(filtered.getKey(), filtered);
            }
        }
        return result;
    }

    /**
     * Basic filter logic.
     * @param context the filter context, not {@code null}.
     * @return the filtered value.
     */
    private static PropertyValue filterValue(FilterContext context) {
        PropertyValue inputValue = context.getProperty();
        PropertyValue filteredValue = inputValue;

        for (int i = 0; i < MAX_FILTER_LOOPS; i++) {
            int changes = 0;
            for (PropertyFilter filter : context.getContext().getPropertyFilters()) {
                filteredValue = filter.filterProperty(inputValue, context);
                if (filteredValue != null && !filteredValue.equals(inputValue)) {
                    changes++;
                    LOG.finest("Filter - " + inputValue + " -> " + filteredValue + " by " + filter);
                }
                if(filteredValue==null){
                    LOG.finest("Filter removed entry - " + inputValue + ": " + filter);
                    break;
                }else{
                    inputValue = filteredValue;
                }
            }
            if (changes == 0) {
                LOG.finest("Finishing filter loop, no changes detected.");
                break;
            } else if (filteredValue == null) {
                break;
            } else {
                if (i == (MAX_FILTER_LOOPS - 1)) {
                    if (LOG.isLoggable(Level.WARNING)) {
                        LOG.warning("Maximal filter loop count reached, aborting filter evaluation after cycles: " + i);
                    }
                } else {
                    LOG.finest("Repeating filter loop, changes detected: " + changes);
                }
            }
        }
        return filteredValue;
    }

}
