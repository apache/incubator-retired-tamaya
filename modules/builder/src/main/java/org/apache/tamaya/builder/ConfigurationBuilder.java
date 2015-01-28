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
package org.apache.tamaya.builder;

import org.apache.tamaya.Configuration;
import org.apache.tamaya.PropertyConverter;
import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.core.internal.DefaultConfiguration;
import org.apache.tamaya.spi.ConfigurationContext;
import org.apache.tamaya.spi.PropertyFilter;
import org.apache.tamaya.spi.PropertySource;
import org.apache.tamaya.spi.PropertySourceProvider;
import org.apache.tamaya.spi.PropertyValueCombinationPolicy;

import java.util.Objects;

/**
 * Builder that allows to build a Configuration completely manually.
 */
public class ConfigurationBuilder {
    /** Builder used to create new ConfigurationContext instances. */
    private ProgrammaticConfigurationContext.Builder contextBuilder = new ProgrammaticConfigurationContext.Builder();

    /**
     * Flag if the config has already been built.
     * Configuration can be built only once
     */
    private boolean built;


    /**
     * Allows to set configuration context during unit tests.
     */
    ConfigurationBuilder setConfigurationContext(ConfigurationContext configurationContext) {
        contextBuilder.setConfigurationContext(configurationContext);
        return this;
    }

    public ConfigurationBuilder addPropertySources(PropertySource... sources){
        checkBuilderState();

        contextBuilder.addPropertySources(Objects.requireNonNull(sources));
        return this;
    }

    private void checkBuilderState() {
        if (built) {
            throw new IllegalStateException("Configuration has already been build.");
        }
    }

    public ConfigurationBuilder addPropertySourceProviders(PropertySourceProvider... propertySourceProviders){
        contextBuilder.addPropertySourceProviders(propertySourceProviders);
        return this;
    }

    public ConfigurationBuilder addPropertyFilters(PropertyFilter... propertyFilters){
        contextBuilder.addPropertyFilters(propertyFilters);
        return this;
    }

    public ConfigurationBuilder setPropertyValueCombinationPolicy(PropertyValueCombinationPolicy propertyValueCombinationPolicy){
        contextBuilder.setPropertyValueCombinationPolicy(propertyValueCombinationPolicy);
        return this;
    }

    public <T> ConfigurationBuilder addPropertyConverter(TypeLiteral<T> type, PropertyConverter<T> propertyConverter){
        contextBuilder.addPropertyConverter(type, propertyConverter);
        return this;
    }

    //X TODO think on a functonality/API for using the default PropertyConverters and use the configured ones here
    //X TODO as overrides used first.


    /**
     * Creates a new Configuration instance based on the configured data.
     * @return a new Configuration instance.
     */
    public Configuration build() {
        checkBuilderState();

        built = true;

        return new DefaultConfiguration(contextBuilder.build());
    }

}
