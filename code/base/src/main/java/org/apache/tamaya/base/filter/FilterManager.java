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
package org.apache.tamaya.base.filter;

import org.apache.tamaya.base.FormatUtils;
import org.apache.tamaya.spi.Filter;
import org.apache.tamaya.spi.ConfigValue;
import org.apache.tamaya.spi.ServiceContext;
import org.apache.tamaya.spi.ServiceContextManager;

import javax.config.Config;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of the Configuration API. This class uses the current {@link javax.config.Config} to evaluate the
 * chain of {@link javax.config.spi.ConfigSource} and {@link Filter}
 * instance to evaluate the current Configuration.
 */
public class FilterManager {
    /**
     * The logger.
     */
    private static final Logger LOG = Logger.getLogger(FilterManager.class.getName());
    /**
     * The maximal number of filter cycles performed before aborting.
     */
    private static final int MAX_FILTER_LOOPS = 10;

    private List<Filter> filters = new ArrayList<>();

    private ClassLoader classloader = ServiceContext.defaultClassLoader();

    /**
     * Create a new filter manager.
     */
    public FilterManager(){
    }

    /**
     * Create a new filter manager.
     * @param filters the filters to be used, not null.
     */
    public FilterManager(List<Filter> filters){
        this.filters.addAll(filters);
        LOG.info("Registered " + filters.size() + " config filter: " + filters);
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
    public FilterManager setClassloader(ClassLoader ClassLoader){
        this.classloader = Objects.requireNonNull(classloader);
        return this;
    }

    /**
     * Get the current list of filters.
     * @return the list of filters.
     */
    public List<Filter> getFilters(){
        return Collections.unmodifiableList(filters);
    }

    /**
     * Adds the given Filter instances, hereby the instances are added
     * to the end of the list with highest priority. The ordering of existing
     * property filters remains unchanged. To sort the
     * filters call {@link #sortFilter}.
     *
     * @param filters the filters to add
     * @return this instance, for chaining, never null.
     */
    public FilterManager addFilter(Filter... filters) {
        return addFilter(Arrays.asList(filters));
    }

    /**
     * Adds the given Filter instances, hereby the instances are added
     * to the end of the list with highest priority. The ordering of existing
     * property filters remains unchanged. To sort the
     * filters call {@link #sortFilter}.
     *
     * @param filters the filters to add
     * @return this instance, for chaining, never null.
     */
    public FilterManager addFilter(Collection<Filter> filters) {
        Objects.requireNonNull(filters);
        for(Filter filter:filters) {
            if (!this.filters.contains(filter)) {
                this.filters.add(filter);
            }
        }
        return this;
    }

    /**
     * Removes the given PropertyFilter instances, if existing. The order of the remaining
     * filters is preserved.
     *
     * @param filters the filter to remove
     * @return this builder, for chaining, never null.
     */
    public FilterManager removeFilters(Filter... filters){
        return removeFilters(Arrays.asList(filters));
    }

    /**
     * Removes the given PropertyFilter instances, if existing. The order of the remaining
     * filters is preserved.
     *
     * @param filters the filter to remove
     * @return this builder, for chaining, never null.
     */
    public FilterManager removeFilters(Collection<Filter> filters){
        Objects.requireNonNull(filters);
        this.filters.removeAll(filters);
        return this;
    }

    /**
     * Add all registered (default) property filters to the context built.
     * @return this builder, for chaining, never null.
     */
    public FilterManager addDefaultFilters() {
        for(Filter pf: ServiceContextManager.getServiceContext().getServices(Filter.class, classloader)){
            addFilter(pf);
        }
        return this;
    }

    public FilterManager sortFilter(Comparator<Filter> comparator) {
        Collections.sort(filters, comparator);
        return this;
    }

    /**
     * Removes all contained items.
     * @return this instance for chaining.
     */
    public FilterManager clear() {
        this.filters.clear();
        return this;
    }

    /**
     * Filters a single value.
     * @param value the raw value, not {@code null}.
     * @return the filtered value, including {@code null}.
     */
    public ConfigValue filterValue(ConfigValue value) {
        FilterContext filterContext = new FilterContext(value, null);
        return filterValue(filterContext);
    }

    /**
     * Filters a single value.
     * @param value the raw value, not {@code null}.
     * @param config the config
     * @return the filtered value, including {@code null}.
     */
    public ConfigValue filterValue(ConfigValue value, Config config) {
        FilterContext filterContext = new FilterContext(value, config);
        return filterValue(filterContext);
    }

    /**
     * Filters all properties.
     * @param rawProperties the unfiltered properties, not {@code null}.
     * @param config the config
     * @return the filtered value, inclusing null.
     */
    public Map<String, String> applyFilters(Map<String, String> rawProperties, Config config) {
        Map<String, String> result = new HashMap<>();
        // Apply filters to values, prevent values filtered to null!
        for (Map.Entry<String, String> entry : rawProperties.entrySet()) {
            FilterContext filterContext = new FilterContext(ConfigValue.of(entry.getKey(), rawProperties), config);
            ConfigValue filtered = filterValue(filterContext);
            if(filtered!=null){
                result.putAll(filtered.asMap());
            }
        }
        return result;
    }

    /**
     * Basic filter logic.
     * @param context the filter context, not {@code null}.
     * @return the filtered value.
     */
    private ConfigValue filterValue(FilterContext context) {
        ConfigValue inputValue = context.getProperty();
        ConfigValue filteredValue = inputValue;
        for (int i = 0; i < MAX_FILTER_LOOPS; i++) {
            int changes = 0;
            for (Filter filter :filters) {
                filteredValue = filter.filterProperty(inputValue);
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

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("Filters\n");
        b.append("-------\n");
        if(filters.isEmpty()){
            b.append("  No filters loaded.\n\n");
        }else {
            b.append("  CLASS                         INFO\n\n");
            for (Filter filter : filters) {
                b.append("  ");
                FormatUtils.appendFormatted(b, filter.getClass().getSimpleName(), 30);
                b.append(FormatUtils.removeNewLines(filter.toString()));
                b.append('\n');
            }
            b.append("\n");
        }
        return b.toString();
    }
}
