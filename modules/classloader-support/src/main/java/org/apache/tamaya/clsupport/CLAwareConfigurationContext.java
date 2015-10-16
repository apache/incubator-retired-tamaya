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
package org.apache.tamaya.clsupport;

import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.spisupport.DefaultConfigurationContext;
import org.apache.tamaya.spi.ConfigurationContext;
import org.apache.tamaya.spi.ConfigurationContextBuilder;
import org.apache.tamaya.spi.PropertyConverter;
import org.apache.tamaya.spi.PropertyFilter;
import org.apache.tamaya.spi.PropertySource;
import org.apache.tamaya.spi.PropertyValueCombinationPolicy;

import javax.annotation.Priority;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Default Implementation of a simple ConfigurationContext.
 */
@Priority(100)
public class CLAwareConfigurationContext implements ConfigurationContext {

    /** The logger used. */
    private final static Logger LOG = Logger.getLogger(CLAwareConfigurationContext.class.getName());

    private ContextManager contextManager = new ContextManager();


    @Override
    public void addPropertySources(PropertySource... propertySourcesToAdd) {
        contextManager.getItemNoParent(true).addPropertySources(propertySourcesToAdd);
    }

    @Override
    public List<PropertySource> getPropertySources() {
        return contextManager.getItemNoParent(true).getPropertySources();
    }

    @Override
    public <T> void addPropertyConverter(TypeLiteral<T> typeToConvert, PropertyConverter<T> propertyConverter) {
        contextManager.getItemNoParent(true).addPropertyConverter(typeToConvert, propertyConverter);
    }

    @Override
    public Map<TypeLiteral<?>, List<PropertyConverter<?>>> getPropertyConverters() {
        return contextManager.getItemNoParent(true).getPropertyConverters();
    }

    @Override
    public <T> List<PropertyConverter<T>> getPropertyConverters(TypeLiteral<T> targetType) {
        return contextManager.getItemNoParent(true).getPropertyConverters(targetType);
    }

    @Override
    public List<PropertyFilter> getPropertyFilters() {
        return contextManager.getItemNoParent(true).getPropertyFilters();
    }

    @Override
    public PropertyValueCombinationPolicy getPropertyValueCombinationPolicy(){
        return contextManager.getItemNoParent(true).getPropertyValueCombinationPolicy();
    }

    @Override
    public ConfigurationContextBuilder toBuilder() {
        return contextManager.getItemNoParent(true).toBuilder();
    }


    /**
     * Subcomponent managing {@link ConfigurationContext} instances, one per classloader.
     */
    private static final class ContextManager extends AbstractClassloaderAwareItemLoader<ConfigurationContext>{

        @Override
        protected ConfigurationContext createItem(ClassLoader classLoader) {
            // Simply create a complete configuration manager for every classloader. Maybe we will optimize this at a
            // later stage in the project but as for now it is the most simple working solution.
            return new DefaultConfigurationContext();
        }

        @Override
        protected void updateItem(ConfigurationContext currentItemSet, ClassLoader classLoader) {
            // ignore, currently not supported.
        }
    }
}
